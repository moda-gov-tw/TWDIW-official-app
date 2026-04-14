package gov.moda.dw.manager.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.annotation.ToMapDTO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author AlexChang
 * @create 2024/07/19
 * @description
 */
@Slf4j
@Component
public class DynamicDTOUtilsImplWithReflect implements DynamicDtoUtils, InitializingBean {

  private final GenericApplicationContext applicationContext;

  private static final ConcurrentHashMap<Class<?>, Map<String, Function<Object, Object>>> classCache = new ConcurrentHashMap<>();

  public DynamicDTOUtilsImplWithReflect(GenericApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void afterPropertiesSet() {
    for (Class<?> clazz : BeanUtils.getClassesWithAnnotation(applicationContext, ToMapDTO.class)) {
      log.info("[debug]this dto is cached :{}", clazz.getName());
      //TODO: 將 加入 cache 的方法與實際取得cache 抽離
      var ignored = this.retrieveGetters(clazz);
    }
  }

  public <T> List<Map<String, Object>> dtoToMapList(List<T> entities) {
    return entities.stream().map(this::dtoToMap).toList();
  }

  public <T> List<Map<String, Object>> dtoToMapList(List<T> entities, List<String> fields) {
    return entities.stream().map(e -> this.dtoToMap(e, fields)).toList();
  }

  public <T> Map<String, Object> dtoToMap(T entity) {
    Map<String, Object> map = new HashMap<>();
    Map<String, Function<Object, Object>> getters = retrieveGetters(entity.getClass());

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
    Map<String, Function<Object, Object>> getters = retrieveGetters(entity.getClass());

    for (String field : fields) {
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

  private Map<String, Function<Object, Object>> retrieveGetters(Class<?> entityClass) {
    return classCache.computeIfAbsent(entityClass, clazz -> {
      Map<String, Function<Object, Object>> getters = new HashMap<>();
      for (Field field : clazz.getDeclaredFields()) {
        ReflectionUtils.makeAccessible(field);
        getters.put(field.getName(), obj -> {
          try {
            return field.get(obj);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        });
      }
      return getters;
    });
  }
}
