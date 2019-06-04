package com.yanghui.study.server.handler;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 处理请求返回http响应
 */
@Component
public class HttpRequestHandler extends AbstractDefaultDataHandler {

    /**
     * 资源文件以F盘作为根路径，请求地址后面加上除 F:/ 的路径即可，
     * 如 F:\server\resource\***.html 请求地址 http://ip:port/server/resource/***.html
     */
    private String basePath = "F:\\";

    public void process(Socket socket) throws Exception {
        String line = null;
        BufferedReader br = null;
        BufferedReader reader = null;
        PrintWriter out = null;
        InputStream in = null;
        //延长处理时间用于并发测试
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(20));
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String header = reader.readLine();
            // 由相对路径计算出绝对路径
            String filePath = basePath + header.split(" ")[1];
            out = new PrintWriter(socket.getOutputStream());
            // 如果请求资源的后缀为jpg或者ico，则读取资源并输出
            if (filePath.endsWith("jpg") || filePath.endsWith("ico")) {
                in = new FileInputStream(filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int i = 0;
                while ((i = in.read()) != -1) {
                    baos.write(i);
                }
                byte[] array = baos.toByteArray();
                out.println("HTTP/1.1 200 OK");
                out.println("Server: YH");
                out.println("Content-Type: image/jpeg");
                out.println("Content-Length: " + array.length);
                out.println("");
                socket.getOutputStream().write(array, 0, array.length);
            } else {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                out = new PrintWriter(socket.getOutputStream());
                out.println("HTTP/1.1 200 OK");
                out.println("Server: YH");
                out.println("Content-Type: text/html; charset=UTF-8");
                out.println("");
                while ((line = br.readLine()) != null) {
                    out.println(line);
                }
            }
            out.flush();
        } catch (Exception ex) {
            out.println("HTTP/1.1 500");
            out.println("");
            out.flush();
        } finally {
            close(br, in, reader, out);
        }
    }

    // 关闭流或者Socket
    private static void close(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (Exception ex) {
                }
            }
        }
    }
}
