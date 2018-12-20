package com.yanghui.study.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.yanghui.study.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
public class Controller {

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String testHttps(){
		return "啦啦啦啦";
	}

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public Map<String,Object> getUser(@RequestBody Map<String,Object> request){
		log.info("==>请求user-service服务-user接口:参数{}",request.toString());
		Map<String,Object> data = new HashMap<>();
		data.put("resultCode", 0);
		List<Map<String,Object>> items = new ArrayList<>();
		Map<String,Object> item1 = new HashMap<>();
		item1.put("CODE","111");
		item1.put("STATUS","ONLINE");
		items.add(item1);
		Map<String,Object> item2 = new HashMap<>();
		item2.put("CODE","哈哈哈");
		item2.put("STATUS","OFFLINE");
		items.add(item2);
		data.put("items",items);
		return data;
	}
}
