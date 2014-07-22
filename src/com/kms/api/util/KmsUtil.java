package com.kms.api.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KmsUtil {
	
	public static Properties getProp(String path) {
		InputStream is = null;
		Properties prop = new Properties();
		try {
			is = new FileInputStream(path);
			prop.load(is);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return prop;
	}
	
	public static String isNull(Object str){
		return isNull(str, "");
	}
	
	public static String isNull(Object str, String def){
		if(str == null || "".equals((String.valueOf(str)).trim()) || "null".equals((String.valueOf(str)).trim()))
			return def;
		else
			return String.valueOf(str);
	}
	
}