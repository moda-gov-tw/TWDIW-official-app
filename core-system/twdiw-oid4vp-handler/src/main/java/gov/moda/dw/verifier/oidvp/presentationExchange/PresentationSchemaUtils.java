package gov.moda.dw.verifier.oidvp.presentationExchange;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaId;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationPOJOException;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationSubmission;
import gov.moda.dw.verifier.oidvp.presentationExchange.schema.AbstractValidator.ValidateResult;
import gov.moda.dw.verifier.oidvp.presentationExchange.schema.PresentationDefinitionValidator;
import gov.moda.dw.verifier.oidvp.presentationExchange.schema.PresentationSubmissionValidator;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PresentationSchemaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationSchemaUtils.class);
    private static final JsonSchemaFactory JSON_SCHEMA_FACTORY_V7 = JsonSchemaFactory.getInstance(VersionFlag.V7);
    private static final JsonSchema SCHEMA_V7 = JSON_SCHEMA_FACTORY_V7.getSchema(SchemaLocation.of(SchemaId.V7));
    private final PresentationDefinitionValidator presentationDefinitionValidator;
    private final PresentationSubmissionValidator presentationSubmissionValidator;

    private final JsonSchema presentationDefinitionSchema;
    private final JsonSchema presentationSubmissionSchema;

    public PresentationSchemaUtils(
        @Qualifier(value = "presentationDefinitionSchema") JsonSchema presentationDefinitionSchema,
        @Qualifier(value = "presentationSubmissionSchema") JsonSchema presentationSubmissionSchema)
    {
        this.presentationDefinitionSchema = presentationDefinitionSchema;
        this.presentationSubmissionSchema = presentationSubmissionSchema;
        presentationDefinitionValidator = new PresentationDefinitionValidator();
        presentationSubmissionValidator = new PresentationSubmissionValidator();
    }


    public static boolean isValidFilterSchema(JsonNode filter) {
        Set<ValidationMessage> messages = SCHEMA_V7.validate(filter);
        if (!messages.isEmpty()) {
            for (ValidationMessage message : messages) {
                LOGGER.error("invalid filter: {}", message.getMessage());
            }
            return false;
        }
        return true;
    }

    public static boolean validateFilter(JsonNode filter, JsonNode valueTobeValidated) {
        JsonSchema jsonSchema = JSON_SCHEMA_FACTORY_V7.getSchema(filter);
        Set<ValidationMessage> result = jsonSchema.validate(valueTobeValidated);
        if (!result.isEmpty()) {
            for (ValidationMessage validationMessage : result) {
                LOGGER.error("validate filter error: {}", validationMessage.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }

    public PDSchemaResult isValidPresentationDefinitionSchema(String pdString) {
        if (pdString == null) {
            return PDSchemaResult.invalid("presentation_definition string is null");
        }
        JsonNode jsonNode = JsonUtils.getJsonNode(pdString);
        return isValidPresentationDefinitionSchema(jsonNode);
    }

    public PDSchemaResult isValidPresentationDefinitionSchema(JsonNode pdJsonNode) {
        if (pdJsonNode.isNull()) {
            return PDSchemaResult.invalid("presentation_definition is not in json format");
        }
        Set<ValidationMessage> result = presentationDefinitionSchema.validate(pdJsonNode);
        if (result.isEmpty()) {
            PresentationDefinition pd = getPresentationDefinitionPOJO(pdJsonNode);
            return validateProperty(pd);
        } else {
            for (ValidationMessage validationMessage : result) {
                LOGGER.error("invalid pd schema: {}", validationMessage.getMessage());
            }
            return PDSchemaResult.invalid("invalid presentation_definition schema");
        }
    }

    public PSSchemaResult isValidPresentationSubmissionSchema(String psString) {
        if (psString == null) {
            return PSSchemaResult.invalid("presentation_submission string is null");
        }

        JsonNode psJson = JsonUtils.getJsonNode(psString);
        if (psJson.isNull()) {
            return PSSchemaResult.invalid("presentation_submission is not in json format");
        }
        Set<ValidationMessage> result = presentationSubmissionSchema.validate(psJson);
        if (result.isEmpty()) {
            PresentationSubmission ps = getPresentationSubmissionPOJO(psString);
            return validateProperty(ps);
        } else {
            for (ValidationMessage validationMessage : result) {
                LOGGER.error("invalid ps schema: {}", validationMessage.getMessage());
            }
            return PSSchemaResult.invalid("invalid presentation_submission schema");
        }
    }

    private PDSchemaResult validateProperty(PresentationDefinition pd) {
        ValidateResult validateResult = presentationDefinitionValidator.validate(pd);
        if (validateResult.isValid()) {
            return PDSchemaResult.valid(pd);
        } else {
            LOGGER.error("invalid pd property: {}", validateResult.message());
            return PDSchemaResult.invalid(validateResult.message());
        }
    }

    private PSSchemaResult validateProperty(PresentationSubmission ps) {
        ValidateResult validateResult = presentationSubmissionValidator.validate(ps);
        if (validateResult.isValid()) {
            return PSSchemaResult.valid(ps);
        } else {
            LOGGER.error("invalid ps property: {}", validateResult.message());
            return PSSchemaResult.invalid(validateResult.message());
        }
    }

    private PresentationDefinition getPresentationDefinitionPOJO(String pdString) {
        try {
            return PresentationPOJOUtils.getPresentationDefinition(pdString);
        } catch (PresentationPOJOException e) {
            LOGGER.error("get PresentationDefinitionPOJO error");
            return null;
        }
    }

    private PresentationDefinition getPresentationDefinitionPOJO(JsonNode pdJsonNode) {
        try {
            return PresentationPOJOUtils.getPresentationDefinition(pdJsonNode);
        } catch (PresentationPOJOException e) {
            LOGGER.error("get PresentationDefinitionPOJO error");
            return null;
        }
    }

    private PresentationSubmission getPresentationSubmissionPOJO(String psString) {
        try {
            return PresentationPOJOUtils.getPresentationSubmission(psString);
        } catch (PresentationPOJOException e) {
            LOGGER.error("get PresentationSubmissionPOJO error");
            return null;
        }
    }


    // record
    public record PDSchemaResult(boolean isValid, String message, PresentationDefinition pd) {

        public static PDSchemaResult valid(PresentationDefinition pd) {
            return new PDSchemaResult(true, "presentation_definition is valid", pd);
        }

        public static PDSchemaResult invalid(String errorMessage) {
            return new PDSchemaResult(false, errorMessage, null);
        }
    }

    public record PSSchemaResult(boolean isValid, String message, PresentationSubmission ps) {

        public static PSSchemaResult valid(PresentationSubmission ps) {
            return new PSSchemaResult(true, "presentation_submission is valid", ps);
        }

        public static PSSchemaResult invalid(String errorMessage) {
            return new PSSchemaResult(false, errorMessage, null);
        }
    }
}
