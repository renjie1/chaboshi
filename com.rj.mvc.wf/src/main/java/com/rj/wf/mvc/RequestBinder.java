package com.rj.wf.mvc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;
import com.rj.wf.mvc.util.PrimitiveConverter;

/**
 * WF自实现的错误对象实体
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 *
 */
public class RequestBinder {
  private static final Logger logger = LoggerFactory.getLogger(RequestBinder.class);
  private static final PrimitiveConverter converter = PrimitiveConverter.INSTANCE;

  public static Object bindAndValidate(Class<?> clazz) {

    Object target = instantiateClass(clazz);

    BeatContext beat = BeatContext.current();
    Object BindResult = bind(target, beat);

    return BindResult;
  }

  public static Object bind(Object target, BeatContext beat) {

    Field[] fields = target.getClass().getDeclaredFields();

    for (Field field : fields) {
      Class<?> clazzClass = field.getType();
      HttpServletRequest request = beat.getRequest();
      String reqParaValue = request.getParameter(field.getName());
      if (converter.canConvert(clazzClass) && reqParaValue != null && !"".equals(reqParaValue)) {
        try {
          Object object = converter.convert(clazzClass, reqParaValue); // 调用标准的Get Set方法
          Method m = (Method) target.getClass().getMethod("set" + getMethodName(field.getName()), field.getType());
          m.invoke(target, object);
        } catch (Exception e) {
          logger.error("Param Bind Error: [targetName=" + target.getClass().getName() + ", fieldName=" + field.getName() + "]", e);
        }
      }
    }

    return target;
  }

  private static String getMethodName(String fieldName) throws Exception {
    byte[] items = fieldName.getBytes(Constant.ENCODING);
    items[0] = (byte) Character.toUpperCase((char) items[0]);
    return new String(items, Constant.ENCODING);
  }

  public static <T> T instantiateClass(Class<T> clazz) {
    if (clazz.isInterface()) {
      logger.info("Specified class is an interface");
    }
    try {
      return instantiateClass(clazz.getDeclaredConstructor());
    } catch (NoSuchMethodException ex) {
      logger.info("No default constructor found");
    }
    return null;
  }

  public static <T> T instantiateClass(Constructor<T> ctor, Object... args) {
    try {
      return ctor.newInstance(args);
    } catch (Exception ex) {
      logger.info("instantiateClass error");
    }
    return null;
  }
}
