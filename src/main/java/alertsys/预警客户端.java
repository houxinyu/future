package alertsys;

import java.net.*;    
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import tool.*;
/**
 * 获取预警信息的客户端
 * @author bill
 * @version 1.0
 * create at 2012-5-24
 */

public class 预警客户端 {
	private static final Logger LOGGER = LoggerFactory.getLogger(预警客户端.class);
	static HashSet<String> alertSet=new HashSet<String>();
	static Socket server;    
//	public static HashMap<String,String> 股票名称集合=new HashMap<String,String>();
	public static void 测试运行(String[] args)throws Exception{
		ArrayList<String> list=文件处理工具.读取文本数据(配置文件.storePath+"codelist.txt");
		HashMap<String,String> codeMap=new HashMap<String,String>();
		for(String line:list){
			String[] infos=line.split(":");
			codeMap.put(infos[0].replace("SH", "").replace("SZ", ""), infos[2]);
		}
		
	server=new Socket(配置文件.server_adress,配置文件.server_port);    
	BufferedReader in=   
	new BufferedReader(new InputStreamReader(server.getInputStream()));    
	PrintWriter out=new PrintWriter(server.getOutputStream());    
	BufferedReader wt=new BufferedReader(new InputStreamReader(System.in));    
	  
	while(true){    
		String str=wt.readLine();    
		out.println(str);    
		out.flush();    
		if(str.equals("bye")){
			str=wt.readLine();    
			out.println(str);    
			out.flush();
			break;    
		}  
		
//		StringBuffer 接收数据=new StringBuffer();
//		String sLine=in.readLine();
//		while(sLine!=null&&!sLine.equals("end")){
//			System.out.println("....");
//			接收数据.append(sLine);
//		}
		String code=in.readLine();
		if(!code.equals("no stock!")){
			String[] stocks=code.split("#");
			for(int i=0;i<stocks.length;i++){
				System.out.println(stocks[i]+"\t"+codeMap.get(stocks[i]));   
			}
		}else{
			System.out.println(code);
		}
		
	}    
	server.close();    
	}
	
	
//	public static HashSet<String> 已经预警集合=new HashSet<String>();
	
	public static void 正式运行(String[] args)throws Exception{
		加载已预警集合();
//		System.out.println("向服务询问预警情况，耐心等待");
		String server_ip=配置文件.获取配置项("server_ip");
		int server_port=Integer.valueOf(配置文件.获取配置项("server_port"));
		server=new Socket(server_ip,server_port);    
		BufferedReader in=   
		new BufferedReader(new InputStreamReader(server.getInputStream()));    
		PrintWriter out=new PrintWriter(server.getOutputStream()); 
		long alertNum=0;
		String alertList=配置文件.获取配置项("alertList");
		while(true){
			try {
				String str="request alert";
				out.println(str);    
				out.flush();    
//				System.out.println("从服务器获得请求信息，并打印：");
				String 消息=in.readLine();
				String[] infos=消息.split("#");
//				System.out.println("1.===========================");
				for(int i=0;i<infos.length;i++){
					String codeName=infos[i].trim().split(" ")[0];
					
					if(!alertSet.contains(infos[i])&&alertList.indexOf(codeName)!=-1&&配置文件.获取配置项("macdalert")!=null&&配置文件.获取配置项("macdalert").equals("true")&&infos[i].indexOf("结构")!=-1){
						LOGGER.info(infos[i]);
						alertSet.add(infos[i]);
						alertNum++;
						MusicPlay.playTwo(MusicPlay.delDigtal(infos[i].split(" ")[0]), MusicPlay.minString(Integer.valueOf(infos[i].split(" ")[1])));
					}
					if(!alertSet.contains(infos[i])&&alertList.indexOf(codeName)!=-1&&配置文件.获取配置项("maalert")!=null&&配置文件.获取配置项("maalert").equals("true")&&(infos[i].indexOf("撑")!=-1||infos[i].indexOf("压")!=-1)){
//						System.out.println(infos[i]);
						LOGGER.info(infos[i]);
						alertSet.add(infos[i]);
						alertNum++;
						String period="five";
						if(Integer.valueOf(infos[i].trim().split(" ")[1])==5){
							period="fivenear";
						}
						if(Integer.valueOf(infos[i].trim().split(" ")[1])==10){
							period="teennear";
						}
						MusicPlay.playTwo(MusicPlay.delDigtal(infos[i].trim().split(" ")[0]), period);

					}
					if(!alertSet.contains(infos[i])&&alertList.indexOf(codeName)!=-1&&配置文件.获取配置项("reversegap")!=null&&配置文件.获取配置项("reversegap").equals("true")&&infos[i].indexOf("跳空")!=-1){
//						System.out.println(infos[i]);
						LOGGER.info(infos[i]);
						alertSet.add(infos[i]);
						alertNum++;
//						MusicPlay.playTwo(MusicPlay.delDigtal(infos[i].split(" ")[0]), MusicPlay.minString(Integer.valueOf(infos[i].split(" ")[1])));
						MusicPlay.playTwo(MusicPlay.delDigtal(infos[i].trim().split(" ")[0]), "reverse");

					}
					
				}
				if(alertNum==0){
					System.out.println("刚向服务器查询过，暂时没有新预警信息，请等待。");
					alertNum++;
				}
//				if(alertNum/5*10==0){//10秒钟检查一下
//					System.out.println("正常...");
//				}
				时间工具.休眠毫秒数(200);
				if(str.equals("bye")){
					out.println(str);    
					out.flush();
					break;    
				} 
			} catch (Exception e) {
				// TODO: handle exception
//				日志工具.fileErr.error(e,e);
				MusicPlay.play();
				System.out.println("客户端出现故障，请重启客户端。");
				时间工具.休眠秒数(5);
			}

			
		}    
		server.close();    
		
	}
	
	public static void 加载已预警集合(){
//		System.out.println("加载已经预警数据，稍等...");
		InputStream is;
		try {
//			is = new FileInputStream("c:/tmp/s.txt");
			is = new FileInputStream(配置文件.获取配置项("alertlog.path")+"/alert.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"GBK"));
			String line=reader.readLine();
			while(line!=null){
//				System.out.println("加载："+line);
				alertSet.add(line);
				line=reader.readLine();
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
//		System.out.println("加载已经预警数据完毕...");
	}
	
	  
	public static void main(String[] args)throws Exception{   

//		测试运行(args);
		
		正式运行(args);
	
	}  

}
