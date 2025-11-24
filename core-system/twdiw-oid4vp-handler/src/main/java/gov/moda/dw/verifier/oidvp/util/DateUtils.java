package gov.moda.dw.verifier.oidvp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import gov.moda.dw.verifier.oidvp.type.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

  private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

  /**
   * 日期時區轉換器(GMT 轉…?)
   *
   * @param instant 日期
   * @param zoneId_1 目的時區
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  public static String convertZone(Instant instant, ZoneId zoneId_1, String convertFormat) {
    return convertZone(instant, ZoneType.GMT.getZoneId(), zoneId_1, convertFormat);
  }

  /**
   * 日期時區轉換器
   *
   * @param instant 日期
   * @param zoneId_1 來源時區
   * @param zoneId_2 目的時區
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  public static String convertZone(Instant instant, ZoneId zoneId_1, ZoneId zoneId_2, String convertFormat) {
    ZonedDateTime time1 = ZonedDateTime.ofInstant(instant, zoneId_1);
    ZonedDateTime time2 = time1.withZoneSameInstant(zoneId_2);
    return DateTimeFormatter.ofPattern(convertFormat).format(time2);
  }

  /**
   * 日期格式轉換器
   *
   * @param instant 日期
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  public static String convertDate(Instant instant, String convertFormat) {
    Date date = Date.from(instant);
    return new SimpleDateFormat(convertFormat).format(date);
  }

  /**
   * 日期格式轉換器
   *
   * @param date 日期
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  public static String convertDate(Date date, String convertFormat) {
    return new SimpleDateFormat(convertFormat).format(date);
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

  /**
   * 日期格式轉換器
   *
   * @param date 日期
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  public static Instant convertInstant(Date date, String convertFormat) {
    if (null == date) return null;
    String datetime = convertDate(date, convertFormat);
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern(convertFormat)
      .toFormatter()
      .withZone(ZoneType.TAIPEI.getZoneId());/* <- needed time zone here */
    ZonedDateTime zdtOriginal = ZonedDateTime.parse(datetime, dtf);
    return zdtOriginal.toInstant();
  }

  /**
   * 日期格式轉換器
   *
   * @param date 日期
   * @param convertFormat 轉換格式
   * @param zoneType 轉換時區
   * @return 轉換後字串
   */
  public static Instant convertInstant(Date date, String convertFormat, ZoneType zoneType) {
    if (null == date) return null;
    String datetime = convertDate(date, convertFormat);
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern(convertFormat)
      .toFormatter()
      .withZone(zoneType.getZoneId());/* <- needed time zone here */
    ZonedDateTime zdtOriginal = ZonedDateTime.parse(datetime, dtf);
    return zdtOriginal.toInstant();
  }

  /**
   * 日期格式轉換器
   *
   * @param dateTime 日期
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  @Deprecated
  public static Instant convertInstant(String dateTime, String convertFormat) {
    if (null == dateTime) return null;
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern(convertFormat)
      .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
      .toFormatter()
      .withZone(ZoneId.of("GMT")/* <- needed time zone here */);
    ZonedDateTime zdtOriginal = ZonedDateTime.parse(dateTime, dtf);
    return zdtOriginal.toInstant();
  }

  /**
   * Instant 換時區
   *
   * @param instant
   * @param hours 正負時區 +8、-1、…
   * @return
   */
  public static Instant changeZone(Instant instant, int hours) {
    return instant.plus(hours, ChronoUnit.HOURS);
  }

  /**
   * 日期格式轉換器
   *
   * @param date 日期
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  public static Instant convertDateInstant(String date, String convertFormat, ZoneType zoneType) {
    if (null == date) return null;
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern(convertFormat)
      .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
      .toFormatter()
      .withZone(zoneType.getZoneId());
    ZonedDateTime zdtOriginal = ZonedDateTime.parse(date, dtf);
    return zdtOriginal.toInstant();
  }

  /**
   * 日期時間格式轉換器
   *
   * @param dateTime 日期時間
   * @param convertFormat 轉換格式
   * @return 轉換後字串
   */
  public static Instant convertDateTimeInstant(String dateTime, String convertFormat, ZoneType zoneType) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(convertFormat);
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
    ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneType.getZoneId());
    return Instant.from(zonedDateTime);
  }

  /**
   * 取得當前日期前一月
   *
   * @param convertFormat 轉換格式
   * @return 前月份日期
   */
  public static String getBeforeMonDate(String convertFormat) {
    SimpleDateFormat sdFormat = new SimpleDateFormat(convertFormat);
    Date date = new Date();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, -1);

    return sdFormat.format(calendar.getTime());
  }

  public static boolean isDate(String dateString, String format) {
    boolean retValue = false;
    if (dateString != null && dateString.length() == format.length()) {
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      formatter.setLenient(false);
      try {
        formatter.parse(dateString);
        retValue = true;
      } catch (ParseException ignored) {}
    }
    return retValue;
  }

  /** 檢驗input date是否為uuuuMMdd格式 注意，因保險公司輸入的時間為台灣時間，所以要用Asia/Taipei時區時間 */
  public static boolean verifyByConvertToInstantDate(String date, String entityName) {
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern("uuuuMMdd")
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .toFormatter()
      .withResolverStyle(ResolverStyle.STRICT)
      .withZone(ZoneType.TAIPEI.getZoneId());
    try {
      ZonedDateTime.parse(date, dtf);
      return true;
    } catch (Exception e) {
      //            log.warn("[{}] {}", entityName, "verifyByConvertToInstantDate的input參數date格式有誤");
      log.warn("[{}] {}", entityName, ExceptionUtils.getStackTrace(e));
      return false;
    }
  }

  /**
   * 檢驗input datetime是否為HHMM格式 注意，因保險公司輸入的時間為台灣時間，所以要用Asia/Taipei時區時間
   *
   * @param time: HHMM格式
   */
  public static boolean verifyByConvertToInstantTime(String time, String entityName) {
    String datetime = DateUtils.getDate("yyyyMMdd") + time;
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern("yyyyMMddHHmm")
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .toFormatter()
      .withZone(ZoneType.TAIPEI.getZoneId());
    try {
      ZonedDateTime.parse(datetime, dtf);
      return true;
    } catch (Exception e) {
      //            log.warn("[{}] {}", entityName, "verifyByConvertToInstantTime的input參數time格式有誤");
      log.warn("[{}] {}", entityName, ExceptionUtils.getStackTrace(e));
      return false;
    }
  }

  /**
   * 轉換date string 和time string to Instant datetime
   *
   * @param datetime: yyyyMMddHHmm格式
   * @return
   */
  public static Instant toInstantDateTime(String datetime) {
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern("yyyyMMddHHmm")
      .toFormatter()
      .withZone(ZoneType.TAIPEI.getZoneId());
    ZonedDateTime zdtOriginal = ZonedDateTime.parse(datetime, dtf);
    return zdtOriginal.toInstant();
  }

  /**
   * 轉換date string to Instant date
   *
   * @param date: yyyyMMdd格式
   * @return
   */
  public static Instant toInstantDate(String date) {
    DateTimeFormatter dtf = new DateTimeFormatterBuilder()
      .appendPattern("yyyyMMdd")
      .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
      .toFormatter()
      .withZone(ZoneType.TAIPEI.getZoneId());
    OffsetDateTime offsetDateTime = ZonedDateTime.parse(date, dtf).toOffsetDateTime();
    return offsetDateTime.toInstant();
  }

  /**
   *  將localDate(yyyy-MM-dd)轉為民國年yyy/MM/dd
   * @param yyyyMMdd
   * @return
   */
  public static String toMinguoDate(String yyyyMMdd) {
    if (StringUtils.isEmpty(yyyyMMdd)) {
      return "";
    }
    Chronology chrono = MinguoChronology.INSTANCE;
    int yyyy = Integer.parseInt(yyyyMMdd.split("-")[0]);
    int month = Integer.parseInt(yyyyMMdd.split("-")[1]);
    int day = Integer.parseInt(yyyyMMdd.split("-")[2]);

    LocalDate toParse = LocalDate.of(yyyy, month, day); //格式器
    DateTimeFormatter df = new DateTimeFormatterBuilder()
      .parseLenient()
      .appendPattern("yyy/MM/dd")
      .toFormatter()
      .withChronology(chrono)
      .withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
    return toParse.format(df);
  }
}
