package gov.moda.dw.verifier.oidvp.web.rest.oidvp;


import static gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError.GEN_QRCODE_ERROR;

import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import gov.moda.dw.verifier.oidvp.annotation.LogAPI;
import gov.moda.dw.verifier.oidvp.annotation.LogInfo;
import gov.moda.dw.verifier.oidvp.annotation.PrivacyInfo;
import gov.moda.dw.verifier.oidvp.common.ApiId;
import gov.moda.dw.verifier.oidvp.config.Constants;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.dao.SessionDao;
import gov.moda.dw.verifier.oidvp.domain.SessionJpa;
import gov.moda.dw.verifier.oidvp.model.OidvpResponse;
import gov.moda.dw.verifier.oidvp.model.PresentationDataRequest;
import gov.moda.dw.verifier.oidvp.model.Response;
import gov.moda.dw.verifier.oidvp.model.VerifyResponse;
import gov.moda.dw.verifier.oidvp.model.VerifyResult;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.oid4vp.AfterAuthorizationResponse;
import gov.moda.dw.verifier.oidvp.model.oid4vp.AuthorizationRequestOpts;
import gov.moda.dw.verifier.oidvp.model.oid4vp.AuthorizationRequestOpts.JARType;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpAuthorizationResponse;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpResponseMode;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpResponseType;
import gov.moda.dw.verifier.oidvp.service.oidvp.OID4VPService;
import gov.moda.dw.verifier.oidvp.service.oidvp.VerifierService;
import gov.moda.dw.verifier.oidvp.util.LogUtils;
import gov.moda.dw.verifier.oidvp.util.QRCodeUtils;
import java.net.URI;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/oidvp")
@LogInfo
public class OidvpEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OidvpEndpointController.class);

    @Autowired SessionDao sessionDao;
    @Autowired VerifierService verifierService;
    @Autowired OID4VPService oid4vpService;
    @Autowired OidvpConfig oidvpConfig;

    @GetMapping(path = "version", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getVersion() {
        return "{\"version\":\"" + Constants.version + "\"}";
    }

    /**
     * Authorization Request, get oidc for vp regular authorization request uri with Presentation Definition data
     *
     * @param transactionId transaction_id
     * @param ref           presentation definition reference
     * @return authorization request uri
     */
    @LogAPI(ApiId.VP001)
    @GetMapping(path = "authorization-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OidvpResponse> getAuthorizationRequestUri(
        @RequestParam(name = "transaction_id", required = false) String transactionId,
        @RequestParam(name = "ref", required = true) String ref,
        @RequestParam(name = "callback", required = false) String callback
    ) throws SQLException, OidvpException
    {
        String transaction = Optional.ofNullable(transactionId).orElseGet(() -> UUID.randomUUID().toString());
        LogUtils.addRequestID(transaction);

        AuthorizationRequestOpts opts = new AuthorizationRequestOpts(OidvpResponseType.VPTOKEN, ResponseMode.FRAGMENT).setCallbackUri(callback);
        // if ClientIdScheme used DID, authorization request MUST be signed.
        URI uri = oid4vpService.getAuthorizationRequestUsingRequestObject(transaction, ClientIdScheme.DID, ref, JARType.VALUE, opts).toURI();
        //        URI uri = oid4vpService.getAuthorizationRequest(transaction, ClientIdScheme.REDIRECT_URI, ref, opts).toURI();
        Integer expiredIn = oidvpConfig.getSessionExpiredTime() * 60;
        OidvpResponse response = OidvpResponse.success(transaction, uri, expiredIn);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * get authorization request qr code, using request_uri
     *
     * @param transactionId transaction_id
     * @param ref           presentation definition reference
     * @return authorization request uri qrcode in base64 encode
     */
    @LogAPI(ApiId.VP002)
    @GetMapping(path = "qr-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getQRCodeData(
        @RequestParam(name = "transaction_id", required = false) String transactionId,
        @RequestParam(name = "ref", required = true) String ref,
        @RequestParam(name = "callback", required = false) String callback
    ) throws SQLException, OidvpException
    {
        String transaction = Optional.ofNullable(transactionId).orElseGet(() -> UUID.randomUUID().toString());
        LogUtils.addRequestID(transaction);
        AuthorizationRequestOpts opts = new AuthorizationRequestOpts(OidvpResponseType.VPTOKEN, OidvpResponseMode.DIRECT_POST).setCallbackUri(callback);
        URI uri = oid4vpService.getAuthorizationRequestUsingRequestObject(transaction, ClientIdScheme.DID, ref, JARType.REFERENCE, opts).toURI();

        // create QR code
        String qrCodeImage = QRCodeUtils.getQRCodeBase64(uri.toString());
        if (qrCodeImage == null) {
            LOGGER.error("create qr code error");
            return new ResponseEntity<>(OidvpResponse.error(GEN_QRCODE_ERROR), GEN_QRCODE_ERROR.getHttpStatus());
        }

        Integer expiredIn = oidvpConfig.getSessionExpiredTime() * 60;
        OidvpResponse response = OidvpResponse.success(transaction, qrCodeImage, uri, expiredIn);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * get the request object of authorization request for specify {@code state} contains Presentation Definition value
     * and Client Metadata value
     *
     * @param state state
     * @return the JWS request object of authorization request
     */
    @LogAPI(ApiId.VP003)
    @GetMapping(path = "request/{state}", produces = "application/oauth-authz-req+jwt")
    public ResponseEntity<String> getRequestObject(@PathVariable("state") String state) throws SQLException, OidvpException {
        // check if session exist
        SessionJpa session = sessionDao.getSessionByStateNotUsed(state);
        String transactionId = session.getTransactionId();
        String clientId = session.getClientId();
        String nonce = session.getNonce();
        String pd = session.getPresentationDefinition();
        ResponseMode responseMode = session.getResponseMode();
        ResponseType responseType = session.getResponseType();

        LogUtils.addRequestID(transactionId);

        AuthorizationRequestOpts opts = new AuthorizationRequestOpts(responseType, responseMode).setExpiredTime(session.getExpiredTime());
        String requestObject = oid4vpService.getRequestObject(clientId, nonce, state, pd, opts).serialize();

        return new ResponseEntity<>(requestObject, HttpStatus.OK);
    }

    /**
     * get the presentation definition json object for the specify {@code state}
     *
     * @param state state
     * @return the json of presentation definition
     */
    @LogAPI(ApiId.VP004)
    @GetMapping(value = "presentation-definition/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPresentationDefinition(@PathVariable(name = "state") String state) throws SQLException, OidvpException {
        // check if session exist
        SessionJpa session = sessionDao.getSessionByStateNotUsed(state);
        String transactionId = session.getTransactionId();
        LogUtils.addRequestID(transactionId);

        String presentationDefinition = session.getPresentationDefinition();

        return new ResponseEntity<>(presentationDefinition, HttpStatus.OK);
    }

    @LogAPI(ApiId.VP006)
    @PostMapping(path = "authorization-response", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authorizationResponse(@PrivacyInfo(keyName = {"vp_token", "custom_data"}) @RequestParam MultiValueMap<String, String> paramMap) throws SQLException {
        OidvpAuthorizationResponse authorizationResponse;
        try {
            authorizationResponse = OidvpAuthorizationResponse.parse(paramMap);
        } catch (Exception e) {
            throw new InvalidParameterException(e.getMessage());
        }

        // check session
        SessionJpa session = sessionDao.getSessionByStateNotUsed(authorizationResponse.getState());
        LogUtils.addRequestID(session.getTransactionId());

        String responseCode = UUID.randomUUID().toString();

        VerifyResult verifyResult = verifierService.verify(session, authorizationResponse, responseCode);

        // After Authorization Response (optional)
        AfterAuthorizationResponse response;
        if (verifyResult.getVerifyResult()) {
            response = AfterAuthorizationResponse.success();
        } else {
            response = AfterAuthorizationResponse.fail(verifyResult.getErrorCode(), verifyResult.getResultDescription());
        }
        return new ResponseEntity<>(response, verifyResult.getErrorCode().getHttpStatus());
    }

    /**
     * get verify result
     *
     * @param transactionId the transaction_id
     * @param responseCode  the response_code
     * @return verify result
     * @throws SQLException if db occurred error
     */
    @LogAPI(ApiId.VP007)
    @PostMapping(path = "result", produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("hasAuthority('dwverifieroid4vp301i')")
    public ResponseEntity<Response> getResult(
        @RequestHeader(name = "transaction-id", required = true) String transactionId,
        @RequestHeader(name = "response-code", required = false) String responseCode
    ) throws SQLException
    {
        LogUtils.addRequestID(transactionId);
        VerifyResult verifyResult = verifierService.getVerifyResult(transactionId, responseCode);
        VerifyResponse response = new VerifyResponse(transactionId, verifyResult);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: This API should be removed once the transition is completed
    @Deprecated
    @LogAPI(ApiId.VP007)
    @GetMapping(path = "result", produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("hasAuthority('dwverifieroid4vp301i')")
    public ResponseEntity<Response> getResultByGet(
        @RequestParam(name = "transaction_id", required = true) String transactionId,
        @RequestParam(name = "response_code", required = false) String responseCode
    ) throws SQLException
    {
        LogUtils.addRequestID(transactionId);
        VerifyResult verifyResult = verifierService.getVerifyResult(transactionId, responseCode);
        VerifyResponse response = new VerifyResponse(transactionId, verifyResult);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @LogAPI(ApiId.VP008)
    @GetMapping(path = "verify-response", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getVerifyResponse(@PrivacyInfo(keyName = {"vp_token", "custom_data"}) @RequestParam MultiValueMap<String, String> paramMap) throws SQLException {
        OidvpAuthorizationResponse authorizationResponse;
        try {
            authorizationResponse = OidvpAuthorizationResponse.parse(paramMap);
        } catch (Exception e) {
            throw new InvalidParameterException(e.getMessage());
        }
        // check session
        SessionJpa session = sessionDao.getSessionByStateNotUsed(authorizationResponse.getState());
        LogUtils.addRequestID(session.getTransactionId());

        String responseCode = UUID.randomUUID().toString();

        VerifyResult verifyResult = verifierService.verify(session, authorizationResponse, responseCode);

        VerifyResponse response = new VerifyResponse(session.getTransactionId(), verifyResult);
        return new ResponseEntity<>(response, verifyResult.getErrorCode().getHttpStatus());
    }

    //    @LogAPI(ApiId.VP010)
//    @PostMapping(path = "pd/modify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> modifyPresentationDefinitionData(@RequestBody PresentationDataRequest request) throws SQLException {
        Response response = verifierService.modifyPresentationDefinitionData(request);
        return ResponseEntity.ok(response);
    }
}
