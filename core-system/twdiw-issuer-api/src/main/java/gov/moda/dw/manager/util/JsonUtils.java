package gov.moda.dw.manager.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/** The Class JsonUtils. */
@Slf4j
public class JsonUtils {

    /** The object mapper. */
    private static ObjectMapper objectMapper = new ObjectMapper()
        .setSerializationInclusion(Include.NON_NULL)
        .registerModule(new JavaTimeModule());

    /**
     * Gets the object by key.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param jsonString the json string
     * @param key the key
     * @return the object by key
     * @throws JsonProcessingException the json processing exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static <T> T getObjectByKey(Class<T> clazz, String jsonString, String key) throws JsonProcessingException, IOException {
        JsonNode rootNode = objectMapper.readTree(new StringReader(jsonString));
        JsonNode innerNode = rootNode.get(key);
        return objectMapper.treeToValue(innerNode, clazz);
    }

    /**
     * Checks if is arrayof objects by key.
     *
     * @param jsonString the json string
     * @param key the key
     * @return true, if is arrayof objects by key
     * @throws JsonProcessingException the json processing exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean isArrayofObjectsByKey(String jsonString, String key) throws JsonProcessingException, IOException {
        JsonNode rootNode = objectMapper.readTree(new StringReader(jsonString));
        JsonNode innerNode = rootNode.get(key);

        return innerNode.isArray();
    }

    /**
     * Gets the objects by key.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param jsonString the json string
     * @param key the key
     * @return the objects by key
     * @throws JsonProcessingException the json processing exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static <T> List<T> getObjectsByKey(Class<T> clazz, String jsonString, String key) throws JsonProcessingException, IOException {
        JsonNode rootNode = objectMapper.readTree(new StringReader(jsonString));
        JsonNode innerNode = rootNode.get(key);

        List<T> ts = new ArrayList<T>();
        if (innerNode.isArray()) {
            for (final JsonNode objNode : innerNode) {
                ts.add(objectMapper.treeToValue(objNode, clazz));
            }
            return ts;
        } else {
            T t = getObjectByKey(clazz, jsonString, key);
            if (t != null) {
                ts.add(t);
            }
        }
        return ts;
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

    public static String toJsonNoThrow(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ex) {
            //            log.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    public static String toJsonNoThrows(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static <T> List<T> toList(Class<T> clazz, Object obj) {
        return toObjects(clazz, JsonUtils.toJsonNoThrows(obj));
    }

    /**
     * To objects.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param jsonString the json string
     * @return the list
     * @throws JsonProcessingException the json processing exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static <T> List<T> toObjects(Class<T> clazz, String jsonString) {
        List<T> ts = new ArrayList<T>();

        try {
            jsonString = jsonString.replaceAll("&quot;", "\"");
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode rootNode = objectMapper.readTree(new StringReader(jsonString));

            if (rootNode.isArray()) {
                for (final JsonNode objNode : rootNode) {
                    ts.add(objectMapper.treeToValue(objNode, clazz));
                }
                return ts;
            } else {
                T t = toObject(clazz, jsonString);
                if (t != null) {
                    ts.add(t);
                }
            }
        } catch (Exception ex) {
            log.error("JsonStr 轉 List 出現錯誤: {}", ex);
            // throw new RuntimeException("JsonStr 轉 List 出現錯誤: " + ex.getMessage());
        }
        return ts;
    }

    public static <T> T to(Class<T> clazz, Object obj) {
        return toObjectNoThrows(clazz, obj instanceof String ? obj.toString() : JsonUtils.toJsonNoThrows(obj));
    }

    /**
     * To object.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param jsonString the json string
     * @return the t
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static <T> T toObjectNoThrows(Class<T> clazz, String jsonString) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        T res = null;
        try {
            res = objectMapper.readValue(jsonString, clazz);
        } catch (IOException ex) {
            log.error("JsonStr 轉 Object 出現錯誤: {}", ex);
            // throw new RuntimeException("JsonStr 轉 Object 出現錯誤: " + ex.getMessage());
        }
        return res;
    }

    /**
     * To object.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param jsonString the json string
     * @return the t
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static <T> T toObject(Class<T> clazz, String jsonString) throws JsonParseException, JsonMappingException, IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(jsonString, clazz);
    }

    /**
     * 分析 Json 字串 解出自訂物件
     *
     * @param <T>
     * @param t
     * @param jsonString
     * @param depth parsing depth
     * @return 回傳屬性其中有一個 null，則反回 null
     */
    public static <T> T parsing(Class<T> t, String jsonString, int depth) {
        depth -= 1;
        final T[] classT = (T[]) new Object[] { null };

        JsonNode node = null;
        try {
            node = objectMapper.readTree(jsonString);
            List<String> fields = takeFields(t);
            for (String field : fields) {
                if (node.get(field) == null) throw new Exception("Json string no field name of keys");
            }
            return JsonUtils.toObject(t, jsonString);
        } catch (Exception e) {
            //            log.debug(e.getMessage().length()>0?e.getMessage():"Json string to Object failure.");

            if (depth > 0) {
                int finalDepth = depth;

                for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                    Map.Entry<String, JsonNode> x = it.next();
                    String value = x.getValue().toString();
                    //                    log.info("\"{}\":{}",x.getKey(),value);
                    classT[0] = parsing(t, value, finalDepth);
                    if (classT[0] != null) break;
                }
            }
        }

        return classT[0];
    }

    /**
     * 分析 Json 字串 解出自訂物件 (預設3層)
     *
     * @param <T>
     * @param t
     * @param jsonString
     * @return 回傳屬性其中有一個 null，則反回 null
     */
    public static <T> T parsing(Class<T> t, String jsonString) {
        int depth = 3;
        log.info("parsing depth is default:{}", depth);
        return parsing(t, jsonString, depth);
    }

    /**
     * @param <T>
     * @param t
     * @return
     */
    private static <T> List<String> takeFields(Class<T> t) {
        List<String> f = new ArrayList<>();
        Field[] fields = t.getDeclaredFields();
        for (Field field : fields) {
            boolean accessible = field.trySetAccessible(); // 嘗試設置可訪問性
            if (accessible) {
                f.add(field.getName());
            }
        }
        return f;
    }
}
