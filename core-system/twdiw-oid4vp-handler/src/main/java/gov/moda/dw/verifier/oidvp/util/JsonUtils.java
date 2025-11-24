package gov.moda.dw.verifier.oidvp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);

    private JsonUtils() {
    }

    public static ArrayNode buildJsonArray(JsonNode... elements) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (JsonNode element : elements) {
            arrayNode.add(element);
        }
        return arrayNode;
    }

    public static ArrayNode buildJsonArray(String... elements) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (String element : elements) {
            arrayNode.add(element);
        }
        return arrayNode;
    }

    /**
     * get JsonNode
     *
     * @param jsonString json string
     * @return JsonNode if is valid json node content.<br> otherwise, {@code NullNode}
     */
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

    public static <T> T jsonNodeToObject(JsonNode jsonNode, Class<T> clazz) {
        if (jsonNode == null) {
            return null;
        }
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (Exception e) {
            LOGGER.warn("convert JsonNode to POJO error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * get json string from input stream
     *
     * @param in input stream
     * @return json string
     * @throws IOException read file fail
     */
    public static String getJsonStringFromFile(InputStream in) throws IOException {
        try {
            JsonNode jsonNode = objectMapper.readValue(in, JsonNode.class);
            return jsonNode.toString();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    LOGGER.warn("close file error: {}", ex.getMessage());
                }
            }
        }
    }

    /**
     * get json string
     *
     * @param object object to get json string
     * @return json string of {@code object}. <br> {@code null}, if failed
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("parse error: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * get object from json string
     *
     * @param jsonString jsonString
     * @param inputClass inputClass
     * @return object of {@code inputClass}, if success.<br> {@code null}, if failed
     */
    public static <T> T convertJsonStringToObject(String jsonString, Class<T> inputClass) {
        try {
            return objectMapper.readValue(jsonString, inputClass);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("convert json string to object error: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * convert json string to map
     *
     * @param jsonString json string
     * @return map for input json string, if success.<br> {@code null}, if failed
     */
    public static Map<String, Object> convertJsonStringToMap(String jsonString) {
        return convertJsonStringToRef(jsonString, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * convert json string to map
     *
     * @param jsonString    json string
     * @param typeReference typeReference
     * @return type for input json string, if success.<br> {@code null}, if failed
     */
    public static <T> T convertJsonStringToRef(String jsonString, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("convert json string to {} error: {}", typeReference.getType().getTypeName(), ex.getMessage());
            return null;
        }
    }

    /**
     * convert json array string to List of Object
     *
     * @param jsonArrayString json array string
     * @param classOfElement  class of Object
     * @param <T>             Object
     * @return List of {@code classOfElement}, if success.<br> {@code null}, if failed
     */
    public static <T> List<T> convertJsonArrayStringToObjectList(String jsonArrayString, Class<T> classOfElement) {
        CollectionType listType =
            objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classOfElement);
        try {
            return objectMapper.readValue(jsonArrayString, listType);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("convert json array string to list error: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * convert Object to map
     *
     * @param object object
     * @return map for input {@code object}, if success.<br> {@code null}, if failed
     */
    public static Map<String, Object> convertObjectToMap(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("convert object to map error: {}", ex.getMessage());
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
}
