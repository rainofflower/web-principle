package web.principle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YangHui
 */
@Slf4j
public class HttpClient {

    public static void main(String... args) throws Exception{
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline
//                                    //ssl层
                                    .addLast(SSLContextHolder.clientSslCtx.newHandler(channel.alloc()))
                                    .addLast("http-serverCodec", new HttpClientCodec());
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
            ChannelFuture f = bootstrap.connect("localhost",HttpServer.PORT).sync();
            DefaultHttpRequest request = new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.POST,
                    "https://localhost:8080?term=algrothim");
            ChannelFuture future = f.channel().writeAndFlush(request).sync();
            future.channel().close().sync();
        }finally {
            workGroup.shutdownGracefully();
        }
    }
}
