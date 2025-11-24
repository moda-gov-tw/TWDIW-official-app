package gov.moda.dw.issuer.vc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON operation
 * <p>
 * supports the conversion of several data type:
 * - JSON String (JS)
 * - Serializable Value Object (VO)
 * - Map
 *
 * @version 20240902
 */
public class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    private final static ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private JsonUtils() {
    }

    public static String voToJs(Object obj) {

        String result = "";

        try {
            result = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }

    public static String mapToJs(Map<String, Object> map) {

        String result = "";

        try {
            result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }

    public static <T> T jsToVo(String json, Class<T> objectClass) {

        try {
            return objectMapper.readValue(json, objectClass);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    public static Map<String, Object> jsToMap(String json) {

        try {
            return objectMapper.readValue(json, LinkedHashMap.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * To json.
     *
     * @param obj the obj
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static String toJsonNoThrows(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
    
    // 將 JSON 字串轉成 List<T>
    public static <T> List<T> jsToList(String json, Class<T> objectClass) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, objectClass));
        } catch (JsonProcessingException e) {
        	LOGGER.error(e.getMessage(), e);
        }
        
        return null;
    }
}
