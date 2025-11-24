package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.dpop.JWKThumbprintConfirmation;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.rar.AuthorizationDetail;
import com.nimbusds.openid.connect.sdk.ClaimsRequest;
import com.nimbusds.openid.connect.sdk.Display;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCClaimsRequest;
import com.nimbusds.openid.connect.sdk.Prompt;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.service.oidvp.OID4VP;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OidvpAuthorizationRequest extends AuthorizationRequest {

    public static final int PURPOSE_MIN_LENGTH = 3;
    public static final int PURPOSE_MAX_LENGTH = 300;

    // oidc4vp new request parameters
    /**
     * the presentation_definition (optional)
     */
    private final PresentationDefinition presentationDefinition;

    /**
     * the presentation_definition_uri (optional)
     */
    private final URI presentationDefinitionURI;

    /**
     * the client_metadata (optional)
     */
    private final VerifierMetadata clientMetadata;

    /**
     * the response_uri (optional)
     */
    private final URI responseURI;

    /**
     * the request_uri_method (optional)
     */
    private final RequestUriMethod requestUriMethod;

    /**
     * the transaction_data (optional)
     */
    private final List<TransactionData> transactionData;

    // siopv2

    /**
     * the id_token_type (optional)
     */
    private final IdTokenType idTokenType;

    // existing oidc parameters

    /**
     * The nonce (required).
     */
    private final Nonce nonce;


    /**
     * The requested display type (optional).
     */
    private final Display display;


    /**
     * The required maximum authentication age, in seconds, -1 if not specified, zero implies prompt=login (optional).
     */
    private final int maxAge;


    /**
     * The end-user's preferred languages and scripts for the user interface (optional).
     */
    private final List<LangTag> uiLocales;


    /**
     * The end-user's preferred languages and scripts for claims being returned (optional).
     */
    private final List<LangTag> claimsLocales;


    /**
     * Previously issued ID Token passed to the authorisation server as a hint about the end-user's current or past
     * authenticated session with the client (optional). Should be present when {@code prompt=none} is used.
     */
    private final JWT idTokenHint;


    /**
     * Hint to the authorisation server about the login identifier the end-user may use to log in (optional).
     */
    private final String loginHint;


    /**
     * Requested Authentication Context Class Reference values (optional).
     */
    private final List<ACR> acrValues;


    /**
     * Individual claims to be returned (optional).
     */
    private final OIDCClaimsRequest claims;


    /**
     * The transaction specific purpose, for use in OpenID Connect Identity Assurance (optional).
     */
    private final String purpose;


    private OidvpAuthorizationRequest(URI uri,
        ResponseType rt,
        ResponseMode rm,
        Scope scope,
        ClientID clientID,
        URI redirectURI,
        State state,
        Nonce nonce,
        Display display,
        Prompt prompt,
        JWKThumbprintConfirmation dpopJKT,
        TrustChain trustChain,
        int maxAge,
        List<LangTag> uiLocales,
        List<LangTag> claimsLocales,
        JWT idTokenHint,
        String loginHint,
        List<ACR> acrValues,
        OIDCClaimsRequest claims,
        String purpose,
        JWT requestObject,
        URI requestURI,
        CodeChallenge codeChallenge,
        CodeChallengeMethod codeChallengeMethod,
        List<AuthorizationDetail> authorizationDetails,
        List<URI> resources,
        boolean includeGrantedScopes,
        Map<String, List<String>> customParams,
        PresentationDefinition presentationDefinition,
        URI presentationDefinitionURI,
        VerifierMetadata clientMetadata,
        URI responseURI,
        RequestUriMethod requestUriMethod,
        List<TransactionData> transactionData,
        IdTokenType idTokenType)
    {
        super(uri, rt, rm, clientID, redirectURI, scope, state, codeChallenge, codeChallengeMethod, authorizationDetails,
            resources, includeGrantedScopes, requestObject, requestURI, prompt, dpopJKT, trustChain, customParams);

        if (!specifiesRequestObject()) {

            if (nonce == null) {
                throw new IllegalArgumentException("nonce must not be null");
            }

            if (redirectURI == null && responseURI == null) {
                throw new IllegalArgumentException("The redirect_uri or response_uri must be specified");
            }

            if (redirectURI != null && responseURI != null) {
                throw new IllegalArgumentException("The redirect_uri and response_uri can not present at the same time");
            }

            if (OidvpResponseMode.DIRECT_POST.equals(rm) && responseURI == null) {
                throw new IllegalArgumentException("response_uri must be present when the response_mode is direct_post");
            }

            if (presentationDefinition == null && presentationDefinitionURI == null && scope == null) {
                throw new IllegalArgumentException("presentation_definition or presentation_definition_uri or scope must be specified");
            }

            if (presentationDefinition != null && presentationDefinitionURI != null) {
                throw new IllegalArgumentException("presentation_definition and presentation_definition_uri can not present at the same time");
            }

            if (requestUriMethod != null) {
                throw new IllegalArgumentException("request_uri_method parameter must not be present if a request_uri parameter is not present.");
            }
        }

        this.nonce = nonce;
        this.responseURI = responseURI;
        this.presentationDefinition = presentationDefinition;
        this.presentationDefinitionURI = presentationDefinitionURI;
        this.requestUriMethod = requestUriMethod;
        this.idTokenType = idTokenType;

        this.clientMetadata = clientMetadata;
        if (transactionData != null) {
            this.transactionData = Collections.unmodifiableList(transactionData);
        } else {
            this.transactionData = null;
        }

        this.display = display;
        this.maxAge = maxAge;
        if (uiLocales != null) {
            this.uiLocales = Collections.unmodifiableList(uiLocales);
        } else {
            this.uiLocales = null;
        }

        if (claimsLocales != null) {
            this.claimsLocales = Collections.unmodifiableList(claimsLocales);
        } else {
            this.claimsLocales = null;
        }
        this.idTokenHint = idTokenHint;
        this.loginHint = loginHint;
        if (acrValues != null) {
            this.acrValues = Collections.unmodifiableList(acrValues);
        } else {
            this.acrValues = null;
        }
        this.claims = claims;

        if (purpose != null) {
            if (purpose.length() < PURPOSE_MIN_LENGTH) {
                throw new IllegalArgumentException(
                    "The purpose must not be shorter than " + PURPOSE_MIN_LENGTH + " characters");
            }
            if (purpose.length() > PURPOSE_MAX_LENGTH) {
                throw new IllegalArgumentException(
                    "The purpose must not be longer than " + PURPOSE_MAX_LENGTH + " characters");
            }
        }
        this.purpose = purpose;
    }

    public static class Builder {


        /**
         * The endpoint URI (optional).
         */
        private URI uri;


        /**
         * The response type (required unless in JAR).
         */
        private ResponseType rt;


        /**
         * The client identifier (required unless in JAR).
         */
        private final ClientID clientID;

        /**
         * The scope (required unless in JAR).
         */
        private Scope scope;


        /**
         * The opaque value to maintain state between the request and the callback (recommended).
         */
        private State state;


        /**
         * The nonce (required for implicit flow (unless in JAR), optional for code flow).
         */
        private Nonce nonce;


        /**
         * The requested display type (optional).
         */
        private Display display;


        /**
         * The requested prompt (optional).
         */
        private Prompt prompt;


        /**
         * The DPoP JWK SHA-256 thumbprint (optional).
         */
        private JWKThumbprintConfirmation dpopJKT;


        /**
         * The OpenID Connect Federation 1.0 trust chain (optional).
         */
        private TrustChain trustChain;


        /**
         * The required maximum authentication age, in seconds, -1 if not specified, zero implies prompt=login
         * (optional).
         */
        private int maxAge = -1;


        /**
         * The end-user's preferred languages and scripts for the user interface (optional).
         */
        private List<LangTag> uiLocales;


        /**
         * The end-user's preferred languages and scripts for claims being returned (optional).
         */
        private List<LangTag> claimsLocales;


        /**
         * Previously issued ID Token passed to the authorisation server as a hint about the end-user's current or past
         * authenticated session with the client (optional). Should be present when {@code prompt=none} is used.
         */
        private JWT idTokenHint;


        /**
         * Hint to the authorisation server about the login identifier the end-user may use to log in (optional).
         */
        private String loginHint;


        /**
         * Requested Authentication Context Class Reference values (optional).
         */
        private List<ACR> acrValues;


        /**
         * Individual claims to be returned (optional).
         */
        private OIDCClaimsRequest claims;


        /**
         * The transaction specific purpose (optional).
         */
        private String purpose;


        /**
         * Request object (optional).
         */
        private JWT requestObject;


        /**
         * Request object URI (optional).
         */
        private URI requestURI;


        /**
         * The response mode (optional).
         */
        private ResponseMode rm;


        /**
         * The authorisation code challenge for PKCE (optional).
         */
        private CodeChallenge codeChallenge;


        /**
         * The authorisation code challenge method for PKCE (optional).
         */
        private CodeChallengeMethod codeChallengeMethod;


        /**
         * The RAR details (optional).
         */
        private List<AuthorizationDetail> authorizationDetails;


        /**
         * The resource URI(s) (optional).
         */
        private List<URI> resources;


        /**
         * Indicates incremental authorisation (optional).
         */
        private boolean includeGrantedScopes;

        // oidvp

        /**
         * the presentation_definition (optional)
         */
        private PresentationDefinition presentationDefinition;

        /**
         * the presentation_definition_uri (optional)
         */
        private URI presentationDefinitionURI;

        /**
         * the client_metadata (optional)
         */
        private VerifierMetadata clientMetadata;

        /**
         * the response_uri / redirect_uri (required unless in JAR)
         */
        private URI responseOrRedirectURI;

        /**
         * the request_uri_method (optional)
         */
        private RequestUriMethod requestUriMethod;

        /**
         * the transaction_data (optional)
         */
        private List<TransactionData> transactionData;

        // siopv2
        /**
         * the id_token_type (optional)
         */
        private IdTokenType idTokenType;


        /**
         * Custom parameters.
         */
        private final Map<String, List<String>> customParams = new HashMap<>();

        /**
         * Creates a new OpenID Connect for Verifiable Presentation authorization request builder.
         *
         * @param rt       The response_type
         * @param clientID The client_id
         * @param nonce    The nonce
         */
        public Builder(ResponseType rt, ClientID clientID, Nonce nonce) {
            if (rt == null) {
                throw new IllegalArgumentException("The response type must not be null");
            }

            this.rt = rt;

            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }

            this.clientID = clientID;

            if (nonce == null) {
                throw new IllegalArgumentException("The nonce must not be null");
            }
            this.nonce = nonce;
            customParameter("nonce", nonce.getValue());
        }

        /**
         * Creates a new OpenID Connect for Verifiable Presentation authorization request (JAR) builder.
         *
         * @param requestURI The request object URI
         * @param clientID   The client_id
         */
        public Builder(URI requestURI, ClientID clientID) {
            if (requestURI == null) {
                throw new IllegalArgumentException("The request URI must not be null");
            }

            this.requestURI = requestURI;

            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }

            this.clientID = clientID;
        }

        /**
         * Creates a new OpenID Connect for Verifiable Presentation authorization request (JAR) builder.
         *
         * @param requestObject The request object
         * @param clientID      The client_id
         */
        public Builder(JWT requestObject, ClientID clientID) {

            if (requestObject == null) {
                throw new IllegalArgumentException("The request object must not be null");
            }

            this.requestObject = requestObject;

            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }

            this.clientID = clientID;
        }


        /**
         * Sets the response type. Corresponds to the {@code response_type} parameter.
         *
         * @param rt The response type. Must not be {@code null}.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder responseType(ResponseType rt) {

            if (rt == null) {
                throw new IllegalArgumentException("The response type must not be null");
            }

            this.rt = rt;
            return this;
        }


        /**
         * Sets the scope. Corresponds to the {@code scope} parameter.
         *
         * @param scope The scope, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder scope(Scope scope) {
            this.scope = scope;
            return this;
        }


        /**
         * Sets the state. Corresponds to the recommended {@code state} parameter.
         *
         * @param state The state, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder state(State state) {
            this.state = state;
            return this;
        }


        /**
         * Sets the URI of the endpoint (HTTP or HTTPS) for which the request is intended.
         *
         * @param uri The endpoint URI, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder endpointURI(URI uri) {
            this.uri = uri;
            return this;
        }


        /**
         * Sets the nonce. Corresponds to the conditionally optional {@code nonce} parameter.
         *
         * @param nonce The nonce, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder nonce(Nonce nonce) {
            this.nonce = nonce;
            return this;
        }


        /**
         * Sets the requested display type. Corresponds to the optional {@code display} parameter.
         *
         * @param display The requested display type, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder display(Display display) {
            this.display = display;
            return this;
        }


        /**
         * Sets the requested prompt. Corresponds to the optional {@code prompt} parameter.
         *
         * @param prompt The requested prompt, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder prompt(Prompt prompt) {
            this.prompt = prompt;
            return this;
        }


        /**
         * Sets the requested prompt. Corresponds to the optional {@code prompt} parameter.
         *
         * @param promptType The requested prompt types, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder prompt(Prompt.Type... promptType) {
            if (promptType.length == 1 && promptType[0] == null) {
                return prompt((Prompt) null);
            } else {
                return prompt(new Prompt(promptType));
            }
        }


        /**
         * Sets the DPoP JWK SHA-256 thumbprint. Corresponds to the optional {@code dpop_jkt} parameter.
         *
         * @param dpopJKT DPoP JWK SHA-256 thumbprint, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder dPoPJWKThumbprintConfirmation(JWKThumbprintConfirmation dpopJKT) {
            this.dpopJKT = dpopJKT;
            return this;
        }


        /**
         * Sets the OpenID Connect Federation 1.0 trust chain. Corresponds to the optional {@code trust_chain}
         * parameter.
         *
         * @param trustChain The trust chain, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder trustChain(TrustChain trustChain) {
            this.trustChain = trustChain;
            return this;
        }


        /**
         * Sets the required maximum authentication age. Corresponds to the optional {@code max_age} parameter.
         *
         * @param maxAge The maximum authentication age, in seconds; 0 if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder maxAge(int maxAge) {
            this.maxAge = maxAge;
            return this;
        }


        /**
         * Sets the end-user's preferred languages and scripts for the user interface, ordered by preference.
         * Corresponds to the optional {@code ui_locales} parameter.
         *
         * @param uiLocales The preferred UI locales, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder uiLocales(List<LangTag> uiLocales) {
            this.uiLocales = uiLocales;
            return this;
        }


        /**
         * Sets the end-user's preferred languages and scripts for the claims being returned, ordered by preference.
         * Corresponds to the optional {@code claims_locales} parameter.
         *
         * @param claimsLocales The preferred claims locales, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder claimsLocales(List<LangTag> claimsLocales) {
            this.claimsLocales = claimsLocales;
            return this;
        }


        /**
         * Sets the ID Token hint. Corresponds to the conditionally optional {@code id_token_hint} parameter.
         *
         * @param idTokenHint The ID Token hint, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder idTokenHint(JWT idTokenHint) {
            this.idTokenHint = idTokenHint;
            return this;
        }


        /**
         * Sets the login hint. Corresponds to the optional {@code login_hint} parameter.
         *
         * @param loginHint The login hint, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder loginHint(String loginHint) {
            this.loginHint = loginHint;
            return this;
        }


        /**
         * Sets the requested Authentication Context Class Reference values. Corresponds to the optional
         * {@code acr_values} parameter.
         *
         * @param acrValues The requested ACR values, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder acrValues(List<ACR> acrValues) {
            this.acrValues = acrValues;
            return this;
        }


        /**
         * Sets the individual claims to be returned. Corresponds to the optional {@code claims} parameter.
         *
         * @param claims The individual claims to be returned, {@code null} if not specified.
         * @return This builder.
         * @see #claims(OIDCClaimsRequest)
         */
        @Deprecated
        public OidvpAuthorizationRequest.Builder claims(ClaimsRequest claims) {

            if (claims == null) {
                this.claims = null;
            } else {
                try {
                    this.claims = OIDCClaimsRequest.parse(claims.toJSONObject());
                } catch (ParseException e) {
                    // Should never happen
                    throw new IllegalArgumentException("Invalid claims: " + e.getMessage(), e);
                }
            }
            return this;
        }


        /**
         * Sets the individual OpenID claims to be returned. Corresponds to the optional {@code claims} parameter.
         *
         * @param claims The individual OpenID claims to be returned, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder claims(OIDCClaimsRequest claims) {
            this.claims = claims;
            return this;
        }


        /**
         * Sets the transaction specific purpose. Corresponds to the optional {@code purpose} parameter.
         *
         * @param purpose The purpose, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }


        /**
         * Sets the request object. Corresponds to the optional {@code request} parameter. Must not be specified
         * together with a request object URI.
         *
         * @param requestObject The request object, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder requestObject(JWT requestObject) {
            this.requestObject = requestObject;
            return this;
        }


        /**
         * Sets the request object URI. Corresponds to the optional {@code request_uri} parameter. Must not be specified
         * together with a request object.
         *
         * @param requestURI The request object URI, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder requestURI(URI requestURI) {
            this.requestURI = requestURI;
            return this;
        }


        /**
         * Sets the response mode. Corresponds to the optional {@code response_mode} parameter. Use of this parameter is
         * not recommended unless a non-default response mode is requested (e.g. form_post).
         *
         * @param rm The response mode, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder responseMode(ResponseMode rm) {
            this.rm = rm;
            return this;
        }


        /**
         * Sets the code challenge for Proof Key for Code Exchange (PKCE) by public OAuth clients.
         *
         * @param codeChallenge       The code challenge, {@code null} if not specified.
         * @param codeChallengeMethod The code challenge method, {@code null} if not specified.
         * @return This builder.
         */
        @Deprecated
        public OidvpAuthorizationRequest.Builder codeChallenge(CodeChallenge codeChallenge, CodeChallengeMethod codeChallengeMethod) {
            this.codeChallenge = codeChallenge;
            this.codeChallengeMethod = codeChallengeMethod;
            return this;
        }


        /**
         * Sets the code challenge for Proof Key for Code Exchange (PKCE) by public OAuth clients.
         *
         * @param codeVerifier        The code verifier to use to compute the code challenge, {@code null} if PKCE is
         *                            not specified.
         * @param codeChallengeMethod The code challenge method, {@code null} if not specified. Defaults to
         *                            {@link CodeChallengeMethod#PLAIN} if a code verifier is specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder codeChallenge(CodeVerifier codeVerifier, CodeChallengeMethod codeChallengeMethod) {
            if (codeVerifier != null) {
                CodeChallengeMethod method =
                    codeChallengeMethod != null ? codeChallengeMethod : CodeChallengeMethod.getDefault();
                codeChallenge = CodeChallenge.compute(method, codeVerifier);
                this.codeChallengeMethod = method;
            } else {
                codeChallenge = null;
                this.codeChallengeMethod = null;
            }
            return this;
        }


        /**
         * Sets the Rich Authorisation Request (RAR) details.
         *
         * @param authorizationDetails The authorisation details, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder authorizationDetails(List<AuthorizationDetail> authorizationDetails) {
            this.authorizationDetails = authorizationDetails;
            return this;
        }


        /**
         * Sets the resource server URI.
         *
         * @param resource The resource URI, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder resource(URI resource) {
            if (resource != null) {
                resources = Collections.singletonList(resource);
            } else {
                resources = null;
            }
            return this;
        }


        /**
         * Sets the resource server URI(s).
         *
         * @param resources The resource URI(s), {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder resources(URI... resources) {
            if (resources != null) {
                this.resources = Arrays.asList(resources);
            } else {
                this.resources = null;
            }
            return this;
        }


        /**
         * Requests incremental authorisation.
         *
         * @param includeGrantedScopes {@code true} to request incremental authorisation.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder includeGrantedScopes(boolean includeGrantedScopes) {
            this.includeGrantedScopes = includeGrantedScopes;
            return this;
        }

        // oidvp

        /**
         * Sets the presentation_definition. Corresponds to the optional {@code presentation_definition} parameter.This
         * parameter MUST be present when presentation_definition_uri parameter is not present.
         *
         * @param presentationDefinition The presentation_definition, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder presentationDefinition(PresentationDefinition presentationDefinition) {
            if (presentationDefinition == null) {
                this.presentationDefinition = null;
            } else {
                this.presentationDefinition = presentationDefinition;
                customParameter("presentation_definition", JsonUtils.toJsonString(presentationDefinition));
            }
            return this;
        }

        /**
         * Sets the presentation_definition_uri. Corresponds to the optional {@code presentation_definition_uri}
         * parameter.This parameter MUST be present when presentation_definition parameter is not present.
         *
         * @param presentationDefinitionURI The presentation_definition_uri, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder presentationDefinitionURI(URI presentationDefinitionURI) {
            if (presentationDefinitionURI == null) {
                this.presentationDefinitionURI = null;
            } else {
                this.presentationDefinitionURI = presentationDefinitionURI;
                customParameter("presentation_definition_uri", presentationDefinitionURI.toString());
            }
            return this;
        }

        /**
         * Sets the client_metadata. Corresponds to the optional {@code client_metadata} parameter.It MUST NOT be
         * present if client_metadata_uri parameter is present.
         *
         * @param clientMetadata The client_metadata, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder clientMetadata(VerifierMetadata clientMetadata) {
            if (clientMetadata == null) {
                this.clientMetadata = null;
            } else {
                this.clientMetadata = clientMetadata;
                customParameter("client_metadata", clientMetadata.toString());
            }
            return this;
        }

        /**
         * Sets the response_uri or redirect_uri. Corresponds to the optional {@code response_uri or redirect_uri}
         * parameter.
         *
         * @param responseOrRedirectURI The response_uri or redirect_uri, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder responseOrRedirectURI(URI responseOrRedirectURI) {
            this.responseOrRedirectURI = responseOrRedirectURI;
            return this;
        }

        /**
         * Sets the request_uri_method. Corresponds to the optional {@code request_uri_method} parameter.
         *
         * @param requestUriMethod The response_uri, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder requestUriMethod(RequestUriMethod requestUriMethod) {
            if (requestUriMethod == null) {
                this.requestUriMethod = null;
            } else {
                this.requestUriMethod = requestUriMethod;
                customParameter("request_uri_method", requestUriMethod.toString());
            }
            return this;
        }

        /**
         * Sets the transaction_data. Corresponds to the optional {@code transaction_data} parameter.
         *
         * @param transactionData The response_uri, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder transactionData(List<TransactionData> transactionData) {
            if (transactionData == null || transactionData.isEmpty()) {
                this.transactionData = null;
            } else {
                this.transactionData = transactionData;
                String[] transactionDataBase64Url = new String[transactionData.size()];
                for (int i = 0; i < transactionData.size(); i++) {
                    String jsonString = JsonUtils.toJsonString(transactionData.get(i));
                    if (jsonString == null) {
                        throw new IllegalArgumentException("transactionData can not convert to json string");
                    }
                    transactionDataBase64Url[i] = Base64URL.encode(jsonString).toString();
                }
                customParameter("transaction_data", transactionDataBase64Url);
            }
            return this;
        }

        /**
         * Sets the id_token_type. Corresponds to the optional {@code id_token_type} parameter.
         *
         * @param idTokenType The response_uri, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder idTokenType(IdTokenType idTokenType) {
            if (idTokenType == null) {
                this.idTokenType = null;
            } else {
                this.idTokenType = idTokenType;
                customParameter("id_token_type", idTokenType.toString());
            }
            return this;
        }

        /**
         * Sets a custom parameter.
         *
         * @param name   The parameter name. Must not be {@code null}.
         * @param values The parameter values, {@code null} if not specified.
         * @return This builder.
         */
        public OidvpAuthorizationRequest.Builder customParameter(String name, String... values) {
            if (values == null || values.length == 0) {
                customParams.remove(name);
            } else {
                customParams.put(name, Arrays.asList(values));
            }
            return this;
        }


        /**
         * Builds a new authentication request.
         *
         * @return The authentication request.
         */
        public OidvpAuthorizationRequest build() {

            try {
                // specify using redirect_uri or response_uri
                URI redirectURI = null;
                URI responseURI = null;
                if (OID4VP.isUsingResponseURI(rm)) {
                    responseURI = responseOrRedirectURI;
                    customParameter("response_uri", responseOrRedirectURI == null ? null : responseOrRedirectURI.toString());
                } else {
                    redirectURI = responseOrRedirectURI;
                }

                return new OidvpAuthorizationRequest(
                    uri, rt, rm, scope, clientID, redirectURI, state, nonce,
                    display, prompt, dpopJKT, trustChain, maxAge, uiLocales, claimsLocales,
                    idTokenHint, loginHint, acrValues, claims,
                    purpose,
                    requestObject, requestURI,
                    codeChallenge, codeChallengeMethod,
                    authorizationDetails,
                    resources,
                    includeGrantedScopes,
                    customParams,
                    presentationDefinition,
                    presentationDefinitionURI,
                    clientMetadata,
                    responseURI,
                    requestUriMethod,
                    transactionData,
                    idTokenType);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Override
    public URI toURI() {
        if (getEndpointURI() != null) {
            ClientIdScheme clientIdScheme = ClientIdScheme.parseByClientID(getClientID());
            if (OID4VP.isAuthorizationRequestMustBeSigned(clientIdScheme)) {
                if (getRequestObject() != null) {
                    if (getRequestObject() instanceof JWSObject jwsObject) {
                        if (JWSObject.State.SIGNED != jwsObject.getState()) {
                            throw new BadOidvpParamException("the authorization request using client_id_scheme:" + clientIdScheme.getValue() + " must be signed, but the request object value has not been signed. (unsigned jwt)");
                        }
                    } else if (getRequestObject() instanceof JWEObject jweObject) {
                        JWSObject jwsObject = jweObject.getPayload().toJWSObject();
                        if (jwsObject == null || JWSObject.State.SIGNED != jwsObject.getState()) {
                            throw new BadOidvpParamException("the authorization request using client_id_scheme:" + clientIdScheme.getValue() + " must be signed, but the request object value has not been signed. (jwe contains unsigned payload)");
                        }
                    } else {
                        throw new BadOidvpParamException("the authorization request using client_id_scheme:" + clientIdScheme.getValue() + " must be signed, but the request object value has not been signed. (plain jwt)");
                    }
                } else if (getRequestURI() == null) {
                    throw new BadOidvpParamException("the authorization request using client_id_scheme:" + clientIdScheme.getValue() + " must be signed, but no request object value or reference was provided.");
                }
            }
        }
        return super.toURI();
    }

    @Override
    public JWTClaimsSet toJWTClaimsSet() {
        JWTClaimsSet jwtClaimsSet = super.toJWTClaimsSet();

        if (jwtClaimsSet.getClaim("max_age") != null) {
            try {
                String maxAgeString = jwtClaimsSet.getStringClaim("max_age");
                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder(jwtClaimsSet);
                builder.claim("max_age", Integer.parseInt(maxAgeString));
                return builder.build();
            } catch (java.text.ParseException e) {
                throw new SerializeException(e.getMessage());
            }
        }

        if (jwtClaimsSet.getClaim("presentation_definition") != null) {
            jwtClaimsSet = new JWTClaimsSet.Builder(jwtClaimsSet)
                .claim("presentation_definition", JsonUtils.convertObjectToMap(getPresentationDefinition()))
                .build();
        }
        if (jwtClaimsSet.getClaim("client_metadata") != null) {
            jwtClaimsSet = new JWTClaimsSet.Builder(jwtClaimsSet)
                .claim("client_metadata", getClientMetadata().toJSONObject())
                .build();
        }
        return jwtClaimsSet;
    }

    public List<ACR> getAcrValues() {
        return acrValues;
    }

    public OIDCClaimsRequest getClaims() {
        return claims;
    }

    public List<LangTag> getClaimsLocales() {
        return claimsLocales;
    }

    public VerifierMetadata getClientMetadata() {
        return clientMetadata;
    }

    public Display getDisplay() {
        return display;
    }

    public JWT getIdTokenHint() {
        return idTokenHint;
    }

    public String getLoginHint() {
        return loginHint;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public Nonce getNonce() {
        return nonce;
    }

    public PresentationDefinition getPresentationDefinition() {
        return presentationDefinition;
    }

    public URI getPresentationDefinitionURI() {
        return presentationDefinitionURI;
    }

    public String getPurpose() {
        return purpose;
    }

    public RequestUriMethod getRequestUriMethod() {
        return requestUriMethod;
    }

    public URI getResponseURI() {
        return responseURI;
    }

    public List<TransactionData> getTransactionData() {
        return transactionData;
    }

    public List<LangTag> getUiLocales() {
        return uiLocales;
    }
}
