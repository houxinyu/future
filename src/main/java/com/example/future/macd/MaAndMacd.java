package com.example.future.macd;

import java.util.ArrayList;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.future.tools.MusicPlay;
import com.example.future.tools.时间工具;
import com.example.future.tools.配置文件;

//结合MA撑压以及MACD结构预警，基本的情况可以通过MyMA和MyMACD分析得到，但预警之前需要通过该类的“获取结构类型”进行分析后再决定
public class MaAndMacd {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppMain.class);
	private static HashSet<String> maAlertSet=new HashSet<String>();
	private static HashSet<String> macdAlertSet=new HashSet<String>();
	private static HashSet<String> jieMaAlertSet=new HashSet<String>();
	public static HashSet<String> openAlertSet=new HashSet<String>();
	private static HashSet<String> rValueAlertSet=new HashSet<String>();
	private static HashSet<String> reverseAlertSet=new HashSet<String>();
	private final static String jiegou="";
	//实现功能如下：
	
	//近端系统:
	//1、结构判断
	//2、预结构判断（钝化）
	//3、初期五日十日角度判断张口
	//4、相关度高品种是否有反向大结构
	//5、回踩均线百分比以及回踩时均线方向
	
	//结构系统:
	//6、是否“小成+大预”
	//7、是否大结构回踩
	//8、是否顺势结构
	//9、三重结构判断
	
	//该预警包括了单个品种的MA和MACD预警，都是加入了结构分析的
	public static void 单品种预警(String codeName){
		//1.MA预警
		if(配置文件.获取真假("newMaAlert")){
			MA单品种预警(codeName);
		}
		//2.MACD预警
		if(配置文件.获取真假("newMacdAlert")){
			MACD单品种预警(codeName);
		}
		//3.结构回踩预警
		if(配置文件.获取真假("jieMaAlert")){
			单品种结构回踩预警(codeName);
		}
		
		//4.R值预警
		if(配置文件.获取真假("rValueAlert")){
			单品种R值预警(codeName);
		}
		
		//5.反向跳空
		if(配置文件.获取真假("reversegap")){
			反向跳空预警(codeName);
		}

	}
	
	public static void 盘后分析(){
		//在DataHandle.计算撑压位置()方法中调用MaAndMacd.获取结构类型(list30,false)和MaAndMacd.获取结构类型(list60,false),可以得到结果
		//等测试完实时预警之后，发现“获取结构类型()”方法没有问题之后，再使用
	}

	private static void MA单品种预警(String codeName) {
		// TODO Auto-generated method stub
		//1、判断是否回踩5日、10日线
		//2、判断最近一个30、60、3600的MACD交叉是顶背离还是底背离
		//3、如果第二步判断是普通交叉，则回踩五日线预警，如果是逆势背离，则回踩十日线预警
		//4、判断回踩的时候是否已经双转，如果已经双转则不进行预警
		
		ArrayList<KEntity> list3600=AlertUtil.getListFromMap(codeName+3600);
//		MyMA.setMA(list3600,5);
//		MyMA.setMA(list3600,10);
		ArrayList<KEntity> list30=AlertUtil.getListFromMap(codeName+30);
		ArrayList<KEntity> list60=AlertUtil.getListFromMap(codeName+60);
//		MyMACD.setMACD(list30);
//		MyMACD.setMACD(list60);
//		MyMACD.setMACD(list3600);
		if(list3600==null||list3600.size()==0){
			LOGGER.info("MaAndMacd.MA单品种预警():"+codeName+" 3600数据为空");
			return;
		}

		//1.判断是否回踩均线，并获得回踩类型。如果没有回踩均线，则不用后续分析
		int maType=回踩均线(list3600,true,0.5);
		if(maType==0){
			return;
		}
		
		Structure struct=获取结构类型(list30,true);
		if(struct.getDevType()==0){//30分钟时间框架没有结构或预结构时，分析60分钟
			struct=获取结构类型(list60,true);
		}
		if(struct.getDevType()==0){//30和60分钟都没结构，则查看15分钟和5分钟是否有三重结构，这也是比较危险的情况
			ArrayList<KEntity> list15=AlertUtil.getListFromMap(codeName+15);
			struct=获取结构类型(list15,true);
		}
		if(struct.getDevType()==0){//30和60分钟都没结构，则查看15分钟和5分钟是否有三重结构，这也是比较危险的情况
			ArrayList<KEntity> list5=AlertUtil.getListFromMap(codeName+5);
			struct=获取结构类型(list5,true);
		}
//		int cdType=struct.getDevType();//cdType为0,-1,1,-2,2
		//=========================打印====================================
//		ArrayList<KEntity> list1=AlertUtil.getListFromMap(codeName+1);
//		if(list1.get(list1.size()-1).getTime().indexOf(":59:")!=-1){
//			System.out.println("MA单品种预警:"+struct);
//		}
		//=========================打印====================================
		
		boolean 条件5=!maAlertSet.contains(list3600.get(0).getName()+"5"+list3600.get(0).getTime());
		boolean 条件10=!maAlertSet.contains(list3600.get(0).getName()+"10"+list3600.get(0).getTime());
		
//		//==================这段代码与开盘预警有关=====================		
//		int trend=获得趋势(list3600,true);
//		boolean 开盘条件1=trend>0&&openAlertSet.contains(list3600.get(0).getName())&&list3600.get(list3600.size()-1).getClose()<(list3600.get(list3600.size()-2).getClose()-AlertUtil.获得商品R值(list3600.get(0).getName()));
//		boolean 开盘条件2=trend<0&&openAlertSet.contains(list3600.get(0).getName())&&list3600.get(list3600.size()-1).getClose()>(list3600.get(list3600.size()-2).getClose()+AlertUtil.获得商品R值(list3600.get(0).getName()));
//		if(开盘条件1){
//			均线预警播放打印(codeName,5,list3600,struct,"支撑1R");
//			return;
//		}
//		if(开盘条件2){
//			均线预警播放打印(codeName,5,list3600,struct,"压力1R");
//			return;
//		}
//		
//		if(条件5&&(maType==5||maType==-5)&&!openAlertSet.contains(codeName)||条件10&&(maType==10||maType==-10||maType==101||maType==-101)&&!openAlertSet.contains(codeName)){
//			String atime=时间工具.取的格式化时间("HHmm");
//			boolean 开盘条件=AlertUtil.isDayGood(codeName)&&(atime.equals("0900")||atime.equals("0901"))||!AlertUtil.isDayGood(codeName)&&(atime.equals("2100")||atime.equals("2101"));
//			if(开盘条件&&!openAlertSet.contains(codeName)&&Math.abs((list3600.get(list3600.size()-1).getClose()-list3600.get(list3600.size()-2).getClose()))<AlertUtil.获得商品R值(codeName)){
//				//LOGGER.info(codeName+" "+list3600.get(list3600.size()-1).getClose()+"开盘预警");
//				openAlertSet.add(codeName);
//			}
//		}
////		==================这段代码与开盘预警有关=====================
		
		
		//开盘预警的只有在第一次进入这个判断，如果已经知道是开盘预警，则不再根据5日10日均线了，而是根据与昨天收盘价1R位置
		if(条件5&&(maType==5||maType==-5)){
			if(maType>0){
				均线预警播放打印(codeName,5,list3600,struct,"支撑");
			}else{
				均线预警播放打印(codeName,5,list3600,struct,"压力");
			}
		}
		if(条件10&&(maType==10||maType==-10||maType==101||maType==-101)){
			if(maType>0){
				均线预警播放打印(codeName,10,list3600,struct,"支撑");
			}else{
				均线预警播放打印(codeName,10,list3600,struct,"压力");
			}
		}
		
	}

	
	
	private static void MACD单品种预警(String codeName) {
		String[] alertMins=配置文件.获取配置项("alertMins").split(",");
		for(int j=0;j<alertMins.length;j++){
			ArrayList<KEntity> minlist=AlertUtil.getListFromMap(codeName+alertMins[j]);
			if(minlist!=null){
				MACD单时间框架预警(minlist);
			}
		}
	}
	
	private static void 单品种R值预警(String codeName){
		int hour=时间工具.获得现在小时();
		if(hour>=0&&hour<=8||hour>=11&&hour<=20){//只在早盘出现才进行预警,上午11点之后的不进行预警
			return;
		}
		ArrayList<KEntity> list3600=AlertUtil.getListFromMap(codeName+3600);
		int trend=获得趋势(list3600,true);
		int size=list3600.size();
		KEntity yesEntity=list3600.get(size-2);
		KEntity todayEntity=list3600.get(size-1);
		if(AlertUtil.获得商品R值(todayEntity.getName())==null){
			return;
		}
		boolean 向上1R=(todayEntity.getClose()-yesEntity.getClose())>AlertUtil.获得商品R值(todayEntity.getName());
		boolean 向上2R=(todayEntity.getClose()-yesEntity.getClose())>2*AlertUtil.获得商品R值(todayEntity.getName());;
		boolean 向下1R=(yesEntity.getClose()-todayEntity.getClose())>AlertUtil.获得商品R值(todayEntity.getName());
		boolean 向下2R=(yesEntity.getClose()-todayEntity.getClose())>2*AlertUtil.获得商品R值(todayEntity.getName());
		if(向上1R){
//			MusicPlay.playTwo(MusicPlay.delDigtal(todayEntity.getName()), MusicPlay.minString(min));
			String key=todayEntity.getName()+todayEntity.getMin()+todayEntity.getTime()+"1R";
			if(!rValueAlertSet.contains(key)){
				LOGGER.info(todayEntity.getName()+" "+todayEntity.getMin()+" "+todayEntity.getClose()+" 上涨1R");
				rValueAlertSet.add(key);
			}
		}
//		if(向上2R){
//			String key=todayEntity.getName()+todayEntity.getMin()+todayEntity.getTime()+"2R";
//			if(!rValueAlertSet.contains(key)){
//				LOGGER.info(todayEntity.getName()+" "+todayEntity.getMin()+" "+todayEntity.getClose()+" 上涨2R");
//				rValueAlertSet.add(key);
//			}
//		}
		if(向下1R){
			String key=todayEntity.getName()+todayEntity.getMin()+todayEntity.getTime()+"-1R";
			if(!rValueAlertSet.contains(key)){
				LOGGER.info(todayEntity.getName()+" "+todayEntity.getMin()+" "+todayEntity.getClose()+" 下跌1R");
				rValueAlertSet.add(key);
			}
		}
//		if(向下2R){
//			String key=todayEntity.getName()+todayEntity.getMin()+todayEntity.getTime()+"-2R";
//			if(!rValueAlertSet.contains(key)){
//				LOGGER.info(todayEntity.getName()+" "+todayEntity.getMin()+" "+todayEntity.getClose()+" 下跌2R");
//				rValueAlertSet.add(key);
//			}
//		}
		
	}
	
	private static void 反向跳空预警(String codeName){
		int hour=时间工具.获得现在小时();
		int min=Integer.valueOf(时间工具.取的格式化时间("mm"));
		boolean 条件1=(hour==9&&min<3);
		boolean 条件2=(hour==21&&min<3);
		if(!条件1&&!条件2){
			return;
		}
		ArrayList<KEntity> list3600=AlertUtil.getListFromMap(codeName+3600);
		KEntity yesEntity=list3600.get(list3600.size()-2);
		KEntity todayEntity=list3600.get(list3600.size()-1);
		boolean 向下跳空=yesEntity.getClose()>yesEntity.getOpen()&&todayEntity.getOpen()<=yesEntity.getOpen();
		boolean 向上跳空=yesEntity.getClose()<yesEntity.getOpen()&&todayEntity.getOpen()>=yesEntity.getOpen();
		String key=yesEntity.getName();
		if(!reverseAlertSet.contains(key)&&向下跳空){
			LOGGER.info(todayEntity.getName()+" "+todayEntity.getMin()+" "+todayEntity.getClose()+" 向下跳空");
			reverseAlertSet.add(key);
			
		}
		if(!reverseAlertSet.contains(key)&&向上跳空){
			LOGGER.info(todayEntity.getName()+" "+todayEntity.getMin()+" "+todayEntity.getClose()+" 向上跳空");
			reverseAlertSet.add(key);
		}
		
		
		
	}
	private static void 单品种结构回踩预警(String codeName){//处于结构（或小时通道）之中，回踩均线
		ArrayList<KEntity> thirtyList=AlertUtil.getListFromMap(codeName+30);
		ArrayList<KEntity> sixtyList=AlertUtil.getListFromMap(codeName+60);
		结构回踩预警(thirtyList);
		结构回踩预警(sixtyList);
	}
	
	private static void 结构回踩预警(ArrayList<KEntity> list){
		//1.处于大结构，处于顺势小时通道
		//2.只对部分配置品种；或处于结构初期，MACD刚交叉5根柱子之内，刚交叉的那根不报，因为有结构预警
		//3.该方法如果日线处于比较好的通道中期近端，操作方向与日线不一致时要谨慎！
		//因为在“MA单品种预警”方法中，30和60分钟列表的MACD已经计算过，这里就不再计算
//		ArrayList<KEntity> thirtyList=AlertUtil.getListFromMap(codeName+30);
		final double hRate=0.2;
//		ArrayList<KEntity> sixtyList=AlertUtil.getListFromMap(codeName+60);
		KEntity hEntity=list.get(list.size()-1);
//		MyMA.setMA(list, 5);
//		MyMA.setMA(list, 10);
//		if(!配置文件.获取真假("newMaAlert")&&!配置文件.获取真假("newMacdAlert")){//前面已经计算过，这里就不进行计算了
//			MyMACD.setMACD(list);
//		}
		Structure struct=获取结构类型(list,true);
//		int trend=获得趋势(sixtyList,true);
		//changeIndex算法如下
//		同向条件1:=DIFF>REF(DIFF,1) AND MA5>REF(MA5,1) AND (REF(DIFF,1)>=REF(DIFF,2) AND REF(MA5,1)<=REF(MA5,2) OR REF(DIFF,1)<=REF(DIFF,2) AND REF(MA5,1)>=REF(MA5,2) OR REF(DIFF,1)<REF(DIFF,2) AND REF(MA5,1)<REF(MA5,2));
//		同向条件2:=DIFF<REF(DIFF,1) AND MA5<REF(MA5,1) AND (REF(DIFF,1)>=REF(DIFF,2) AND REF(MA5,1)<=REF(MA5,2) OR REF(DIFF,1)<=REF(DIFF,2) AND REF(MA5,1)>=REF(MA5,2) OR REF(DIFF,1)>REF(DIFF,2) AND REF(MA5,1)>REF(MA5,2));

		//目前只对处于结构中的进行预警，可以改进的地方是，寻找到DIF和MA5变成同向的那一天作为changeIndex，在changeIndex的3天之内，或crossIndex的5天之内回踩都可以
		boolean 背离交叉=(struct.getCrossIndex()!=list.size()-1)&&(list.size()-1-struct.getCrossIndex())<=5;//交叉的5根K线之内回踩，已经交叉
		
		int sIndex=-1;
		if(hEntity.getMin()==60){
			sIndex=计算最近一次同向(list);
		}
		if(sIndex!=-1&&hEntity.getMin()==60){
//			System.out.println("MaAndMacd.单品种结构回踩:"+sixtyList.get(struct.getCrossIndex()));
//			System.out.println((list.size()-1-sIndex)+"MaAndMacd.单品种结构回踩:"+list.get(sIndex));
		}

		boolean 小时双转=(hEntity.getMin()==60)&&(sIndex!=list.size()-1)&&(sIndex!=-1)&&(list.size()-1-sIndex)<=4;//普通交叉只做1小时的
		boolean 初期交叉=背离交叉||小时双转;
		
		boolean 配置品种=false;
		if(初期交叉||配置品种){
			if(struct.getDevType()==1&&小时回踩均线(list,true,hRate)==10||struct.getDevType()==-1&&小时回踩均线(list,true,hRate)==-10){
				//背离交叉
				//(codeName+period+time)
				String key=hEntity.getName()+hEntity.getMin()+hEntity.getTime();
				if(!jieMaAlertSet.contains(key)){
					MusicPlay.playAlertAudio();
					if(hEntity.getMin()==60){
						LOGGER.info(hEntity.getName()+" "+hEntity.getMin()+" "+hEntity.getClose()+" "+(struct.getDevType()==1?"底":"顶")+jiegou+"回踩");
					}else{
						LOGGER.info(hEntity.getName()+" "+hEntity.getMin()+" "+hEntity.getClose()+" "+(struct.getDevType()==1?"底":"顶")+jiegou+"回踩");
					}
					
					jieMaAlertSet.add(key);
				}
			}else if(hEntity.getMin()==60&&小时双转&&(小时回踩均线(list,true,hRate)==-5||小时回踩均线(list,true,hRate)==5)){
				//均线未交叉，回踩五日线
				String key=hEntity.getName()+hEntity.getMin()+hEntity.getTime();
				if(!jieMaAlertSet.contains(key)){
					MusicPlay.playAlertAudio();
					LOGGER.info(hEntity.getName()+" "+hEntity.getMin()+" "+hEntity.getClose()+" "+(小时回踩均线(list,true,hRate)==5?"多":"空")+"回踩");
					jieMaAlertSet.add(key);
				}
			}else if(hEntity.getMin()==60&&背离交叉&&(struct.getCrossType()==1&&小时回踩均线(list,true,hRate)==10||struct.getCrossType()==-1&&小时回踩均线(list,true,hRate)==-10)){
				//普通交叉，回踩10日线
				String key=hEntity.getName()+hEntity.getMin()+hEntity.getTime();
				if(!jieMaAlertSet.contains(key)){
					MusicPlay.playAlertAudio();
					LOGGER.info(hEntity.getName()+" "+hEntity.getMin()+" "+hEntity.getClose()+" "+(struct.getCrossType()==1?"多":"空")+"回踩");
					jieMaAlertSet.add(key);
				}
			}
		}
	}
	//1.回踩时要观察是否操作方向与日线方向不一致，且日线处于近端
	//2.有1R收益后不亏
	//3.修正时趋势横向走，或与修正方向不一致时，这种修正回踩不做。修正是指在红绿柱没有改变方向的情况下继续重新变长，不是交叉
	//4.最近一次同向最好不要做“修正”，只做交叉，然后按照建仓之后“1R止损+3R止盈”模式
	//5.如果是很好的结构或是顺势交叉，则按照波段持有，是普通交叉则按照远端止盈或3R止盈模式
	//6.
	private static int 计算最近一次同向(ArrayList<KEntity> list){
//		同向条件1:=DIFF>REF(DIFF,1) AND MA5>REF(MA5,1) AND (REF(DIFF,1)>=REF(DIFF,2) AND REF(MA5,1)<=REF(MA5,2) OR REF(DIFF,1)<=REF(DIFF,2) AND REF(MA5,1)>=REF(MA5,2) OR REF(DIFF,1)<REF(DIFF,2) AND REF(MA5,1)<REF(MA5,2));
//		同向条件2:=DIFF<REF(DIFF,1) AND MA5<REF(MA5,1) AND (REF(DIFF,1)>=REF(DIFF,2) AND REF(MA5,1)<=REF(MA5,2) OR REF(DIFF,1)<=REF(DIFF,2) AND REF(MA5,1)>=REF(MA5,2) OR REF(DIFF,1)>REF(DIFF,2) AND REF(MA5,1)>REF(MA5,2));

		int sIndex=-1;
		int cIndex=-1;
//		System.out.println(list.size()+":");s
		for(int i=list.size()-1;i>3;i--){
			KEntity nE=list.get(i);
			KEntity pE=list.get(i-1);
			KEntity ppE=list.get(i-2);
			double DIF=nE.getDif();
			double REF_DIF_1=pE.getDif();
			double REF_DIF_2=ppE.getDif();
			
			double MA5=nE.getMA5();
			double REF_MA5_1=pE.getMA5();
			double REF_MA5_2=ppE.getMA5();
			boolean 同向条件1=DIF>REF_DIF_1 && MA5>REF_MA5_1 && (REF_DIF_1>=REF_DIF_2 && REF_MA5_1<=REF_MA5_2 || REF_DIF_1<=REF_DIF_2 && REF_MA5_1>=REF_MA5_2 || REF_DIF_1<REF_DIF_2 && REF_MA5_1<REF_MA5_2);
		    boolean 同向条件2=DIF<REF_DIF_1 && MA5<REF_MA5_1 && (REF_DIF_1>=REF_DIF_2 && REF_MA5_1<=REF_MA5_2 || REF_DIF_1<=REF_DIF_2 && REF_MA5_1>=REF_MA5_2 || REF_DIF_1>REF_DIF_2 && REF_MA5_1>REF_MA5_2);

//		    System.out.println(同向条件1+":"+同向条件2);
//		    if(i==list.size()-5){
//		    	System.out.println(nE);
//		    	System.out.println(pE);
//		    	System.out.println(ppE);
//		    	System.out.println("1:"+(DIF<REF_DIF_1));
//		    	System.out.println("2:"+(MA5<REF_MA5_1));
//		    	System.out.println("3:"+(REF_DIF_1>=REF_DIF_2 && REF_MA5_1<=REF_MA5_2));
//		    	System.out.println("4:"+(REF_DIF_1<=REF_DIF_2 && REF_MA5_1>=REF_MA5_2));
//		    	System.out.println("5:"+(REF_DIF_1>REF_DIF_2 && REF_MA5_1>REF_MA5_2));
//		    	System.out.println("同向条件2:"+同向条件2);
//		    }
		    
			if((同向条件1||同向条件2)&&sIndex==-1){
				sIndex=i;
			}
			if(sIndex!=-1){//定位到DIF和MA同向的K线后，找到该K线之前的交叉
				KEntity crossEntity=list.get(i);
				if(crossEntity.getCrossType()!=0){
					cIndex=i;
					break;
				}
			}
		}
		//DIF转向确保是逆向的，不是顺势修正的
//		System.out.println("cIndex:"+cIndex+" sIndex:"+sIndex+" size:"+list.size());
		
//		if(list.get(cIndex).getCrossType()==1&&list.get(sIndex).getDif()<list.get(sIndex-1).getDif()||list.get(cIndex).getCrossType()==-1&&list.get(sIndex).getDif()>list.get(sIndex-1).getDif()){
		if(list.get(cIndex).getCrossType()==1&&list.get(sIndex).getDif()<list.get(sIndex-1).getDif()||list.get(cIndex).getCrossType()==-1&&list.get(sIndex).getDif()>list.get(sIndex-1).getDif()){
			return sIndex;
		}else{
			return -1;
		}
	}
	
	private static void MACD单时间框架预警(ArrayList<KEntity> list){
		//小结构要满足：顺势、三重、小大才报，大结构满足三重才报，大结构通过“单品种结构回踩预警”
		Structure struct=获取结构类型(list,true);
		int period=struct.getPeriod();
		KEntity entity=list.get(list.size()-1);
		String time=list.get(list.size()-1).getTime();
		String key=entity.getName()+entity.getTime()+entity.getMin();
		int min=entity.getMin();
		int hour=时间工具.获得现在小时();
		boolean 条件0=(time.indexOf(" 14:")==-1)||(time.indexOf(" 14:")!=-1&&hour==14);
		boolean 条件1=(struct.getDevType()==1||struct.getDevType()==-1)&&entity.getDeviateType()!=0;
		boolean 条件2=!macdAlertSet.contains(key);
		boolean 条件3=struct.getBigDevType()!=0||struct.isTriDevType()||period>15||struct.isForTrend();//大结构，小结构带大（预）结构，三重结构，顺势结构
		
		//=========================打印====================================
//		ArrayList<KEntity> list1=AlertUtil.getListFromMap(struct.getName()+1);
//		if(list1.get(list1.size()-1).getTime().indexOf(":59:")!=-1){
//			System.out.println("MA单品种预警:"+struct);
//		}
		//=========================打印====================================
		
		String 结构信息="";
		if(struct.isTriDevType()){
			结构信息=结构信息+" 三重";
		}
		if(struct.isForTrend()){
			结构信息=结构信息+" 顺势";
		}
		//是小结构的时候，肯定有下面的一种情况才行
		if(period<15){
			if(struct.getBigDevType()!=0){
				String aa="";
				if(struct.getBigDevType()==-1){
					aa="顶";
				}else if(struct.getBigDevType()==1){
					aa="底";
				}else if(struct.getBigDevType()==-2){
					aa="预顶";
				}else if(struct.getBigDevType()==2){
					aa="预底";
				}
				结构信息=结构信息+" 有"+struct.getBigPeriod()+aa;
			}
		}
		if(!结构信息.equals("")){
			结构信息=" "+结构信息;
		}

		if(条件0&&条件1&&条件2&&条件3){
			MusicPlay.playTwo(MusicPlay.delDigtal(entity.getName()), MusicPlay.minString(min));
			LOGGER.info(entity.getName()+" "+entity.getMin()+" "+entity.getClose()+" "+(struct.getDevType()==1?"底"+jiegou:"顶"+jiegou)+结构信息);
			macdAlertSet.add(key);
		}
		
	}
	

	
	//只对最近K线进行分析，如果对所有历史进行分析，则判断三重结构的时候，计算交叉值方式有变，参考MyMACD.是否三重结构()，或者可以采用截取list的subList的方式进行分析
	//返回结构类型，0表示没有结构，-1表示预顶结构，-2表示顶结构，1表示预底结构，-2表示底结构
	public static Structure 获取结构类型(ArrayList<KEntity> list,boolean 盘中){
		//新算法：
		//1、检查目前状况，最近的交叉是金叉死叉
		//2、如果是零轴上金叉，先判断是否钝化，然后向前检查同侧是否有死叉，并通过DIF值比较确定是否预结构
		//3、如果是零轴上死叉，先判断是否背离，再判断同侧是否有三个死叉来确定三重结构
		//4、判断同侧的时候，是以最近一次交叉位置的DEA值为标准，而不是以最近一根KEntity的DEA值为标准
		//5、获取趋势方向，判断是否顺势
		//6、如果是小时间框架的结构，还要在大时间框架重复1、2步骤，确定是否有大的同向预结构
		//7、零轴下与上面相反。
		Structure struct=new Structure();
		int dType=0;
		int cIndex=-1;
//		KEntity nowEntity=list.get(list.size()-1);//用来计算预结构的
		KEntity crossEntity=null;//用来计算结构的
		for(int i=list.size()-1;i>0;i--){
			KEntity tempEntity=list.get(i);
			if(tempEntity.getCrossType()!=0){
				crossEntity=tempEntity;
				dType=tempEntity.getDeviateType();
				cIndex=i;
				break;
			}
		}
		//1、先判断是否处于结构中
		//2、如果是处在结构，则判断是小结构大结构
		//2.1:小结构，要判断是否三重和顺势结构，以及是否有预结构
		//2.2:大结构判断是否三重结构和顺势结构
		//3、如果不是结构，则判断是否预结构
		
		
		boolean isStruct=(dType!=0);
		if(isStruct){
			if(crossEntity.getMin()<30){//小结构
				struct=小结构详情(list,cIndex,盘中);
			}else{//大结构
				struct=大结构详情(list,cIndex,盘中);
			}
		}else{
			if(crossEntity.getMin()<30){//小结构预结构没有意义，不做处理
				;
			}else{//大结构
				struct=预结构详情(list,cIndex,盘中);
			}
		}
		
		return struct;
	}
	
	private static Structure 小结构详情(ArrayList<KEntity> list,int index,boolean 盘中){
		//1、判断是否顺势结构
		//2、判断是否三重结构
		//3、判断是否有预结构，预结构的时间框架是多少
		Structure struct=new Structure();
		struct.setCrossIndex(index);
		KEntity crossEntity=list.get(index);
		int cType=crossEntity.getCrossType();
		int dType=crossEntity.getDeviateType();//
		boolean forTrend=是否顺势结构(list,index,盘中);
		boolean triDev=是否三重结构(list,index);
		boolean passivate=是否钝化(list);

		ArrayList<KEntity> thirtyList=AlertUtil.getListFromMap(crossEntity.getName()+30);
		ArrayList<KEntity> sixtyList=AlertUtil.getListFromMap(crossEntity.getName()+60);
		if(是否有大结构(list,thirtyList,index)){//先判断是否有结构，比较容易判断
			struct.setBigPeriod(30);//有三十分钟预结构
			struct.setBigDevType(dType*1);
		}else if(是否有大结构(list,sixtyList,index)){
			struct.setBigPeriod(60);//有三十分钟预结构
			struct.setBigDevType(dType*1);
		}else if(是否有大预结构(list,thirtyList,index,盘中)){
			struct.setBigPeriod(30);//有三十分钟预结构
			struct.setBigDevType(dType*2);
		}else if(是否有大预结构(list,sixtyList,index,盘中)){
			struct.setBigPeriod(60);//有六十分钟预结构
			struct.setBigDevType(dType*2);
		}else{
			struct.setBigPeriod(0);
			struct.setBigDevType(0);
		}
		
		struct.setCrossType(cType);
		struct.setDevType(dType);//如果是预结构，则设置的值是-2或2
		struct.setForTrend(forTrend);//--------------是否顺势----------
		struct.setName(crossEntity.getName());
		struct.setPassivate(passivate);//已经处于结构之中
		struct.setPeriod(crossEntity.getMin());
		struct.setTime(list.get(list.size()-1).getTime());
		struct.setTriDevType(triDev);//--------------是否三重----------
		return struct;

	}
	
	private static Structure 大结构详情(ArrayList<KEntity> list,int index,boolean 盘中){
		//1、判断是否顺势结构
		//2、判断是否三重结构
		Structure struct=new Structure();
		struct.setCrossIndex(index);
		KEntity crossEntity=list.get(index);
		int cType=crossEntity.getCrossType();
		int dType=crossEntity.getDeviateType();
		boolean forTrend=是否顺势结构(list,index,盘中);
		boolean triDev=是否三重结构(list,index);
		boolean passivate=是否钝化(list);

		struct.setBigPeriod(3600);//半小时和小时结构的大结构看日线
		struct.setCrossType(cType);
		struct.setDevType(dType);//如果是预结构，则设置的值是-2或2，因为这里是已经确定为结构了，所以直接把crossEntity的结构类型赋值就可以，其值肯定是1或-1
		struct.setForTrend(forTrend);//--------------是否顺势----------
		struct.setName(crossEntity.getName());
		struct.setPassivate(passivate);//已经处于结构之中，一般肯定是钝化的，但如果已经穿越了零轴，也可能不是
		struct.setPeriod(crossEntity.getMin());
		struct.setTime(list.get(list.size()-1).getTime());
		struct.setTriDevType(triDev);//--------------是否三重----------
		return struct;
	}
	

	
	private static Structure 预结构详情(ArrayList<KEntity> list,int index,boolean 盘中){
		//1、是否顺势预结构
		//2、是否三重预结构
		Structure struct=new Structure();
		struct.setCrossIndex(index);
		if(!是否钝化(list)){
			return struct;
		}
		KEntity crossEntity=list.get(index);
		int cType=crossEntity.getCrossType();
//		int dType=crossEntity.getDeviateType();
		boolean forTrend=是否顺势结构(list,index,盘中);
		boolean triDev=是否三重结构(list,index);
		boolean passivate=是否钝化(list);
		boolean isyDtype=是否预结构(list,盘中);
		

		struct.setBigPeriod(3600);//半小时和小时结构的大结构看日线
		struct.setCrossType(cType);
		
		//预结构可能是0,-2,2
		if(isyDtype){
			if(cType==1&&crossEntity.getDea()>0){//零轴上金叉对应“预顶背离”
				struct.setDevType(-2);
			}
			if(cType==-1&&crossEntity.getDea()<0){
				struct.setDevType(2);
			}
		}else{
			struct.setDevType(0);
		}
		//如果是预结构，则设置的值是-2或2，因为这里是已经确定为结构了，所以直接把crossEntity的结构类型赋值就可以，其值肯定是1或-1
		
		struct.setForTrend(forTrend);//--------------是否顺势----------
		struct.setName(crossEntity.getName());
		struct.setPassivate(passivate);//已经处于结构之中
		struct.setPeriod(crossEntity.getMin());
		struct.setTime(list.get(list.size()-1).getTime());
		struct.setTriDevType(triDev);//--------------是否三重----------
		return struct;
	}
	
	private static boolean 是否顺势结构(ArrayList<KEntity> list,int index,boolean 盘中){
		KEntity crossEntity=list.get(index);
		int dType=crossEntity.getDeviateType();
		int forTrend=获得趋势(AlertUtil.getListFromMap(crossEntity.getName()+3600),盘中);
		if(dType*forTrend>0){
			return true;
		}
		return false;
	}
	
	//需要在已经判断list有预结构的情况下，才进行是否顺势预结构的判断
	private static boolean 是否顺势预结构(ArrayList<KEntity> list,int index,boolean 盘中){
		KEntity crossEntity=list.get(index);
		int forTrend=获得趋势(AlertUtil.getListFromMap(crossEntity.getName()+3600),盘中);
		int cType=crossEntity.getCrossType();
		if(cType*forTrend<0){
			return true;
		}
		return false;
	}
	
	private static boolean 是否三重结构(ArrayList<KEntity> list,int index){
		KEntity crossEntity=list.get(index);
		int dType=crossEntity.getDeviateType();
		if(dType==0){
			return false;
		}
		int crossNum=1;//包括index处的交叉，一共有三次交叉，且index处是结构
		for(int i=index-1;i>0;i--){
			KEntity tEntity=list.get(i);
			if(tEntity.getDea()*crossEntity.getDea()>0){
				if(tEntity.getCrossType()==crossEntity.getCrossType()){
					crossNum++;
					if(crossNum>=3){
						break;
					}
				}
			}else{
				break;
			}
		}
		if(crossNum>=3){
			return true;
		}else{
			return false;
		}
		
	}
	
	private static boolean 是否三重预结构(ArrayList<KEntity> list){
		int index=list.size()-1;
		KEntity crossEntity=list.get(index);

		int crossNum=1;//包括index处的交叉，一共有三次交叉，且index处是结构
		for(int i=index-1;i>0;i--){
			KEntity tEntity=list.get(i);
			if(tEntity.getDea()*crossEntity.getDea()>0){
				if(tEntity.getCrossType()*crossEntity.getCrossType()<0){
					crossNum++;
					if(crossNum>=3){
						break;
					}
				}
			}else{
				break;
			}
		}
		if(crossNum>=3){
			return true;
		}else{
			return false;
		}
		
	}
	
	//针对小结构，判断是否有同向大结构
	private static boolean 是否有大结构(ArrayList<KEntity> smallList,ArrayList<KEntity> bigList,int index){
		KEntity sCrossEntity=smallList.get(index);
		if(sCrossEntity.getMin()>15){//大时间框架判断本身是否结构就可以了
			return false;
		}
		int dType=0;
		int cType=0;
		for(int i=bigList.size()-1;i>0;i--){
			KEntity tEntity=bigList.get(i);
			dType=tEntity.getDeviateType();
			cType=tEntity.getCrossType();
			if(cType!=0){
				break;
			}
		}

		if(sCrossEntity.getDeviateType()==dType){//大小结构同向
			return true;
		}
		return false;
	}
	
	//如何获得预结构的时间框架？如果bigList是30分钟，则预结构框架是30分钟，如果是1小时则预结构框架是60分钟
	private static boolean 是否有大预结构(ArrayList<KEntity> smallList,ArrayList<KEntity> bigList,int index,boolean 盘中){
		KEntity sCrossEntity=smallList.get(index);
		if(sCrossEntity.getMin()>15){//大结构不考虑预结构问题
			return false;
		}
		if(是否预结构(bigList,盘中)){
			return true;
		}

		return false;
	}

	//list是针对30、60分钟
	private static boolean 是否预结构(ArrayList<KEntity> list,boolean 盘中){
		if(!是否钝化(list)){
			return false;
		}
		
		int cIndex1=-1;
		int cType1=0;
		int cIndex2=-1;
		int cType2=0;
		int crossNum=0;
		KEntity nowEntity=list.get(list.size()-1);
		KEntity bigCrossEntity1=null;
		KEntity bigCrossEntity2=null;
		
		for(int i=list.size()-1;i>0;i--){
			KEntity tempEntity=list.get(i);
			if(tempEntity.getCrossType()!=0&&crossNum<1){
				bigCrossEntity1=tempEntity;
				cType1=tempEntity.getDeviateType();
				cIndex1=i;
				crossNum++;
//				break;
			}
			if(crossNum>=1&&bigCrossEntity1!=null&&tempEntity.getCrossType()!=0&&bigCrossEntity1.getDea()*tempEntity.getDea()>0){
				bigCrossEntity2=tempEntity;
				cType2=tempEntity.getDeviateType();
				cIndex2=i;
				crossNum++;
			}
			if(crossNum>=2||bigCrossEntity1!=null&&bigCrossEntity1.getDea()*tempEntity.getDea()<0){//DEA穿零轴
				break;
			}
		}
		if(crossNum<2){
			return false;
		}
//		if(dType==sCrossEntity.getDeviateType()){//同向的“小结构+大结构”，结构在结构处处理
//			return true;
//		}
		//预结构判断算法
		//0.最近处于钝化之中，在开始位置判断了
		//1.从bigCrossEntity1开始往前，在DEA未改变的情况下还有一次交叉bigCrossEntity
		//2.比较nowEntity和bigCrossEntity2的DIF，此处两个entity的dea都在同一个方向
		//3.
		boolean 条件11 = bigCrossEntity2.getClose() <= nowEntity.getClose()
		|| MyMACD.getNearCloseHigh(bigCrossEntity2, 5,list) <= MyMACD.getNearCloseHigh(//修改成前一处交叉点的高点低于目前的高点是否就可以？？？？？？否则很多平顶的会错过
				nowEntity, 5,list);
		boolean 条件12=是否三重预结构(list);
		boolean 条件13=Math.abs(MyMACD.getNearDifHigh(bigCrossEntity2, 5, list))>Math.abs(MyMACD.getNearDifHigh(nowEntity, 5, list))&&bigCrossEntity1.getDea()>0;
		
		
		boolean 条件21= bigCrossEntity2.getClose() >= nowEntity.getClose()
		|| MyMACD.getNearCloseLow(bigCrossEntity2, 5,list) >= MyMACD.getNearCloseLow(
				nowEntity, 5,list);
		boolean 条件22=条件12;
		boolean 条件23=Math.abs(MyMACD.getNearDifLow(bigCrossEntity2, 5, list))<Math.abs(MyMACD.getNearDifLow(nowEntity, 5, list))&&bigCrossEntity1.getDea()<0;
		
		//在已经钝化的情况下，只要判断DIF就可以了，不需要判断价格
		
//		if(条件11&&条件13||条件12&&条件13||条件21&&条件23||条件22&&条件23){
		if(条件13||条件23){
			return true;
		}
		return false;
	}
	
	//盘中和盘后分析获得的方式不一样
	private static int 获得趋势(ArrayList<KEntity> list,boolean 盘中){
//		ArrayList<KEntity> list=AlertUtil.getListFromMap(name+3600);
		int forTrend=0;
		int k=list.size()-1;
		if(盘中){//盘中判断方向，是以前一天数据为准，盘后盘方向则包含本天数据
			k=k-1;
		}
		for(;k>list.size()-5;k--){
			KEntity nEntity=list.get(k);
			KEntity pEntity=list.get(k-1);
			if(nEntity.getMA5()>pEntity.getMA5()&&nEntity.getDif()>=pEntity.getDif()){
				//isUP=true;
				forTrend=1;
				break;
			}
			if(nEntity.getMA5()<pEntity.getMA5()&&nEntity.getDif()<=pEntity.getDif()){
				forTrend=-1;
				break;
			}
		}
		return forTrend;
	}
	
	private static boolean 是否钝化(ArrayList<KEntity> list){
		if(Math.abs(list.get(list.size()-1).getMacd())<Math.abs(list.get(list.size()-2).getMacd())){
			return true;
		}else{
			return false;
		}
	}
	
	private static void 均线预警播放打印(String codeName,int period,ArrayList<KEntity> list,Structure struct,String 撑压){
		String newCodeName=MusicPlay.delDigtal(codeName);
		String time=list.get(list.size()-1).getTime();
		int structPeriod=struct.getPeriod();
		if(!maAlertSet.contains(codeName+period+time)){
			if(period==5){
				MusicPlay.playTwo(newCodeName, "fivenear");
			}else if(period==10){
				MusicPlay.playTwo(newCodeName, "teennear");
			}else{
				MusicPlay.play(newCodeName);
			}
//			日志工具.fileTrack.info("-------------------------------------预警:"+codeName+" "+period+"日线撑压");
//			日志工具.fileTrack.info("                                        "+codeName+" "+period+" "+list.get(list.size()-1).getClose()+" "+撑压);
			String 结构信息=struct.isForTrend()?"顺势":"";
			if(struct.getDevType()==-1&&structPeriod>15||struct.getDevType()==-1&&structPeriod<=15&&struct.isTriDevType()){
				if(structPeriod<=15){
					结构信息=结构信息+struct.getPeriod()+"三重顶";
				}else{
					结构信息=结构信息+struct.getPeriod()+"顶";
				}
			}else if(struct.getDevType()==1&&structPeriod>15||struct.getDevType()==1&&structPeriod<=15&&struct.isTriDevType()){
				if(structPeriod<=15){
					结构信息=结构信息+struct.getPeriod()+"三重底";
				}else{
					结构信息=结构信息+struct.getPeriod()+"底";
				}
			}else if(struct.getDevType()==-2){
				结构信息=结构信息+struct.getPeriod()+"预顶";
			}else if(struct.getDevType()==2){
				结构信息=结构信息+struct.getPeriod()+"预底";
			}else{
//				结构信息="无结构";
			}
			if(!结构信息.equals("")){
				结构信息=结构信息+jiegou;
			}
//			LOGGER.info("                                        "+codeName+" "+period+" "+list.get(list.size()-1).getClose()+" "+撑压+" "+结构信息);
			LOGGER.info("                                        "+codeName+" "+period+" "+list.get(list.size()-1).getClose()+" "+撑压);
//			日志工具.fileTrack.info(codeName+" "+period+" "+list.get(list.size()-1).getClose()+" "+撑压+" "+结构信息);
			maAlertSet.add(codeName+period+time);
//			AlertUtil.打印列表(list, "MyMA.均线预警播放打印()");
		}
	}
	
	//返回值5,10,-5,-10表示正常的五日十日回踩，101和-101表示出现单转后的回踩
	public static int 回踩均线(ArrayList<KEntity> list,boolean 盘中,double nearRate){
		int maType=0;
		if(list.size()<=4){
			LOGGER.info("MyMA:93:"+list.get(0).getName());
			return maType;
		}
		//5,10,101,-5,-10,-101
		int trend=获得趋势(list,盘中);//五日和DIF趋势，判断是向上还是向下
		double 预警比率=nearRate;

		KEntity ppEntity=list.get(list.size()-3);
		KEntity preEntity=list.get(list.size()-2);
		KEntity nowEntity=list.get(list.size()-1);

		//条件1：同向
		boolean 条件11=ppEntity.getMA5()<=preEntity.getMA5()&&ppEntity.getDif()<=preEntity.getDif();
		//条件2：单转
		boolean 条件12=ppEntity.getMA5()<preEntity.getMA5()&&ppEntity.getDif()>preEntity.getDif()||ppEntity.getMA5()>preEntity.getMA5()&&ppEntity.getDif()<preEntity.getDif();

		//条件1：同向
		boolean 条件21=ppEntity.getMA5()>=preEntity.getMA5()&&ppEntity.getDif()>=preEntity.getDif();
		//条件2：单转
		boolean 条件22=ppEntity.getMA5()>preEntity.getMA5()&&ppEntity.getDif()<preEntity.getDif()||ppEntity.getMA5()<preEntity.getMA5()&&ppEntity.getDif()>preEntity.getDif();

		
		double 距五日比率=Math.abs((nowEntity.getClose()-nowEntity.getMA5())/nowEntity.getMA5()*100);
		double 距十日比率=Math.abs((nowEntity.getClose()-nowEntity.getMA10())/nowEntity.getMA10()*100);

		//支撑时均线方向要保证
		if(trend==1){
			if(条件11&&距五日比率<=预警比率&&nowEntity.getMA5()>preEntity.getMA5()){//前一天同向时五日支撑，如果到达5日时，5日已经转向，则不进行预警，等待到达10看能否满足
				maType=5;
			}
			if(条件11&&距十日比率<=预警比率&&nowEntity.getMA10()>preEntity.getMA10()){//前一天同向时十日支撑
				maType=10;
			}
			if(条件12&&距十日比率<=预警比率&&nowEntity.getMA10()>preEntity.getMA10()){//前一天单转时十日支撑
				maType=101;
			}
		}
		
		if(trend==-1){
			if(条件21&&距五日比率<=预警比率&&nowEntity.getMA5()<preEntity.getMA5()){
				maType=-5;
			}
			if(条件21&&距十日比率<=预警比率&&nowEntity.getMA10()<preEntity.getMA10()){
				maType=-10;
			}
			if(条件22&&距十日比率<=预警比率&&nowEntity.getMA10()<preEntity.getMA10()){
				maType=-101;
			}
		}
		
		return maType;
		
	}
	
	//返回值5,10,-5,-10表示正常的五日十日回踩，101和-101表示出现单转后的回踩
	public static int 小时回踩均线(ArrayList<KEntity> list,boolean 盘中,double nearRate){
		int maType=0;
		if(list.size()<=4){
			LOGGER.info("MyMA:93:"+list.get(0).getName());
			return maType;
		}
		//5,10,101,-5,-10,-101
		int trend=获得趋势(list,盘中);//五日和DIF趋势，判断是向上还是向下
		double 预警比率=nearRate;

		KEntity ppEntity=list.get(list.size()-3);
		KEntity preEntity=list.get(list.size()-2);
		KEntity nowEntity=list.get(list.size()-1);

		//条件1：同向
		boolean 条件11=ppEntity.getMA5()<=preEntity.getMA5()&&ppEntity.getDif()<=preEntity.getDif();
		//条件2：单转
		boolean 条件12=ppEntity.getMA5()<preEntity.getMA5()&&ppEntity.getDif()>preEntity.getDif()||ppEntity.getMA5()>preEntity.getMA5()&&ppEntity.getDif()<preEntity.getDif();

		//条件1：同向
		boolean 条件21=ppEntity.getMA5()>=preEntity.getMA5()&&ppEntity.getDif()>=preEntity.getDif();
		//条件2：单转
		boolean 条件22=ppEntity.getMA5()>preEntity.getMA5()&&ppEntity.getDif()<preEntity.getDif()||ppEntity.getMA5()<preEntity.getMA5()&&ppEntity.getDif()>preEntity.getDif();

		
		double 距五日比率=Math.abs((nowEntity.getClose()-nowEntity.getMA5())/nowEntity.getMA5()*100);
		double 距十日比率=Math.abs((nowEntity.getClose()-nowEntity.getMA10())/nowEntity.getMA10()*100);

		//支撑时均线方向要保证
		if(trend==1){
			if(距五日比率<=预警比率){
				maType=5;
			}
			if(距十日比率<=预警比率){
				maType=10;
			}
//			if(条件11&&距五日比率<=预警比率&&nowEntity.getMA5()>preEntity.getMA5()){//前一天同向时五日支撑，如果到达5日时，5日已经转向，则不进行预警，等待到达10看能否满足
//				maType=5;
//			}
//			if(条件11&&距十日比率<=预警比率&&nowEntity.getMA10()>preEntity.getMA10()){//前一天同向时十日支撑
//				maType=10;
//			}
//			if(条件12&&距十日比率<=预警比率&&nowEntity.getMA10()>preEntity.getMA10()){//前一天单转时十日支撑
//				maType=101;
//			}
		}
		
		if(trend==-1){
			if(距五日比率<=预警比率){
				maType=-5;
			}
			if(距十日比率<=预警比率){
				maType=-10;
			}
//			if(条件21&&距五日比率<=预警比率&&nowEntity.getMA5()<preEntity.getMA5()){
//				maType=-5;
//			}
//			if(条件21&&距十日比率<=预警比率&&nowEntity.getMA10()<preEntity.getMA10()){
//				maType=-10;
//			}
//			if(条件22&&距十日比率<=预警比率&&nowEntity.getMA10()<preEntity.getMA10()){
//				maType=-101;
//			}
		}
		
		return maType;
		
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	

}
