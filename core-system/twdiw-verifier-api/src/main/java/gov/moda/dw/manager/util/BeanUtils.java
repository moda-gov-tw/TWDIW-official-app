package gov.moda.dw.manager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author AlexChang
 * @create 2024/07/22
 * @description
 */
public class BeanUtils {

    public static List<Class<?>> getClassesWithAnnotation(GenericApplicationContext applicationContext, Class<?> annotationClass) {
        ConfigurableListableBeanFactory factory = applicationContext.getBeanFactory();
        List<Class<?>> classList = new ArrayList<>();
        for (String name : factory.getBeanDefinitionNames()) {
            if (isAnnotated(factory, name, annotationClass)) {
                try {
                    classList.add(Class.forName(factory.getBeanDefinition(name).getBeanClassName()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classList;
    }

    private static boolean isAnnotated(ConfigurableListableBeanFactory factory, String beanName, Class<?> annotationClass) {
        BeanDefinition beanDefinition = factory.getBeanDefinition(beanName);
        if (beanDefinition.getSource() instanceof AnnotatedTypeMetadata metadata) {
            return metadata.getAnnotationAttributes(annotationClass.getName()) != null;
        }
        return false;
    }
}
