package web.principle;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YangHui
 */
@Slf4j
public class HttpServer {

    static final int BOSS_THREAD = 1;
    static final int WORK_THREAD = 200;
    static final int PORT = 8080;

    public static void main(String... args) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_THREAD);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        final HttpServerHandler httpServerHandler = new HttpServerHandler();
        final BasicAuthenticationHandler authenticationHandler = new BasicAuthenticationHandler();
        final ResponseHandler responseHandler = new ResponseHandler();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
                    //Option是为了NioServerSocketChannel设置的，用来接收传入连接的
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline
                                    //ssl层
                                    .addLast(SSLContextHolder.serverSslCtx.newHandler(channel.alloc()))
                                    //服务端对request解码,对response编码
                                    .addLast("http-serverCodec", new HttpServerCodec())
                                    //keep-alive实现
                                    .addLast("http-KeepAlive", new HttpServerKeepAliveHandler())
                                    //将多个消息转换为单一的FullHttpRequest或FullHttpResponse对象
                                    .addLast("http-aggregator",new HttpObjectAggregator(1048576))
                                    //解决大数据包传输问题，用于支持异步写大量数据流并且不需要消耗大量内存也不会导致内存溢出错误( OutOfMemoryError )。
                                    //仅支持ChunkedInput类型的消息。也就是说，仅当消息类型是ChunkedInput时才能实现ChunkedWriteHandler提供的大数据包传输功能
                                    .addLast("http-chunked",new ChunkedWriteHandler())
                                    .addLast("http-server-handler",httpServerHandler)
                                    .addLast("basic-authentication-handler",authenticationHandler)
                                    .addLast("response-handler",responseHandler);
                            log.info("\nchannel( {} ) has established",channel);
                            //监听TCP连接断开
                            channel.closeFuture().addListener((f)->{
                                if(f.isSuccess()){
                                    log.info("\nchannel( {} ) has disconnected",channel);
                                }else{
                                    log.info("\nchannel( {} ) closed failed",channel);
                                }
                            });
                        }
                    });
            ChannelFuture f = bootstrap.bind(PORT).sync();
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
