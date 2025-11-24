package gov.moda.dw.verifier.oidvp.service.oidvp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nimbusds.jwt.SignedJWT;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.dao.PresentationDefinitionDao;
import gov.moda.dw.verifier.oidvp.dao.VerifyResultDao;
import gov.moda.dw.verifier.oidvp.domain.SessionJpa;
import gov.moda.dw.verifier.oidvp.domain.VcResponseObjectDTO;
import gov.moda.dw.verifier.oidvp.domain.VerifyResultJpa;
import gov.moda.dw.verifier.oidvp.model.PresentationDataRequest;
import gov.moda.dw.verifier.oidvp.model.PresentationDataRequest.Mode;
import gov.moda.dw.verifier.oidvp.model.Response;
import gov.moda.dw.verifier.oidvp.model.VerifyResult;
import gov.moda.dw.verifier.oidvp.model.VpValidatedResult;
import gov.moda.dw.verifier.oidvp.model.VpValidatedResult.VcData;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpErrorProperty;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationEvaluationException;
import gov.moda.dw.verifier.oidvp.model.errors.VpValidatedException;
import gov.moda.dw.verifier.oidvp.model.oid4vp.AuthorizationResponse;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpAuthorizationResponse;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.CredentialInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationDefinitionEvaluation.CredentialPath;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationPOJOUtils;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationSchemaUtils;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationSchemaUtils.PDSchemaResult;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationSchemaUtils.PSSchemaResult;
import gov.moda.dw.verifier.oidvp.presentationExchange.handler.PresentationDefinitionHandlerClient.PresentationDefinitionHandlerResult;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationSubmission;
import gov.moda.dw.verifier.oidvp.service.oidvp.customData.CustomDataValidator;
import gov.moda.dw.verifier.oidvp.service.oidvp.customData.CustomDataValidator.CustomDataValidateResult;
import gov.moda.dw.verifier.oidvp.service.vp.PresentationValidationService;
import gov.moda.dw.verifier.oidvp.service.vp.VerifierBusinessService;
import gov.moda.dw.verifier.oidvp.util.StringUtils;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class VerifierService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifierService.class);

    private final OidvpConfig oidvpConfig;
    private final VerifyResultDao verifyResultDao;
    private final PresentationDefinitionEvaluation presentationDefinitionEvaluation;
    private final PresentationDefinitionDao presentationDefinitionDao;
    private final PresentationSchemaUtils presentationSchemaUtils;
    private final PresentationValidationService presentationValidationService;
    private final VerifierBusinessService verifierBusinessService;

    private final int verifyResultAliveTime;

    public VerifierService(OidvpConfig oidvpConfig, PresentationDefinitionDao presentationDefinitionDao, PresentationDefinitionEvaluation presentationDefinitionEvaluation,
        PresentationSchemaUtils presentationSchemaUtils, PresentationValidationService presentationValidationService, VerifyResultDao verifyResultDao, VerifierBusinessService verifierBusinessService)
    {
        this.oidvpConfig = oidvpConfig;
        this.presentationDefinitionDao = presentationDefinitionDao;
        this.presentationDefinitionEvaluation = presentationDefinitionEvaluation;
        this.presentationSchemaUtils = presentationSchemaUtils;
        this.presentationValidationService = presentationValidationService;
        this.verifyResultDao = verifyResultDao;
        this.verifierBusinessService = verifierBusinessService;
        verifyResultAliveTime = oidvpConfig.getVerifyResultExpiredTime();
    }

    /**
     * verify oid4vp authorization response
     */
    public VerifyResult verify(SessionJpa session, AuthorizationResponse authorizationResponse, String responseCode) throws SQLException {
        VerifyInfoDTO verifyInfoDTO = new VerifyInfoDTO(session);
        VerifyResult verifyResult;
        if (authorizationResponse instanceof OidvpAuthorizationResponse oidvpAuthorizationResponse) {
            verifyResult = verify(verifyInfoDTO, oidvpAuthorizationResponse);
        } else {
            throw new UnsupportedOperationException("unsupported AuthorizationResponse type: " + authorizationResponse.getClass());
        }

        if (session.getCallback() != null) {
            doVerifyResultCallbackAsync(session, verifyResult);
        }
        saveVerifyResult(session, responseCode, verifyResult);
        return verifyResult;
    }

    public VerifyResult verify(VerifyInfoDTO verifyInfoDTO, OidvpAuthorizationResponse authorizationResponse) {
        VerifyResult verifyResult;
        if (authorizationResponse.isSuccess()) {
            verifyResult = verifyPresentation(verifyInfoDTO, authorizationResponse.getVpToken(), authorizationResponse.getPresentationSubmission());
        } else { // wallet return error
            String _error = StringUtils.removeIllegalChars(authorizationResponse.getError());
            String _errorDescription = StringUtils.removeIllegalChars(authorizationResponse.getErrorDescription());
            LOGGER.error("wallet return error: {}, {}", _error, _errorDescription);
            String errorMessage = "wallet response error: " + Optional.ofNullable(_errorDescription)
                                                                      .map(ed -> _error + ";" + ed)
                                                                      .orElse(_error);
            verifyResult = VerifyResult.fail(OidvpError.AUTHZ_RESPONSE_ERROR, errorMessage);
        }

        verifyResult = validateCustomData(verifyResult, authorizationResponse);
        return verifyResult;
    }

    public VerifyResult verifyPresentation(VerifyInfoDTO verifyInfoDTO, String vpToken, String presentationSubmission) {
        String nonce = verifyInfoDTO.nonce();
        String clientId = verifyInfoDTO.clientId();
        String pdString = verifyInfoDTO.presentationDefinition();
        String holderDid = null;

        if (!org.springframework.util.StringUtils.hasText(nonce)
            || !org.springframework.util.StringUtils.hasText(clientId)
            || !org.springframework.util.StringUtils.hasText(pdString)
        ) {
            LOGGER.error("required verify info form verifyInfoDTO is null or blank");
            return VerifyResult.fail(OidvpError.BAD_OIDVP_PARAM, "required verify info is null or blank");
        }

        // start validate vp
        try {
            PresentationSubmission ps;
            PSSchemaResult psResult = presentationSchemaUtils.isValidPresentationSubmissionSchema(presentationSubmission);
            if (psResult.isValid()) {
                ps = psResult.ps();
            } else {
                String errorMessage = "invalid presentation_submission: " + psResult.message();
                throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_SUBMISSION, errorMessage);
            }

            LOGGER.debug("pd={}", pdString);
            PresentationDefinition pd = PresentationPOJOUtils.getPresentationDefinition(pdString);

            // 1. validate vp and vc is valid
            ArrayNode vpTokens = presentationValidationService.getVpTokenArrayNode(vpToken);
            List<VpValidatedResult> vpValidatedResults = presentationValidationService.validateVpToken(oidvpConfig.getVpVerifyUri(), vpTokens, verifyInfoDTO.transactionId());
            holderDid = presentationValidationService.extractHolderDid(vpValidatedResults);
            LOGGER.info("[check if vp is validated]: PASS");

            // 2. check client_id and nonce
            if (validateClientIdAndNonce(vpValidatedResults, verifyInfoDTO)) {
                LOGGER.info("[check client_id and nonce]: PASS");
            }

            // 3.1 check if presentation_submission id match presentation_definition id
            // 3.2 check the number of vp/vc
            // 3.3 check if Credential meet all criteria in presentation_definition
            HashMap<CredentialPath, CredentialInfo> vcs = toCredentialMap(vpValidatedResults);
            PresentationDefinitionHandlerResult evaluationResult = presentationDefinitionEvaluation.evaluatePresentations(pd, ps, vcs);
            LOGGER.info("[check if vc match presentation_definition]: PASS");

            LOGGER.info("verify success!");
            List<VcResponseObjectDTO> vcClaims = VcResponseObjectDTO.toVcResponseObjectDTOs(evaluationResult);
            return VerifyResult.success(holderDid, vcClaims);
        } catch (OidvpException exception) {
            OidvpErrorProperty oidvpErrorProperty = (OidvpErrorProperty) exception;
            String message = oidvpErrorProperty.getOidvpErrorMessage();
            LOGGER.error("vp validate fail, {}", message, exception);
            return VerifyResult.fail(holderDid, oidvpErrorProperty.getOidvpError(), message);
        } catch (Exception exception) {
            LOGGER.error("vp verify fail, other error: {}", exception.getMessage(), exception);
            return VerifyResult.fail(holderDid, OidvpError.VERIFY_FAIL, "verify fail");
        }
    }

    /**
     * get verify result by transaction_id
     */
    public VerifyResult getVerifyResult(String transactionId, String responseCode) throws SQLException {
        if (responseCode == null && transactionId == null) {
            throw new InvalidParameterException("'response_code' and 'transaction_id' must not be null at the same time.");
        }

        VerifyResultJpa verifyResultJpa;
        if (responseCode != null) {
            verifyResultJpa = verifyResultDao.getVerifyResultNotExpiredByResponseCode(responseCode, verifyResultAliveTime);
            if (!transactionId.equals(verifyResultJpa.getTransactionId())) {
                throw new InvalidParameterException("invalid transaction_id");
            }
        } else {
            verifyResultJpa = verifyResultDao.getVerifyResultNotExpired(transactionId, verifyResultAliveTime);
        }

        VerifyResult verifyResult = new VerifyResult(verifyResultJpa);

        if (oidvpConfig.getVerifyResultDeleteAfterQuery()) {
            verifyResultDao.deleteVerifyResult(transactionId);
        }

        return verifyResult;
    }

    public Response modifyPresentationDefinitionData(PresentationDataRequest request) throws SQLException {
        if (request == null) {
            throw new InvalidParameterException("request must not be null");
        }
        if (request.getMode() == null || request.getBusinessId() == null || request.getSerialNo() == null) {
            throw new InvalidParameterException("required input is not exist");
        }

        Response response = new Response();
        Mode mode = Mode.getMode(request.getMode());
        String businessId = request.getBusinessId();
        String serialNo = request.getSerialNo();

        if (Mode.save.equals(mode)) {
            JsonNode pdJsonNode = request.getPresentationDefinition();
            if (pdJsonNode == null) {
                throw new InvalidParameterException("presentation_definition must be submit");
            }

            PDSchemaResult result = presentationSchemaUtils.isValidPresentationDefinitionSchema(pdJsonNode);
            if (!result.isValid()) {
                LOGGER.error("invalid presentation_definition - business_id={}, serial_no={}", businessId, serialNo);
                response.setCode(OidvpError.INVALID_PRESENTATION_DEFINITION.getCode());
                response.setMessage(OidvpError.INVALID_PRESENTATION_DEFINITION.getMsg());
                return response;
            }

            String presentationDefinition = pdJsonNode.toString();
            presentationDefinitionDao.savePresentationDefinition(businessId, serialNo, presentationDefinition);
            LOGGER.info("save presentation_definition success - business_id={}, serial_no={}, presentation_definition={}",
                businessId, serialNo, presentationDefinition);
        } else {
            presentationDefinitionDao.deletePresentationDefinitionById(businessId, serialNo);
            LOGGER.info("delete presentation_definition success - business_id={}, serial_no={}", businessId, serialNo);
        }

        response.setCode(OidvpError.SUCCESS.getCode());
        response.setMessage(OidvpError.SUCCESS.getMsg());
        return response;
    }

    private VerifyResultJpa saveVerifyResult(SessionJpa sessionJpa, String responseCode, VerifyResult verifyResult) throws SQLException {
        Assert.notNull(sessionJpa, "sessionJpa must not be null");
        Assert.notNull(responseCode, "responseCode must not be null");
        Assert.notNull(verifyResult, "verifyResult must not be null");

        VerifyResultJpa result = verifyResultDao.updateSessionAndSaveResult(sessionJpa, responseCode, verifyResult);
        LOGGER.info("verify result saved");
        return result;
    }

    private void doVerifyResultCallbackAsync(SessionJpa session, VerifyResult verifyResult) {
        LOGGER.info("doing VerifyResult callback...");
        verifierBusinessService.doVerifyResultCallbackAsync(session.getCallback(), verifyResult, session.getTransactionId(), session.getRef());
    }

    private boolean validateClientIdAndNonce(List<VpValidatedResult> vpValidatedResults, VerifyInfoDTO verifyInfoDTO) throws VpValidatedException {
        for (VpValidatedResult validatedResult : vpValidatedResults) {
            if (validatedResult.getClientId() == null) {
                throw new VpValidatedException(OidvpError.ID_NONCE_NOT_MATCH, "vp does not contain client_id");
            }
            if (validatedResult.getNonce() == null) {
                throw new VpValidatedException(OidvpError.ID_NONCE_NOT_MATCH, "vp does not contain nonce");
            }

            if (!Objects.equals(validatedResult.getClientId(), verifyInfoDTO.clientId())) {
                LOGGER.error("invalid client_id: not matched");
                throw new VpValidatedException(OidvpError.ID_NONCE_NOT_MATCH, "invalid client_id or nonce");
            }
            if (!Objects.equals(validatedResult.getNonce(), verifyInfoDTO.nonce())) {
                LOGGER.error("invalid nonce: not matched");
                throw new VpValidatedException(OidvpError.ID_NONCE_NOT_MATCH, "invalid client_id or nonce");
            }
        }
        return true;
    }

    private HashMap<CredentialPath, CredentialInfo> toCredentialMap(VpValidatedResult vpValidatedResult) throws VpValidatedException {
        HashMap<CredentialPath, CredentialInfo> vcs = new HashMap<>();
        for (VcData vcData : vpValidatedResult.getVcs()) {
            CredentialPath path = new CredentialPath(vcData.getVpPath(), vcData.getVcPath());
            JsonNode vc = vcData.getCredential();
            if (vc == null || vc.isNull()) {
                throw new VpValidatedException(OidvpError.VP_RESPONSE_ERROR, "vp validate response is not contained 'credential'");
            }
            // all vc format in use are currently using limitDisclosure.
            Boolean limitDisclosureSupported = vcData.getLimitDisclosureSupported();
            if (limitDisclosureSupported == null) {
                throw new VpValidatedException(OidvpError.VP_RESPONSE_ERROR, "vp validate response is not specified 'LimitDisclosureSupported'");
            }
            vcs.put(path, new CredentialInfo(vc, limitDisclosureSupported));
            LOGGER.info("path = {}", path);
            LOGGER.debug("vc = {}", vc);
        }
        return vcs;
    }

    private HashMap<CredentialPath, CredentialInfo> toCredentialMap(List<VpValidatedResult> vpValidatedResults) throws VpValidatedException {
        HashMap<CredentialPath, CredentialInfo> vcs = new HashMap<>();
        for (VpValidatedResult vpValidatedResult : vpValidatedResults) {
            HashMap<CredentialPath, CredentialInfo> credentialMap = toCredentialMap(vpValidatedResult);
            vcs.putAll(credentialMap);
        }
        return vcs;
    }

    private VerifyResult validateCustomData(VerifyResult verifyResult, OidvpAuthorizationResponse authorizationResponse) {
        if (verifyResult.getVerifyResult() && authorizationResponse.getCustomData() != null) {
            SignedJWT customData;
            try {
                customData = SignedJWT.parse(authorizationResponse.getCustomData());
            } catch (ParseException e) {
                LOGGER.error("invalid custom data format: {}", e.getMessage());
                return VerifyResult.fail(OidvpError.INVALID_CUSTOM_DATA, "invalid custom data format");
            }

            CustomDataValidateResult validateResult = CustomDataValidator.validate(verifyResult.getHolderDid(), customData);
            if (validateResult.validateResult()) {
                LOGGER.debug("custom data validate success");
                verifyResult.setCustomData(validateResult.customData());
                return verifyResult;
            } else {
                LOGGER.error("custom data validate fail: {}", validateResult.errorMessage());
                return VerifyResult.fail(verifyResult.getHolderDid(), OidvpError.INVALID_CUSTOM_DATA, validateResult.errorMessage());
            }
        }
        return verifyResult;
    }

    public record VerifyInfoDTO(String transactionId, String clientId, String nonce, String presentationDefinition, String ref) {

        public VerifyInfoDTO(SessionJpa sessionJpa) {
            this(sessionJpa.getTransactionId(), sessionJpa.getClientId(), sessionJpa.getNonce(), sessionJpa.getPresentationDefinition(), sessionJpa.getRef());
        }
    }
}
