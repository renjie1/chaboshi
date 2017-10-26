package com.rj.wf.mvc.initial.sys;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Test;

public class TestConfigInit {

	@Test
	public void testSystemClassLoader() throws IOException {
		String path = TestConfigInit.class.getResource("/space.properties").toString();
		System.out.println(path);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = cl.getResourceAsStream(path);
		PropertyResourceBundle pp = new PropertyResourceBundle(inputStream);
		Enumeration<String> en = pp.getKeys();
		while (en.hasMoreElements()) {
			//String key = (String) en.nextElement();
			System.out.println(en.nextElement());
		}
	}
	
	
	@Test
	public void test(){
		String user = System.getProperty("user.dir");
		System.out.println(user);
		File file = new File(user);
		String path = file.getAbsolutePath();
		System.out.println(path.substring(0, path.indexOf("\\")));
	}
	
	@Test
	public void loginit(){
		String loggerPath = Thread.currentThread().getContextClassLoader().getResource("conf").getPath();
		System.out.println(loggerPath + "/wflogger.xml");
		DOMConfigurator.configure(loggerPath + "/wflogger.xml");
		Logger logger = Logger.getLogger(TestConfigInit.class);
		logger.info("初始化成功");
	}
	
	
}
