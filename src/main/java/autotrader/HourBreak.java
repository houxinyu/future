package autotrader;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.future.macd.AlertUtil;
import com.example.future.macd.DataHandle;
import com.example.future.macd.KEntity;

public class HourBreak {

	//1、加载数据
	//2、从第二根K线开始，突破前高建多头，突破前低建空头，建仓之前如果有持仓则先平仓
	//3、如果第二根K线既突破前低又突破前高，则记录一笔亏损单，亏损幅度是前一K线的振幅，如果当前K线收阴则持有空单，否则持有多单
	
	public static void main(String[] args) {
		DataHandle.加载历史K线数据();
		ArrayList<KEntity> list=AlertUtil.getListFromMap("CF705"+60);
		打印单品种交易(list);
	}
	
	public static void 打印单品种交易(ArrayList<KEntity> list){
		double traderPrice=0.0;
		String treaderTime="";
		boolean isUp=false;
		DecimalFormat df = new DecimalFormat("0.#");
		for(int i=1;i<list.size();i++){
			KEntity preEntity=list.get(i-1);
			KEntity nowEntity=list.get(i);
			if(nowEntity.getHigh()>preEntity.getHigh()&&nowEntity.getLow()<preEntity.getLow()){
				if(nowEntity.getClose()>nowEntity.getOpen()){
					
					String traderInfo=(isUp?"多":"空")+"\t"+treaderTime+"\t"+traderPrice;
					int flag=(isUp?1:-1);
					double oPrice=traderPrice;
					treaderTime=nowEntity.getTime();
//					if(nowEntity.getOpen()>preEntity.getHigh()){
//						traderPrice=nowEntity.getOpen();
//					}else{
//						traderPrice=preEntity.getHigh();
//					}s
					traderPrice=preEntity.getHigh();
					double cPrice=0.0;
					if(flag>0){
						cPrice=preEntity.getLow();
					}else{
						cPrice=preEntity.getHigh();
					}
					
					traderInfo=traderInfo+"\t"+treaderTime+"\t"+cPrice+"\t"+df.format((flag*(cPrice-oPrice)/oPrice*100)-0.2)+"";
					System.out.println(traderInfo);
					isUp=true;
				}
				if(nowEntity.getClose()<nowEntity.getOpen()){
					String traderInfo=(isUp?"多":"空")+"\t"+treaderTime+"\t"+traderPrice;
					int flag=(isUp?1:-1);
					double oPrice=traderPrice;
					treaderTime=nowEntity.getTime();
//					if(nowEntity.getOpen()<preEntity.getLow()){
//						traderPrice=nowEntity.getOpen();
//					}else{
//						traderPrice=preEntity.getLow();
//					}
					traderPrice=preEntity.getLow();
					double cPrice=0.0;
					if(flag>0){
						cPrice=preEntity.getLow();
					}else{
						cPrice=preEntity.getHigh();
					}
					traderInfo=traderInfo+"\t"+treaderTime+"\t"+cPrice+"\t"+df.format(flag*(cPrice-oPrice)/oPrice*100-0.2)+"";
					System.out.println(traderInfo);
					isUp=false;
				}
			}else if(nowEntity.getHigh()>preEntity.getHigh()&&!isUp){
				isUp=true;
				String traderInfo="空\t"+treaderTime+"\t"+traderPrice;
				double oPrice=traderPrice;
				treaderTime=nowEntity.getTime();
				if(nowEntity.getOpen()>preEntity.getHigh()){
					traderPrice=nowEntity.getOpen();
				}else{
					traderPrice=preEntity.getHigh();
				}
				
				double cPrice=traderPrice;
				traderInfo=traderInfo+"\t"+treaderTime+"\t"+traderPrice+"\t"+df.format((-1*(cPrice-oPrice)/oPrice*100)-0.2)+"";
				System.out.println(traderInfo);
			}else if(nowEntity.getLow()<preEntity.getLow()&&isUp){
				isUp=false;
				String traderInfo="多\t"+treaderTime+"\t"+traderPrice;
				double oPrice=traderPrice;
				treaderTime=nowEntity.getTime();
				if(nowEntity.getOpen()<preEntity.getLow()){
					traderPrice=nowEntity.getOpen();
				}else{
					traderPrice=preEntity.getLow();
				}
				
				double cPrice=traderPrice;
				traderInfo=traderInfo+"\t"+treaderTime+"\t"+traderPrice+"\t"+df.format((cPrice-oPrice)/oPrice*100-0.2)+"";
				System.out.println(traderInfo);
			}
		}
		
	}
}
