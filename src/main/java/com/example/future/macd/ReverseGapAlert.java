package com.example.future.macd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.future.tools.MusicPlay;
import com.example.future.tools.时间工具;
import com.example.future.tools.配置文件;


public class ReverseGapAlert {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyMACD.class);
	private static String alertList="RM0,MA0,ZC0,CF0,FG0,TA0,SR0,OI0,RB0,RU0,BU0,HC0,M0,C0,P0,I0,L0,Y0,J0,PP0,JM0,JD0,CS0,A0";
//	private static String alertList="RM0,MA0,ZC0,CF0,FG0,TA0,SR0,OI0,RB0,RU0,BU0,AG0,AU0,CU0,ZN0,AL0,SN0,NI0,HC0,M0,C0,P0,I0,L0,Y0,J0,PP0,JM0,JD0,CS0,A0";
//	public static String alertList="RM1701,MA1701,ZC1701,CF1701,FG1701,TA1701,SR1701,OI1701,rb1701,ru1701,bu1612,ag1612,au1612,ni1701,hc1701,m1701,c1701,p1701,i1701,l1701,y1701,j1701,pp1701,jm1701,jd1701,cs1701,a1701";
	private static HashMap<String,KEntity> entityMap=new HashMap<String,KEntity>();
	private static ArrayList<KEntity> entityList=new ArrayList<KEntity>();
	private static ArrayList<KEntity> preEntityList=new ArrayList<KEntity>();
	private static HashSet<String> alertSet=new HashSet<String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		检查反向跳空预警();
//		取今天K线数据();
//		MyMACD.打印列表(entityList, "ReverseGapAlert.main");
		
//		抓取前一天K线();
//		AlertUtil.打印列表(preEntityList, "ReverseGapAlert.main");

	}
	
	public static ArrayList<KEntity> 抓取前一天K线(){
		String[] codes=alertList.split(",");
//		ArrayList<KEntity> list=new ArrayList<KEntity>();
		if(entityMap.size()>0){
			return preEntityList;
		}
		for(int i=0;i<codes.length;i++){
			String[] infos=AlertUtil.抓新浪取历史数据(codes[i],3600).replace("\"", "").replace("[",
			"").replace("]", "").split(";");
			String[] oneinfos=infos[infos.length-1].split(",");
			KEntity entity=new KEntity();
			entity.setName(codes[i]);
			String tp=DataHandle.getTpFromName(codes[i]);
			entity.setTP(tp);
			entity.setTime(oneinfos[0]);
			entity.setMin(3600);
			entity.setOpen(Double.parseDouble(oneinfos[1]));
			entity.setHigh(Double.parseDouble(oneinfos[2]));
			entity.setLow(Double.parseDouble(oneinfos[3]));
			entity.setClose(Double.parseDouble(oneinfos[4]));
			preEntityList.add(entity);
			entityMap.put(entity.getName(), entity);
//			System.out.println("昨天："+entity);
			
		}
		return preEntityList;
	}
	
	
	public static ArrayList<KEntity> 取今天K线数据(){
		//http://hq.sinajs.cn/list=M1309
		if(entityList.size()>0){
			return entityList;
		}
//		ArrayList<KEntity> list=new ArrayList<KEntity>();
		String[] infos=AlertUtil.抓新浪取历史数据(alertList,0).split(";");
		for(int i=0;i<infos.length-1;i++){
//			System.out.println(i+":"+infos[i].replace("var hq_str_", "").replace("=\"", ",").replace("\"", ""));
			String[] oneinfos=infos[i].replace("var hq_str_", "").replace("=\"", ",").replace("\"", "").split(",");
			KEntity entity=new KEntity();
			entity.setName(oneinfos[0].trim());
			String tp=DataHandle.getTpFromName(oneinfos[0].trim());
			entity.setTP(tp);
			int hour=时间工具.获得现在小时();
			if(hour>=21){
				entity.setTime(时间工具.取得后一交易日期(infos[18])+" 00:00:00");
			}else{
				entity.setTime(oneinfos[18]);
			}
			entity.setMin(3600);
			entity.setOpen(Double.parseDouble(oneinfos[3]));
			entity.setHigh(Double.parseDouble(oneinfos[4]));
			entity.setLow(Double.parseDouble(oneinfos[5]));
			entity.setClose(Double.parseDouble(oneinfos[7]));
			entityList.add(entity);
//			System.out.println("今天："+entity);
		}
		return entityList;
	}
	
	private static void 清理数据(){
		entityMap.clear();
		entityList.clear();
		alertSet.clear();
		
	}
	
	public static void 检查反向跳空预警(){
		int hour=时间工具.获得现在小时();
		if(hour==20||hour==8){
			清理数据();
		}
		boolean 时间条件=(hour==21||hour==9);

		if(配置文件.获取配置项("reversegap").equals("true")&&时间条件){
			抓取前一天K线();
			ArrayList<KEntity> list=取今天K线数据();
			for(int i=0;i<list.size();i++){
				KEntity todayEntity=list.get(i);
				KEntity yesEntity=entityMap.get(todayEntity.getName());
//				if(todayEntity.getName().equals("ZC0")){
//					System.out.println(yesEntity);
//					System.out.println(todayEntity);
//				}
				boolean 条件1=(hour==9)&&("C0CS0PP0L0JD0".indexOf(todayEntity.getName())!=-1);
				boolean 条件2=(hour==21)&&("C0CS0PP0L0JD0".indexOf(todayEntity.getName())==-1);
				boolean 条件3=条件1 || 条件2;
//				条件3=true;
				if(!alertSet.contains(todayEntity.getName())&&条件3&&!yesEntity.getTime().equals(todayEntity.getTime())&&yesEntity.getOpen()>yesEntity.getClose()&&todayEntity.getOpen()>=yesEntity.getOpen()){
					MusicPlay.playTwo(MusicPlay.delDigtal(todayEntity.getName()), "reverse");
					LOGGER.info("                                                       "+todayEntity.getName()+" "+"向上跳空");
				}
				if(!alertSet.contains(todayEntity.getName())&&条件3&&!yesEntity.getTime().equals(todayEntity.getTime())&&yesEntity.getOpen()<yesEntity.getClose()&&todayEntity.getOpen()<=yesEntity.getOpen()){
					MusicPlay.playTwo(MusicPlay.delDigtal(todayEntity.getName()), "reverse");
					LOGGER.info("                                                       "+todayEntity.getName()+" "+"向下跳空");
				}
				alertSet.add(todayEntity.getName());//无论是否预警，当天都只检查一次
					
			}
		}
		时间工具.休眠秒数(3);
	}
	
	public static void 实时检查反向跳空(){
		if(配置文件.获取配置项("reversegap").equals("true")){
			System.out.println("反向预警程序启动，开始检查开盘...");
			NewThread runThread=new NewThread(NewThread.ThreadType.反向跳空线程);
			new Thread(runThread).start();
		}

	}
	

}
