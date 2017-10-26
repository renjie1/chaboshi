package com.rj.wf.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WFHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public WFHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

}
