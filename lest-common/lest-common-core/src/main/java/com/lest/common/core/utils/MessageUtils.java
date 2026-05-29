package com.lest.common.core.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 消息工具类（国际化支持）
 *
 * @author yshan2028
 */
public class MessageUtils {

    private static MessageSource messageSource;

    private MessageUtils() {
    }

    public static void setMessageSource(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    /**
     * 获取国际化消息
     *
     * @param code 消息码
     * @return 本地化消息文本
     */
    public static String getMessage(String code) {
        return getMessage(code, null);
    }

    /**
     * 获取国际化消息（带参数）
     *
     * @param code  消息码
     * @param args  参数
     * @return 本地化消息文本
     */
    public static String getMessage(String code, Object[] args) {
        if (messageSource == null) {
            return code;
        }
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code;
        }
    }
}
