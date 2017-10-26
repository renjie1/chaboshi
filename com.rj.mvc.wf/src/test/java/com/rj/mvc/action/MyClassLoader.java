package com.rj.mvc.action;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class MyClassLoader extends ClassLoader {

	private String path;
	public MyClassLoader(String _path) {
		super();
		this.path = _path;
	}

	
	
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//通过 文件输入流 读取 指定的class文件
		String file = path+"/"+name+".class";
		try {
			FileInputStream fis = new FileInputStream(file);
			//将读取的class文件对应的 字节数据 写入到内存中
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int i = 0;
			while ((i = fis.read())!=-1) {
				out.write(i);
			}
			fis.close();
			byte[] buf = out.toByteArray();//提取 写到内存中的字节数据到数组
			//  public byte[] toByteArray()创建一个新分配的 byte 数组。其大小是此输出流的当前大小，并且缓冲区的有效内容已复制到该数组中。 
			return defineClass(buf, 0, buf.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.findClass(name);
	}
	

}
