package com.example.future.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DingDingMessageUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DingDingMessageUtil.class);
	
	   public static String access_token = "f8972b95c45cda32ede6416c01d37026ca2af40e946d40f2996772e61e5574be";
	   
	   //https://oapi.dingtalk.com/robot/send?access_token=f8972b95c45cda32ede6416c01d37026ca2af40e946d40f2996772e61e5574be
	   
	   
	   public static void sendTextMessage(String msg, String accessToken) {
		   if(accessToken == null) {
			   accessToken = access_token;
		   }
		   
		      try {
			         Message message = new Message();
			         message.setMsgtype("text");
			         message.setText(new MessageInfo(msg));
			         URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=" + accessToken);
			         // 建立http连接
			         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			         conn.setDoOutput(true);
			         conn.setDoInput(true);
			         conn.setUseCaches(false);
			         conn.setRequestMethod("POST");
			         conn.setRequestProperty("Charset", "UTF-8");
			         conn.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
			         conn.connect();
			         OutputStream out = conn.getOutputStream();//springBoot自带的Jackson
			         ObjectMapper mapper = new ObjectMapper();
			         String textMessage = mapper.writeValueAsString(message);
			         byte[] data = textMessage.getBytes("UTF-8");//防止乱码
			         out.write(data);
			         out.flush();
			         out.close();
			         System.out.println(conn.getResponseCode());
			         InputStream in = conn.getInputStream();
			         byte[] data1 = new byte[in.available()];
			         in.read(data1);
			         System.out.println(new String(data1));
			      } catch (Exception e) {
			         e.printStackTrace();
			      }
	   }

	   public static void sendTextMessage(String msg) {
		   sendTextMessage(msg, null);
	   }
	   
	   public static void main(String[] args) {
		 DingDingMessageUtil.sendTextMessage("test", "f0d33c246bba924fcd6ddc17f2c13b4dba43f83e52a9e7acacebbd08d7474a94");
	}
	}

	class Message {
	   private String msgtype;
	   private MessageInfo text;

	   public String getMsgtype() {
	      return msgtype;
	   }

	   public void setMsgtype(String msgtype) {
	      this.msgtype = msgtype;
	   }

	   public MessageInfo getText() {
	      return text;
	   }

	   public void setText(MessageInfo text) {
	      this.text = text;
	   }
	}

	class MessageInfo {
	   private String content;

	   public MessageInfo(String content) {
	      this.content = content;
	   }

	   public String getContent() {
	      return content;
	   }

	   public void setContent(String content) {
	      this.content = content;
	   }
	   

	}