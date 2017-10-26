package com.rj.wf.mvc.exception;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;

public class WFException extends RuntimeException {
  private static final Logger logger = LoggerFactory.getLogger(WFException.class);
  private static final long serialVersionUID = 1L;

  private static final Map<Class<?>, WFExceptionHandler<Throwable>> HANDLERS = new ConcurrentHashMap<Class<?>, WFExceptionHandler<Throwable>>();

  private static WFExceptionHandler<Throwable> defaultExceptionHanlder = DefaultWFExceptionHandler.INSTANCE;

  public static <T extends Throwable> void setHandler(Class<T> clazz, WFExceptionHandler<Throwable> handler) {
    HANDLERS.put(clazz, handler);
  }

  public static <T extends Throwable> WFExceptionHandler<Throwable> getHandler(Class<T> clazz) {
    WFExceptionHandler<Throwable> handler = HANDLERS.get(clazz);

    if (handler == null)
      return getDefaultExceptionHandler();

    return handler;
  }

  public static WFExceptionHandler<Throwable> getDefaultExceptionHandler() {
    return defaultExceptionHanlder;
  }

  /**
   * 设置默认异常处理器
   * 
   * @param defaultExceptionHanlder 异常处理器
   */
  public static void setDefaultExceptionHanlder(WFExceptionHandler<Throwable> defaultExceptionHanlder) {
    if (defaultExceptionHanlder != null) {
      WFException.defaultExceptionHanlder = defaultExceptionHanlder;
    } else {
      logger.warn("can't set defaultExceptionHandler to null, now it is " + WFException.defaultExceptionHanlder);
    }
  }

  public WFException(Throwable e) {
    super(e);
  }

}
