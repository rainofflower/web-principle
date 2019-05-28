package com.yanghui.study.server.handler;

import com.yanghui.study.server.handler.DataHandler;

import java.net.Socket;

public abstract class AbstractDefaultDataHandler implements DataHandler {

    public void handleRequest(Socket socket) throws Exception {
        try{
            process(socket);
        }finally {
            socket.close();
        }
    }

    abstract void process(Socket socket) throws Exception;
}
