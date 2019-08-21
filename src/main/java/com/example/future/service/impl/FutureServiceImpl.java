package com.example.future.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.future.config.FutureConfig;
import com.example.future.macd.DataHandle2;
import com.example.future.service.FutureService;


@Service
public class FutureServiceImpl implements FutureService {
	
//	@Autowired
//	FutureConfig config;
	
	@Autowired
	DataHandle2 dataHandle2;

	@Override
	public ArrayList<String> futureAlertMain() {
		dataHandle2.clearHistoryData();
		dataHandle2.loadHistoryData();
		dataHandle2.loadTodayKEntity();
		dataHandle2.caculateMACD();
		return dataHandle2.caculateAlert();
	}
	

}
