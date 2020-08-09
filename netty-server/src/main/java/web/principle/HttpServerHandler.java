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
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
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
