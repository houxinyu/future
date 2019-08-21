package com.example.future.tools;

import java.util.concurrent.Callable;     
import java.util.concurrent.ExecutionException;     
import java.util.concurrent.FutureTask; 
/**
 * 分线程汇总示例程序
 * @author content
 * @version 1.0
 * create at 2012-5-13
 * 使用方法参考http://pluto418.iteye.com/blog/1179497
 */    
        
public class FutureTaskDemo {     
     
    @SuppressWarnings("unchecked")     
    public static void main(String[] args) {     
     
        // 初始化一个Callable对象和FutureTask对象     
        Callable otherPerson = new 蝙蝠侠();     
     
        // 由此任务去执行，可根据需要创建多个FutureTask?
        FutureTask futureTask = new FutureTask(otherPerson);     
     
        // 使用futureTask创建一个线程     
        Thread newhread = new Thread(futureTask);   
        
        
        // 初始化一个Callable对象和FutureTask对象     
        Callable otherPerson1 = new 童柏雄();     
     
        // 由此任务去执行，可根据需要创建多个FutureTask?
        FutureTask futureTask1 = new FutureTask(otherPerson1);     
     
        // 使用futureTask创建一个线程     
        Thread newhread1 = new Thread(futureTask1); 
        
        long start=System.currentTimeMillis();
             
        System.out.println("newhread线程现在开始启动，启动时间为：" + start     
                + " 纳秒");     
             
        newhread.start();   
        
        newhread1.start();
             
        System.out.println("主线程——东方不败，开始执行其他任务");     
             
        System.out.println("东方不败开始准备小刀，消毒...");     
     
        //兄弟线程在后台的计算线程是否完成，如果未完成则等待     
        //阻塞     
        while (!futureTask.isDone()||!futureTask1.isDone()) {     
                 
            try {     
                Thread.sleep(500);     
                System.out.println("东方不败：“等兄弟回来了，我就和小弟弟告别……颤抖……”");     
            } catch (InterruptedException e) {     
                e.printStackTrace();     
            }     
        }     
             
        long end = System.currentTimeMillis();
        System.out.println("newhread线程执行完毕，此时时间为:" + end);     
        
        System.out.println("总共用时:"+(end-start));
        String 蝙蝠侠结果 = null;  
        String 童柏雄结果 = null;
        try {     
        	蝙蝠侠结果 = (String) futureTask.get();     
        	童柏雄结果 = (String) futureTask1.get();     
     
        } catch (InterruptedException e) {     
            e.printStackTrace();     
        } catch (ExecutionException e) {     
            e.printStackTrace();     
        }     
             
        if("蝙蝠侠：：：经过一番厮杀取得《葵花宝典》上半部".equals(蝙蝠侠结果)&&"童柏雄：：：经过一番厮杀取得《葵花宝典》下半部".equals(童柏雄结果)){     
            System.out.println("两位兄弟，干得好，我挥刀自宫了啊！");     
        }else{     
            System.out.println("还好我没自宫！否则白白牺牲了……");     
        }     
             
    }     
}     
     
@SuppressWarnings("all")     
class 蝙蝠侠 implements Callable {     
     
//    @Override     
    public Object call() throws Exception {     
     
        // 先休息休息再拼命去！     
        Thread.sleep(5000);     
        String result = "蝙蝠侠：：：经过一番厮杀取得《葵花宝典》上半部";     
        System.out.println(result);     
        return result;     
    }     
     
}  


@SuppressWarnings("all")     
class 童柏雄 implements Callable {     
     
//    @Override     
    public Object call() throws Exception {     
     
        // 先休息休息再拼命去！     
        Thread.sleep(5000);     
        String result = "童柏雄：：：经过一番厮杀取得《葵花宝典》下半部";     
        System.out.println(result);     
        return result;     
    }     
     
} 
