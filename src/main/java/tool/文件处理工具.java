package tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 文件处理工具类
 * @author content
 * @version 1.0
 * create at 2012-5-8
 */

public class 文件处理工具 {
	private static final Logger LOGGER = LoggerFactory.getLogger(文件处理工具.class);
    public static ArrayList<String> 读取文本数据(String path){
		InputStream is;
		ArrayList<String> hs=new ArrayList<String>();
		try {
//			is = new FileInputStream("c:/tmp/s.txt");
			is = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			String line=reader.readLine();
			while(line!=null){
				//System.out.println(line);
				if(line.indexOf("#")==-1){
					hs.add(line);
				}
				line=reader.readLine();
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return hs;
    }
    
    public static String 读取文件最后一行(String filePath){
    	String lastLine="";
    	RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(filePath, "r");
	    	long len = raf.length();  
	    	if (len != 0L) {  
	    	  long pos = len - 1;  
	    	  while (pos > 0) {   
	    	    pos--;  
	    	    raf.seek(pos);  
	    	    if (raf.readByte() == '\n') {
	    	      lastLine = raf.readLine();  
	    	      break;  
	    	    }  
	    	  }  
	    	}  
	    	raf.close();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

    	return lastLine;
    }
    
    public static ArrayList<String> 读取文件最后N行(String filePath,int n){
    	ArrayList<String> list=new ArrayList<String>();
    	String line="";
    	RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(filePath, "r");
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
	  	    	      line = raf.readLine();  
	  	    	      while(line!=null){
	  	    	    	  
	  	    	    	  if(line.length()!=0){
//	  	    	    		System.out.println(line);
	  	    	    		list.add(line);
	  	    	    	  }
	  	    	    	
	  	    	    	line = raf.readLine();  
	  	    	      }
		    	      break;  
	    	    	}

	    	    }  
	    	  }  
	    	}  
	    	raf.close();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

    	return list;
    }
    
    public static String 读取文件第一行(String filePath){
    	String firstLine="";
    	RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(filePath, "r");
	    	firstLine=raf.readLine();
	    	raf.close();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

    	return firstLine;
    }
    
    
    public static ArrayList<String> 读取文本数据(String path,String code){
		InputStream is;
		ArrayList<String> hs=new ArrayList<String>();
		try {
//			is = new FileInputStream("c:/tmp/s.txt");
			is = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,code));
			String line=reader.readLine();
			while(line!=null){
				//System.out.println(line);
				if(line.indexOf("2016")!=-1){
					hs.add(line.trim().replace("  ", "-").replace(" ", "").replace("--", "-"));
				}
				line=reader.readLine();
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return hs;
    }
    
    public static ArrayList<String> 读取字符串数据(String content){
		InputStream is;
		ArrayList<String> hs=new ArrayList<String>();
		try {
//			is = new FileInputStream("c:/tmp/s.txt");
//			is = new FileInputStream(path);
			String osString="GBK";
			
			if(new File("/data/nfs/xzgl/").exists()){
				osString="UTF-8";
			}
			
			is=字符串工具.String2InputStream(content);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,osString));
			String line=reader.readLine();
			while(line!=null){
				//System.out.println(line);
				hs.add(line);
				line=reader.readLine();
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return hs;
    }
    

//  新建一个文件夹 
    public static void newFolder(String folderPath) { 
        try { 
          String filePath = folderPath; 
          File myFilePath = new File(filePath); 
          if (!myFilePath.exists()) { 
            myFilePath.mkdir(); 
          } 
        } catch (Exception e) { 
        	LOGGER.error(e.getMessage(), e);
        } 
      } 

//    删除文件夹 
    public static void delFolder(String folderPath){ 
        try{ 
        	String filePath = folderPath; 
        	File delPath = new File(filePath); 
        	delPath.delete(); 
        }catch (Exception e) { 
        	LOGGER.error(e.getMessage(), e);
        } 
    } 

//    新建文件 
    public static void createFile(String fileName){ 
        try{ 
        	File myFileName = new File(fileName); 
        	if (!myFileName.exists()) { 
        		myFileName.createNewFile(); 
        	} 
        }catch (Exception e) { 
        	LOGGER.error(e.getMessage(), e);
        } 
    } 
    
    public static boolean 文件是否存在(String fileName){
    	File myFileName = new File(fileName); 
    	if(myFileName.exists()){
    		return true;
    	}
    	return false;
    }

