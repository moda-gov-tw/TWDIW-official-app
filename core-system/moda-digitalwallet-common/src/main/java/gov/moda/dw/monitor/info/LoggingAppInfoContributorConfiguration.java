package gov.moda.dw.monitor.info;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingAppInfoContributorConfiguration {
    @Bean
    public LoggingAppInfoContributor loggingAppInfoContributor(BuildProperties buildProperties) {
        return new LoggingAppInfoContributor(buildProperties);
    }
} 