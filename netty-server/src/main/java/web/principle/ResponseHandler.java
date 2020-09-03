package web.principle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;

/**
 * 构建http响应
 * @author YangHui
 */
@ChannelHandler.Sharable
@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        HttpHeaders headers = request.headers();
        int nextInt = new Random().nextInt(100);
        String res = "<html><head><title>HTTP</title></head><body><h1>你请求uri为：" + uri + "</h1><p>"+nextInt+"</p></body></html>";
        ByteBuf byteBuf = Unpooled.copiedBuffer(res, CharsetUtil.UTF_8);
        //创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                byteBuf);
        //设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        /**
         * 如果响应中不添加响应主体长度，每次请求都会建立新的TCP连接，服务端发送响应后会关闭当前连接（响应首部会添加 Connection: close）
         */
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
        /**
         * google chrome 会记录服务器返回的cookie,除非关闭chrome，否则访问相同服务器请求首部中会自动添加cookie
         */
        String cookie = headers.get(HttpHeaderNames.COOKIE);
        if(cookie == null || cookie.length() == 0){
            response.headers().set(HttpHeaderNames.SET_COOKIE,"uid="+ UUID.randomUUID().toString().replaceAll("-",""));
        }else{
            response.headers().set(HttpHeaderNames.SET_COOKIE,cookie);
        }
        //响应客户端
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("\nchannel( {} ) is abnormal",ctx.channel());
        cause.printStackTrace();
        ctx.close();
    }

}
