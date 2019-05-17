package datacrawl;

import tool.时间工具;

public class 测试分类 {
	public static void main(String[] args) {
		int a=10;
		int b=20;
		System.out.println(a+b);
		long start=时间工具.获得现在时间();
		long end=时间工具.获得现在时间();
		System.out.println(时间工具.耗时毫秒(start, end));
		
	}

}
