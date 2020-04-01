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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.future.config.FutureConfig;
import com.example.future.macd.AlertUtil;
import com.example.future.macd.KEntity;
import com.example.future.service.FutureService;
import com.example.future.service.MailService;
import com.example.future.tools.DingDingMessageUtil;
import com.example.future.tools.JsonUtils;

@Component
@ConfigurationProperties
public class FutureSchedule {
	private static final Logger LOGGER = LoggerFactory.getLogger(FutureSchedule.class);
	
	public static boolean isGettingFiftenDatas = false;
	
	public static boolean haveGettedHourDatas = false;
	
	@Autowired
	FutureConfig config;
	
	@Autowired
	FutureService futureService;
	
	@Autowired
	MailService mailService;
	
	private Map<String,String> map = new HashMap<>();
	
	//30分钟预警
//    @Scheduled(cron="10 38 13 ? * MON-FRI")
//    public void alertFor30_test(){
//    	alert(30, 0);
//    	alert(30, 1);
//    	alert(30, 2);
//    }
////    @Scheduled(cron="10 29,59 21,22 ? * MON-FRI")
//    public void alertFor30_1(){
//    	alert(30, 1, 2);
//    }
////    @Scheduled(cron="10 29 23 ? * MON-FRI")
//    public void alertFor30_2(){
//    	alert(30, 2);
//    }
////    @Scheduled(cron="10 29,59 9 ? * MON-FRI")
//    public void alertFor30_3(){
//    	alert(30, 0, 1, 2);
//    }
////    @Scheduled(cron="10 44 10,13,14 ? * MON-FRI")
//    public void alertFor30_4(){
//    	alert(30, 0, 1, 2);
//    }
////    @Scheduled(cron="10 14 11,13,14 ? * MON-FRI")
//    public void alertFor30_5(){
//    	alert(30, 0, 1, 2);
//    }
    
    
    /**
     * 半小时预警 9:29,9:59,   10:44,13:44,14:44  14:14,11:14,
     */
  @Scheduled(cron="10 29,59 9 ? * MON-FRI")
  public void alertFor30_1(){
  	alert(30, 0);
  }
  @Scheduled(cron="10 44 10,13,14 ? * MON-FRI")
  public void alertFor30_2(){
  	alert(30, 0);
  }
  @Scheduled(cron="10 14 11,14 ? * MON-FRI")
  public void alertFor30_3(){
  	alert(30, 0);
  }
    
 
    
    /**
     * 1小时预警
     * 0	0:00	0:00	0:00	9:58	0:00	11:13	0:00	14:13	0:00	14:58
     * 1	21:58	22:58	0:00	9:58	0:00	11:13	0:00	14:13	0:00	14:58
     * 2	21:58	22:58	9:28	0:00	10:43	0:00	13:43	0:00	14:43	0:00
     */
    //测试
//    @Scheduled(cron="40 07 17 ? * *")
//    public void alertForHour_test(){
//    	crawlFiftenDatas_2();
//    	crawHourDatas_1();
//    	alert(60, 0, 1, 2);
//    }
    
//    //晚上
//    @Scheduled(cron="40 58 21,22 ? * MON-FRI")
//    public void alertForHour_1(){
//    	alert(60, 1, 2);
//    }
//    //白天1
//    @Scheduled(cron="40 13 11,14 ? * MON-FRI")
//    public void alertForHour_2(){
//    	alert(60, 0, 1);
//    }
//    //白天2
//    @Scheduled(cron="40 43 10,13,14 ? * MON-FRI")
//    public void alertForHour_3(){
//    	alert(60, 2);
//    }
//    @Scheduled(cron="40 28 9 ? * MON-FRI")
//    public void alertForHour_4(){
//    	alert(60, 2);
//    }
//    @Scheduled(cron="40 58 9,14 ? * MON-FRI")
//    public void alertForHour_5(){
//    	alert(60, 0, 1);
//    }
    
  //算法说明 
    
    //白天0：9:58	0:00	11:13	0:00	14:13	0:00	14:58
    @Scheduled(cron="10 59 9,14 ? * MON-FRI")//对应的是文化财经9:00和14:15开始的k线
    public void alertForHour_1(){
    	alert(60, 0);
    	//printHour(60);
    }
    @Scheduled(cron="10 14 11,14 ? * MON-FRI")//对应的是文化财经10:00和11:15开始的k先
    public void alertForHour_2(){
    	alert(60, 0);
    	//printHour(60);
    }
    
    
    @Scheduled(cron="10 10 15 ? * MON-FRI")//每天收盘之后，打印当天的k线数据，以便进行验证
    public void printDataAfterTraderTime() {
    	printHour(30);
    	printHour(60);
    	//每天设置状态为false，第二天就可以再重新抓取异常1小时的数据
    	haveGettedHourDatas = false;
    }
    
