package macd;

import java.io.Serializable;

/**
 * K线实体类
 * @author houxinyu
 *
 */
public class KEntity implements Serializable{
	//TP,name,time,devTime,min,open,high,low,close,dif,dea,macd,shortEma,midEma,longEma,crossType,deviateType,WMType,preIndex,addMin
	private String TP;//交易所 Trading Places
	private String name;//M0,M1701,RM1609等等
	private String time;//2016-09-14 15:00:00
	private String devTime;//背离起始时间2016-09-14 15:00:00
	private int min;//5,15,30,60,3600分别代表5分钟、15分钟、30分钟、60分钟和1天
	private double open;
	private double high;
	private double low;
	private double close;
	private double dif;
	private double dea;
	private double macd;
	private double shortEma;
	private double midEma;
	private double longEma;
	private int crossType;//0：不交叉，1：金叉，-1：死叉
	private int deviateType;//0:不背离,1:底背离,-1:顶背离
	private int WMType;//0：没有wm结构，1为W底，-1为M顶
	private int preIndex;//前一根实体的下标
	private int addMin;//0:不是右肩，1：右肩底，-1：右肩顶
	private double MA3;
	private double MA5;
	private double MA10;
	private double MA20;
	private double MA30;
	private double MA40;
	private double MA60;//该值用来存储TR值
	private double MA120;//该值用来存储ATR值
	private double MAn;
	private int period;//MAn最后保存的均线周期

	public double getMA10() {
		return MA10;
	}

	public void setMA10(double ma10) {
		MA10 = ma10;
	}

	public double getMA120() {
		return MA120;
	}

	public void setMA120(double ma120) {
		MA120 = ma120;
	}

	public double getMA20() {
		return MA20;
	}

	public void setMA20(double ma20) {
		MA20 = ma20;
	}

	public double getMA3() {
		return MA3;
	}

	public void setMA3(double ma3) {
		MA3 = ma3;
	}

	public double getMA30() {
		return MA30;
	}

	public void setMA30(double ma30) {
		MA30 = ma30;
	}

	public double getMA40() {
		return MA40;
	}

	public void setMA40(double ma40) {
		MA40 = ma40;
	}

	public double getMA5() {
		return MA5;
	}

	public void setMA5(double ma5) {
		MA5 = ma5;
	}

	public double getMA60() {
		return MA60;
	}

	public void setMA60(double ma60) {
		MA60 = ma60;
	}

	public double getMAn(int period) {
		double rMAN=MAn;
		switch(period){
		case 3:
			rMAN=MA3;
			break;
		case 5:
			rMAN=MA5;
			break;
		case 10:
			rMAN=MA10;
			break;
		case 20:
			rMAN=MA20;
			break;
		case 30:
			rMAN=MA30;
			break;
		case 40:
			rMAN=MA40;
			break;
		case 60:
			rMAN=MA60;
			break;
		case 120:
			rMAN=MA120;
			break;
		default:
			rMAN=MAn;
			break;
		}
		return rMAN;
	}

	public void setMAn(double man,int period) {
		MAn = man;
		this.period=period;
		switch(period){
		case 3:
			MA3=man;
			break;
		case 5:
			MA5=man;
			break;
		case 10:
			MA10=man;
			break;
		case 20:
			MA20=man;
			break;
		case 30:
			MA30=man;
			break;
		case 40:
			MA40=man;
			break;
		case 60:
			MA60=man;
			break;
		case 120:
			MA120=man;
			break;
		default:
			MAn = man;
			break;
		}
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getDea() {
		return dea;
	}

	public void setDea(double dea) {
		this.dea = dea;
	}

	public double getDif() {
		return dif;
	}

	public void setDif(double dif) {
		this.dif = dif;
	}


	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getMacd() {
		return macd;
	}

	public void setMacd(double macd) {
		this.macd = macd;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}
	

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		if(time.length()<11){
			this.time=time+" 15:00:00";
		}else{
			this.time = time;
		}
		
	}

	public double getLongEma() {
		return longEma;
	}

	public void setLongEma(double longEma) {
		this.longEma = longEma;
	}

	public double getShortEma() {
		return shortEma;
	}

	public void setShortEma(double shortEma) {
		this.shortEma = shortEma;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPreIndex() {
		return preIndex;
	}

	public void setPreIndex(int preIndex) {
		this.preIndex = preIndex;
	}

	public double getMidEma() {
		return midEma;
	}

	public void setMidEma(double midEma) {
		this.midEma = midEma;
	}

	public int getCrossType() {
		return crossType;
	}

	public void setCrossType(int crossType) {
		this.crossType = crossType;
	}

	public int getDeviateType() {
		return deviateType;
	}

	public void setDeviateType(int deviateType) {
		this.deviateType = deviateType;
	}

//	public String toString(){
//		return "TP:"+TP+" name:"+name+" min:"+min+" o:"+open+" h:"+high+" l:"+low+" c:"+close+" sEma:"+shortEma+" lEma:"+longEma+" dif:"+dif+" dea:"+dea+" macd:"+macd+" time:"+time+" pIndex:"+preIndex+" cType:"+crossType+" dType:"+deviateType;
//	}
	
	public String toString(){
		return "TP:"+TP+" name:"+name+" min:"+min+" o:"+open+" h:"+high+" l:"+low+" c:"+close+" MA5:"+MA5+" MA10:"+MA10+" MA"+period+":"+MAn+" dif:"+dif+" dea:"+dea+" macd:"+macd+" time:"+time+" pIndex:"+preIndex+" cType:"+crossType+" dType:"+deviateType;
	}

	public String getDevTime() {
		return devTime;
	}

	public void setDevTime(String deviateTime) {
		this.devTime = deviateTime;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getWMType() {
		return WMType;
	}

	public void setWMType(int type) {
		WMType = type;
	}

	public int getAddMin() {
		return addMin;
	}

	public void setAddMin(int addMin) {
		this.addMin = addMin;
	}

	public String getTP() {
		return TP;
	}

	public void setTP(String tp) {
		TP = tp;
	}



}
