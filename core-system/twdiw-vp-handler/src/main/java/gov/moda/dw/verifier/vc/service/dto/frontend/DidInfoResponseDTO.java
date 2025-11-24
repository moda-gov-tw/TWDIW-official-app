package gov.moda.dw.verifier.vc.service.dto.frontend;

import gov.moda.dw.verifier.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class DidInfoResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code = -1;
    private String msg;
    private Map<String, Object> data;

    public DidInfoResponseDTO() {
    }

    public DidInfoResponseDTO(int code, String msg, Map<String, Object> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public DidInfoResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            DidInfoResponseDTO didInfoResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
            if (didInfoResponseDTO != null) {
                this.data = didInfoResponseDTO.getData();
                this.code = didInfoResponseDTO.getCode();
                this.msg = didInfoResponseDTO.getMsg();
            }
        }
    }

    public int getCode() {
        return code;
    }

    public DidInfoResponseDTO setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public DidInfoResponseDTO setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public DidInfoResponseDTO setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
