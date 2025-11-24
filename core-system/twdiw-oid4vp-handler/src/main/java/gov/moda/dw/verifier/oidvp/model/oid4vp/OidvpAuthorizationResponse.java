package gov.moda.dw.verifier.oidvp.model.oid4vp;

import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public class OidvpAuthorizationResponse extends AuthorizationResponse {

    private final String vpToken;
    private final String presentationSubmission;
    private final String state;
    private final String error;
    private final String errorDescription;
    private final String customData;


    public static OidvpAuthorizationResponse parse(Map<String, List<String>> params) {
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Parameters must be present.");
        }

        String state = getFirstValue(params, "state");
        if (state == null || state.isEmpty()) {
            throw new IllegalArgumentException("state must be present.");
        }

        String vpToken = getFirstValue(params, "vp_token");

        String presentationSubmission = getFirstValue(params, "presentation_submission");

        String error = getFirstValue(params, "error");

        String errorDescription = getFirstValue(params, "error_description");

        String customData = getFirstValue(params, "custom_data");
        customData = (customData == null || customData.isEmpty()) ? null : customData;

        return new OidvpAuthorizationResponse(state, vpToken, presentationSubmission, error, errorDescription, customData);
    }

    public OidvpAuthorizationResponse(String state, String vpToken, String presentationSubmission, String error, String errorDescription, String customData) {
        if (!StringUtils.hasText(state)) {
            throw new IllegalArgumentException("'state' must be present.");
        }
        this.state = state;

        if (!StringUtils.hasText(vpToken) && !StringUtils.hasText(error)) {
            throw new IllegalArgumentException("'vp_token' and 'error' must not be null at the same time.");
        }

        if (vpToken != null && error != null) {
            throw new IllegalArgumentException("'vp_token' and 'error' cannot be present at the same time.");
        }

        if (vpToken == null && !StringUtils.hasText(error)) {
            throw new IllegalArgumentException("'error' must not be empty.");
        }
        this.vpToken = vpToken;
        this.error = error;

        if (vpToken != null && !StringUtils.hasText(presentationSubmission)) {
            throw new IllegalArgumentException("'presentation_submission' is not present");
        }
        this.presentationSubmission = presentationSubmission;

        this.errorDescription = errorDescription;
        this.customData = customData;
    }

    public String getCustomData() {
        return customData;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getPresentationSubmission() {
        return presentationSubmission;
    }

    public String getState() {
        return state;
    }

    public String getVpToken() {
        return vpToken;
    }

    public boolean isSuccess() {
        return this.error == null && StringUtils.hasText(this.vpToken);
    }

    private static <K, V> V getFirstValue(Map<K, List<V>> map, K key) {
        List<V> valueList = map.get(key);
        return valueList != null && !valueList.isEmpty() ? valueList.get(0) : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (customData != null) {
            sb.append("\"custom_data\":\"")
              .append(customData).append('\"');
        }
        if (vpToken != null) {
            sb.append(",\"vp_token\":\"")
              .append(vpToken).append('\"');
        }
        if (presentationSubmission != null) {
            sb.append(",\"presentation_submission\":\"")
              .append(presentationSubmission).append('\"');
        }
        if (state != null) {
            sb.append(",\"state\":\"")
              .append(state).append('\"');
        }
        if (error != null) {
            sb.append(",\"error\":\"")
              .append(error).append('\"');
        }
        if (errorDescription != null) {
            sb.append(",\"error_description\":\"")
              .append(errorDescription).append('\"');
        }
        sb.append("}");
        String s = sb.toString();
        return (s.startsWith("{,")) ? s.replace("{,", "{") : s;
    }
}
