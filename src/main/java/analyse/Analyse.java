package analyse;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import macd.AlertUtil;
import macd.KEntity;
import macd.MaAndMacd;
import macd.MyATR;
import macd.MyMA;
import macd.MyMACD;
import macd.Structure;

import tool.CSVUtils;
import tool.MailTool;
import tool.文件处理工具;
import tool.日志工具;
import tool.时间工具;
import tool.配置文件;

public class Analyse {
	private static final Logger LOGGER = LoggerFactory.getLogger(Analyse.class);
//	public static HashMap<String,Double> 商品R值=new HashMap<String,Double>();
	public static HashMap<String,Double> 预警价位=new HashMap<String,Double>();
	public static void 计算撑压位置(boolean 正式运行){
//		DataHandle.大富翁历史数据处理();
		LOGGER.info("计算撑压位置");
		//2、加载历史K线数据，包括新浪和大富翁的数据，在开盘前启动，如果是开盘后启动因为比较慢，则需要另外进行处理，历史数据包括1、3、5、15、30、60
		int hour=时间工具.获得现在小时();
		int week=时间工具.获得今天星期几();
		if((hour<15||hour>21||week==0||week==6)&&正式运行){
			return;
		}
		try {
//			StringBuffer sb=new StringBuffer();
			ArrayList<String> dataList=new ArrayList<String>();
			String dateTime=时间工具.取的格式化时间("yyyyMMdd");
			String path=配置文件.获取配置项("run_path").replace("run.bat", "")+"\\log\\analy"+dateTime+".csv";
			File file=new File(path);
			if(file.exists()){
//				return;
				文件处理工具.delFile(path);
			}
//			else{
//				文件处理工具.createFile(path);
//			}
			文件处理工具.createFile(path);
//			sb.append(dateTime+"\r\n");
			String[] alertList=配置文件.获取配置项("alertList").split(",");
			for(int i=0;i<alertList.length;i++){
				AnalyseData ad=new AnalyseData();
				ad.setMaNum(5);
				ad.setStructInfo("");
//				System.out.println(alertList[i]+"====================================");
//				sb.append(alertList[i]+"");
				ArrayList<KEntity> list=AlertUtil.getListFromMap(alertList[i]+3600);
				if(list==null){
//					System.out.println("1321:"+alertList[i]+"数据不存在");
					LOGGER.info(alertList[i]+"数据不存在");
				}else{
					MyMA.setMA(list, 5);
					MyMA.setMA(list, 10);
					MyMACD.setMACD(list);
					MyATR.setTR(list);
//					MyATR.setATR(list, 26);
					MyATR.setATR(list, 26);
					//设置今天价格为x
					//MA10=(Cx+Ct1+Ct2+...Ct9)/10
					//(Cx-MA10)/MA10*100=0.5
					//C10c=1.005/(10-1.005)*(C1+C2+...C9)//支撑位置
					//C5c=1.005/(5-1.005)*(C1+C2+...C4)
					//C10y=0.995/(10-0.995)*(C1+C2+...C9)
					//C5y=0.995/(5-0.995)*(C1+C2+...C4)
					
					KEntity entity=list.get(list.size()-1);
					KEntity preEntity=list.get(list.size()-2);
					KEntity ppEntity=list.get(list.size()-3);
					KEntity fourEntity=list.get(list.size()-4);
					DecimalFormat df = new DecimalFormat("0.#");
					DecimalFormat df1 = new DecimalFormat("0");
					boolean 趋势条件=false;
					boolean isUP=false;
					boolean isDOWN=false;
					for(int k=list.size()-1;k>list.size()-5;k--){
						KEntity nEntity=list.get(k);
						KEntity pEntity=list.get(k-1);
						if(nEntity.getMA5()>pEntity.getMA5()&&nEntity.getDif()>=pEntity.getDif()){
							趋势条件=true;
							isUP=true;
							break;
						}
						if(nEntity.getMA5()<pEntity.getMA5()&&nEntity.getDif()<=pEntity.getDif()){
							趋势条件=true;
							isDOWN=true;
							break;
						}
					}
					
					//条件1：同向
					boolean 条件1=preEntity.getMA5()<entity.getMA5()&&preEntity.getDif()<=entity.getDif();
					//条件2：单转
					boolean 条件2=preEntity.getMA5()<=entity.getMA5()&&preEntity.getDif()>=entity.getDif()||preEntity.getMA5()>=entity.getMA5()&&preEntity.getDif()<=entity.getDif();
//					System.out.println(entity.getName()+" preClose:"+preEntity.getClose()+" close:"+entity.getClose());
					//ppEntity.getMA5()<=preEntity.getMA5()&&ppEntity.getDif()<=preEntity.getDif()
//					if(preEntity.getMA5()<entity.getMA5()&&(preEntity.getDif()<=entity.getDif())||(preEntity.getDif()>entity.getDif()&&ppEntity.getDif()<=preEntity.getDif())){
					if(趋势条件&&isUP){
						ad.setName(alertList[i]);
						ad.setNearDirect(1);
//						sb.append((i+1)+"."+alertList[i]+"");
//						sb.append("撑");
						double tatol=0.0;
						for(int j=list.size()-1;j>list.size()-5;j--){
							tatol+=list.get(j).getClose();
						}
						double C5c=1.005/(5-1.005)*tatol;
//						System.out.println(alertList[i]+" 五日支撑："+df.format(C5c));
						if(entity.getClose()>1000){
//							sb.append(" 五："+df1.format(C5c));
							ad.setFiveMaPrice(Double.parseDouble(df1.format(C5c)));
						}else{
//							sb.append(" 五："+df.format(C5c));
							ad.setFiveMaPrice(Double.parseDouble(df.format(C5c)));
						}
						
						tatol=0.0;
						for(int j=list.size()-1;j>list.size()-10;j--){
							tatol+=list.get(j).getClose();
						}
						double C10c=1.005/(10-1.005)*tatol;
//						System.out.println(alertList[i]+" 十日支撑："+df.format(C10c));
						
						if(entity.getClose()>1000){
//							sb.append(" 十："+df1.format(C10c));
							ad.setTenMaPrice(Double.parseDouble(df1.format(C10c)));
						}else{
//							sb.append(" 十："+df.format(C10c));
							ad.setTenMaPrice(Double.parseDouble(df.format(C10c)));
						}
//						商品R值.put(entity.getName(), Double.parseDouble(df.format(entity.getMA120()*0.3)));
						ad.setStopPoint(Double.parseDouble(df.format(entity.getMA120()*0.3)));
						ad.setStopRate(Double.parseDouble(df.format((entity.getMA120()*0.3/entity.getClose()*100))));
						ad.setProfitPoint(Double.parseDouble(df.format(entity.getMA120()*0.9)));
						ad.setProfitRate(Double.parseDouble(df.format((entity.getMA120()*0.9/entity.getClose()*100))));
						ad.setPositionSize(Double.parseDouble(df.format(entity.getClose()*AlertUtil.获得可开仓位(entity.getName())/(entity.getMA120()*3*100))));
						ad.setPositionRate(Double.parseDouble(df.format(entity.getClose()/(entity.getMA120()*3))));
						if(条件2){
							ad.setMaNum(10);
							ad.setStructInfo(ad.getStructInfo()+"单转 ");
						}
					}

					
					//条件1：同向
					boolean 条件3=preEntity.getMA5()>entity.getMA5()&&preEntity.getDif()>=entity.getDif();
					//条件2：单转
					boolean 条件4=preEntity.getMA5()<=entity.getMA5()&&preEntity.getDif()>=entity.getDif()||preEntity.getMA5()>=entity.getMA5()&&preEntity.getDif()<=entity.getDif();

					if(趋势条件&&isDOWN){	
						ad.setName(alertList[i]);
						ad.setNearDirect(-1);
						double tatol=0.0;
						for(int j=list.size()-1;j>list.size()-5;j--){
							tatol+=list.get(j).getClose();
						}
						double C5y=0.995/(5-0.995)*tatol;
						if(entity.getClose()>1000){
							ad.setFiveMaPrice(Double.parseDouble(df1.format(C5y)));
						}else{
							ad.setFiveMaPrice(Double.parseDouble(df.format(C5y)));
						}
						tatol=0.0;
						for(int j=list.size()-1;j>list.size()-10;j--){
							tatol+=list.get(j).getClose();
						}
						double C10y=0.995/(10-0.995)*tatol;
						if(entity.getClose()>1000){
							ad.setTenMaPrice(Double.parseDouble(df1.format(C10y)));
						}else{
							ad.setTenMaPrice(Double.parseDouble(df.format(C10y)));
						}
						//0.3ATR=df.format((ATR*0.3/entity.getClose()*100))
//						商品R值.put(entity.getName(), Double.parseDouble(df.format(entity.getMA120()*0.3)));
						ad.setStopPoint(Double.parseDouble(df.format(entity.getMA120()*0.3)));
						ad.setStopRate(Double.parseDouble(df.format((entity.getMA120()*0.3/entity.getClose()*100))));
						ad.setProfitPoint(Double.parseDouble(df.format(entity.getMA120()*0.9)));
						ad.setProfitRate(Double.parseDouble(df.format((entity.getMA120()*0.9/entity.getClose()*100))));
						ad.setPositionSize(Double.parseDouble(df.format(entity.getClose()*AlertUtil.获得可开仓位(entity.getName())/(entity.getMA120()*3*100))));
						ad.setPositionRate(Double.parseDouble(df.format(entity.getClose()/(entity.getMA120()*3))));
						
						if(条件4){
							ad.setMaNum(10);
							ad.setStructInfo(ad.getStructInfo()+"单转 ");
						}
					}
					
					
					MyATR.setATR(list, 5);
					ad.setFiveStopRate(Double.parseDouble(df.format((entity.getMA120()*0.3/entity.getClose()*100))));
					
				}
				
				String structInfo=结构分析(alertList[i]);
				
				if(structInfo.startsWith("1")&&ad.getNearDirect()==-1||structInfo.startsWith("-1")&&ad.getNearDirect()==1){
					ad.setMaNum(10);
//					ad.setStructInfo(ad.getStructInfo()+"有结构");
				}
				ad.setStructInfo(structInfo.replace("1 ", "").replace("-1 ", "").replace("0 ", "")+ad.getStructInfo());
				
				String weekInfo=周线分析(alertList[i],ad.getStopRate());
				ad.setStructInfo(ad.getStructInfo()+" "+weekInfo);

				预警价位.put(ad.getName()+5, ad.getFiveMaPrice());
				预警价位.put(ad.getName()+10, ad.getTenMaPrice());
				dataList.add(ad.toString());

			}
			
			String table=listToTable(dataList);
			CSVUtils.exportCsv(file, dataList);
			发送邮件(table,正式运行);
			if(!正式运行){
				System.out.println(table);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(),e);
		}
		
	}
	
