package com.rj.wf.mvc.initial.sys;

import javax.servlet.ServletContext;

import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;

public class SysInitial {

	private static final Logger logger = LoggerFactory.getLogger(SysInitial.class);

	public static void init(ServletContext sc) {
		try {
			LoggerInit.init(sc);
			ConfigInit.init();
			ActionInit.init(sc);
			InterceptorInit.init();
			VelocityInit.init(sc);
			XssInit.init();
		} catch (Throwable throwable) {
			logger.error("WF SysInitial failed!!!", throwable);
		}
	}

}
