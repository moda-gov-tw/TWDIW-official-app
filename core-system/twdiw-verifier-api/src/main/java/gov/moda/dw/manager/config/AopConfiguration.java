package gov.moda.dw.manager.config;

import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.aop.RestResourceAop;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
public class AopConfiguration {

    @Bean
    @ConditionalOnMissingBean(RestResourceAop.class)
    public RestResourceAop restResourceAop() {
        return new RestResourceAop();
    }
}
