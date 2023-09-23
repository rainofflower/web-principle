package com.yanghui.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@Import(WebConfig.class)
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
		System.out.println(ClassLoader.getSystemClassLoader());			//sun.misc.Launcher$AppClassLoader@xxx
		/**
		 * idea直接启动，和上面一样（当作普通maven项目）；
		 * 使用spring-boot-maven-plugin打成jar包，java -jar xxx.jar启动，打印org.springframework.boot.loader.LaunchedURLClassLoader@xxx
		 */
		System.out.println(Thread.currentThread().getContextClassLoader());
		/**
		 * 同上，打印
		 * org.springframework.boot.loader.LaunchedURLClassLoader@xxx
		 */
		System.out.println(UserServiceApplication.class.getClassLoader());
	}
}
