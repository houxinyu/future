package com.example.future.macd;

import java.util.ArrayList;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.future.tools.MusicPlay;
import com.example.future.tools.配置文件;

public class MyMA {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyMA.class);
	public static HashSet<String> maAlertSet=new HashSet<String>();
	
	/**
	 * calculate MA values
	 * @return
	 */
	public static final void setMA1(ArrayList<KEntity> list,int period) {
		// MA = （C1+C2+C3+C4+C5+.。。.+Cn）/n
		if(list.size()<10){
			for(int i=0;i<list.size();i++){
				list.get(i).setMAn(list.get(i).getClose(), period);//只有几根K线的时候均线计算没有意义，所以这里也不再计算
			}
		}else{
			for(int i=list.size()-1;i>=list.size()-10;i--){
				double total=0.0;
				for(int j=i;j>i-period;j--){
					total+=list.get(j).getClose();
				}
				double maPeriod=total/period;
				list.get(i).setMAn(maPeriod, period);
			}
		}

	}
	
	public static final void setMA(ArrayList<KEntity> list,int period) {
		// MA = （C1+C2+C3+C4+C5+.。。.+Cn）/n
		if(list.size()<10){
			for(int i=0;i<list.size();i++){
				list.get(i).setMAn(list.get(i).getClose(), period);//只有几根K线的时候均线计算没有意义，所以这里也不再计算
			}
		}else{
			for(int i=list.size()-1;i>=list.size()-10;i--){
				//加入该判断避免重新计算MA的值，只计算最后一个KEntity的值
				if((list.get(i).getMAn(period)+"").equals("0.0")||(i==list.size()-1)){
					double total=0.0;
					for(int j=i;j>i-period;j--){
						total+=list.get(j).getClose();
					}
					double maPeriod=total/period;
					list.get(i).setMAn(maPeriod, period);
				}

			}
		}

	}
	

	
//	public static void 实时计算MA() {
//		// TODO Auto-generated method stub
//		if(配置文件.获取配置项("maalert")!=null&&配置文件.获取配置项("maalert").equals("true")){
//			System.out.println("MA预警程序启动，开始计算MA...");
//			NewThread runThread=new NewThread(NewThread.ThreadType.MA预警线程);
//			new Thread(runThread).start();
//		}
//	}
	
//	public static void MA计算预警(){
//		String[] aList=配置文件.获取配置项("alertList").split(",");
//		for(int i=0;i<aList.length;i++){
//			ArrayList<KEntity> list=AlertUtil.getListFromMap(aList[i]+3600);
////			System.out.println("MyMA.MA计算预警():"+list.get(0).getName());
//			setMA(list,5);
//			setMA(list,10);
//			MyMACD.setMACD(list);
//			MA单品种预警(list);
//		}
//	}
	
