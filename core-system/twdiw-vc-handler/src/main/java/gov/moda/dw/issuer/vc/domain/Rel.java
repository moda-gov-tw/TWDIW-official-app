package gov.moda.dw.issuer.vc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Rel.
 */
@Entity
@Table(name = "rel",schema = "vc_manager")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Rel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 左值table(Table 代碼) UK
   */
  @NotNull
  @Size(max = 20)
  @Column(name = "left_tbl", length = 20, nullable = false)
  private String leftTbl;

  /**
   * 左值id(Table 的id) UK
   */
  @NotNull
  @Column(name = "left_id", nullable = false)
  private Long leftId;

  /**
   * 右值table(Table 代碼) UK
   */
  @NotNull
  @Size(max = 20)
  @Column(name = "right_tbl", length = 20, nullable = false)
  private String rightTbl;

  /**
   * 右值id(Table 的id) UK
   */
  @NotNull
  @Column(name = "right_id", nullable = false)
  private Long rightId;

  /**
   * 左值代碼
   */
  @Size(max = 320)
  @Column(name = "left_code", length = 320)
  private String leftCode;

  /**
   * 右值代碼
   */
  @Size(max = 50)
  @Column(name = "right_code", length = 50)
  private String rightCode;

  /**
   * 狀態
   */
  @NotNull
  @Size(max = 10)
  @Column(name = "state", length = 10, nullable = false)
  private String state;

  /**
   * 預留欄位1
   */
  @Size(max = 255)
  @Column(name = "data_role_1", length = 255)
  private String dataRole1;

  /**
   * 預留欄位2
   */
  @Size(max = 255)
  @Column(name = "data_role_2", length = 255)
  private String dataRole2;

  /**
   * 受控項
   */
  @Size(max = 64)
  @Column(name = "data_auth", length = 64)
  private String dataAuth;

  /**
   * 建立日
   */
  @NotNull
  @Column(name = "create_time", nullable = false)
  private Instant createTime;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public Rel id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLeftTbl() {
    return this.leftTbl;
  }

  public Rel leftTbl(String leftTbl) {
    this.setLeftTbl(leftTbl);
    return this;
  }

  public void setLeftTbl(String leftTbl) {
    this.leftTbl = leftTbl;
  }

  public Long getLeftId() {
    return this.leftId;
  }

  public Rel leftId(Long leftId) {
    this.setLeftId(leftId);
    return this;
  }

  public void setLeftId(Long leftId) {
    this.leftId = leftId;
  }

  public String getRightTbl() {
    return this.rightTbl;
  }

  public Rel rightTbl(String rightTbl) {
    this.setRightTbl(rightTbl);
    return this;
  }

  public void setRightTbl(String rightTbl) {
    this.rightTbl = rightTbl;
  }

  public Long getRightId() {
    return this.rightId;
  }

  public Rel rightId(Long rightId) {
    this.setRightId(rightId);
    return this;
  }

  public void setRightId(Long rightId) {
    this.rightId = rightId;
  }

  public String getLeftCode() {
    return this.leftCode;
  }

  public Rel leftCode(String leftCode) {
    this.setLeftCode(leftCode);
    return this;
  }

  public void setLeftCode(String leftCode) {
    this.leftCode = leftCode;
  }

  public String getRightCode() {
    return this.rightCode;
  }

  public Rel rightCode(String rightCode) {
    this.setRightCode(rightCode);
    return this;
  }

  public void setRightCode(String rightCode) {
    this.rightCode = rightCode;
  }

  public String getState() {
    return this.state;
  }

  public Rel state(String state) {
    this.setState(state);
    return this;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getDataRole1() {
    return this.dataRole1;
  }

  public Rel dataRole1(String dataRole1) {
    this.setDataRole1(dataRole1);
    return this;
  }

  public void setDataRole1(String dataRole1) {
    this.dataRole1 = dataRole1;
  }

  public String getDataRole2() {
    return this.dataRole2;
  }

  public Rel dataRole2(String dataRole2) {
    this.setDataRole2(dataRole2);
    return this;
  }

  public void setDataRole2(String dataRole2) {
    this.dataRole2 = dataRole2;
  }

  public String getDataAuth() {
    return this.dataAuth;
  }

  public Rel dataAuth(String dataAuth) {
    this.setDataAuth(dataAuth);
    return this;
  }

  public void setDataAuth(String dataAuth) {
    this.dataAuth = dataAuth;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public Rel createTime(Instant createTime) {
    this.setCreateTime(createTime);
    return this;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Rel)) {
      return false;
    }
    return getId() != null && getId().equals(((Rel) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Rel{" +
            "id=" + getId() +
            ", leftTbl='" + getLeftTbl() + "'" +
            ", leftId=" + getLeftId() +
            ", rightTbl='" + getRightTbl() + "'" +
            ", rightId=" + getRightId() +
            ", leftCode='" + getLeftCode() + "'" +
            ", rightCode='" + getRightCode() + "'" +
            ", state='" + getState() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", dataAuth='" + getDataAuth() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
