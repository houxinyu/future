package windows;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import tool.文件处理工具;

public class 交易记录处理 {
	public static HashMap<String,Integer> sp=new HashMap<String,Integer>();
	
	public static void test(){
		ArrayList<String> list=文件处理工具.读取文本数据("D:/dataserver/ttt.txt","GB2312");
//		for(int i=0;i<list.size();i++){
//			System.out.println(list.get(i));
//		}
		ArrayList<String> listAfter=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			String[] ss=list.get(i).split("-");
			if(ss[4].equals("开仓")){
				String openString=list.get(i);
				for(int j=i;j<list.size();j++){
					String[] tt=list.get(j).split("-");
					if(ss[2].equals(tt[2])&&tt[4].indexOf("平")!=-1){
						String closeString=list.get(j);
						listAfter.add(openString+"-"+closeString);
						break;
					}
				}
			}
		}
		ArrayList<String> infoList=new ArrayList<String>();
		for(int i=0;i<listAfter.size();i++){
			String[] yy=listAfter.get(i).replace("--", "-").split("-");
			int flag=1;
			if(yy[3].equals("卖出")){
				flag=-1;
			}
//			System.out.println(listAfter.get(i));
			String info=yy[2]+"\t"+yy[3]+"\t"+yy[0]+yy[1]+"\t"+yy[5]+"\t"+yy[10]+yy[11]+"\t"+yy[15]+"\t"+yy[6]+"\t"+Float.valueOf(yy[7])*2+"\t"+(Float.valueOf(yy[15])-Float.valueOf(yy[5]))/Float.valueOf(yy[5])*100*flag;
			infoList.add(info);
//			System.out.println(info);
		}
		
		System.out.println("=======================================================");
		
		ArrayList<String> lastList=new ArrayList<String>();
		for(int i=0;i<infoList.size();i++){
			String[] zz=infoList.get(i).split("	");
			String newInfo=infoList.get(i);
			for(int j=i+1;j<infoList.size();j++){
				String[] aa=infoList.get(j).split("	");
				if(!zz[0].equals(aa[0])){
					break;
				}
				String time1=zz[2];
				String time2=aa[2];
				DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm:ss");
				long diff=100*1000;
				try {
					diff = df.parse(time2).getTime()-df.parse(time1).getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(diff<3*1000&&diff>0){
					String[] bb=newInfo.split("	");
					newInfo=bb[0]+"\t"+bb[1]+"\t"+bb[2]+"\t"+bb[3]+"\t"+bb[4]+"\t"+bb[5]+"\t"+(Integer.valueOf(bb[6])+Integer.valueOf(aa[6]))+"\t"+(Float.valueOf(bb[7])+Float.valueOf(aa[7]))+"\t"+bb[8];
					i=j;
				}else{
					break;
				}
				
				
			}
			String[] cc=newInfo.split("\t");
			String name=cc[0].substring(0, cc[0].indexOf('1')+1).toLowerCase();
//			System.out.println(name);
			newInfo=cc[0]+"\t"+cc[1]+"\t"+cc[2]+"\t"+cc[3]+"\t"+cc[4]+"\t"+cc[5]+"\t"+cc[6]+"\t"+cc[7]+"\t"+cc[8]+"\t"+(Float.valueOf(cc[3])*Float.valueOf(cc[8])*Float.valueOf(cc[6])*sp.get(name)/100-Float.valueOf(cc[7]));
			lastList.add(newInfo);
		}
		OutputStream os;
		try {
			os = new FileOutputStream("D:/dataserver/uuu.txt");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"gb2312"));
			for(int i=lastList.size()-1;i>=0;i--){
				System.out.println(lastList.get(i));
				writer.write(lastList.get(i)+"\r\n");
			}
			writer.close();
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void init(){
		sp.put("sr1", 10);
		sp.put("cf1", 5);
		sp.put("oi1", 10);
		sp.put("ma1", 10);
		sp.put("fg1", 20);
		sp.put("rm1", 10);
		sp.put("zc1", 100);
		
		sp.put("c1", 10);//玉米
		sp.put("cs1", 10);
		sp.put("a1", 10);
		sp.put("m1", 10);
		sp.put("y1", 10);
		sp.put("p1", 10);
		sp.put("jd1", 5);
		sp.put("l1", 5);
		sp.put("pp1", 5);
		sp.put("j1", 100);
		sp.put("jm1", 60);
		sp.put("i1", 100);
		
		sp.put("cu1", 5);//玉米
		sp.put("al1", 5);
		sp.put("zn1", 5);
		sp.put("ni1", 1);
		sp.put("sn1", 1);
		sp.put("au1", 1000);
		sp.put("ag1", 15);
		sp.put("rb1", 10);
		sp.put("hc1", 10);
		sp.put("bu1", 10);
		sp.put("ru", 10);
		
	}
	
	public static void myMain(){
		init();
		test();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		init();
		test();
	}

}
