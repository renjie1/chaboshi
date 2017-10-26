package com.rj.wf.mvc.log;

import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory {

	private static final ConcurrentHashMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();
	
	public static Logger getLogger(final String name) {
		Logger logger = loggerMap.get(name);
		if(logger != null)
			return logger;
		logger = new Logger(org.slf4j.LoggerFactory.getLogger(name));
		loggerMap.put(name, logger);
		
		return logger;
	}
	
	
	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}
	
}
