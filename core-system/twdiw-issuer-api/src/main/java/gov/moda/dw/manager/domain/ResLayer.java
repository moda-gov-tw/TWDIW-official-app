package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ResLayer.
 */
@Entity
@Table(name = "res_layer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResLayer implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 父id
   */
  @Column(name = "parent_id")
  private Long parentId;

  /**
   * 子id
   */
  @NotNull
  @Column(name = "child_id", nullable = false)
  private Long childId;

  /**
   * 父代碼
   */
  @Size(max = 50)
  @Column(name = "parent_code", length = 50)
  private String parentCode;

  /**
   * 子代碼
   */
  @Size(max = 50)
  @Column(name = "child_code", length = 50)
  private String childCode;

  /**
   * 建立日期時間
   */
  @NotNull
  @Column(name = "create_time", nullable = false)
  private Instant createTime;

  /**
   * 排序
   */
  @Size(max = 10)
  @Column(name = "orderval", length = 10)
  private String orderval;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public ResLayer id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getParentId() {
    return this.parentId;
  }

  public ResLayer parentId(Long parentId) {
    this.setParentId(parentId);
    return this;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Long getChildId() {
    return this.childId;
  }

  public ResLayer childId(Long childId) {
    this.setChildId(childId);
    return this;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

  public String getParentCode() {
    return this.parentCode;
  }

  public ResLayer parentCode(String parentCode) {
    this.setParentCode(parentCode);
    return this;
  }

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
  }

  public String getChildCode() {
    return this.childCode;
  }

  public ResLayer childCode(String childCode) {
    this.setChildCode(childCode);
    return this;
  }

  public void setChildCode(String childCode) {
    this.childCode = childCode;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public ResLayer createTime(Instant createTime) {
    this.setCreateTime(createTime);
    return this;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public String getOrderval() {
    return this.orderval;
  }

  public ResLayer orderval(String orderval) {
    this.setOrderval(orderval);
    return this;
  }

  public void setOrderval(String orderval) {
    this.orderval = orderval;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ResLayer)) {
      return false;
    }
    return getId() != null && getId().equals(((ResLayer) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ResLayer{" +
            "id=" + getId() +
            ", parentId=" + getParentId() +
            ", childId=" + getChildId() +
            ", parentCode='" + getParentCode() + "'" +
            ", childCode='" + getChildCode() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", orderval='" + getOrderval() + "'" +
            "}";
    }
}
