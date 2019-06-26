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
	
	@Value("#{'${future.alertList}'.split(',')}")
	private List<String> alertList;
	

	@Value("#{'${future.alertNameList}'.split(',')}")
	private List<String> alertNameList;
	
	//futrue.accesstoken
//	@Value("${accessToken}") 
//	@Value("${futrue.accessToken}")
	@Value("${future.accessToken}")
	private String accessToken;
	
	@Value("${future.sendMail}")
	private boolean sendMail;
	

	@Value("${future.sendDingtalk}")
	private boolean sendDingtalk;
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

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

	
	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}

	public boolean isSendDingtalk() {
		return sendDingtalk;
	}

	public void setSendDingtalk(boolean sendDingtalk) {
		this.sendDingtalk = sendDingtalk;
	}

}
