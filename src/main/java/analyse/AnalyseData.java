package analyse;

public class AnalyseData {
	private String name;//商品名称
	private String analyDate;
	private int nearDirect;//近端方向：1多头，-1空头
	private int maNum;//是做5日还是10日
	private double fiveMaPrice;//5日均线预警价格
	private double tenMaPrice;//10日均线预警价格
	
	private int structDirect;//大结构方向
	private String structInfo;//结构信息，包括大结构或小结构描述，或大结构结束状况
//	private int thirtyDtype;//30分钟结构
//	private int sixtyDtype;//60分钟结构
//	private int fifteenDtype;//15分钟结构
//	private int fiveDtype;//5分钟结构
	
	private double stopPoint;//止损相差点位
	private double profitPoint;//止盈相差点位
	private double stopRate;//止损百分比
	private double profitRate;//止盈百分比
	
	private double fiveStopRate;

	private double positionRate;//可仓位百分比
	private double positionSize;//可开仓位手数
	
	public String toString(){//没有把时间放到里面
		String sd="--";
		if(structDirect==1){
			sd="上";
		}
		if(structDirect==-1){
			sd="下";
		}
		return name+","+(nearDirect>0?"多":"空")+","+maNum+","+fiveMaPrice+","+tenMaPrice+","+structInfo+","+stopPoint+","+stopRate+"%,"+profitPoint+","+profitRate+"%,"+positionSize+"手,"+positionRate+"%,"+fiveStopRate;
	}
	public String getAnalyDate() {
		return analyDate;
	}
	public void setAnalyDate(String analyDate) {
		this.analyDate = analyDate;
	}
	public double getFiveMaPrice() {
		return fiveMaPrice;
	}
	public void setFiveMaPrice(double fiveMaPrice) {
		this.fiveMaPrice = fiveMaPrice;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNearDirect() {
		return nearDirect;
	}
	public void setNearDirect(int nearDirect) {
		this.nearDirect = nearDirect;
	}
	public double getPositionRate() {
		return positionRate;
	}
	public void setPositionRate(double positionRate) {
		this.positionRate = positionRate;
	}
	public double getPositionSize() {
		return positionSize;
	}
	public void setPositionSize(double positionSize) {
		this.positionSize = positionSize;
	}
	public double getProfitPoint() {
		return profitPoint;
	}
	public void setProfitPoint(double profitPoint) {
		this.profitPoint = profitPoint;
	}
	public double getProfitRate() {
		return profitRate;
	}
	public void setProfitRate(double profitRate) {
		this.profitRate = profitRate;
	}
	public double getStopPoint() {
		return stopPoint;
	}
	public void setStopPoint(double stopPoint) {
		this.stopPoint = stopPoint;
	}
	public double getStopRate() {
		return stopRate;
	}
	public void setStopRate(double stopRate) {
		this.stopRate = stopRate;
	}
	public int getStructDirect() {
		return structDirect;
	}
	public void setStructDirect(int structDirect) {
		this.structDirect = structDirect;
	}
	public String getStructInfo() {
		return structInfo;
	}
	public void setStructInfo(String structInfo) {
		this.structInfo = structInfo;
	}
	public double getTenMaPrice() {
		return tenMaPrice;
	}
	public void setTenMaPrice(double tenMaPrice) {
		this.tenMaPrice = tenMaPrice;
	}
	public int getMaNum() {
		return maNum;
	}
	public void setMaNum(int maNum) {
		this.maNum = maNum;
	}

	
	public static void main(String[] args) {
		System.out.println(new AnalyseData());
	}
	public double getFiveStopRate() {
		return fiveStopRate;
	}
	public void setFiveStopRate(double fiveStopRate) {
		this.fiveStopRate = fiveStopRate;
	}

}
