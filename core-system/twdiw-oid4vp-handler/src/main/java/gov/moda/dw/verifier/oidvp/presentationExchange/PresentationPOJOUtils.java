package gov.moda.dw.verifier.oidvp.presentationExchange;

import com.fasterxml.jackson.databind.JsonNode;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationPOJOException;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationSubmission;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;

public class PresentationPOJOUtils {

    public static PresentationDefinition getPresentationDefinition(String pdJson) throws PresentationPOJOException {
        PresentationDefinition presentationDefinition = JsonUtils.convertJsonStringToObject(pdJson, PresentationDefinition.class);
        if (presentationDefinition == null) {
            throw new PresentationPOJOException(OidvpError.INVALID_PRESENTATION_DEFINITION, "parse presentation_definition to object error");
        }
        return presentationDefinition;
    }

    public static PresentationDefinition getPresentationDefinition(JsonNode pdJsonNode) throws PresentationPOJOException {
        PresentationDefinition presentationDefinition = JsonUtils.jsonNodeToObject(pdJsonNode, PresentationDefinition.class);
        if (presentationDefinition == null) {
            throw new PresentationPOJOException(OidvpError.INVALID_PRESENTATION_DEFINITION, "parse presentation_definition to object error");
        }
        return presentationDefinition;
    }

    public static PresentationSubmission getPresentationSubmission(String psJson) throws PresentationPOJOException {
        PresentationSubmission presentationSubmission = JsonUtils.convertJsonStringToObject(psJson, PresentationSubmission.class);
        if (presentationSubmission == null) {
            throw new PresentationPOJOException(OidvpError.INVALID_PRESENTATION_SUBMISSION, "parse presentation_submission to object error");
        }
        return presentationSubmission;
    }
}
