package gov.moda.dw.manager.util;

import java.time.Instant;
import gov.moda.dw.manager.type.ZoneType;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;

/**
 * 檢查時間共用
 *
 */
public abstract class CheckTimeUtils {

  /**
   * 檢查時間區間是否可用
   *
   * @param ENTITY_NAME 模塊名稱
   * @param beginDate 起始時間
   * @param endDate 結束時間
   * @throws BadRequestAlertException 檢查時間錯誤資訊
   */
  public CheckTimeUtils(String ENTITY_NAME, Instant beginDate, Instant endDate) throws BadRequestAlertException {
    boolean haveBeginDate = null != beginDate;
    boolean haveEndDate = null != endDate;

    if (haveBeginDate && haveEndDate) {
      if (endDate.isBefore(beginDate)) // 起始時間大於結束時間
      throw new BadRequestAlertException("起始時間大於結束時間", ENTITY_NAME, "timeError");

      allHave(beginDate, endDate);
    } else if (!haveBeginDate && haveEndDate) {
      // 沒帶起始日期
      justEnd(endDate);
    } else if (haveBeginDate) {
      // 沒帶結束日期
      justBegin(beginDate);
    }
  }

  /**
   * 檢查時間區間是否可用
   *
   * @param ENTITY_NAME 模塊名稱
   * @param beginDateString 起始時間
   * @param endDateString 結束時間
   * @param dateFormat 日期編排格式
   * @throws BadRequestAlertException 檢查時間錯誤資訊
   */
  public CheckTimeUtils(String ENTITY_NAME, String beginDateString, String endDateString, String dateFormat)
    throws BadRequestAlertException {
    boolean haveBeginDate = null != beginDateString && !beginDateString.isEmpty();
    boolean haveEndDate = null != endDateString && !endDateString.isEmpty();

    if (haveBeginDate && haveEndDate) {
      Instant beginDate = DateUtils.convertDateInstant(beginDateString, dateFormat, ZoneType.GMT);
      Instant endDate = DateUtils.convertDateInstant(endDateString, dateFormat, ZoneType.GMT);
      if (endDate.isBefore(beginDate)) // 起始時間大於結束時間
      throw new BadRequestAlertException("起始時間大於結束時間", ENTITY_NAME, "timeError");

      allHave(beginDate, endDate);
    } else if (!haveBeginDate && haveEndDate) {
      Instant endDate = DateUtils.convertDateInstant(endDateString, dateFormat, ZoneType.GMT);
      // 沒帶起始日期
      justEnd(endDate);
    } else if (haveBeginDate) {
      Instant beginDate = DateUtils.convertDateInstant(beginDateString, dateFormat, ZoneType.GMT);
      // 沒帶結束日期
      justBegin(beginDate);
    }
  }

  /**
   * 檢查時間區間是否可用
   *
   * @param beginDate 起始時間
   * @param endDate 結束時間
   * @throws BadRequestAlertException 檢查時間錯誤資訊
   */
  public CheckTimeUtils(Instant beginDate, Instant endDate) throws Exception {
    boolean haveBeginDate = null != beginDate;
    boolean haveEndDate = null != endDate;

    if (haveBeginDate && haveEndDate) {
      if (endDate.isBefore(beginDate)) // 起始時間大於結束時間
      throw new Exception("起始時間大於結束時間");

      allHave(beginDate, endDate);
    } else if (!haveBeginDate && haveEndDate) {
      // 沒帶起始日期
      justEnd(endDate);
    } else if (haveBeginDate) {
      // 沒帶結束日期
      justBegin(beginDate);
    }
  }

  /**
   * 時間區間檢查為可用
   *
   * @param beginDate 起始時間
   * @param endDate 結束時間
   */
  public abstract void allHave(Instant beginDate, Instant endDate);

  /**
   * 只有起始時間
   *
   * @param beginDate 起始時間
   */
  public abstract void justBegin(Instant beginDate);

  /**
   * 只有結束時間
   *
   * @param endDate 結束時間
   */
  public abstract void justEnd(Instant endDate);
}
