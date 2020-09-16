package web.principle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * 回显http请求报文
 * @author YangHui
 */
@ChannelHandler.Sharable
@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            //FullHttpRequest request
            FullHttpRequest request = (FullHttpRequest)msg;
            //获取请求数据
            HttpMethod method = request.method();
            String uri = request.uri();
            HttpVersion httpVersion = request.protocolVersion();
            HttpHeaders headers = request.headers();
            ByteBuf content = request.content();
            //回显原始请求报文
            StringBuilder sb = new StringBuilder();
            sb.append(method.name()).append(" ").append(uri).append(" ").append(httpVersion.text()).append("\n");
            Iterator<Map.Entry<String, String>> iterator = headers.iteratorAsString();
            while(iterator.hasNext()){
                Map.Entry<String, String> header = iterator.next();
                sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
            }
            if(!headers.isEmpty()){
                sb.append("\n");
            }
            int length = content.readableBytes();
            byte[] data = new byte[length];
            content.readBytes(data);
            sb.append(new String(data));
            log.info("\nchannel( {} ) received request:\n{}",ctx.channel(),sb.toString());
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
        if(sslHandler != null){
            sslHandler.handshakeFuture().addListener((f)->{
                    ctx.writeAndFlush(
                            "ssl handshake" + InetAddress.getLocalHost().getHostName() + "\n");
                    ctx.writeAndFlush(
                            "Your session is protected by " +
                                    ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                                    " cipher suite.\n");
                }
            );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("\nchannel( {} ) is abnormal",ctx.channel());
        cause.printStackTrace();
        ctx.close();
    }

}
