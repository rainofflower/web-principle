package com.yanghui.study.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class Controller {

    @Autowired
    private WebClient webClient;

    @GetMapping("/web-client-request")
    public String webClientRequest(){
//        Map<String,Object> param = new HashMap<>();
//        param.put("name","杨晖");
//        String jsonString = JSON.toJSONString(param);
        //                        uriBuilder -> uriBuilder
//                                .host("http://localhost")
//                                .port(8750)
//                                .path("/simple")
//                                //.queryParam("name","杨晖")
//                                .build()
        WebClient webClient2 = WebClient.create();
        Flux<String> flux = webClient2.get()
                .uri("http://localhost:8750/simple")
                .retrieve()
                .bodyToFlux(String.class);
        //log.info(mono.block());
        //return response.block();
        return "llal";
    }
}
