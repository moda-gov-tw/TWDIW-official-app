package gov.moda.dw.manager.web.rest.errors;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Getter;

/**
 * 客製錯誤處理
 */
@Getter
public class DWException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    private final String msg;

    public DWException() {
        this(StatusCode.FAIL);
    }

    public DWException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public DWException(StatusCode statusCode) {
        this(statusCode.getCode(), statusCode.getMsg());
    }

}
