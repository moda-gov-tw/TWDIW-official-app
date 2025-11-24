package gov.moda.dw.verifier.oidvp.presentationExchange.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationEvaluationException;
import gov.moda.dw.verifier.oidvp.model.errors.VcValidatedException;
import gov.moda.dw.verifier.oidvp.model.vc.DecodedVerifiableCredential;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.SubmissionInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationSchemaUtils;
import gov.moda.dw.verifier.oidvp.presentationExchange.error.ClaimErrorMessage;
import gov.moda.dw.verifier.oidvp.presentationExchange.error.ClaimErrorMessage.ErrorType;
import gov.moda.dw.verifier.oidvp.presentationExchange.error.PresentationErrorUtils;
import gov.moda.dw.verifier.oidvp.presentationExchange.handler.PresentationDefinitionHandlerClient.ClaimInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Field;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.InputDescriptor;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.util.JsonPathUtils;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputDescriptorFieldHandler extends PresentationDefinitionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputDescriptorFieldHandler.class);
    public static final String VC_TYPE_PATH = "$.type";
    public static final String VC_CREDENTIAL_TYPE = "VerifiableCredential";

    public static final String CREDENTIAL_SUBJECT_PATH_PREDICATE = "$.credentialSubject.";

    public InputDescriptorFieldHandler(PresentationDefinitionHandlerClient client) {
        super("InputDescriptorFieldHandler", client);
    }

    @Override
    public void handle(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws PresentationEvaluationException, VcValidatedException
    {
        if (isUseSubmissionRequirement(pd)) {
            for (SubmissionInfo submissionInfo : submissionInfos) {
                validateIsClaimsMatchInputDescriptorField(submissionInfo);
                LOGGER.info("[check is vc matched selected input_descriptors fields]: PASS");
            }
        } else {
            validateIsClaimsMatchPresentationDefinition(pd, submissionInfos);
            LOGGER.info("[check is vc matched all input_descriptors fields]: PASS");
        }
    }

    private boolean isUseSubmissionRequirement(PresentationDefinition pd) {
        return pd.getSubmissionRequirements() != null && !pd.getSubmissionRequirements().isEmpty();
    }

    private void validateIsClaimsMatchPresentationDefinition(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws VcValidatedException, PresentationEvaluationException
    {
        List<String> inputDescriptorList = new ArrayList<>(pd.getInputDescriptors()
                                                             .stream()
                                                             .map(InputDescriptor::getId)
                                                             .toList());
        for (SubmissionInfo submissionInfo : submissionInfos) {
            InputDescriptor inputDescriptor = submissionInfo.inputDescriptor();
            if (validateIsClaimsMatchInputDescriptorField(submissionInfo)) {
                LOGGER.info("[check is vc claim matched input_descriptor field]: PASS");
                inputDescriptorList.remove(inputDescriptor.getId());
            }
        }
        // vc must meet the criteria of all InputDescriptor of PresentationDefinition
        if (!inputDescriptorList.isEmpty()) {
            throw new VcValidatedException(OidvpError.VC_NOT_MATCH_PRESENTATION_DEFINITION, "vcs did not match all input_descriptors fields in presentation_definition");
        }
    }

    private boolean validateIsClaimsMatchInputDescriptorField(SubmissionInfo submissionInfo)
        throws VcValidatedException, PresentationEvaluationException
    {
        InputDescriptor inputDescriptor = submissionInfo.inputDescriptor();
        DecodedVerifiableCredential decodedVc = submissionInfo.decodedVc();
        String vc = decodedVc.getJsonString();
        FormatRegistry vcFormat = decodedVc.getFormat();

        List<Field> fields = inputDescriptor.getConstraints().getFields();
        DocumentContext vcJsonDocument = JsonPathUtils.getJsonDocument(vc);
        ObjectNode claimObject = getClient().getObjectMapper().createObjectNode();

        for (Field field : fields) {
            List<String> paths = field.getPath();
            if (paths == null) {
                throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "'path' must not be null");
            }

            if (field.getPredicate() != null) {
                continue;
            }

            JsonNode filter = getFilter(field);
            int matched = 0;
            for (String path : paths) {
                String _path = isJwtVc(vcFormat) ? normalizeJwtVcPath(path) : path;
                JsonNode pathValue = JsonPathUtils.getValue(vcJsonDocument, _path);
                if (!pathValue.isNull()) {
                    if (filter != null) {
                        boolean validated = PresentationSchemaUtils.validateFilter(filter, pathValue);
                        if (!validated) {
                            ClaimErrorMessage claimError = PresentationErrorUtils.getErrorMessage(ErrorType.FILTER, submissionInfo, paths, vcJsonDocument);
                            String errorMessage = "vc claim is not matched the requirement of filter".concat(claimError.getEvaluationErrorMessage());
                            LOGGER.error(errorMessage);
                            throw new VcValidatedException(OidvpError.VC_CLAIM_NOT_MATCH_FILTER, errorMessage);
                        }
                    }

                    if (!isPathCorrespondToCredentialType(vcFormat, _path)) {
                        claimObject.set(getPathFieldName(vcFormat, _path), pathValue);
                    }

                    matched++;
                    break;
                }
            }
            boolean optional = Optional.ofNullable(field.getOptional()).orElse(false);
            // vc must match one path value
            if (matched == 0 && !optional) {
                ClaimErrorMessage claimError = PresentationErrorUtils.getErrorMessage(ErrorType.CLAIM_NOT_FOUND, submissionInfo, paths, vcJsonDocument);
                String errorMessage = "can not find required claim in vc".concat(claimError.getEvaluationErrorMessage());
                LOGGER.error(errorMessage);
                throw new VcValidatedException(OidvpError.CLAIM_NOT_FOUND, errorMessage);
            }
        }

        // get credential type
        String credentialTypePath = getCredentialTypePath(vcFormat);
        JsonNode typeJsonNode = JsonPathUtils.getValue(vcJsonDocument, credentialTypePath);
        if (typeJsonNode.isNull()) {
            throw new VcValidatedException(OidvpError.INVALID_VC, "vc must contain 'type'");
        }
        List<String> type = getCredentialType(typeJsonNode);

        // update result claim map
        getClient().getClaimInfoMap().put(inputDescriptor.getId(), new ClaimInfo(type, claimObject));

        return true;
    }

    private JsonNode getFilter(Field field) throws PresentationEvaluationException {
        Map<String, Object> filter = field.getFilter();
        JsonNode filterJsonNode;
        if (filter != null && !filter.isEmpty()) {
            filterJsonNode = JsonUtils.getJsonNode(filter);
            if (filterJsonNode.isNull()) {
                throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "invalid filter format");
            }
            return filterJsonNode;
        } else {
            return null;
        }
    }

    private boolean isPathCorrespondToCredentialType(FormatRegistry format, String path) {
        return getCredentialTypePath(format).equals(path);
    }

    private boolean isJwtVc(FormatRegistry vcFormat) {
        return FormatRegistry.JWT_VC.equals(vcFormat) || FormatRegistry.JWT_VC_JSON.equals(vcFormat);
    }

    private String normalizeJwtVcPath(String path) {
        // Since the jwt format vc to evaluate for this process is jwt decoded, convert the path if path is for jwt.
        if (path.startsWith("$.vc.")) {
            return path.replace("$.vc.", "$.");
        } else if (path.startsWith("vc.")) {
            return path.replace("vc.", "$.");
        } else {
            return path;
        }
    }

    private String getPathFieldName(FormatRegistry format, String path) {
        String fieldName;
        switch (format) {
            case LDP_VC, JWT_VC, JWT_VC_JSON -> fieldName = path.replace(CREDENTIAL_SUBJECT_PATH_PREDICATE, "");
            default -> throw new UnsupportedOperationException();
        }
        return fieldName;
    }

    private String getCredentialTypePath(FormatRegistry formatRegistry) {
        String typePath;
        switch (formatRegistry) {
            case LDP_VC, JWT_VC, JWT_VC_JSON -> typePath = VC_TYPE_PATH; // for jwt decoded jwt_vc
            default -> throw new UnsupportedOperationException();
        }
        return typePath;
    }

    private List<String> getCredentialType(JsonNode type) {
        if (type.isArray()) {
            ArrayList<String> credentialType = new ArrayList<>();
            ArrayNode typeArray = (ArrayNode) type;
            for (JsonNode typeNode : typeArray) {
                String _type = typeNode.asText();
                if (!VC_CREDENTIAL_TYPE.equals(_type) && _type != null) {
                    credentialType.add(_type);
                }
            }
            return credentialType;
        } else {
            String _type = type.asText();
            return VC_CREDENTIAL_TYPE.equals(_type) ? Collections.emptyList() : Collections.singletonList(_type);
        }
    }
}
