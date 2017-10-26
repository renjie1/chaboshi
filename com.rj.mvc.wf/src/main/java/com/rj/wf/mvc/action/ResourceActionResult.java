package com.rj.wf.mvc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rj.wf.mvc.ActionResult;
import com.rj.wf.mvc.BeatContext;
import com.rj.wf.mvc.util.PathUtils;

/**
 *
 * 处理静态文件
 */
public class ResourceActionResult implements ActionResult {

	@Override
	public void render() {
		BeatContext beat = BeatContext.current();
		HttpServletRequest request = beat.getRequest();
		HttpServletResponse response = beat.getResponse();
		try {
			String uri = beat.getRequest().getRequestURI();
			String contextPath = beat.getRequest().getContextPath();
			String relativeUrl = uri.substring(contextPath.length());
			String simplyPath = PathUtils.simplyWithoutSuffix(relativeUrl);
			String bagPath = PathUtils.simplyWithoutSuffix(simplyPath);

			request.getRequestDispatcher("/resources" + bagPath).forward(request, response);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