//    删除文件 
    public static void delFile(String fileName){ 
        try{ 
        	File myFileName = new File(fileName); 
        	myFileName.delete(); 
        }catch (Exception e) { 
        	LOGGER.error(e.getMessage(), e);
        } 
    } 
    
    public static void renameFile(String oldFileName,String newFileName){
        try{ 
        	File oldFile = new File(oldFileName); 
        	if(oldFile.exists()){
        		oldFile.renameTo(new File(newFileName));
        	}else{
        		System.out.println(oldFileName+"文件不存在。");
        	}
        }catch (Exception e) { 
        	e.printStackTrace();
        } 
    }
    
    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
    
    public static void 存储信息到文件(String fileName,String text){
    	try {
    		OutputStream os=null;
    		BufferedWriter writer=null;
    		boolean isAppend=false;
//    		String fileName=path+时间工具.获得今日日期()+".txt";
    		File file=new File(fileName);
    		if(file.exists()){
    			isAppend=true;
    		}
    		String osString="GBK";
			if(isAppend){
				os = new FileOutputStream(fileName,true);
				writer = new BufferedWriter(new OutputStreamWriter(os,osString));
			}else{
				os = new FileOutputStream(fileName);
				writer = new BufferedWriter(new OutputStreamWriter(os,osString));
			}
			writer.append(text);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage(), e);
		}
    }
    
    public static void 整理训练文件(){
    	String folderName="E:/cache/360云盘/文档/工作文档/图片处理/修改测试/训练文件1/";
    	String folderName2="E:/cache/360云盘/文档/工作文档/图片处理/修改测试/训练文件2/";
    	File[] list = new File(folderName).listFiles();
    	Arrays.sort(list, new FileSorter(FileSorter.TYPE_SIZE_DOWN));
    	System.out.println("文件大小\t\t文件修改日期\t\t文件类型\t\t文件名称");
    	for (int i=0;i<list.length;i++) {
//    		System.out.println(list[i].length() + "\t\t" + new Date(list[i].lastModified()).toString() + "\t\t" + (list[i].isDirectory() ? "目录" : "文件") + "\t\t" +  list[i].getName() ); 
    		try {
    			String name="";
    			if(i<10){
    				name="00"+(i+1);
    			}else if(i<100){
    				name="0"+(i+1);
    			}else{
    				name=""+(i+1);
    			}
				copyFile(new File(folderName+list[i].getName()),new File(folderName2+name+"_2.exe"));
				if(list[i].getName().indexOf('j')!=-1){
					copyFile(new File("E:/cache/360云盘/文档/工作文档/图片处理/修改测试/焦炭K线图/"+list[i].getName().replace("j", "").replace(".exe", "_1.png")),new File(folderName2+name+"_1.png"));
					copyFile(new File("E:/cache/360云盘/文档/工作文档/图片处理/修改测试/焦炭K线图/"+list[i].getName().replace("j", "").replace(".exe", "_3.png")),new File(folderName2+name+"_3.png"));
				}else if(list[i].getName().indexOf('p')!=-1){
					copyFile(new File("E:/cache/360云盘/文档/工作文档/图片处理/修改测试/棕榈油K线图/"+list[i].getName().replace("p", "").replace(".exe", "_1.png")),new File(folderName2+name+"_1.png"));
					copyFile(new File("E:/cache/360云盘/文档/工作文档/图片处理/修改测试/棕榈油K线图/"+list[i].getName().replace("p", "").replace(".exe", "_3.png")),new File(folderName2+name+"_3.png"));
				}else if(list[i].getName().indexOf('r')!=-1) {
					copyFile(new File("E:/cache/360云盘/文档/工作文档/图片处理/修改测试/橡胶K线图/"+list[i].getName().replace("r", "").replace(".exe", "_1.png")),new File(folderName2+name+"_1.png"));
					copyFile(new File("E:/cache/360云盘/文档/工作文档/图片处理/修改测试/橡胶K线图/"+list[i].getName().replace("r", "").replace(".exe", "_3.png")),new File(folderName2+name+"_3.png"));
				}else{
					System.out.println("出错了....");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    public static void main(String[] args) {

    	while(true){
    	File[] files=new File("D:/md_future/temp/test").listFiles();
    	long start=时间工具.获得现在时间();
    	ArrayList<String> list=new ArrayList<String>();
    	int h=0;
//    	System.out.println("dc,y1701,2016-09-20 14:59:58.666,6380.0000,769818,-160,16072680.0000,252.0000,46,206,????,B,6378.0000,6380.0000,93,27".length());
//    	System.out.println(117*2000000/1000/1000);
    		h++;
    		
        	for(int i=0;i<files.length;i++){
            	list.addAll(读取文件最后N行(files[i].getAbsolutePath(),10));
        	}
//        	list.add("dc,y1701,2016-09-20 14:59:58.666,6380.0000,769818,-160,16072680.0000,252.0000,46,206,????,B,6378.0000,6380.0000,93,27");
//        	if(h==2000000){
//        		break;
//        	}

//    	System.out.println("行数："+list.size());
    	long end=时间工具.获得现在时间();
    	System.out.println(时间工具.耗时毫秒(start, end));
    	

    	}

    	
	}

}
