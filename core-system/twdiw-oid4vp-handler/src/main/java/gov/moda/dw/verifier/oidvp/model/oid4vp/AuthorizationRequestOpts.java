package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import java.time.LocalDateTime;
import java.util.Objects;

public class AuthorizationRequestOpts {

    private ResponseMode responseMode = OidvpResponseMode.DIRECT_POST;
    private final ResponseType responseType;
    private LocalDateTime expiredTime;
    private String callbackUri;

    public AuthorizationRequestOpts(ResponseType responseType) {
        this.responseType = responseType;
    }

    public AuthorizationRequestOpts(ResponseType responseType, ResponseMode responseMode) {
        this.responseType = Objects.requireNonNull(responseType);
        this.responseMode = responseMode == null ? OidvpResponseMode.DIRECT_POST : responseMode;
    }

    public ResponseMode getResponseMode() {
        return responseMode;
    }

    public AuthorizationRequestOpts setResponseMode(ResponseMode responseMode) {
        this.responseMode = responseMode;
        return this;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public AuthorizationRequestOpts setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
        return this;
    }

    public String getCallbackUri() {
        return callbackUri;
    }

    public AuthorizationRequestOpts setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
        return this;
    }

    public enum JARType {
        NONE, VALUE, REFERENCE
    }
}
