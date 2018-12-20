package com.yanghui.study.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.yanghui.study.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
public class Controller {

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${user-service-path}")
	private String url;

	@GetMapping("/rest-request")
	public String restRequest(){
		log.info("==>请求RestTemplate-use服务");
		List<Map<String,Object>> items = new ArrayList<>();
		Map<String,Object> item1 = new HashMap<>();
		item1.put("code","111");
		Map<String,Object> item2 = new HashMap<>();
		item2.put("code","222");
		items.add(item1);
		items.add(item2);

		Map<String,Object> requestData = new HashMap<>();
		requestData.put("serviceCode","lala-service");
		requestData.put("serviceName","啦啦啦");
		Map<String,Object> requestParam = new HashMap<>();
		requestParam.put("items",items);
		requestData.put("p",requestParam);
		String jsonString = JSON.toJSONString(requestData);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url+"/user", jsonString, String.class);
		responseEntity.getStatusCode();
		String responseDataStr = responseEntity.getBody();
		JSONObject responseData = JSONObject.parseObject(responseDataStr);
		int num = 0;
		int resultCode = (Integer)responseData.get("resultCode");
		if(resultCode == 0){
			JSONArray jsonArray = (JSONArray) responseData.get("items");
			if(jsonArray.size() > 0){
				for (int i = 0; i<jsonArray.size(); i++){
					JSONObject subsystem = jsonArray.getJSONObject(i);
					if(subsystem.get("STATUS").toString().equals("OFFLINE")){
						num++;
					}
				}
			}
		}
		return responseDataStr;
	}
}
