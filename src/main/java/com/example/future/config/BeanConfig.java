package com.example.future.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.future.macd.DataHandle2;
import com.example.future.macd.DataHandle3;
import com.example.future.macd.DataHandleAll;
import com.example.future.macd.DataHandleApi;

@Configuration
public class BeanConfig {
	
	@Autowired
	FutureConfig config;
	
	@Bean
	public DataHandle2 dataHandle2() {
		return new DataHandle2(config);
	}
	
	@Bean
	public DataHandle3 dataHandle3() {
		return new DataHandle3(config);
	}
	
	@Bean
	public DataHandleAll dataHandleAll() {
		return new DataHandleAll(config);
	}
	
	@Bean
	public DataHandleApi dataHandleApi() {
		return new DataHandleApi(config);
	}

}