	private static void 发送邮件(String table,boolean 正式运行){
		if(配置文件.获取配置项("sendmail")!=null&&配置文件.获取配置项("sendmail").equals("true")){
//			MailTool.sendMail(sb.toString().replace("\r\n", "<br>"),"撑压数值",配置文件.获取配置项("mailAddress"));
//			MailTool.sendMail(table,"撑压数值",配置文件.获取配置项("mailAddress"));
			try {
				String to=配置文件.获取配置项("mailAddress");
				if(!正式运行){
					to=配置文件.获取配置项("mailAddressTwo");
				}
				MailTool.qqMail(table,"盘后分析报告",to,配置文件.获取配置项("mailCCAddress"));
				LOGGER.info("发送邮件至："+to);
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.info("发送邮件失败！");
				LOGGER.error(e.getMessage(),e);
				try {
					时间工具.休眠毫秒数(60);//1分钟之后尝试重发邮件
					if(正式运行){
						MailTool.qqMail(table,"盘后分析报告",配置文件.获取配置项("mailAddress"),配置文件.获取配置项("mailCCAddress"));
					}
				} catch (Exception ee) {
					// TODO: handle exception
				}
			}
			
		}
	}
	
	private static String 结构分析(String codeName){
		StringBuffer sb=new StringBuffer();
		ArrayList<KEntity> structList=AlertUtil.getListFromMap(codeName+30);
		MyMACD.setMACD(structList);
		Structure struct=MaAndMacd.获取结构类型(structList, false);
		if(struct.getDevType()==0){
			structList=AlertUtil.getListFromMap(codeName+60);
			MyMACD.setMACD(structList);
			struct=MaAndMacd.获取结构类型(structList, false);
			if(struct.getDevType()!=0){
				sb.append(struct.getDevType()+" ");
//				ad.setStructDirect(struct.getDevType());
				switch(struct.getDevType()){
				case 1:
//					ad.setStructInfo("60底"+ad.getStructInfo());
					sb.append("60底");
					break;
				case -1:
					sb.append("60顶");
//					ad.setStructInfo("60顶"+ad.getStructInfo());
					break;
				case 21:
					sb.append("60预底");
//					ad.setStructInfo("60预底"+ad.getStructInfo());
					break;
				case -2:
					sb.append("60预顶");
//					ad.setStructInfo("60预顶"+ad.getStructInfo());
					break;
				default:
					sb.append("----");
//					ad.setStructInfo("----"+ad.getStructInfo());
				    break;
				}
			}else{
				sb.append(struct.getDevType()+" ");
				sb.append("----");
//				ad.setStructDirect(struct.getDevType());
//				ad.setStructInfo("----"+ad.getStructInfo());
			}
		}else{
//			ad.setStructDirect(struct.getDevType());
			sb.append(struct.getDevType()+" ");
			switch(struct.getDevType()){
			case 1:
				sb.append("30底");
//				ad.setStructInfo("30底"+ad.getStructInfo());
				break;
			case -1:
				sb.append("30顶");
//				ad.setStructInfo("30顶"+ad.getStructInfo());
				break;
			case 21:
				sb.append("30预底");
//				ad.setStructInfo("30预底"+ad.getStructInfo());
				break;
			case -2:
				sb.append("30预顶");
//				ad.setStructInfo("30预顶"+ad.getStructInfo());
				break;
			default:
				sb.append("----");
//				ad.setStructInfo("----"+ad.getStructInfo());
			    break;
			}
		}
		
//		if(struct.getDevType()==0||struct.isForTrend()){
//			sb.append("60底");
//			ad.setStructDirect(struct.getDevType());
//			ad.setStructInfo("----"+ad.getStructInfo());
//		}
		
//		if(struct.getDevType()==1&&ad.getNearDirect()==-1||struct.getDevType()==-1&&ad.getNearDirect()==1){
//			ad.setMaNum(10);
////			ad.setStructInfo(ad.getStructInfo()+"有结构");
//		}
		return sb.toString();
	}
	
