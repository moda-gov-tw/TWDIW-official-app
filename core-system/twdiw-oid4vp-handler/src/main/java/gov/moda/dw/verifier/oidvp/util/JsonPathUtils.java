package gov.moda.dw.verifier.oidvp.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPathUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPathUtils.class);
    private static final Configuration CONFIGURATION = Configuration.defaultConfiguration()
                                                                    .addOptions(Option.REQUIRE_PROPERTIES);


    public static DocumentContext getJsonDocument(String json) {
        try {
            return JsonPath.parse(json, CONFIGURATION);
        } catch (Exception e) {
            LOGGER.error("parse json to document error: {}", e.getMessage());
            throw new IllegalArgumentException("invalid json, parse json document error");
        }
    }

    /**
     * get json path value form parsed json document
     *
     * @param jsonDocument parsed json document
     * @param jsonPath     json path
     * @return value of {@link JsonNode}, otherwise {@link NullNode} if unable to get value from json or cannot parse
     * the value to JsonNode.
     */
    public static JsonNode getValue(DocumentContext jsonDocument, String jsonPath) {
        try {
            Object value = jsonDocument.read(jsonPath);
            return JsonUtils.getJsonNode(value);
        } catch (PathNotFoundException e) {
            LOGGER.warn("can not find value in the path");
            return JsonUtils.getJsonNode(null);
        } catch (Exception e) {
            LOGGER.error("get jsonPath value error: {}", e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
