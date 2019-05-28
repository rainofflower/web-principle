package com.yanghui.study.Client;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Client {

    public static final String HOST = "localhost";

    public static final int PORT = 8080;
    @Test
    public void test() throws IOException {
        for(int i = 0; i< 10; i++){
            Socket socket = new Socket(HOST, PORT);
            socket.getOutputStream().write(("晚上好"+i).getBytes(StandardCharsets.UTF_8));
            socket.shutdownOutput();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s = in.readLine();
            log.info(s);
            socket.close();
        }
    }
}
