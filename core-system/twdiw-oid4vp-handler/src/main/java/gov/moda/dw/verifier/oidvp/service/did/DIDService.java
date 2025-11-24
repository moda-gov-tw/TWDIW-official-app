package gov.moda.dw.verifier.oidvp.service.did;

import com.nimbusds.jose.jwk.ECKey;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.VerifierRegisterDidRequest;
import gov.moda.dw.verifier.oidvp.model.did.DIDCreateResponse;
import gov.moda.dw.verifier.oidvp.model.did.DIDGenerateRequest;
import gov.moda.dw.verifier.oidvp.model.did.DIDGenerateResponse;
import gov.moda.dw.verifier.oidvp.model.did.DIDRegisterResponse;
import gov.moda.dw.verifier.oidvp.model.did.DIDResponseData;
import gov.moda.dw.verifier.oidvp.model.did.DIDReviewRequest;
import gov.moda.dw.verifier.oidvp.model.did.DIDReviewResponse;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.model.errors.JWKStoreOperationException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.util.JWKUtils;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DIDService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DIDService.class);
    public static final String DID_KEY_ID = "verifier-did";

    private final DIDFrontendService didFrontendService;
    private final DIDEntity didEntity;

    public DIDService(DIDFrontendService didFrontendService, DIDEntity didEntity) {
        this.didFrontendService = didFrontendService;
        this.didEntity = didEntity;
    }

    /**
     * Register verifier DID.
     *
     * @param org organize info
     * @return Registered DID and DID Key jwk.
     * @throws OidvpException if register DID error.
     */
    @Deprecated
    public RegisterDidResult registerVerifierDID(Map<String, Object> org) throws OidvpException {
        if (org == null) {
            throw new InvalidParameterException("'org' must not be null");
        }

        String accessToken = didFrontendService.getAccessToken();

        // 1. generate DID
        ECKey didKey = JWKUtils.getDefaultECKey(DID_KEY_ID);
        if (didKey == null) {
            throw new OidvpException(OidvpError.GENERATE_KEY_ERROR, "generate register DID key error");
        }
        DIDGenerateResponse didGenerateResponse = didFrontendService.generateDID(new DIDGenerateRequest(didKey), accessToken);
        if (!didGenerateResponse.isSuccess()) {
            LOGGER.error("didGenerateResponse = [{}]", JsonUtils.toJsonString(didGenerateResponse));
            throw new OidvpException(OidvpError.CALL_FRONTEND_GENERATE_DID_ERROR, "call frontend generate did error. (code=" + didGenerateResponse.getCode() + ")");
        }
        Map<String, Object> didDocument = Optional.ofNullable(didGenerateResponse.getData())
                                                  .map(DIDResponseData::getDid)
                                                  .filter(document -> !document.isEmpty())
                                                  .orElseThrow(() -> new OidvpException(OidvpError.CALL_FRONTEND_GENERATE_DID_ERROR, "frontend generate did response with empty didDocument"));

        String did = didFrontendService.extractDidFromDidDocument(didDocument);

        // 2. register DID
        DIDRegisterResponse didRegisterResponse = didFrontendService.registerDID(didKey, didDocument, org, accessToken);
        if (!didRegisterResponse.isSuccess()) {
            LOGGER.error("didRegisterResponse = [{}]", JsonUtils.toJsonString(didRegisterResponse));
            throw new OidvpException(OidvpError.CALL_FRONTEND_REGISTER_DID_ERROR, "call frontend register did error. (code=" + didRegisterResponse.getCode() + ")");
        }
        String _did = Optional.ofNullable(didRegisterResponse.getData())
                              .map(DIDResponseData::getId)
                              .filter(didValue -> !didValue.isEmpty())
                              .orElseThrow(() -> new OidvpException(OidvpError.CALL_FRONTEND_REGISTER_DID_ERROR, "frontend register did response with empty did"));

        if (!did.equals(_did)) {
            throw new OidvpException(OidvpError.CALL_FRONTEND_REGISTER_DID_ERROR, "register response did is not matched with generated did");
        }

        // 3. review DID
        DIDReviewResponse didReviewResponse = didFrontendService.reviewDID(new DIDReviewRequest(did, true), accessToken);
        if (!didReviewResponse.isSuccess()) {
            LOGGER.error("didReviewResponse = [{}]", JsonUtils.toJsonString(didReviewResponse));
            throw new OidvpException(OidvpError.CALL_FRONTEND_REVIEW_DID_ERROR, "call frontend review did error. (code=" + didReviewResponse.getCode() + ")");
        }

        // 4. store DID and DID key
        LOGGER.info("store DID and DID key...");
        try {
            didEntity.saveDidAndDidKey(did, didKey);
        } catch (SQLException e) {
            LOGGER.error("registerVerifierDID: store DID error: {}", e.getMessage());
            throw new OidvpException(OidvpError.REGISTER_DID_ERROR, "store DID error", e);
        } catch (JWKStoreOperationException e) {
            LOGGER.error("registerVerifierDID: store DID key error: {}", e.getMessage());
            throw new OidvpException(OidvpError.REGISTER_DID_ERROR, "store DID key error", e);
        }

        LOGGER.info("DID registration completed");
        return new RegisterDidResult(did, didKey);
    }

    /**
     * Register verifier DID.
     *
     * @param request VerifierRegisterDidRequest
     * @return Registered DID and DID Key jwk.
     * @throws OidvpException if register DID error.
     */
    public RegisterDidResult registerVerifierDID(VerifierRegisterDidRequest request) throws OidvpException {
        String accessToken = didFrontendService.getAccessToken();

        // 1. generate DID
        ECKey didKey = JWKUtils.getDefaultECKey(DID_KEY_ID);
        if (didKey == null) {
            throw new OidvpException(OidvpError.GENERATE_KEY_ERROR, "generate register DID key error");
        }
        DIDGenerateResponse didGenerateResponse = didFrontendService.generateDID(new DIDGenerateRequest(didKey), accessToken);
        if (!didGenerateResponse.isSuccess()) {
            LOGGER.error("didGenerateResponse = [{}]", JsonUtils.toJsonString(didGenerateResponse));
            throw new OidvpException(OidvpError.CALL_FRONTEND_GENERATE_DID_ERROR, "call frontend generate did error. (code=" + didGenerateResponse.getCode() + ", msg=" + didGenerateResponse.getMsg() + ")");
        }
        Map<String, Object> didDocument = Optional.ofNullable(didGenerateResponse.getData())
                                                  .map(DIDResponseData::getDid)
                                                  .filter(document -> !document.isEmpty())
                                                  .orElseThrow(() -> new OidvpException(OidvpError.CALL_FRONTEND_GENERATE_DID_ERROR, "frontend generate did response with empty didDocument"));

        String did = didFrontendService.extractDidFromDidDocument(didDocument);

        // 2. DID
        Map<String, Object> org = request == null ? Collections.emptyMap() : request.getOrg();
        String p7data = request == null ? "" : request.getP7data();
        DIDCreateResponse didCreateResponse = didFrontendService.createDID(didKey, didDocument, org, p7data, accessToken);
        if (!didCreateResponse.isSuccess()) {
            LOGGER.error("didCreateResponse = [{}]", JsonUtils.toJsonString(didCreateResponse));
            throw new OidvpException(OidvpError.CALL_FRONTEND_CREATE_DID_ERROR, "call frontend create did error. (code=" + didCreateResponse.getCode() + ", msg=" + didCreateResponse.getMsg() + ")");
        }
        String _did = Optional.ofNullable(didCreateResponse.getData())
                              .map(DIDResponseData::getId)
                              .filter(didValue -> !didValue.isEmpty())
                              .orElseThrow(() -> new OidvpException(OidvpError.CALL_FRONTEND_CREATE_DID_ERROR, "frontend register did response with empty did"));

        if (!did.equals(_did)) {
            throw new OidvpException(OidvpError.CALL_FRONTEND_REGISTER_DID_ERROR, "register response did is not matched with generated did");
        }

        // 3. store DID and DID key
        LOGGER.info("store DID and DID key...");
        try {
            didEntity.saveDidAndDidKey(did, didKey);
        } catch (SQLException e) {
            LOGGER.error("registerVerifierDID: store DID error: {}", e.getMessage());
            throw new OidvpException(OidvpError.REGISTER_DID_ERROR, "store DID error", e);
        } catch (JWKStoreOperationException e) {
            LOGGER.error("registerVerifierDID: store DID key error: {}", e.getMessage());
            throw new OidvpException(OidvpError.REGISTER_DID_ERROR, "store DID key error", e);
        }

        LOGGER.info("DID registration completed");
        return new RegisterDidResult(did, didKey);
    }

    public String getDID() {
        try {
            return didEntity.getDID();
        } catch (NoSuchElementException e) {
            throw new OidvpRuntimeException(OidvpError.GET_DID_ERROR, "get DID error: DID not exist. The DID registration might not have been successfully completed.");
        } catch (SQLException e) {
            LOGGER.error("get DID error: {}", e.getMessage());
            throw new OidvpRuntimeException(OidvpError.GET_DID_ERROR, "get DID error: " + e.getMessage(), e);
        }
    }

    public ECKey getDIDKey() throws OidvpException {
        try {
            return didEntity.getDIDKey();
        } catch (NoSuchElementException e) {
            throw new OidvpException(OidvpError.GET_DID_ERROR, "get DID key error: DID key not exist. The DID registration might not have been successfully completed.");
        } catch (JWKStoreOperationException e) {
            LOGGER.error("get DID key error: {}", e.getMessage());
            throw new OidvpException(OidvpError.GET_DID_ERROR, "get DID key error: " + e.getMessage(), e);
        }
    }

    public record RegisterDidResult(String did, ECKey didKey) {}
}
