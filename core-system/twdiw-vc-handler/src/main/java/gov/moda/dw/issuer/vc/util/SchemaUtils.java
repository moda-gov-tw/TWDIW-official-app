package gov.moda.dw.issuer.vc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasquatch.jsonschemainferrer.FormatInferrerInput;
import com.saasquatch.jsonschemainferrer.FormatInferrers;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.RequiredPolicies;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import com.saasquatch.jsonschemainferrer.TitleDescriptionGenerators;
import gov.moda.dw.issuer.vc.vo.VcException;
//import net.jimblackler.jsonschemafriend.Schema;
//import net.jimblackler.jsonschemafriend.SchemaStore;
//import net.jimblackler.jsonschemafriend.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * json schema utilization
 *
 * @version 20240902
 */
public class SchemaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaUtils.class);

    public static String generateSchema(String inputJson,
                                        String id,
                                        String title,
                                        String description) throws VcException {

        try {
            if (inputJson == null || inputJson.isBlank()) {
                throw new Exception("invalid input for schema generation");
            }

            JsonSchemaInferrer inferrer = JsonSchemaInferrer.newBuilder()
                .setSpecVersion(SpecVersion.DRAFT_2020_12)
                .addFormatInferrers(
                    // datetime
                    FormatInferrers.dateTime(),
                    // uri
                    SchemaUtils::absoluteUriFormatInferrer)
                .setRequiredPolicy(RequiredPolicies.noOp())
                // description
                .setTitleDescriptionGenerator(TitleDescriptionGenerators.useFieldNamesAsTitles())
                .build();

            // generate schema
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode input = objectMapper.readTree(inputJson);
            JsonNode output = inferrer.inferForSample(input);
            String schemaJson = objectMapper.writeValueAsString(output);

            // append additional attributes to generated schema json
            return addAdditionalAttributes(schemaJson, id, title, description);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_SYS_GENERATE_SCHEMA_ERROR, e.getMessage());
        }
    }

//    public static boolean validateBySchema(String schemaJson, String inputJson) {
//
//        boolean result = false;
//
//        try {
//            // load schema
//            SchemaStore schemaStore = new SchemaStore();
//            Schema schema = schemaStore.loadSchemaJson(schemaJson);
//
//            // validation error will throw exception
//            Validator validator = new Validator();
//            validator.validateJson(schema, inputJson);
//
//            return true;
//
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//
//        return result;
//    }

    /**
     * FormatInferrer for URI
     *
     * @param input FormatInferrerInput object
     * @return result
     */
    private static String absoluteUriFormatInferrer(@Nonnull FormatInferrerInput input) {

        try {
            String textValue = input.getSample().textValue();
            if (textValue == null) {
                return null;
            }

            URI uri = new URI(textValue);
            if (uri.isAbsolute()) {
                return "uri";
            }
        } catch (Exception e) {
            // ignore
        }

        return null;
    }

    private static String addAdditionalAttributes(String schemaJson,
                                                  String id,
                                                  String title,
                                                  String description) throws Exception {

        if (schemaJson == null || schemaJson.isBlank()) {
            throw new Exception("invalid generated schema json");
        }

        Map<String, Object> map = JsonUtils.jsToMap(schemaJson);
        if (map == null || map.isEmpty()) {
            throw new Exception("invalid generated schema json");
        }

        Map<String, Object> sortedMap = new LinkedHashMap<>();

        // $id
        if (id != null && !id.isBlank()) {
            sortedMap.put("$id", id);
        }

        // $schema
        sortedMap.putIfAbsent("$schema", map.get("$schema"));

        if (title != null && !title.isBlank()) {
            sortedMap.put("title", title);
        }

        if (description != null && !description.isBlank()) {
            sortedMap.put("description", description);
        }

        // type
        sortedMap.putIfAbsent("type", map.get("type"));

        // properties
        sortedMap.putIfAbsent("properties", map.get("properties"));

        return JsonUtils.mapToJs(sortedMap);
    }
}
