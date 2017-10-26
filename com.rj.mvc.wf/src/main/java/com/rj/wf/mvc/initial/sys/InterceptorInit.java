package com.rj.wf.mvc.initial.sys;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rj.wf.mvc.Config;
import com.rj.wf.mvc.WFInterceptor;
import com.rj.wf.mvc.annotation.Interceptor.Scope;
import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;
import com.rj.wf.mvc.scan.DefaultClassScanner;

public final class InterceptorInit {
	private static final Logger logger = LoggerFactory.getLogger(InterceptorInit.class);
	private static List<WFInterceptor> globalInterceptors;
	public static List<WFInterceptor> getGlobalInterceptors() {
		return globalInterceptors;
	}

	public static void init() throws Exception {
		buildGlobalInterceptors();
		logger.info("WF init interceptors complete!");
	}
	// 初始化全局拦截器在Bootstrap的doFilter中实现拦截
	private static void buildGlobalInterceptors() throws Exception {
		logger.debug("实现拦截");
		Set<Class<? extends WFInterceptor>> interceptorsClasses = getIntercepter();
		List<WFInterceptor> interceptors = new ArrayList<WFInterceptor>();
		for (Class<? extends WFInterceptor> clazz : interceptorsClasses) {
			WFInterceptor interceptor = null;
			try {
				interceptor = clazz.newInstance();
				if (Scope.GLOBAL == interceptor.scope()) {
					interceptors.add(interceptor);
				}
			} catch (Exception e) {
				logger.error("Build global interceptor failed, Interceptor: " + clazz.getName());
				e.printStackTrace();
				throw new Exception("Build global interceptor failed!", e);
			}
		}

		// 根据order进行排序
		Collections.sort(interceptors, WFInterceptor.INTERCEPTOR_SORTER);
		for (WFInterceptor interceptor : interceptors) {
			logger.info("Load Global Interceptor : " + interceptor.getClass().getName());
		}

		globalInterceptors = interceptors;

	}

	@SuppressWarnings("unchecked")
	private static Set<Class<? extends WFInterceptor>> getIntercepter() throws Exception {
		Set<Class<?>> clazzSet = DefaultClassScanner.getInstance().getClassList(Config.getPackageSpace(), ".*\\.interceptors\\..*Interceptor");
		Set<Class<? extends WFInterceptor>> intercepters = new HashSet<Class<? extends WFInterceptor>>();
		for (Class<?> clazz : clazzSet) {
			if (WFInterceptor.class.isAssignableFrom(clazz) && !Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers()) && Modifier.isPublic(clazz.getModifiers())) {
				intercepters.add((Class<? extends WFInterceptor>) clazz);
			}
		}
		return intercepters;
	}

}
