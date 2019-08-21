package com.example.future.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间处理工具类
 * @author content
 * @version 1.0
 * create at 2012-5-10
 */

public class 时间工具 {
	private static final Logger LOGGER = LoggerFactory.getLogger(时间工具.class);
	//	9:30-11:30 13:00-15:00
	private static void init(){
//		TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
//		TimeZone.setDefault(tz);
		
	}
	public static boolean 满足工作时间条件(){
		init();
		Calendar cal1 = Calendar.getInstance();  
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00")); 
        
		boolean timeFlag = true;
		int hour = Integer.valueOf(new SimpleDateFormat("HH")
				.format(new Date()));
		int mm=Integer.valueOf(new SimpleDateFormat("mm")
				.format(new Date()));
		
		if(hour<9||hour>=15){
			timeFlag=false;
		}
		if(hour==9&&mm<30||hour==11&&mm>30||hour==12){
			timeFlag=false;
		}
		
		if(获得今天星期几()==6||获得今天星期几()==0){
			timeFlag=false;
		}
		return timeFlag;
	}
	
	public static void 休眠秒数(int n) {
		try {
			Thread.sleep(n * 1000);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static void 休眠毫秒数(int n) {
		try {
			Thread.sleep(n);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static String 获得今日日期(){
		init();
        return 取的格式化时间("yyyy-MM-dd");
	}
	
	public static long 获得毫秒时间(){
		return Calendar.getInstance().getTimeInMillis();
	}
	
	
	public static String 取得前一交易日期(){
		init();
		Calendar cal = new GregorianCalendar();
		
		if(获得今天星期几()==1){
			cal.add(Calendar.DAY_OF_YEAR, -3);
		}else{
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(
        		"yyyy-MM-dd");
        String datatime = bartDateFormat.format(cal.getTime());
        return datatime;
	}
	
	public static String 取得前一交易日期(String date){
		init();
		Calendar cal=Calendar.getInstance();
		//date格式是2012-06-01
		if(date.length()==10){
			cal.set(Calendar.YEAR,Integer.valueOf(date.substring(0,4)));//改变年份
			cal.set(Calendar.MONTH,Integer.valueOf(date.substring(5,7))-1);//改变月份，1月是0
			cal.set(Calendar.DATE,Integer.valueOf(date.substring(8,10)));//这是日期
		}
		if(获得今天星期几(cal)==1){
			cal.add(Calendar.DAY_OF_YEAR, -3);
		}else{
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(
        		"yyyy-MM-dd");
        String datatime = bartDateFormat.format(cal.getTime());
        return datatime;
	}
	
	public static String 取得后一交易日期(String date){
		init();
		Calendar cal=Calendar.getInstance();
		//date格式是2012-06-01
		if(date.length()==10){
			cal.set(Calendar.YEAR,Integer.valueOf(date.substring(0,4)));//改变年份
			cal.set(Calendar.MONTH,Integer.valueOf(date.substring(5,7))-1);//改变月份，1月是0
			cal.set(Calendar.DATE,Integer.valueOf(date.substring(8,10)));//这是日期
		}
		if(获得今天星期几(cal)==5){
			cal.add(Calendar.DAY_OF_YEAR, 3);
		}else{
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(
        		"yyyy-MM-dd");
        String datatime = bartDateFormat.format(cal.getTime());
        return datatime;
	}
	
	public static int 间隔几天(String start,String end)throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");     
        Date startDate = sdf.parse(start);       
        Date endDate = sdf.parse(end);    
        long totalDate = 0;    
        Calendar calendar = Calendar.getInstance();    
        calendar.setTime(startDate);    
        long timestart = calendar.getTimeInMillis();    
        calendar.setTime(endDate);    
        long timeend = calendar.getTimeInMillis();    
        totalDate = Math.abs((timeend - timestart))/(1000*60*60*24);    
        return Integer.valueOf(totalDate+"");  
	}
	
	   public static long getDistDates(String start,String end) throws ParseException      
	    {    
	          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");     
	          Date startDate = sdf.parse(start);       
	          Date endDate = sdf.parse(end);    
	          return getDistDates(startDate,endDate);    
	    }
	   
	   public static long getDistDates(Date startDate,Date endDate)      
	    {    
	        long totalDate = 0;    
	        Calendar calendar = Calendar.getInstance();    
	        calendar.setTime(startDate);    
	        long timestart = calendar.getTimeInMillis();    
	        calendar.setTime(endDate);    
	        long timeend = calendar.getTimeInMillis();    
	        totalDate = Math.abs((timeend - timestart))/(1000*60*60*24);    
	        return totalDate;    
	    }
	
	public static int 获得今天星期几(){
		init();
		Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK)-1;

	}
	
	public static int 获得今天星期几(Calendar c){
		init();
        return c.get(Calendar.DAY_OF_WEEK)-1;

	}
	
	/**  
	  * 根据日期字符串判断当月第几周  
	  * @param str  
	  * @return  
	  * @throws Exception  
	  */  
	 public static int getWeek(String str) throws Exception{  
		 SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		 if(str.indexOf("-")!=-1){
			 sdf= new SimpleDateFormat("yyyy-MM-dd");
		 }else{
			 sdf= new SimpleDateFormat("yyyyMMdd");
		 }
	     Date date =sdf.parse(str);  
	     Calendar calendar = Calendar.getInstance();  
	     calendar.setTime(date);  
//	     String ym=str.replace("-", "").substring(0, 5);
	     String year=str.replace("-", "").substring(0, 4);
	     //第几周  
	     int week = calendar.get(Calendar.WEEK_OF_YEAR);  
	     //第几天，从周日开始  
//	     int day = calendar.get(Calendar.DAY_OF_WEEK);  
	     return Integer.valueOf(year+week);  
	 }
	
	public static long 获得现在时间(){
		init();
		return new Date().getTime();
	}
	
	public static int 获得现在小时(){
//		TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
//		TimeZone.setDefault(tz);
		init();
		return Integer.valueOf(new SimpleDateFormat("HH")
				.format(new Date()));
	}
	public static String 取的格式化时间(String format){
		init();
		//		Calendar cal = new GregorianCalendar();
//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone( "GMT+8:00 "));
//		Calendar cal=Calendar.getInstance(TimeZone.getTimeZone( "Asia/Shanghai "));
		
		Calendar cal=Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone( "Asia/Shanghai"));
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(
        		format);
        String datatime = bartDateFormat.format(cal.getTime());
        return datatime;
	}
	
	public static long 耗时毫秒(long start,long end){
		return (end-start);
	}
	
	public static int 时间转下标(String 时间){
        Calendar c1 = Calendar.getInstance();
//        c1.set(Calendar.YEAR,2008);//改变年份
//        c1.set(Calendar.MONTH,4);//改变月份，这是5月，1月是0
//        c1.set(Calendar.DATE,15);//这是日期
        c1.set(Calendar.HOUR_OF_DAY,9);//这是时
        c1.set(Calendar.MINUTE,30);//这是分
        c1.set(Calendar.SECOND,0);//这是秒
        
        int hour=Integer.valueOf(时间.substring(0, 2));
        int min=Integer.valueOf(时间.substring(3, 5));
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.HOUR_OF_DAY,hour);
        c2.set(Calendar.MINUTE,min);
        long i = c2.getTimeInMillis() - c1.getTimeInMillis();
        long result = i / (1000 * 60);
//        System.out.println(result);
        
        if(hour>12){
        	result=result-90;
        }
        if(hour==12){
        	result=120;
        }
        if(hour>=15){
        	return 239;
        }
        if(hour==9&&min<=30){
        	return 0;
        }

		return Integer.valueOf(result+"");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(时间工具.获得现在小时());
		System.out.println(取得后一交易日期(时间工具.取的格式化时间("yyyy-MM-dd")));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
////		System.out.println(间隔几天("20120604",取的格式化时间("yyyyMMdd")));
////		System.out.println(获得今日日期());
////		System.out.println(获得今天星期几());
////		
//		for(int i=9;i<=15;i++){
//			for(int j=0;j<60;j++){
//				String hour=""+i;
//				String min=""+j;
//				if(i<10){
//					hour="0"+i;
//				}
//				if(j<10){
//					min="0"+j;
//				}
//				System.out.println(hour+":"+min+":32    "+时间转下标(hour+":"+min+":32"));
//			}
//		}
//		int hourin=3000;
//		int dayin=2000;
//		System.out.println();
//		
////		float[] ddd=new float[30];
////		for(int i=0;i<ddd.length;i++){
////			System.out.println(ddd[i]);
////		}
//		
//		System.out.println(时间转下标("12:59:53"));
//		System.out.println(时间转下标("11:30:42"));
//		System.out.println(时间转下标("13:00:43"));
//		System.out.println(时间转下标("15:03:33"));
		
		
		
		
		

	}
	
}
