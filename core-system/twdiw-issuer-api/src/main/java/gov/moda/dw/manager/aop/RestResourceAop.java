package gov.moda.dw.manager.aop;

import gov.moda.dw.manager.security.auth.AmsAuthenticationProvider;
import gov.moda.dw.manager.service.custom.track.CustomApiTrackService;
import gov.moda.dw.manager.service.custom.track.CustomWebTrackService;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.StringTrimUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
public class RestResourceAop {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private CustomApiTrackService customApiTrackService;

    @Autowired
    private CustomWebTrackService customWebTrackService;

    @Autowired
    private AmsAuthenticationProvider amsAuthenticationProvider;

    /**
     * 抓取Api的log。
     *
     * @param joinPoint ProceedingJoinPoint
     * @return Object
     * @throws Throwable Throwable
     */
    @Around("execution(* gov.moda.dw.manager.api.rest..*.*(..))")
    public Object aroundForApi(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("RestResourceAop-aroundForApi，執行around:@Start。");

        // 記錄around起始時間。
        long startTime = System.currentTimeMillis();

        List<String> nullColumns = new ArrayList<String>();

        // 取得正在被調用的方法完整類名。
        String className = joinPoint.getSignature().getDeclaringTypeName();

        // 取得正在被調用的方法簡單類名稱。
        String simpleClassName = joinPoint.getSignature().getDeclaringType().getSimpleName().replace("Resource", "");

        // 取得正在被調用的方法名。
        String methodName = joinPoint.getSignature().getName();

        try {
            // 檢查參數陣列中的每個參數字段，若有字段有notNull註解，且值為null或空，則加入nullColumns中。
            for (Object requestObj : joinPoint.getArgs()) {
                nullColumns.addAll(this.checkNotNull(requestObj));
            }

            if (!nullColumns.isEmpty()) {
                return new ResponseDTO<>(
                    nullColumns.toString() + StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getMsg(),
                    simpleClassName + "01"
                );
            }

            // 繼續執行被攔截的方法，並獲取方法的返回結果。
            Object responseDTO = joinPoint.proceed();

            log.debug("RestResourceAop-aroundForApi，API方法:{}.{}", className, methodName);
            log.debug("RestResourceAop-aroundForApi，API回傳型態:{}", responseDTO.getClass().getName());

            if (responseDTO.getClass().equals(ResponseDTO.class)) {
                ResponseDTO<?> res = (ResponseDTO<?>) responseDTO;
                customApiTrackService.saveApiTrack(joinPoint.getArgs(), res, ResType.API.getCode(), System.currentTimeMillis() - startTime);
                return res;
            } else {
                // 進到這裡代表API的return type不是ResponseDTO。
                log.error(
                    "RestResourceAop-aroundForApi，{}中的{}回傳型態錯誤，請開發人員將return type改為ResponseDTO({})",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    responseDTO.getClass().getName()
                );
                return responseDTO;
            }
        } catch (Exception ex) {
            // note:catch起來之後ExceptionTranslator就不會再處理了。
            log.info("RestResourceAop-aroundForApi，API發生Exception");

            // 找出Exception發生的行數。
            String exceptionLineNumber = "";
            for (StackTraceElement ste : ex.getStackTrace()) {
                if (ste.getClassName().equals(className) && ste.getMethodName().equals(methodName)) {
                    exceptionLineNumber = String.valueOf(ste.getLineNumber());
                    break;
                }
            }

            // 組出error code。
            String code;
            // 如果找不到Exception的行數就視為在AOP中噴錯。
            if ("".equals(exceptionLineNumber)) {
                code = "S-AOP";
            } else {
                code = "S-" + simpleClassName + exceptionLineNumber;
                code = StringTrimUtils.limit20Size(code);
            }
            // 組出Exception message。
            String msg;
            if (ex.getMessage() == null) {
                msg = ExceptionUtils.getRootCauseMessage(ex).replace(": ", " ");
            } else {
                msg = ex.getMessage();
            }

            log.error("RestResourceAop-aroundForApi，發生異常:{}", ExceptionUtils.getStackTrace(ex));
            ResponseDTO<?> responseDTO = new ResponseDTO<>(msg, code);

            customApiTrackService.saveApiTrack(
                joinPoint.getArgs(),
                responseDTO,
                ResType.API.getCode(),
                System.currentTimeMillis() - startTime
            );
            return responseDTO;
        }
    }

    /**
     * 抓取Web的log。
     *
     * @param joinPoint ProceedingJoinPoint
     * @return Object
     * @throws Throwable Throwable
     */
    @Around("execution(* gov.moda.dw.manager.web.rest.custom.*.*(..))")
    public Object aroundForWeb(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("RestResourceAop-aroundForWeb，執行around:@Start。");

        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        // 取得 JWT Token
        String token = request.getHeader(AUTHORIZATION_HEADER);
        String jwt = null;
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        }

        // 檢查 JWT 剩餘時間
        if (jwt != null && amsAuthenticationProvider.isTokenExpiringSoon(jwt, 10 * 60)) {

            Authentication authenticationToken = amsAuthenticationProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String newToken = amsAuthenticationProvider.createToken(authenticationToken, false);
            log.info("JWT Token 即將過期，已產生新的 Token");

            if (response != null) {
                response.setHeader(AUTHORIZATION_HEADER, "Bearer " + newToken);
            }
        }


        // 繼續執行被攔截的方法，並獲取方法的返回結果。
        Object responseDTO = joinPoint.proceed();

        //        if (responseDTO.getClass().equals(ResponseEntity.class)) {
        // 進到這裡代表API的return type是ResponseEntity。
        customWebTrackService.saveWebTrack(joinPoint.getArgs(), responseDTO, ResType.WEB.getCode(), System.currentTimeMillis() - startTime);
        //        } else {
        // 進到這裡代表API的return type不是ResponseEntity。
        //            log.info(
        //                "RestResourceAop-aroundForWeb，{}中的{}回傳型態不是ResponseEntity。",
        //                joinPoint.getSignature().getDeclaringTypeName(),
        //                joinPoint.getSignature().getName()
        //            );
        //        }
        return responseDTO;
    }

    public <T> List<String> checkNotNull(T req) {
        List<String> nullColumns = new ArrayList<>();

        if (req == null) {
            return nullColumns;
        }

        for (Field f : req.getClass().getDeclaredFields()) {
            try {
                ReflectionUtils.makeAccessible(f);
                for (Annotation annotation : f.getDeclaredAnnotations()) {
                    if (annotation.annotationType().getName().toLowerCase().contains("notnull")) {
                        if (f.get(req) == null || f.get(req).toString().isEmpty()) {
                            log.debug("RestResourceAop-checkNotNull，\"{}\"欄位必填。", f.getName());
                            nullColumns.add(f.getName());
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("RestResourceAop-checkNotNull，發生異常:{}", ExceptionUtils.getStackTrace(ex));
            }
        }
        return nullColumns;
    }
}
