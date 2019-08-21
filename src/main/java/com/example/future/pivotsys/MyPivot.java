package com.example.future.pivotsys;

//枢轴点系统
public class MyPivot {
//	　　R2 = P + (H - L) = P + (R1 - S1)
//	　　R1 = (P x 2) - L
//	　　P = (H + L + C) / 3
//	　　S1 = (P x 2) - H
//	　　S2 = P - (H - L) = P - (R1 - S1)
	
//	Resistance 3 = High + 2*(Pivot - Low)
//	Resistance 2 = Pivot + (R1 - S1)
//	Resistance 1 = 2 * Pivot - Low
//	Pivot Point = ( High + Close + Low )/3
//	Support 1 = 2 * Pivot - High
//	Support 2 = Pivot - (R1 - S1)
//	Support 3 = Low - 2*(High - Pivot)
	
	
//	　　pivot:= (high + low + close) / 3;（用前一天的最高、最低和收盘）
//	　　r1:= 2*pivot - low;
//	　　s1:= 2*pivot - high;
//	　　r2:= pivot + (r1-s1);
//	　　s2:= pivot - (r1-s1);
//	　　r3:= high + 2 * (pivot-low);
//	　　s3:= low - 2 * (high - pivot);
//	　　sm1:=(pivot+s1)/2;
//	　　sm2:=(s1+s2)/2;
//	　　sm3:=(s2+s3)/2;
//	　　rm1:=(pivot+r1)/2;
//	　　rm2:=(r1+r2)/2;
//	　　rm3:=(r2+r3)/2;
	
}
