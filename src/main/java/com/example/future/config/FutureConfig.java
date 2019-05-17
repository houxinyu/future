package com.example.future.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:future.properties")
@ConfigurationProperties(prefix = "future")
public class FutureConfig {
	
	@Value("#{'${futrue.alertList}'.split(',')}")
	private List<String> alertList;
	

	@Value("#{'${futrue.alertNameList}'.split(',')}")
	private List<String> alertNameList;
	
	public List<String> getAlertList() {
		return alertList;
	}

	public void setAlertList(List<String> alertList) {
		this.alertList = alertList;
	}

	public List<String> getAlertNameList() {
		return alertNameList;
	}

	public void setAlertNameList(List<String> alertNameList) {
		this.alertNameList = alertNameList;
	}


}
