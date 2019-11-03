package com.example.future.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.future.macd.DataHandle2;
import com.example.future.macd.DataHandle3;
import com.example.future.macd.DataHandleApi;
import com.example.future.service.FutureService;


@Service
public class FutureServiceImpl implements FutureService {
	
//	@Autowired
//	FutureConfig config;
	
	@Autowired
	DataHandle2 dataHandle2;
	
	@Autowired
	DataHandle3 dataHandle3;
	
	@Autowired
	DataHandleApi dataHandleApi;

	@Override
	public ArrayList<String> futureAlertMain() {
		//dataHandle2.clearHistoryData();
		//加载历史数据
		dataHandle2.loadHistoryData();
		//加载当前数据
		dataHandle2.loadTodayKEntity();
		dataHandle2.caculateMACD();
		return dataHandle2.caculateAlert();
	}
	
	@Override
	public ArrayList<String> futureAlertFiveMin() {
		//dataHandle3.clearHistoryData();
		dataHandle3.loadHistoryData();
		//dataHandle3.loadTodayKEntity();
		dataHandle3.caculateMACD();
		return dataHandle3.caculateAlert();
	}
	
//	@Override
//	public ArrayList<String> futureAlert(int min) {
//		//dataHandle3.clearHistoryData();
//		dataHandleAll.loadHistoryData(min);
//		//dataHandle3.loadTodayKEntity();
//		dataHandleAll.caculateMACD(min);
//		return dataHandleAll.caculateAlert(min);
//	}
	

	/**
	 * 针对30分钟和60分钟
	 */
	@Override
	public ArrayList<String> futureAlert(int min, int type) {
		//从内存缓存获取第二页15分钟历史数据
		dataHandleApi.loadHistoryFromMem();
		//获取最新一页15分钟历史数据
		dataHandleApi.loadHistoryData("15M", type);
		//使用15分钟k线合并半小时和1小时数据
		
		//上面的数据如何不重复获取？
		dataHandleApi.mergeData(min, type);
		
		dataHandleApi.caculateMACD(min, type);
		
		return dataHandleApi.caculateAlert(min, type);
	}
	
	//针对每天的数据
	@Override
	public ArrayList<String> futureAlertDay(int min, int type) {
		dataHandleApi.loadHistoryData("D", type);
		dataHandleApi.caculateMACD(min, type);
		return dataHandleApi.caculateAlert(min, type);
	}
	

}
