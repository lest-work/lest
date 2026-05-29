package com.lest.common.core.utils;

import com.lest.common.core.text.StrFormatter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 字符串工具类
 *
 * @author yshan2028
 */
public class StringUtils {

    private static final String NULLSTR = "";

    private static final char SEPARATOR = '_';

    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || NULLSTR.equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return NULLSTR;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return NULLSTR;
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    public static boolean isEmpty(Object[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> T nvl(T value, T defaultValue) {
        return isNotNull(value) ? value : defaultValue;
    }

    public static String nvl(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

    public static boolean in(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String format(String template, Object... params) {
        if (isEmpty(template) || params == null || params.length == 0) {
            return template;
        }
        return StrFormatter.format(template, params);
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

    public static String convertToCamelCase(String str) {
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

    public static String toCamelCase(String str) {
        return convertToCamelCase(str);
    }

    public static boolean matches(String str, String pattern) {
        if (isBlank(str) || isBlank(pattern)) {
            return false;
        }
        return str.matches(pattern);
    }

    public static boolean hasText(CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean contains(Collection<String> coll, String str) {
        return coll != null && coll.contains(str);
    }

    public static String getString(byte[] bytes) {
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] getBytes(String str) {
        return str == null ? null : str.getBytes(StandardCharsets.UTF_8);
    }

    public static String htmlEscape(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String randomUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean isAssignable(Class<?> target, Class<?> source) {
        return target != null && source != null && target.isAssignableFrom(source);
    }

    public static boolean isEquals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    public static String defaultString(Object obj) {
        return defaultString(obj, NULLSTR);
    }

    public static String defaultString(Object obj, String defaultStr) {
        return obj == null ? defaultStr : obj.toString();
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
}
