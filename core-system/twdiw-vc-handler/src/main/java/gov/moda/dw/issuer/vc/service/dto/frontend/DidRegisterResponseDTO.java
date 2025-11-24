package gov.moda.dw.issuer.vc.service.dto.frontend;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * DID registration response (from frontend service)
 *
 * @version 20240902
 */
public class DidRegisterResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code = -1;
    private String msg;
    private Map<String, Object> data;


    public DidRegisterResponseDTO() {
    }

    public DidRegisterResponseDTO(Map<String, Object> data, int code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public DidRegisterResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            DidRegisterResponseDTO didRegisterResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
            if (didRegisterResponseDTO != null) {
                this.data = didRegisterResponseDTO.getData();
                this.code = didRegisterResponseDTO.getCode();
                this.msg = didRegisterResponseDTO.getMsg();
            }
        }
    }

    public Map<String, Object> getData() {
        return data;
    }

    public DidRegisterResponseDTO setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public DidRegisterResponseDTO setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public DidRegisterResponseDTO setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
