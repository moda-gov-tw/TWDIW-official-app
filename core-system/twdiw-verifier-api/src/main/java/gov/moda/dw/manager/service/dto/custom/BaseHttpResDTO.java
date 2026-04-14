package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonInclude;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseHttpResDTO<T> {

    private String code;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public BaseHttpResDTO() {
    }

    public BaseHttpResDTO(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseHttpResDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> BaseHttpResDTO<T> success(T data) {
        return new BaseHttpResDTO<>(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMsg(), data);
    }

    public static <T> BaseHttpResDTO<T> success() {
        return new BaseHttpResDTO<>(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMsg());
    }

    public static <T> BaseHttpResDTO<T> error(String code, String message) {
        return new BaseHttpResDTO<>(code, message, null);
    }

    public static <T> BaseHttpResDTO<T> error(StatusCode statusCode) {
        return new BaseHttpResDTO<>(statusCode.getCode(), statusCode.getMsg(), null);
    }

}