    public void printHour(int min) {
		JSONArray symbolArray = JsonUtils.readSymbols();
		for(Object s:symbolArray) {
			JSONObject symbol = (JSONObject)s;
			ArrayList<KEntity> cacheList = AlertUtil.getListFromMap(symbol.getString("Symbol")+min);
			LOGGER.info("==============================================");
			LOGGER.info(symbol.getString("Symbol")+min+ "分钟数据：");
			int i = 0;
			for(KEntity k:cacheList) {
				LOGGER.info((i++)+"\t"+k.getTime()+"\t"+k.getOpen()+"\t"+k.getHigh()+"\t"+k.getLow()+"\t"+k.getClose()+"\t"+k.getMacd()+"\t"+k.getDif()+"\t"+k.getDea());
			}
		}
    	
    }
    
    public void printHour(int min,String symbolToPrt) {
		JSONArray symbolArray = JsonUtils.readSymbols();
		for(Object s:symbolArray) {
			JSONObject symbol = (JSONObject)s;
			if(symbolToPrt.equals(symbol.getString("Symbol"))) {
				ArrayList<KEntity> cacheList = AlertUtil.getListFromMap(symbol.getString("Symbol")+min);
				LOGGER.info("==============================================");
				LOGGER.info(symbol.getString("Symbol")+min+ "分钟数据：");
				int i = 0;
				for(KEntity k:cacheList) {
					LOGGER.info((i++)+"\t"+k.getTime()+"\t"+k.getOpen()+"\t"+k.getHigh()+"\t"+k.getLow()+"\t"+k.getClose()+"\t"+k.getMacd()+"\t"+k.getDif()+"\t"+k.getDea());
				}
			}

		}
    	
    }
    
    
   //day预警type0,type1,type2
//    @Scheduled(cron="30 06 16 ? * *")
    public void alertForDay_test(){
    	alertDay(3600, 0);
    	alertDay(3600, 1);
    	alertDay(3600, 2);
    }
    
  
    //day预警type0,type1,type2
    //@Scheduled(cron="0 50 14 ? * MON-FRI")
    public void alertForDay(){
    	alertDay(3600,0,1,2);
    }
    

   
    //获取15分钟数据，每15分钟一次
    //测试
//    @Scheduled(cron="0 37 13 ? * MON-FRI")
//    public void crawlFiftenDatas_test(){
//    	isGettingFiftenDatas = true;
//    	futureService.getFiftenDatas(15, 0);
//    	futureService.getFiftenDatas(15, 1);
//    	futureService.getFiftenDatas(15, 2);
//    	if(!haveGettedHourDatas) {
//    		haveGettedHourDatas = true;
//    		crawHourDatas_1();
//    	}
//		isGettingFiftenDatas = false;
//    }
    
//    @Scheduled(cron="0 28,58 21,22,23 ? * MON-FRI")
//    public void crawlFiftenDatas_1(){
//    	isGettingFiftenDatas = true;
////    	futureService.getFiftenDatas(15, 0);
//    	futureService.getFiftenDatas(15, 1);
//    	futureService.getFiftenDatas(15, 2);
//    	if(!haveGettedHourDatas) {
//    		haveGettedHourDatas = true;
//    		crawHourDatas_1();
//    	}
//		isGettingFiftenDatas = false;
//    }
    
    
    //@Scheduled(cron="0 28,58,43,13 9,10,11,13,14 ? * MON-FRI")
    public void crawlFiftenDatas_2(){
    	LOGGER.info("抓取15分钟历史数据开始...");
    	isGettingFiftenDatas = true;
    	futureService.getFiftenDatas(15, 0);
    	//futureService.getFiftenDatas(15, 1);
    	//futureService.getFiftenDatas(15, 2);
    	if(!haveGettedHourDatas) {
    		haveGettedHourDatas = true;
    		crawHourDatas_1();
    	}
		isGettingFiftenDatas = false;
		LOGGER.info("抓取15分钟历史数据完成...");
    }
    
    
    //获取1小时数据，每天1次
    //@Scheduled(cron="0 58 8,20 ? * MON-FRI")
    public void crawHourDatas_1(){
    	LOGGER.info("抓取1小时历史数据开始...");
    	futureService.getHourDatas(60, 0);
    	futureService.getHourDatas(60, 1);
    	futureService.getHourDatas(60, 2);
    	LOGGER.info("抓取1小时历史数据完成...");
    }
    
