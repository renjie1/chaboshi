package com.rj.wf.mvc.initial.sys;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.velocity.app.Velocity;

import com.rj.wf.mvc.Config;
import com.rj.wf.mvc.Constant;
import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;

public final class VelocityInit {
  private static final Logger logger = LoggerFactory.getLogger(VelocityInit.class);
  private static Map<String, String> kvs = new HashMap<String, String>();

  public static void init(ServletContext sc) throws Exception {
    initKvs();

    for (Map.Entry<String, String> entry : kvs.entrySet()) {
      Velocity.setProperty(entry.getKey(), entry.getValue());
    }
    String webAppPath = sc.getRealPath("/");
    Velocity.setProperty("file.resource.loader.path", webAppPath);
    try {
      Velocity.init();
      logger.info("WF init Velocity complete!");
    } catch (Exception e) {
      logger.error("Some Velocity properties maybe illegal, please recheck them.");
      throw new Exception("Velocity init failed!", e);
    }
  }

  private static void initKvs() {
    kvs.put("resource.loader", "file");
    kvs.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    kvs.put("input.encoding", Constant.ENCODING);
    kvs.put("output.encoding", Constant.ENCODING);
    kvs.put("default.contentType", "text/html; charset=" + Constant.ENCODING);
    kvs.put("velocimarco.library.autoreload", "true");
    kvs.put("runtime.log.error.stacktrace", "false");
    kvs.put("runtime.log.warn.stacktrace", "false");
    kvs.put("runtime.log.info.stacktrace", "false");
    kvs.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
    kvs.put("runtime.log.logsystem.log4j.category", "velocity_log");

    if (Config.isONLINE()) {
      kvs.put("file.resource.loader.cache", "true");
      kvs.put("file.resource.loader.modificationCheckInterval", "0");
    } else {
      kvs.put("file.resource.loader.cache", "false");
      kvs.put("file.resource.loader.modificationCheckInterval", "2");
    }
  }

  public static void setProperty(String key, String value) {
    kvs.put(key, value);
  }
}
