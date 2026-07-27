package gov.moda.dw.manager.service.dto;

import java.io.Serializable;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class ErrorResponseDTO implements Serializable {

  private String message;
  private int code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
