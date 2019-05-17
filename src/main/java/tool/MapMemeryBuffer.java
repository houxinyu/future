package tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapMemeryBuffer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapMemeryBuffer.class);
	/**
	 * @param args
	 */
    public static void main(String[] args) throws Exception {  
        ByteBuffer byteBuf = ByteBuffer.allocate(1024 * 14 * 1024);  
        byte[] bbb = new byte[14 * 1024 * 1024];  
        FileInputStream fis = new FileInputStream("D:/md_future/temp/test/ag1612_20160920.csv");  
        FileChannel fc = fis.getChannel();  
        long timeStar = System.currentTimeMillis();// 得到当前的时间  
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY,0, fc.size());  
//        int size=105*10;
//        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY,fc.size()-size, size);  
        StringBuffer sb=new StringBuffer();
        int num=0;
        byte[] tt=new byte[100];
        long size=0;
        while(mbb.remaining()>0){
        	ByteBuffer bb=mbb.get(tt);
        	tt[num]=mbb.get();
        	size+=100;
        	if(size%100==0){
        		String line=new String(tt);
        		System.out.println(line.replace("\r\n", "")+";");
        		num=0;
        		if(fc.size()-size<100){
        			tt=new byte[Integer.valueOf((fc.size()-size)+"")];
        		}else{
        			tt=new byte[100];
        		}
        		
        	}
//        	byte[] tt=new byte[110];
//        	mbb.get(tt);
//        	String line=new String(tt);
//        	sb.append(line);
//        	num++;
//        	if(num%100==0){
//        		sb=new StringBuffer();
//        		break;
//        	}
////        	时间工具.休眠秒数(1);
        }
        System.out.println(sb);
//    	byte[] tt=new byte[size];
//    	mbb.get(tt);
    	
        System.out.println(fc.size()/1024);
        long timeEnd = System.currentTimeMillis();// 得到当前的时间  
        System.out.println("Read time1 :" + (timeEnd - timeStar) + "ms");  
        fc.close();  
        fis.close();  
        
        timeStar = System.currentTimeMillis();
        随机读取("D:/md_future/temp/test/ag1612_20160920.csv",1000);
        timeEnd = System.currentTimeMillis();
        System.out.println("Read time2 :" + (timeEnd - timeStar) + "ms");
    }  
    
	public static void 随机读取(String filePath,int lastMin){
		try {
			File f= new File(filePath);  
//			System.out.println("文件长度："+f.length()+" seek:"+(f.length()-127*60*lastMin));
			RandomAccessFile rAfile=new RandomAccessFile(filePath,"r");
			if(lastMin==0||f.length()<127*10){//开盘前5分钟读取所有数据,即使lastMin不为0，也读取全部
				;//读取所有数据
			}else{
				seek(rAfile,lastMin);//读取最后10行
				
			}
			
	    	String line=rAfile.readLine();
	    	while(line!=null){
	    		line=rAfile.readLine();
	    	}

		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(), e);
		}

	}
	
	private static void seek(RandomAccessFile raf,int n){
		try {
			int k=0;
	    	long len = raf.length();  
	    	if (len != 0L) {  
	    	  long pos = len - 1;  
	    	  while (pos > 0) {   
	    	    pos--;  
	    	    raf.seek(pos);  
	    	    if (raf.readByte() == '\n') {
	    	    	k++;
	    	    	if(k==n){
		    	      break;  
	    	    	}

	    	    }  
	    	  }  
	    	} 
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(), e);
		}
	}

}
