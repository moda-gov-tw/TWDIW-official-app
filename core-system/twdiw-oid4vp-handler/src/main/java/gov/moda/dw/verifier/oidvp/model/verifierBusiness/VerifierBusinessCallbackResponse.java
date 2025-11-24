package gov.moda.dw.verifier.oidvp.model.verifierBusiness;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.Map;

public class VerifierBusinessCallbackResponse {

    private String code;
    private String message;
    @JsonAnySetter
    private Map<String, Object> params;


    public VerifierBusinessCallbackResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public boolean isSuccess() {
        return "0".equals(this.code);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"code\":\"").append(code).append('\"');
        sb.append(",\"message\":\"").append(message).append('\"');
        if (params != null) {
            sb.append(",\"params\":")
              .append(params);
        }
        sb.append("}");
        String s = sb.toString();
        return (s.startsWith("{,")) ? s.replace("{,", "{") : s;
    }
}
