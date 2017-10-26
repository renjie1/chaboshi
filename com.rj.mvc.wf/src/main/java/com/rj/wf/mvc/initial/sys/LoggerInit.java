package com.rj.wf.mvc.initial.sys;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;

public class LoggerInit {

	private static final Logger logger = LoggerFactory.getLogger(LoggerInit.class);
	private static final String configFileName = "wflogger.xml";
	private static final AtomicBoolean init = new AtomicBoolean(false);

	static {
		try {
			init(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init(ServletContext sc) throws Exception {
		if (init.compareAndSet(false, true)) {
			Log4jInit.init(sc);
		}
	}

	
	public static class Log4jInit {
		public static void init(ServletContext sc) throws Exception {
			URL configs = getLog4jConfigurationLocation();
			logger.info("获取的wflogger的路劲：" + configs.getPath());
			if (null != configs) {
				String configPath = configs.getPath();
				if (null != configPath && !configPath.isEmpty()) {
					BasicConfigurator.resetConfiguration();
					DOMConfigurator.configure(configPath);
					DOMConfigurator.configureAndWatch(configPath);
					logger.info("WF init Log4j success, the config file : " + configPath);
					return;
				}
			}
			logger.warn("WF init Log4j fail, the config file:{} is not found.", configFileName);
		}

		
		private static URL getLog4jConfigurationLocation() throws MalformedURLException {
			URL configs;
			String path = Thread.currentThread().getContextClassLoader().getResource("/") + "/" + configFileName;
			if (new File(path).exists()) {
				configs = new URL("file", "", path);
			} else {
				configs = Loader.getResource(configFileName);
			}
			return configs;
		}
	}

}
