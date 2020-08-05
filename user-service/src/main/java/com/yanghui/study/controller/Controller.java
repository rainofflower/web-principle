package com.yanghui.study.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.yanghui.study.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.Callable;

@Slf4j
@RestController
public class Controller {

	/**
	 * request里面保存着请求中 起始行和首部数据
	 * 参数列表保存着请求 参数、主体
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(path = "/user", method = {RequestMethod.POST,RequestMethod.GET})
	public Callable<Map<String,Object>> getUser(@RequestBody Map<String,Object> param, HttpServletRequest request){
		return () -> {
			log.info("--request--\nmethod:{} protocol:{}",request.getMethod(),request.getProtocol());
			Enumeration<String> headerNames = request.getHeaderNames();
			while(headerNames.hasMoreElements()){
				String s = headerNames.nextElement();
				String header = request.getHeader(s);
				System.out.println(s + ": "+header);
			}
			Enumeration<String> parameterNames = request.getParameterNames();
			while (parameterNames.hasMoreElements()){
				String s = parameterNames.nextElement();
				String parameter = request.getParameter(s);
				System.out.println(s + ": "+parameter);
			}
			log.info("==>请求user-service服务-user接口:参数{}",param.toString());
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
		};
		/*try {
            Thread.sleep(5000);
        }catch (Exception e){
		    //
        }*/
	}

	@GetMapping("/simple")
	public String simpleTest(HttpServletRequest request){
		log.info("--request--\nmethod:{} scheme:{} protocol:{}\ncontentType:{}",request.getMethod(),
				request.getScheme(),
				request.getProtocol(),
				request.getContentType());
		log.info("收到请求...");
		return "请求成功啦~~";
	}

	@RequestMapping("/httpClient")
	public String httpClient(@RequestParam String appno){
		log.info("收到请求...{}",appno);
		return "请求成功啦~~";
	}

	@RequestMapping("/test")
	public String httpTest(@RequestBody List<User> list, HttpServletRequest request, HttpServletResponse response){
		log.info("收到请求...{}",JSON.toJSONString(list));
		return "请求成功啦~~";
	}
}
