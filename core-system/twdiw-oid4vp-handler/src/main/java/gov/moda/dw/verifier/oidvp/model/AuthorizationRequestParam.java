package gov.moda.dw.verifier.oidvp.model;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.Nonce;
import gov.moda.dw.verifier.oidvp.model.oid4vp.IdTokenType;
import gov.moda.dw.verifier.oidvp.model.oid4vp.PresentationDefinitionParameter;
import gov.moda.dw.verifier.oidvp.model.oid4vp.RequestUriMethod;
import gov.moda.dw.verifier.oidvp.model.oid4vp.TransactionData;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public class AuthorizationRequestParam extends RequestParam {

    private URI authorizationEndpoint;

    private State state;

    private Nonce nonce;

    private ResponseType responseType4VP;

    private ResponseMode responseMode4VP;

    /**
     * when response_mode is 'direct_post' or 'direct_post.jwt' will use this uri to set 'response_uri', otherwise will
     * use this uri to set 'redirect_uri'
     */
    private URI redirectOrResponseURI;

    private URI requestURI;

    private PresentationDefinitionParameter presentationDefinitionParameter;

    private VerifierMetadata clientMetadata;

    private Scope scope;

    private RequestUriMethod requestUriMethod;

    private List<TransactionData> transactionData;

    private JWT requestObject;

    private IdTokenType idTokenType;


    public AuthorizationRequestParam(ResponseType responseType4VP, Nonce nonce) {
        this.responseType4VP = responseType4VP;
        this.nonce = nonce;
    }

    public AuthorizationRequestParam(URI authorizationEndpoint, ResponseType responseType4VP, Nonce nonce) {
        this.authorizationEndpoint = Objects.requireNonNull(authorizationEndpoint);
        this.responseType4VP = responseType4VP;
        this.nonce = nonce;
    }

    public AuthorizationRequestParam(URI authorizationEndpoint, URI requestURI) {
        this.authorizationEndpoint = Objects.requireNonNull(authorizationEndpoint);
        this.requestURI = requestURI;
    }

    public AuthorizationRequestParam(URI authorizationEndpoint, JWT requestObject) {
        this.authorizationEndpoint = Objects.requireNonNull(authorizationEndpoint);
        this.requestObject = requestObject;
    }

    // setter
    public AuthorizationRequestParam setAuthorizationEndpoint(URI authorizationEndpoint) {
        this.authorizationEndpoint = Objects.requireNonNull(authorizationEndpoint);
        return this;
    }

    public AuthorizationRequestParam setClientMetadata(VerifierMetadata clientMetadata) {
        this.clientMetadata = clientMetadata;
        return this;
    }

    public AuthorizationRequestParam setNonce(Nonce nonce) {
        this.nonce = nonce;
        return this;
    }

    public AuthorizationRequestParam setPresentationDefinitionParameter(PresentationDefinitionParameter presentationDefinitionParameter) {
        this.presentationDefinitionParameter = presentationDefinitionParameter;
        return this;
    }

    /**
     * when response_mode is 'direct_post' or 'direct_post.jwt' will use this uri to set 'response_uri', otherwise will
     * use this uri to set 'redirect_uri'
     *
     * @param redirectOrResponseURI redirect_uri or response_uri to be set
     * @return AuthorizationRequestParam
     */
    public AuthorizationRequestParam setRedirectOrResponseURI(URI redirectOrResponseURI) {
        this.redirectOrResponseURI = redirectOrResponseURI;
        return this;
    }

    public AuthorizationRequestParam setRequestObject(JWT requestObject) {
        this.requestObject = requestObject;
        return this;
    }

    public AuthorizationRequestParam setRequestURI(URI requestURI) {
        this.requestURI = requestURI;
        return this;
    }

    public AuthorizationRequestParam setRequestUriMethod(RequestUriMethod requestUriMethod) {
        this.requestUriMethod = requestUriMethod;
        return this;
    }

    public AuthorizationRequestParam setResponseMode4VP(ResponseMode responseMode4VP) {
        this.responseMode4VP = responseMode4VP;
        return this;
    }

    public AuthorizationRequestParam setResponseType4VP(ResponseType responseType4VP) {
        this.responseType4VP = responseType4VP;
        return this;
    }

    public AuthorizationRequestParam setScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public AuthorizationRequestParam setState(State state) {
        this.state = state;
        return this;
    }

    public AuthorizationRequestParam setTransactionData(List<TransactionData> transactionData) {
        this.transactionData = transactionData;
        return this;
    }

    public AuthorizationRequestParam setIdTokenType(IdTokenType idTokenType) {
        this.idTokenType = idTokenType;
        return this;
    }

    // getter
    public URI getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public VerifierMetadata getClientMetadata() {
        return clientMetadata;
    }

    public Nonce getNonce() {
        return nonce;
    }

    public PresentationDefinitionParameter getPresentationDefinitionParameter() {
        return presentationDefinitionParameter;
    }

    public URI getRedirectOrResponseURI() {
        return redirectOrResponseURI;
    }

    public JWT getRequestObject() {
        return requestObject;
    }

    public URI getRequestURI() {
        return requestURI;
    }

    public RequestUriMethod getRequestUriMethod() {
        return requestUriMethod;
    }

    public ResponseMode getResponseMode4VP() {
        return responseMode4VP;
    }

    public ResponseType getResponseType4VP() {
        return responseType4VP;
    }

    public Scope getScope() {
        return scope;
    }

    public State getState() {
        return state;
    }

    public List<TransactionData> getTransactionData() {
        return transactionData;
    }

    public boolean specifiesRequestObject() {
        return requestObject != null || requestURI != null;
    }

    public IdTokenType getIdTokenType() {
        return idTokenType;
    }
}
