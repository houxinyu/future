package com.example.future;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableEurekaServer
@SpringBootApplication
@EnableScheduling
public class FutureApplication {

	public static void main(String[] args) {
//		AppMain2.预警主程序();
		SpringApplication.run(FutureApplication.class, args);
	}
	
	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		taskScheduler.setThreadNamePrefix("spring-task");
		return taskScheduler;
		
	}

}
