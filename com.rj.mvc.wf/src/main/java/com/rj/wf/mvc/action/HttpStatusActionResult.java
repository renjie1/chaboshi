package com.rj.wf.mvc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rj.wf.mvc.ActionResult;
import com.rj.wf.mvc.BeatContext;
import com.rj.wf.mvc.Constant;

public class HttpStatusActionResult implements ActionResult {

	private int status;
	
	public HttpStatusActionResult(int status){
		this.status = status;
	}
	
	@Override
	public void render() {
		BeatContext beat = BeatContext.current();
		HttpServletRequest request = beat.getRequest();
		HttpServletResponse response = beat.getResponse();
		try {
			response.setStatus(status);
			//request.getRequestDispatcher("/resources/test/" + status + Constant.PAGESUFFIX).forward(request, response);
			request.getRequestDispatcher("/web/login").forward(request, response);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
