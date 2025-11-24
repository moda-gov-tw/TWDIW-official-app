package gov.moda.dw.verifier.oidvp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import java.net.URI;

@JsonInclude(Include.NON_NULL)
public class OidvpResponse extends Response {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("qrcode_image")
    private String qrCodeImage;

    @JsonProperty("auth_uri")
    private String authUri;

    @JsonProperty("expired_in")
    private Integer expiredIn;

    public static OidvpResponse success(String transactionId, URI authUri) {
        OidvpResponse response = new OidvpResponse();
        response.setTransactionId(transactionId);
        response.setAuthUri(authUri.toString());
        return response;
    }

    public static OidvpResponse success(String transactionId, URI authUri, Integer expiredIn) {
        OidvpResponse response = new OidvpResponse();
        response.setTransactionId(transactionId);
        response.setAuthUri(authUri.toString());
        response.setExpiredIn(expiredIn);
        return response;
    }

    public static OidvpResponse success(String transactionId, String qrCode, URI authUri) {
        OidvpResponse response = new OidvpResponse();
        response.setTransactionId(transactionId);
        response.setQrCodeImage(qrCode);
        response.setAuthUri(authUri.toString());
        return response;
    }

    public static OidvpResponse success(String transactionId, String qrCode, URI authUri, Integer expiredIn) {
        OidvpResponse response = new OidvpResponse();
        response.setTransactionId(transactionId);
        response.setQrCodeImage(qrCode);
        response.setAuthUri(authUri.toString());
        response.setExpiredIn(expiredIn);
        return response;
    }

    public static OidvpResponse error(OidvpError error) {
        OidvpResponse response = new OidvpResponse();
        response.setCode(error.getCode());
        response.setMessage(error.getMsg());
        return response;
    }

    public static OidvpResponse error(OidvpError error, String errorMessage) {
        OidvpResponse response = new OidvpResponse();
        response.setCode(error.getCode());
        response.setMessage(errorMessage);
        return response;
    }

    public static OidvpResponse error(int errorCode, String errorMessage) {
        OidvpResponse response = new OidvpResponse();
        response.setCode(errorCode);
        response.setMessage(errorMessage);
        return response;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAuthUri() {
        return authUri;
    }

    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public void setExpiredIn(Integer expiredIn) {
        this.expiredIn = expiredIn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (authUri != null) {
            sb.append("\"authUri\":\"")
              .append(authUri).append('\"');
        }
        if (transactionId != null) {
            sb.append(",\"transactionId\":\"")
              .append(transactionId).append('\"');
        }
        if (qrCodeImage != null) {
            sb.append(",\"qrCodeImage\":\"")
              .append(qrCodeImage).append('\"');
        }
        if (code != null) {
            sb.append(",\"code\":")
              .append(code);
        }
        if (message != null) {
            sb.append(",\"message\":\"")
              .append(message).append('\"');
        }
        if (expiredIn != null) {
            sb.append(",\"expiredIn\":\"")
              .append(message).append('\"');
        }
        sb.append("}");
        String s = sb.toString();
        return (s.startsWith("{,")) ? s.replace("{,", "{") : s;
    }
}
