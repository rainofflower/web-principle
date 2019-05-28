package com.yanghui.study.server.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class SimpleDataHandler extends AbstractDefaultDataHandler {

    public void process(Socket socket) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String s = in.readLine();
        log.info("服务器收到消息："+s);
        OutputStream out = socket.getOutputStream();
        out.write(("服务器返回信息："+s).getBytes(StandardCharsets.UTF_8));
    }
}
