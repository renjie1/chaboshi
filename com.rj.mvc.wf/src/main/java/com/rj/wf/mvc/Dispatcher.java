package com.rj.wf.mvc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rj.wf.mvc.action.Action;
import com.rj.wf.mvc.action.AntPathMatcher;
import com.rj.wf.mvc.action.HttpStatusActionResult;
import com.rj.wf.mvc.action.MethodAction;
import com.rj.wf.mvc.initial.sys.ActionInit;
import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;
import com.rj.wf.mvc.util.PathUtils;

public class Dispatcher {
	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	public ActionResult service(BeatContext beat) {
		Action action = findAction(beat);
		return (null == action) ? new HttpStatusActionResult(404) : action.invoke();
	}

	// 这里有优化空间
	private Action findAction(BeatContext beat) {
		String uri = beat.getRequest().getRequestURI();
		String contextPath = beat.getRequest().getContextPath();
		String relativeUrl = uri.substring(contextPath.length());
		String simplyPath = PathUtils.simplyWithoutSuffix(relativeUrl);
		String bagPath = PathUtils.simplyWithoutSuffix(simplyPath);
		logger.info("------------------------------------------");
		logger.info("uri:" + uri);
		logger.info("contextPath:" + contextPath);
		logger.info("relativeUrl:" + relativeUrl);
		logger.info("simplyPath:" + simplyPath);
		logger.info("bagPath:" + bagPath);
		logger.info("------------------------------------------");

		Set<Action> exactActionMap = ActionInit.getResourceActions(bagPath);
		if (exactActionMap != null) {
			for (Action action : exactActionMap) {
				logger.info("搜索固定方式的路径：" + action.path());
				if (action.matchHttpMethod()) {
					logger.info("匹配到action:" + action.path());
					return action;
				}
			}
		}

		Map<String, Set<MethodAction>> ministyActions = ActionInit.getPatternActions();
		if (ministyActions != null) {
			for (Map.Entry<String, Set<MethodAction>> entry : ministyActions.entrySet()) {
				logger.info("匹配方式的路径：" + entry.getKey());
				boolean match = pathMatcher.doMatch(entry.getKey(), bagPath, true, null);
				if (match) {
					Set<MethodAction> actions = ministyActions.get(entry.getKey());
					for (MethodAction action : actions) {
						if (action.matchHttpMethod()) {
							return action;
						}
					}
				}
			}
		}

		return null;
	}

	public enum HttpMethod {

		GET,

		POST,

		HEAD;

		public static String parse(String method) {
			if (method == null || method.isEmpty())
				return null;

			return method.toUpperCase();
		}

		public static Set<HttpMethod> suportHttpMethods() {
			Set<HttpMethod> methods = new HashSet<Dispatcher.HttpMethod>();
			methods.add(HEAD);
			return methods;
		}

	}

}
