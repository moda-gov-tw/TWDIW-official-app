package gov.moda.dw.verifier.oidvp.model.did;

public class DIDBaseResponse {

    String code;

    String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return "0".equals(this.code);
    }
}
