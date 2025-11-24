package gov.moda.dw.issuer.vc.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import gov.moda.dw.issuer.vc.type.StatusCode;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> implements Serializable {

  private static final long serialVersionUID = 3937533115408118803L;

  @Setter
  @Getter
  private String code;

  @Setter
  @Getter
  private String msg;

  /** 實際的 ResponseDTO */
  @Setter
  @Getter
  T data;

  public ResponseDTO() {
    this.setStatusCode(StatusCode.FAIL);
  }

  public ResponseDTO(StatusCode statusCode) {
    this.setStatusCode(statusCode);
  }

  public ResponseDTO(T data) {
    this.data = data;
    this.setStatusCode(StatusCode.SUCCESS);
  }

  public ResponseDTO(T data, StatusCode statusCode) {
    this.data = data;
    this.setStatusCode(statusCode);
  }

  /**
   * 只有 AOP 能用
   *
   * @param msg  msg
   * @param code code
   */
  public ResponseDTO(String msg, String code) {
    this.msg = msg;
    this.code = code;
  }

  public void setStatusCode(StatusCode statusCode) {
    if (statusCode != null) {
      this.msg = statusCode.getMsg();
      this.code = statusCode.getCode();
    }
  }
}
