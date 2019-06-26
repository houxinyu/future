package com.example.future.scheduler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.example.future.tools.PageUtil;

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
//	@Scheduled(cron="1 43 17 * * ?")
    public void test1(){

		LOGGER.info("执行任务：" + new Date());
		LOGGER.info("" + config.isSendMail());

		
		
	}
	
	
//	@Scheduled(cron="* 55 13 * * ?")
//	@Scheduled(cron="01 24 10 * * ?")
    public void test(){

		LOGGER.info("执行任务：" + new Date());
		
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
	
			
	@Scheduled(cron="0 30,40,31 18 * * ?")
    public void printDate(){
		
		LOGGER.info("执行任务：" + new Date());
		
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
			if(list.size() > 0) {
				if(config.isSendMail()) {
					mailService.sendMail(list.toString());
				}
				if(config.isSendDingtalk()) {
					DingDingMessageUtil.sendTextMessage(list.toString(), config.getAccessToken());
				}
			}
			LOGGER.info(">>>>>>>>>>>>>>" + list.toString());
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
