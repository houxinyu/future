package com.example.future.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.future.scheduler.FutureSchedule;
import com.example.future.service.MailService;


@RestController
public class AdminController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private FutureSchedule schedule;

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String index() {
		// Service Instance instance = client.getLocalServiceinstance();
		// logger.info("/hello, host:" + instance.getHost() + instance.getServiceid());

		LOGGER.info("provide ....");
		return "Provide 1...";
	}
	
	@RequestMapping(value = "/sendMail", method = RequestMethod.GET)
	public String sendMail(@RequestParam String content) {

		LOGGER.info("invoke sendMail,content " + content);
		return mailService.sendMail(content);
	}
	
	@RequestMapping(value = "/sendAlertMail", method = RequestMethod.POST)
	public String sendMail2(@RequestBody String content) {
		
		LOGGER.info("invoke sendAlertMail,content " + content);
		return mailService.sendMail(content);
	}

}
