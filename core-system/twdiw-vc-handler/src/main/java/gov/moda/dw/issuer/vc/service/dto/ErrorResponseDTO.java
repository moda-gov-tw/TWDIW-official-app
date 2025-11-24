package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import gov.moda.dw.issuer.vc.vo.VcException;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * error response
 *
 * @version 20240902
 */
public class ErrorResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private int code;

    @NotBlank
    private String message;

    private Map<String, Object> info;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(VcException vcException) {
        this(((ErrorResponseProperty) vcException).getResponseCode(), ((ErrorResponseProperty) vcException).getResponseMessage(), null);
    }

    public ErrorResponseDTO(ErrorResponseProperty errorResponseProperty){
        this(errorResponseProperty.getResponseCode(), errorResponseProperty.getResponseMessage(), null);
    }

    public ErrorResponseDTO(int code, String message) {
        this(code, message, null);
    }

    public ErrorResponseDTO(int code, String message, Map<String, Object> info) {
        this.code = code;
        this.message = message;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public ErrorResponseDTO setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorResponseDTO setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public ErrorResponseDTO setInfo(Map<String, Object> info) {
        this.info = info;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
