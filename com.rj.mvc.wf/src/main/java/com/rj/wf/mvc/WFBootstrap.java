package com.rj.wf.mvc;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rj.wf.mvc.exception.WFException;
import com.rj.wf.mvc.exception.WFExceptionHandler;
import com.rj.wf.mvc.initial.sys.AppInitial;
import com.rj.wf.mvc.initial.sys.SysInitial;
import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;
import com.rj.wf.mvc.toolbox.monitor.ActionTimeMonitor;
import com.rj.wf.mvc.toolbox.monitor.DefaultActionTimeMonitor;

@WebFilter(urlPatterns ={ "/*" }, asyncSupported = true)
public class WFBootstrap implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(WFBootstrap.class);
	private Dispatcher dispatcher;
	private static final AtomicBoolean hasInit = new AtomicBoolean(false);
	private static final ActionTimeMonitor timeMonitor = new DefaultActionTimeMonitor();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		long start = System.currentTimeMillis();
		if(hasInit.compareAndSet(false, true)) {
			synchronized (WFBootstrap.class) {
				System.out.println("初始化开始");
				SysInitial.init(filterConfig.getServletContext());
				System.out.println("初始化结束");
				AppInitial.initial();
				dispatcher = new Dispatcher();
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;
		if (request.getCharacterEncoding() == null)
			request.setCharacterEncoding(Constant.ENCODING);
		BeatContext beat = BeatContext.register(httpReq, httpResp);
		ActionResult result = null;
		timeMonitor.start();
		System.out.println("进入过滤器-------------------");
		try {
			result = InterceptorHandler.excuteGlobalBeforeInterceptors();
			if (result != null) {
				result.render();
				return;
			}
			result = dispatcher.service(beat);
			ActionResult afterResult = InterceptorHandler.excuteGlobalAfterInterceptors();
			if (afterResult != null) {
				result = afterResult;
			}
		} catch (Throwable e) {
			// 处理异常问题
			WFExceptionHandler<Throwable> handler = WFException.getHandler(e.getClass());
			result = handler.handleException(e);
		} finally {
			timeMonitor.post();
			if (result != null) {
				result.render();
			}
			try {
				InterceptorHandler.excuteGlobalComplete();
			} catch (Throwable e) {
				WFExceptionHandler<Throwable> handler = WFException.getHandler(e.getClass());
				handler.handleException(e);
			}
			BeatContext.clear();
		}
	}


	@Override
	public void destroy() {
		System.out.println("销毁应用程序");
	}

}
