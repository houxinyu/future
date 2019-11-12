package com.example.future.service;

import java.util.ArrayList;

public interface FutureService {
	
	public  ArrayList<String> futureAlertMain();
	
	public  ArrayList<String> futureAlertFiveMin();
	
	public  ArrayList<String> futureAlertDay(int min, int type);
	
	public  ArrayList<String> futureAlert(int min, int type);
	
	public  void getFiftenDatas(int min, int type);
	
	public  void getHourDatas(int min, int type);

}
