package com.yanghui.learn.common;

/**
 * @author YangHui
 */
public class Content {

    public static void main(String... args){
        String s = "{\"test\":2}";
        byte[] bytes = s.getBytes();
        System.out.println("Content-Length: "+bytes.length);
        for(int i = 0; i<bytes.length; i++){
            System.out.println(bytes[i]);
        }
    }
}
