package com.rj.wf.mvc.initial.sys;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.rj.wf.mvc.AppInit;
import com.rj.wf.mvc.Config;
import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;
import com.rj.wf.mvc.scan.DefaultClassScanner;

public class AppInitial {

	private static Set<Class<? extends AppInit>> inits;
	private static final Logger _WFLOG = LoggerFactory.getLogger(AppInitial.class);
	
	public static void initial() {
		inits = getInits();
		for (Class<?> init : inits) {
			try {
				Method initMethod = init.getMethod("init");
				initMethod.invoke(init.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private static Set<Class<? extends AppInit>> getInits() {
		_WFLOG.info("scan initial start...");
		Set<Class<?>> sets = DefaultClassScanner.getInstance().getClassList(Config.getPackageSpace(), ".*\\.inits\\..*Init");
		Set<Class<? extends AppInit>> initSet = new HashSet<Class<? extends AppInit>>();
		for (Class<?> clazz : sets) {
			if (AppInit.class.isAssignableFrom(clazz) 
					&& !Modifier.isInterface(clazz.getModifiers()) 
					&& !Modifier.isAbstract(clazz.getModifiers()) 
					&& Modifier.isPublic(clazz.getModifiers())) {
			  initSet.add((Class<? extends AppInit>) clazz);
			}
		}
		_WFLOG.info("scan initial complete!");
		return initSet;
	}
}
