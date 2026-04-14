package gov.moda.dw.manager.util;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.annotation.RequiredField;
import gov.moda.dw.manager.annotation.ToMapDTO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Utility class for converting DTOs to Maps.
 * <p>
 * This class provides methods to convert a list of DTOs or a single DTO to a list of maps or a map respectively.
 * 提供將DTO轉換為Map的工具類。
 * 此類提供將DTO列表或單一DTO轉換為Map列表或Map的方法。
 * </p>
 */
@Slf4j
@Component
@Primary
public class DynamicDtoUtilsImplWithLambdaFactory implements DynamicDtoUtils, InitializingBean {

    private final GenericApplicationContext applicationContext;
    private static final ConcurrentHashMap<Class<?>, Map<String, Function<Object, Object>>> classCache = new ConcurrentHashMap<>();

    public DynamicDtoUtilsImplWithLambdaFactory(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        for (Class<?> clazz : BeanUtils.getClassesWithAnnotation(applicationContext, ToMapDTO.class)) {
            classCache.put(clazz, this.retrieveGetters(clazz));
            log.info("[debug]this dto is cached :{}", clazz.getName());
        }
    }

    public <T> List<Map<String, Object>> dtoToMapList(List<T> entities) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (T entity : entities) {
            Map<String, Object> stringObjectMap = dtoToMap(entity);
            list.add(stringObjectMap);
        }
        return list;
    }

    public <T> List<Map<String, Object>> dtoToMapList(List<T> entities, List<String> fields) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (T e : entities) {
            Map<String, Object> stringObjectMap = this.dtoToMap(e, fields);
            list.add(stringObjectMap);
        }
        return list;
    }

    public <T> Map<String, Object> dtoToMap(T entity) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Function<Object, Object>> getters = retrieveGettersFromCache(entity.getClass());

        for (Map.Entry<String, Function<Object, Object>> entry : getters.entrySet()) {
            try {
                map.put(entry.getKey(), entry.getValue().apply(entity));
            } catch (Throwable e) {
                log.warn("Error converting DTO to Map: {}", e.getMessage());
            }
        }
        return map;
    }

    public <T> Map<String, Object> dtoToMap(T entity, List<String> fields) {
        Map<String, Object> map = new HashMap<>();
        Class<?> entityClass = entity.getClass();
        Map<String, Function<Object, Object>> getters = retrieveGettersFromCache(entityClass);

        //get required fields
        var requiredFields = new HashSet<String>();

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(RequiredField.class)) {
                requiredFields.add(field.getName());
            }
        }

        requiredFields.addAll(fields);

        for (String field : requiredFields) {
            try {
                Function<Object, Object> getter = getters.get(field);
                if (getter != null) {
                    map.put(field, getter.apply(entity));
                } else {
                    log.warn("No getter found for field: {}", field);
                }
            } catch (Throwable e) {
                log.warn("Error converting DTO to Map with specified fields: {}", e.getMessage());
            }
        }
        return map;
    }

    private Map<String, Function<Object, Object>> retrieveGettersFromCache(Class<?> entityClass) {
        return classCache.computeIfAbsent(entityClass, this::retrieveGetters);
    }

    private Map<String, Function<Object, Object>> retrieveGetters(Class<?> clazz) {
        Map<String, Function<Object, Object>> getters = new HashMap<>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (Field field : clazz.getDeclaredFields()) {
            try {
                String fieldName = field.getName();
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method getterMethod = clazz.getMethod(getterName);
                MethodHandle getterHandle = lookup.findVirtual(clazz, getterName, MethodType.methodType(getterMethod.getReturnType()));

                CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    getterHandle,
                    MethodType.methodType(getterMethod.getReturnType(), clazz)
                );

                Function<Object, Object> getter = (Function<Object, Object>) site.getTarget().invokeExact();
                getters.put(fieldName, getter);
            } catch (Throwable t) {
                log.warn("Error creating getter for field '{}': {}", field.getName(), t.getMessage());
            }
        }
        return getters;
    }
}
