package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.CookiesMsg;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link CookiesMsg} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CookiesMsgDTO implements Serializable {

  private Long id;

  @NotNull
  private String cid;

  private String msg;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CookiesMsgDTO)) {
      return false;
    }

    CookiesMsgDTO cookiesMsgDTO = (CookiesMsgDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, cookiesMsgDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "CookiesMsgDTO{" +
            "id=" + getId() +
            ", cid='" + getCid() + "'" +
            ", msg='" + getMsg() + "'" +
            "}";
    }
}
