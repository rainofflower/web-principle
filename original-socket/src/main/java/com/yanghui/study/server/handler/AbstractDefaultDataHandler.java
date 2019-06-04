package com.yanghui.study.server.handler;

import java.net.Socket;

public abstract class AbstractDefaultDataHandler implements DataHandler {

    public void handleRequest(Socket socket) throws Exception {
        try{
            process(socket);
        }finally {
            if(socket != null){
                socket.close();
            }
        }
    }

    protected abstract void process(Socket socket) throws Exception;
}
