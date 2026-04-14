package gov.moda.dw.manager.util;

import java.util.List;
import java.util.Map;

/**
 * @author AlexChang
 * @create 2024/07/19
 * @description
 */
public interface DynamicDtoUtils {
    <T> Map<String, Object> dtoToMap(T entities);

    <T> Map<String, Object> dtoToMap(T entities, List<String> fields);

    <T> List<Map<String, Object>> dtoToMapList(List<T> entities);

    <T> List<Map<String, Object>> dtoToMapList(List<T> entities, List<String> fields);
}
