package com.yanghui.study.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "userFeignWithHttps", url = "${service-path.user-service-with-https}")
public interface UserFeignWithHttps {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String getUser();
}
