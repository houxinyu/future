package alertsys;

import java.io.*;
import java.net.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import analyse.Analyse;
import tool.*;

/**
 * 进行股票预警的服务端
 * 
 * @author content
 * @version 1.0 create at 2012-5-24
 */

public class 预警服务端 extends ServerSocket {
	private static final Logger LOGGER = LoggerFactory.getLogger(预警服务端.class);
//	public static boolean 服务是否启动 = false;

	public static boolean 已经加载 = false;

//	public static ArrayList<String> 预警列表=new ArrayList<String>();
	public static HashMap<String,HashSet<String>> clientList=new  HashMap<String,HashSet<String>>();

	public 预警服务端() throws IOException {
		super(Integer.valueOf(配置文件.获取配置项("server_port")));
		try {
			while (true) {
				Socket socket = accept();
				new CreateServerThread(socket);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			close();
		}
//		服务是否启动 = true;
	}
	
	public static ArrayList<String> 加载预警列表(String request){
		ArrayList<String> list=new ArrayList<String>();
		int hour=时间工具.获得现在小时();
		int min=Integer.valueOf(时间工具.取的格式化时间("mm"));
		if(hour==23&&min>58||hour==1&&min<2){//凌晨不读取日志文件，避免日志文件更新失败
			return list;
		}
		try {
			
			InputStream is;
			String logfile="track.txt";
			if(request.indexOf("houxinyu")!=-1){
				logfile="alert.txt";
			}
			if(request.indexOf("history")!=-1){
				String date;
				if(request.length()>(request.indexOf("history")+"history".length()+1)){
					date=request.substring(request.indexOf("history")+"history".length()).trim();
				}else{
					date=时间工具.取得前一交易日期().replace("-", "");
				}
				
//				System.out.println(配置文件.获取配置项("alertlog.path")+"/track.txt."+时间工具.取得前一交易日期().replace("-", ""));
//				if(request.indexOf("houxinyu")!=-1){
//					is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/"+logfile+"."+date);
//				}else{
//					
//					is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/"+logfile+"."+date);
////					is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/track.txt."+时间工具.取得前一交易日期().replace("-", ""));
//				}
				is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/"+logfile+"."+date);
				list.add(时间工具.取得前一交易日期().replace("-", "")+"日志");
			}else{
				is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/"+logfile);
//				if(request.indexOf("houxinyu")!=-1){
//					is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/"+logfile);
//				}else{
//					is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/"+logfile);
//				}
				
			}
//			is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/track.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"GBK"));
			String line=reader.readLine();
			while(line!=null){
				HashSet<String> set=clientList.get(request);
				if(set==null){
					set=new HashSet<String>();
					clientList.put(request, set);
				}
				if(!set.contains(line)&&line.indexOf("=")==-1){
					list.add(line);
					if(request.indexOf("history")==-1){
						set.add(line);
					}
				}
				line=reader.readLine();
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return list;
	}

	// --- CreateServerThread
	class CreateServerThread extends Thread {
		private Socket client;

		private BufferedReader in;

		private PrintWriter out;

		public CreateServerThread(Socket s) throws IOException {
//			System.out.println("一个客户端启动...");
			String ip=s.getInetAddress().toString();
			LOGGER.info("一个客户端启动:"+ip);
//			HashSet<String> set=new HashSet<String>();
//			ipList.put(ip, set);
			client = s;
			in = new BufferedReader(new InputStreamReader(client
					.getInputStream(), "GB2312"));
			out = new PrintWriter(client.getOutputStream(), true);
			start();
		}

		public void run() {
			try {
				int num=0;
				while (true) {

					String line = in.readLine();
					if(num==0){
						//客户端重连的时候清空列表
						clientList.remove(line);
						LOGGER.info(line);
					}
					num++;
					if(num>100){
						num=1;
					}
					if(line==null||line.equals("")){
						break;
					}
					String msg = createMessage(line);
					out.println(msg);
//					System.out.println("服务器接受新请求："+line+" "+时间工具.取的格式化时间("HHmmss"));
				}
				
				out.println("--- See you, bye! ---");
				client.close();
			} catch (IOException e) {
				// 日志工具.fileErr.error(e,e);
//				日志工具.fileErr.error("客户端异常结束连接");
				LOGGER.info("一个客户端结束:"+client.getInetAddress());
			}
		}

		//
		private String createMessage(String request) {

			StringBuffer sb = new StringBuffer();
			sb.append("");
			int i = 0;
			ArrayList<String> list=加载预警列表(request);
//			System.out.println("预警服务端.createMessage:"+request+"\t"+list.size());
			for (String code : list) {
				if(i==0){
					sb.append("" + code);
				}else{
					sb.append("#" + code);
				}
				
				i++;
			}
			sb.append("");
			if (sb.toString().equals("")) {
				return "no alert!";
			}
			return sb.toString();
		}

	}

	public static void main(String[] args) throws IOException {
		new 预警服务端();
	}
}
