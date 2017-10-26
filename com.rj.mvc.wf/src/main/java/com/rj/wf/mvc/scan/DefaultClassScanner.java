package com.rj.wf.mvc.scan;

import java.lang.annotation.Annotation;
import java.util.Set;

public class DefaultClassScanner implements ClassScanner {
	private static final DefaultClassScanner Instance = new DefaultClassScanner();
	private DefaultClassScanner() {
	}

	public static DefaultClassScanner getInstance() {
		return Instance;
	}

	@Override
	public Set<Class<?>> getClassList(String packageName, final String pattern) {
		return new DefaultClassFilter(packageName) {
			@Override
			public boolean filterCondition(Class<?> cls) {
				String className = cls.getName();
				String patternStr = (null == pattern || pattern.isEmpty()) ? ".*" : pattern;
				return className.startsWith(packageName) && className.matches(patternStr);
			}
		}.getClassList();
	}

	
	@Override
	public Set<Class<?>> getClassListByAnnotation(String packageName,
			Class<? extends Annotation> annotationClass) {
		return null;
	}

	@Override
	public Set<Class<?>> getClassListBySuper(String packageName,
			Class<?> superClass) {
		return null;
	}

	@Override
	public Set<Class<?>> getClassList(String packageName,
			String packagePattern, ClassLoader classLoader) {
		return null;
	}

	@Override
	public Set<Class<?>> getClassListByAnnotation(String packageName,
			Class<? extends Annotation> annotationClass, ClassLoader classLoader) {
		return null;
	}

	@Override
	public Set<Class<?>> getClassListBySuper(String packageName,
			Class<?> superClass, ClassLoader classLoader) {
		return null;
	}
	
}
