package com.rj.wf.mvc.action;

import com.rj.wf.mvc.ActionResult;

public interface Action {

	public String path();

	public ActionResult invoke();

	public boolean matchHttpMethod(); // 验证POST GET
}
