package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.Response;

@JsonInclude(Include.NON_NULL)
public class AfterAuthorizationResponse extends Response {

    @JsonProperty("redirect_uri")
    private String redirectURI;

    public static AfterAuthorizationResponse success() {
        AfterAuthorizationResponse response = new AfterAuthorizationResponse();
        response.setCode(OidvpError.SUCCESS.getCode());
        response.setMessage(OidvpError.SUCCESS.getMsg());
        return response;
    }

    public static AfterAuthorizationResponse success(String redirectURI) {
        AfterAuthorizationResponse response = new AfterAuthorizationResponse();
        response.setCode(OidvpError.SUCCESS.getCode());
        response.setMessage(OidvpError.SUCCESS.getMsg());
        response.setRedirectURI(redirectURI);
        return response;
    }

    public static AfterAuthorizationResponse fail(OidvpError error, String errorMessage) {
        AfterAuthorizationResponse response = new AfterAuthorizationResponse();
        response.setCode(error.getCode());
        response.setMessage(errorMessage);
        return response;
    }

    public static AfterAuthorizationResponse fail(
        OidvpError error, String errorMessage, String redirectURI) {
        AfterAuthorizationResponse response = new AfterAuthorizationResponse();
        response.setCode(error.getCode());
        response.setMessage(errorMessage);
        response.setRedirectURI(redirectURI);
        return response;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (redirectURI != null) {
            sb.append("\"redirectURI\":\"")
              .append(redirectURI).append('\"');
        }
        if (code != null) {
            sb.append(",\"code\":")
              .append(code);
        }
        if (message != null) {
            sb.append(",\"message\":\"")
              .append(message).append('\"');
        }
        sb.append("}");
        String s = sb.toString();
        return (s.startsWith("{,")) ? s.replace("{,", "{") : s;
    }
}
