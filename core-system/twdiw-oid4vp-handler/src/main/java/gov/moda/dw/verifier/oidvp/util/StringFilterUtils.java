package gov.moda.dw.verifier.oidvp.util;

import java.util.List;
import tech.jhipster.service.filter.StringFilter;

public class StringFilterUtils {

    public static StringFilter toEqualStringFilter(String str) {
        StringFilter sf = new StringFilter();
        sf.setEquals(str);
        return sf;
    }

    public static StringFilter toNotEqualStringFilter(String str) {
        StringFilter sf = new StringFilter();
        sf.setNotEquals(str);
        return sf;
    }

    public static StringFilter toContainStringFilter(String str) {
        StringFilter sf = new StringFilter();
        sf.setContains(str);
        return sf;
    }

    public static StringFilter toInStringFilter(List<String> strLst) {
        StringFilter sf = new StringFilter();
        sf.setIn(strLst);
        return sf;
    }

    public static StringFilter toNotInStringFilter(List<String> strLst) {
        StringFilter sf = new StringFilter();
        sf.setNotIn(strLst);
        return sf;
    }

    public static StringFilter toNotContainStringFilter(String str) {
        StringFilter sf = new StringFilter();
        sf.setDoesNotContain(str);
        return sf;
    }
}
