package gov.moda.dw.verifier.oidvp.aop.log;

import gov.moda.dw.verifier.oidvp.annotation.LogAPI;
import gov.moda.dw.verifier.oidvp.annotation.LogInfo;
import gov.moda.dw.verifier.oidvp.annotation.LogInfo.LogType;
import gov.moda.dw.verifier.oidvp.annotation.PrivacyInfo;
import gov.moda.dw.verifier.oidvp.util.LogUtils;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestLoggerAspect {

    @Pointcut("@annotation(gov.moda.dw.verifier.oidvp.annotation.LogInfo) || @within(gov.moda.dw.verifier.oidvp.annotation.LogInfo)")
    public void logRequestPointcut() {
    }

    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    @Before(value = "@annotation(logAPI)")
    @Order(1)
    public void addAPINameToLog(LogAPI logAPI) {
        LogUtils.addAPIName(logAPI.value());
    }

    @AfterThrowing(value = "logRequestPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        LogInfo logInfo = getLogRequestAnnotation(joinPoint);
        if (logInfo != null) {
            if (logInfo.logType() != LogType.RESPONSE) {
                Logger log = logger(joinPoint);
                log.info("Enter: {}() with arguments = {}", joinPoint.getSignature().getName(), getParameters(joinPoint));
            }
        }
    }

    @AfterReturning(value = "logRequestPointcut()", returning = "response")
    public void logRequest(JoinPoint joinPoint, Object response) {
        LogInfo logInfo = getLogRequestAnnotation(joinPoint);
        if (logInfo != null) {
            Logger log = logger(joinPoint);
            LogType logType = logInfo.logType();
            if (logType != LogType.RESPONSE) {
                log.info("Enter: {}() with arguments = {}", joinPoint.getSignature().getName(), getParameters(joinPoint));
            }

            if (logType != LogType.REQUEST) {
                Object responseValue;
                if (response instanceof ResponseEntity<?> _response) {
                    responseValue = _response.getBody();
                } else {
                    responseValue = response;
                }
                log.info("Return: response = {}", responseValue);
            }
        }
    }

    private LogInfo getLogRequestAnnotation(JoinPoint joinPoint) {
        LogInfo logInfo = AnnotationUtils.findAnnotation(joinPoint.getSignature().getDeclaringType(), LogInfo.class);
        if (logInfo == null) {
            return AnnotationUtils.findAnnotation(((MethodSignature) joinPoint.getSignature()).getMethod(), LogInfo.class);
        } else {
            return logInfo;
        }
    }

    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Annotation[][] annotations = signature.getMethod().getParameterAnnotations();
        Map<String, Object> parameters = new HashMap<>();

        // data masking
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] annotation = annotations[i];
            String parameterName = parameterNames[i];
            Object parameter = args[i];
            for (Annotation anno : annotation) {
                if (anno instanceof PrivacyInfo) {
                    if (parameter instanceof String value) {
                        parameters.put(parameterName, getMaskedString(value));
                    } else if (parameter instanceof Map<?, ?> map) {
                        List<String> nameList = Arrays.stream(((PrivacyInfo) anno).keyName()).toList();
                        map.forEach((k, v) -> {
                            if (v instanceof List<?> list) {
                                if (list.size() == 1) {
                                    parameters.put((String) k, getDisplayString((String) k, list.get(0), nameList));
                                } else {
                                    ArrayList<String> valueList = new ArrayList<>();
                                    list.forEach(lv -> valueList.add(getDisplayString((String) k, lv, nameList)));
                                    parameters.put((String) k, valueList);
                                }
                            } else {
                                parameters.put((String) k, getDisplayString((String) k, v, nameList));
                            }
                        });
                    } else {
                        parameters.put(parameterName, "*****");
                    }
                    break;
                } else {
                    parameters.put(parameterName, parameter);
                }
            }
        }

        return parameters;
    }

    private String getMaskedString(String value) {
        if (value == null) {
            return null;
        }

        int length = value.length();
        String masked;
        if (length > 12) {
            masked = value.substring(0, 3).concat("*****").concat(value.substring(length - 4));
        } else if (length < 3) {
            masked = value.substring(0, 1).concat("*****");
        } else {
            masked = value.substring(0, 2).concat("*****");
        }
        return masked;
    }

    private String getDisplayString(String key, Object value, List<String> filterKeys) {
        boolean mask = !filterKeys.isEmpty();
        String _value;
        if (mask && filterKeys.contains(key)) {
            _value = value != null ? getMaskedString(value.toString()) : null;
        } else {
            _value = value.toString();
        }
        return _value;
    }
}