    public void crawHalfHourDatas_1(){
    	LOGGER.info("抓取半小时历史数据开始...");
    	futureService.getHourDatas(30, 0);
    	futureService.getHourDatas(30, 1);
    	futureService.getHourDatas(30, 2);
    	LOGGER.info("抓取半小时历史数据完成...");
    }
    
    //在特定时间启动一次抓取
    //30 10 1 20 10 ? 2011--------- 2011年10月20号1点10分30秒触发任务，
    //不需要运行，因为在采集15分钟数据时，会先判断小时数据是否已经采集
//    @Scheduled(cron="30 49 19 ? * MON-FRI")
//    public void crawHourDatas_2(){
//    	crawHourDatas_1();
//    }
    
//    //测试1
//    @Scheduled(cron="30 03 21 ? * MON-FRI")
//    public void crawHourDatas_2(){
//    	crawHourDatas_1();
////    	crawlFiftenDatas_2();
//    	alertForHour_1();
//    	printHour(60);
//    }
//    
//    //测试2
//    @Scheduled(cron="30 15 21 ? * MON-FRI")
//    public void crawHalfHourDatas_2(){
//    	crawHalfHourDatas_1();
//    	alertFor30_1();
//    	printHour(30);
//    }
    
    //半小时
    //9:28,9:58,10:43,11:13,13:43,14:13,14:43,
  //9:28,10:43,13:43,14:43,
  //@Scheduled(cron="30 28 9 ? * MON-FRI")
  public void warnForHalf1(){
	  //只看半小时
	  DingDingMessageUtil.sendTextMessage(new Date() + "查看30分钟数据", config.getAccessToken());
  }
  //@Scheduled(cron="30 43 10,13,14 ? * MON-FRI")
  public void warnForHalf2(){
	  //只看半小时
	  DingDingMessageUtil.sendTextMessage(new Date() + "查看30分钟数据", config.getAccessToken());
  }


    //9:58,11:13,14:13,14:58
  //@Scheduled(cron="30 58 9,14 ? * MON-FRI")
  public void warnForHalfAndHour1(){
	  //同时看半小时和1小时
	  DingDingMessageUtil.sendTextMessage(new Date() + "查看30分钟和1小时数据", config.getAccessToken());

  }
  //@Scheduled(cron="30 13 11,14 ? * MON-FRI")
  public void warnForHalfAndHour2(){
	  //同时看半小时和1小时
	  DingDingMessageUtil.sendTextMessage(new Date() + "查看30分钟和1小时数据", config.getAccessToken());

  }
    
    
    /**
     * 考虑到信号比较多，后续会对信号进行筛选，只有那些柱子比较少，或者只在中轨一侧的信号才进行预警，尤其是30分钟的信号。
     * @param min
     */
    public void alert(int min, int ...types){
//    	while(isGettingFiftenDatas) {
//    		try {
//				Thread.sleep(10 * 1000);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//    	}
		
		LOGGER.info("执行任务：" + new Date());
		
//		if(map.size() == 0) {
//			for(String name:config.getAlertNameList()) {
//				map.put(name.split("_")[0], name.split("_")[1]);
//			}
//		}
		
		ArrayList<String> list = new ArrayList<>();
				
		for(int type:types) {
			list.addAll(futureService.futureAlert(min, type));
		}


		
		List<String> alertList = new ArrayList<>();
		Set<String> alertSet = new HashSet<>();
		for(int i=0; i<list.size(); i++) {
			if(!alertSet.contains(list.get(i).replaceAll("[0-9]+", ""))) {
				if(JsonUtils.getName(list.get(i)) != null) {
					alertList.add(JsonUtils.getName(list.get(i)));
				}else {
					alertList.add(list.get(i));
				}
				
				//打印数据
				printHour(min, JsonUtils.getSymbol(alertList.get(alertList.size()-1)));
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
    
    public void alertDay(int min, int ...types){
		
		LOGGER.info("执行任务：" + new Date());
		
//		if(map.size() == 0) {
//			for(String name:config.getAlertNameList()) {
//				map.put(name.split("_")[0], name.split("_")[1]);
//			}
//		}
		
//		ArrayList<String> list = futureService.futureAlertDay(min, type);
		
		ArrayList<String> list = new ArrayList<>();
		
		for(int type:types) {
			list.addAll(futureService.futureAlertDay(min, type));
		}
		
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
