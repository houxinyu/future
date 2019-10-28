package macd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.future.config.FutureConfig;

import tool.时间工具;
import tool.配置文件;

/**
 * 实时行情信息抓取类
 * 
 * @author content
 * @version 1.0 create at 2012-5-8
 */

@Component
public class DataHandle2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataHandle2.class);
	

	private FutureConfig config;
	
	public DataHandle2(FutureConfig config) {
		this.config = config;
	}
	
//	public static ArrayList<String> futureAlertMain(){
//		DataHandle2 dataHandle2 = new DataHandle2();
//		dataHandle2.clearHistoryData();
//		dataHandle2.loadHistoryData();
//		dataHandle2.loadTodayKEntity();
//		dataHandle2.caculateMACD();
//		return dataHandle2.caculateAlert();
//		
//	}
	

	public  void clearHistoryData() {
		AlertUtil.getKentityListMap().clear();
	}
	
	public  void loadHistoryData(){
		LOGGER.info("加载历史K线数据...");
		long start= System.currentTimeMillis();
		List<String> alertList = config.getAlertList();
		
		LOGGER.info("alertList:" + alertList);
		
		ArrayList<KEntity> list=new ArrayList<KEntity>();
		for(String urlCodeName: alertList) {
			list=loadHisMinData(urlCodeName,3600);
			LOGGER.info((urlCodeName+"3600")+"历史数据 size:" + list.size());
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
			}
			LOGGER.info(list.get(0).getName()+list.get(0).getMin()+"历史数据：" + list.size());

		}
		long end= System.currentTimeMillis();
		LOGGER.info("加载历史K线数据完毕。耗时："+(start-end)/1000+"秒");
	}
	
	public  void loadTodayKEntity(){
		LOGGER.info("加载今日K线数据...");
		long start= System.currentTimeMillis();
		List<String> alertList = config.getAlertList();
		LOGGER.info("alertList:" + alertList);

		ArrayList<KEntity> list=new ArrayList<KEntity>();
		for(String urlCodeName:alertList) {
			try {
				list=loadHisMinData(urlCodeName,0);
				if(list.size()>0){
					AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
				}
			}catch(Exception e) {
				LOGGER.error(e.getMessage(),e);
			}

		}
		long end= System.currentTimeMillis();
		LOGGER.info("加载今日K线数据完毕。耗时：" + (start - end)/1000+"秒");
	}
	
	//一次请求所有
	public  void loadTodayKEntityList(){
		LOGGER.info("加载今日K线数据...");
		long start= System.currentTimeMillis();
		List<String> alertList = config.getAlertList();
		LOGGER.info("alertList:" + alertList);
		
//		String[] crawlList = AlertUtil.抓新浪取历史数据("BU0,MA0,RU0,TA0,HC0,RB0,I0,FG0,JM0,J0,ZC0,RM0,M0,A0,P0,Y0,OI0,CF0,SR0,C0,CS0,JD0,PP0,L0,V0,EG0,AP0,SF0,SM0,SP0", 0).replaceAll("var hq_str_", "").split(";");
//
//		ArrayList<KEntity> list=new ArrayList<KEntity>();
//		for(String urlCodeName:alertList) {
//			list=loadHisMinData(urlCodeName,0);
//			if(list.size()>0){
//				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
//			}
//		}
//		long end= System.currentTimeMillis();
//		LOGGER.info("加载今日K线数据完毕。耗时：" + (start - end)/1000+"秒");
	}


	public  void caculateMACD() {
//		String[] alertList=配置文件.获取配置项("alertList").split(",");
		List<String> alertList = config.getAlertList();
		for(String urlCode:alertList){
			ArrayList<KEntity> list = AlertUtil.getListFromMap(urlCode + "3600");
			if(list != null && list.size() != 0) {
				MyMACD.setMACD(list);
			}
		}
	}
	
	public  ArrayList<String> caculateAlert() {
//		String[] alertList=配置文件.获取配置项("alertList").split(",");
		ArrayList<String> alertCodeList = new ArrayList<>();
		List<String> alertList = config.getAlertList();
		for(String urlCode:alertList){
			ArrayList<KEntity> list = AlertUtil.getListFromMap(urlCode + "3600");
			if(list == null || list.size() == 0) {
				LOGGER.warn(urlCode + "数据不存在！");
				//发送邮件
			}else {
				KEntity todayEntity = list.get(list.size()-1);
				KEntity preEntity = list.get(list.size()-2);
				KEntity ppEntity = list.get(list.size()-3);
				//
				boolean 绿转 = preEntity.getDea() > 0 && preEntity.getMacd() < ppEntity.getMacd() && preEntity.getMacd() < todayEntity.getMacd() && todayEntity.getMacd() < 0;
				boolean 红转 = preEntity.getDea() < 0 && preEntity.getMacd() > ppEntity.getMacd() && preEntity.getMacd() > todayEntity.getMacd() && todayEntity.getMacd() > 0;
				
				if(绿转 || 红转) {
					LOGGER.info("======================================================================");
					
					LOGGER.info("品种:" + urlCode + " 出现转折:" + 绿转 + "," +  红转);
					
					LOGGER.info(todayEntity.getDea() + ":" + todayEntity.getDea() + "," + ppEntity.getMacd() + "," + preEntity.getTime() + ":" + preEntity.getMacd()+ "," + todayEntity.getMacd());
					
					
					alertCodeList.add(urlCode);
					
					LOGGER.info("======================================================================");
				}

			}
		}
		
		LOGGER.info("alertCodeList size:" + alertCodeList.size());
		
		return alertCodeList;
	}

	private static ArrayList<KEntity> getDayList(String codeName,int code0){
		ArrayList<KEntity> dayList=new ArrayList<KEntity>();
		try {
			String UrlCodeName=AlertUtil.getUrlCodeName(codeName);
			if(code0==0){
				UrlCodeName=AlertUtil.获得主力合约代码(codeName);
			}
			String[] data = AlertUtil.抓新浪取历史数据(UrlCodeName, 3600).replace("\"", "").replace("[","").split(";");																	
			for (int k = 0; k <=data.length-1; k++) {
				int r=loadHisData(data[k],codeName,3600,dayList);
				if(r==0){//无效数据
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(),e);
		}

		return dayList;
	}


	public static ArrayList<KEntity> delSubList(ArrayList<KEntity> list,int size){
		ArrayList<KEntity> listNew=new ArrayList<KEntity>();
		if(list.size()>size){
			for(int i=list.size()-size;i<list.size();i++){
				KEntity tempEntity=list.get(i);
				tempEntity.setPreIndex(listNew.size()-1);
				listNew.add(tempEntity);
			}
		}else{
			for(int i=0;i<list.size();i++){
				KEntity tempEntity=list.get(i);
				tempEntity.setPreIndex(listNew.size()-1);
				listNew.add(tempEntity);
			}
		}
		return listNew;
	}
	
	/**
	 * 
	 * @param urlCodeName
	 * @param min 3600代表历史日线，0代表当天日线
	 * @return
	 */
	public static ArrayList<KEntity> loadHisMinData(String urlCodeName,int min){
		ArrayList<KEntity> list=new ArrayList<KEntity>();
//		String codeName=AlertUtil.getCodeName(urlCodeName);
		if(min==3600){//处理历史日线数据
			String[] data = AlertUtil.抓新浪取历史数据(urlCodeName, min).replace("\"", "").replace("[",
			"").split(";");
			for (int i = 0; i <=data.length-1; i++) {
				int r=loadHisData(data[i],urlCodeName,min,list);
				if(r==0){//无效数据
					break;
				}
			}
		}else if(min==0){//处理当天日线数据
			//varhq_str_M0="豆粕连续,145958,开盘价[index=2],最高价[index=3],最低价[index=4],3178,3153,最新价[index=7],3154,3162,3169,1325,223,1371608,成交量[index=14],连,豆粕,2013-06-28[index=17]";
			
			list = AlertUtil.getListFromMap(urlCodeName+3600);
			
			LOGGER.info((urlCodeName+"3600") + "历史数据：" + (null == list));
			
			LOGGER.info("历史数据：" + list.size());
			LOGGER.info("今日数据:" + AlertUtil.抓新浪取历史数据(urlCodeName, min));
			
			String[] data = AlertUtil.抓新浪取历史数据(urlCodeName, min).replace("\"",
			"").split(",");
			
			KEntity todayEntity = new KEntity();
			todayEntity.setTime(data[17]);
			todayEntity.setOpen(Double.parseDouble(data[2]));
			todayEntity.setHigh(Double.parseDouble(data[3]));
			todayEntity.setLow(Double.parseDouble(data[4]));
			todayEntity.setClose(Double.parseDouble(data[7]));
			
			list.add(todayEntity);

		}
		return list;

	}
	
	//因为新浪历史数据接口，日线数据和其他时间框架数据的前后顺序不一样，所以遍历一个从前到后
	private static int loadHisData(String data,String name,int min,ArrayList<KEntity> list){
		
		String[] infos = data.split(",");
		if(infos[0].length()<11){
			infos[0]=infos[0]+" 00:00:00";
		}
		//如果现在是15:00-00:00，则取日期为今天且时间为15:00:00之前的数据
		//如果现在是00:00-15:00，则取日期为前一天，且时间为15:00之前的数据
		int hour=时间工具.获得现在小时();
		String stopTime=时间工具.获得今日日期()+" 15:00:00";
		if(hour<15){
			stopTime=时间工具.取得前一交易日期()+" 15:00:00";
		}else{
			stopTime=时间工具.获得今日日期()+" 15:00:00";
		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
        try {
			Date skipTime = sdf.parse(stopTime);
//			System.out.println("￥￥￥￥￥："+infos[0]);
			Date dataTime = sdf.parse(infos[0]);
//			System.out.println(stopTime+" "+infos[0]);
			if(dataTime.getTime()>skipTime.getTime()){
				//break;
				return 0;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
		}
		String tp=getTpFromName(name);
		KEntity kEntity = new KEntity();
		kEntity.setTP(tp);
		kEntity.setName(name);
		kEntity.setMin(min);
		kEntity.setTime(infos[0]);
		kEntity.setOpen(Double.parseDouble(infos[1]));
		kEntity.setHigh(Double.parseDouble(infos[2]));
		kEntity.setLow(Double.parseDouble(infos[3]));
		kEntity.setClose(Double.parseDouble(infos[4]));
		kEntity.setPreIndex(list.size()-1);
		list.add(kEntity);
		return 1;
	}
	
	
	public static ArrayList<KEntity> lineListToEnityList(ArrayList<String> list){
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		if(list.size()==1){
			entityList.add(lineToEntity(list.get(0)));
		}
		if(list.size()>1){
			for(int i=0;i<list.size();i++){
				KEntity kEntity=lineToEntity(list.get(i));
				for(i=i+1;i<list.size();i++){
					KEntity nextEntity=lineToEntity(list.get(i));
					if(kEntity.getTime().substring(0, 16).equals(nextEntity.getTime().substring(0, 16))){
						kEntity.setClose(nextEntity.getClose());
						if(kEntity.getHigh()<nextEntity.getHigh()){
							kEntity.setHigh(nextEntity.getHigh());
						}
						if(kEntity.getLow()>nextEntity.getLow()){
							kEntity.setLow(nextEntity.getLow());
						}
						if(i==list.size()-1){
							entityList.add(kEntity);
						}
						
					}else{
						entityList.add(kEntity);
						kEntity=nextEntity;
					}
				}
			}

		}
		
		return entityList;
	}
	
	private static KEntity lineToEntity(String line){
		KEntity kEntity=new KEntity();
    	String[] lineInfos=line.split(",");
		kEntity.setTP(lineInfos[0]);
		kEntity.setName(lineInfos[1]);
		kEntity.setTime(lineInfos[2].substring(0, 19));
		kEntity.setMin(1);//1分钟K线
		kEntity.setClose(Double.parseDouble(lineInfos[3]));
		kEntity.setOpen(Double.parseDouble(lineInfos[3]));
		kEntity.setHigh(Double.parseDouble(lineInfos[3]));
		kEntity.setLow(Double.parseDouble(lineInfos[3]));
		return kEntity;
	}
	

	
	
	public static String getTpFromName(String name){
    	//RM1701,MA1701,ZC1701,CF1701,FG1701,TA1701,SR1701,OI1701,rb1701,ru1701,bu1612,ag1612,au1612,ni1701,hc1701,m1701,c1701,p1701,i1701,l1701,y1701,j1701,pp1701,jm1701,jd1701,cs1701,a1701

		String tp="dc";
    	if("rb,ru,bu,ag,au,ni,hc,al,cu,sn,zn".indexOf(name.substring(0, 2))!=-1){
    		tp="sc";
    	}else if("RM1701,MA1701,ZC1701,CF1701,FG1701,TA1701,SR1701,OI1701".indexOf(name.substring(0, 2))!=-1){
    		tp="zc";
    	}
    	return tp;
	}
	

	
	public static void main(String[] args){
		
//		AlertUtil.sleepIfNotDayWorkTime();
		
//		LOGGER.info(AlertUtil.抓新浪取历史数据("BU0,MA0,RU0,TA0,HC0,RB0,I0,FG0,JM0,J0,ZC0,RM0,M0,A0,P0,Y0,OI0,CF0,SR0,C0,CS0,JD0,PP0,L0,V0,EG0,AP0,SF0,SM0,SP0", 0).replaceAll("var hq_str_", ""));
//		DataHandle2 d = new DataHandle2();
//		d.loadTodayKEntityList();
	}




	
	


}
