package gov.moda.dw.issuer.vc.service.dto.push;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * request of push to notify status change (to push service)
 *
 * @version 20240902
 */
public class PushNotifyStatusChangeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private RequestBody requestBody;

    public PushNotifyStatusChangeRequestDTO() {
    }

    public PushNotifyStatusChangeRequestDTO(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public PushNotifyStatusChangeRequestDTO(String reqJson) {

        if (reqJson != null && !reqJson.isBlank()) {
            PushNotifyStatusChangeRequestDTO pushNotifyStatusChangeRequestDTO = JsonUtils.jsToVo(reqJson, this.getClass());
            if (pushNotifyStatusChangeRequestDTO != null) {
                this.requestBody = pushNotifyStatusChangeRequestDTO.getRequestBody();
            }
        }
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public PushNotifyStatusChangeRequestDTO setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }

    public static class RequestBody {

        private String appId;
        private String domainKey;
        private String title;
        private String message;
        private String param;

        public RequestBody() {
        }

        public RequestBody(String appId, String domainKey, String title, String message, String param) {
            this.appId = appId;
            this.domainKey = domainKey;
            this.title = title;
            this.message = message;
            this.param = param;
        }

        public String getAppId() {
            return appId;
        }

        public RequestBody setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public String getDomainKey() {
            return domainKey;
        }

        public RequestBody setDomainKey(String domainKey) {
            this.domainKey = domainKey;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public RequestBody setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public RequestBody setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getParam() {
            return param;
        }

        public RequestBody setParam(String param) {
            this.param = param;
            return this;
        }
    }
}
