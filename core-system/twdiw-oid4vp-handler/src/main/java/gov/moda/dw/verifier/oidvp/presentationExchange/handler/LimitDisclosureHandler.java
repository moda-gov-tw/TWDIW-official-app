package gov.moda.dw.verifier.oidvp.presentationExchange.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationEvaluationException;
import gov.moda.dw.verifier.oidvp.model.errors.VcValidatedException;
import gov.moda.dw.verifier.oidvp.model.vc.DecodedVerifiableCredential;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.SubmissionInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.handler.PresentationDefinitionHandlerClient.ClaimInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Constraints;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.Constraints.DisclosureLimitation;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.InputDescriptor;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitDisclosureHandler extends PresentationDefinitionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimitDisclosureHandler.class);

    public static final String VC_CREDENTIAL_SUBJECT_PREDICATE = "credentialSubject";

    public LimitDisclosureHandler(PresentationDefinitionHandlerClient client) {
        super("LimitDisclosureHandler", client);
    }

    @Override
    public void handle(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws PresentationEvaluationException, VcValidatedException
    {
        for (SubmissionInfo submissionInfo : submissionInfos) {
            Boolean isVcSupportedLimitDisclosure = submissionInfo.decodedVc().isLimitDisclosure();
            DisclosureLimitation disclosureLimitation = Optional.of(submissionInfo)
                                                                .map(SubmissionInfo::inputDescriptor)
                                                                .map(InputDescriptor::getConstraints)
                                                                .map(Constraints::getLimitDisclosure)
                                                                .orElse(null);

            if (!isVcSupportedLimitDisclosure && DisclosureLimitation.REQUIRED.equals(disclosureLimitation)) {
                final String message = "received vc is not supported limit_disclosure but presentation_definition's limit_disclosure is 'required'";
                LOGGER.error(message);
                throw new VcValidatedException(OidvpError.VC_NOT_SUPPORT_LIMIT_DISCLOSURE, message);
            }

            if (isVcSupportedLimitDisclosure && DisclosureLimitation.REQUIRED.equals(disclosureLimitation)) {
                continue;
            }

            // complement claims to result when not setting limit_disclosure or limit_disclosure is preferred
            ObjectNode credentialSubject = getVcCredentialSubject(submissionInfo);
            assert submissionInfo.inputDescriptor() != null;
            complementClaims(submissionInfo.inputDescriptor().getId(), credentialSubject);
        }
    }

    private ObjectNode getVcCredentialSubject(SubmissionInfo submissionInfo) throws VcValidatedException {
        // for vc
        switch (submissionInfo.decodedVc().getFormat()) {
            case LDP_VC, JWT_VC, JWT_VC_JSON -> {
                JsonNode credentialSubject = Optional.of(submissionInfo.decodedVc())
                                                     .map(DecodedVerifiableCredential::getCredential)
                                                     .map(c -> c.get(VC_CREDENTIAL_SUBJECT_PREDICATE))
                                                     .orElseThrow(() -> new VcValidatedException(OidvpError.INVALID_VC, "can not find 'credentialSubject' field in vc"));

                if (credentialSubject.isObject()) {
                    ObjectNode credentialSubjectObjectNode = (ObjectNode) credentialSubject;
                    credentialSubjectObjectNode.remove("id");
                    return credentialSubjectObjectNode;
                } else {
                    // should not happen
                    throw new VcValidatedException(OidvpError.INVALID_VC, "vc credentialSubject is not json object");
                }
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    private void complementClaims(String inputDescriptorId, ObjectNode credentialSubject) {
        ClaimInfo claimInfo = getClient().getClaimInfoMap().get(inputDescriptorId);
        if (claimInfo == null) {
            // claims may not have been submitted or is invalid to submit, if claims is not found doing nothing.
            LOGGER.warn("claims for input_descriptor id({}) is not found from claims map", inputDescriptorId);
            return;
        }
        List<String> type = claimInfo.type();
        getClient().getClaimInfoMap().put(inputDescriptorId, new ClaimInfo(type, credentialSubject));
    }
}
