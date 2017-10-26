package com.rj.wf.mvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rj.wf.mvc.initial.sys.ConfigInit;

public class Config {

	private static Map<String, String> properties = new ConcurrentHashMap<String, String>();
	
	public static boolean isONLINE() {
		return "ONLINE".equals(ConfigInit.getMODE());
	}
	
	public static boolean isOFFLINE() {
		return !isONLINE();
	}
	
	/**
	 * 获取根路径
	 * 
	 * @return
	 */
	public static String getRootPath() {
		return isONLINE() ? ConfigInit.getDISK() + "/opt/web/" + ConfigInit.getCLUSTER()
		:ConfigInit.getDISK() + "/opt/web";
	}
	
	
	/**
	 * 获得集群名
	 * 
	 * @return
	 */
	public static String getCluster() {
		return ConfigInit.getCLUSTER();
	}
	
	
	/**
	 * 获得扫包前缀
	 * 
	 * @return
	 */
	public static String getPackageSpace() {
		return ConfigInit.getPACKAGESPACE();
	}
	
	
	
	/**
	 * 获得远程日志配置
	 * 
	 * @return
	 */
	public static String getLogSpaceURL() {
		return ConfigInit.getLOGSPACEURL();
	}
	
	
	
	/**
	 * 获取配置路径
	 * 
	 * @return
	 */
	public static String getConfigFolder() {
		return getRootPath() + "/wf/conf/";
	}

	
	/**
	 * 获取WF日志路径
	 * 
	 * @return
	 */
	public static String getLogPath() {
		return getRootPath() + "/logs/";
	}

	public static String getProperty(String key) {
		return properties.get(key);
	}

	
	public static void setProperty(String key, String value) throws Exception {
		if (properties.get(key) != null)
			throw new Exception("Property key exist: " + key);

		properties.put(key, value);
	}

	
	
}
