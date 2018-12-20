package com.yanghui.study.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanghui.study.feign.UserFeign;
import com.yanghui.study.feign.UserFeignWithHttps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class Controller {

	@Autowired
	private UserFeign userFeign;

	@Autowired
	private UserFeignWithHttps userFeignWithHttps;

	@GetMapping("/feign-https-test")
	public String httpsTest(){
		log.info("==>请求Feign-Https测试接口");
		String str = userFeignWithHttps.getUser();
		return str;
	}

	@GetMapping("/feign-request")
	public String restRequest(){
		log.info("==>请求Feign-use服务");
		List<Map<String,Object>> items = new ArrayList<>();
		Map<String,Object> item1 = new HashMap<>();
		item1.put("code","111");
		Map<String,Object> item2 = new HashMap<>();
		item2.put("code","222");
		items.add(item1);
		items.add(item2);

		Map<String,Object> requestData = new HashMap<>();
		requestData.put("serviceCode","yaya-service");
		requestData.put("serviceName","哈哈哈");
		Map<String,Object> requestParam = new HashMap<>();
		requestParam.put("items",items);
		requestData.put("p",requestParam);
		List<Object> projectNos = new ArrayList<>();
		projectNos.add("SD001");
		projectNos.add("SD002");
		projectNos.add("SD003");
		requestData.put("projectNos",projectNos);
		String jsonString = JSON.toJSONString(requestData);
		String response = "";
		try{
			response =  userFeign.getUser(jsonString);
		}catch(Exception e){
			log.error("请求user服务失败！",e);
 		}
		int num = 0;
		JSONObject responseData = JSONObject.parseObject(response);
		if(responseData.getIntValue("resultCode")==0){
			JSONArray jsonArray = responseData.getJSONArray("items");
			for(int i = 0; i<jsonArray.size(); i++){
				JSONObject subsystem = jsonArray.getJSONObject(i);
				if(subsystem.getString("STATUS").equals("ONLINE") ){
					num++;
				}
			}
		}
		return response;
	}
}
