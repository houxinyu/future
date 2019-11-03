package com.example.future.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonUtils {
	
	static JSONArray symbolArray = null;
	static Map<String,Integer> typeMap = new HashMap<>();
	
	/**
     * 读取json文件，返回json串
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static JSONArray readSymbols() {
    	if(symbolArray == null) {
    		synchronized(JSONArray.class) {
    			if(symbolArray == null) {
    		    	String path = JsonUtils.class.getClassLoader().getResource("symbol.json").getPath();
    		        String s = JsonUtils.readJsonFile(path);
    		        symbolArray = JSON.parseArray(s);
    		        
    		        for(Object o:symbolArray) {
    		        	JSONObject jo = (JSONObject) o;
    		        	typeMap.put(jo.getString("Symbol"), Integer.valueOf(jo.getIntValue("Type")));
    		        }
    			}
    		}
    	}
        return symbolArray;
    }
    
    public static JSONArray readDataArray(String fullSymbol) {
    	JSONArray dataArray;
    	String path = JsonUtils.class.getClassLoader().getResource(fullSymbol + ".json").getPath();
        String s = JsonUtils.readJsonFile(path);
        dataArray = JSON.parseArray(s);
        return dataArray;
    }
    
    public static int getType(String symbol) {
    	Integer type = typeMap.get(symbol);
    	if(type == null) {
    		return 0;
    	}else {
    		return type;
    	}
    }
    
    public static void main(String[] args) throws  Exception{
        JSONArray symbolArray = readSymbols();
        for (int i = 0 ; i < symbolArray.size();i++){
            JSONObject key1 = (JSONObject)symbolArray.get(i);
            String fullSymbol = (String)key1.get("FullSymbol");
            String symbol = (String)key1.get("Symbol");
            System.out.println(fullSymbol + "," + symbol);
        }
    }
}
