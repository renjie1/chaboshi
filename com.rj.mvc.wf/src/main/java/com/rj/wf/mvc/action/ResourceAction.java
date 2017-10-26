package com.rj.wf.mvc.action;

import java.util.Set;

import com.rj.wf.mvc.ActionResult;
import com.rj.wf.mvc.BeatContext;
import com.rj.wf.mvc.Dispatcher.HttpMethod;

/**
 * 对静态文件处理，把所有静态文件名保存在set中，如何精确匹配，表明当前请求就是静态文件
 * 
 */

public class ResourceAction implements Action {

	private final String path;
	private Set<HttpMethod> supportMethods = HttpMethod.suportHttpMethods();

	public ResourceAction(String path) {
		this.path = path;
		initHttpMethods();
	}

	private void initHttpMethods() {
		supportMethods.add(HttpMethod.GET);
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public ActionResult invoke() {
		return new ResourceActionResult();
	}

	@Override
	public boolean matchHttpMethod() {
		String requestMethod = BeatContext.current().getRequest().getMethod();
		String currentMethod = HttpMethod.parse(requestMethod);
		Boolean result = false;
		try {
			HttpMethod httpMethod = HttpMethod.valueOf(currentMethod);
			result = supportMethods.contains(httpMethod);
		} catch (Exception e) {
			result = false;
		}

		return result;
	}

}