//	public static void MA计算单个品种预警(ArrayList<KEntity> list){
//		MyMA.setMA(list,5);
//		MyMA.setMA(list,10);
//		MyMACD.setMACD(list);
//		MyMA.MA单品种预警(list);
//	}
	
	public static void MA计算预警(String codeName){
//			if(配置文件.获取配置项("maalert")!=null&&配置文件.获取配置项("maalert").equals("true")){
		if(配置文件.获取真假("maalert")){
				ArrayList<KEntity> dayList=AlertUtil.getListFromMap(codeName+3600);
				if(dayList!=null){
					MyMA.MA单品种预警(dayList);
				}else{
					LOGGER.info("1110:"+codeName+" 3600数据为空");
				}
			}
	}
	
	public static void MA单品种预警(ArrayList<KEntity> list){
//		MyMA.setMA(list,5);
//		MyMA.setMA(list,10);
//		MyMACD.setMACD(list);
		
		final double 预警比率=0.5;
		KEntity fourEntity=list.get(list.size()-4);
		KEntity ppEntity=list.get(list.size()-3);
		KEntity preEntity=list.get(list.size()-2);
		KEntity nowEntity=list.get(list.size()-1);
//		boolean 趋势条件=false;
		boolean isUP=false;
		boolean isDOWN=false;
		for(int k=list.size()-2;k>list.size()-5;k--){
			KEntity nEntity=list.get(k);
			KEntity pEntity=list.get(k-1);
			if(nEntity.getMA5()>pEntity.getMA5()&&nEntity.getDif()>=pEntity.getDif()){
//				趋势条件=true;
				isUP=true;
				break;
			}
			if(nEntity.getMA5()<pEntity.getMA5()&&nEntity.getDif()<=pEntity.getDif()){
//				趋势条件=true;
				isDOWN=true;
				break;
			}
		}
		
		if(isUP){
			if(Math.abs((nowEntity.getClose()-nowEntity.getMA5())/nowEntity.getMA5()*100)<=预警比率||nowEntity.getClose()<=nowEntity.getMA5()){
				均线预警播放打印(nowEntity.getName(),5,list,true);
			}
			if(Math.abs((nowEntity.getClose()-nowEntity.getMA10())/nowEntity.getMA10()*100)<=预警比率||nowEntity.getClose()<=nowEntity.getMA10()){
				均线预警播放打印(nowEntity.getName(),10,list,true);
			}
		}
		if(isDOWN){
			if(Math.abs((nowEntity.getClose()-nowEntity.getMA5())/nowEntity.getMA5()*100)<=预警比率||nowEntity.getClose()>=nowEntity.getMA5()){
				均线预警播放打印(nowEntity.getName(),5,list,false);
			}
			if(Math.abs((nowEntity.getClose()-nowEntity.getMA10())/nowEntity.getMA10()*100)<=预警比率||nowEntity.getClose()>=nowEntity.getMA10()){
				均线预警播放打印(nowEntity.getName(),10,list,false);
			}
		}
		
		
//		boolean 条件11=ppEntity.getMA5()<=preEntity.getMA5()&&ppEntity.getDif()<=preEntity.getDif();
//		boolean 条件12=ppEntity.getMA5()<preEntity.getMA5()&&ppEntity.getDif()>preEntity.getDif()||ppEntity.getMA5()>preEntity.getMA5()&&ppEntity.getDif()<preEntity.getDif();
//		boolean 条件21=ppEntity.getMA5()>=preEntity.getMA5()&&ppEntity.getDif()>=preEntity.getDif();
//		boolean 条件22=ppEntity.getMA5()>preEntity.getMA5()&&ppEntity.getDif()<preEntity.getDif()||ppEntity.getMA5()<preEntity.getMA5()&&ppEntity.getDif()>preEntity.getDif();
//		double 距五日比率=Math.abs((nowEntity.getClose()-nowEntity.getMA5())/nowEntity.getMA5()*100);
//		double 距十日比率=Math.abs((nowEntity.getClose()-nowEntity.getMA10())/nowEntity.getMA10()*100);
		
		//支撑预警条件①：“A、前一天DIF向上，前一天五日向上；B、非通道初期；C、今天距五日比率小于0.3”-------------------------------这种是最正常的支撑
		//支撑预警条件②：“A、前一天DIF向上，前一天五日向上；B、通道初期张口；C、今天距十日比率小于0.3”；----------------------------这种初期张口的支撑
		//支撑预警条件③：“A、前一天DIF刚转向向下；B、前一天五日向上；C、前一天十日向上；D、今天距十日比率小于0.3”---------------------这种是趋势反转假信号，面临调整
		//支撑预警条件④：“A、前一天DIF向上，前一天五日向上；B、最近几天从最高点下来经历了半小时或一小时顶结构；C、今天距十日比率小于0.3”---这种是趋势反转真信号，会面临较大调整
//		boolean 开盘条件=开盘条件1||开盘条件2;
		//支撑时均线方向要保证
		
		
//		if(isUP){
//			if(条件11&&(距五日比率<=预警比率||nowEntity.getClose()<nowEntity.getMA5())){
//				均线预警播放打印(nowEntity.getName(),5,list,true);
//			}
//			if(条件11&&(距十日比率<=预警比率||nowEntity.getClose()<nowEntity.getMA10())&&nowEntity.getMA10()>preEntity.getMA10()){
//				均线预警播放打印(nowEntity.getName(),10,list,true);
//			}
//			if(条件12&&距十日比率<=预警比率&&nowEntity.getMA10()>preEntity.getMA10()){
//				均线预警播放打印(nowEntity.getName(),10,list,true);
//			}
//			
//		}
//
//		
//		if(isDOWN){
//			if(条件21&&(距五日比率<=预警比率||nowEntity.getClose()>nowEntity.getMA5())){
//				均线预警播放打印(nowEntity.getName(),5,list,false);
//			}
//			if(条件21&&(距十日比率<=预警比率||nowEntity.getClose()>nowEntity.getMA10())&&nowEntity.getMA10()<preEntity.getMA10()){
//				均线预警播放打印(nowEntity.getName(),10,list,false);
//			}
//			if(条件22&&距十日比率<=预警比率&&nowEntity.getMA10()<preEntity.getMA10()){
//				均线预警播放打印(nowEntity.getName(),10,list,false);
//			}
//		}

		
	}

	
	
	private static void 均线预警播放打印(String codeName,int period,ArrayList<KEntity> list,boolean isUP){
		String newCodeName=MusicPlay.delDigtal(codeName);
		String time=list.get(list.size()-1).getTime();
		if(!maAlertSet.contains(codeName+period+time)){
			if(period==5){
				MusicPlay.playTwo(newCodeName, "fivenear");
			}else if(period==10){
				MusicPlay.playTwo(newCodeName, "teennear");
			}else{
				MusicPlay.play(newCodeName);
			}
//			日志工具.fileTrack.info("-------------------------------------预警:"+codeName+" "+period+"日线撑压");
			LOGGER.info("                                        "+codeName+" "+period+" "+list.get(list.size()-1).getClose()+(isUP?" 支撑":" 压力"));
			maAlertSet.add(codeName+period+time);
//			AlertUtil.打印列表(list, "MyMA.均线预警播放打印()");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataHandle.加载历史K线数据();
//		long start=时间工具.获得毫秒时间();
//		ArrayList<KEntity> list=AlertUtil.getListFromMap("RM701"+3600);
//		setMA(list,5);
//		MyMACD.打印列表(list,"MyMACD.main");
//		System.out.println("********************");
//		setMA(list,10);
//		MyMACD.打印列表(list,"MyMACD.main");
//		long end=时间工具.获得毫秒时间();
//		System.out.println("MA计算时间："+(end-start));
		
//		MA计算预警();
	}

}
