package macd;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tool.时间工具;

public class AppMain2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppMain2.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.info("系统启动...");
		List<String> list = new ArrayList<String>();
		list.add("M0");
		list.add("RU0");
		LOGGER.info(list.toString());
//		预警主程序();
	}
	
//	public static ArrayList<String> 预警主程序(){
//		LOGGER.info("时间："+时间工具.获得现在小时());
//		DataHandle2 dataHandle2 = new DataHandle2();
//		dataHandle2.clearHistoryData();
//		dataHandle2.loadHistoryData();
//		dataHandle2.loadTodayKEntity();
//		dataHandle2.caculateMACD();
//		return dataHandle2.caculateAlert();
//		
//	}

}
