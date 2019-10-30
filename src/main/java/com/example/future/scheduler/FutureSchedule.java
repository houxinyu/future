package com.example.future.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.future.config.FutureConfig;
import com.example.future.service.FutureService;
import com.example.future.service.MailService;
import com.example.future.tools.DingDingMessageUtil;

@Component
@ConfigurationProperties
public class FutureSchedule {
	private static final Logger LOGGER = LoggerFactory.getLogger(FutureSchedule.class);
	
	
	@Autowired
	FutureConfig config;
	
	@Autowired
	FutureService futureService;
	
	@Autowired
	MailService mailService;
	
	private Map<String,String> map = new HashMap<>();
	
	
//	@Scheduled(cron="* */1 09 * * ?")
	@Scheduled(cron="30 40 17 * * ?")
    public void test1(){

		LOGGER.info("执行任务：" + new Date());
		LOGGER.info("" + config.isSendMail());
		alert(30);
	}
	
	
	
	
//	@Scheduled(cron="01 12 18 * * ?")
//	@Scheduled(cron="01 24 10 * * ?")
    public void test(){

		LOGGER.info("执行任务：" + new Date());
		
		alertForDay();
		
		if(map.size() == 0) {
			for(String name:config.getAlertNameList()) {
				map.put(name.split("_")[0], name.split("_")[1]);
			}
		}
		
		ArrayList<String> list = futureService.futureAlertMain();
		
		for(int i=0;i<list.size();i++) {
			if(map.get(list.get(i)) != null) {
				list.set(i, map.get(list.get(i)) + "(" + list.get(i) + ")");
			}
		}
		
//        String url = null;
		try {
			mailService.sendMail(list.toString());
			
			LOGGER.info(list.toString());
//			url = "http://localhost:8761/sendMail?content="+URLEncoder.encode(list.toString(),"UTF-8");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}

    }
    
    //@Scheduled(cron="45 4,9,14,19,24,29,34,39,44,49,54,59 21-23 ? * MON-FRI")
	//@Scheduled(cron="0 30,40,50 14 * * ?")
    public void alertFor5min(){
		
		LOGGER.info("执行任务：" + new Date());
		
		if(map.size() == 0) {
			for(String name:config.getAlertNameList()) {
				map.put(name.split("_")[0], name.split("_")[1]);
			}
		}
		
		ArrayList<String> list = futureService.futureAlertFiveMin();
		
		List<String> alertList = new ArrayList<>();
		Set<String> alertSet = new HashSet<>();
		for(int i=0; i<list.size(); i++) {
			if(!alertSet.contains(list.get(i).replaceAll("[0-9]+", ""))) {
				if(map.get(list.get(i)) != null) {
					alertList.add(map.get(list.get(i)) + "(" + list.get(i) + ")");
				}else {
					alertList.add(list.get(i) + "(" + list.get(i) + ")");
				}
			}
			alertSet.add(list.get(i).replaceAll("[0-9]+", ""));
		}
		
		try {
			if(alertList.size() > 0) {
				if(config.isSendMail()) {
					mailService.sendMail(alertList.toString());
				}
				if(config.isSendDingtalk()) {
//					DingDingMessageUtil.sendTextMessage(list.toString().replaceAll(",", "\n\n"), config.getAccessToken());
//					Thread.sleep(1000);
					DingDingMessageUtil.sendTextMessage(alertList.toString().replaceAll(",", "\n\n"), config.getAccessToken());
				}
			}
			LOGGER.info(">>>>>>>>>>>>>>" + alertList.toString());
//			url = "http://localhost:8761/sendMail?content="+URLEncoder.encode(list.toString(),"UTF-8");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			try {
				mailService.sendMail("预警程序报错:" + e.getMessage());
			}catch(Exception mailException) {
				
			}
			
		}


    }
    
	//日线预警
    @Scheduled(cron="0 30,40,50 14 ? * MON-FRI")
    public void alertForDay(){
    	alert(3600);
    }
    
    
    //30分钟预警
    @Scheduled(cron="0 28,58 9,10,11,14,21,22,23 ? * MON-FRI")
    public void alertFor30(){
    	alert(30);
    }
    
    
    //60分钟预警
    @Scheduled(cron="0 58 9,10,14,21,22 ? * MON-FRI")
    public void alertFor60(){
    	alert(60);
    }
	
	

    public void alert(int min){
		
		LOGGER.info("执行任务：" + new Date());
		
		if(map.size() == 0) {
			for(String name:config.getAlertNameList()) {
				map.put(name.split("_")[0], name.split("_")[1]);
			}
		}
		
		ArrayList<String> list = futureService.futureAlert(min);
		
		List<String> alertList = new ArrayList<>();
		Set<String> alertSet = new HashSet<>();
		for(int i=0; i<list.size(); i++) {
			if(!alertSet.contains(list.get(i).replaceAll("[0-9]+", ""))) {
				if(map.get(list.get(i)) != null) {
					alertList.add(map.get(list.get(i)) + "(" + list.get(i) + ")");
				}else {
					alertList.add(list.get(i) + "(" + list.get(i) + ")");
				}
			}
			alertSet.add(list.get(i).replaceAll("[0-9]+", ""));
		}
		
		try {
			if(alertList.size() > 0) {
				if(config.isSendMail()) {
					mailService.sendMail(alertList.toString());
				}
				if(config.isSendDingtalk()) {
//					DingDingMessageUtil.sendTextMessage(list.toString().replaceAll(",", "\n\n"), config.getAccessToken());
//					Thread.sleep(1000);
					DingDingMessageUtil.sendTextMessage(min + "预警:\n\n" + alertList.toString().replaceAll(",", "\n\n"), config.getAccessToken());
				}
			}
			LOGGER.info(">>>>>>>>>>>>>>" + alertList.toString());
//			url = "http://localhost:8761/sendMail?content="+URLEncoder.encode(list.toString(),"UTF-8");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			try {
				mailService.sendMail("预警程序报错:" + e.getMessage());
			}catch(Exception mailException) {
				
			}
			
		}


    }
}
