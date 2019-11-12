package com.example.future.macd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.future.tools.MusicPlay;
import com.example.future.tools.时间工具;
import com.example.future.tools.配置文件;

public class MyMACD {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyMACD.class);
	public static HashSet<String> macdAlertSet=new HashSet<String>();
	private final static int cSize=240;
	private final static int longPeriod=26;
	private final static int shortPeriod=12;
	private final static int midlePeriod=9;
	
	
	public static void MACD计算预警(String codeName){
		String[] alertMins=配置文件.获取配置项("alertMins").split(",");
//		if(配置文件.获取配置项("macdalert")!=null&&配置文件.获取配置项("macdalert").equals("true")){
		if(配置文件.获取真假("macdalert")){
			for(int j=0;j<alertMins.length;j++){
				ArrayList<KEntity> minlist=AlertUtil.getListFromMap(codeName+alertMins[j]);
				if(minlist!=null){
//					System.out.println("1416:"+codeName+" "+alertMins[j]+" 数据为空");
					MyMACD.MACD单时间框架预警(minlist);
				}
				
			}
		}
	}
	
	public static void MACD单时间框架预警(ArrayList<KEntity> list){
//		if(list.get(0).getMin()==30&&list.get(0).getName().equals("CF701")){
//			打印列表(list,"单时间框架预警**************");
//			时间工具.休眠秒数(10);
//		}
//		System.out.println(list.get(list.size()-1));
		int n=0;//只计算最近的数据
		if(list.size()>240){
			n=list.size()-240;
		}
		for(int i=n;i<list.size();i++){
//			System.out.println(list.get(i));
			KEntity errEntity=null;
			try {
//				setMACD(list.get(i),list, 12, 26, 9);//在进入该方法之前已经计算过
				KEntity entity=null;
				try {
					entity=list.get(list.size()-1);
				} catch (Exception e) {
					// TODO: handle exception
				}
				if(entity==null){
					entity=AlertUtil.getEntityFromMap(list.get(0).getName()+list.get(0).getMin());//如果出现数据同步问题，可以暂时使用map里面的数据
				}
				errEntity=entity;
				String key=entity.getName()+entity.getTime()+entity.getMin();
				String time=entity.getTime();
				int min=entity.getMin();
				int hour=时间工具.获得现在小时();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
//				此处还要增加判断，看是否最新的一根K线，如果是之前的，则因为没有实时效果，不进行预警。下面的条件0就是这种判断
				boolean 条件0=(time.indexOf(" 14:")==-1)||(time.indexOf(" 14:")!=-1&&hour==14);
				boolean 条件1=entity.getDeviateType()!=0;
				boolean 条件2=!macdAlertSet.contains(key);

				if(条件0&&条件1&&条件2){
					MusicPlay.playTwo(MusicPlay.delDigtal(entity.getName()), MusicPlay.minString(min));
					LOGGER.info(entity.getName()+" "+entity.getMin()+" "+entity.getClose()+" "+(entity.getDeviateType()==1?"底结构":"顶结构"));
					macdAlertSet.add(key);
				}

			} catch (Exception e) {
				// TODO: handle exception
//				日志工具.fileErr.error("预警出错："+errEntity);
				LOGGER.error(e.getMessage(), e);
			}
			
		}

		
	}

	/**
	 * Calculate EMA,
	 * 
	 * @param list
	 *            :Price list to calculate，the first at head, the last at tail.
	 * @return
	 */
	public static final Double getEXPMA2(final List<Double> list,
			final int number) {
		// EMA（12）= 前一日EMA（12）×11/13＋今日收盘价×2/13
		// EMA（26）= 前一日EMA（26）×25/27＋今日收盘价×2/27

		// 开始计算EMA值，
		Double k = 2.0 / (number + 1.0);// 计算出序数
		Double ema = list.get(0);// 第一天ema等于当天收盘价
		for (int i = 1; i < list.size(); i++) {
			// 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
			ema = list.get(i) * k + ema * (1 - k);
		}
		return ema;
	}

	/**
	 * calculate MACD values
	 * 
	 * @param list
	 *            :Price list to calculate，the first at head, the last at tail.
	 * @param shortPeriod
	 *            :the short period value.
	 * @param longPeriod
	 *            :the long period value.
	 * @param midPeriod
	 *            :the mid period value.
	 * @return
	 */
	public static final HashMap<String, Double> getMACD2(
			final List<Double> list, final int shortPeriod,
			final int longPeriod, int midPeriod) {
		HashMap<String, Double> macdData = new HashMap<String, Double>();
		List<Double> diffList = new ArrayList<Double>();
		Double shortEMA = 0.0;
		Double longEMA = 0.0;
		Double dif = 0.0;
		Double dea = 0.0;

		for (int i = list.size() - 1; i >= 0; i--) {
			List<Double> sublist = list.subList(0, list.size() - i);
			shortEMA = MyMACD.getEXPMA2(sublist, shortPeriod);
			longEMA = MyMACD.getEXPMA2(sublist, longPeriod);
			dif = shortEMA - longEMA;
			diffList.add(dif);
		}
		dea = MyMACD.getEXPMA2(diffList, midPeriod);
		macdData.put("DIF", dif);
		macdData.put("DEA", dea);
		macdData.put("MACD", (dif - dea) * 2);
		return macdData;
	}

	/**
	 * Calculate EMA,
	 * 
	 * @param list
	 *            :Price list to calculate，the first at head, the last at tail.
	 * @return
	 */
	public static final void setEXPMA(KEntity kEntity,KEntity preEntity, final int shortNumber,
			final int longNumber) {
		// EMA（12）= 前一日EMA（12）×11/13＋今日收盘价×2/13
		// EMA（26）= 前一日EMA（26）×25/27＋今日收盘价×2/27
		// Double ema = 0.0;
		if (kEntity.getPreIndex() == -1) {
			kEntity.setShortEma(kEntity.getClose());
			kEntity.setLongEma(kEntity.getClose());
		} else {
//			// 因为上面有判断"getPreIndex() == -1"的判断，所以这里不用判断是否为null
//			KEntity preEntity = entityMap.get(
//					kEntity.getName() + kEntity.getMin()).get(
//					kEntity.getPreIndex());

			// EMA（12）= 前一日EMA（12）×11/13＋今日收盘价×2/13
			// EMA（26）= 前一日EMA（26）×25/27＋今日收盘价×2/27
			// double
			// shortEma=preEntity.getShortEma()*(shortNumber-1)/(shortNumber+1)+kEntity.getClose()*2/(shortNumber+1);
			// double
			// longEma=preEntity.getLongEma()*(longNumber-1)/(longNumber+1)+kEntity.getClose()*2/(longNumber+1);
			// kEntity.setShortEma(shortEma);
			// kEntity.setLongEma(longEma);
			Double shortK = 2.0 / (shortNumber + 1.0);// 计算出序数
			Double longK = 2.0 / (longNumber + 1.0);// 计算出序数
			kEntity.setShortEma(kEntity.getClose() * shortK
					+ preEntity.getShortEma() * (1 - shortK));
			kEntity.setLongEma(kEntity.getClose() * longK
					+ preEntity.getLongEma() * (1 - longK));
		}
	}

	public static KEntity getPreEntity(final KEntity ce,ArrayList<KEntity> list) {
		if (ce.getPreIndex() == -1) {
			return null;
		}
		if(ce.getPreIndex()>=list.size()) {
			System.out.println(ce.getName());
		}
		return list.get(ce.getPreIndex());
	}

	/**
	 * calculate MACD values
	 * 
	 * @param list
	 *            :Price list to calculate，the first at head, the last at tail.
	 * @param shortPeriod
	 *            :the short period value.
	 * @param longPeriod
	 *            :the long period value.
	 * @param midPeriod
	 *            :the mid period value.
	 * @return
	 */
	public static final void setMACD(KEntity kEntity,ArrayList<KEntity> list, final int shortPeriod,
			final int longPeriod, int midPeriod) {
		HashMap<String, Double> macdData = new HashMap<String, Double>();
		Double dif = 0.0;
		Double dea = 0.0;
		double macd = 0.0;
		setEXPMA(kEntity,getPreEntity(kEntity,list), shortPeriod, longPeriod);

		// DIFF=今日EMA（12）- 今日EMA（26）
		dif = kEntity.getShortEma() - kEntity.getLongEma();

		// DEA（MACD）= 前一日DEA×8/10＋今日DIF×2/10
		if (kEntity.getPreIndex() != -1) {
			dea = getPreEntity(kEntity,list).getDea() * (midPeriod - 1)
					/ (midPeriod + 1) + dif * 2 / (midPeriod + 1);
		} else {
			dea = dif * 2 / (midPeriod + 1);
		}

		// BAR(macd)=2×(DIFF－DEA)
		macd = (dif - dea) * 2;
		kEntity.setDif(dif);
		kEntity.setDea(dea);
		kEntity.setMacd(macd);
		setCrossType(kEntity,list);
		//bug to fix:死循环
		//setDeviateType(kEntity,list);
	}
	
	//均线预警系统涉及MACD中DIF与五日均线同向的问题，采用默认周期
	public static void setMACD(ArrayList<KEntity> list){
//		int n=0;//只计算最近的数据
//		if(list.size()>cSize){
//			n=list.size()-cSize;
//		}
//		for(int i=n;i<list.size();i++){
//			MyMACD.setMACD(list.get(i),list, 12, 26, 9);
//		}
		setMACD(list,shortPeriod,longPeriod,midlePeriod);
	}
	
	//均线预警系统涉及MACD中DIF与五日均线同向的问题
	public static void setMACD1(ArrayList<KEntity> list,int shortPeriod,int longPeriod,int midPeriod){
		int n=0;//只计算最近的数据
		if(list.size()>cSize){
			n=list.size()-cSize;
		}
		for(int i=n;i<list.size();i++){
			MyMACD.setMACD(list.get(i),list, shortPeriod, longPeriod, midPeriod);
		}
	}
	
	//均线预警系统涉及MACD中DIF与五日均线同向的问题
	public static void setMACD(ArrayList<KEntity> list,int shortPeriod,int longPeriod,int midPeriod){
		int n=0;//只计算最近的数据
//		if(list.size()>cSize){
//			n=list.size()-cSize;
//		}
		for(int i=n;i<list.size();i++){
			//经过这样的判断之后，之前已经计算过的值就不做重新计算，只有最后一个entity的值要根据最新价格进行重新计算
			if((list.get(i).getDif()+"").equals("0.0")&&(list.get(i).getDea()+"").equals("0.0")||(i==list.size()-1)){
				MyMACD.setMACD(list.get(i),list, shortPeriod, longPeriod, midPeriod);
			}
		}
	}

	// 设置交叉类型，0：不交叉，1：金叉，-1：死叉
	private static int setCrossType(KEntity ce,ArrayList<KEntity> list) {
		int type = 0;
		KEntity preEntity = getPreEntity(ce,list);
		if (preEntity == null) {
			;
		} else if (preEntity.getDea() > preEntity.getDif()
				&& ce.getDea() <= ce.getDif()
				|| preEntity.getDea() >= preEntity.getDif()
				&& ce.getDea() < ce.getDif()) {
			type = 1;// 金叉
		} else if (preEntity.getDea() < preEntity.getDif()
				&& ce.getDea() >= ce.getDif()
				|| preEntity.getDea() <= preEntity.getDif()
				&& ce.getDea() > ce.getDif()) {
			type = -1;// 死叉
		}
		ce.setCrossType(type);
		return type;
	}
	
	

	// 设置背离状态
	private static int setDeviateType(KEntity kEntity,ArrayList<KEntity> list) {
		int type = 0;
		KEntity preEntity = getPreEntity(kEntity,list);
		if (kEntity.getCrossType() == 0 || preEntity == null) {
			;
		} else if (kEntity.getCrossType() == 1) {
			// KEntity preEntity=getPreEntity(kEntity);
			double dea = preEntity.getDea();
			while (dea <= 0.0 && preEntity != null) {
				if (preEntity.getCrossType() == 1) {// 之前出现金叉的K线
					if (isDeviate(preEntity, kEntity,list)) {// 判断是否背离isDeviate
//					if (isMWShape(preEntity, kEntity)) {// 判断是否WMisMWShape
					// System.out.println("1:"+preEntity);
					// System.out.println("2:"+kEntity);
						kEntity.setDevTime(preEntity.getTime());
						type = 1;
						break;
					}
				}
				preEntity = getPreEntity(preEntity,list);
				if (preEntity == null) {
					break;
				} else {
					dea = preEntity.getDea();
				}

			}
		} else if (kEntity.getCrossType() == -1) {
			double dea = preEntity.getDea();
			while (dea >= 0.0 && preEntity != null) {
				if (preEntity.getCrossType() == -1) {
					if (isDeviate(preEntity, kEntity,list)) {// 判断是否背离isDeviate
//					if (isMWShape(preEntity, kEntity)) {// 判断是否WMisMWShape			
					// System.out.println("1:"+preEntity);
					// System.out.println("2:"+kEntity);
						kEntity.setDevTime(preEntity.getTime());
						type = -1;
						break;
					}
				}
				preEntity = getPreEntity(preEntity,list);
				if (preEntity == null) {
					break;
				} else {
					dea = preEntity.getDea();
				}
			}
		}
		kEntity.setDeviateType(type);
		return type;
	}

	// 获得实体最近number根K线的最高价
	public static double getNearCloseHigh(final KEntity kEntity, int number,ArrayList<KEntity> list) {
		double high = kEntity.getHigh();
		KEntity preEntity = getPreEntity(kEntity,list);
		for (int i = number; i >= 0 && preEntity != null; i--) {
			if (high < preEntity.getHigh()) {
				high = preEntity.getHigh();
			}
			preEntity = getPreEntity(preEntity,list);
		}
		return high;
	}

	public static double getNearCloseLow(final KEntity kEntity, int number,ArrayList<KEntity> list) {
		double low = kEntity.getLow();
		KEntity preEntity = getPreEntity(kEntity,list);
		for (int i = number; i >= 0 && preEntity != null; i--) {
			if (low > preEntity.getLow()) {
				low = preEntity.getLow();
			}
			preEntity = getPreEntity(preEntity,list);
		}
		return low;
	}

	public static double getNearDifHigh(final KEntity kEntity, int number,ArrayList<KEntity> list) {
		double high = kEntity.getDif();
		KEntity preEntity = getPreEntity(kEntity,list);
		for (int i = number; i >= 0 && preEntity != null; i--) {
			if (high < preEntity.getDif()) {
				high = preEntity.getDif();
			}
			preEntity = getPreEntity(preEntity,list);
		}
		return high;
	}

	public static double getNearDifLow(final KEntity kEntity, int number,ArrayList<KEntity> list) {
		double low = kEntity.getDif();
		KEntity preEntity = getPreEntity(kEntity,list);
		for (int i = number; i >= 0 && preEntity != null; i--) {
			if (low > preEntity.getDif()) {
				low = preEntity.getDif();
			}
			preEntity = getPreEntity(preEntity,list);
		}
		return low;
	}

	// 判断是否背离，这里判断背离并不是严格意义上的，二十以第二波的DIF比第一波更靠近零轴为标准，
	//这里对于价格和DIF同时靠近零轴的情况无法判断
	private static boolean isDeviate(KEntity preEntity, KEntity kEntity,ArrayList<KEntity> list) {
		boolean isDeviate = false;
		final int number = 5;
		// (REF(CLOSE,A1+1)>=CLOSE OR 上次低价>=最近低价) AND (DIFF>REF(DIFF,A1+1) OR
		// 最近低DIF>=上次低DIF);
		// (REF(CLOSE,A2+1)<=CLOSE OR 上次高价<=最近高价) AND (REF(DIFF,A2+1)>DIFF OR
		// 上次高DIF>=最近高DIF)
		boolean 条件11 = preEntity.getClose() >= kEntity.getClose()
				|| getNearCloseLow(preEntity, number,list) >= getNearCloseLow(
						kEntity, number,list);
		boolean 条件12 = preEntity.getDif() <= kEntity.getDif()
				|| getNearDifLow(preEntity, number,list) <= getNearDifLow(kEntity,
						number,list);
		boolean 条件13 = 是否三重结构(kEntity,list);//三重的时候不需要比较价格，只需要比较DIF值就可以
		boolean 条件14 = preEntity.getDif() <= kEntity.getDif();
		boolean 条件21 = preEntity.getClose() <= kEntity.getClose()
				|| getNearCloseHigh(preEntity, number,list) <= getNearCloseHigh(
						kEntity, number,list);
		boolean 条件22 = preEntity.getDif() >= kEntity.getDif()
				|| getNearDifHigh(preEntity, number,list) >= getNearDifHigh(kEntity,
						number,list);
		boolean 条件23 = 条件13;
		boolean 条件24 =  preEntity.getDif() >= kEntity.getDif();
		if ((条件11&&条件12 ||条件13&&条件14)&&kEntity.getCrossType() == 1) {//底结构，首先判断是否金叉
			isDeviate = true;
		} else if ((条件21&&条件22 ||条件23&&条件24)&&kEntity.getCrossType() == -1) {
			isDeviate = true;
		} else {
			isDeviate = false;
		}
		return isDeviate;
	}
	
	private static boolean 是否三重结构(KEntity crossEntity,ArrayList<KEntity> list){
		int index=crossEntity.getPreIndex();
		int crossNum=1;//包括index处的交叉，一共有三次同向交叉，且index处是结构
		for(int i=index;i>0;i--){
			KEntity tEntity=list.get(i);
			if(tEntity.getDea()*crossEntity.getDea()>0){
				if(tEntity.getCrossType()==crossEntity.getCrossType()){
					crossNum++;
					if(crossNum>=3){
//						System.out.println("MyMACD333:"+crossEntity);
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
//		return false;
		
	}
	
	// 判断是否MW，这种对M或W形态的判断，比上面的结构判断条件更松
	private static boolean isMWShape(KEntity preEntity, KEntity kEntity,ArrayList<KEntity> list) {
		boolean isMWShape = false;
		//对于结构而言，需要有一定的时间周期
		int indexDev=kEntity.getPreIndex()-preEntity.getPreIndex();

		final int number = 5;
		// (REF(CLOSE,A1+1)>=CLOSE OR 上次低价>=最近低价) AND (DIFF>REF(DIFF,A1+1) OR
		// 最近低DIF>=上次低DIF);
		// (REF(CLOSE,A2+1)<=CLOSE OR 上次高价<=最近高价) AND (REF(DIFF,A2+1)>DIFF OR
		// 上次高DIF>=最近高DIF)
		
//		boolean 条件1 = preEntity.getClose() >= kEntity.getClose()
//				|| getNearCloseLow(preEntity, number) >= getNearCloseLow(
//						kEntity, number);
		boolean 条件2 = preEntity.getDif() <= kEntity.getDif()
				|| getNearDifLow(preEntity, number,list) <= getNearDifLow(kEntity,
						number,list);
//		boolean 条件3 = preEntity.getClose() <= kEntity.getClose()
//				|| getNearCloseHigh(preEntity, number) <= getNearCloseHigh(
//						kEntity, number);
		boolean 条件4 = preEntity.getDif() >= kEntity.getDif()
				|| getNearDifHigh(preEntity, number,list) >= getNearDifHigh(kEntity,
						number,list);

				
		//与背离的判断差别就在于价格的判断，为了更严格，一般都要保证第二次交叉比第一次更接近0轴
		if (indexDev>=15&&条件2&&kEntity.getCrossType() == 1) {
			isMWShape = true;
		} else if (indexDev>=15&&条件4&&kEntity.getCrossType() == -1) {
			isMWShape = true;
		} else {
			isMWShape = false;
		}
		return isMWShape;
	}
	

//	//单个品种K线历史数据，时间框架为min
//	public static void kHistoryData(String name, int min) {
//		//已经加载过就不需要处理
//		if(AlertUtil.getListFromMap(数据抓取.getCodeName(name)+min)!=null){
//			return;
//		}
//		ArrayList<KEntity> list = new ArrayList<KEntity>();
//		if(min==3){
//			list=handleThreeMinData(name,min);
//		}else if(min==3600){
//			String[] data = 数据抓取.抓新浪取历史数据(name, min).replace("\"", "").replace("[",
//					"").split(";");
//			name=数据抓取.getCodeName(name);
////			ArrayList<KEntity> list = new ArrayList<KEntity>();
//			for (int i = 0; i <=data.length-1; i++) {
////				System.out.println(data[i]);
//				int r=handData(data[i],name,min,list);
//				if(r==0){//无效数据
//					break;
//				}
//			}
//
//		}else{
//			String[] data = 数据抓取.抓新浪取历史数据(name, min).replace("\"", "").replace("[",
//					"").split(";");
//			name=数据抓取.getCodeName(name);
////			ArrayList<KEntity> list = new ArrayList<KEntity>();
//			for (int i = data.length-1; i >=0; i--) {
////				System.out.println("-------");
//				int r=handData(data[i],name,min,list);
//				if(r==0){
//					break;
//				}
//			}
//		}
//		
//		if(list.size()>0){
//			AlertUtil.putListToMap(list.get(0).getName() + list.get(0).getMin(), list);
//		}
//		
//		for (int i = 0; i < list.size(); i++) {
//			setMACD(list.get(i),list, 12, 26, 9);
//		}
//		
//
//
//	}
	
//	//因为新浪历史数据接口，日线数据和其他时间框架数据的前后顺序不一样，所以遍历一个从前到后
//	private static int handData(String data,String name,int min,ArrayList<KEntity> list){
//		
//		String[] infos = data.split(",");
//		if(infos[0].length()<11){
//			infos[0]=infos[0]+" 00:00:00";
//		}
//		//如果现在是15:00-00:00，则取日期为今天且时间为15:00:00之前的数据
//		//如果现在是00:00-15:00，则取日期为前一天，且时间为15:00之前的数据
//		int hour=时间工具.获得现在小时();
//		String stopTime=时间工具.获得今日日期()+" 15:00:00";
//		if(hour<15){
//			stopTime=时间工具.取得前一交易日期()+" 15:00:00";
//		}else{
//			stopTime=时间工具.获得今日日期()+" 15:00:00";
//		}
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
//        try {
//			Date skipTime = sdf.parse(stopTime);
//			Date dataTime = sdf.parse(infos[0]);
//			if(dataTime.getTime()>skipTime.getTime()){
//				return 0;
//			}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		KEntity kEntity = new KEntity();
//		kEntity.setName(name);
//		kEntity.setMin(min);
//		kEntity.setTime(infos[0]);
//		kEntity.setOpen(Double.parseDouble(infos[1]));
//		kEntity.setHigh(Double.parseDouble(infos[2]));
//		kEntity.setLow(Double.parseDouble(infos[3]));
//		kEntity.setClose(Double.parseDouble(infos[4]));
//		kEntity.setPreIndex(list.size()-1);
//		list.add(kEntity);
//		return 1;
//	}

//	//加载所有品种的历史数据
//	public static void loadHistoryData() {
//		String[] names=配置文件.getItem("crawlList", "").split(",");
//		for(int i=0;i<names.length;i++){
//			kHistoryData(names[i],5);
//			kHistoryData(names[i],15);
//			kHistoryData(names[i],30);
//			kHistoryData(names[i],60);
//			kHistoryData(names[i],3600);
//		}
//	}
//	
	
//	private static ArrayList<KEntity> handleThreeMinData(String urlCodeName,int min){
//		ArrayList<KEntity> list=new ArrayList<KEntity>();
//		String codeName=数据抓取.getCodeName(urlCodeName);
//		
//		String preStorePath=数据抓取.getPreStorePath(urlCodeName);
//		
//		String hisFile=配置文件.获取配置项("crawl.path")+"/his/"+codeName+"_his_"+min+".txt";
//		if(new File(hisFile).exists()){
////			System.out.println(hisFile+"K线历史文件存在，导入K线历史数据");
//			list=数据抓取.大富翁K线导入(codeName,min);
//		}else{
//			File file=new File(preStorePath);
//			if(file.exists()){
////				System.out.println(preStorePath+"原始数据文件存在，导入原始历史数据");
//				list=数据抓取.数据转换(数据抓取.大富翁分时数据导入(preStorePath,0),min);
//			}else{
////				System.out.println("今天之前的历史K线和历史原始数据文件都不存在----");
//				//list是空列表，只处理当天的数据
//			}
//		}
//		
//		return list;
//
//	}
	
	
//	public static void 实时计算MACD() {
//		// TODO Auto-generated method stub
//		if(配置文件.获取配置项("macdalert")!=null&&配置文件.获取配置项("macdalert").equals("true")){
//			System.out.println("MACD预警程序启动，开始计算MACD...");
////			System.out.println("预警品种："+配置文件.获取配置项("alertList"));
////			System.out.println("预警时间："+配置文件.获取配置项("alertMins"));
//			NewThread runThread=new NewThread(NewThread.ThreadType.MACD预警线程);
//			new Thread(runThread).start();
//		}
//
//	}
	

	

	
	private static int 头肩形态右肩顶底(ArrayList<KEntity> list) {
		// TODO Auto-generated method stub
		int jType=0;
		KEntity entity=list.get(list.size()-1);
		int cType=entity.getCrossType();
		if(cType==-1){
			for(int i=list.size()-1;i>0;i--){
				KEntity tempEntity=list.get(i);
				if(tempEntity.getDeviateType()==1){//如果死叉之前先遇到的是金叉结构，则该死叉不是右肩顶
					return 0;
				}
				if(tempEntity.getCrossType()==-1&&tempEntity.getDeviateType()==0){//上一次死叉不是结构，在判断头肩结构的时候，之前的结构DIF可以不是背离状态，因此需要修改
					return 0;
				}
				if(tempEntity.getDeviateType()==-1){
					if(Math.abs(tempEntity.getDif())>Math.abs(entity.getDif())){
						return 0;
					}
					return -1;
				}
			}
		}
		if(cType==1){
			for(int i=list.size()-1;i>0;i--){
				KEntity tempEntity=list.get(i);
				if(list.get(i).getDeviateType()==-1){//如果死叉之前先遇到的是金叉结构，则该死叉不是右肩顶
					return 0;
				}
				if(tempEntity.getCrossType()==1&&tempEntity.getDeviateType()==0){//上一次死叉不是结构，在判断头肩结构的时候，之前的结构DIF可以不是背离状态，因此需要修改
					return 0;
				}
				if(list.get(i).getDeviateType()==1){
					if(Math.abs(tempEntity.getDif())>Math.abs(entity.getDif())){
						return 0;
					}
					return 1;
				}
			}
		}

		return jType;
	}

//	public static void 打印列表(ArrayList<KEntity> list,String 标志){
//		int i=0;
//		if(list.size()>20){
//			i=list.size()-20;
//		}
//		for(;i<list.size();i++){
//			System.out.println(list.get(i));
//		}
//		System.out.println(标志);
//	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		caculateMACD("RM1701",3);
		
//		long start=时间工具.获得毫秒时间();
//		caculateNewMACD("I1701",30);
//		long end=时间工具.获得毫秒时间();
//		System.out.println("传统："+(end-start));

//		日志工具.fileTrack.info("结构预警\tCF1701\t5\t2016-09-29");
//		long start=Calendar.getInstance().getTimeInMillis();
//		loadHistoryData();
//		System.out.println(start=Calendar.getInstance().getTimeInMillis()-start);

		
		

	}



}
