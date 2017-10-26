package com.rj.wf.mvc.initial.sys;

import com.rj.wf.mvc.Config;
import com.rj.wf.mvc.toolbox.xss.XssConverter;

public final class XssInit {

	public static void init() throws Exception {
		String xssPropertyPath = Config.getConfigFolder() + "/xss.properties";
		XssConverter.initProperty(xssPropertyPath);
	}
}
