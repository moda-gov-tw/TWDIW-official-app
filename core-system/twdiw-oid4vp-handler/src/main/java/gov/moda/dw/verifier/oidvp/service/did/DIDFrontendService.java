package gov.moda.dw.verifier.oidvp.service.did;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSHeader.Builder;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.dao.OidvpPropertyDAO;
import gov.moda.dw.verifier.oidvp.model.did.DIDCreateRequest;
import gov.moda.dw.verifier.oidvp.model.did.DIDCreateResponse;
import gov.moda.dw.verifier.oidvp.model.did.DIDGenerateRequest;
import gov.moda.dw.verifier.oidvp.model.did.DIDGenerateResponse;
import gov.moda.dw.verifier.oidvp.model.did.DIDRegisterRequest;
import gov.moda.dw.verifier.oidvp.model.did.DIDRegisterResponse;
import gov.moda.dw.verifier.oidvp.model.did.DIDReviewRequest;
import gov.moda.dw.verifier.oidvp.model.did.DIDReviewResponse;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.util.HttpUtils;
import gov.moda.dw.verifier.oidvp.util.JWKUtils;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientResponseException;

@Service
public class DIDFrontendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DIDFrontendService.class);
    public static final int VERIFIER_ORG_TYPE = 2;
    public static final String ACCESS_TOKEN_PREDICATE = "access_token.frontend.register_did";

    private final HttpUtils httpUtils;
    private final OidvpConfig oidvpConfig;
    private final OidvpPropertyDAO oidvpPropertyDAO;


    public DIDFrontendService(OidvpConfig oidvpConfig, OidvpPropertyDAO oidvpPropertyDAO) {
        Assert.notNull(oidvpConfig.getFrontendGenerateDidURI(), OidvpConfig.FRONTEND_GENERATE_DID_PREDICATE + " must be specified.");
        Assert.notNull(oidvpConfig.getFrontendCreateDidURI(), OidvpConfig.FRONTEND_CREATE_DID_PREDICATE + " must be specified.");
        this.oidvpConfig = oidvpConfig;
        this.oidvpPropertyDAO = oidvpPropertyDAO;
        if (oidvpConfig.isHttpProxyEnable()) {
            this.httpUtils = new HttpUtils.HttpUtilsBuilder(3, oidvpConfig.getVerifierBusinessHttpTimeout())
                .setProxy(oidvpConfig.getHttpProxyProtocol(), oidvpConfig.getHttpProxyUrl(), oidvpConfig.getHttpProxyPort())
                .build();
        } else {
            this.httpUtils = new HttpUtils.HttpUtilsBuilder(3, oidvpConfig.getVerifierBusinessHttpTimeout()).build();
        }
    }

    public DIDGenerateResponse generateDID(DIDGenerateRequest request, String accessToken) throws OidvpException {
        if (request == null) {
            throw new BadOidvpParamException("DIDGenerateRequest must not be null");
        }
        URI frontendGenerateDidURI = oidvpConfig.getFrontendGenerateDidURI();
        HttpHeaders authHeader = getAuthHeader(accessToken);
        try {
            ResponseEntity<DIDGenerateResponse> response = httpUtils.doPost(frontendGenerateDidURI.toString(), request, authHeader, DIDGenerateResponse.class);
            if (response.getBody() == null) {
                throw new OidvpException(OidvpError.CALL_FRONTEND_GENERATE_DID_ERROR, "frontend generate DID response null");
            } else {
                return response.getBody();
            }
        } catch (RestClientResponseException e) {
            String errorMessage = "fail to generate DID (http=" + e.getStatusCode().value() + ", response=[" + e.getResponseBodyAsString() + "])";
            throw new OidvpException(OidvpError.CALL_FRONTEND_GENERATE_DID_ERROR, errorMessage);
        } catch (Exception e) {
            throw new OidvpException(OidvpError.CALL_FRONTEND_GENERATE_DID_ERROR, "call frontend generate did error, " + e.getMessage(), e);
        }
    }

    @Deprecated
    public DIDRegisterResponse registerDID(ECKey didKey, Map<String, Object> didDocument, Map<String, Object> org, String accessToken) throws OidvpException {
        if (didKey == null) {
            throw new InvalidParameterException("didKey must not be null");
        }
        if (didDocument == null || didDocument.isEmpty()) {
            throw new InvalidParameterException("didDocument must not be empty");
        }

        // sign didDocument
        JWT signedDidDocument = signDidDocument(didKey, didDocument);
        DIDRegisterRequest request = new DIDRegisterRequest(signedDidDocument.serialize(), VERIFIER_ORG_TYPE, org);
        URI frontendRegisterDidURI = oidvpConfig.getFrontendRegisterDidURI();
        HttpHeaders authHeader = getAuthHeader(accessToken);
        try {
            ResponseEntity<DIDRegisterResponse> response = httpUtils.doPost(frontendRegisterDidURI.toString(), request, authHeader, DIDRegisterResponse.class);
            if (response.getBody() == null) {
                throw new OidvpException(OidvpError.CALL_FRONTEND_REGISTER_DID_ERROR, "frontend register DID response null");
            } else {
                return response.getBody();
            }
        } catch (RestClientResponseException e) {
            String errorMessage = "fail to register DID (http=" + e.getStatusCode().value() + ", response=[" + e.getResponseBodyAsString() + "])";
            throw new OidvpException(OidvpError.CALL_FRONTEND_REGISTER_DID_ERROR, errorMessage);
        } catch (Exception e) {
            throw new OidvpException(OidvpError.CALL_FRONTEND_REGISTER_DID_ERROR, "call frontend register did error" + e.getMessage(), e);
        }
    }

    @Deprecated
    public DIDReviewResponse reviewDID(DIDReviewRequest request, String accessToken) throws OidvpException {
        if (request == null) {
            throw new BadOidvpParamException("DIDReviewRequest must not be null");
        }
        URI frontendReviewDidURI = oidvpConfig.getFrontendReviewDidURI();
        HttpHeaders authHeader = getAuthHeader(accessToken);
        try {
            ResponseEntity<DIDReviewResponse> response = httpUtils.doPost(frontendReviewDidURI.toString(), request, authHeader, DIDReviewResponse.class);
            if (response.getBody() == null) {
                throw new OidvpException(OidvpError.CALL_FRONTEND_REVIEW_DID_ERROR, "frontend review DID response null");
            } else {
                return response.getBody();
            }
        } catch (RestClientResponseException e) {
            String errorMessage = "fail to review DID (http=" + e.getStatusCode().value() + ", response=[" + e.getResponseBodyAsString() + "])";
            throw new OidvpException(OidvpError.CALL_FRONTEND_REVIEW_DID_ERROR, errorMessage);
        } catch (Exception e) {
            throw new OidvpException(OidvpError.CALL_FRONTEND_REVIEW_DID_ERROR, "call frontend review did error" + e.getMessage(), e);
        }
    }

    public DIDCreateResponse createDID(ECKey didKey, Map<String, Object> didDocument, Map<String, Object> org, String p7data, String accessToken) throws OidvpException {
        if (didKey == null) {
            throw new InvalidParameterException("didKey must not be null");
        }
        if (didDocument == null || didDocument.isEmpty()) {
            throw new InvalidParameterException("didDocument must not be empty");
        }

        JWT signedDidDocument = signDidDocument(didKey, didDocument);
        DIDCreateRequest didCreateRequest = new DIDCreateRequest(signedDidDocument.serialize(), VERIFIER_ORG_TYPE, org, p7data);
        URI frontendCreateDidURI = oidvpConfig.getFrontendCreateDidURI();
        HttpHeaders authHeader = getAuthHeader(accessToken);
        try {
            ResponseEntity<DIDCreateResponse> response = httpUtils.doPost(frontendCreateDidURI.toString(), didCreateRequest, authHeader, DIDCreateResponse.class);
            if (response.getBody() == null) {
                throw new OidvpException(OidvpError.CALL_FRONTEND_CREATE_DID_ERROR, "frontend create DID response null");
            } else {
                return response.getBody();
            }
        } catch (RestClientResponseException e) {
            String errorMessage = "fail to create DID (http=" + e.getStatusCode().value() + ", response=[" + e.getResponseBodyAsString() + "]";
            throw new OidvpException(OidvpError.CALL_FRONTEND_CREATE_DID_ERROR, errorMessage);
        } catch (Exception e) {
            throw new OidvpException(OidvpError.CALL_FRONTEND_CREATE_DID_ERROR, "call frontend create did error" + e.getMessage(), e);
        }
    }

    public String extractDidFromDidDocument(Map<String, Object> didDocument) throws OidvpException {
        if (!didDocument.containsKey("id")) {
            throw new OidvpException(OidvpError.PARSE_DID_FROM_DOCUMENT_ERROR, "'id' not found in did document");
        }
        String did = (String) didDocument.get("id");
        if (did == null || did.isEmpty()) {
            throw new OidvpException(OidvpError.PARSE_DID_FROM_DOCUMENT_ERROR, "empty did in did document");
        }
        return did;
    }

    public JWT signDidDocument(ECKey ecKey, Map<String, Object> didDocument) throws OidvpException {
        try {
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(didDocument);
            JWSAlgorithm algorithm = JWKUtils.getDefaultJWSAlgorithm(ecKey);
            JWSHeader jwsHeader = new Builder(algorithm)
                .keyID(ecKey.getKeyID())
                .type(JOSEObjectType.JWT)
                .jwk(ecKey.toPublicJWK())
                .build();

            JWSSigner signer = JWKUtils.getJWSSigner(ecKey, algorithm);
            SignedJWT signedDidDocument = new SignedJWT(jwsHeader, claimsSet);
            signedDidDocument.sign(signer);
            return signedDidDocument;
        } catch (Exception e) {
            LOGGER.error("sign did document error: {}", e.getMessage(), e);
            throw new OidvpException(OidvpError.SIGN_DID_DOCUMENT_ERROR);
        }
    }

    public String getAccessToken() {
        try {
            return oidvpPropertyDAO.getPropertyByKey(ACCESS_TOKEN_PREDICATE).getValue();
        } catch (Exception e) {
            throw new OidvpRuntimeException(OidvpError.REGISTER_DID_ERROR, "get access_token error: " + e.getMessage(), e);
        }
    }

    private HttpHeaders getAuthHeader(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new OidvpRuntimeException(OidvpError.REGISTER_DID_ERROR, "access_token is empty");
        }
        if (!accessToken.matches("^[a-zA-Z0-9/_\\-\\.]+$")) {
            throw new OidvpRuntimeException(OidvpError.REGISTER_DID_ERROR, "access_token is invalid");
        }

        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("Access-Token", List.of(accessToken));
        HttpHeaders headers = new HttpHeaders(map);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
