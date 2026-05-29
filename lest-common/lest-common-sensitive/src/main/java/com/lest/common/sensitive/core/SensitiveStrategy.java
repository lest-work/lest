package com.lest.common.sensitive.core;

/**
 * 数据脱敏策略
 *
 * @author yshan2028
 */
public enum SensitiveStrategy {

    /**
     * 手机号脱敏：138****5678
     */
    PHONE(phone -> {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }, null, null),

    /**
     * 邮箱脱敏：t***@example.com
     */
    EMAIL(email -> {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 1) {
            return email;
        }
        return parts[0].charAt(0) + "***" + "@" + parts[1];
    }, null, null),

    /**
     * 姓名脱敏：张*丰
     */
    NAME(name -> {
        if (name == null || name.length() < 2) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*" + name.charAt(name.length() - 1);
    }, null, null),

    /**
     * 身份证脱敏：110***********1234
     */
    ID_CARD(idCard -> {
        if (idCard == null || idCard.length() < 10) {
            return idCard;
        }
        return idCard.substring(0, 3) + "**********" + idCard.substring(idCard.length() - 4);
    }, null, null),

    /**
     * 银行卡脱敏：6222***********1234
     */
    BANK_CARD(bankCard -> {
        if (bankCard == null || bankCard.length() < 10) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "**********" + bankCard.substring(bankCard.length() - 4);
    }, null, null),

    /**
     * 地址脱敏：广东省深圳市****
     */
    ADDRESS(address -> {
        if (address == null || address.length() < 6) {
            return address;
        }
        int keepLen = Math.min(6, address.length() / 2);
        return address.substring(0, keepLen) + "****";
    }, null, null),

    /**
     * 密码：始终返回 ***
     */
    PASSWORD(password -> "***", null, null),

    /**
     * 自定义正则脱敏
     */
    REGEX((value) -> value, null, null);

    private final SensitiveExecutor executor;
    private final String pattern;
    private final String replacement;

    SensitiveStrategy(SensitiveExecutor executor, String pattern, String replacement) {
        this.executor = executor;
        this.pattern = pattern;
        this.replacement = replacement;
    }

    public String desensitize(String value) {
        return executor.desensitize(value);
    }

    public String desensitize(String value, String regexPattern, String regexReplacement) {
        if (value == null) {
            return null;
        }
        if (regexPattern != null && regexReplacement != null) {
            return value.replaceAll(regexPattern, regexReplacement);
        }
        return executor.desensitize(value);
    }

    @FunctionalInterface
    public interface SensitiveExecutor {
        String desensitize(String value);
    }
}
