package gov.moda.dw.issuer.vc.service.dto.frontend;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * DID review response (from frontend service)
 *
 * @version 20240902
 */
public class DidReviewResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code = -1;
    private String msg;
    private Map<String, Object> data;

    public DidReviewResponseDTO() {
    }

    public DidReviewResponseDTO(int code, String msg, Map<String, Object> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public DidReviewResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            DidReviewResponseDTO didReviewResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
            if (didReviewResponseDTO != null) {
                this.data = didReviewResponseDTO.getData();
                this.code = didReviewResponseDTO.getCode();
                this.msg = didReviewResponseDTO.getMsg();
            }
        }
    }

    public int getCode() {
        return code;
    }

    public DidReviewResponseDTO setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public DidReviewResponseDTO setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public DidReviewResponseDTO setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
