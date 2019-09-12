package com.example.future.macd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.future.tools.PageUtil;
import com.example.future.tools.文件处理工具;
import com.example.future.tools.时间工具;
import com.example.future.tools.配置文件;

public class AlertUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlertUtil.class);
//	 key="品种名称name+时间框架类别type"，如"M03600"指的是豆粕连续的日线实体
	private static HashMap<String, ArrayList<KEntity>> kEntityListMap = new HashMap<String, ArrayList<KEntity>>();
	private static HashMap<String,KEntity> kEntityMap=new HashMap<String,KEntity>();
	public static HashMap<String,String> nameMap=new HashMap<String,String>();
	public static HashMap<String,String> urlNameMap=new HashMap<String,String>();
	public static HashMap<String,Integer> 可开仓位=new HashMap<String,Integer>();
	public static HashMap<String,Double> 商品R值=new HashMap<String,Double>();
	
	
	public static Double 获得商品R值(String codeName){
		if(商品R值.size()==0){
			try {
				String dateTime=时间工具.取的格式化时间("yyyyMMdd");
				String path=配置文件.获取配置项("run_path").replace("run.bat", "")+"\\log\\analy"+dateTime+".csv";
//				if(!new File(path).exists()){
//					
//				}
				ArrayList<String> list=文件处理工具.读取文本数据(path);
				for(int i=1;i<list.size();i++){
//					System.out.println(list.get(i));
					String[] lineInfos=list.get(i).split(",");
					商品R值.put(lineInfos[1], Double.valueOf(lineInfos[7])*0.9);//比1R值稍小
//					System.out.println(lineInfos[1]+":"+lineInfos[7]);
				}

			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage(),e);
			}
		}
		return 商品R值.get(codeName);
	}
	
	public static int 获得可开仓位(String name){
		if(可开仓位.size()==0){
			可开仓位.put("bu1706", 15);
			可开仓位.put("ru1705", 1);
			可开仓位.put("MA705", 17);
			可开仓位.put("TA705", 18);
			可开仓位.put("hc1705", 10);
			可开仓位.put("rb1705", 11);
			可开仓位.put("i1705", 5);
			可开仓位.put("FG705", 18);
			可开仓位.put("jm1705", 3);
			可开仓位.put("j1705", 1);
			可开仓位.put("ZC705", 7);
			可开仓位.put("RM705", 20);
			可开仓位.put("m1705", 14);
			可开仓位.put("a1705", 10);
			可开仓位.put("p1705", 6);
			可开仓位.put("y1705", 6);
			可开仓位.put("OI705", 5);
			可开仓位.put("CF705", 5);
			可开仓位.put("SR705", 7);
			可开仓位.put("c1705", 28);
			可开仓位.put("cs1705", 22);
			可开仓位.put("jd1705", 10);
			可开仓位.put("l1705", 9);
			可开仓位.put("pp1705", 10);
			可开仓位.put("v1705", 13);
		}
		
		return 可开仓位.get(name);
		
	}
	
	//codeName to URLName
	public static String getUrlCodeName1(String key){
		if(urlNameMap.size()==0){
			urlNameMap.put("RM701","RM1701");
			urlNameMap.put("MA701","MA1701");
			urlNameMap.put("ZC701","ZC1701");
			urlNameMap.put("CF701","CF1701");
			urlNameMap.put("FG701","FG1701");
			urlNameMap.put("TA701","TA1701");
			urlNameMap.put("SR701","SR1701");
			urlNameMap.put("OI701","OI1701");
		}
		if(urlNameMap.get(key)==null){
			urlNameMap.put(key, key.toUpperCase());
		}
		return urlNameMap.get(key);
	}
	
	public static String getUrlCodeName(String key){
		if(key.indexOf("RM")!=-1||key.indexOf("MA")!=-1||key.indexOf("ZC")!=-1||key.indexOf("CF")!=-1||key.indexOf("FG")!=-1||key.indexOf("TA")!=-1||key.indexOf("SR")!=-1||key.indexOf("OI")!=-1){
			return key.replace("RM", "RM1").replace("MA", "MA1").replace("ZC", "ZC1").replace("CF", "CF1").replace("FG", "FG1").replace("TA", "TA1").replace("SR", "SR1").replace("OI", "OI1");
		}else{
			return key.toUpperCase();
		}
//		return urlNameMap.get(key);
	}
	
	public static String 获得主力合约代码(String code){
		 String pat="\\d+";
		 Pattern p=Pattern.compile(pat);
		 Matcher m=p.matcher(code);
		 String key=m.replaceAll("");
//		if(key.indexOf("RM")!=-1||key.indexOf("MA")!=-1||key.indexOf("ZC")!=-1||key.indexOf("CF")!=-1||key.indexOf("FG")!=-1||key.indexOf("TA")!=-1||key.indexOf("SR")!=-1||key.indexOf("OI")!=-1){
//			return key.replace("RM", "RM1").replace("MA", "MA1").replace("ZC", "ZC1").replace("CF", "CF1").replace("FG", "FG1").replace("TA", "TA1").replace("SR", "SR1").replace("OI", "OI1");
//		}else{
//			return key.toUpperCase()+"0";
//		}
		return key.toUpperCase()+"0";
//		return urlNameMap.get(key);
	}
	
	//URLName to codeName
	public static String getCodeName1(String key){
		if(nameMap.size()==0){
			nameMap.put("RM1701", "RM701");
			nameMap.put("MA1701", "MA701");
			nameMap.put("ZC1701", "ZC701");
			nameMap.put("CF1701", "CF701");
			nameMap.put("FG1701", "FG701");
			nameMap.put("TA1701", "TA701");
			nameMap.put("SR1701", "SR701");
			nameMap.put("OI1701", "OI701");
		}
		if(nameMap.get(key)==null){
			nameMap.put(key, key.toLowerCase());
		}
		return nameMap.get(key);
	}
	
	public static String getCodeName(String key){
		if(key.indexOf("RM")!=-1||key.indexOf("MA")!=-1||key.indexOf("ZC")!=-1||key.indexOf("CF")!=-1||key.indexOf("FG")!=-1||key.indexOf("TA")!=-1||key.indexOf("SR")!=-1||key.indexOf("OI")!=-1){
			return key.replace("RM1", "RM").replace("MA1", "MA").replace("ZC1", "ZC").replace("CF1", "CF").replace("FG1", "FG").replace("TA1", "TA").replace("SR1", "SR").replace("OI1", "OI");
		}else{
			return key.toLowerCase();
		}
	}
	
	public static boolean isDayGood(String fileName){
		return fileName.substring(0, 2).equals("c1")||fileName.substring(0, 2).equals("cs")||fileName.substring(0, 2).equals("pp")||fileName.substring(0, 2).equals("l1")||fileName.substring(0, 2).equals("jd")||fileName.substring(0, 2).equals("v1");
	}
	
	public static ArrayList<File> getFilesArray(){
		String[] tp={"zc","dc","sc"};
		ArrayList<File> fileList=new ArrayList<File>();
		String alertList=配置文件.获取配置项("alertList");
		for(int t=0;t<tp.length;t++){
			int hour=时间工具.获得现在小时();
			if(hour<15){//在15点之前，要加载当天的数据
				File[] tempFiles=new File(配置文件.获取配置项("crawl.path")+"/"+tp[t]+"/"+时间工具.取的格式化时间("yyyyMMdd")).listFiles();
				for(int i=0;i<tempFiles.length;i++){
					if(alertList.indexOf(tempFiles[i].getName().substring(0, tempFiles[i].getName().indexOf("_")))!=-1){
						fileList.add(tempFiles[i]);
					}
					
				}
			}else{//加载下一个工作日，通过列表寻找今天之后的文件夹,周六周日加载后一天的，测试时有效，实际运行时周末不加载
//				System.out.println("DD:"+配置文件.获取配置项("crawl.path")+"/"+tp[t]);
				File[] tempFiles=new File(配置文件.获取配置项("crawl.path")+"/"+tp[t]).listFiles();
				for(int k=0;k<tempFiles.length;k++){
					if(Integer.valueOf(tempFiles[k].getName())>Integer.valueOf(时间工具.取的格式化时间("yyyyMMdd"))){
						File[] tfiles=new File(tempFiles[k].getAbsolutePath()+"/").listFiles();
						for(int i=0;i<tfiles.length;i++){
							if(alertList.indexOf(tfiles[i].getName().substring(0, tfiles[i].getName().indexOf("_")))!=-1){
								fileList.add(tfiles[i]);
							}
						}
						break;
					}
				}
				
			}
			
		}
		return fileList;
	}
	
	public static synchronized ArrayList<KEntity> getListFromMap(String key){
		return kEntityListMap.get(key);
	}
	
	public static synchronized void putListToMap(String key,ArrayList<KEntity> list){
		kEntityListMap.put(key, list);
	}
	
	public static synchronized HashMap<String, ArrayList<KEntity>> getKentityListMap(){
		return kEntityListMap;
	}
	
	
	public static synchronized KEntity getEntityFromMap(String key){
		return kEntityMap.get(key);
	}
	
	public static synchronized void putEntityToMap(String key,KEntity entity){
		kEntityMap.put(key, entity);
	}
	
	 public static void zip(String zipName,File file) throws IOException  
	    {  
		 File zipFile=new File(zipName);
		 if(zipFile.exists()){
			 return;
		 }
		 LOGGER.info("正在压缩备份:"+file.getAbsolutePath());  
	        ZipOutputStream zo =   
	                new ZipOutputStream(new FileOutputStream(new File(zipName)));//全局公用一个 ZipOutputStrea对象!  
	          
	        zip(zipName,file,zo);  
	        zo.close();  
	        LOGGER.info("压缩备份完毕。");  
	    }  
	    public static void zip(String zipName, File file,ZipOutputStream zo)   
	            throws IOException  
	    {  
	        if(!file.isDirectory())  
	        {  
	            BufferedInputStream bufi =   
	                    new BufferedInputStream(new FileInputStream(file));  
	            ZipEntry ze = new ZipEntry(file.getName());  
	            zo.putNextEntry(ze);// 开始写入新的 ZIP 文件条目并将流定位到条目数据的开始处。  
	            int len = 0;  
	            byte[] b = new byte[1024];  
//	            System.out.println("正在压缩....");  
	            while ((len = bufi.read(b)) != -1)  
	            {  
	                zo.write(b, 0, len);// 将读取的字节数组写入压缩文件中  
	            }  
//	            System.out.println("压缩完成!");  
	            zo.closeEntry();  
	        }  
	        else  
	        {  
	            File[] files = file.listFiles();//遍历  
	            for(File fi : files)  
	            {  
	                zip(zipName,fi,zo);//递归调用  
	            }  
	        }  
	    }  
	
	public static void 打印列表(ArrayList<KEntity> list,String 标志){
		int i=0;
//		if(list.size()>20){
//			i=list.size()-20;
//		}
		for(;i<list.size();i++){
			System.out.println(list.get(i));
		}
		System.out.println(标志);
	}
	
	public static void 打印信息(String info,String className,String methodName){
		if(配置文件.获取配置项("isdebug")!=null&&配置文件.获取配置项("isdebug").equals("true")){
			System.out.println(info+"\t"+className+"\t"+methodName);
		}
	}
	
	public static void 打印信息(String info){
		if(配置文件.获取配置项("isdebug")!=null&&配置文件.获取配置项("isdebug").equals("true")){
			System.out.println(info);
		}
	}
	
	public static void 打印列表(){
		
	}
	
	public static void sleepIfNotNightWorkTime(){
		int hour=时间工具.获得现在小时();
		int week=时间工具.获得今天星期几();
		int n=0;
		while(hour>=15&&hour<21||week==6||week==0){
			if(n==0){
				LOGGER.info("非工作时间，等待开盘...");
			}
			n++;
			时间工具.休眠秒数(10);
			hour=时间工具.获得现在小时();
			week=时间工具.获得今天星期几();
		}
	}
	
	public static void sleepIfNotDayWorkTime(){
		int hour=时间工具.获得现在小时();
		int week=时间工具.获得今天星期几();
		int n=0;
		while(hour>=15||hour<9||week==6||week==0){
			if(n==0){
				LOGGER.info("白天数据，等上午开盘时加载...");
			}
			n++;
			时间工具.休眠秒数(10);
			hour=时间工具.获得现在小时();
			week=时间工具.获得今天星期几();
		}
	}
	
	/**
	 * 从新浪股票接口抓取K线数据，返回的数据需要判断是否为null或包含"ERROR"字符串,或返回的是空字符串
	 * 抓取前一天及之前的数据，当天的数据通过大富翁数据接口提供
	 * @param codeList
	 * @return
	 */
	public static String 抓新浪取历史数据(String code,int min) {
		String url = "http://stock2.finance.sina.com.cn/futures/api/json.php/IndexService.getInnerFuturesMiniKLine"+min+"m?symbol="+code;
		if(min==3600){
			url="http://stock2.finance.sina.com.cn/futures/api/json.php/IndexService.getInnerFuturesDailyKLine?symbol="+code;
			//[["2018-11-27","5828.000","5830.000","5382.000","5382.000","471124"],["2018-11-28","5300.000","5314.000","5142.000","5184.000","1027200"]]
			//[["2018-11-27","5828.000","5830.000","5382.000","5382.000","471124"],["2018-11-28","5300.000","5314.000","5142.000","5184.000","1027200"]]
		}
		if(min==0){
			url="http://hq.sinajs.cn/list="+code.toUpperCase();
			//varhq_str_M0="豆粕连续,145958,3170,3190,3145,3178,3153,3154,3154,3162,3169,1325,223,1371608,1611074,连,豆粕,2013-06-28";
			//varhq_str_M0="豆粕连续,145958,开盘价[index=2],最高价[index=3],最低价[index=4],3178,3153,最新价[index=7],3154,3162,3169,1325,223,1371608,成交量[index=14],连,豆粕,2013-06-28";
			//hq_str_MA0="郑醇连续,232954,2456.00,2460.00,2444.00,2455.00,2448.00,2449.00,2449.00,2452.00,2465.00,166,71,1180954,380302,郑,郑醇,2019-05-10,0,2483.000,2361.000,2483.000,2361.000,2558.000,2361.000,2693.000,2361.000,45.618";
		}
//		LOGGER.info("url:"+url);
//		HttpURLConnection httpConnection = 网页工具.get(url);
		String content = "";
		try {
			content = PageUtil.httpGet(url);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		if(content.indexOf("null")!=-1||content.indexOf("ERROR")!=-1||content.length()==0){
			return "ERROR";
		}
		return content.replace("[[", "[").replace("]]", "]").replace("],[", "];[");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			String zipName=配置文件.获取配置项("crawl.path")+"/his"+时间工具.取的格式化时间("yyyyMMdd")+".zip";
//			String sourceFile=配置文件.获取配置项("crawl.path")+"/his";
//			AlertUtil.zip(zipName, new File(sourceFile));
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
		
//		System.out.println(获得商品R值("i1705"));
		
		String test="Hx   ddd  ddd iee   ee   ";
		System.out.println(test.trim().replaceAll("\\s+", " "));
		
		System.out.println(抓新浪取历史数据("",5));
		
	}

}
