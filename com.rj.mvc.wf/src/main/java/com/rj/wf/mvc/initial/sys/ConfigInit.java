package com.rj.wf.mvc.initial.sys;

import static com.rj.wf.mvc.Constant.NAME_PACKAGESPACE;
import static com.rj.wf.mvc.Constant.PATH_SPACE_PROPERTIES;

import java.io.File;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import com.rj.wf.mvc.Constant;
import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;

public class ConfigInit {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigInit.class);

	private static String DISK;

	private static String MODE;

	private static String CLUSTER;

	private static String PACKAGESPACE;

	private static String LOGSPACEURL;

	public static void init() throws Exception {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = cl.getResourceAsStream(PATH_SPACE_PROPERTIES);
			PropertyResourceBundle pp = new PropertyResourceBundle(inputStream);
			PACKAGESPACE = pp.containsKey(NAME_PACKAGESPACE) ? pp.getString(NAME_PACKAGESPACE) : "";
			if (PACKAGESPACE == null || PACKAGESPACE.trim().isEmpty()) {
				throw new Exception("Do not specify a value for the packagespace");
			}

			LOGSPACEURL = pp.containsKey(Constant.NAME_LOGSPACE) ? pp.getString(Constant.NAME_LOGSPACE) : "";
			File file = new File(System.getProperty(Constant.PATH_USER_DIR));
			String path = file.getAbsolutePath().replace('\\', '/');
			DISK = path.substring(0, path.indexOf('/'));
			CLUSTER = pp.containsKey(Constant.NAME_CLUSTERNAME) ? pp.getString(Constant.NAME_CLUSTERNAME) : "";
			LOGGER.info("ClusterName: " + CLUSTER);
			MODE = (CLUSTER == null || CLUSTER.trim().isEmpty()) ? Constant.NAME_OFFLINE : Constant.NAME_ONLINE;
			printConfiguration();
			LOGGER.info("WF init ConfigInfo Complete!");
		} catch (Exception e) {
			LOGGER.error("META-INF in the classpath folder to ensure that there is 'space.properties' configuration file, "
					+ "and specifies the value namespace or vm parameters contain WF.clustername");
			throw new Exception("Config init failed!", e);
		}
	}

	private static void printConfiguration() {
		LOGGER.info("WF MODE: " + MODE);
		LOGGER.info("WF CLUSTER:" + CLUSTER);
		LOGGER.info("WF CONFIG_FOLDER:" + DISK);
		LOGGER.info("WF PACKAGE_SPACE:" + PACKAGESPACE);
		LOGGER.info("WF LOGSPACE_URL:" + LOGSPACEURL);
	}

	
	public static String getDISK() {
		return DISK;
	}


	public static String getMODE() {
		return MODE;
	}


	public static String getCLUSTER() {
		return CLUSTER;
	}


	public static String getPACKAGESPACE() {
		return PACKAGESPACE;
	}


	public static String getLOGSPACEURL() {
		return LOGSPACEURL;
	}

}
