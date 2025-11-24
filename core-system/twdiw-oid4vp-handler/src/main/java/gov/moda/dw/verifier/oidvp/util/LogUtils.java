package gov.moda.dw.verifier.oidvp.util;

import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogUtils {

    public static final int LOG_IMP;
    public static final int LOG4J2 = 1;
    public static final int LOGBACK = 2;

    // key name
    public static final String TRACE_ID_NAME = "traceID";
    public static final String API_NAME = "apiName";

    static {
        LOG_IMP = LOGBACK;
    }

    public static void addRequestID(String requestID) {
        switch (LOG_IMP) {
            case LOG4J2 -> ThreadContext.put(TRACE_ID_NAME, requestID);
            case LOGBACK -> MDC.put(TRACE_ID_NAME, requestID);
        }
    }

    public static void addAPIName(String apiName) {

        switch (LOG_IMP) {
            case LOG4J2 -> ThreadContext.put(API_NAME, apiName);
            case LOGBACK -> MDC.put(API_NAME, apiName);
        }
    }

    public static void clearAll() {
        switch (LOG_IMP) {
            case LOG4J2 -> ThreadContext.clearAll();
            case LOGBACK -> MDC.clear();
        }
    }

    public static void logRequest(String methodName, Object param, Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Enter {}(), parameters = {}", methodName, param.toString());
    }

    public static void logRequest(String methodName, Map<String, String> params, Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Enter {}(), parameters = {}", methodName, params);
    }

    public static void logRequest(String transactionId, String methodName, Object param, Class<?> clazz) {
        addRequestID(transactionId);
        logRequest(methodName, param, clazz);
    }

    public static void logRequest(String transactionId, String methodName, Map<String, String> params, Class<?> clazz) {
        addRequestID(transactionId);
        logRequest(methodName, params, clazz);
    }
}
