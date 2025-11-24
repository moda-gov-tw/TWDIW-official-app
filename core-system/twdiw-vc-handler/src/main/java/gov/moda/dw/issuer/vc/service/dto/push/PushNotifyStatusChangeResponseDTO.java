package gov.moda.dw.issuer.vc.service.dto.push;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * response of push to notify status change (from push service)
 *
 * @version 20240902
 */
public class PushNotifyStatusChangeResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ResponseHeader responseHeader;

    public PushNotifyStatusChangeResponseDTO() {
    }

    public PushNotifyStatusChangeResponseDTO(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public PushNotifyStatusChangeResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            PushNotifyStatusChangeResponseDTO pushNotifyStatusChangeResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
            if (pushNotifyStatusChangeResponseDTO != null) {
                this.responseHeader = pushNotifyStatusChangeResponseDTO.getResponseHeader();
            }
        }
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public PushNotifyStatusChangeResponseDTO setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }

    public static class ResponseHeader {

        private String returnCode;
        private String returnMsg;

        public ResponseHeader() {
        }

        public ResponseHeader(String returnCode, String returnMsg) {
            this.returnCode = returnCode;
            this.returnMsg = returnMsg;
        }

        public String getReturnCode() {
            return returnCode;
        }

        public ResponseHeader setReturnCode(String returnCode) {
            this.returnCode = returnCode;
            return this;
        }

        public String getReturnMsg() {
            return returnMsg;
        }

        public ResponseHeader setReturnMsg(String returnMsg) {
            this.returnMsg = returnMsg;
            return this;
        }
    }
}
