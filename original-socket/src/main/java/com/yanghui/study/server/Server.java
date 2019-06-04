package com.yanghui.study.server;

import com.yanghui.study.server.handler.DataHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class Server implements ApplicationRunner {

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private Executor threadPool;

    @Autowired
    @Qualifier("httpRequestHandler")
    private DataHandler handler;

    public void run(ApplicationArguments args) throws Exception{
        ServerSocket listener = new ServerSocket(serverPort);
        try{
            while(true){
                Socket socket = listener.accept();
                threadPool.execute(()->{
                    try {
                        handler.handleRequest(socket);
                    } catch (Exception e) {
                        log.error("发生异常，信息"+e);
                    }
                });
            }
        } finally {
            if(listener != null){
                listener.close();
            }
        }
    }
}
