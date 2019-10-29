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
	@Scheduled(cron="0 34 15 * * ?")
    public void test1(){

		LOGGER.info("执行任务：" + new Date());
		LOGGER.info("" + config.isSendMail());
		//printDate();
		DingDingMessageUtil.sendTextMessage("测试", config.getAccessToken());

		
		
	}
	
	
//	@Scheduled(cron="01 12 18 * * ?")
//	@Scheduled(cron="01 09 15 * * ?")
    public void test(){

		LOGGER.info("执行任务：" + new Date());
		
		dayMacdAlert();
		
//		if(map.size() == 0) {
//			for(String name:config.getAlertNameList()) {
//				map.put(name.split("_")[0], name.split("_")[1]);
//			}
//		}
//		
//		ArrayList<String> list = futureService.futureAlertMain();
//		
//		for(int i=0;i<list.size();i++) {
//			if(map.get(list.get(i)) != null) {
//				list.set(i, map.get(list.get(i)) + "(" + list.get(i) + ")");
//			}
//		}
//		
////        String url = null;
//		try {
//			mailService.sendMail(list.toString());
//			
//			LOGGER.info(list.toString());
////			url = "http://localhost:8761/sendMail?content="+URLEncoder.encode(list.toString(),"UTF-8");
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage(),e);
//		}

    }
	
    //0 15 10 ? * MON-FRI
			
    @Scheduled(cron="0 30,40,50 14 ? * MON-FRI")
	//@Scheduled(cron="0 30,40,50 14 * * ?")
    public void dayMacdAlert(){
		
		LOGGER.info("执行任务：" + new Date());
		
		if(map.size() == 0) {
			for(String name:config.getAlertNameList()) {
				map.put(name.split("_")[0], name.split("_")[1]);
			}
		}
		
		ArrayList<String> list = futureService.futureAlertMain();
		
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
		
//		for(int i=0;i<list.size();i++) {
//			if(map.get(list.get(i)) != null) {
//				list.set(i, map.get(list.get(i)) + "(" + list.get(i) + ")");
//			}
//		}
		
//        String url = null;
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
       
//		if(url != null) {
//	        String content = PageUtil.httpGet(url);
//	        LOGGER.info(content);
//		}else {
//			LOGGER.warn("url is null.");
//		}


    }
}
