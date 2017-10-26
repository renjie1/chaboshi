package com.rj.wf.mvc.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveConverter {

  private static final Map<Class<?>, Converter<?>> map = new HashMap<Class<?>, Converter<?>>();

  public static final PrimitiveConverter INSTANCE = new PrimitiveConverter();

  private PrimitiveConverter() {
    init();
  }

  void init() {

    Converter<?> c = new StringConverter();
    map.put(String.class, c);

    c = new BooleanConverter();
    map.put(boolean.class, c);
    map.put(Boolean.class, c);

    c = new CharacterConverter();
    map.put(char.class, c);
    map.put(Character.class, c);

    c = new ByteConverter();
    map.put(byte.class, c);
    map.put(Byte.class, c);

    c = new ShortConverter();
    map.put(short.class, c);
    map.put(Short.class, c);

    c = new IntegerConverter();
    map.put(int.class, c);
    map.put(Integer.class, c);

    c = new LongConverter();
    map.put(long.class, c);
    map.put(Long.class, c);

    c = new FloatConverter();
    map.put(float.class, c);
    map.put(Float.class, c);

    c = new DoubleConverter();
    map.put(double.class, c);
    map.put(Double.class, c);

    c = new BigDecimalConverter();
    map.put(BigDecimal.class, c);

    c = new DateConverter();
    map.put(Date.class, c);
  }

  public boolean canConvert(Class<?> clazz) {
    return map.containsKey(clazz);
  }

  public Object convert(Class<?> clazz, String s) {
    Converter<?> c = map.get(clazz);
    return c.convert(s);
  }
}

interface Converter<T> {

  /**
   * Convert a not-null String to specified object.
   */
  T convert(String s);

}

/**
 * Convert String to Boolean.
 * 
 */
class BooleanConverter implements Converter<Boolean> {

  public Boolean convert(String s) {
    return Boolean.parseBoolean(s);
  }
}

/**
 * Convert String to Byte.
 * 
 */
class ByteConverter implements Converter<Byte> {

  public Byte convert(String s) {
    return Byte.parseByte(s);
  }
}

/**
 * Convert String to Character.
 * 
 */
class CharacterConverter implements Converter<Character> {

  public Character convert(String s) {
    if (s.length() == 0)
      throw new IllegalArgumentException("Cannot convert empty string to char.");
    return s.charAt(0);
  }
}

/**
 * Convert String to Double.
 * 
 */
class DoubleConverter implements Converter<Double> {

  public Double convert(String s) {
    return Double.parseDouble(s);
  }
}

/**
 * Convert String to Float.
 * 
 */
class FloatConverter implements Converter<Float> {

  public Float convert(String s) {
    return Float.parseFloat(s);
  }
}

/**
 * Convert String to Integer.
 * 
 */
class IntegerConverter implements Converter<Integer> {

  public Integer convert(String s) {
    return Integer.parseInt(s);
  }
}

/**
 * Convert String to Long.
 * 
 */
class LongConverter implements Converter<Long> {

  public Long convert(String s) {
    return Long.parseLong(s);
  }
}

/**
 * Convert String to Short.
 * 
 */
class ShortConverter implements Converter<Short> {

  public Short convert(String s) {
    return Short.parseShort(s);
  }
}

/**
 * Convert String to String.
 * 
 */
class StringConverter implements Converter<String> {

  public String convert(String s) {
    return s;
  }
}

class BigDecimalConverter implements Converter<BigDecimal> {

  public BigDecimal convert(String s) {
    return new BigDecimal(s);
  }
}

class DateConverter implements Converter<Date> {
  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  public Date convert(String s) {
    try {
      return dateFormat.parse(s);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
}
