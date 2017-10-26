package com.rj.wf.mvc.scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public interface MethodScanner {

	/**
	 * 获取一个类上方法名符合正则的所有的方法
	 */
	List<Method> getMethodList(Class<?> clazz, String methodPattern);

	/**
	 * 获取一个类上有期望Annotation的所有方法
	 */
	List<Method> getMethodListByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass);

	/**
	 * 获取一个类及接口类上有期望Annotation的所有方法
	 */
	List<Method> getMethodListByAnnotationInterface(Class<?> clazz, Class<? extends Annotation> annotationClass);
	
}
