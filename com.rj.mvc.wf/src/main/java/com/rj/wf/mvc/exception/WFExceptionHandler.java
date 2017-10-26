package com.rj.wf.mvc.exception;

import com.rj.wf.mvc.ActionResult;

public interface WFExceptionHandler<T extends Throwable> {
	
	public ActionResult handleException(T exception);

}

