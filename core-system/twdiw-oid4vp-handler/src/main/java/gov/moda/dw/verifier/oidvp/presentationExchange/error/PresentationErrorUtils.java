package gov.moda.dw.verifier.oidvp.presentationExchange.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.DocumentContext;
import gov.moda.dw.verifier.oidvp.model.vc.DecodedVerifiableCredential;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.SubmissionInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.error.ClaimErrorMessage.ErrorType;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;
import gov.moda.dw.verifier.oidvp.util.JsonPathUtils;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresentationErrorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationErrorUtils.class);

    public static ClaimErrorMessage getErrorMessage(ErrorType type, SubmissionInfo submissionInfo, List<String> paths, DocumentContext jsonDocument) {
        String requiredClaimName = Optional.ofNullable(paths.get(0))
                                           .map(n -> n.split("\\."))
                                           .map(s -> s[s.length - 1])
                                           .orElse(null);
        String vcType = getVcType(submissionInfo.decodedVc(), jsonDocument);
        return new ClaimErrorMessage(type, requiredClaimName, submissionInfo.inputDescriptor().getId(), vcType);
    }

    private static String getVcType(DecodedVerifiableCredential vc, DocumentContext vcJsonDocument) {
        DocumentContext _vcJsonDocument =
            (vcJsonDocument == null) ? JsonPathUtils.getJsonDocument(vc.getJsonString()) : vcJsonDocument;

        String type = null;
        String typePath;
        FormatRegistry vcFormat = vc.getFormat();
        switch (vcFormat) {
            case JWT_VC, JWT_VC_JSON, LDP_VC -> typePath = "$.type";
            default -> typePath = null;
        }

        try {
            if (typePath != null) {
                JsonNode typeJsonNode = JsonPathUtils.getValue(_vcJsonDocument, typePath);
                type = (typeJsonNode.isNull()) ? "" : typeJsonNode.toString();
            }
        } catch (Exception e) {
            LOGGER.error("get type name from vc error: {}", e.getMessage());
            type = "";
        }
        return type;
    }
}
