package com.example.future.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.example.future.tools.JsonUtils;

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
	
	//30分钟预警
    //@Scheduled(cron="0 28,58 21,22 ? * MON-FRI")
    public void alertFor30_1(){
    	alert(30, 0);
    	alert(30, 1);
    	alert(30, 2);
    }
    //@Scheduled(cron="0 28 23 ? * MON-FRI")
    public void alertFor30_2(){
    	alert(30, 2);
    }
    //@Scheduled(cron="0 28,58 9 ? * MON-FRI")
    public void alertFor30_3(){
    	alert(30, 0);
    	alert(30, 1);
    	alert(30, 2);
    }
    //@Scheduled(cron="0 43 10,13,14 ? * MON-FRI")
    public void alertFor30_4(){
    	alert(30, 0);
    	alert(30, 1);
    	alert(30, 2);
    }
    //@Scheduled(cron="0 13 11,13,14 ? * MON-FRI")
    public void alertFor30_5(){
    	alert(30, 0);
    	alert(30, 1);
    	alert(30, 2);
    }
    
    
	
	//1小时测试
//    @Scheduled(cron="30 11 14 ? * *")
    public void alertFor60_test(){
    	alert(60, 0);
    	alert(60, 1);
    	alert(60, 2);
    }
    
    //晚上：21:58,22:58,
    //白天：09:28,09:58,10:43,11:13,13:43,
    
    //60分钟预警type2
    @Scheduled(cron="0 58 21,22,14 ? * MON-FRI")
    public void alertFor60_1(){
    	alert(60, 2);
    }
    @Scheduled(cron="0 28 9 ? * MON-FRI")
    public void alertFor60_2(){
    	alert(60, 2);
    }
    @Scheduled(cron="0 43 10,13,14 ? * MON-FRI")
    public void alertFor60_3(){
    	alert(60, 2);
    }
    
  
    //60分钟预警type1
    @Scheduled(cron="0 58 9,14,21,22 ? * MON-FRI")
    public void alertFor60_4(){
    	alert(60, 1);
    }
    @Scheduled(cron="0 13 11,14 ? * MON-FRI")
    public void alertFor60_5(){
    	alert(60, 1);
    }
    
  
    //60分钟预警type0
    @Scheduled(cron="0 58 9,14 ? * MON-FRI")
    public void alertFor60_6(){
    	alert(60, 0);
    }
    @Scheduled(cron="0 13 11,14 ? * MON-FRI")
    public void alertFor60_7(){
    	alert(60, 0);
    }
    
    
   //day预警type0,type1,type2
    @Scheduled(cron="30 06 16 ? * *")
    public void alertForDay_test(){
    	alertDay(3600, 0);
    	alertDay(3600, 1);
    	alertDay(3600, 2);
    }
    
  
    //day预警type0,type1,type2
    @Scheduled(cron="0 50 14 ? * MON-FRI")
    public void alertForDay(){
    	alertDay(3600, 0);
    	alertDay(3600, 1);
    	alertDay(3600, 2);
    }
    
    /**
     * 考虑到信号比较多，后续会对信号进行筛选，只有那些柱子比较少，或者只在中轨一侧的信号才进行预警，尤其是30分钟的信号。
     * @param min
     */
    public void alert(int min, int type){
		
		LOGGER.info("执行任务：" + new Date());
		
//		if(map.size() == 0) {
//			for(String name:config.getAlertNameList()) {
//				map.put(name.split("_")[0], name.split("_")[1]);
//			}
//		}
		
		ArrayList<String> list = futureService.futureAlert(min, type);
		
		List<String> alertList = new ArrayList<>();
		Set<String> alertSet = new HashSet<>();
		for(int i=0; i<list.size(); i++) {
			if(!alertSet.contains(list.get(i).replaceAll("[0-9]+", ""))) {
				if(JsonUtils.getName(list.get(i)) != null) {
					alertList.add(JsonUtils.getName(list.get(i)));
				}else {
					alertList.add(list.get(i));
				}
			}
			alertSet.add(list.get(i).replaceAll("[0-9]+", ""));
		}
		
		try {
			GregorianCalendar gc = new GregorianCalendar();
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(gc.getTime());
			
			
			if(alertList.size() > 0) {
				if(config.isSendMail()) {
					mailService.sendMail(alertList.toString());
				}
				if(config.isSendDingtalk()) {
//					DingDingMessageUtil.sendTextMessage(list.toString().replaceAll(",", "\n\n"), config.getAccessToken());
//					Thread.sleep(1000);
					DingDingMessageUtil.sendTextMessage(time + "\n\n" + min + "预警:\n\n" + alertList.toString().replaceAll(",", "\n\n"), config.getAccessToken());
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
    
    public void alertDay(int min, int type){
		
		LOGGER.info("执行任务：" + new Date());
		
//		if(map.size() == 0) {
//			for(String name:config.getAlertNameList()) {
//				map.put(name.split("_")[0], name.split("_")[1]);
//			}
//		}
		
		ArrayList<String> list = futureService.futureAlertDay(min, type);
		
		List<String> alertList = new ArrayList<>();
		Set<String> alertSet = new HashSet<>();
		for(int i=0; i<list.size(); i++) {
			if(!alertSet.contains(list.get(i).replaceAll("[0-9]+", ""))) {
				if(JsonUtils.getName(list.get(i)) != null) {
					alertList.add(JsonUtils.getName(list.get(i)));
				}else {
					alertList.add(list.get(i));
				}
			}
			alertSet.add(list.get(i).replaceAll("[0-9]+", ""));
		}
		
		try {
			GregorianCalendar gc = new GregorianCalendar();
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(gc.getTime());
			
			
			if(alertList.size() > 0) {
				if(config.isSendMail()) {
					mailService.sendMail(alertList.toString());
				}
				if(config.isSendDingtalk()) {
//					DingDingMessageUtil.sendTextMessage(list.toString().replaceAll(",", "\n\n"), config.getAccessToken());
//					Thread.sleep(1000);
					DingDingMessageUtil.sendTextMessage(time + "\n\n" + min + "预警:\n\n" + alertList.toString().replaceAll(",", "\n\n"), config.getAccessToken());
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
