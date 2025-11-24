package gov.moda.dw.verifier.oidvp.service.oidvp;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.Nonce;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.dao.PresentationDefinitionDao;
import gov.moda.dw.verifier.oidvp.dao.SessionDao;
import gov.moda.dw.verifier.oidvp.domain.SessionJpa;
import gov.moda.dw.verifier.oidvp.model.AuthorizationRequestParam;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationEvaluationException;
import gov.moda.dw.verifier.oidvp.model.oid4vp.AuthorizationRequestOpts;
import gov.moda.dw.verifier.oidvp.model.oid4vp.AuthorizationRequestOpts.JARType;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpAuthorizationRequest;
import gov.moda.dw.verifier.oidvp.model.oid4vp.PresentationDefinitionParameter;
import gov.moda.dw.verifier.oidvp.model.oid4vp.RequestObjectOpts;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationPOJOUtils;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationSchemaUtils;
import gov.moda.dw.verifier.oidvp.presentationExchange.PresentationSchemaUtils.PDSchemaResult;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.service.metadata.MetadataService;
import gov.moda.dw.verifier.oidvp.service.vp.VerifierBusinessService;
import java.net.URI;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OID4VPService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OID4VPService.class);
    public static final int DEFAULT_BYTE_LEN = 32;

    private final OID4VP oid4VP;
    private final MetadataService metadataService;
    private final SessionDao sessionDao;
    private final PresentationDefinitionDao presentationDefinitionDao;
    private final PresentationSchemaUtils presentationSchemaUtils;
    private final VerifierBusinessService verifierBusinessService;

    private final URI endpointURI;
    private final URI redirectUri;
    private final URI responseUri;
    private final URI requestUri;
    private final String issuer;
    private final int sessionExpiredTime;

    public OID4VPService(OidvpConfig oidvpConfig, OID4VP oid4VP, MetadataService metadataService, SessionDao sessionDao,
        PresentationDefinitionDao presentationDefinitionDao, PresentationSchemaUtils presentationSchemaUtils, VerifierBusinessService verifierBusinessService)
    {
        this.oid4VP = oid4VP;
        this.metadataService = metadataService;
        this.sessionDao = sessionDao;
        this.presentationDefinitionDao = presentationDefinitionDao;
        this.presentationSchemaUtils = presentationSchemaUtils;
        this.verifierBusinessService = verifierBusinessService;

        endpointURI = oidvpConfig.getEndpointUri();
        redirectUri = oidvpConfig.getRedirectUri();
        responseUri = oidvpConfig.getResponseUri();
        requestUri = oidvpConfig.getRequestUri();
        issuer = oidvpConfig.getIssuer();
        sessionExpiredTime = oidvpConfig.getSessionExpiredTime();
    }


    public OidvpAuthorizationRequest getAuthorizationRequest(String transactionId, ClientIdScheme clientIdScheme, String ref, AuthorizationRequestOpts opts) throws SQLException, OidvpException {
        return getAuthorizationRequest(transactionId, clientIdScheme, ref, JARType.NONE, opts);
    }

    public OidvpAuthorizationRequest getAuthorizationRequestUsingRequestObject(String transactionId, ClientIdScheme clientIdScheme, String ref, JARType type, AuthorizationRequestOpts opts) throws SQLException, OidvpException {
        return getAuthorizationRequest(transactionId, clientIdScheme, ref, type, opts);
    }

    public JWT getRequestObject(String clientId, String nonce, String state, String pd, AuthorizationRequestOpts opts) throws OidvpException {
        if (clientId == null || clientId.isEmpty()) {
            throw new BadOidvpParamException("'clientId' must not be null");
        }
        if (nonce == null || state == null) {
            throw new BadOidvpParamException("'nonce' or 'state' must not be null");
        }
        if (pd == null || pd.isEmpty()) {
            throw new BadOidvpParamException("presentation_definition pd must not be empty");
        }
        if (opts == null) {
            throw new InvalidParameterException("AuthorizationRequestOpts must be specified");
        }

        if (opts.getExpiredTime() == null) {
            throw new BadOidvpParamException("'expiredTime' in AuthorizationRequestOpts must be specified when creating request object.");
        }
        PresentationDefinition presentationDefinition = PresentationPOJOUtils.getPresentationDefinition(pd);
        return getRequestObject(clientId, new Nonce(nonce), new State(state), presentationDefinition, opts);
    }

    private OidvpAuthorizationRequest getAuthorizationRequest(String transactionId, ClientIdScheme clientIdScheme, String ref,
        JARType jarType, AuthorizationRequestOpts opts) throws SQLException, OidvpException
    {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new InvalidParameterException("transactionId is empty");
        }
        if (ref == null) {
            throw new InvalidParameterException("ref must not be null");
        }
        if (jarType == null) {
            throw new BadOidvpParamException("jarType must be specified.");
        }
        if (opts == null) {
            throw new InvalidParameterException("AuthorizationRequestOpts must be specified");
        }
        if (opts.getCallbackUri() != null && !verifierBusinessService.validateCallbackURI(opts.getCallbackUri())) {
            throw new InvalidParameterException("callback URI is invalid.");
        }

        PresentationDefinitionDTO definitionDTO = getPresentationDefinitionFromVpItem(ref);
        PresentationDefinition pd = definitionDTO.pd();
        Nonce nonce = new Nonce(DEFAULT_BYTE_LEN);
        State state = new State(DEFAULT_BYTE_LEN);

        LocalDateTime expiredTime;
        if (opts.getExpiredTime() == null) {
            expiredTime = LocalDateTime.now().plusMinutes(sessionExpiredTime);
            opts.setExpiredTime(expiredTime);
        } else {
            if (opts.getExpiredTime().isBefore(LocalDateTime.now())) {
                throw new BadOidvpParamException("expiredTime for AuthorizationRequest must not before current time.");
            }
            expiredTime = opts.getExpiredTime();
        }
        OidvpAuthorizationRequest authorizationRequest =
            switch (jarType) {
                case NONE -> getAuthorizationRequest(clientIdScheme, nonce, state, pd, opts);
                case REFERENCE -> {
                    URI requestURI = getRequestUri(requestUri, state);
                    yield getAuthorizationRequestUsingRequestURI(clientIdScheme, requestURI, opts);
                }
                case VALUE -> {
                    JWT requestObject = getRequestObject(clientIdScheme, nonce, state, pd, opts);
                    yield getAuthorizationRequestUsingRequest(clientIdScheme, requestObject, opts);
                }
            };

        // create request session
        String clientId = authorizationRequest.getClientID().getValue();
        createSession(transactionId, clientId, nonce.getValue(), state.getValue(), ref, definitionDTO.pdString(), opts, expiredTime);
        LOGGER.info("create session success");
        return authorizationRequest;
    }

    private OidvpAuthorizationRequest getAuthorizationRequest(ClientIdScheme clientIdScheme, Nonce nonce, State state, PresentationDefinition pd, AuthorizationRequestOpts opts) throws OidvpException {
        LOGGER.info("build AuthorizationRequest...client_id_scheme=[{}]", clientIdScheme.getValue());
        VerifierMetadata metadata = getMetadata();
        AuthorizationRequestParam requestParam = buildRequestParam(nonce, state, pd, metadata, opts);
        return oid4VP.getAuthorizationRequest(clientIdScheme, requestParam);
    }

    private OidvpAuthorizationRequest getAuthorizationRequestUsingRequestURI(ClientIdScheme clientIdScheme, URI requestURI, AuthorizationRequestOpts opts) {
        LOGGER.info("build AuthorizationRequest using request_uri...client_id_scheme=[{}]", clientIdScheme.getValue());
        AuthorizationRequestParam requestParam = buildRequestParamForJAR(null, requestURI, opts);
        return oid4VP.getAuthorizationRequest(clientIdScheme, requestParam);
    }

    private OidvpAuthorizationRequest getAuthorizationRequestUsingRequest(ClientIdScheme clientIdScheme, JWT requestObject, AuthorizationRequestOpts opts) {
        LOGGER.info("build AuthorizationRequest using request...client_id_scheme=[{}]", clientIdScheme.getValue());
        AuthorizationRequestParam requestParam = buildRequestParamForJAR(requestObject, null, opts);
        return oid4VP.getAuthorizationRequest(clientIdScheme, requestParam);
    }

    private JWT getRequestObject(String clientId, Nonce nonce, State state, PresentationDefinition pd, AuthorizationRequestOpts opts) throws OidvpException {
        LOGGER.info("create request object...oidvp client_id=[{}]", clientId);
        VerifierMetadata metadata = getMetadata();
        AuthorizationRequestParam requestParam = buildRequestParam(nonce, state, pd, metadata, opts);
        RequestObjectOpts requestObjectOpts = buildRequestObjectOpts(issuer, opts);
        return oid4VP.getRequestObject(clientId, requestParam, requestObjectOpts);
    }

    private JWT getRequestObject(ClientIdScheme clientIdScheme, Nonce nonce, State state, PresentationDefinition pd, AuthorizationRequestOpts opts) throws OidvpException {
        LOGGER.info("create request object...client_id_scheme=[{}]", clientIdScheme);
        VerifierMetadata metadata = getMetadata();
        AuthorizationRequestParam requestParam = buildRequestParam(nonce, state, pd, metadata, opts);
        RequestObjectOpts requestObjectOpts = buildRequestObjectOpts(issuer, opts);
        return oid4VP.getRequestObject(clientIdScheme, requestParam, requestObjectOpts);
    }

    private RequestObjectOpts buildRequestObjectOpts(String issuer, AuthorizationRequestOpts opts) {
        RequestObjectOpts requestObjectOpts = new RequestObjectOpts();
        if (opts.getExpiredTime() != null) {
            requestObjectOpts.setExpiredTime(Timestamp.valueOf(opts.getExpiredTime()));
        }
        return requestObjectOpts;
    }

    private AuthorizationRequestParam buildRequestParam(Nonce nonce, State state, PresentationDefinition pd, VerifierMetadata verifierMetadata, AuthorizationRequestOpts opts) {
        PresentationDefinitionParameter pdp = new PresentationDefinitionParameter(pd);
        ResponseMode rm = opts.getResponseMode();
        ResponseType rt = opts.getResponseType();
        return new AuthorizationRequestParam(endpointURI, rt, nonce)
            .setRedirectOrResponseURI(specifyRedirectOrResponseUri(rm))
            .setState(state)
            .setResponseMode4VP(rm)
            .setPresentationDefinitionParameter(pdp)
            .setClientMetadata(verifierMetadata);
    }

    private AuthorizationRequestParam buildRequestParamForJAR(JWT requestObject, URI requestURI, AuthorizationRequestOpts opts) {
        AuthorizationRequestParam requestParam;
        if (requestObject != null) {
            requestParam = new AuthorizationRequestParam(endpointURI, requestObject);
        } else if (requestURI != null) {
            requestParam = new AuthorizationRequestParam(endpointURI, requestURI);
        } else {
            throw new BadOidvpParamException("requestObject or requestURI must be specified");
        }
        requestParam.setResponseMode4VP(opts.getResponseMode());
        return requestParam;
    }

    private VerifierMetadata getMetadata() throws OidvpException {
        try {
            return metadataService.getVerifierMetadata();
        } catch (SQLException e) {
            throw new OidvpException(OidvpError.GET_METADATA_ERROR, "get metadata error: " + e.getMessage(), e);
        }
    }

    private PresentationDefinitionDTO getPresentationDefinitionFromVpItem(String ref) throws SQLException, PresentationEvaluationException {
        String presentationDefinition = presentationDefinitionDao.getPresentationDefinition(ref);
        PDSchemaResult result = presentationSchemaUtils.isValidPresentationDefinitionSchema(presentationDefinition);
        if (result.isValid()) {
            return new PresentationDefinitionDTO(presentationDefinition, result.pd());
        } else {
            LOGGER.error("invalid presentation definition: {}", result.message());
            throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION);
        }
    }

    private URI getRequestUri(URI baseRequestUri, State state) {
        if (baseRequestUri == null) {
            throw new BadOidvpParamException("can not build request_uri, request_uri must not be null");
        }
        return UriComponentsBuilder.fromUri(baseRequestUri)
                                   .path(state.getValue())
                                   .build()
                                   .toUri();
    }

    private URI specifyRedirectOrResponseUri(ResponseMode rm) {
        return OID4VP.isUsingResponseURI(rm) ? responseUri : redirectUri;
    }

    private void createSession(String transactionId, String clientID, String nonce, String state, String ref,
        String pdString, AuthorizationRequestOpts opts, LocalDateTime expiredTime) throws SQLException
    {
        if (transactionId.length() > 50) {
            throw new InvalidParameterException("invalid transaction_id length");
        }
        if (clientID.length() > 500) {
            throw new IllegalArgumentException("invalid client_id length");
        }
        if (nonce.length() > 64 || state.length() > 64) {
            throw new IllegalArgumentException("invalid nonce/state length");
        }
        if (ref.length() > 100) {
            throw new InvalidParameterException("invalid ref length");
        }

        SessionJpa sessionJpa = new SessionJpa();
        sessionJpa.setTransactionId(transactionId);
        sessionJpa.setClientId(clientID);
        sessionJpa.setNonce(nonce);
        sessionJpa.setState(state);
        sessionJpa.setRef(ref);
        sessionJpa.setPresentationDefinition(pdString);
        sessionJpa.setResponseMode(opts.getResponseMode());
        sessionJpa.setResponseType(opts.getResponseType());
        sessionJpa.setExpiredTime(expiredTime);
        if (opts.getCallbackUri() != null) {
            sessionJpa.setCallback(opts.getCallbackUri());
        }

        sessionDao.createSession(sessionJpa);
    }

    private record PresentationDefinitionDTO(String pdString, PresentationDefinition pd) {}
}
