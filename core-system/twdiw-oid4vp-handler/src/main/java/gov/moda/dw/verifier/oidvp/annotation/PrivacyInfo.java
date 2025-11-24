package gov.moda.dw.verifier.oidvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivacyInfo {

    @AliasFor("keyName")
    String[] value() default "";

    /**
     * for Map
     */
    @AliasFor("value")
    String[] keyName() default "";
}