	private static String 周线分析(String codeName,double stopRate){
		String weekInfo="";
		ArrayList<KEntity> weekList=AlertUtil.getListFromMap(codeName+7200);
		ArrayList<KEntity> dayList=AlertUtil.getListFromMap(codeName+3600);
		KEntity dayEntity=dayList.get(dayList.size()-1);
//		for(int i=dayList.size()-2;i>0;i--){
//			
//		}
		KEntity weekEntity=new KEntity();
		weekEntity.setName(dayEntity.getName());
		weekEntity.setClose(dayEntity.getClose());
		weekEntity.setTP(dayEntity.getTP());
		weekEntity.setTime(时间工具.取的格式化时间("yyyy-MM-dd"+" 00:00:00"));
		weekEntity.setPreIndex(weekList.size()-1);
		weekList.add(weekEntity);
		MyMA.setMA(weekList, 5);
		
		KEntity ppWeekEntity=weekList.get(weekList.size()-3);//因为nowWeekEntity的收盘价是假设昨天一样
		KEntity preWeekEntity=weekList.get(weekList.size()-2);
		KEntity nowWeekEntity=weekList.get(weekList.size()-1);
		if(ppWeekEntity.getMA5()<preWeekEntity.getMA5()){//向上
			boolean 条件11=nowWeekEntity.getClose()<nowWeekEntity.getMA5();//在5日线和10日线之间，肯定是近端
			boolean 条件12=Math.abs((nowWeekEntity.getClose()-nowWeekEntity.getMA5())*100)/nowWeekEntity.getMA5()<stopRate;//小于1%以内可看成是近端
			if(条件11||条件12){
				weekInfo="周多近端";
			}else{
				if(Math.abs((preWeekEntity.getClose()-nowWeekEntity.getMA5())*100)/nowWeekEntity.getMA5()<stopRate){//与这周的均线进行比较，看是否近端发起
					weekInfo="周线多头";
				}
				
			}
		}else{
			boolean 条件21=nowWeekEntity.getClose()>nowWeekEntity.getMA5();//在5日线和10日线之间，肯定是近端
			boolean 条件22=Math.abs((nowWeekEntity.getClose()-nowWeekEntity.getMA5())*100)/nowWeekEntity.getMA5()<stopRate;//小于1%以内可看成是近端
			if(条件21||条件22){
				weekInfo="周空近端";
			}else{
				if(Math.abs((preWeekEntity.getClose()-nowWeekEntity.getMA5())*100)/nowWeekEntity.getMA5()<stopRate){
					weekInfo="周线空头";
				}
			}
		}
		
		return weekInfo;
		
	}
	
