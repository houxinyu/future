package com.example.future.windows;

import java.io.IOException;

public class Reboot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String cmd = "cmd /c start D:\\dataserver\\run.bat";// pass
        try {
            Process ps = Runtime.getRuntime().exec(cmd);
//            ps.waitFor();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } 
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
	}

}
