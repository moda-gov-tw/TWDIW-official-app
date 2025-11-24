package gov.moda.dw.verifier.oidvp.presentationExchange.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationSchemaUtils;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Field;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.util.List;
import java.util.Map;

public class FieldValidator extends AbstractValidator<Field> {

    @Override
    public ValidateResult validate(Field field) {
        if (!isValidJsonPath(field)) {
            return new ValidateResult(false, "field 'path' value is invalid jsonpath");
        }
        // may be checked when doing json schema validation
        if (field.getFilter() != null && !isValidFilter(field.getFilter())) {
            return new ValidateResult(false, "invalid filter schema");
        }

        return new ValidateResult(true, "field is valid");
    }

    private boolean isValidFilter(Map<String, Object> filter) {
        JsonNode jsonNode = JsonUtils.getJsonNode(filter);
        if (jsonNode.isNull()) {
            return false;
        }
        return PresentationSchemaUtils.isValidFilterSchema(jsonNode);
    }

    private boolean isValidJsonPath(Field field) {
        List<String> path = field.getPath();
        if (path == null || path.isEmpty()) {
            return false;
        }

        for (String jsonPath : path) {
            if (!isValidJsonPath(jsonPath)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidJsonPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }

        try {
           JsonPath.compile(path);
           return true;
        } catch (Exception e) {
            return false;
        }
    }
}
