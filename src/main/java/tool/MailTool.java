package tool;
//import javax.mail.Authenticator;
//import javax.mail.Message;  
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;  
//import javax.mail.Transport;  
//import javax.mail.Message.RecipientType;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;  
 
//import createMails.WithAttachmentMessage;  
/**  
 * 邮件发送程序  
 * @author  
 */ 
public class MailTool {  
	private static final Logger LOGGER = LoggerFactory.getLogger(MailTool.class);
    String smtpServer = "smtp.qq.com";  
    String user = "149378631@qq.com";  
//    String pwd = "uhpksqkdvirnbhha"; 
//    String pwd="epjpixygfybobiii";//POP3
    
    private static final String MAIL_SENDER = "houxinyu";
    
//    @Autowired
//    private static JavaMailSenderImpl jmailSender;
 
    /**  
     * 创建Session对象，此时需要配置传输的协议，是否身份认证  
     */ 
//    public Session createSession(String protocol) {  
//        Properties property = new Properties();  
//        property.setProperty("mail.transport.protocol", protocol);  
//        property.setProperty("mail.smtp.auth", "true");  
// 
//        Session session = Session.getInstance(property);  
//          
//        // 启动JavaMail调试功能，可以返回与SMTP服务器交互的命令信息  
//        // 可以从控制台中看一下服务器的响应信息  
////        session.setDebug(true);   
//        return session;  
//    }  
 
    /**  
     * 传入Session、MimeMessage对象，创建 Transport 对象发送邮件  
     */ 
//    public void sendMail(Session session, MimeMessage msg) throws Exception {  
//          
//        // 设置发件人使用的SMTP服务器、用户名、密码  
////        String smtpServer = "smtp.163.com";  
////        String user = "onlyhxy@163.com";  
////        String pwd = "134119.xz";  
//        
////        String smtpServer = "smtp.qq.com";  
////        String user = "149378631@qq.com";  
////        String pwd = "uhpksqkdvirnbhha";  
// 
//        // 由 Session 对象获得 Transport 对象  
//        Transport transport = session.getTransport();  
//        // 发送用户名、密码连接到指定的 smtp 服务器  
//        transport.connect(smtpServer, user, pwd);  
// 
//        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));  
//        transport.close();  
//    } 
    
    public static void sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //邮件发送人
            simpleMailMessage.setFrom(MAIL_SENDER);
            //邮件接收人
            simpleMailMessage.setTo(to);
            //邮件主题
            simpleMailMessage.setSubject(subject);
            //邮件内容
            simpleMailMessage.setText(content);
//            jmailSender.send(simpleMailMessage);
        } catch (Exception e) {
            LOGGER.error("邮件发送失败", e);
        }
    }
    
    public static void sendMail(String content,String subject,String to,String cc){
//    	try {
////            String from = "onlyhxy@163.com";  
//            String from = "149378631@qq.com";
////            String to = "149378631@qq.com";  
////            String subject = "撑压数值";
//            
//        	MailTool sender = new MailTool();  
//        	Session session = sender.createSession("smtp"); 
//            MimeMessage msg = new MimeMessage(session);  
//            msg.setFrom(new InternetAddress(from));  
//            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));  
//            msg.setRecipient(Message.RecipientType.CC, new InternetAddress(cc));
//            msg.setSubject(subject);
//            msg.setContent(content, "text/html;charset=UTF-8");
////            msg.setFileName(arg0);
//        	sender.sendMail(session, msg);
//		} catch (Exception e) {
//			// TODO: handle exception
//			LOGGER.error(e.getMessage(),e);
//		}

    }
    
    
    public static void qqMail(String content,String subject,String toMailAdd,String ccMailAdd) throws Exception{
//    	//创建Properties 类用于记录邮箱的一些属性
//        final Properties props = new Properties();
//        // 表示SMTP发送邮件，必须进行身份验证
//        props.put("mail.smtp.auth", "true");
//        //此处填写SMTP服务器
//        props.put("mail.smtp.host", "smtp.qq.com");
//        //端口号，QQ邮箱给出了两个端口，但是另一个我一直使用不了，所以就给出这一个587
//        props.put("mail.smtp.port", "587");
//        // 此处填写你的账号
//        props.put("mail.user", "149378631@qq.com");
//        // 此处的密码就是前面说的16位STMP口令
//        props.put("mail.password", "epjpixygfybobiii");
//
//        // 构建授权信息，用于进行SMTP进行身份验证
//        Authenticator authenticator = new Authenticator() {
//
//            protected PasswordAuthentication getPasswordAuthentication() {
//                // 用户名、密码
//                String userName = props.getProperty("mail.user");
//                String password = props.getProperty("mail.password");
//                return new PasswordAuthentication(userName, password);
//            }
//        };
//        // 使用环境属性和授权信息，创建邮件会话
//        Session mailSession = Session.getInstance(props, authenticator);
//        // 创建邮件消息
//        MimeMessage message = new MimeMessage(mailSession);
//        // 设置发件人
//        InternetAddress form = new InternetAddress(
//                props.getProperty("mail.user"));
//        message.setFrom(form);
//
//        // 设置收件人的邮箱
//        InternetAddress to = new InternetAddress(toMailAdd);
//        message.setRecipient(RecipientType.TO, to);
//        
//        if(ccMailAdd!=null&&!ccMailAdd.equals("")){
//            InternetAddress cc = new InternetAddress(ccMailAdd);
//            message.setRecipient(RecipientType.CC, cc);
//        }
//
//        // 设置邮件标题
//        message.setSubject(subject);
//
//        // 设置邮件的内容体
//        message.setContent(content, "text/html;charset=UTF-8");
//
//        // 最后当然就是发送邮件啦
//        Transport.send(message);
    }
 
    // 测试：发送邮件  
    public static void main(String[] args) throws Exception {  
//    	sendMail("点点滴滴","撑压数值","149378631@qq.com");
    	
//    	qqMail();
    	
    	sendSimpleMail("onlyhxy@163.com", "testsubject", "testcontent");
    	
    }  
} 