package com.example.future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.future.config.FutureConfig;
import com.example.future.service.MailService;
import com.example.future.tools.DingDingMessageUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FutureApplicationTests {
	@Autowired
	private MailService mailService;
	
	@Autowired
	FutureConfig config;

	@Test
	public void contextLoads() {
	}
	
//	@Test
//	public void sendMailTest() {
//		
//		mailService.sendMail("test");
//	}
	
	@Test
	public void sendToDingTalk() {
		System.out.println("==========================");
		System.out.println(config.getAccessToken());
		DingDingMessageUtil.sendTextMessage("[MA0郑醇 ZC0热卷]", config.getAccessToken());
	}

}
