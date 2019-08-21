package com.example.future.windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
 
/**
 * 总是置顶的窗口
 */
public class ShowYK extends JFrame {
//public class AlwaysOnTopWindow extends JDialog {
	
 
	public float openPrice=16754f;//开仓价格
	public int isUp=-1;//-1表示空头开仓，1表示多头开仓
	public JLabel backLabel=new JLabel("");
	public String goods="ru";
	
	public String showPercent(float nowPrice){
//		float nowPrice=16735f;
		float percent=100*(nowPrice-openPrice)*isUp/openPrice;
		DecimalFormat fnum = new DecimalFormat("##0.000"); //保留小数点后面三位小数
//		String perString=(percent<0.0f?"亏损:":"盈利:")+fnum.format(percent)+"%"; //盈亏百分比字符串
		String perString=fnum.format(percent)+"%"; //盈亏百分比字符串
		backLabel.setOpaque(true);
		backLabel.setBackground(Color.BLACK);
		backLabel.setForeground(percent<0.0f?Color.GREEN:Color.RED);
		if(percent>0.01){
			backLabel.setText(goods+"  "+perString+" 超1%止盈？");
			new 声音报警("D:/appdata/temp/成交.wav").start();
		}else{
			backLabel.setText(goods+"  "+perString);
		}
		
		return perString;
	}
	
	/**
	 * 
	 * @param info对话框输入字符串
	 * @return 商品期货的代码
	 */
	public String whatGoods(String info){
		String wGoods="unKnown";
		if(info.indexOf("ru")!=-1){
			goods="橡胶";
			wGoods=info.replaceAll("ru", "");
		}else if(info.indexOf("l")!=-1){
			goods="线材";
			wGoods=info.replaceAll("l", "");
		}else if(info.indexOf("ta")!=-1){
			goods="PTA";
			wGoods=info.replaceAll("ta", "");
		}else if(info.indexOf("p")!=-1){
			goods="棕榈油";
			wGoods=info.replaceAll("p", "");
		}else if(info.indexOf("y")!=-1){
			goods="豆油";
			wGoods=info.replaceAll("y", "");
		}else if(info.indexOf("m")!=-1){
			goods="豆粕";
			wGoods=info.replaceAll("m", "");
		}else if(info.indexOf("rm")!=-1){
			goods="菜粕";
			wGoods=info.replaceAll("rm", "");
		}else if(info.indexOf("j")!=-1){
			goods="焦炭";
			wGoods=info.replaceAll("j", "");
		}else if(info.indexOf("ag")!=-1){
			goods="白银";
			wGoods=info.replaceAll("ag", "");
		}else if(info.indexOf("rb")!=-1){
			goods="螺纹钢";
			wGoods=info.replaceAll("rb", "");
		}else if(info.indexOf("fg")!=-1){
			goods="玻璃";
			wGoods=info.replaceAll("fg", "");
		}else if(info.indexOf("sr")!=-1){
			goods="白糖";
			wGoods=info.replaceAll("sr", "");
		}
		
		return wGoods;
	}
	
	/**
	 * @param title 输入对话框的标题
	 */
	public void inputOpenInfo(String title){
    	String openString=JOptionPane.showInputDialog(this,"请输入（格式：ru16464k或ru15433d）:",title,JOptionPane.PLAIN_MESSAGE);
    	openString=whatGoods(openString.toLowerCase());
    	if(openString.equals("unKnown")){
    		inputOpenInfo("输入格式有误，不知道是何种商品！请重新输入");
    	}
    	if(openString.indexOf("k")==-1&&openString.indexOf("d")==-1){
    		inputOpenInfo("输入格式有误，不知道建仓方向！请重新输入");
    	}
    	String tempString=openString.replace("k", "").replace("d", "");
    	
    	try {
    		openPrice=Float.valueOf(tempString);
		} catch (Exception e) {
			// TODO: handle exception
			inputOpenInfo("输入格式有误，不符合数字规范！请重新输入");
		}
		
    	if(openString.toLowerCase().indexOf("d")!=-1){
    		isUp=1;
    	}
	}
	
    public ShowYK() throws HeadlessException {
    	inputOpenInfo("建仓信息");

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);//JFrame退出程序
//        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);//JDialog销毁提醒，是否退出由java虚拟机决定
        this.setSize(180, 60);//窗口大小
        this.setResizable(false);//设置窗口是否可以调整大小
        this.setLocation(1170, 660);//窗口位置
        this.setAlwaysOnTop(true);//设置窗口置顶
        this.setTitle(getCurrentTitle());//设置窗口标题
        this.add(backLabel);
        
 
//        // 鼠标事件：双击置顶/取消置顶
//        final AlwaysOnTopWindow thisFrame = this;
//        this.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 2) {
////                    thisFrame.setAlwaysOnTop(!thisFrame.isAlwaysOnTop());
//                	thisFrame.setAlwaysOnTop(true);
//                    thisFrame.setTitle(getCurrentTitle());
//                }
//            }
//        });
    }
 
    private String getCurrentTitle() {
//        return isAlwaysOnTop()? "双击取消置顶":"双击置顶";
    	return goods;
    }
    
    /**
     * @param aw 要刷新的窗口
     */
    public static void refreshData(ShowYK aw){
		for(int i=0;i<10000*10000*10;i++){
			//抓取数据
			String dataFromSina=实时数据抓取.从新浪抓取实时数据(aw.goods).trim();
			String[] infos=dataFromSina.split(",");
			aw.showPercent(Float.valueOf(infos[8]));
	    	try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
 
    public static void main(String[] args) {
//    	ShowYK aw= new ShowYK();
//    	aw.setVisible(true);
//    	refreshData(aw);
    	
    	交易记录处理.myMain();

		
        //1、启动程序，弹出对话框，输入建仓价位和方向
        //2、启动“时间事件”，定时刷新盈亏比例
        //3、根据盈利和亏损，改变文字颜色，盈利显示红色，亏损显示绿色
    }
}