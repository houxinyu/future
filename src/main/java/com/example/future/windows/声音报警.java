package com.example.future.windows;
//文件名:MuiscPlay.java 
import java.io.*; 
import java.net.URL; 
import sun.audio.*; 

/** 
* 
* @author wuhuiwen 
* 播放音频文件，产生音效 
*/ 
public class 声音报警 { 
    private AudioStream  as; //单次播放声音用 
    ContinuousAudioDataStream cas;//循环播放声音 
    // 构造函数 
    public 声音报警(URL url) 
    { 
        try { 
            //打开一个声音文件流作为输入 
            as = new AudioStream (url.openStream()); 
        } catch (FileNotFoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 
    } 
    
//    new FileInputStream(sourceFile)
    public 声音报警(String file) 
    { 
        try { 
            //打开一个声音文件流作为输入 
            as = new AudioStream (new FileInputStream(file)); 
        } catch (FileNotFoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 
    } 
    
    // 一次播放 开始 
    public void start() 
    { 
        if( as==null ){ 
            System.out.println("AudioStream object is not created!"); 
            return; 
        }else{ 
            AudioPlayer.player.start (as); 
        } 
    } 
    // 一次播放 停止 
    public void stop() 
    { 
        if( as==null ){ 
            System.out.println("AudioStream object is not created!"); 
            return; 
        }else{ 
            AudioPlayer.player.stop(as); 
        }        
    } 
    // 循环播放 开始 
    public void continuousStart() 
    { 
        // Create AudioData source. 
        AudioData data = null; 
        try { 
            data = as.getData(); 
        } catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 

        // Create ContinuousAudioDataStream. 
        cas = new ContinuousAudioDataStream (data); 

        // Play audio. 
        AudioPlayer.player.start(cas); 
    } 
    // 循环播放 停止 
    public void continuousStop() 
    { 
        if(cas != null) 
        { 
            AudioPlayer.player.stop (cas); 
        }    
    } 
    
    public static void main(String[] args) {
    	new 声音报警("D:/appdata/temp/成交.wav").start();
	}

}
