package gov.moda.dw.manager.service.dto.custom;

public class DwFront104iResDTO {

    private String msg;
    private String code;
    private DwFront104iResData data;

    public DwFront104iResData getData() {
        return data;
    }

    public void setData(DwFront104iResData data) {
        this.data = data;
    }

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
}
