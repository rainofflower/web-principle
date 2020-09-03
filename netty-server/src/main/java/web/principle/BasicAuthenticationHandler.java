package web.principle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            //path /private/basic下的uri 使用 Basic Authentication
            if(uri.startsWith("/private/basic")){
                boolean match = false;
                if(!(authorization == null || authorization.length() == 0)){
                    //对authorization解码并验证
                    log.info("Base64 原始 Authorization: {}",authorization);
                    if(authorization.startsWith("Basic ") || authorization.startsWith("basic ")){
                        String str = authorization.substring(6);
                        String decodeAuth = new String(Base64.getDecoder().decode(str));
                        log.info("Base64 解码 Authorization: {}",decodeAuth);
                        if(decodeAuth.length() > 1){
                            String[] split = decodeAuth.split(":");
                            if(split.length == 2){
                                if(Objects.equals(split[1],USER_INFO.get(split[0]))){
                                    log.info("Bingo! the account {} password {} matches",split[0],split[1]);
                                    match = true;
                                }
                            }
                        }
                    }
                }
                if(!match){
                    /**
                     * 401状态码 和 www-authenticate 首部两者不可或缺
                     */
                    //http响应
                    FullHttpResponse response = new DefaultFullHttpResponse(
                            HttpVersion.HTTP_1_1,
                            HttpResponseStatus.UNAUTHORIZED);
                    //设置头信息
                    response.headers()
                            .set(HttpHeaderNames.WWW_AUTHENTICATE,"Basic realm=private")
                            .set(HttpHeaderNames.CONTENT_LENGTH,0);
                    //响应客户端
                    ctx.write(response);
                }else{
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
