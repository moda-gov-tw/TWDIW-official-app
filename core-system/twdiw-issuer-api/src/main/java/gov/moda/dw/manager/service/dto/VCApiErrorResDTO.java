package gov.moda.dw.manager.service.dto;

public class VCApiErrorResDTO {

    private String code;
    private String message;

    private String resp_code;
    private String resp_message;

    private DwissuerVC203iInfoResp info;

    public String getResp_code() {
        return resp_code;
    }

    public void setResp_code(String resp_code) {
        this.resp_code = resp_code;
    }

    public String getResp_message() {
        return resp_message;
    }

    public void setResp_message(String resp_message) {
        this.resp_message = resp_message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DwissuerVC203iInfoResp getInfo() {
        return info;
    }

    public void setInfo(DwissuerVC203iInfoResp info) {
        this.info = info;
    }
}
