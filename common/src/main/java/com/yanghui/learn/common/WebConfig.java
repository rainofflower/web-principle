package com.yanghui.learn.common;

import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author YangHui
 */
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer){
        configurer.setDefaultTimeout(1*60*1000);
    }
}
