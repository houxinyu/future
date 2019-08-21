package com.example.future.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.log4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * 数据库处理工具类
 * @author content
 * @version 1.0
 * create at 2012-5-8
 */

public class 数据库工具 {
	private static final Logger LOGGER = LoggerFactory.getLogger(配置文件.class);
//	private static Logger fileErr = Logger.getLogger("fileErr");
	static String url = "jdbc:mysql:///stock?useUnicode=true&characterEncoding=utf-8";
	static String userName = "root";// 登录数据库用户名
	static String password = "";// 用户密码
	static Connection con = null;// 数据库连接
	static Statement stat = null;
	static ResultSet rs = null;
	
	
	private static void 初始化数据库链接() {
		try {
			// 登录JDBC-ODBC驱动程序
			if (con == null) {
//				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(url, userName, password);
//				con = DriverManager.getConnection(url);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据查询语句查询数据库，返回结果集
	 * @param sql
	 * @return
	 */
	public static ResultSet 查询数据库(String sql) {
		if (con == null) {
			初始化数据库链接();
		}
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(sql);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return rs;
	}
	
	
	
	/**
	 * 根据更新语句修改数据库，每次更新一条记录
	 * @param sql
	 */
	public static void 更新数据库(String sql) {
		if (con == null) {
			初始化数据库链接();
		}
		try {
			stat = con.createStatement();
			stat.executeUpdate(sql);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * sqlList存储sql语句集合，对于大量的数据来说，进行批量的更新性能有明显提升
	 * @param sqlList
	 */
	public static void 批量更新数据库(ArrayList<String> sqlList){
		if(sqlList.size()==0){
			return;
		}
		try {
			con.setAutoCommit(false); 
//			PreparedStatement pst = (PreparedStatement) con.prepareStatement("insert into test04 values (?,'中国')"); 
			PreparedStatement pst =  (PreparedStatement)con.prepareStatement(sqlList.get(0));
			for (int i = 1; i < sqlList.size(); i++) { 
//			pst.setInt(1, i); 
			// 把一个SQL命令加入命令列表 
//			pst.addBatch(); 
			pst.addBatch(sqlList.get(i));
			} 
			// 执行批量更新 
			pst.executeBatch(); 
			// 语句执行完毕，提交本事务 
			con.commit(); 
			pst.close(); 
//			con.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}


	}
	

	public static void 关闭游标() {

		try {
			if (rs != null) {
				rs.close();
			}
			if (stat != null) {
				stat.close();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}


}
