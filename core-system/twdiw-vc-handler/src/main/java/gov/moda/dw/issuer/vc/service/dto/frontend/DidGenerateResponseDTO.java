package gov.moda.dw.issuer.vc.service.dto.frontend;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DID generation response (from frontend service)
 *
 * @version 20240902
 */
public class DidGenerateResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code = -1;
    private String msg;
    private Data data;


    public DidGenerateResponseDTO() {
    }

    public DidGenerateResponseDTO(int code, String msg, Data data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public DidGenerateResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            DidGenerateResponseDTO didGenerateResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
            if (didGenerateResponseDTO != null) {
                this.code = didGenerateResponseDTO.getCode();
                this.msg = didGenerateResponseDTO.getMsg();
                this.data = didGenerateResponseDTO.getData();
            }
        }
    }

    public int getCode() {
        return code;
    }

    public DidGenerateResponseDTO setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public DidGenerateResponseDTO setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Data getData() {
        return data;
    }

    public DidGenerateResponseDTO setData(Data data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }

    public static class Data {

        private Map<String, Object> did;

        public Data() {
        }

        public Data(Map<String, Object> did) {
            this.did = did;
        }

        public Map<String, Object> getDid() {
            return did;
        }

        public Data setDid(Map<String, Object> did) {
            this.did = did;
            return this;
        }

        @Override
        public String toString() {
            return JsonUtils.voToJs(this);
        }
    }
}
