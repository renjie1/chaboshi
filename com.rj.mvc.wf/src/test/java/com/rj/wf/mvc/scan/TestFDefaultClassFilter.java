package com.rj.wf.mvc.scan;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.junit.Test;

public class TestFDefaultClassFilter {

	@Test
	public void testGetClassList() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		String packageName = "com.chaboshi";
		String path = packageName.replace(".", "/");
		System.out.println(path);
	}


	@Test
	public void testReplace() throws IOException {
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("com/rj");
		while (resources.hasMoreElements()) {
			URL url = (URL) resources.nextElement();
			System.out.println(url.toString());
		}
	}
	
	
	@Test
	public void testAddClass() {
		String filePath = "D:/opt/eclipse-chaboshi/com.rj.mvc.wf/target/test-classes/com/rj/wf/mvc/scan";
		System.out.println(filePath);
		File[] files = new File(filePath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
		for(File f1: files) {
			if(f1.isFile()) {
			} else {
			}
		}
	}
	
	
}
