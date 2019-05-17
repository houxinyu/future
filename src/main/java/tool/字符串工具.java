package tool;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

//import javax.swing.JOptionPane;

/**
 * 字符串处理工具类
 * @author content
 * @version 1.0
 * create at 2012-5-8
 */

public class 字符串工具 {
	
	public static byte[] getBytes(InputStream inputstream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i = 0;
		while ((i = inputstream.read()) != -1) {
			byteArrayOutputStream.write(i);
		}
		return byteArrayOutputStream.toByteArray();
	}
	
	public static InputStream String2InputStream(String str){
	    ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
	    return stream;
	}


	public static String inputStream2String(InputStream is) throws IOException{
	    BufferedReader in = new BufferedReader(new InputStreamReader(is));
	    StringBuffer buffer = new StringBuffer();
	    String line = "";
	    while ((line = in.readLine()) != null){
	      buffer.append(line);
	    }
	    return buffer.toString();
	}
	
	public static String GBK2UTF8(String src){
		String GBK;
		String UTF8=src;
		try {
			GBK = new String(src.getBytes( "GBK"));
			String unicode = new String(GBK.getBytes(),"GBK");    
			UTF8 = new String(unicode.getBytes("UTF-8"));   
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   

		   
		return UTF8;
	}
	
	/**
	 * 把字符串转换成float并四舍五入
	 * @param num
	 * @return
	 */
	public static float 保留两位(float num){
//		System.out.println(num);
		try {
			BigDecimal   b   =   new   BigDecimal(num); 
			float   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
			return f1;
		} catch (Exception e) {
			// TODO: handle exception
//			System.out.println(num);
		}
		return 0.00f;

	}

//	public static void 弹出消息窗口(String 弹出内容){
//		JOptionPane.showMessageDialog(null, 弹出内容, "股票异动预警", JOptionPane.ERROR_MESSAGE);
//	}
	
	   
    public static String 提取所在省份(String 注册地){
    	if(注册地.indexOf("安徽")!=-1)
    		return "安徽";
    	else if(注册地.indexOf("北京")!=-1)
    		return "北京";
    	else if(注册地.indexOf("重庆")!=-1)
    		return "重庆";
    	else if(注册地.indexOf("黑龙江")!=-1)
    		return "黑龙江";
    	else if(注册地.indexOf("福建")!=-1)
    		return "福建";
    	else if(注册地.indexOf("甘肃")!=-1)
    		return "甘肃";
    	else if(注册地.indexOf("广东")!=-1)
    		return "广东";
    	else if(注册地.indexOf("广西")!=-1)
    		return "广西";
    	else if(注册地.indexOf("贵州")!=-1)
    		return "贵州";
    	else if(注册地.indexOf("海南")!=-1)
    		return "海南";
    	else if(注册地.indexOf("河南")!=-1)
    		return "河南";
    	else if(注册地.indexOf("河北")!=-1)
    		return "河北";
    	else if(注册地.indexOf("湖南")!=-1)
    		return "湖南";
    	else if(注册地.indexOf("湖北")!=-1)
    		return "湖北";
    	else if(注册地.indexOf("吉林")!=-1)
    		return "吉林";
    	else if(注册地.indexOf("江苏")!=-1)
    		return "江苏";
    	else if(注册地.indexOf("江西")!=-1)
    		return "江西";
    	else if(注册地.indexOf("辽宁")!=-1)
    		return "辽宁";
    	else if(注册地.indexOf("内蒙古")!=-1)
    		return "内蒙古";
    	else if(注册地.indexOf("宁夏")!=-1)
    		return "宁夏";
    	else if(注册地.indexOf("青海")!=-1)
    		return "青海";
    	else if(注册地.indexOf("山东")!=-1)
    		return "山东";
    	else if(注册地.indexOf("山西")!=-1)
    		return "山西";
    	else if(注册地.indexOf("陕西")!=-1)
    		return "陕西";
    	else if(注册地.indexOf("上海")!=-1)
    		return "上海";
    	else if(注册地.indexOf("四川")!=-1)
    		return "四川";
    	else if(注册地.indexOf("天津")!=-1)
    		return "天津";
    	else if(注册地.indexOf("西藏")!=-1)
    		return "西藏";
    	else if(注册地.indexOf("新疆")!=-1)
    		return "新疆";
    	else if(注册地.indexOf("云南")!=-1)
    		return "云南";
    	else if(注册地.indexOf("浙江")!=-1)
    		return "浙江";
    	else
    		return "未知";
    }
    
}
