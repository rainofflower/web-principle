package com.yanghui.study.server.handler;

import java.net.Socket;

public interface DataHandler {

    default void handleRequest(Socket socket) throws Exception {
        throw new UnsupportedOperationException("处理方法未实现！");
    }
}
