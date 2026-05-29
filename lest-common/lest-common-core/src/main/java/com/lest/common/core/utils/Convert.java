package com.lest.common.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 类型转换工具类
 *
 * @author yshan2028
 */
public class Convert {

    private static final char SEPARATOR = '_';

    public static String toStr(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    public static String utf8Str(Object value) {
        return toStr(value, "");
    }

    public static String toStr(Object value, String defaultValue) {
        return value == null ? defaultValue : String.valueOf(value);
    }

    public static Integer toInt(Object value) {
        return toInt(value, null);
    }

    public static Integer toInt(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Long toLong(Object value) {
        return toLong(value, null);
    }

    public static Long toLong(Object value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Double toDouble(Object value) {
        return toDouble(value, null);
    }

    public static Double toDouble(Object value, Double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Float toFloat(Object value) {
        return toFloat(value, null);
    }

    public static Float toFloat(Object value, Float defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        try {
            return Float.parseFloat(value.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Boolean toBool(Object value) {
        return toBool(value, null);
    }

    public static Boolean toBool(Object value, Boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String str = value.toString().trim().toLowerCase();
        if ("true".equals(str) || "1".equals(str) || "yes".equals(str) || "on".equals(str)) {
            return true;
        }
        if ("false".equals(str) || "0".equals(str) || "no".equals(str) || "off".equals(str)) {
            return false;
        }
        return defaultValue;
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        try {
            return new BigDecimal(value.toString().trim()).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toCamelCase(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        String result = toUnderScoreCase(str);
        if (result.indexOf(SEPARATOR) != -1) {
            result = result.replace(String.valueOf(SEPARATOR), "");
        }
        if (result.length() <= 1) {
            return result.toLowerCase();
        }
        return Character.toLowerCase(result.charAt(0)) + result.substring(1);
    }

    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean preCharIsUpperCase = true;
        boolean curCharIsUpperCase = true;
        boolean nextCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }
            curCharIsUpperCase = Character.isUpperCase(c);
            if (i < str.length() - 1) {
                nextCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }
            if (preCharIsUpperCase && curCharIsUpperCase && !nextCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String toPath(String str) {
        if (str == null) {
            return null;
        }
        return str.replace(".", "/");
    }

    public static String stripStr(String str) {
        return str == null ? null : str.trim();
    }

    public static String[] toStrArray(String str, String separator) {
        return str == null ? null : str.split(separator);
    }

    public static String[] toStrArray(String str) {
        return toStrArray(str, ",");
    }

    public static Long[] toLongArray(String str) {
        if (str == null || str.isBlank()) {
            return null;
        }
        String[] arr = str.split(",");
        Long[] result = new Long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = Long.parseLong(arr[i].trim());
        }
        return result;
    }

    public static String formatNumber(double value, int scale) {
        DecimalFormat df = new DecimalFormat("#0." + "0".repeat(Math.max(0, scale)));
        return df.format(value);
    }

    public static Long[] toLongArray(Object... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        Long[] result = new Long[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = toLong(values[i]);
        }
        return result;
    }
}
