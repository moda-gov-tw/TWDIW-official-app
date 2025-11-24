package gov.moda.dw.verifier.vc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    public static final String DATETIME_PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    public enum TimeUnit {
        YEAR, MONTH, DAY, HOUR, MINUTE, SECOND
    }

    /**
     * convert LocalDateTime to String
     *
     * @param ldt input date time
     * @return time string
     */
    public static String toTimeString(LocalDateTime ldt) {
        return toTimeString(ldt, DATETIME_PATTERN_DEFAULT);
    }


    /**
     * convert LocalDateTime to String
     *
     * @param ldt input date time
     * @param pattern date format pattern, default is yyyy-MM-dd HH:mm:ss
     * @return time string
     */
    public static String toTimeString(LocalDateTime ldt, String pattern) {

        try {
            if (pattern != null && !pattern.isEmpty()) {
                return ldt.format(DateTimeFormatter.ofPattern(pattern));
            } else {
                return ldt.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN_DEFAULT));
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    /**
     * convert String to LocalDateTime
     *
     * @param input input time string
     * @return date time
     */
    public static LocalDateTime toLocalDateTime(String input) {
        return toLocalDateTime(input, DATETIME_PATTERN_DEFAULT);
    }

    /**
     * convert String to LocalDateTime
     *
     * @param input input time string
     * @param pattern date format pattern, default is yyyy-MM-dd HH:mm:ss
     * @return date time
     */
    public static LocalDateTime toLocalDateTime(String input, String pattern) {

        try {
            if (pattern != null && !pattern.isEmpty()) {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern(pattern));
            } else {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern(DATETIME_PATTERN_DEFAULT));
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    /**
     * convert LocalDateTime to Date
     *
     * @param ldt local date time
     * @return date time
     */
    public static Date toDate(LocalDateTime ldt) {

        try {
            ZoneId zoneId = ZoneId.systemDefault();
            Instant instant = ldt.atZone(zoneId).toInstant();
            return Date.from(instant);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    /**
     * convert LocalDateTime to Date
     *
     * @param date date time
     * @return local date time
     */
    public static LocalDateTime fromDate(Date date) {

        try {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return LocalDateTime.ofInstant(instant, zoneId);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    /**
     * calculate date time
     *
     * @param ldt input local date time
     * @param timeUnit time unit can be YEAR, MONTH, DAY, HOUR
     * @param num number of unit (can be negative)
     * @return result date time
     */
    public static LocalDateTime calculate(LocalDateTime ldt, TimeUnit timeUnit, int num) {

        switch (timeUnit) {
            case YEAR -> {
                return ldt.plusYears(num);
            }
            case MONTH -> {
                return ldt.plusMonths(num);
            }
            case DAY -> {
                return ldt.plusDays(num);
            }
            case HOUR -> {
                return ldt.plusHours(num);
            }
            case MINUTE -> {
                return ldt.plusMinutes(num);
            }
            case SECOND -> {
                return ldt.plusSeconds(num);
            }
        }

        return null;
    }

    /**
     * 取得當前日期
     *
     * @param convertFormat 轉換格式
     * @return 當前日期
     */
    public static String getDate(String convertFormat) {
        Date date = new Date();
        return new SimpleDateFormat(convertFormat).format(date);
    }

}
