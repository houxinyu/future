package macd;

import java.util.ArrayList;

public class MyATR {
	/**
	 * calculate TR values
	 * @return
	 */
	public static final void setTR(ArrayList<KEntity> list) {
		// MA = （C1+C2+C3+C4+C5+.。。.+Cn）/n
		//MAX(MAX((HIGH-LOW),ABS(REF(CLOSE,1)-HIGH)),ABS(REF(CLOSE,1)-LOW));
		for(int i=1;i<list.size();i++){
			list.get(i).setMA60(Math.max(Math.max((list.get(i).getHigh()-list.get(i).getLow()),Math.abs(list.get(i-1).getClose()-list.get(i).getHigh())),Math.abs(list.get(i-1).getClose()-list.get(i).getLow())));
		}
	}
	
	/**
	 * calculate MA values
	 * @return
	 */
	public static final void setATR(ArrayList<KEntity> list,int period) {
		// MA = （C1+C2+C3+C4+C5+.。。.+Cn）/n
		if(list.size()<10){
			for(int i=0;i<list.size();i++){
				list.get(i).setMA120(list.get(i).getMA60());//只有几根K线的时候均线计算没有意义，所以这里也不再计算
			}
		}else{
			for(int i=list.size()-1;i>=list.size()-10;i--){
				double total=0.0;
				for(int j=i;j>i-period;j--){
					total+=list.get(j).getMA60();
				}
				double maPeriod=total/period;
				list.get(i).setMA120(maPeriod);
			}
		}

	}
}
