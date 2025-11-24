package gov.moda.dw.verifier.oidvp.util;

import java.util.List;
import tech.jhipster.service.filter.LongFilter;

public class LongFilterUtils {

  public static LongFilter toEqualLongFilter(Long ln) {
    LongFilter ls = new LongFilter();
    ls.setEquals(ln);
    return ls;
  }

  public static LongFilter toInLongFilter(List<Long> lnList) {
    LongFilter ls = new LongFilter();
    ls.setIn(lnList);
    return ls;
  }
}
