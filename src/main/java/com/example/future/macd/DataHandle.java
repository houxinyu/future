package com.example.future.macd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.future.tools.MailTool;
import com.example.future.tools.文件处理工具;
import com.example.future.tools.时间工具;
import com.example.future.tools.配置文件;

/**
 * 实时行情信息抓取类
 * 
 * @author content
 * @version 1.0 create at 2012-5-8
 */

public class DataHandle {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataHandle.class);
	
	//记录已经经过“加载当天大富翁数据”加载的那些品种
	public static HashSet<String> loadFileSet=new HashSet<String>();
	
	//修改tp和day的值，就可以处理所有数据
	public static void 大富翁历史数据处理(){

		long start=时间工具.获得现在时间();
//		System.out.println("大富翁历史数据处理，请稍后（几分钟）...");
		LOGGER.info("大富翁历史数据处理，请稍后（几分钟）...");
		try {
			int hour=时间工具.获得现在小时();
			int week=时间工具.获得今天星期几();
			if(hour>=15&&hour<21&&week!=0&&week!=6){
				String zipName=配置文件.获取配置项("crawl.path")+"/his"+时间工具.取的格式化时间("yyyyMMdd")+".zip";
				String sourceFile=配置文件.获取配置项("crawl.path")+"/his";
				AlertUtil.zip(zipName, new File(sourceFile));
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(),e);
		}
		处理历史日线数据();
		处理历史周线数据();
		//D:\md_future\temp\zc\20160920
		String alertList=配置文件.获取配置项("alertList");
		String crawlPath=配置文件.获取配置项("crawl.path")+"/";
//		String handleDay=时间工具.取的格式化时间("yyyyMMdd");
		String handleDay="20160922";
		String[] tp={"zc","dc","sc"};
		for(int t=0;t<tp.length;t++){
			File[] folder=new File(crawlPath+tp[t]+"/").listFiles();
			for(int h=0;h<folder.length;h++){
				String today=时间工具.取的格式化时间("yyyyMMdd");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
				handleDay=folder[h].getName();
				try {
					int hour=时间工具.获得现在小时();
					int week=时间工具.获得今天星期几();
					if(sdf.parse(today).getTime()<sdf.parse(handleDay).getTime()||today.equals(handleDay)&&hour<15||week==0||week==6){
//						System.out.println("最新数据，暂不处理...handleDay:"+handleDay+" today:"+today);
						
						break;//最新一天的数据，不需要处理；或者是当天的数据，但是时间上不是15点之后，实时数据不要处理成历史数据
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File file=new File(crawlPath+tp[t]+"/"+handleDay);
				File[] files=file.listFiles();
				for(int i=0;i<files.length;i++){
//					long start=时间工具.获得毫秒时间();
					int nMin=1;
					String codeName=files[i].getName().substring(0, files[i].getName().indexOf("_"));
					String file1=crawlPath+tp[t]+"/"+handleDay+"/"+codeName+"_"+handleDay+".csv";
					String file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
						
					if(!codeName.startsWith("b1")&&alertList.indexOf(codeName)!=-1&&是需要处理数据(file2,file1)){
//						System.out.println("新数据"+files[i].getName()+" "+codeName);
							ArrayList<KEntity> list1=大富翁分时数据导入(file1,0);
							ArrayList<KEntity> listTemp;
	
							大富翁K线导出(file2,list1);
							
							nMin=3;
							listTemp=数据转换(list1,nMin);
							file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
							大富翁K线导出(file2,listTemp);
							
							nMin=5;
							listTemp=数据转换(list1,nMin);
							file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
							大富翁K线导出(file2,listTemp);
							
							nMin=15;
							listTemp=数据转换(list1,nMin);
							file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
							大富翁K线导出(file2,listTemp);
							
							nMin=30;
							listTemp=数据转换(list1,nMin);
							file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
							大富翁K线导出(file2,listTemp);
							
							nMin=60;
							listTemp=数据转换(list1,nMin);
							file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
							大富翁K线导出(file2,listTemp);
							
							
							nMin=3600;
							if(Integer.valueOf(handleDay)>=Integer.valueOf(时间工具.取的格式化时间("yyyyMMdd"))){//如果不做判断，可能会把最早的日线数据写入
								listTemp=数据转换(list1,nMin);
								file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
								大富翁K线导出(file2,listTemp);
							}
						
					}else{
						;
					}
//					if(!codeName.startsWith("b1")&&alertList.indexOf(codeName)!=-1){
//						处理周线数据(handleDay,crawlPath,codeName);
//					}
					

				}
			}
			

		}
		long end=时间工具.获得现在时间();
		LOGGER.info("大富翁历史数据处理完毕。耗时："+时间工具.耗时毫秒(start, end)/1000+"秒");
		
	}
	
	private static void 处理周线数据(String handleDay,String crawlPath,String codeName){
		try {
			int nMin=7200;
			//时间工具.getWeek(时间工具.取的格式化时间("yyyy-MM-dd"))
			int week=时间工具.获得今天星期几();
//			if(时间工具.getWeek(handleDay)>时间工具.getWeek(时间工具.取的格式化时间("yyyy-MM-dd"))||week==5){//handleDay的周期大于今天才进行处理
			if(week==5&&时间工具.getWeek(handleDay)>=时间工具.getWeek(时间工具.取的格式化时间("yyyy-MM-dd"))){
//				listTemp=数据转换(list1,nMin);
				LOGGER.info("历史数据处理：处理周线数据..."+codeName);
				//导入日线数据
				ArrayList<KEntity> dayList=大富翁分时数据导入(crawlPath+"his/"+codeName+"_his_"+3600+".txt",0);
				KEntity weekEntity=null;
				ArrayList<KEntity> weekList=new ArrayList<KEntity>();
				for(int k=dayList.size()-1;k>0;k--){
					if(weekEntity==null){
						weekEntity=dayList.get(k);
					}
					KEntity tempEntity=dayList.get(k);
					if(时间工具.getWeek(tempEntity.getTime().substring(0,10))==时间工具.getWeek(weekEntity.getTime().substring(0,10))){
						if(weekEntity.getHigh()<tempEntity.getHigh()){
							weekEntity.setHigh(tempEntity.getHigh());
						}
						if(weekEntity.getLow()>tempEntity.getLow()){
							weekEntity.setLow(tempEntity.getLow());
						}
						weekEntity.setOpen(tempEntity.getOpen());
						weekEntity.setTime(tempEntity.getTime());
					}else{
						weekEntity.setMin(7200);
						weekEntity.setTime(weekEntity.getTime().substring(0,10)+" 00:00:00");
						weekList.add(weekEntity);
						break;
					}
				}
				String file2=crawlPath+"his/"+codeName+"_his_"+nMin+".txt";
				大富翁K线导出(file2,weekList);
			}else{
//				System.out.println("历史数据处理：没有周线数据需要处理...");
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	public static void 加载历史K线数据(){
		LOGGER.info("加载历史K线数据，请稍后(1分钟内)...");
		long start=时间工具.获得现在时间();
		String[] urlCodeNames=配置文件.获取配置项("alertList").split(",");
		for(int i=0;i<urlCodeNames.length;i++){
			urlCodeNames[i]=AlertUtil.getUrlCodeName(urlCodeNames[i]);
		}
//		String[] urlCodeNames=配置文件.获取配置项("crawlList").split(",");
		ArrayList<KEntity> list=new ArrayList<KEntity>();
		for(int i=0;i<urlCodeNames.length;i++){
//			LOGGER.info("加载品种:"+urlCodeNames[i]);
			//不加载1分钟历史数据，1分钟都采用最新实时数据
//			System.out.println(urlCodeNames[i]);
//			list=loadHisMinData(urlCodeNames[i],1);
//			
//			if(list.size()>0){
////				System.out.println("加载："+list.get(0).getName());
//				MyMACD.putListToMap(list.get(0).getName()+list.get(0).getMin(), list);
//			}
			list=loadHisMinData(urlCodeNames[i],3);
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
			}
			list=loadHisMinData(urlCodeNames[i],5);
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
			}
			list=loadHisMinData(urlCodeNames[i],15);
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
			}
			list=loadHisMinData(urlCodeNames[i],30);
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
			}
			list=loadHisMinData(urlCodeNames[i],60);
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
			}
//			if(配置文件.获取配置项("maalert")!=null&&配置文件.获取配置项("maalert").equals("true")){//需要进行日线撑压预警的时候才有必要加载日线数据
//				list=loadHisMinData(urlCodeNames[i],3600);
//				if(list.size()>0){
//					AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
//				}
//			}
			list=loadHisMinData(urlCodeNames[i],3600);
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), delSubList(list,240));
			}
			list=loadHisMinData(urlCodeNames[i],7200);
			if(list.size()>0){
				AlertUtil.putListToMap(list.get(0).getName()+list.get(0).getMin(), list);//周线数据很少，所以无需删除操作
			}

		}
		long end=时间工具.获得现在时间();
		LOGGER.info("加载历史K线数据完毕。耗时："+时间工具.耗时毫秒(start, end)/1000+"秒");
	}

	
	
	
	//加载的是当天1分钟数据
	public static void 同步导入白天数据(){
		AlertUtil.sleepIfNotDayWorkTime();
		同步导入数据(true);
	}
	
	//加载的是当天1分钟数据
	public static void 同步导入晚上数据(){
		
		AlertUtil.sleepIfNotNightWorkTime();
		同步导入数据(false);
	}
	
	
	public static void 同步导入数据(boolean 是白天品种){
		String time="晚上";
		if(是白天品种){
			time="白天";
		}
		long start=时间工具.获得现在时间();
//		System.out.println("加载"+time+"开盘品种分时数据，请稍后（几分钟）...");
		
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		String alertList=配置文件.获取配置项("alertList");
		AlertUtil.sleepIfNotNightWorkTime();
		LOGGER.info("开始同步"+time+"数据...");
		ArrayList<File> fileList=AlertUtil.getFilesArray();
		for(int i=0;i<fileList.size();i++){
//			LOGGER.info("异步导入："+fileList.get(i).getName()+" 是否白天:"+是白天品种);
			boolean 条件1=AlertUtil.isDayGood(fileList.get(i).getName())==false&&是白天品种==false;//晚上正常开盘品种
			boolean 条件2=AlertUtil.isDayGood(fileList.get(i).getName())==true&&是白天品种==true;//白天开盘品种
//			boolean 条件3=AlertUtil.isDayGood(fileList.get(i).getName())==false&&是白天品种==true&&!loadFileSet.contains(fileList.get(i).getAbsolutePath());//节假日晚上开盘的品种无数据，需要等到白天再加载
//			boolean 白天品种=(AlertUtil.isDayGood(fileList.get(i).getName())==false&&是白天品种==false||AlertUtil.isDayGood(fileList.get(i).getName())==true&&是白天品种==true);
			boolean 白天品种=条件1 || 条件2;
			if(alertList.indexOf(fileList.get(i).getName().substring(0, fileList.get(i).getName().indexOf("_")))!=-1&&fileList.get(i).exists()&&白天品种&&!loadFileSet.contains(fileList.get(i).getAbsolutePath())){
//				LOGGER.info("同步导入："+fileList.get(i).getName()+" 是否白天:"+是白天品种);
				entityList=大富翁分时数据导入(fileList.get(i).getAbsolutePath(),0);
				if(entityList.size()>0){
					AlertUtil.putListToMap(entityList.get(0).getName()+1, entityList);
					loadFileSet.add(fileList.get(i).getAbsolutePath());
				}
				//考虑到白天开盘的品种，因为晚上没有数据，如果在9:00之前启动系统，则这些白天的数据无法预警，因此上面的代码有漏洞
			}

		}
		
		long end=时间工具.获得现在时间();
		
//		LOGGER.info("加载数据大小。。。。。:"+loadFileSet.size());
//		for(String key:loadFileSet){
//			LOGGER.info("加载数据："+key);
//		}
		LOGGER.info("加载"+time+"开盘品种分时数据完毕。耗时："+时间工具.耗时毫秒(start, end)/1000+"秒");

	}
	

	//加载的是当天1分钟数据
	public static void 异步导入分时数据(){
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		int hour1=时间工具.获得现在小时();
		int week1=时间工具.获得今天星期几();
		int n=0;
		while(hour1>=15&&hour1<21||hour1>=0&&hour1<9||week1==6||week1==0){
			if(n==0){
				LOGGER.info("非工作时间，开盘后启动异步导入线程...");
			}
			n++;
			时间工具.休眠秒数(10);
			hour1=时间工具.获得现在小时();
			week1=时间工具.获得今天星期几();
		}
		
		ArrayList<File> fileList=AlertUtil.getFilesArray();
		for(int i=0;i<fileList.size();i++){
			//在之前的数据没有加载之前，也不对最新的数据进行处理
			if(loadFileSet.contains(fileList.get(i).getAbsolutePath())){//有同步加载的数据才会进行异步加载合并处理
//				System.out.println("195:"+files[i].getAbsolutePath());
				entityList=lineListToEnityList(文件处理工具.读取文件最后N行(fileList.get(i).getAbsolutePath(),10));
//				if(fileList.get(i).getAbsolutePath().indexOf("m1705")!=-1){
////					System.out.println("异步导入分时数据()新数据条数："+entityList.size());
//					if(entityList.size()>0){
//						System.out.println("合并前:"+entityList.get(entityList.size()-1).getClose());
//					}
//				}
//        		System.out.println("新数据条数："+entityList.size());
        		if(entityList.size()>0){
        			ArrayList<KEntity> oneMinList=AlertUtil.getListFromMap(entityList.get(0).getName()+1);
    				KEntity lastEntity=oneMinList.get(oneMinList.size()-1);
    				for(int j=0;j<entityList.size();j++){
    					KEntity tempEntity=entityList.get(j);
    					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    					Date lastEntityTime;
    					Date tempEntityTime;
    					try {
    						lastEntityTime = sdf.parse(lastEntity.getTime().substring(0, 16)+":00");
    						tempEntityTime=sdf.parse(tempEntity.getTime().substring(0, 16)+":00");
    						if(lastEntityTime.getTime()<tempEntityTime.getTime()){
    							oneMinList.add(tempEntity);//在后面增加一条分钟K线数据
//    							System.out.println("新增："+tempEntity);
    						}else if(lastEntity.getTime().substring(0, 16).equals(tempEntity.getTime().substring(0, 16))){//时间相同的时候重新设置价格，但不新增数据
//    							if(tempEntity.getHigh()<lastEntity.getHigh()){
//    								tempEntity.setHigh(lastEntity.getHigh());
//    							}
//    							if(tempEntity.getLow()>lastEntity.getLow()){
//    								tempEntity.setLow(lastEntity.getLow());
//    							}
//    							tempEntity.setTime(lastEntity.getTime());
//    							tempEntity.setOpen(lastEntity.getOpen());
//    							tempEntity.setPreIndex(lastEntity.getPreIndex());
//    							lastEntity=tempEntity;//覆盖掉原来的
    							
    							if(tempEntity.getHigh()>lastEntity.getHigh()){
    								lastEntity.setHigh(tempEntity.getHigh());
    							}
    							if(tempEntity.getLow()<lastEntity.getLow()){
    								lastEntity.setLow(tempEntity.getLow());
    							}
//    							tempEntity.setTime(lastEntity.getTime());
    							lastEntity.setClose(tempEntity.getClose());//这样就会改变oneMinList里面最后一条记录的值
//    							tempEntity.setPreIndex(lastEntity.getPreIndex());
//    							lastEntity=tempEntity;//覆盖掉原来的
    						}
    					} catch (ParseException e) {
    						// TODO Auto-generated catch block
    						LOGGER.error(e.getMessage(), e);
    					}

    				}
//    				System.out.println("2.DataHandle.加载当天大富翁数据():"+entityList.get(0).getName());
//    				if(fileList.get(i).getAbsolutePath().indexOf("m1705")!=-1){
////    					System.out.println("异步导入分时数据()新数据条数："+entityList.size());
//    					if(oneMinList.size()>0){
//    						System.out.println("合并后:"+oneMinList.get(oneMinList.size()-1).getClose());
//    					}
//    					System.out.println();
//    				}
    				AlertUtil.putListToMap(entityList.get(0).getName()+1, oneMinList);
        		}
			}else{
//				System.out.println(files[i].getAbsolutePath()+"还未加载");
			}

		}
//		System.out.println("166:================================================");


	}
	


	//把大富翁推送的实时数据导入内存，并转换成1分钟K线数据，last
	public static ArrayList<KEntity> 大富翁分时数据导入(String filePath,long lastMin){
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		try {
			File f= new File(filePath);  
			if(f.length()==0){
				return entityList;
			}
//			System.out.println("文件长度："+f.length()+" seek:"+(f.length()-127*60*lastMin));
			RandomAccessFile rAfile=new RandomAccessFile(filePath,"r");
			if(lastMin==0||f.length()<127*10){//开盘前5分钟读取所有数据,即使lastMin不为0，也读取全部
				;//读取所有数据
			}else{
				seek(rAfile,10);//读取最后10行
			}
//	    	CSVFileUtil csvUtil=new CSVFileUtil(filePath);
	    	String line=rAfile.readLine();
//    		String[] lineInfos=CSVFileUtil.fromCSVLine(line, 5);
	    	String[] lineInfos=line.split(",");
    		KEntity kEntity=new KEntity();
    		kEntity.setTP(lineInfos[0]);
    		kEntity.setName(lineInfos[1]);
    		if(lineInfos[2].indexOf("08:59:")!=-1){
    			kEntity.setTime(lineInfos[2].substring(0, 10)+" 09:00:00");
    		}else if(lineInfos[2].indexOf("20:59:")!=-1){
    			kEntity.setTime(lineInfos[2].substring(0, 10)+" 21:00:00");
    		}else{
    			kEntity.setTime(lineInfos[2].substring(0, 19));
    		}
    		
    		kEntity.setMin(1);//1分钟K线
    		kEntity.setClose(Double.parseDouble(lineInfos[3]));
    		kEntity.setOpen(Double.parseDouble(lineInfos[3]));
    		kEntity.setHigh(Double.parseDouble(lineInfos[3]));
    		kEntity.setLow(Double.parseDouble(lineInfos[3]));
	    	while(line!=null){
	    		line=rAfile.readLine();
	    		while(line!=null){
	    			lineInfos=line.split(",");
//	    			lineInfos=CSVFileUtil.fromCSVLine(line, 5);
	    			if(kEntity.getTime().substring(0, 16).equals(lineInfos[2].substring(0, 16))){
	    				kEntity.setClose(Double.parseDouble(lineInfos[3]));
	    				if(kEntity.getHigh()<Double.parseDouble(lineInfos[3])){
	    					kEntity.setHigh(Double.parseDouble(lineInfos[3]));
	    				}
	    				if(kEntity.getLow()>Double.parseDouble(lineInfos[3])){
	    					kEntity.setLow(Double.parseDouble(lineInfos[3]));
	    				}
//	    				System.out.println(line);
	    				line=rAfile.readLine();
	    				if(line==null){//容易遗漏
		    				kEntity.setPreIndex(entityList.size()-1);
//		    				System.out.println("##:"+kEntity);
		    				if(Integer.valueOf(kEntity.getTime().substring(11, 13))>15&&Integer.valueOf(kEntity.getTime().substring(11, 13))<20){
		    					;//其他时间的无效数据
		    				}else{
//		    					System.out.println("##:"+kEntity);
		    					entityList.add(kEntity);
		    				}
	    				}
	    			}else{
	    				
//	    				System.out.println("##:"+kEntity);
	    				if(Integer.valueOf(kEntity.getTime().substring(11, 13))>15&&Integer.valueOf(kEntity.getTime().substring(11, 13))<20){
	    					;//其他时间的无效数据
	    				}else{
	    					kEntity.setPreIndex(entityList.size()-1);
	    					entityList.add(kEntity);
	    				}
	    				
	    				kEntity=new KEntity();
	    				kEntity.setTP(lineInfos[0]);
	    	    		kEntity.setName(lineInfos[1]);
	    	    		kEntity.setTime(lineInfos[2].substring(0, 19));
	    	    		kEntity.setMin(1);
	    	    		kEntity.setClose(Double.parseDouble(lineInfos[3]));
	    	    		kEntity.setOpen(Double.parseDouble(lineInfos[3]));
	    	    		kEntity.setHigh(Double.parseDouble(lineInfos[3]));
	    	    		kEntity.setLow(Double.parseDouble(lineInfos[3]));
	    	    		break;
	    			}
	    		}

	    	}

		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(), e);
		}

		return entityList;
	}
	
	//把转换成K线的大富翁数据导出到历史文件中
	public static void 大富翁K线导出(String file,ArrayList<KEntity> list){
//		String file=filePath.replace(".csv", "_"+min+"_his.text");
		if(list.size()==0){
			LOGGER.info(file+"没有数据，所以不写入历史文件");
			return;
		}
		
		文件处理工具.createFile(file);
		String lastLine=文件处理工具.读取文件最后一行(file);
		String lastTime="2000-01-22 00:00:00";
		if(!lastLine.equals("")){
			lastTime=lastLine.split(",")[2];
		}
		String nowTime=list.get(0).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//避免比文件中数据还晚的数据写入文件，这样即使重复对数据进行处理也不会再次写入
			//但是，在处理大富翁数据时，一定要按照顺序进行处理，实在记不住到哪一天了，可以看看文件，或按照日期重新处理，因为之前的数据不会被再次写入
			if(sdf.parse(lastTime).getTime()<sdf.parse(nowTime).getTime()){
				BufferedWriter out = null ;  
				String conent="";
		        try  {  
		            out = new  BufferedWriter( new  OutputStreamWriter(  
		                    new  FileOutputStream(file,  true )));  
		            for(int i=0;i<list.size();i++){
		            			
		            	//RM1701,MA1701,ZC1701,CF1701,FG1701,TA1701,SR1701,OI1701,rb1701,ru1701,bu1612,ag1612,au1612,ni1701,hc1701,m1701,c1701,p1701,i1701,l1701,y1701,j1701,pp1701,jm1701,jd1701,cs1701,a1701
		            	KEntity entity=list.get(i);
		            	String tp=getTpFromName(entity.getName());
		            	out.write(tp+","+entity.getName()+","+entity.getTime()+","+0+","+entity.getMin()+","+entity.getOpen()+","+entity.getHigh()+","+entity.getLow()+","+entity.getClose()+","+entity.getDif()+","+entity.getDea()+","+entity.getMacd()+","+entity.getShortEma()+","+entity.getMidEma()+","+entity.getLongEma()+","+entity.getCrossType()+","+entity.getDeviateType()+","+entity.getWMType()+","+entity.getPreIndex()+","+entity.getAddMin()+"\n");

		            }
		             
		        } catch  (Exception e) {  
		        	LOGGER.error(e.getMessage(), e);
		        } finally  {  
		            try  {  
		                out.close();  
		            } catch  (IOException e) {  
		            	LOGGER.error(e.getMessage(), e);
		            }  
		        } 
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 
	}
	

	
	public static ArrayList<KEntity> 大富翁K线导入(String codeName,int min){
		InputStream is;
		String path=配置文件.获取配置项("crawl.path")+"/his/"+codeName+"_his_"+min+".txt";
		ArrayList<KEntity> list=new ArrayList<KEntity>();
		try {
//			is = new FileInputStream("c:/tmp/s.txt");
//			is = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
//			BufferedReader reader = new BufferedReader(new FileInputStream(path));
			String line=reader.readLine();
			while(line!=null){
//				System.out.println(line);
				String[] infos=line.split(",");
				KEntity entity=new KEntity();
				entity.setTP(infos[0]);
				entity.setName(infos[1]);
				entity.setTime(infos[2]);
				entity.setDevTime(infos[3]);
				entity.setMin(Integer.valueOf(infos[4]));
				entity.setOpen(Double.valueOf(infos[5]));
				entity.setHigh(Double.valueOf(infos[6]));
				entity.setLow(Double.valueOf(infos[7]));
				entity.setClose(Double.valueOf(infos[8]));
				
				entity.setDif(Double.valueOf(infos[9]));
				entity.setDea(Double.valueOf(infos[10]));
				entity.setMacd(Double.valueOf(infos[11]));
				entity.setShortEma(Double.valueOf(infos[12]));
				entity.setMidEma(Double.valueOf(infos[13]));
				entity.setLongEma(Double.valueOf(infos[14]));
				
				entity.setCrossType(Integer.valueOf(infos[15]));
				entity.setDeviateType(Integer.valueOf(infos[16]));
				entity.setWMType(Integer.valueOf(infos[17]));
				entity.setPreIndex(list.size()-1);
				entity.setAddMin(Integer.valueOf(infos[19]));
				list.add(entity);
				line=reader.readLine();
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return list;
	}
	
	
	private static boolean 是需要处理数据(String hisFilePath,String newFilePath){
		boolean isNewData=false;
		文件处理工具.createFile(hisFilePath);
		String lastLine=文件处理工具.读取文件最后一行(hisFilePath);
		String lastTime="2000-01-22 00:00:00";
		if(!lastLine.equals("")){
			lastTime=lastLine.split(",")[2];
		}
		int hour=时间工具.获得现在小时();
		
		String firstLine=文件处理工具.读取文件第一行(newFilePath);
		if(firstLine==null||firstLine.equals("")){
			return false;
		}
//		String nowTime=CSVFileUtil.fromCSVLine(firstLine, 5)[2];
		String nowTime=firstLine.split(",")[2];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		try {
			if(sdf.parse(lastTime).getTime()<sdf.parse(nowTime).getTime()){
				isNewData=true;
			}
			if(hour>=15&&sdf2.parse(newFilePath.substring(newFilePath.length()-12, newFilePath.length()-4)).getTime()>sdf2.parse(时间工具.取的格式化时间("yyyyMMdd")).getTime()){
//				System.out.println("是实时数据，不进行处理");
				isNewData=false;//是当天的数据，不进行历史处理
			}
			if(hour<15&&sdf2.parse(newFilePath.substring(newFilePath.length()-12, newFilePath.length()-4)).getTime()>=sdf2.parse(时间工具.取的格式化时间("yyyyMMdd")).getTime()){
//				System.out.println(hour+"是实时数据，不进行处理");
				isNewData=false;//是当天的数据，不进行历史处理
				;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
		}
		return isNewData;
	}
	
	

	

	

	public static void 分钟转多个时间合并() {
//		System.out.println("K线数据合并...");
		// TODO Auto-generated method stub
		long start=时间工具.获得现在时间();
		int hour1=时间工具.获得现在小时();
		int week1=时间工具.获得今天星期几();
		int n=0;
		while(hour1>=15&&hour1<21||hour1>=0&&hour1<9||week1==6||week1==0){
			if(n==0){
				LOGGER.info("非工作时间，开盘后启动合并线程...");
			}
			n++;
			时间工具.休眠秒数(10);
			hour1=时间工具.获得现在小时();
			week1=时间工具.获得今天星期几();
		}
		
		String[] tp={"zc","dc","sc"};
		File[] files=new File("D:/md_future/temp/test").listFiles();
		String alertList=配置文件.获取配置项("alertList");
		StringBuffer sb=new StringBuffer();
		for(int t=0;t<tp.length;t++){
			int hour=时间工具.获得现在小时();
			int week=时间工具.获得今天星期几();
			if(hour<21&&week!=6&&week!=0){//加载下一个工作日，通过列表寻找今天之后的文件夹,周六周日加载后一天的，测试时有效，实际运行时周末不加载
				files=new File(配置文件.获取配置项("crawl.path")+"/"+tp[t]+"/"+时间工具.取的格式化时间("yyyyMMdd")).listFiles();
//				System.out.println(配置文件.获取配置项("crawl.path")+"/"+tp[t]+"/"+时间工具.取的格式化时间("yyyyMMdd"));
			}else{
//				System.out.println("DD:"+配置文件.获取配置项("crawl.path")+"/"+tp[t]);
				File[] tempFiles=new File(配置文件.获取配置项("crawl.path")+"/"+tp[t]).listFiles();
				for(int k=0;k<tempFiles.length;k++){
					if(Integer.valueOf(tempFiles[k].getName())>Integer.valueOf(时间工具.取的格式化时间("yyyyMMdd"))){
						files=new File(tempFiles[k].getAbsolutePath()+"/").listFiles();
						break;
					}
				}
				
			}
			
			if(files==null){//对应的目录不存在
				return;
			}
//			System.out.println("1380:================================");
//			StringBuffer sb=new StringBuffer();
			for(int i=0;i<files.length;i++){
				String codeName=files[i].getName().substring(0, files[i].getName().indexOf("_"));
				ArrayList<KEntity> oneMinList=AlertUtil.getListFromMap(codeName+1);
//				System.out.println("DataHandle.分钟转多时间框架():合并测试..."+codeName+" "+(oneMinList==null));
				if(!codeName.startsWith("b1")&&oneMinList!=null&&alertList.indexOf(codeName)!=-1){//有一个数据b1701，可能被rb1701覆盖到，所以可能会引起错误
					sb.append(codeName+" "+oneMinList.get(oneMinList.size()-1).getClose()+"#");
					//把1分钟数据转换成其他时间框架数据
//					System.out.println("1387:"+codeName);
					分钟转单个时间合并(oneMinList,3,codeName);
					分钟转单个时间合并(oneMinList,5,codeName);
					分钟转单个时间合并(oneMinList,15,codeName);
					分钟转单个时间合并(oneMinList,30,codeName);
					分钟转单个时间合并(oneMinList,60,codeName);
					分钟转单个时间合并(oneMinList,3600,codeName);
					initMaAndMacd(codeName);//计算MA和MACD的值
					MyMACD.MACD计算预警(codeName);
					MyMA.MA计算预警(codeName);
					MaAndMacd.单品种预警(codeName);

				}

			}
			
			
		}
		时间工具.休眠毫秒数(500);
//		LOGGER.info(sb);
		long end=时间工具.获得现在时间();
//		System.out.println("1457分钟转多时间框架:"+时间工具.耗时毫秒(start, end));
	}
	
	private static void 分钟转单个时间合并(ArrayList<KEntity> oneMinList,int min,String codeName){
		ArrayList<KEntity> tempList=null;
//		if(min==3600){
//			tempList=数据转换(codeName);
//
//		}else{
			tempList=DataHandle.数据转换(oneMinList,min);
//		}
		if(tempList==null||tempList.size()==0){//考虑抓取日线数据失败的可能
			return;
		}

		ArrayList<KEntity> hisList=AlertUtil.getListFromMap(codeName+min);
		for(int j=0;j<tempList.size();j++){
			//如果与最后一条数据实际一致，则更新最后一条
			//如果比最后一条数据更新，则添加一条
			//如果比最后一条数据更旧，则不做操作
			if(hisList==null){
//				System.out.println("1471:"+codeName+" min:"+min+"数据为空");
				return;
			}
			KEntity lastEntity=hisList.get(hisList.size()-1);
			KEntity newEntity=tempList.get(j);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			if(codeName.equals("ru1701")&&min==3600){
//				System.out.println("1.DataHandle.单个时间框架合并处理()");
//				System.out.println(lastEntity);
//				System.out.println(newEntity);
//				System.out.println("比较时间前后:"+比较时间前后(lastEntity,newEntity));
//			}
			if(比较时间前后(lastEntity,newEntity)==0){
				
				if(lastEntity.getHigh()>newEntity.getHigh()){
					newEntity.setHigh(lastEntity.getHigh());
				}
				if(lastEntity.getLow()<newEntity.getLow()){
					newEntity.setLow(lastEntity.getLow());
				}
				newEntity.setOpen(lastEntity.getOpen());
				newEntity.setTime(lastEntity.getTime());
				newEntity.setPreIndex(lastEntity.getPreIndex());
//				lastEntity=newEntity;//覆盖掉，比起下面先删除一条数据似乎更安全一些，否则在刚删除时，被其他程序使用hisList的最后一个实体，和可能会出错
				AlertUtil.putEntityToMap(codeName+min, newEntity);
				hisList.remove(hisList.size()-1);
				hisList.add(newEntity);
//				hisList.remove(lastEntity);

			}else if(比较时间前后(lastEntity,newEntity)<0){
				tempList.get(j).setPreIndex(hisList.size()-1);
				AlertUtil.putEntityToMap(codeName+min, tempList.get(j));
				hisList.add(tempList.get(j));
			}else{
				;
			}

		}
		AlertUtil.putListToMap(codeName+min, hisList);
		
	}
	

	//计算均线值和MACD值，经过这里的计算，后面的分析就不用计算了
	public static void initMaAndMacd(String codeName){
		ArrayList<KEntity> list3=AlertUtil.getListFromMap(codeName+3);
		ArrayList<KEntity> list5=AlertUtil.getListFromMap(codeName+5);
		ArrayList<KEntity> list15=AlertUtil.getListFromMap(codeName+15);
		ArrayList<KEntity> list30=AlertUtil.getListFromMap(codeName+30);
		ArrayList<KEntity> list60=AlertUtil.getListFromMap(codeName+60);
		ArrayList<KEntity> list3600=AlertUtil.getListFromMap(codeName+3600);
		ArrayList<KEntity> list7200=AlertUtil.getListFromMap(codeName+7200);
		MyMACD.setMACD(list3);
		MyMACD.setMACD(list5);
		MyMACD.setMACD(list15);
		MyMACD.setMACD(list30);
		MyMACD.setMACD(list60);
		MyMACD.setMACD(list3600);
		MyMACD.setMACD(list7200);
		MyMA.setMA(list30, 5);
		MyMA.setMA(list30, 10);
		MyMA.setMA(list60, 5);
		MyMA.setMA(list60, 10);
		MyMA.setMA(list3600, 5);
		MyMA.setMA(list3600, 10);
		MyMA.setMA(list7200, 5);
		MyMA.setMA(list7200, 10);
		
	}

	
	//历史日线数据处理,主要是那些刚刚配置，而没有日线历史数据的
	public static void 处理历史日线数据(){
		try {
			String[] aaa=配置文件.获取配置项("alertList").split(",");
			for(int i=0;i<aaa.length;i++){
				String crawlPath=配置文件.获取配置项("crawl.path")+"/";
				String file2=crawlPath+"his/"+aaa[i]+"_his_"+3600+".txt";
				File fff=new File(file2);
				if(!fff.exists()){
					LOGGER.info("日线处理:"+aaa[i]);
					时间工具.休眠秒数(3);
				}
				for(int j=0;j<=3;j++){//这里新浪接口经常得不到数据，所以多请求几次
					try {
						if(!fff.exists()){
							ArrayList<KEntity> listTemp=loadHisMinData(AlertUtil.getUrlCodeName(aaa[i]),3600);
							listTemp.remove(listTemp.size()-1);//把当天的排除
							大富翁K线导出(file2,listTemp);
							时间工具.休眠秒数(5);
							if(fff.exists()){
//								System.out.println(aaa[i]+"日线处理成功!");
								LOGGER.info(aaa[i]+"日线处理成功!");
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						LOGGER.error(e.getMessage(),e);
						
					}
				}
				

			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(),e);
		}


	}
	
	//通过日线数据组合周线数据，盘中则获取周一的开盘价，和实时收盘价
	public static void 处理历史周线数据(){
		try {
			String[] aaa=配置文件.获取配置项("alertList").split(",");
			for(int i=0;i<aaa.length;i++){
				String crawlPath=配置文件.获取配置项("crawl.path")+"/";

				String file2=crawlPath+"his/"+aaa[i]+"_his_"+7200+".txt";
				File fff=new File(file2);
				int week=时间工具.获得今天星期几();
//				File[] folder=new File(crawlPath+"dc/").listFiles();
				//该条件针对周五休假的情况
//				boolean hasNewData=时间工具.getWeek(folder[folder.length-1].getName())>时间工具.getWeek(时间工具.取的格式化时间("yyyyMMdd"));
//				System.out.println(folder[folder.length-1].getName());
				if(week==5&&fff.exists()){//星期五重新生成
					文件处理工具.delFile(file2);
				}
				if(!fff.exists()){
					LOGGER.info("周线处理:"+aaa[i]);
					时间工具.休眠秒数(3);
				}
				for(int j=0;j<=3;j++){//这里新浪接口经常得不到数据，所以多请求几次
					try {
						if(!fff.exists()){
							ArrayList<KEntity> dayList主力=getDayList(aaa[i],1);
							时间工具.休眠秒数(2);
							ArrayList<KEntity> dayList连续=getDayList(aaa[i],0);
							ArrayList<KEntity> dayList=new ArrayList<KEntity>();
							if(dayList主力.size()>0&&dayList连续.size()>0){
								KEntity zEntity=dayList主力.get(0);
								for(int n=0;n<dayList连续.size();n++){
									KEntity lEntity=dayList连续.get(n);
									if(lEntity.getTime().equals(zEntity.getTime())){
										break;
									}
//									System.out.println("增加1："+lEntity);
									dayList.add(lEntity);
								}
//								System.out.println("===========================");
								for(int m=0;m<dayList主力.size();m++){
									dayList.add(dayList主力.get(m));
//									System.out.println("增加2："+dayList主力.get(m));
								}
							}

							ArrayList<KEntity> weekList=new ArrayList<KEntity>();
							KEntity weekEntity=null;
							for(int k=0;k<dayList.size();k++){
								if(weekEntity==null){
									weekEntity=dayList.get(k);
								}
								KEntity tempEntity=dayList.get(k);
//								System.out.println(tempEntity.getTime().substring(0,10));
								if(week==5){
									
								}else{
									
								}
								if(时间工具.getWeek(tempEntity.getTime().substring(0,10))==时间工具.getWeek(时间工具.取的格式化时间("yyyy-MM-dd"))&&week!=5){//本周的数据不进行处理
									//数据已经到达最新一周，要把原来一周的数据保存
									weekEntity.setMin(7200);
									weekEntity.setPreIndex(weekList.size()-1);
									weekList.add(weekEntity);
									break;
								}else{
									if(时间工具.getWeek(tempEntity.getTime().substring(0,10))==时间工具.getWeek(weekEntity.getTime().substring(0,10))){
										if(weekEntity.getHigh()<tempEntity.getHigh()){
											weekEntity.setHigh(tempEntity.getHigh());
										}
										if(weekEntity.getLow()>tempEntity.getLow()){
											weekEntity.setLow(tempEntity.getLow());
										}
										weekEntity.setClose(tempEntity.getClose());
										if(k==dayList.size()-1&&week==5){
											weekEntity.setMin(7200);
											weekEntity.setPreIndex(weekList.size()-1);
											weekList.add(weekEntity);
										}
									}else{
										weekEntity.setMin(7200);
										weekEntity.setPreIndex(weekList.size()-1);
										weekList.add(weekEntity);
										weekEntity=tempEntity;//两个不是同一周的数据时，重新开始
									}
								}
							}
							
							大富翁K线导出(file2,weekList);
							时间工具.休眠秒数(5);
							if(fff.exists()){
//								System.out.println(aaa[i]+"周线处理成功!");
								LOGGER.info(aaa[i]+"周线处理成功!");
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						LOGGER.error(e.getMessage(),e);
						
					}
				}
				
				if(!fff.exists()){
					LOGGER.info(aaa[i]+"周线处理失败，请查找原因!");
					try {
						MailTool.qqMail(aaa[i]+"周线处理失败，请查找原因!", "周线数据处理失败通知！", 配置文件.获取配置项("mailAddressTwo"),配置文件.获取配置项("mailCCAddress"));
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(),e);
		}
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
	
	

	
	//把1分钟数据转换成其他时间K线数据，nMin数值3、5、15、30、60
	public static ArrayList<KEntity> 数据转换(ArrayList<KEntity> list,int nMin){
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		//nMin为5、15时的转换与nMin为30和60的转换有差别
		if(nMin==15||nMin==5){
			entityList=数据转换0515(list,nMin);
		}else if(nMin==3600){
			entityList=数据转换3600(list,nMin);
		}else if(nMin==3){//3,30,60分钟
			entityList=数据转换03(list,nMin);
		}else if(nMin==30||nMin==60){
			entityList=数据转换3060(list,nMin);
		}
		return entityList;
	}
	
	//采用分钟叠加方式，可以适用多时间框架，主要针对3、30、60分钟，涉及到时间边缘值处理
	private static ArrayList<KEntity> 数据转换03(ArrayList<KEntity> list,int nMin){
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		KEntity kEntity=new KEntity();
		for(int i=0;i<list.size();i++){
			kEntity=copyEntity(list.get(i));
			for(int j=0;j<nMin-1&&i<list.size()-1;j++){
				++i;
				KEntity nextEntity=copyEntity(list.get(i));
//				if(nextEntity.getTime().indexOf(" 22:59")!=-1&&nextEntity.getTP().indexOf("sc")!=-1||nextEntity.getTime().indexOf(" 23:29")!=-1&&(nextEntity.getTP().indexOf("zc")!=-1||nextEntity.getTP().indexOf("dc")!=-1)||nextEntity.getTime().indexOf(" 10:14")!=-1||nextEntity.getTime().indexOf(" 11:29")!=-1||nextEntity.getTime().indexOf(" 14:59")!=-1){
//					j=j-1;
//				}
				//有的有23:00:00和23:30:00数据，有的没有
				
				boolean b1=nextEntity.getTime().indexOf(" 22:59")!=-1&&nextEntity.getTP().indexOf("sc")!=-1&&(i<list.size()-2)&&list.get(i+1).getTime().indexOf(" 23:00")!=-1;
				boolean b2=nextEntity.getTime().indexOf(" 23:29")!=-1&&(nextEntity.getTP().indexOf("sc")!=-1||nextEntity.getTP().indexOf("dc")!=-1)&&(i<list.size()-2)&&list.get(i+1).getTime().indexOf(" 23:30")!=-1;
				boolean b3=(nextEntity.getTime().indexOf(" 10:14")!=-1||nextEntity.getTime().indexOf(" 11:29")!=-1||nextEntity.getTime().indexOf(" 14:59")!=-1)&&(i<list.size()-2)&&(list.get(i+1).getTime().indexOf(" 10:15")!=-1||list.get(i+1).getTime().indexOf(" 11:30")!=-1||list.get(i+1).getTime().indexOf(" 15:00")!=-1);
//				boolean b4=nextEntity.getTime().indexOf(" 10:14")!=-1&&(i<list.size()-2)&&list.get(i+1).getTime().indexOf(" 10:15")==-1;
				if(b1||b2||b3){
					j=j-1;
				}
//				if(b4){
//					j++;
//				}
				if(kEntity.getHigh()<nextEntity.getHigh()){
					kEntity.setHigh(nextEntity.getHigh());
				}
				if(kEntity.getLow()>nextEntity.getLow()){
					kEntity.setLow(nextEntity.getLow());
				}
				kEntity.setClose(nextEntity.getClose());
				kEntity.setPreIndex(entityList.size()-1);
				kEntity.setMin(nMin);
//				kEntity.setTime(kEntity.getTime().substring(0, 13)+":"+(nK*nMin<10?"0"+nK*nMin:""+nK*nMin)+":00");

				//跨天边界
				boolean 跨天条件=nextEntity.getTime().indexOf(" 15:00:00")!=-1;
				if(j==nMin-2||i==list.size()-1||跨天条件){
					entityList.add(kEntity);
					break;
				}
			}
			
		}
		
		return entityList;
	}
	
	//针对5、15分钟数据
	private static ArrayList<KEntity> 数据转换0515(ArrayList<KEntity> list,int nMin){
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		KEntity kEntity=new KEntity();
		for(int i=0;i<list.size();i++){
			kEntity=copyEntity(list.get(i));
			int addMin=0;
			for(i=i+1;i<list.size();i++){
				//因为list是常驻内存的一个列表，不能用该列表的对象去赋值给其他对象，否则被赋值对象改变也会引起该列表的变化，其他需要使用该列表的程序就会得到被污染的数据
				KEntity nextEntity=copyEntity(list.get(i));
				String kTime=kEntity.getTime();
				String nTime=nextEntity.getTime();
				if(kTime.substring(11, 13).equals(kTime.substring(11, 13))&&(Integer.valueOf(kTime.substring(14, 16))/nMin==Integer.valueOf(nTime.substring(14, 16))/nMin)){
					addMin++;
					kEntity.setClose(nextEntity.getClose());
					if(kEntity.getHigh()<nextEntity.getHigh()){
						kEntity.setHigh(nextEntity.getHigh());
					}
					if(kEntity.getLow()>nextEntity.getLow()){
						kEntity.setLow(nextEntity.getLow());
					}
					//容易遗漏最后一条数据
					if(i==list.size()-1){
						kEntity.setPreIndex(entityList.size()-1);
						kEntity.setMin(nMin);
						int nK=Integer.valueOf(kTime.substring(14, 16))/nMin;
						kEntity.setTime(kEntity.getTime().substring(0, 13)+":"+(nK*nMin<10?"0"+nK*nMin:""+nK*nMin)+":00");
						kEntity.setAddMin(addMin);
						entityList.add(kEntity);
						kEntity=nextEntity;
						break;
					}
				}else{
					kEntity.setPreIndex(entityList.size()-1);
					kEntity.setMin(nMin);
					int nK=Integer.valueOf(kTime.substring(14, 16))/nMin;
					kEntity.setTime(kEntity.getTime().substring(0, 13)+":"+(nK*nMin<10?"0"+nK*nMin:""+nK*nMin)+":00");
					kEntity.setAddMin(addMin);
					entityList.add(kEntity);
					kEntity=nextEntity;
					break;
				}
			}
			
		}
		
		return entityList;
	}

	
	
	//采用分钟叠加方式，可以适用多时间框架，主要针对3、30、60分钟，涉及到时间边缘值处理
	private static ArrayList<KEntity> 数据转换3060(ArrayList<KEntity> list,int nMin){
//		大连和郑州：21:00,21:30,22:00,22:30,23:00(属于23:00),9:00,9:30,10:00,10:45,11:15,13:45,14:15,14:45
//		上海：21:00,21:30,22:00,22:30(23:00属于22:30),9:00,9:30,10:00,10:45,11:15,13:45,14:15,14:45
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		if(list.size()==0){
			return entityList;
		}
		KEntity kEntity=copyEntity(list.get(0));
			for(int i=1;i<list.size();i++){
				KEntity nextEntity=copyEntity(list.get(i));
//				System.out.println(获得30分钟区域(kEntity)+":"+获得30分钟区域(nextEntity));
				boolean 条件30=(nMin==30)&&(获得30分钟区域(kEntity)==获得30分钟区域(nextEntity));
				boolean 条件60=(nMin==60)&&(获得60分钟区域(kEntity)==获得60分钟区域(nextEntity));
				if(条件30||条件60){
//					System.out.println(获得30分钟区域(kEntity)+":"+获得30分钟区域(nextEntity));
					if(kEntity.getHigh()<nextEntity.getHigh()){
						kEntity.setHigh(nextEntity.getHigh());
					}
					if(kEntity.getLow()>nextEntity.getLow()){
						kEntity.setLow(nextEntity.getLow());
					}
					kEntity.setClose(nextEntity.getClose());
					kEntity.setPreIndex(entityList.size()-1);
					kEntity.setMin(nMin);
					
					if(i==list.size()-1){
						entityList.add(kEntity);
					}
////				System.out.println("最后"+kEntity);
//				entityList.add(kEntity);
//			}

				}else{
//					System.out.println(nextEntity);
//					System.out.println("正常"+kEntity+" ta:"+获得30分钟区域(kEntity));
					entityList.add(kEntity);
					kEntity=nextEntity;
				}
		}
		
		return entityList;
	}
	

	
	
	//针对日线处理
	private static ArrayList<KEntity> 数据转换3600(ArrayList<KEntity> list,int nMin){
//		System.out.println(list.get(0).getName());
//		System.out.println(list.get(0));
//		System.out.println(list.get(list.size()-1));
		double high=0.0;
		double low=1000000;
		for(int i=0;i<list.size();i++){
			if(list.get(i).getHigh()>high){
				high=list.get(i).getHigh();
			}
			if(list.get(i).getLow()<low){
				low=list.get(i).getLow();
			}
		}
		
		ArrayList<KEntity> entityList=new ArrayList<KEntity>();
		KEntity dayEntity=copyEntity(list.get(0));
		int hour=时间工具.获得现在小时();
		if(hour>=21){
			dayEntity.setTime(时间工具.取得后一交易日期(时间工具.取的格式化时间("yyyy-MM-dd"))+" 00:00:00");
		}else{
			dayEntity.setTime(时间工具.取的格式化时间("yyyy-MM-dd")+" 00:00:00");
		}
		dayEntity.setHigh(high);
		dayEntity.setLow(low);
		dayEntity.setMin(3600);
		dayEntity.setClose(list.get(list.size()-1).getClose());
		
		
//		KEntity kEntity=getDayKEntity(list.get(0).getName());
//		if(kEntity!=null){
//			entityList.add(kEntity);
//		}
		entityList.add(dayEntity);
		return entityList;
	}
	
	private static int 获得30分钟区域(KEntity kEntity){
//		大连和郑州：21:00,21:30,22:00,22:30,23:00(属于23:00),9:00,9:30,10:00,10:45,11:15,13:45,14:15,14:45
//		上海：21:00,21:30,22:00,22:30(23:00属于22:30),9:00,9:30,10:00,10:45,11:15,13:45,14:15,14:45

		//白天品种是否不一样？
		int ta=0;
		int time=Integer.valueOf(kEntity.getTime().substring(11, 16).replace(":", ""));
		if(time>2000&&time<2130){
			ta=0;
		}else if(time>=2130&&time<2200){
			ta=1;
		}else if(time>=2200&&time<2230){
			ta=2;
		}else if(time>=2230&&time<=2300&&kEntity.getTP().equals("sc")){
			ta=3;
		}else if(time>=2230&&time<2300&&!kEntity.getTP().equals("sc")){
			ta=3;
		}else if(time>=2300&&time<2331&&!kEntity.getTP().equals("sc")){
			ta=4;
		}else if(time>=859&&time<930){
			ta=5;
		}else if(time>=930&&time<1000){
			ta=6;
		}else if(time>=1000&&time<1045){
			ta=7;
		}else if(time>=1045&&time<1115){
			ta=8;
		}else if(time>=1115&&time<1345){
			ta=9;
		}else if(time>=1345&&time<1415){
			ta=10;
		}else if(time>=1415&&time<1445){
			ta=11;
		}else if(time>=1445&&time<1501){
			ta=12;
		}

		return ta;
	}	
	
	private static int 获得60分钟区域(KEntity kEntity){
		int ta=0;
		int time=Integer.valueOf(kEntity.getTime().substring(11, 16).replace(":", ""));
		//上海21:00-22:00,22:00-23:00,9:00-10:00,10:00-11:15,11:15-14:15,14:15-15:00
		//大连：21:00-22:00,22:00-23:00,23:00-9:30,9:30-10:45,10:45-13:45,13:45-14:45,14:45-15:00
		if(kEntity.getTP().equals("sc")){
			if(time>2058&&time<2200){
				ta=0;
			}else if(time>=2200&&time<2301){
				ta=1;
			}else if(time>859&&time<1000){
				ta=2;
			}else if(time>=1000&&time<1115){
				ta=3;
			}else if(time>=1115&&time<1415){
				ta=4;
			}else if(time>=1415&&time<=1500){
				ta=5;
			}
			
		}else{
			if(AlertUtil.isDayGood(kEntity.getName())){
				if(time>859&&time<1000){
					ta=0;
				}else if(time>=1000&&time<1115){
					ta=1;
				}else if(time>=1115&&time<1415){
					ta=2;
				}else if(time>=1415&&time<=1500){
					ta=3;
				}
			}else{
				if(time>2058&&time<2200){
					ta=0;
				}else if(time>=2200&&time<2300){
					ta=1;
				}else if(time>=2300||time<930){//晚上到第二天
					ta=2;
				}else if(time>=930&&time<1045){
					ta=3;
				}else if(time>=1045&&time<1345){
					ta=4;
				}else if(time>=1345&&time<1445){
					ta=5;
				}else if(time>=1445&&time<=1500){
					ta=6;
				}
			}

		}


		return ta;
	}

	
	private static int 比较时间前后(KEntity firstEntity,KEntity secondEntity){
		int equal=0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String firstTime=firstEntity.getTime();
		String secondTime=secondEntity.getTime();
		int min=firstEntity.getMin();
		try {
			if(sdf.parse(firstTime.substring(0, 16)+":00").getTime()<sdf.parse(secondTime.substring(0, 16)+":00").getTime()){
				equal=-1;
			}
			if(sdf.parse(firstTime.substring(0, 16)+":00").getTime()>sdf.parse(secondTime.substring(0, 16)+":00").getTime()){
				equal=1;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
		}
		return equal;
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
	
	public static ArrayList<KEntity> loadHisMinData(String urlCodeName,int min){
		ArrayList<KEntity> list=new ArrayList<KEntity>();
		String codeName=AlertUtil.getCodeName(urlCodeName);
		String hisFile=配置文件.获取配置项("crawl.path")+"/his/"+codeName+"_his_"+min+".txt";
		//小于15分钟的数据
		if(new File(hisFile).exists()&&min<=7200){
//			System.out.println(hisFile+"K线历史文件存在，导入K线历史数据");
			list=DataHandle.大富翁K线导入(codeName,min);
		}else{//有时候可能没有历史数据，那就抓取新浪的历史数据，但该接口非常不稳定，所以近可能保证上面处理需要的历史数据存在
			if(min==3600){//处理日线数据
				String[] data = AlertUtil.抓新浪取历史数据(urlCodeName, min).replace("\"", "").replace("[",
				"").split(";");
				for (int i = 0; i <=data.length-1; i++) {
					int r=loadHisData(data[i],codeName,min,list);
					if(r==0){//无效数据
						break;
					}
				}
			}else if(min>=5){//因为3分钟以内数据无法抓取
				String[] data = AlertUtil.抓新浪取历史数据(urlCodeName, min).replace("\"", "").replace("[",
				"").split(";");
				for (int i = data.length-1; i >=0; i--) {
					int r=loadHisData(data[i],codeName,min,list);
					if(r==0){
						break;
					}
				}
			}
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
	
	
	private static void seek(RandomAccessFile raf,int n){
		try {
			int k=0;
	    	long len = raf.length();  
	    	if (len != 0L) {  
	    	  long pos = len - 1;  
	    	  while (pos > 0) {   
	    	    pos--;  
	    	    raf.seek(pos);  
	    	    if (raf.readByte() == '\n') {
	    	    	k++;
	    	    	if(k==n){
		    	      break;  
	    	    	}

	    	    }  
	    	  }  
	    	} 
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private static KEntity copyEntity(KEntity oe){
//		TP,name,time,devTime,min,open,high,low,close,dif,dea,macd,shortEma,midEma,longEma,crossType,deviateType,WMType,preIndex,addMin
		KEntity newEntity=new KEntity();
		
		newEntity.setTP(oe.getTP());
		newEntity.setName(oe.getName());
		newEntity.setTime(oe.getTime());
//		newEntity.setDevTime(oe.getDevTime());
//		newEntity.setMin(oe.getMin());
		newEntity.setOpen(oe.getOpen());
		newEntity.setHigh(oe.getHigh());
		newEntity.setLow(oe.getLow());
		newEntity.setClose(oe.getClose());
//		newEntity.setDif(oe.getDif());
//		newEntity.setDea(oe.getDea());
//		newEntity.setMacd(oe.getMacd());
//		newEntity.setShortEma(oe.getShortEma());
//		newEntity.setMidEma(oe.getMidEma());
//		newEntity.setLongEma(oe.getLongEma());
//		newEntity.setCrossType(oe.getCrossType());
//		newEntity.setDeviateType(oe.getDeviateType());
//		newEntity.setWMType(oe.getWMType());
//		newEntity.setPreIndex(oe.getPreIndex());
//		newEntity.setAddMin(oe.getAddMin());
		
		
		return newEntity;
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
	
	private static boolean 是需要处理数据Test(String hisFilePath,String newFilePath){

		int hour=时间工具.获得现在小时();
		
		String firstLine=文件处理工具.读取文件第一行(newFilePath);
		System.out.println(firstLine);
		
		return false;

	}


	
	public static void main(String[] args){
//		加载历史K线数据();
//		ArrayList<KEntity> weekList=AlertUtil.getListFromMap("ZC705"+7200);
//		AlertUtil.打印列表(weekList, "DataHandle.main");
		
//		System.out.println(new KEntity());
		
//		String crawlPath=配置文件.获取配置项("crawl.path")+"/";
//		File[] folder=new File(crawlPath+"dc/").listFiles();
//		System.out.println(folder[folder.length-1].getName());
		
//		String file1="D:/md_future/temp/dc/20170102/c1705_20170102.csv";
//		是需要处理数据Test("",file1);
		
		
		
//		for(int i=0;i<folder.length;i++){
//			System.out.println(folder[folder.length-1].getName());
//		}
		
//		计算撑压位置(false);
		
//		try {
//			System.out.println(时间工具.取的格式化时间("yyyy-MM-dd"));
//			System.out.println(时间工具.getWeek(时间工具.取的格式化时间("yyyy-MM-dd")));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//		处理历史日线数据();
//		处理历史周线数据();
		
//		大富翁历史数据处理();
//		double dd=1.0;
//		
//		for(int i=1;i<30;i++){
//			System.out.println("可开手数"+(i+1)+" 每手占%："+new DecimalFormat("0.#").format(dd*100/i));
//		}
		
		AlertUtil.sleepIfNotDayWorkTime();
	}




	
	


}
