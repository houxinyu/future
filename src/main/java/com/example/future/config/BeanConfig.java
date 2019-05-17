package com.example.future.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import macd.DataHandle2;

@Configuration
public class BeanConfig {
	
	@Autowired
	FutureConfig config;
	
	@Bean
	public DataHandle2 dataHandle2() {
		return new DataHandle2(config);
	}

}
