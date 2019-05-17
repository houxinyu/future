package alertsys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tool.*;

public class WarnClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(WarnClient.class);
//	public static void 测试运行(String[] args){
//		//客户端流程
//		//1、从服务器端读取最近预警的代码
//		//2、打印预警代码
//	}
	
	public static int num=0;//异常超过一定次数退出程序
	
	public static void 正式运行(String[] args){
		try {
			System.out.println("系统已启动...");
			预警客户端.正式运行(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//如果预警出现万科A，则代表程序死掉了
//			剪贴板工具.setSysClipboardText("000002");
//			日志工具.fileErr.error(e, e);
			MusicPlay.play();
			System.out.println("客户端出现故障，请重启客户端。");
			时间工具.休眠秒数(5);
			num++;
			if(num==100){
				LOGGER.error(e.getMessage(),e);
				LOGGER.error("系统出现异常，1分钟后退出程序！");
				时间工具.休眠秒数(60);
				System.exit(1);
			}
		}
	}
	
	public static void main(String[] args) {
		正式运行(args);
	}

}
