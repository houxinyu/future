package alertsys;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 预警系统启动类
 * @author content
 * @version 1.0
 * create at 2012-6-16
 */

public class WarnServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WarnClient.class);
	
	//正式运行无需加载数据，因在抓取数据程序中已经序列化数据
	public static void 正式运行(String[] args){
		new Thread(){
			public void run() { 
				try {
					new 预警服务端();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(),e);
				}
			}
		}.start();
	}
	public static void main(String[] args) {
		正式运行(args);
	}

}
