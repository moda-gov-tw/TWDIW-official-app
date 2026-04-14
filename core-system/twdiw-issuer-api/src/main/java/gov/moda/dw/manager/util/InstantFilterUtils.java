package gov.moda.dw.manager.util;

import java.time.Instant;
import tech.jhipster.service.filter.InstantFilter;

/**
 * 時間條件篩選
 *
 */
public class InstantFilterUtils {

  public static InstantFilter toInstantFilter(String ENTITY_NAME, Instant beginDate, Instant endDate) {
    InstantFilter dateFilter = new InstantFilter();
    new CheckTimeUtils(ENTITY_NAME, beginDate, endDate) {
      @Override
      public void allHave(Instant beginDate, Instant endDate) {
        dateFilter.setGreaterThanOrEqual(beginDate);
        dateFilter.setLessThanOrEqual(endDate);
      }

      @Override
      public void justBegin(Instant beginDate) {
        dateFilter.setGreaterThanOrEqual(beginDate);
      }

      @Override
      public void justEnd(Instant endDate) {
        dateFilter.setLessThanOrEqual(endDate);
      }
    };
    return dateFilter;
  }

  public static InstantFilter toInstantFilterBetween(String ENTITY_NAME, Instant beginDate, Instant endDate) {
    InstantFilter dateFilter = new InstantFilter();
    new CheckTimeUtils(ENTITY_NAME, beginDate, endDate) {
      @Override
      public void allHave(Instant beginDate, Instant endDate) {
        dateFilter.setGreaterThanOrEqual(beginDate);
        dateFilter.setLessThan(endDate);
      }

      @Override
      public void justBegin(Instant beginDate) {
        dateFilter.setGreaterThanOrEqual(beginDate);
      }

      @Override
      public void justEnd(Instant endDate) {
        dateFilter.setLessThan(endDate);
      }
    };
    return dateFilter;
  }

  public static InstantFilter toIsNotNullInstantFilter(Boolean notNull) {
    InstantFilter insf = new InstantFilter();
    insf.setSpecified(notNull);
    return insf;
  }
}
