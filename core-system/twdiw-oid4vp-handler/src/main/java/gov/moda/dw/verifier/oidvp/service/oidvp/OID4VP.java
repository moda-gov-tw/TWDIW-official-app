package gov.moda.dw.verifier.oidvp.service.oidvp;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.id.ClientID;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.AuthorizationRequestParam;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpAuthorizationRequest;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpClientIdentifier;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpResponseMode;
import gov.moda.dw.verifier.oidvp.model.oid4vp.PresentationDefinitionParameter;
import gov.moda.dw.verifier.oidvp.model.oid4vp.RequestObjectOpts;
import gov.moda.dw.verifier.oidvp.service.oidvp.clientID.OidvpClientIdProvider;
import gov.moda.dw.verifier.oidvp.service.oidvp.clientID.OidvpClientIdProviderFactory;
import gov.moda.dw.verifier.oidvp.util.DIDUtils;
import gov.moda.dw.verifier.oidvp.util.JWKUtils;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class OID4VP {

    public static final JOSEObjectType REQUEST_OBJECT_TYP = new JOSEObjectType("oauth-authz-req+jwt");
    public static final Set<ClientIdScheme> MUST_BE_SIGNED_SCHEMES;

    static {
        MUST_BE_SIGNED_SCHEMES = new HashSet<>();
        MUST_BE_SIGNED_SCHEMES.add(ClientIdScheme.DID);
        MUST_BE_SIGNED_SCHEMES.add(ClientIdScheme.VERIFIER_ATTESTATION);
    }

    private final OidvpClientIdProviderFactory oidvpClientIdProviderFactory;

    public OID4VP(OidvpClientIdProviderFactory oidvpClientIdProviderFactory) {
        this.oidvpClientIdProviderFactory = oidvpClientIdProviderFactory;
    }


    public OidvpAuthorizationRequest getAuthorizationRequest(ClientIdScheme clientIdScheme, AuthorizationRequestParam requestParam) {
        OidvpClientIdentifier oidvpClientIdentifier = specifyClientIdentifier(clientIdScheme, requestParam);
        return createAuthorizationRequest(oidvpClientIdentifier, requestParam);
    }

    public JWT getRequestObject(ClientIdScheme clientIdScheme, AuthorizationRequestParam requestParam, RequestObjectOpts requestObjectOpts) throws OidvpException {
        OidvpAuthorizationRequest authorizationRequest = getAuthorizationRequest(clientIdScheme, requestParam);
        return createRequestObject(authorizationRequest, requestObjectOpts, OID4VP.resolveRequestObjectType(clientIdScheme, false));
    }

    public JWT getRequestObject(String oidvpClientId, AuthorizationRequestParam requestParam, RequestObjectOpts requestObjectOpts) throws OidvpException {
        OidvpClientIdentifier oidvpClientIdentifier = OidvpClientIdentifier.parse(oidvpClientId);
        OidvpAuthorizationRequest authorizationRequest = createAuthorizationRequest(oidvpClientIdentifier, requestParam);
        return createRequestObject(authorizationRequest, requestObjectOpts, OID4VP.resolveRequestObjectType(oidvpClientIdentifier.getClientIdScheme(), false));
    }

    public JWT getRequestObject(OidvpAuthorizationRequest oidvpAuthorizationRequest, RequestObjectOpts requestObjectOpts) throws OidvpException {
        OidvpClientIdentifier oidvpClientIdentifier = OidvpClientIdentifier.parse(oidvpAuthorizationRequest.getClientID().getValue());
        return createRequestObject(oidvpAuthorizationRequest, requestObjectOpts, OID4VP.resolveRequestObjectType(oidvpClientIdentifier.getClientIdScheme(), false));
    }

    public static RequestObjectType resolveRequestObjectType(ClientIdScheme clientIdScheme, boolean encrypted) {
        return resolveRequestObjectType(clientIdScheme, true, encrypted);
    }

    public static RequestObjectType resolveRequestObjectType(ClientIdScheme clientIdScheme, boolean signed, boolean encrypted) {
        if (isAuthorizationRequestMustBeSigned(clientIdScheme) || (signed && isAuthorizationRequestCanBeSigned(clientIdScheme))) {
            return encrypted ? RequestObjectType.SINGED_AND_ENCRYPTED : RequestObjectType.SINGED;
        } else {
            return encrypted ? RequestObjectType.ENCRYPTED : RequestObjectType.PLAIN;
        }
    }

    public static boolean isAuthorizationRequestMustBeSigned(ClientIdScheme clientIdScheme) {
        return MUST_BE_SIGNED_SCHEMES.contains(clientIdScheme);
    }

    public static boolean isAuthorizationRequestCanBeSigned(ClientIdScheme clientIdScheme) {
        return ClientIdScheme.REDIRECT_URI != clientIdScheme;
    }

    public static boolean isUsingResponseURI(ResponseMode rm) {
        return OidvpResponseMode.DIRECT_POST.equals(rm) || OidvpResponseMode.DIRECT_POST_JWT.equals(rm);
    }

    private OidvpAuthorizationRequest createAuthorizationRequest(OidvpClientIdentifier oidvpClientIdentifier, AuthorizationRequestParam requestParam) {
        if (requestParam == null) {
            throw new IllegalArgumentException("AuthorizationRequestParam must not be null");
        }
        if (oidvpClientIdentifier == null) {
            throw new IllegalArgumentException("oidvpClientIdentifier must not be specified");
        }

        ClientID clientID = oidvpClientIdentifier.toOidvpClientID();
        OidvpAuthorizationRequest.Builder builder;
        if (requestParam.specifiesRequestObject()) {
            if (requestParam.getRequestURI() != null) {
                builder = new OidvpAuthorizationRequest.Builder(requestParam.getRequestURI(), clientID);
            } else {
                if (!isCurrentClientIdMatchedRequestObjectClientId(clientID, requestParam.getRequestObject())) {
                    throw new BadOidvpParamException(OidvpError.OIDVP_CLIENT_ID_NOT_MATCH_ERROR, "create authorization request error: The client_id does not match request object's client_id, the client_id might have been changed.");
                }
                builder = new OidvpAuthorizationRequest.Builder(requestParam.getRequestObject(), clientID);
            }
            return builder.endpointURI(requestParam.getAuthorizationEndpoint()).build();
        } else {
            ResponseType responseType = requestParam.getResponseType4VP();
            if (responseType == null) {
                throw new BadOidvpParamException("'responseType' must be specified");
            }
            builder = new OidvpAuthorizationRequest.Builder(responseType, clientID, requestParam.getNonce());
        }

        if (oidvpClientIdentifier.getClientIdScheme() == ClientIdScheme.REDIRECT_URI && !oidvpClientIdentifier.getOriginalClientID().getValue().equals(requestParam.getRedirectOrResponseURI().toString())) {
            throw new BadOidvpParamException("redirect_uri/response_uri must be the same with orig_client_id when client_id_scheme is 'redirect_uri'.");
        }
        builder.responseOrRedirectURI(requestParam.getRedirectOrResponseURI());

        PresentationDefinitionParameter pdp = requestParam.getPresentationDefinitionParameter();
        if (pdp != null) {
            if (pdp.getPresentationDefinition() != null) {
                builder.presentationDefinition(pdp.getPresentationDefinition());
            } else if (pdp.getPresentationDefinitionUri() != null) {
                builder.presentationDefinitionURI(pdp.getPresentationDefinitionUri());
            } else {
                throw new BadOidvpParamException("presentation_definition or presentation_definition_uri must be specified");
            }
        }

        return builder.endpointURI(requestParam.getAuthorizationEndpoint())
                      .responseMode(requestParam.getResponseMode4VP())
                      .state(requestParam.getState())
                      .scope(requestParam.getScope())
                      .clientMetadata(requestParam.getClientMetadata())
                      .transactionData(requestParam.getTransactionData())
                      .idTokenType(requestParam.getIdTokenType())
                      .build();
    }

    private JWT createRequestObject(OidvpAuthorizationRequest authorizationRequest, RequestObjectOpts requestObjectOpts, RequestObjectType requestObjectType) throws OidvpException {
        if (requestObjectOpts == null) {
            throw new BadOidvpParamException(" must not be null");
        }
        if (authorizationRequest == null) {
            throw new BadOidvpParamException("authorizationRequest must not be null");
        }
        if (requestObjectType == null) {
            throw new BadOidvpParamException("requestObjectType must not be specified");
        }

        OidvpClientIdentifier oidvpClientIdentifier = OidvpClientIdentifier.parse(authorizationRequest.getClientID().getValue());

        String iss = oidvpClientIdentifier.getOriginalClientID().getValue();
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder(authorizationRequest.toJWTClaimsSet())
            .issuer(iss)
            .audience(iss);

        if (requestObjectOpts.getExpiredTime() != null) {
            if (requestObjectOpts.getExpiredTime().after(Timestamp.from(Instant.now()))) {
                builder.expirationTime(requestObjectOpts.getExpiredTime());
            } else {
                throw new IllegalArgumentException("Bad expiredTime value, expiredTime is before current time.");
            }
        }
        JWTClaimsSet claimsSet = builder.build();

        checkRequestObjectType(requestObjectType, oidvpClientIdentifier.getClientIdScheme());
        JWT requestObjectJWT;
        switch (requestObjectType) {
            case PLAIN -> {
                PlainHeader header = new PlainHeader.Builder().type(REQUEST_OBJECT_TYP).build();
                requestObjectJWT = new PlainJWT(header, claimsSet);
            }
            case SINGED -> {
                JWK jwk = getActiveSigningJWK(oidvpClientIdentifier);
                try {
                    JWSAlgorithm algorithm = JWKUtils.getDefaultJWSAlgorithm(jwk);
                    JWSHeader header = new JWSHeader.Builder(algorithm)
                        .type(REQUEST_OBJECT_TYP)
                        .keyID(jwk.getKeyID())
                        .build();

                    SignedJWT jws = new SignedJWT(header, claimsSet);
                    jws.sign(JWKUtils.getJWSSigner(jwk, algorithm));
                    requestObjectJWT = jws;
                } catch (Exception e) {
                    throw new OidvpException(OidvpError.SIGN_REQUEST_OBJECT_ERROR, e);
                }
            }
            default -> throw new UnsupportedOperationException("unsupported requestObjectType: " + requestObjectType);
        }

        return requestObjectJWT;
    }

    private OidvpClientIdentifier specifyClientIdentifier(ClientIdScheme clientIdScheme, AuthorizationRequestParam requestParam) {
        if (requestParam == null) {
            throw new IllegalArgumentException("AuthorizationRequestParam must not be null");
        }
        OidvpClientIdProvider oidvpClientIdProvider = oidvpClientIdProviderFactory.createClientIdProvider(clientIdScheme, requestParam.getResponseMode4VP());
        return new OidvpClientIdentifier(oidvpClientIdProvider);
    }

    private boolean isCurrentClientIdMatchedRequestObjectClientId(ClientID currentClientId, JWT requestObject) {
        try {
            String requestObjectClientId = (String) requestObject.getJWTClaimsSet().getClaim("client_id");
            return currentClientId.getValue().equals(requestObjectClientId);
        } catch (Exception e) {
            throw new OidvpRuntimeException(OidvpError.BAD_OIDVP_PARAM, "parse request object error", e);
        }
    }

    private JWK getActiveSigningJWK(OidvpClientIdentifier oidvpClientIdentifier) {
        ClientIdScheme clientIdScheme = oidvpClientIdentifier.getClientIdScheme();
        JWK currentSigningJWK = oidvpClientIdProviderFactory.createClientIdProvider(clientIdScheme, null).getSigningJWK();
        if (currentSigningJWK == null) {
            throw new BadOidvpParamException("signed request object error: get signing jwk error, jwk must not be null.");
        }

        boolean matched;
        if (clientIdScheme == ClientIdScheme.DID) {
            try {
                JWK publicKeyFromDID = DIDUtils.extractPublicKeyFromDID(oidvpClientIdentifier.getOriginalClientID().getValue());
                matched = publicKeyFromDID.getRequiredParams().equals(currentSigningJWK.getRequiredParams());
            } catch (Exception e) {
                throw new OidvpRuntimeException(OidvpError.PARSE_DID_ERROR, e.getMessage(), e);
            }
        } else {
            // TODO: other clientIdScheme's signing key check
            throw new UnsupportedOperationException();
        }

        if (!matched) {
            throw new BadOidvpParamException(OidvpError.OIDVP_CLIENT_ID_NOT_MATCH_ERROR, "create request object error: client_id not match signing jwk, the client_id or signing jwk might have been changed.");
        }
        return currentSigningJWK;
    }

    private void checkRequestObjectType(RequestObjectType requestObjectType, ClientIdScheme clientIdScheme) {
        if (RequestObjectType.PLAIN == requestObjectType) {
            if (isAuthorizationRequestMustBeSigned(clientIdScheme)) {
                throw new BadOidvpParamException("request must be singed when using client_id_scheme:" + clientIdScheme + ", but the request object type is set to " + requestObjectType);
            }
        } else if (RequestObjectType.SINGED == requestObjectType || RequestObjectType.SINGED_AND_ENCRYPTED == requestObjectType) {
            if (!isAuthorizationRequestCanBeSigned(clientIdScheme)) {
                throw new BadOidvpParamException("request must not be singed when using client_id_scheme:" + clientIdScheme + ", but the request object type is set to " + requestObjectType);
            }
        }
    }

    public enum RequestObjectType {
        PLAIN,
        SINGED,
        ENCRYPTED,
        SINGED_AND_ENCRYPTED
    }
}
