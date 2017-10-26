package com.rj.mvc.action;

import org.junit.Test;

public class TestActionInit {

	@Test
	public void testClass() throws ClassNotFoundException {
		String path = "C:/Users/Administrator/Desktop/1013上线/web/www/controllers";
		//File file = new File(path);
		String name = "WebController";
		MyClassLoader mcl = new MyClassLoader(path);
		Class<?> cc = mcl.findClass(name);
		System.out.println(cc.getName());
	}
	
	
	
}