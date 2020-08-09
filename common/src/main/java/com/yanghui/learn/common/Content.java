package com.yanghui.learn.common;

/**
 * @author YangHui
 */
public class Content {

    public static void main(String... args){
        String s = "<html><head><title>DEMO</title></head><body><h1>你请求uri为：/test</h1><p>5</p></body></html>";
        byte[] bytes = s.getBytes();
        System.out.println("Content-Length: "+bytes.length);
        for(int i = 0; i<bytes.length; i++){
            System.out.println(bytes[i]);
        }
    }
}
