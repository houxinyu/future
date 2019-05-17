package macd;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import analyse.Analyse;

import tool.*;

public class AppMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.info("系统启动...");
		预警主程序();
	}
	
	public static void 预警主程序(){
		LOGGER.info("时间："+时间工具.获得现在小时());
		重启程序线程();
		// TODO Auto-generated method stub
		//1、处理大富翁历史数据，即把历史tick数据处理成K线数据
		DataHandle.大富翁历史数据处理();
		
		//2、加载历史K线数据，包括新浪和大富翁的数据，在开盘前启动，如果是开盘后启动因为比较慢，则需要另外进行处理，历史数据包括1、3、5、15、30、60
		DataHandle.加载历史K线数据();
		
//		DataHandle.计算撑压位置(true);
		Analyse.计算撑压位置(true);
//		3、启动数据处理线程，把大富翁最新数据转换成1分钟数据
//		先启动异步线程，这里要改成Thread独立线程，然后再加载当天大富翁数据，该线程会检查那些当天大富翁数据已经导入，已经导入的则异步更新
//		DataHandle.大富翁最新异步导入();
		数据异步导入线程();
		DataHandle.同步导入晚上数据();//一般是程序启动的时候时间是21:00开盘之后会做这一步处理，这一步的时间与距离21:00的开盘时间有关
		
		//4、取出1分钟数据的最后一分钟数据，分别与其他时间框架进行合并，这是一个单独线程，实时合并，合并之后立马计算MACD和MA值，并进行预警
//		DataHandle.分钟多时间框架合并();
		数据异步合并线程();
		
//		测试();
		DataHandle.同步导入白天数据();
		DataHandle.同步导入晚上数据();//对于周五或者节假日，本来晚上开盘的数据，却没有开盘，则需要重新加载

		
	}
	
	public static void 重启程序线程(){
		NewThread runThread1=new NewThread(NewThread.ThreadType.重启线程);
		new Thread(runThread1).start();
	}
	
	public static void 数据异步合并线程(){
		NewThread runThread=new NewThread(NewThread.ThreadType.异步合并线程);
		new Thread(runThread).start();
	}
	
	public static void 数据异步导入线程(){
		//线程调用DataHandle.异步导入();
		NewThread runThread=new NewThread(NewThread.ThreadType.异步加载线程);
		new Thread(runThread).start();
	}
	
	public static void 测试(){
		时间工具.休眠秒数(10);
		ArrayList<KEntity> list=AlertUtil.getListFromMap("hc1705"+3600);
		AlertUtil.打印列表(list, "AppMain.测试");
	}

}
