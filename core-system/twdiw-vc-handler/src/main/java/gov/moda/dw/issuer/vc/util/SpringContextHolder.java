package gov.moda.dw.issuer.vc.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Context Holder
 * 提供全域存取 Spring ApplicationContext 的方式
 * 
 * @author Louis
 * @version 20240902
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 取得 ApplicationContext
     * 
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根據 bean name 取得 bean
     * 
     * @param name bean name
     * @return bean instance
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 根據 bean type 取得 bean
     * 
     * @param clazz bean type
     * @param <T> bean type
     * @return bean instance
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根據 bean name 和 type 取得 bean
     * 
     * @param name bean name
     * @param clazz bean type
     * @param <T> bean type
     * @return bean instance
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
} 