package com.rj.wf.mvc.initial.sys;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import com.rj.wf.mvc.Config;
import com.rj.wf.mvc.action.Action;
import com.rj.wf.mvc.action.AntPathMatcher;
import com.rj.wf.mvc.action.MethodAction;
import com.rj.wf.mvc.action.ResourceAction;
import com.rj.wf.mvc.annotation.GET;
import com.rj.wf.mvc.annotation.POST;
import com.rj.wf.mvc.annotation.Path;
import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;
import com.rj.wf.mvc.scan.DefaultClassScanner;
import com.rj.wf.mvc.scan.DefaultMethodScanner;
import com.rj.wf.mvc.util.AnnotationUtils;
import com.rj.wf.mvc.util.ClassUtils;
import com.rj.wf.mvc.util.Pair;

public final class ActionInit {

	private static final Logger logger = LoggerFactory.getLogger(ActionInit.class);
	// 匹配方式的Action
	private static final Map<String, Set<MethodAction>> patternActions = new HashMap<String, Set<MethodAction>>();
	// 固定路径Action
	static final Map<String, Set<Action>> resourceActions = new HashMap<String, Set<Action>>();

	public static final Map<String, Set<MethodAction>> getPatternActions() {
		return patternActions;
	}

	public static final Set<Action> getResourceActions(final String path) {
		return resourceActions.get(path);
	}

	public static void init(ServletContext sc) throws Exception {
		logger.info("WF RootPath:" + Config.getRootPath());
		logger.info("WF package:" + Config.getPackageSpace());
		List<MethodAction> methodActions = PatternActionInit.getPatternActions();
		for(MethodAction action: methodActions) {
			if(action.isPattern()) {
				if(!patternActions.containsKey(action.path())) {
					patternActions.put(action.path(), new HashSet<MethodAction>());
				}
				patternActions.get(action.path()).add(action);
			} else {
				if (!resourceActions.containsKey(action.path())) {
					resourceActions.put(action.path(), new HashSet<Action>());
				} else {
					logger.info("Exist same PatternAction path : " + action.path() + "\r\n > Controller : "
							+ action.getController().getClass().getName() + "\r\n > Method : " + action.getMethod().getName());
				}
				resourceActions.get(action.path()).add(action);
			}
		}
		Set<String> resource = resourceActions.keySet();
		for(String str: resource) {
			Set<Action> setMA = resourceActions.get(str);
			for(Action m: setMA) {
				System.out.println("固定方式的action path:" + m.path() + "  " + m.path());
			}
		}

		List<Action> resourceActionsList = ResourceActionInit.getResourceActions(sc);
		for (Action action : resourceActionsList) {
			if (resourceActions.containsKey(action.path())) {// 方法注解路径与文件路径冲突
				logger.info("Exist same ResourceAction path :" + action.path());
				continue;
			}
			Set<Action> actionList = new HashSet<Action>();
			actionList.add(action);
			resourceActions.put(action.path(), actionList);
		}
		logger.info("WF init controolers complete!");
	}
	
}


class PatternActionInit {
	private static final Logger logger = LoggerFactory.getLogger(ActionInit.class);
	private static final String CONCTROLLER_PATTERN = ".*\\.controllers\\..*Controller";
	private static final Map<String, Object> controllers = new HashMap<String, Object>();
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
	static List<MethodAction> getPatternActions() throws Exception {
		Set<Class<?>> controllerClasses = DefaultClassScanner.getInstance().getClassList(Config.getPackageSpace(),
				CONCTROLLER_PATTERN);
		logger.info("controllerClasses size:" + controllerClasses.size());
		List<MethodAction> actions = new ArrayList<MethodAction>();
		for(Class<?> cc: controllerClasses) {
			actions.addAll(analyze(cc));// 抽取Controller中的Action
		}
		return actions;
	}

	private static List<MethodAction> analyze(Class<?> clazz) throws Exception {
		Path onControllerPath = AnnotationUtils.findAnnotation(clazz, Path.class); // 获得Controller上的Path注解
		String[] onControllerUrl = getPathurls(onControllerPath);
		List<Method> mList = DefaultMethodScanner.getInstance().getMethodListByAnnotation(clazz, Path.class);// 获得Path注解的方法
		for(Method m: mList) {
			System.out.println("获取的方法是：" + m.getName());
		}
		List<MethodAction> actions = new ArrayList<MethodAction>();
		for (int i = 0; i < onControllerUrl.length; i++) {
			for (Method method : mList) {// 组装成Action
				actions.addAll(getMethodAction(onControllerUrl[i], clazz, method));
			}
		}

		return actions;
	}

