package com.rj.wf.mvc.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;

import com.rj.wf.mvc.ActionResult;
import com.rj.wf.mvc.BeatContext;
import com.rj.wf.mvc.Dispatcher.HttpMethod;
import com.rj.wf.mvc.InterceptorHandler;
import com.rj.wf.mvc.RequestBinder;
import com.rj.wf.mvc.WFInterceptor;
import com.rj.wf.mvc.annotation.Interceptor;
import com.rj.wf.mvc.exception.WFException;
import com.rj.wf.mvc.util.AnnotationUtils;
import com.rj.wf.mvc.util.PrimitiveConverter;

public class MethodAction implements Action {

	protected Object controller;

	protected Method method;

	protected String pathPattern;

	/**
	 * 方法上所有参数名，按顺序排列
	 */
	protected List<String> paramNames;

	/**
	 * 方法上所有参数类型，按顺序排列
	 */
	protected List<Class<?>> paramTypes;

	/**
	 * 所有annotation，包括并覆盖controller上的annotation，
	 */
	protected Set<Annotation> annotations;

	/**
	 * 是否是模版匹配
	 */
	protected boolean isPattern;

	private List<WFInterceptor> interceptors = new ArrayList<WFInterceptor>();

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	private static final PrimitiveConverter converter = PrimitiveConverter.INSTANCE;

	private Set<HttpMethod> supportMethods = HttpMethod.suportHttpMethods();

	public MethodAction(Object controller, Method method, String pathPattern, boolean isGet, boolean isPost, List<String> paramNames, List<Class<?>> paramTypes, Set<Annotation> annotations)
			throws Exception {
		this.controller = controller;
		this.method = method;
		this.pathPattern = pathPattern;
		this.paramNames = paramNames;
		this.paramTypes = paramTypes;
		this.annotations = annotations;
		this.isPattern = pathMatcher.isPattern(pathPattern) || paramTypes.size() > 0;
		this.interceptors = generateActionInterceptors();
		initHttpMethods(isGet, isPost);
	}

	private void initHttpMethods(boolean isGet, boolean isPost) {
		if (isGet) {
			supportMethods.add(HttpMethod.GET);
		}
		if (isPost) {
			supportMethods.add(HttpMethod.POST);
		}
	}

	// 查找拦截器注解
	private List<WFInterceptor> generateActionInterceptors() throws Exception {
		for (Annotation annotation : annotations) {
			try {
				parseWFInterceptor(annotation);
			} catch (Exception e) {
				throw new Exception("Generate action interceptor failed.", e);
			}
		}
		// 根据Order值排序拦截器
		Collections.sort(interceptors, WFInterceptor.INTERCEPTOR_SORTER);

		return interceptors;
	}

	public Method getMethod() {
		return method;
	}

	public Object getController() {
		return controller;
	}

	private void parseWFInterceptor(Annotation annotation) throws Exception {

		Interceptor interAnnotation = (Interceptor) ((annotation.annotationType() == Interceptor.class) ? annotation : null);
		if (interAnnotation == null) {
			interAnnotation = AnnotationUtils.findAnnotation(annotation.getClass(), Interceptor.class);
		}
		if (interAnnotation == null) {
			return;
		}

		WFInterceptor mvcInterceptor = interAnnotation.value().newInstance();
		interceptors.add(mvcInterceptor);
	}

	@Override
	public String path() {
		return pathPattern;
	}

	public boolean isPattern() {
		return isPattern;
	}

	public List<WFInterceptor> getInterceptors() {
		return interceptors;
	}

	// 数据绑定
	private Object[] matchValues() {
		if (null == paramTypes || paramTypes.isEmpty()) {
			return null;
		}

		Object[] params = new Object[paramTypes.size()];

		BeatContext beat = BeatContext.current();
		ServletRequest request = beat.getRequest();
		String uri = beat.getRequest().getRequestURI();
		Map<String, String> urlParams = pathMatcher.extractUriTemplateVariables(this.pathPattern, uri);
		for (int i = 0; i < paramNames.size(); i++) {
			Class<?> clazz = paramTypes.get(i);
			// 普通类型直接bind
			if (converter.canConvert(clazz)) {
				String paramName = paramNames.get(i);
				String v = urlParams.get(paramName);
				if(v == null) {
					v = request.getParameter(paramName);
				}
				if (v != null) {
					params[i] = converter.convert(clazz, v);
				}
				continue;
			}

			params[i] = RequestBinder.bindAndValidate(clazz);
		}
		return params;
	}

	@Override
	public ActionResult invoke() {
		Object result = null;
		Object preResult = null;

		preResult = InterceptorHandler.excuteActionBeforeInterceptors(this);
		if (preResult != null) {
			return (ActionResult) preResult;
		}

		try {
			result = method.invoke(controller, matchValues());
		} catch (IllegalArgumentException e) {
			throw new WFException(e);
		} catch (IllegalAccessException e) {
			throw new WFException(e);
		} catch (InvocationTargetException e) {
			throw new WFException(e);
		}

		ActionResult afterResult = InterceptorHandler.excuteActionAfterInterceptors(this);
		if (afterResult != null) {
			result = afterResult;
		}

		InterceptorHandler.excuteActionComplet(this);
		return (ActionResult) result;
	}

	@Override
	public boolean matchHttpMethod() {
		String requestMethod = BeatContext.current().getRequest().getMethod();
		String currentMethod = HttpMethod.parse(requestMethod);
		HttpMethod httpMethod = null;
		System.out.println("currentMethod:" + currentMethod);
		try {
			httpMethod = HttpMethod.valueOf(currentMethod);
			System.out.println("转换之后的请求是：" + httpMethod.name());
		} catch (Exception e) {
			return false;
		}

		for(HttpMethod h: supportMethods) {
			System.out.println("支持的httpMethod:" + h.name());
		}

		return supportMethods.contains(httpMethod);
	}

	@Override
	public String toString() {
		return "MethodAction [controller=" + controller + ", method=" + method
				+ ", pathPattern=" + pathPattern + ", paramNames=" + paramNames
				+ ", paramTypes=" + paramTypes + ", annotations=" + annotations
				+ ", isPattern=" + isPattern + ", interceptors=" + interceptors
				+ ", pathMatcher=" + pathMatcher + ", supportMethods="
				+ supportMethods + "]";
	}

}
