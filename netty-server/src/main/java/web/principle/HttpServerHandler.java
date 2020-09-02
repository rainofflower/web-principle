package web.principle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @author YangHui
 */
@ChannelHandler.Sharable
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String id = ctx.channel().id().asShortText();
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
        log.info("\nchannel(id: {} ) received request:\n{}",id,sb.toString());
        int nextInt = new Random().nextInt(100);
        String msg = "<html><head><title>DEMO</title></head><body><h1>你请求uri为：" + uri + "</h1><p>"+nextInt+"</p></body></html>";
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
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
        log.error("\nchannel(id: {} ) is abnormal",ctx.channel().id().asShortText());
        cause.printStackTrace();
        ctx.close();
    }

}