	private static List<MethodAction> getMethodAction(String pathUrl, Class<?> clazz, Method method) throws Exception {
		Path pathAnnotation = AnnotationUtils.findAnnotation(method, Path.class);
		String[] onMethodUri = pathAnnotation.value();
		List<MethodAction> actions = new ArrayList<MethodAction>();

		for (String methodUri : onMethodUri) {
			String pathPattern = combinePathPattern(pathUrl, methodUri); // 组装URL,//这里组合会有重复URL
			List<String> paramNames = Arrays.asList(ClassUtils.getMethodParamNames(clazz, method));
			List<Class<?>> paramTypes = Arrays.asList(method.getParameterTypes());
			Set<Annotation> annotations = new HashSet<Annotation>(); // 这里查找用户自定义拦截器注解
			annotations.addAll(Arrays.asList(clazz.getAnnotations()));// Controller类上的注解
			annotations.addAll(Arrays.asList(method.getAnnotations()));// 方法上的注解

			boolean[] httpSupport = pickUpHttpMethod(method);// 设置请求类型
			Object controller = controllers.get(clazz.getPackage() + clazz.getName());
			if (controller == null) {
				controller = clazz.newInstance();
				controllers.put(clazz.getPackage() + clazz.getName(), controller);
			}
			if (logger.isInfoEnabled()) {
				logger.info("methodUri >>> " + pathPattern + " >> " + method.getName() + " get : " + httpSupport[0]
						+ ", post : " + httpSupport[1]);
			}
			actions.add(new MethodAction(controller, method, pathPattern, httpSupport[0], httpSupport[1], paramNames,
					paramTypes, annotations));
		}

		return actions;
	}

	private static String[] getPathurls(Path path) {
		String[] pathUrls = (path == null) ? new String[]
				{ "/" } : path.value();
				for (String pathUrl : pathUrls) {
					pathUrl = prefixPathPattern(pathUrl);
				}

				return pathUrls;
	}

	/**
	 * path ==> /path
	 * 
	 * @param pathUrl
	 * @return
	 */
	private static String prefixPathPattern(String pathUrl) {
		if (!pathUrl.isEmpty() && pathUrl.charAt(0) != '/')
			pathUrl = '/' + pathUrl;

		return pathUrl;
	}


	private static boolean[] pickUpHttpMethod(Method method) {

		GET getAnnotaion = AnnotationUtils.findAnnotation(method, GET.class);
		POST posttAnnotaion = AnnotationUtils.findAnnotation(method, POST.class);

		if (getAnnotaion == null && posttAnnotaion != null)
			return new boolean[]
					{ false, true };

		if (getAnnotaion != null && posttAnnotaion == null)
			return new boolean[]
					{ true, false };

		return new boolean[]
				{ true, true };
	}


	/**
	 * 连接两个URIPath
	 * 
	 * @param typePath
	 * @param methodPath
	 * @return
	 */
	private static String combinePathPattern(String typePath, String methodPath) {
		if (typePath.equals("/")) {
			typePath = "";
		}
		String uri = pathMatcher.combine(suffixPathPattern(typePath), prefixPathPattern(methodPath));
		return uri.equals("/") ? "/" : suffixPathPattern(uri);
	}


	/**
	 * path/ ==> path path///// ==> path
	 * 
	 * @param pathUrl
	 * @return
	 */
	private static String suffixPathPattern(String pathUrl) {
		while (!pathUrl.isEmpty() && pathUrl.endsWith("/")) {
			pathUrl = pathUrl.substring(0, pathUrl.length() - 1);
		}
		return pathUrl;
	}

}





class ResourceActionInit {
	private static final Logger logger = LoggerFactory.getLogger(ActionInit.class);
	/**
	 * 静态文件名set
	 */
	private static List<String> staticFiles = new ArrayList<String>();

	/**
	 * 不允许访问的文件或文件夹
	 */
	private static final Set<String> forbitPath = new HashSet<String>(Arrays.asList(""));

	static List<Action> getResourceActions(ServletContext servletContext) {
		String resourceFolder = servletContext.getRealPath("/") + "/resources";
		final File staticResourcesFolder = new File(resourceFolder);
		try {
			staticFiles = findFiles(staticResourcesFolder, forbitPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Action> staticFileActions = new ArrayList<Action>();
		for (String staticFile : staticFiles) {
			if (logger.isDebugEnabled()) {
				logger.debug(" static : " + staticFile); // 这里初始化静态资源文件
			}
			staticFileActions.add(new ResourceAction(staticFile));
		}

		return staticFileActions;
	}

	static List<String> findFiles(File directory, Set<String> forbitPath) throws Exception {
		List<String> staticFiles = new ArrayList<String>();
		Deque<Pair<File, String>> dirs = new LinkedList<Pair<File,String>>();
		dirs.add(Pair.build(directory, "/"));
		while (dirs.size() > 0) {
			Pair<File, String> pop = dirs.pop();
			File[] files = pop.getKey().listFiles();
			if (files != null) {
				for (File file : files) {
					String name = pop.getValue() + file.getName();
					if (forbitPath.contains(name))
						continue;
					if (file.isFile()) {
						staticFiles.add(name);
					} else {
						dirs.push(Pair.build(file, name + '/'));
					}
				}
			}
		}

		return staticFiles;
	}
}

