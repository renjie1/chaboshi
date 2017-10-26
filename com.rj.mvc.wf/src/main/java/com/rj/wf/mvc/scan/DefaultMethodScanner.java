package com.rj.wf.mvc.scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class DefaultMethodScanner implements MethodScanner {

	private static final DefaultMethodScanner Instance = new DefaultMethodScanner();

	private DefaultMethodScanner() {
	}
	
	public static DefaultMethodScanner getInstance() {
		return Instance;
	}
	
	@Override
	public List<Method> getMethodList(Class<?> clazz, String methodPattern) {
		return new PatternNameMethodFilter(clazz, methodPattern) {

			@Override
			public boolean filterCondition(Method method) {
				return method.getName().matches(methodPattern);
			}
		}.getMethodList();
	}

	@Override
	public List<Method> getMethodListByAnnotation(Class<?> clazz,
			Class<? extends Annotation> annotationType) {
		return new AnnotationMethodFilter(clazz, annotationType) {

			@Override
			public boolean filterCondition(Method method) {
				return method.isAnnotationPresent(annotationType);
			}
		}.getMethodList();
	}

	@Override
	public List<Method> getMethodListByAnnotationInterface(Class<?> clazz,
			Class<? extends Annotation> annotationClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
