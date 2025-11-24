package gov.moda.dw.verifier.oidvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogInfo {

    @AliasFor("logType")
    LogType value() default LogType.ALL;

    @AliasFor("value")
    LogType logType() default LogType.ALL;

    enum LogType {
        ALL, REQUEST, RESPONSE
    }
}
