package com.example.future.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.future.service.MailService;

@Service
public class MailServiceImpl implements MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
	
	@Autowired
	private JavaMailSender mailSender;

	@Override
	public String sendMail(String content) {
		
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //邮件发送人
            simpleMailMessage.setFrom("149378631@qq.com");
            //邮件接收人
            simpleMailMessage.setTo("onlyhxy@163.com");
            //邮件主题
            simpleMailMessage.setSubject("品种预警");
            
            //邮件内容
            simpleMailMessage.setText(content);
            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            LOGGER.error("邮件发送失败", e);
            return "send mail fail!";
        }
		LOGGER.info("provide ....");
		
		return "send mail success!";
	}
	
	


}