	private static String listToTable(ArrayList<String> list){
		StringBuffer sb=new StringBuffer();
//		sb.append("<table border=\"1\"  bordercolor=\"#000000\">");
		sb.append("<table>");
		sb.append("\r\n<tr bgcolor=\"#802A2A\">");
		String[] thinfos="序号,名称,方向,操作线,五日价,十日价,结构信息,止损点,止损比,止盈点,止盈比,开仓数,开仓比,5ATR".split(",");
		for(int i=0;i<thinfos.length;i++){
			sb.append("<th nowrap=\"nowrap\" style=\"color:#FFFFFF;\">");
			sb.append(thinfos[i]);
			sb.append("</th>");
		}
		sb.append("</tr>");
		
		for(int i=0;i<list.size();i++){
			String[] adinfos=list.get(i).split(",");
			if(adinfos[1].indexOf("多")!=-1){
				sb.append("\r\n<tr bgcolor=\"#B22222\">");
			}else{
				sb.append("\r\n<tr bgcolor=\"#308014\">");
			}
			sb.append("<td  style=\"color:#FFFFFF;\">");
			sb.append(i+1);
			sb.append("</td>");
			
			for(int j=0;j<adinfos.length;j++){
				if(adinfos[j].indexOf("底")!=-1||adinfos[j].indexOf("顶")!=-1){
					sb.append("<td nowrap=\"nowrap\"   style=\"color:#FFFF00;border: solid thin #000000\">");
				}else{
					sb.append("<td nowrap=\"nowrap\"   style=\"color:#FFFFFF;border: solid thin #000000\">");
				}
				
				sb.append(adinfos[j]);
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
		
//		return "测试信息";
	}
	
	private static double maByATR(final double atrRate){
		double latrRate=atrRate;
		if(latrRate>=2.0){
			latrRate=2.0;
		}
		if(latrRate<=0.5){
			latrRate=0.5;
		}
		double cToMa=0.5;
		if(latrRate>=1.0){
			cToMa=cToMa+(0.2/1)*(latrRate-1);
		}else{
			cToMa=cToMa-(0.4/0.5)*(1-latrRate);
		}
		
		return cToMa;
	}
	
	
	public static void main(String[] args) {
//		DataHandle.加载历史K线数据();
//		Analyse.计算撑压位置(false);
//		DecimalFormat df = new DecimalFormat("0.##");
//		for(int i=5;i<25;i++){
//			System.out.println(""+df.format(i*0.1)+"======="+df.format(maByATR(i*0.1)));
//		}
		
		发送邮件("test",false);
		
	}

}
