package gov.moda.dw.manager.monitor;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomHealthExclusion {

    @Bean
    public static BeanFactoryPostProcessor removeCommonHealthExtension() {
        return (beanFactory) -> {
            if (beanFactory instanceof DefaultListableBeanFactory defaultFactory) {
                for (String beanName : defaultFactory.getBeanNamesForType(
                    gov.moda.dw.monitor.health.CustomHealthWebExtension.class)) {
                    defaultFactory.removeBeanDefinition(beanName);
                }
            }
        };
    }
}
