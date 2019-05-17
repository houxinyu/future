package macd;

public class Structure {
	private int crossType;//最近交叉类型:金叉1、死叉-1、非交叉0
	private int crossIndex;//最近交叉的下标
	private int devType;//结构类型:底结构1、顶结构-1、非结构0、底预结构2、顶预结构-2
	private boolean triDevType;//是否三重结构：是三重1、不是0；
	private boolean forTrend;//是否顺势结构：是顺势1、不是0；
	private boolean passivate;//是否钝化：是1、不是0
	private int period;//结构时间框架
	private int bigDevType;//结构类型:底结构1、顶结构-1、非结构0、底预结构2、顶预结构-2
	private int bigPeriod;//预结构时间框架
	private String name;//M0,M1701,RM1609等等
	private String time;//2016-09-14 15:00:00
	public int getCrossType() {
		return crossType;
	}
	public void setCrossType(int crossType) {
		this.crossType = crossType;
	}
	public int getDevType() {
		return devType;
	}
	public void setDevType(int devType) {
		this.devType = devType;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public boolean isTriDevType() {
		return triDevType;
	}
	public void setTriDevType(boolean triDevType) {
		this.triDevType = triDevType;
	}
	public int getBigPeriod() {
		return bigPeriod;
	}
	public void setBigPeriod(int bigPeriod) {
		this.bigPeriod = bigPeriod;
	}
	public boolean isForTrend() {
		return forTrend;
	}
	public void setForTrend(boolean forTrend) {
		this.forTrend = forTrend;
	}
	public boolean isPassivate() {
		return passivate;
	}
	public void setPassivate(boolean passivate) {
		this.passivate = passivate;
	}
	public int getBigDevType() {
		return bigDevType;
	}
	public void setBigDevType(int bigDevType) {
		this.bigDevType = bigDevType;
	}
	
	public String toString(){
		return "name:"+getName()+" time:"+getTime()+" min:"+getPeriod()+" dType:"+getDevType()+" cType:"+getCrossType()+" trend:"+isForTrend()+" tri:"+isTriDevType()+" pass:"+isPassivate()+" bigDtype:"+getBigDevType()+" bigMin:"+getBigPeriod();
	}
	
	public static void main(String[] args) {
		System.out.println(new Structure());
	}
	public int getCrossIndex() {
		return crossIndex;
	}
	public void setCrossIndex(int crossIndex) {
		this.crossIndex = crossIndex;
	}

}
