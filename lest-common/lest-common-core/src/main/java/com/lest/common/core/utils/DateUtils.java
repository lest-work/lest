package com.lest.common.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author yshan2028
 */
public class DateUtils {

    public static final String YYYY = "yyyy";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"
    };

    private DateUtils() {
    }

    public static Date now() {
        return new Date();
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(String format) {
        return parseDateToStr(format, new Date());
    }

    public static String dateTime(Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String parseDateToStr(String format, Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static Date dateTime(String format, String ts) {
        if (StringUtils.isEmpty(ts)) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDate(String str, String... parsePatterns) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        SimpleDateFormat parser;
        ParseException parseException = null;
        for (String parsePattern : parsePatterns) {
            parser = new SimpleDateFormat(parsePattern);
            parser.setLenient(false);
            try {
                return parser.parse(str);
            } catch (ParseException e) {
                parseException = e;
            }
        }
        throw parseException;
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime dateTime) {
        return format(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    public static LocalDateTime parseLocalDateTime(String dateStr) {
        return parseLocalDateTime(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static long diffInSeconds(LocalDateTime t1, LocalDateTime t2) {
        return Duration.between(t1, t2).getSeconds();
    }

    public static long diffInMinutes(LocalDateTime t1, LocalDateTime t2) {
        return Duration.between(t1, t2).toMinutes();
    }

    public static long diffInHours(LocalDateTime t1, LocalDateTime t2) {
        return Duration.between(t1, t2).toHours();
    }

    public static long diffInDays(LocalDateTime t1, LocalDateTime t2) {
        return Duration.between(t1, t2).toDays();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        LocalDate d1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate d2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return d1.equals(d2);
    }

    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }
}
