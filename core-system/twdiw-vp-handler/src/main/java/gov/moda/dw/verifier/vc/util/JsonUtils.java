package gov.moda.dw.verifier.vc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON operation
 * <p>
 * supports the conversion of several data type: - JSON String (JS) - Serializable Value Object (VO) - Map
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
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    public static Map<String, Object> objectToMap(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});
        } catch (IllegalArgumentException ex) {
            LOGGER.error("convert object to map error: {}", ex.getMessage());
            return null;
        }
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

    public static JsonNode getJsonNode(String jsonString) {
        if (jsonString == null) {
            return objectMapper.getNodeFactory().nullNode();
        }
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            LOGGER.warn("not valid json node");
            return objectMapper.getNodeFactory().nullNode();
        }
    }

    public static JsonNode getJsonNode(Object object) {
        if (object == null) {
            return objectMapper.getNodeFactory().nullNode();
        }
        try {
            return objectMapper.valueToTree(object);
        } catch (Exception e) {
            LOGGER.warn("convert object to JsonNode error: {}", e.getMessage());
            return objectMapper.getNodeFactory().nullNode();
        }
    }
}
