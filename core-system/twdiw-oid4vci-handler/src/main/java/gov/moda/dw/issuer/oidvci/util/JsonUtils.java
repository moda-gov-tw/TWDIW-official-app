package gov.moda.dw.issuer.oidvci.util;

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

  private JsonUtils() {}

  public static boolean isValidJson(final String jsonString) {
    try {
      objectMapper.readTree(jsonString);
    } catch (final JsonProcessingException e) {
      LOGGER.error("not valid json");
      return false;
    }
    return true;
  }

  public static boolean isJsonNode(final String jsonString) {
    try {
      objectMapper.readTree(jsonString);
      return true;
    } catch (final JsonProcessingException e) {
      LOGGER.error("not valid json");
      return false;
    }
  }

  public static boolean isJsonArray(final String jsonString) {
    try {
      final JsonNode jsonNode = objectMapper.readTree(jsonString);
      return jsonNode.isArray();
    } catch (final JsonProcessingException e) {
      LOGGER.error("not valid json");
      return false;
    }
  }

  public static ArrayNode buildJsonArray(final JsonNode... elements) {
    final ArrayNode arrayNode = objectMapper.createArrayNode();
    for (final JsonNode element : elements) {
      arrayNode.add(element);
    }
    return arrayNode;
  }

  public static ArrayNode buildJsonArray(final String... elements) {
    final ArrayNode arrayNode = objectMapper.createArrayNode();
    for (final String element : elements) {
      arrayNode.add(element);
    }
    return arrayNode;
  }

  /**
   * get JsonNode
   *
   * @param jsonString json string
   * @return JsonNode if is valid json node content.<br>
   *     otherwise, {@code null}
   */
  public static JsonNode getJsonNode(final String jsonString) {
    try {
      return objectMapper.readTree(jsonString);
    } catch (final JsonProcessingException e) {
      LOGGER.error("not valid json node");
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
  public static String getJsonStringFromFile(final InputStream in) throws IOException {
    try {
      final JsonNode jsonNode = objectMapper.readValue(in, JsonNode.class);
      return jsonNode.toString();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (final IOException ex) {
          LOGGER.error("close file error: {}", ex.getMessage());
        }
      }
    }
  }

  /**
   * get json string
   *
   * @param object object to get json string
   * @return json string of {@code object}. <br>
   *     {@code null}, if failed
   */
  public static String toJSONString(final Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (final JsonProcessingException ex) {
      LOGGER.error("parse error: {}", ex.getMessage());
      return null;
    }
  }

  /**
   * get object from json string
   *
   * @param jsonString jsonString
   * @param inputClass inputClass
   * @return object of {@code inputClass}, if success.<br>
   *     {@code null}, if failed
   */
  public static <T> T convertjsonStringtoObject(
      final String jsonString, final Class<T> inputClass) {
    try {
      return objectMapper.readValue(jsonString, inputClass);
    } catch (final JsonProcessingException ex) {
      LOGGER.error("convert json string to object error: {}", ex.getMessage());
      return null;
    }
  }

  /**
   * convert json string to map
   *
   * @param jsonString json string
   * @return map for input json string, if success.<br>
   *     {@code null}, if failed
   */
  public static Map<String, Object> convertJsonStringToMap(final String jsonString) {
    try {
      return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
    } catch (final JsonProcessingException ex) {
      LOGGER.error("convert json string to map error: {}", ex.getMessage());
      return null;
    }
  }

  /**
   * convert json array string to List of Object
   *
   * @param jsonArrayString json array string
   * @param classOfElement class of Object
   * @return List of {@code classOfElement}, if success.<br>
   *     {@code null}, if failed
   * @param <T> Object
   */
  public static <T> List<T> convertJsonArrayStringToObjectList(
      final String jsonArrayString, final Class<T> classOfElement) {
    final CollectionType listType =
        objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classOfElement);
    try {
      return objectMapper.readValue(jsonArrayString, listType);
    } catch (final JsonProcessingException ex) {
      LOGGER.error("convert json array string to list error: {}", ex.getMessage());
      return null;
    }
  }

  /**
   * convert Object to map
   *
   * @param object object
   * @return map for input {@code object}, if success.<br>
   *     {@code null}, if failed
   */
  public static Map<String, Object> convertObjectToMap(final Object object) {
    final String jsonString = toJSONString(object);
    return convertJsonStringToMap(jsonString);
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
