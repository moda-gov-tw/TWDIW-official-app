package gov.moda.dw.verifier.vc.util;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;

public class LogUtils {
  public static final int LOG_IMP;
  public static final int LOG4J2 = 1;
  public static final int LOGBACK = 2;

  // key name
  public static final String TRACE_ID_NAME = "traceID";
  public static final String VC_PATH = "vcPath";

  static {
    LOG_IMP = LOGBACK;
  }

  public static void addRequestID(final String requestID) {
    switch (LOG_IMP) {
      case LOG4J2 -> ThreadContext.put(TRACE_ID_NAME, requestID);
      case LOGBACK -> MDC.put(TRACE_ID_NAME, requestID);
    }
  }

  public static void addVcPath(final String vcPath) {

    switch (LOG_IMP) {
      case LOG4J2 -> ThreadContext.put(VC_PATH, vcPath);
      case LOGBACK -> MDC.put(VC_PATH, vcPath);
    }
  }

  public static void clearAll() {
    switch (LOG_IMP) {
      case LOG4J2 -> ThreadContext.clearAll();
      case LOGBACK -> MDC.clear();
    }
  }
}
