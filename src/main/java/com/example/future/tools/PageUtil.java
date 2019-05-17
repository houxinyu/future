package com.example.future.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpState;
//import org.apache.commons.httpclient.HttpVersion;
//import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.params.HttpClientParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.core.ParseException;

/**
 * 网页处理工具类
 * @author content
 * @version 1.0
 * create at 2012-5-8
 */

public class PageUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(PageUtil.class);
//    public static HttpClient client = new HttpClient();
    
    public static String httpPost(String url, byte[] buf)
    {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {

//            String url = "http://" + host + ":" + port;
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000).setConnectTimeout(6000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("User-Agent", "Mozilla/5.0");

            ByteArrayEntity entity = new ByteArrayEntity(buf);
            httpPost.setEntity(entity);

            httpResponse = httpClient.execute(httpPost);

            reader = new BufferedReader(new InputStreamReader(
                    httpResponse.getEntity().getContent()));

            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

        }catch (Exception var){
            var.printStackTrace();
        }finally {
        	try {
                if(reader != null){
                    reader.close();
                }
        	}catch(Exception re) {
        		
        	}
        	
        	try {
                if(httpResponse != null){
                    httpResponse.close();
                }
        	}catch(Exception re) {
        		
        	}
        	
        	
        	try {
        		httpClient.close();
        	}catch(Exception re) {
        		
        	}

            
        }
        return response.toString();

    }
    
    public static String httpGet(String url) {  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        
        String content = "";
        try {  
            // 创建httpget.    
            HttpGet httpget = new HttpGet(url);  
//            System.out.println("executing request " + httpget.getURI());  
            LOGGER.info("url:" + url);
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity();  
                // 打印响应状态    
                LOGGER.info("status:" + response.getStatusLine());
                if (entity != null) {  
//                    // 打印响应内容长度    
//                    System.out.println("Response content length: " + entity.getContentLength());  
//                    // 打印响应内容    
//                    System.out.println("Response content: " + EntityUtils.toString(entity));  
                    
                    content = EntityUtils.toString(entity);  
                }  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        
        return content;
    }
	public static HttpURLConnection get(String url) {
//		String accept = "application/xml";
//		try {
//			URL uRL = new URL(url);
//			HttpURLConnection httpConnection = (HttpURLConnection) uRL
//					.openConnection();
////			httpConnection.setRequestProperty("Charset", "gbk");
//			httpConnection.setConnectTimeout(3000);
//			httpConnection.setReadTimeout(3000);
//			httpConnection.setDoInput(true);
//			httpConnection.setDoOutput(true);
//			httpConnection.setRequestProperty("Accept", accept);
//			httpConnection.setRequestProperty("Content-Type", "text/xml");
//			httpConnection.setRequestMethod("GET");
//			httpConnection.getResponseCode();
//			return httpConnection;
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage(), e);
//		}
		return null;
	}
	
	
    /**
     * 按照指定的编码(encoding)方式抓取指定地址(url)网页数据，返回网页的html源代码
     * @param url
     * @param encoding
     * @return
     */
    public static String getHtml(String url, String encoding) {
        StringBuffer sb = new StringBuffer();
//        try {
//            
//            HttpClientParams clientParams = new HttpClientParams();
//            clientParams.setHttpElementCharset(encoding);
//            HttpState httpState = new HttpState();
//            client.setParams(clientParams);
//            client.setState(httpState);
//            clientParams.setVersion(HttpVersion.HTTP_1_1);
//
//            
//            //1.
//            GetMethod get = null;
//            BufferedReader reader = null;
//            String s = null;
//            get = new GetMethod(url);
//            client.executeMethod(get);
//            reader = new BufferedReader(new InputStreamReader(get
//                    .getResponseBodyAsStream(), encoding));
//            s = reader.readLine();
//
//
//            while (s != null) {
//                sb.append(s + "\n");
//                s = reader.readLine();
//            }
//            
//        } catch (Exception e) {
//        	日志工具.fileErr.error(e, e);
//        }
        return sb.toString();
    }
    

    
    public static String postHtml(String url, String encoding,
            String[] paraNames, String[] paraValues) {
        StringBuffer sb = new StringBuffer();
//        try {
//            HttpClient client = new HttpClient();
//
//            HttpClientParams clientParams = new HttpClientParams();
//            clientParams.setParameter("http.useragent",
//                    "Opera/9.26 (Windows NT 5.1; U; zh-cn)");
//            client.getHttpConnectionManager().getParams().setSoTimeout(
//                    30 * 1000);
//            clientParams.setHttpElementCharset(encoding);
//            HttpState httpState = new HttpState();
//            client.setParams(clientParams);
//            client.setState(httpState);
//            clientParams.setVersion(HttpVersion.HTTP_1_1);
//
//            BufferedReader reader = null;
//            String s = null;
//            PostMethod post = new PostMethod(url);
//            NameValuePair[] nameValuePair = new NameValuePair[paraNames.length];
//            for (int i = 0; i < nameValuePair.length; i++) {
//                nameValuePair[i] = new NameValuePair(paraNames[i],
//                        paraValues[i]);
//            }
//
//            post.setRequestBody(nameValuePair);
//            client.executeMethod(post);
//
//            reader = new BufferedReader(new InputStreamReader(post
//                    .getResponseBodyAsStream(), encoding));
//            s = reader.readLine();
//
//            while (s != null) {
//                sb.append(s + "\n");
//                s = reader.readLine();
//            }
//
//        } catch (Exception e) {
//        	日志工具.fileErr.error(e, e);
//        }
        return sb.toString();
    }
    
    
    public static String 从网页中抽取属性(String startStr,String endStr,String pageContent){
    	int zgbFrom=pageContent.indexOf(startStr)+startStr.length();
    	int zgbEnd=pageContent.indexOf(endStr, zgbFrom);
    	String 属性=pageContent.substring(zgbFrom, zgbEnd);
    	属性=Html2Text(属性).trim();
    	return 属性;
    }
    
    /**
     * 把html源代码数据转换成普通文本数据，过滤掉html代码
     * @param inputString
     * @return
     */
	public static String Html2Text(String inputString) {   
        String htmlStr = inputString; // 含html标签的字符串   
        String textStr = "";   
        java.util.regex.Pattern p_script;   
        java.util.regex.Matcher m_script;   
        java.util.regex.Pattern p_style;   
        java.util.regex.Matcher m_style;   
        java.util.regex.Pattern p_html;   
        java.util.regex.Matcher m_html;   
  
        java.util.regex.Pattern p_html1;   
        java.util.regex.Matcher m_html1;   
  
        try {   
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>   
            // }   
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>   
            // }   
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式   
            String regEx_html1 = "<[^>]+";   
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);   
            m_script = p_script.matcher(htmlStr);   
            htmlStr = m_script.replaceAll(""); // 过滤script标签   
  
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);   
            m_style = p_style.matcher(htmlStr);   
            htmlStr = m_style.replaceAll(""); // 过滤style标签   
  
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);   
            m_html = p_html.matcher(htmlStr);   
            htmlStr = m_html.replaceAll(""); // 过滤html标签   
  
            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);   
            m_html1 = p_html1.matcher(htmlStr);   
            htmlStr = m_html1.replaceAll(""); // 过滤html标签   
  
            textStr = htmlStr;   
  
        } catch (Exception e) {   
        	LOGGER.error(e.getMessage(), e);
        }   
  
        return textStr;// 返回文本字符串   
    }   



}
