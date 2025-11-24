package gov.moda.dw.issuer.vc.util;

public class ArrayUtils {

  public static boolean contains(final Object[] array, final Object objectToFind) {
    if (array == null || objectToFind == null) {
      return false;
    }
    for (final Object object : array) {
      if (objectToFind.equals(object)) return true;
    }
    return false;
  }
}
