package gov.moda.dw.manager.service.dto;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VCItemData} entity.
 */
public class Http4xxResDTO {

    private String code;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
