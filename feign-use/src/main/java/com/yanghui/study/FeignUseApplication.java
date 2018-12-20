package com.yanghui.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FeignUseApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeignUseApplication.class, args);
	}
}
