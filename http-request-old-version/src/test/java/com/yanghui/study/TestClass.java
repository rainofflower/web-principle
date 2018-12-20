package com.yanghui.study;

import com.alibaba.fastjson.JSON;
import com.yanghui.study.config.RestTemplateConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestTemplateConfiguration.class)
@Slf4j
public class TestClass {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test(){
        Map<String,Object> requestData = new HashMap<>();
        requestData.put("serviceCode","http-request-old-version");
        requestData.put("serviceName","风吹麦浪");
        String jsonString = JSON.toJSONString(requestData);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8751/user", jsonString, String.class);
        String responseJson = responseEntity.getBody();
        log.info(responseJson);
    }
}
