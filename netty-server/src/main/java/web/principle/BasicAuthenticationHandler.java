package web.principle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic Authentication
 * 基本认证
 * @author YangHui
 */
@ChannelHandler.Sharable
@Slf4j
public class BasicAuthenticationHandler extends ChannelInboundHandlerAdapter {

    private static Map<String,String> USER_INFO;

    static{
        USER_INFO = new HashMap<>();
        USER_INFO.put("yanghui","1234");
        USER_INFO.put("rainofflower","1234");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest)msg;
            //获取请求数据
            String uri = request.uri();
            HttpHeaders headers = request.headers();
            String authorization = headers.get(HttpHeaderNames.AUTHORIZATION);
            if(uri.startsWith("/private/info")){
                if(authorization == null || authorization.length() == 0){
                    //http响应
                    FullHttpResponse response = new DefaultFullHttpResponse(
                            HttpVersion.HTTP_1_1,
                            HttpResponseStatus.UNAUTHORIZED);
                    //设置头信息
                    response.headers()
                            .set(HttpHeaderNames.WWW_AUTHENTICATE,"Basic")
                            .set(HttpHeaderNames.CONTENT_LENGTH,0);
                    //响应客户端
                    ctx.write(response);
                }else{
                    //对authorization解码并验证

                    ctx.fireChannelRead(msg);
                }
            }else{
                ctx.fireChannelRead(msg);
            }
        }
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
