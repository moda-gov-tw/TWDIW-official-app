package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A CookiesMsg.
 */
@Entity
@Table(name = "cookies_msg")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CookiesMsg implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "cid", nullable = false)
  private String cid;

  @Column(name = "msg")
  private String msg;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public CookiesMsg id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCid() {
    return this.cid;
  }

  public CookiesMsg cid(String cid) {
    this.setCid(cid);
    return this;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public String getMsg() {
    return this.msg;
  }

  public CookiesMsg msg(String msg) {
    this.setMsg(msg);
    return this;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CookiesMsg)) {
      return false;
    }
    return getId() != null && getId().equals(((CookiesMsg) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "CookiesMsg{" +
            "id=" + getId() +
            ", cid='" + getCid() + "'" +
            ", msg='" + getMsg() + "'" +
            "}";
    }
}
