package tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

/**
 * 
 * @author wuhuiwen 播放音频文件，产生音效
 */
public class MusicPlay {
	private static final Logger LOGGER = LoggerFactory.getLogger(MusicPlay.class);
	private AudioStream as; // 单次播放声音用

	ContinuousAudioDataStream cas;// 循环播放声音
	
	private static long lastTime=0l;

	// 构造函数
	public MusicPlay(String filePath) {
		try {
			// 打开一个声音文件流作为输入
			InputStream inputStream = new FileInputStream(new File(filePath));
			as = new AudioStream(inputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
			System.out.println("声音文件出错...");
		}
	}

	// 一次播放 开始
	public void start() {
		if (as == null) {
			System.out.println("AudioStream object is not created!");
			return;
		} else {
			AudioPlayer.player.start(as);
		}
	}

	// 一次播放 停止
	public void stop() {
		if (as == null) {
			System.out.println("AudioStream object is not created!");
			return;
		} else {
			AudioPlayer.player.stop(as);
		}
	}

	// 循环播放 开始
	public void continuousStart() {
		// Create AudioData source.
		AudioData data = null;
		try {
			data = as.getData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create ContinuousAudioDataStream.
		cas = new ContinuousAudioDataStream(data);

		// Play audio.
		AudioPlayer.player.start(cas);
	}

	// 循环播放 停止
	public void continuousStop() {
		if (cas != null) {
			AudioPlayer.player.stop(cas);
		}
	}

	public static void playFile(String filePath){//该方法播放系统中的文件，而不是jar包里面的文件
		try {
			// 打开一个声音文件流作为输入
			InputStream inputStream = new FileInputStream(new File(filePath));
			AudioStream ass = new AudioStream(inputStream);
			AudioPlayer.player.start(ass);// 开始播放
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
			System.out.println("声音文件出错...");
		}
	}


	public static void play() {
		if(配置文件.获取配置项("alertAudio")!=null&&配置文件.获取配置项("alertAudio").equals("true")){
			try {
//				InputStream in=MusicPlay.class.getClassLoader().getResourceAsStream("bell.wav");
				InputStream in=MusicPlay.class.getClassLoader().getResourceAsStream("audio/bell.wav");
				try {
					AudioStream as = new AudioStream(in);// 创建AudioStream 对象
					AudioPlayer.player.start(as);// 开始播放
					// AudioPlayer.player.stop(as);//停止播放，本例没有设置播放时间，歌曲结束自动停止
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void playAlertAudio() {
			try {
//				InputStream in=MusicPlay.class.getClassLoader().getResourceAsStream("bell.wav");
				InputStream in=MusicPlay.class.getClassLoader().getResourceAsStream("audio/bell.wav");
				try {
					AudioStream as = new AudioStream(in);// 创建AudioStream 对象
					AudioPlayer.player.start(as);// 开始播放
					// AudioPlayer.player.stop(as);//停止播放，本例没有设置播放时间，歌曲结束自动停止
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void play(String code) {
//		System.out.println("=================");
		if(配置文件.获取配置项("alertAudio")!=null&&配置文件.获取配置项("alertAudio").equals("true")){
			try {
//				 String pat="\\d+";
//				 Pattern p=Pattern.compile(pat);
//				 Matcher m=p.matcher(code);
//				 String newCode=m.replaceAll("").toLowerCase();
//				 System.out.println(newCode);
				InputStream in=MusicPlay.class.getClassLoader().getResourceAsStream("audio/bell.wav");
				InputStream inCode=MusicPlay.class.getClassLoader().getResourceAsStream("audio/"+code+".wav");
				try {
					if(inCode!=null){
						AudioStream as = new AudioStream(inCode);// 创建AudioStream 对象
						AudioPlayer.player.start(as);// 开始播放
						// AudioPlayer.player.stop(as);//停止播放，本例没有设置播放时间，歌曲结束自动停止
					}else{
						if(in==null){
							System.out.println(code+".wav文件不存在");
							if(配置文件.获取配置项("alertlog.path")!=null){
								playFile(配置文件.获取配置项("alertlog.path").replace("log", "")+"/bell.wav");
							}else if(配置文件.获取配置项("run_path")!=null){
								playFile(配置文件.获取配置项("run_path").replace("run.bat", "")+"\\bell.wav");
							}

						}else{
							AudioStream as = new AudioStream(in);// 创建AudioStream 对象
							AudioPlayer.player.start(as);// 开始播放
						}

						// AudioPlayer.player.stop(as);//停止播放，本例没有设置播放时间，歌曲结束自动停止
					}

				} catch (IOException e) {
					LOGGER.error(e.getMessage(),e);
					System.out.println(code+"声音文件报错.");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void playTwo(String firstAudio,String secondAudio){
		long startTime=时间工具.获得毫秒时间();
		if((startTime-lastTime)/1000<3){
			play("bell");
		}else{
			play(firstAudio);
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e.getMessage(), e);
			}
			play(secondAudio);
		}
		lastTime=时间工具.获得毫秒时间();
	}
	
	public static String delDigtal(String code){
		 String pat="\\d+";
		 Pattern p=Pattern.compile(pat);
		 Matcher m=p.matcher(code);
		 String newCode=m.replaceAll("");
		 return newCode;
	}
	
	
	public static String minString(int min){
		String minString="bell";
		switch(min){
		case 3:
			minString="three";
			break;
		case 5:
			minString="five";
			break;
		case 15:
			minString="fifteen";
			break;
		case 30:
			minString="thirty";
			break;
		case 60:
			minString="sixty";
			break;
		case 3600:
			minString="day";
			break;
		default:
			break;
		}
		return minString;
	}
	
	public static void main(String[] args) {

//		play();
//		play("TA");
//		try {
//			Thread.sleep(1200);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		playFile("D:\\dataserver\\bell.wav");
//		playTwo("ZC","three");
	}

}
