package web.principle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * 回显http请求报文
 * @author YangHui
 */
@ChannelHandler.Sharable
@Slf4j
public class ServerPlainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("---receive---:{}",msg);
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
