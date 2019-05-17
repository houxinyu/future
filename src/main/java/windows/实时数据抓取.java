package windows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.future.tools.PageUtil;

import tool.字符串工具;
import tool.时间工具;

public class 实时数据抓取 {
	private static final Logger LOGGER = LoggerFactory.getLogger(实时数据抓取.class);
	
	public static String 返回商品代码(String goodsName){
		String gName="unKnown";
		if(goodsName.equals("橡胶")){
			gName="RU";
		}else if(goodsName.equals("线材")){
			gName="L";
		}else if(goodsName.equals("PTA")){
			gName="TA";
		}else if(goodsName.equals("棕榈油")){
			gName="P";
		}else if(goodsName.equals("豆油")){
			gName="Y";
		}else if(goodsName.equals("豆粕")){
			gName="M";
		}else if(goodsName.equals("菜粕")){
			gName="RM";
		}else if(goodsName.equals("焦炭")){
			gName="J";
		}else if(goodsName.equals("白银")){
			gName="AG";
		}else if(goodsName.equals("玻璃")){
			gName="FG";
		}else if(goodsName.equals("螺纹钢")){
			gName="RB";
		}else if(goodsName.equals("白糖")){
			gName="SR";
		}
		return gName;
	}
	
	/**
	 * 从新浪future接口抓取一次数据，提供一个future code list，返回这些future的实时数据列表
	 * @param codeList
	 * @return
	 */
	public static String 从新浪抓取实时数据(String codeList){
//		String url="http://hq.sinajs.cn/list=J1309,RB1310,CU1306,FG1309,RU1309,SR1309,Y1309,M1309,P1309,RM1309,TA1309";
		String 主力合约="1405";
		String url="http://hq.sinajs.cn/list="+返回商品代码(codeList)+主力合约;
//		url="http://hq.sinajs.cn/list="+codeList.toLowerCase();
		HttpURLConnection httpConnection = PageUtil.get(url);
		String content="";
		try {
			InputStream is = httpConnection.getInputStream();
			byte[] b = 字符串工具.getBytes(is);
			content=new String(b);
			content=content.replace("sh", "sh,").replace("sz", "sz,").replace("var hq_str_", "").replace("=\"", ",").replace("\";", " "+时间工具.取的格式化时间("HH:mm:ss"));
		}catch(Exception e){
			LOGGER.error(e.getMessage(), e);
		}
		return content;
	}
	
	/**
	 * 存储文件路径，以及存储内容
	 * @param path
	 * @param content
	 */
	public static void 存储到文件(String path,String list){
		//每个future每天240笔分时数据，8个品种，每天就有56万条数据，每天一个以日期命名的文件
		OutputStream os=null;
		BufferedWriter writer=null;
		boolean isAppend=false;
		String fileName=path+时间工具.获得今日日期()+".txt";
		File file=new File(fileName);
		if(file.exists()){
			isAppend=true;
		}
		String osString="GBK";
		if(path.indexOf("/data/nfs/xzgl/")!=-1){
			osString="UTF-8";
		}
		
		try {
			if(isAppend){
				os = new FileOutputStream(fileName,true);
				writer = new BufferedWriter(new OutputStreamWriter(os,osString));
			}else{
				os = new FileOutputStream(fileName);
				writer = new BufferedWriter(new OutputStreamWriter(os,osString));
			}
//			writer.append(list+"\r\n");
			writer.append(list);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		
	}

	public static void main(String[] args) {
		for(int i=0;i<1000;i++){
		System.out.println(从新浪抓取实时数据("").trim());
		时间工具.休眠秒数(5);
		}
		
		
//		System.out.println(时间工具.取的格式化时间("HH:mm:ss"));
//		while(true){
//			while(时间工具.满足工作时间条件()){
//				long start=时间工具.获得现在时间();
//				存储到文件("D:/data/",从新浪抓取实时数据(""));
//				long end=时间工具.获得现在时间();
//				System.out.println("本次抓取耗时："+时间工具.耗时毫秒(start, end)+"毫秒");
//				时间工具.休眠秒数(2);
//			}
//			时间工具.休眠秒数(10);
//		}
	}
}
