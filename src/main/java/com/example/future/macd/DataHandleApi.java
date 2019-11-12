package com.example.future.macd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.future.config.FutureConfig;
import com.example.future.tools.HttpUtils;
import com.example.future.tools.JsonUtils;
import com.example.future.tools.时间工具;

/**
 * 实时行情信息抓取类
 * 
 * @author content
 * @version 1.0 create at 2012-5-8
 */

// @Component
public class DataHandleApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataHandleApi.class);

	private FutureConfig config;

	public DataHandleApi(FutureConfig config) {
		this.config = config;
	}

	// public static ArrayList<String> futureAlertMain(){
	// DataHandle2 dataHandle2 = new DataHandle2();
	// dataHandle2.clearHistoryData();
	// dataHandle2.loadHistoryData();
	// dataHandle2.loadTodayKEntity();
	// dataHandle2.caculateMACD();
	// return dataHandle2.caculateAlert();
	//
	// }

	public void clearHistoryData() {
		AlertUtil.getKentityListMap().clear();
	}
	
	public void loadHistoryFromMem() {
		
	}

	/**
	 * 
	 * @param min 1M,5M,15M,30M,1H,2H
	 */
	public static void loadHistoryData(String min, int type) {
		LOGGER.info("加载历史K线数据...");
		long start = System.currentTimeMillis();
		JSONArray symbolArray = JsonUtils.readSymbols();
		for(Object s:symbolArray) {
			JSONObject symbol = (JSONObject)s;
			if(symbol.getInteger("Type") == type) {
				LOGGER.info("======================="+symbol.getString("FullSymbol")+ ":" + min + "=======================");
				//生产部署从api接口获取
				JSONArray dataArray = null;
				if(min.equals("1H")) {
					dataArray = getHistoryData(symbol.getString("FullSymbol"),min, 2, 100, 1);
				} else {
					dataArray = getHistoryData(symbol.getString("FullSymbol"),min, 1, 500, 1);
				}
				//开发调试从文件读取
//				dataArray = JsonUtils.readDataArray(symbol.getString("FullSymbol") + min);
//				System.out.println(dataArray);
				LOGGER.info("======================="+symbol.getString("FullSymbol")+ ":" + dataArray.size() + "=======================");
				ArrayList<KEntity> list = new ArrayList<KEntity>();
				if(min.equals("30M") || min.equals("1H") || min.equals("D")) {
					for(int i=dataArray.size()-1;i>=0;i--) {
						JSONObject kdata = (JSONObject)dataArray.get(i);
						String tp=getTpFromName(symbol.getString("Market"));
						KEntity kEntity = new KEntity();
						kEntity.setTP(tp);
						kEntity.setName(symbol.getString("Symbol"));
						if(min.equals("15M")) {
							kEntity.setMin(15);
						}else if(min.equals("1H")) {
							kEntity.setMin(60);
						}else if(min.equals("30M")) {
							kEntity.setMin(30);
						}else if(min.equals("D")) {
							kEntity.setMin(3600);
						}
						
						kEntity.setTime(kdata.getString("D"));
						kEntity.setOpen(Float.parseFloat(kdata.getString("O")));
						kEntity.setHigh(Float.parseFloat(kdata.getString("H")));
						kEntity.setLow(Float.parseFloat(kdata.getString("L")));
						kEntity.setClose(Float.parseFloat(kdata.getString("C")));
						kEntity.setPreIndex(list.size()-1);
						list.add(kEntity);
					}
				} else {
					for(Object k:dataArray) {
						JSONObject kdata = (JSONObject)k;
						String tp=getTpFromName(symbol.getString("Market"));
						KEntity kEntity = new KEntity();
						kEntity.setTP(tp);
						kEntity.setName(symbol.getString("Symbol"));
						if(min.equals("15M")) {
							kEntity.setMin(15);
						}else if(min.equals("1H")) {
							kEntity.setMin(60);
						}else if(min.equals("30M")) {
							kEntity.setMin(30);
						}else if(min.equals("D")) {
							kEntity.setMin(3600);
						}
						
						kEntity.setTime(kdata.getString("D"));
						kEntity.setOpen(Float.parseFloat(kdata.getString("O")));
						kEntity.setHigh(Float.parseFloat(kdata.getString("H")));
						kEntity.setLow(Float.parseFloat(kdata.getString("L")));
						kEntity.setClose(Float.parseFloat(kdata.getString("C")));
						kEntity.setPreIndex(list.size()-1);
						list.add(kEntity);
					}
				}

				if(list.size()>0){
					System.out.println("putlist:" + list.get(0).getName()+list.get(0).getMin());
					AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), list);
				}
//				for(KEntity k:list) {
//					System.out.println(k.getTime());
////					System.out.println(k.getTime() + "[" + k + "]");
//				}
			
			}

		}
		long end = System.currentTimeMillis();
		LOGGER.info("加载历史K线数据完毕。耗时：" + (end -start)  + "毫秒");
	}
	
	/**
	 * 
	 * @param min 30分钟或60分钟
	 */
	public static void mergeData(int min, int type) {
		JSONArray symbolArray = JsonUtils.readSymbols();
		for(Object s:symbolArray) {
			JSONObject symbol = (JSONObject)s;
			if(symbol.getInteger("Type") == type) {
				ArrayList<KEntity> cacheList = AlertUtil.getListFromMap(symbol.getString("Symbol")+min);
				if(cacheList==null) {
					//如果1H数据为空，则先抓取一下数据:
//					if(cacheList == null || cacheList.size() == 0) {
//						//每天加载一次1小时数据
//						loadHistoryData("1H", type);
//						cacheList = AlertUtil.getListFromMap(symbol.getString("Symbol")+min);
//					}
					if(cacheList==null) {
						cacheList = new ArrayList<KEntity>();
					}
					
				}
				ArrayList<KEntity> fiftenList = AlertUtil.getListFromMap(symbol.getString("Symbol")+"15");
				
				//使用15分钟list，合并成半小时或1小时，然后AlertUtil.putListToMap(key, list);
				if(fiftenList != null && fiftenList.size()>0) {
					if(min == 30) {
						ArrayList<KEntity> halfList = new ArrayList<>();
						mergeHalfHourData(fiftenList,halfList);
						AlertUtil.putListToMap(halfList.get(0).getName() + "30", halfList);
					} else if (min == 60) {
						ArrayList<KEntity> hourList = new ArrayList<>();
						mergeHourData(fiftenList, hourList, cacheList);
						AlertUtil.putListToMap(hourList.get(0).getName() + "60", hourList);
					} else {
						LOGGER.warn("不支持的合并类型");
					}
				}
			}
			
		}
	}
	
	
	public static void mergeHalfHourData(ArrayList<KEntity> fiftenList, ArrayList<KEntity> halfHourList) {
		// 进行半小时合并，只需两根两根合并就ok
				// 跳过后面部分k线
				int i = fiftenList.size() - 1;
				for (; i >= 0; i--) {
					if (fiftenList.get(i).getTime().contains("09:00:00")) {
						break;
					}
				}
				
				
				//组装所有历史数据
				for(; i >= 0;) {
					KEntity newKEntity;
					if(i ==0 || fiftenList.get(i).getTime().contains("14:45:00")) {
						newKEntity = mergeMuilt(fiftenList.get(i));
						i -=1;
					}else {
						newKEntity = mergeMuilt(fiftenList.get(i), fiftenList.get(i-1));
						i-=2;
					}
					newKEntity.setPreIndex(halfHourList.size()-1);
					newKEntity.setMin(30);
					halfHourList.add(newKEntity);
				}
	}
	
	public static void mergeHourData(ArrayList<KEntity> fiftenList, ArrayList<KEntity> hourList, ArrayList<KEntity> cacheList) {
		int type = JsonUtils.getType(fiftenList.get(0).getName());
		LOGGER.info("type:" + type);
		if(type ==0) {
			//0:白天品种
			mergeToHourType0(fiftenList, hourList, cacheList);
		} else if (type ==1) {
			//1:23:00品种
			//a.上一天有晚上品种
			//b.上一天无晚上品种
			mergeToHourType1(fiftenList, hourList, cacheList);
		} else {
			//2:23:30品种
			//a.上一天有晚上品种
			//b.上一天无晚上品种
			mergeToHourType2(fiftenList, hourList, cacheList);
		}
	}
	
	//合并白天1小时
	private static void mergeToHourType0(ArrayList<KEntity> fiftenList, ArrayList<KEntity> hourList, ArrayList<KEntity> cacheList) {
		// 进行1小时合并
		// 跳过后面部分k线
		int i = fiftenList.size() - 1;
		for (; i >= 0; i--) {
			if (fiftenList.get(i).getTime().contains("09:00:00")) {
				break;
			}
		}
		

		//从缓存中加载前100小时左右数据
		for(int n=cacheList.size()-1; n>=0; n--) {
			if(cacheList.get(n).getTime().equals(fiftenList.get(i).getTime())) {
				break;
			}else {
				KEntity newEntity = (KEntity)cacheList.get(n).clone();
				newEntity.setPreIndex(hourList.size()-1);
				hourList.add(newEntity);
			}
		}
		
		//组装所有历史数据
		for(; i >= 3;) {
			KEntity newKEntity;
			//14:45:00
			if(fiftenList.get(i-2).getTime().contains("14:45:00")) {
				newKEntity = mergeMuilt(fiftenList.get(i), fiftenList.get(i-1), fiftenList.get(i-2));
				i-=3;
			} else {
				newKEntity = mergeMuilt(fiftenList.get(i), fiftenList.get(i-1), fiftenList.get(i-2), fiftenList.get(i-3));
				i-=4;
			}
			newKEntity.setPreIndex(hourList.size()-1);
			hourList.add(newKEntity);
		}
		//组装最近一根
		if(i>0) {
			KEntity[] kArray=new KEntity[i + 1]; 
			for(; i>=0; i--) {
				kArray[kArray.length-1 -i] = (KEntity)fiftenList.get(i).clone();
			}
			KEntity newKEntity = mergeMuilt(kArray);
			newKEntity.setPreIndex(hourList.size()-1);
			hourList.add(newKEntity);
		}
	}
	
	private static void mergeToHourType1(ArrayList<KEntity> fiftenList, ArrayList<KEntity> hourList, ArrayList<KEntity> cacheList) {
		// 进行1小时合并
		// 跳过后面部分k线
		int i = fiftenList.size() - 1;
		for (; i >= 0; i--) {
			if (fiftenList.get(i).getTime().contains("21:00:00")) {
				break;
			}
		}

		//从缓存中加载前100小时左右数据
		for(int n=cacheList.size()-1; n>=0; n--) {
			if(cacheList.get(n).getTime().equals(fiftenList.get(i).getTime())) {
				break;
			}else {
				KEntity newEntity = (KEntity)cacheList.get(n).clone();
				newEntity.setPreIndex(hourList.size()-1);
				hourList.add(newEntity);
			}
		}
		
		
		//组装所有历史数据
		for(; i >= 3;) {
			KEntity newKEntity;
			//14:45:00
			if(fiftenList.get(i-2).getTime().contains("14:45:00")) {
				newKEntity = mergeMuilt(fiftenList.get(i), fiftenList.get(i-1), fiftenList.get(i-2));
				i-=3;
			} else {
				newKEntity = mergeMuilt(fiftenList.get(i), fiftenList.get(i-1), fiftenList.get(i-2), fiftenList.get(i-3));
				i-=4;
			}
			newKEntity.setPreIndex(hourList.size()-1);
			hourList.add(newKEntity);
		}
		//组装最近一根
		if(i>0) {
			KEntity[] kArray=new KEntity[i + 1]; 
			for(; i>=0; i--) {
//				kArray[i] = fiftenList.get(i);
				kArray[kArray.length-1 -i] = (KEntity)fiftenList.get(i).clone();
			}
			KEntity newKEntity = mergeMuilt(kArray);
			newKEntity.setPreIndex(hourList.size()-1);
			hourList.add(newKEntity);
		}
	}
	
	
	private static void mergeToHourType2(ArrayList<KEntity> fiftenList, ArrayList<KEntity> hourList, ArrayList<KEntity> cacheList) {
		// 进行1小时合并
		// 跳过后面部分k线
		int i = fiftenList.size() - 1;
		for (; i >= 0; i--) {
			if (fiftenList.get(i).getTime().contains("21:00:00")) {
				break;
			}
		}
		//从缓存中加载前100小时左右数据
		for(int n=cacheList.size()-1; n>=0; n--) {
			if(cacheList.get(n).getTime().equals(fiftenList.get(i).getTime())) {
				break;
			}else {
				KEntity newEntity = (KEntity)cacheList.get(n).clone();
				newEntity.setPreIndex(hourList.size()-1);
				hourList.add(newEntity);
			}
		}
		
		//组装所有历史数据
		for(; i >= 3;) {
			KEntity newKEntity;
			//14:45:00
			if(fiftenList.get(i).getTime().contains("14:45:00")) {
				newKEntity = mergeMuilt(fiftenList.get(i));
				i-=1;
			} else if(fiftenList.get(i).getTime().contains("09:00:00")){
				newKEntity = mergeMuilt(fiftenList.get(i), fiftenList.get(i-1));
				newKEntity.setTime(newKEntity.getTime().replace("09:00:00", "23:00:00"));
				
				String time = newKEntity.getTime();
				GregorianCalendar gc = new GregorianCalendar();
				try {
					gc.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gc.add(Calendar.DAY_OF_MONTH, -1);
				newKEntity.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(gc.getTime()));
				i-=2;
			} else {
				newKEntity = mergeMuilt(fiftenList.get(i), fiftenList.get(i-1), fiftenList.get(i-2), fiftenList.get(i-3));
				i-=4;
			}
			newKEntity.setPreIndex(hourList.size()-1);
			newKEntity.setMin(60);
			hourList.add(newKEntity);
		}
		//组装最近一根
		if(i>0) {
			KEntity[] kArray=new KEntity[i + 1]; 
//			int firstIndex = kArray.length-1;
			for(; i>=0; i--) {
				kArray[kArray.length-1 -i] = (KEntity)fiftenList.get(i).clone();
			}
			
			if(kArray[0].getTime().contains("09:00:00")) {
				//剩下的是早上开盘的1根或2根，且上一天没有交易
				kArray[0].setTime(kArray[0].getTime().replace("09:00:00", "23:00:00"));
				
				String time = kArray[0].getTime();
				GregorianCalendar gc = new GregorianCalendar();
				try {
					gc.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					LOGGER.error(e.getMessage());
				}
				gc.add(Calendar.DAY_OF_MONTH, -1);
				kArray[0].setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(gc.getTime()));
				
				KEntity newKEntity = mergeMuilt(kArray);
				newKEntity.setMin(60);
				newKEntity.setPreIndex(hourList.size()-1);
				hourList.add(newKEntity);
				
				if(kArray.length>2) {
					KEntity[] kArray2=new KEntity[kArray.length-2];
					for(int j=kArray.length-3; j>=0; j--) {
						kArray2[j] = (KEntity)kArray[j].clone();
					}
					KEntity newKEntity2 = mergeMuilt(kArray2);
					newKEntity2.setMin(60);
					newKEntity2.setPreIndex(hourList.size()-1);
					hourList.add(newKEntity2);
				}
			}else if(kArray[0].getTime().contains("14:45:00")) {
				//剩下三根，有一根是14:45，和晚上刚开盘的1根或两根
				KEntity newKEntity1 = mergeMuilt(kArray[0]);
				newKEntity1.setMin(60);
				newKEntity1.setPreIndex(hourList.size()-1);
				hourList.add(newKEntity1);
				
				if(kArray.length>1) {
					KEntity[] kArray2=new KEntity[kArray.length-1];
					for(int j=kArray.length-2; j>=0; j--) {
						kArray2[j] = (KEntity)kArray[j].clone();
					}
					KEntity newKEntity2 = mergeMuilt(kArray2);
					newKEntity2.setMin(60);
					newKEntity2.setPreIndex(hourList.size()-1);
					hourList.add(newKEntity2);
				}

				
			}else {
				KEntity newKEntity = mergeMuilt(kArray);
				newKEntity.setMin(60);
				newKEntity.setPreIndex(hourList.size()-1);
				hourList.add(newKEntity);
			}

		}
	}
	
	

	public static void caculateMACD(int min, int type) {
		JSONArray symbolArray = JsonUtils.readSymbols();
		for(Object s:symbolArray) {
			JSONObject symbol = (JSONObject)s;
			if(symbol.getInteger("Type") == type) {
				ArrayList<KEntity> list = AlertUtil.getListFromMap(symbol.getString("Symbol") + min);
				if (list != null && list.size() != 0) {
//					MyMA.setMA(list, 20);
					MyMA.setMA(list, 26);
					MyMACD.setMACD(list);
				}
			}
		}
	}

	public static ArrayList<String> caculateAlert(int min, int type) {
		ArrayList<String> alertCodeList = new ArrayList<>();
		
		JSONArray symbolArray = JsonUtils.readSymbols();
		for(Object s:symbolArray) {
			JSONObject symbol = (JSONObject)s;
			if(symbol.getInteger("Type") == type) {
				ArrayList<KEntity> list = AlertUtil.getListFromMap(symbol.getString("Symbol") + min);
				if (list == null || list.size() == 0) {
					LOGGER.warn(symbol.getString("Symbol") + "数据不存在！");
					// 发送邮件
				} else {
					KEntity todayEntity = list.get(list.size() - 1);
					KEntity preEntity = list.get(list.size() - 2);
					KEntity ppEntity = list.get(list.size() - 3);
					//
					boolean 绿转 = preEntity.getDea() > 0 && preEntity.getMacd() < ppEntity.getMacd()
							&& preEntity.getMacd() < todayEntity.getMacd() && todayEntity.getMacd() < 0;
					boolean 红转 = preEntity.getDea() < 0 && preEntity.getMacd() > ppEntity.getMacd()
							&& preEntity.getMacd() > todayEntity.getMacd() && todayEntity.getMacd() > 0;
					
					//30分钟的，只接受在一侧的预警，如果后续发现1小时的信号也太多，那也采用该限制，尽量做最好的信号
					if(min == 30) {
						绿转 = 绿转 && todayEntity.getClose()>todayEntity.getMAn(26);
						//绿柱数不能超过5根
						int greenNum =0;
						for(int k=list.size()-1;k>0;k--) {
							if(list.get(k).getMacd()<0) {
								greenNum++;
							}else {
								break;
							}
							if(greenNum>6) {
								break;
							}
						}
						绿转 = 绿转 && (greenNum<=6);
						
						红转 = 红转 && todayEntity.getClose()<todayEntity.getMAn(26);
						//红柱数不能超过5根
						int redNum =0;
						for(int k=list.size()-1;k>0;k--) {
							if(list.get(k).getMacd()>0) {
								redNum++;
							}else {
								break;
							}
							if(redNum>6) {
								break;
							}
						}
						红转 = 红转 && (redNum<=6);
					}

					if (绿转 || 红转) {
						LOGGER.info("======================================================================");

						LOGGER.info("品种:" + symbol.getString("Symbol") + " 出现转折:" + (绿转 ? "绿转" : "") + "," + (红转 ? "红转" : ""));

						LOGGER.info("黄线Dea:" + todayEntity.getDea() + ", 柱子（前，昨，今）：" + ppEntity.getMacd() + ","
								+ preEntity.getTime() + ":" + preEntity.getMacd() + "," + todayEntity.getMacd());

						alertCodeList.add(JsonUtils.getName(symbol.getString("Symbol")));

						LOGGER.info("======================================================================");
					}

				}
			}
			
		}

		LOGGER.info("alertCodeList size:" + alertCodeList.size());

		return alertCodeList;
	}


	public static ArrayList<KEntity> delSubList(ArrayList<KEntity> list, int size) {
		ArrayList<KEntity> listNew = new ArrayList<KEntity>();
		if (list.size() > size) {
			for (int i = list.size() - size; i < list.size(); i++) {
				KEntity tempEntity = list.get(i);
				tempEntity.setPreIndex(listNew.size() - 1);
				listNew.add(tempEntity);
			}
		} else {
			for (int i = 0; i < list.size(); i++) {
				KEntity tempEntity = list.get(i);
				tempEntity.setPreIndex(listNew.size() - 1);
				listNew.add(tempEntity);
			}
		}
		
		return listNew;
	}


	//把多根K线合并成1根K线，注意顺序
	private static  KEntity mergeMuilt(KEntity ... args) {
		KEntity newKEntity = (KEntity) args[0].clone();
		for(KEntity k:args) {
			if(newKEntity.getHigh() < k.getHigh()) {
				newKEntity.setHigh(k.getHigh());
			}
			if(newKEntity.getLow() > k.getLow()) {
				newKEntity.setLow(k.getLow());
			}
		}
		newKEntity.setClose(args[args.length-1].getClose());
		return newKEntity;
	}





	public static String getTpFromName(String name) {
		// RM1701,MA1701,ZC1701,CF1701,FG1701,TA1701,SR1701,OI1701,rb1701,ru1701,bu1612,ag1612,au1612,ni1701,hc1701,m1701,c1701,p1701,i1701,l1701,y1701,j1701,pp1701,jm1701,jd1701,cs1701,a1701

		String tp = "dc";
		if ("rb,ru,bu,ag,au,ni,hc,al,cu,sn,zn".indexOf(name.substring(0, 2)) != -1) {
			tp = "sc";
		} else if ("RM1701,MA1701,ZC1701,CF1701,FG1701,TA1701,SR1701,OI1701".indexOf(name.substring(0, 2)) != -1) {
			tp = "zc";
		}
		return tp;
	}
	

	
	/**
	 * @param fiftenList
	 * @param hourList
	 */

	
	//获取实时数据
	public static void getRealTimeData(String symbol) {
	    String host = "http://alirm-com.konpn.com";
	    String path = "/query/com";
	    String method = "GET";
	    String appcode = "354bf12bd6904c0cbc5de7c828aa7fdf";
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    Map<String, String> querys = new HashMap<String, String>();
//	    querys.put("symbol", "CZCEMA2001");
	    querys.put("symbol", symbol);
	    querys.put("withks", "1");
	    //withks=1&withticks=0


	    try {
	    	HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
	    	//System.out.println(response.toString());
	    	//获取response的body
	    	System.out.println(EntityUtils.toString(response.getEntity()));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	//1、获取历史数据,且带最近一次有效数据
	//2、小时线，按照4根15分支合并，半小时按照2根15分钟合并
	//3、时间都是以最新一根为准
	//4、每天启动时，先请求一次第二页的数据，保存起来，使用pidx=2,psize=400,保证与第一页有重叠，避免今天的数据请求时与之前出现中间数据丢失
	//5、每15分钟请求一次，根据不同产品进行预警
	/**
	 * 
	 * @param symbol STRING	必选	品种代码,参考列表
	 * @param period STRING	必选	取 1M,5M,10M,15M,30M,1H,2H,4H,D,W,M。部分品种无W,M
	 * @param pidx INT	必选	页码,排序是从当前往历史方向排,第一页是当前处。接口输出的日周期数据只有最近2年，分钟周期数据只有最近10天(更多历史数据可申请打包下载)。
	 * @param psize INT	可选	每页最多500个数据
	 * @param withlast INT	可选	是否包含最新的一个动态k线数据，第一页有效，1为包含，0为不包含
	 */
	public static JSONArray getHistoryData(String symbol,String period, int pidx, int psize, int withlast) {
	    String host = "http://alirm-com.konpn.com";
	    String path = "/query/comkm";
	    String method = "GET";
	    String appcode = "354bf12bd6904c0cbc5de7c828aa7fdf";
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("symbol", symbol);
	    querys.put("period",period);
	    querys.put("pidx",pidx+"");
	    querys.put("psize",psize+"");
	    querys.put("withlast",withlast+"");
	    //withks=1&withticks=0

	    JSONArray dataArray = new JSONArray();
	    try {
	    	HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
	    	dataArray = (JSONArray)(JSON.parseObject(EntityUtils.toString(response.getEntity())).get("Obj"));
	    	return dataArray;
	    } catch (Exception e) {
	    	LOGGER.error(e.getMessage(), e);
	    }
	    
	    return dataArray;
	}
	
	
	public static void main(String[] args) {

		String testSymbol = "JD2001";
//		int type = 0;
//		
//		ArrayList<KEntity> cacheList = AlertUtil.getListFromMap(testSymbol+60);
//		System.out.println("1.cacheList is null:" + (cacheList == null));
//		if(cacheList == null || cacheList.size() == 0) {
//			//每天加载一次1小时数据
//			loadHistoryData("1H", type);
//		}
//		
//		cacheList = AlertUtil.getListFromMap(testSymbol+60);
//		System.out.println("2.cacheList.size():" + cacheList.size());
//
//		
////		//每15分钟加载一次15分钟数据
//		loadHistoryData("15M", type);
////		
////		System.out.println("***********************");
//		mergeData(60, type);
//		
//		ArrayList<KEntity> cacheList15 = AlertUtil.getListFromMap(testSymbol+15);
//		System.out.println("cacheList15.size():" + cacheList15.size());
//		
////		for(KEntity k:cacheList15) {
////			System.out.println(k.getTime() + "["+k+"]");
////		}
//
//		cacheList = AlertUtil.getListFromMap(testSymbol+60);
//		System.out.println("3.cacheList.size():" + cacheList.size());
////		for(KEntity k:cacheList) {
////			System.out.println(k.getTime() + "["+k+"]");
////		}
//		
//		long start =System.currentTimeMillis();
//		System.out.println("caculateMACD start");
//		caculateMACD(60, type);
//		System.out.println("caculateMACD end " + (System.currentTimeMillis() - start));
//		
//		ArrayList<String> alertList = caculateAlert(60, type);
//		System.out.println("alertList.size():" + alertList.size());
//		for(String a:alertList) {
//			System.out.println(a);
//		}
		
		
//		ArrayList<KEntity> dayList = AlertUtil.getListFromMap(testSymbol+3600);
//		System.out.println("dayList.size():" + dayList.size());
//		
//		for(KEntity k:dayList) {
//			System.out.println(k.getTime() + "["+k+"]");
//		}
		
//		cacheList = AlertUtil.getListFromMap(testSymbol+60);
//		for(KEntity k:cacheList) {
//			System.out.println(k.getTime() + "["+k+"]");
//		}
		
		
		//day
//		int type = 0;
//		loadHistoryData("D", type);
//		caculateMACD(3600, type);
//		ArrayList<String> list = caculateAlert(3600, type);
//		System.out.println("list:" + list);
//		
//		ArrayList<KEntity> dayList = AlertUtil.getListFromMap(testSymbol+3600);
//		for(KEntity k:dayList) {
//			System.out.println(k.getTime() + "[" + k + "]");
//		}
		
		
//		type = 1;
//		loadHistoryData("D", type);
//		caculateMACD(3600, type);
//		list = caculateAlert(3600, type);
//		System.out.println("list:" + list);
		
//		type = 2;
//		loadHistoryData("D", type);
//		caculateMACD(3600, type);
//		list = caculateAlert(3600, type);
//		System.out.println("list:" + list);
		
		int type = 0;
		loadHistoryData("15M", type);
		mergeData(30, type);
		caculateMACD(30, type);
		ArrayList<String> list = caculateAlert(30, type);
		System.out.println("list:" + list);
		
		ArrayList<KEntity> dayList = AlertUtil.getListFromMap(testSymbol+30);
		for(KEntity k:dayList) {
			System.out.println(k.getTime() + "[" + k + "]");
		}
		
	}

}
