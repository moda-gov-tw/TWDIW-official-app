package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.type.StatusCode;

@Getter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ams351wRelResultDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 6545077905842341476L;

  @Setter
  private String code;

  @Setter
  private String msg;

  private StatusCode statusCode;

  @Setter
  private List<ResDTO> resDTOList;

  public Ams351wRelResultDTO(StatusCode statusCode) {
    this.setStatusCode(statusCode);
  }

  public Ams351wRelResultDTO(List<ResDTO> resDTOList) {
    this.resDTOList = resDTOList;
    this.setStatusCode(StatusCode.SUCCESS);
  }

  public void setStatusCode(StatusCode statusCode) {
    if (statusCode != null) {
      this.statusCode = statusCode;
      this.msg = statusCode.getMsg();
      this.code = statusCode.getCode();
    }
  }
}
