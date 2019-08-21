package com.example.future.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class 剪贴板工具 {
	/** 
	 * 从剪切板获得文字。 
	 */  
	public static String getSysClipboardText() {  
	    String ret = "";  
	    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();  
	    // 获取剪切板中的内容  
	    Transferable clipTf = sysClip.getContents(null);  

	     if (clipTf != null) {  
	         // 检查内容是否是文本类型  
	         if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {  
	             try {  
	                 ret = (String) clipTf  
	                         .getTransferData(DataFlavor.stringFlavor);  
	             } catch (Exception e) {  
	                 e.printStackTrace();  
	             }  
	         }  
	     }  

	     return ret;  
	 }  

	 /** 
	  * 将字符串复制到剪切板。 
	  */  
	 public static void setSysClipboardText(String writeMe) {  
	     Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
	     Transferable tText = new StringSelection(writeMe);  
	     clip.setContents(tText, null);  
	 } 
	 
	 /** 
	  * 清空剪切板。 
	  */  
	 public static void clearSysClipboardText() {  
	     Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
	     Transferable tText = new StringSelection(""); 
	     clip.setContents(tText, null);  
	 } 
	 
	 public static void main(String[] args) {
		 setSysClipboardText("002351");
		 try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		 clearSysClipboardText();
	}
}
