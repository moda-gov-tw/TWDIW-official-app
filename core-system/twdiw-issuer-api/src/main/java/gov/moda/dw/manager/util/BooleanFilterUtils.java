package gov.moda.dw.manager.util;

import tech.jhipster.service.filter.BooleanFilter;

public class BooleanFilterUtils {

  public static BooleanFilter toEqualBooleanFilter(Boolean flag) {
    BooleanFilter bf = new BooleanFilter();
    bf.setEquals(flag);
    return bf;
  }

  public static BooleanFilter toNotEqualBooleanFilter(Boolean flag) {
    BooleanFilter bf = new BooleanFilter();
    bf.setNotEquals(flag);
    return bf;
  }
}
