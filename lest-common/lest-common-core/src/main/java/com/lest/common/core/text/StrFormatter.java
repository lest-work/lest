package com.lest.common.core.text;

import com.lest.common.core.utils.Convert;
import com.lest.common.core.utils.StringUtils;

/**
 * 字符串格式化工具
 *
 * @author yshan2028
 */
public class StrFormatter {

    public static final String EMPTY_JSON = "{}";
    public static final char C_BACKSLASH = '\\';
    public static final char C_DELIM_START = '{';
    public static final char C_DELIM_END = '}';

    /**
     * 格式化字符串
     * 此方法只是简单将占位符 {} 按照顺序替换为参数
     * 如果想输出 {} 使用 \转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 格式化后的字符串
     */
    public static String format(final String strPattern, final Object... argArray) {
        if (StringUtils.isEmpty(strPattern) || StringUtils.isEmpty(argArray)) {
            return strPattern;
        }

        final int strPatternLength = strPattern.length();
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
        int handledPosition = 0;
        int delimIndex;

        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
            if (delimIndex == -1) {
                if (handledPosition == 0) {
                    return strPattern;
                } else {
                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }
            }

            if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {
                if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(Convert.utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                } else {
                    argIndex--;
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(C_DELIM_START);
                    handledPosition = delimIndex + 1;
                }
            } else {
                sbuf.append(strPattern, handledPosition, delimIndex);
                sbuf.append(Convert.utf8Str(argArray[argIndex]));
                handledPosition = delimIndex + 2;
            }
        }

        sbuf.append(strPattern, handledPosition, strPattern.length());
        return sbuf.toString();
    }
}
