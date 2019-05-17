package tool;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件的工具类
 * @author content
 * @version 1.0
 * create at 2012-5-11
 */

public class 配置文件 {
	private static final Logger LOGGER = LoggerFactory.getLogger(配置文件.class);
	
//	public static String storePath="/data/nfs/xzgl/218.202.227.108_data/mp3/10/09/06/13/";
	
	public static String storePath="/appdata/stock/server/";
	
	public static String serialFileName="objectFile.obj";
	
	public static final int server_port = 10000; 
	
//	public static final String server_adress="218.202.227.231";
	
	public static final String server_adress="192.168.1.103";

	/* 属性文件所对应的属性对象变量 */
	private static Properties dbProps = new Properties();

	/* 本类可能存在的惟一的一个实例 */
	private static 配置文件 m_instance = new 配置文件();

	private static InputStream ise;

	private static OutputStream os;


	/* 私有的构造子,用以保证外界无法直接实例化 */
	private 配置文件() {
		try {
			ise = getClass().getResourceAsStream("/stock.properties");
			dbProps.load(ise);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/*
	 * 静态工厂方法 @return 返还类的唯一实例
	 */
	synchronized public static 配置文件 getInstance() {
		return m_instance;
	}

	/*
	 * 读取一个特定的属性项 @param name 属性项的项名 @param defaultVal 属性项的默认值 @return
	 * 属性项的值(如此项存在),默认值(如此项不存在)
	 */
	final public static String getItem(String name, String defualtVal) {
		String val = dbProps.getProperty(name);
		if (val == null) {
			return defualtVal;
		} else {
			return val;
		}
	}

	final public static String 获取配置项(String name) {
		if(m_instance==null){
			配置文件.getInstance();
		}
		String val = dbProps.getProperty(name);
		if (val == null) {
			return null;
		} else {
			return val;
		}
	}
	
	final public static boolean 获取真假(String name) {
		if(m_instance==null){
			配置文件.getInstance();
		}
		String val = dbProps.getProperty(name);
		if (val == null) {
			return false;
		} else {
//			return val;
			if(val.toLowerCase().equals("1")||val.toLowerCase().equals("true")){
				return true;
			}else{
				return false;
			}
		}
	}

	final public String getReloadItem(String name, String defaultVal) {
		try {
			ise = getClass().getResourceAsStream("/stock.properties");
			dbProps.load(ise);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		String val = dbProps.getProperty(name);
		if (val == null) {
			return defaultVal;
		} else {
			return val;
		}

	}

	public synchronized static void saveItem(String path, String name, String value) {
		try {
			os = new FileOutputStream(path + "/stock.properties");
			dbProps.setProperty(name, value);
			dbProps.store(os, "");
			os.close();
			ise.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException("UNABLE   TO   STORE,   EXITING...");
		}
	}

	public static void main(String[] args) {
//		配置文件.getInstance();
//		workpath=/dataserver
//		alertAudio=false
//		macdalert=false
//		maalert=false
//		newMaAlert=false
//		newMacdAlert=false
//		jieMaAlert=true
//		reversegap=true
//		sendmail=true
//		mailAddress=244962625@qq.com
//		mailAddressTwo=onlyhxy@163.com
//		mailCCAddress=15675730062@wo.cn
//		alertMins=5,15,30,60
//		alertList=l1705
//		crawl.path=D:\\md_future\\temp
//		alertlog.path=D:/alertserver/log
//		server_port=10000
//		isdebug=false
//		server_ip=120.76.52.134
//		run_path=D:\\dataserver\\run.bat
//		restart_time=15:21
		saveItem(获取配置项("workpath")+"/config","test","test2");
		System.out.println(配置文件.获取配置项("workpath"));
		System.out.println(配置文件.获取配置项("restart_tt"));
		System.out.println(配置文件.获取配置项("alertAudio"));
		System.out.println(配置文件.获取配置项("macdalert"));
		System.out.println(配置文件.获取配置项("maalert"));
		System.out.println(配置文件.获取配置项("newMaAlert"));
		System.out.println(配置文件.获取配置项("newMacdAlert"));
		System.out.println(配置文件.获取配置项("jieMaAlert"));
		System.out.println(配置文件.获取配置项("reversegap"));

		System.out.println(配置文件.获取配置项("sendmail"));
		System.out.println(配置文件.获取配置项("mailAddress"));
		System.out.println(配置文件.获取配置项("mailAddressTwo"));
		System.out.println(配置文件.获取配置项("mailCCAddress"));
		System.out.println(配置文件.获取配置项("alertMins"));
		System.out.println(配置文件.获取配置项("alertList"));
		System.out.println(配置文件.获取配置项("crawl.path"));
		System.out.println(配置文件.获取配置项("alertlog.path"));
		
		System.out.println(配置文件.获取配置项("server_port"));
		System.out.println(配置文件.获取配置项("isdebug"));
		System.out.println(配置文件.获取配置项("server_ip"));
		System.out.println(配置文件.获取配置项("run_path"));
		System.out.println(配置文件.获取配置项("restart_time"));
		System.out.println(配置文件.获取配置项("test"));
		
//		 Properties prop = new Properties();
//
////		获取输入流
//		  InputStream in = 配置文件.class.getResourceAsStream(获取配置项("workpath")+"/config"+"/stock.properties");
//
////		加载进去
//		  try {
//			prop.load(in);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		  Set keyValue = prop.keySet();
//		  for (Iterator it = keyValue.iterator(); it.hasNext();)
//		  {
//		  String key = (String) it.next();
//		  System.out.println("key:"+key);
//		  }
	}
}
