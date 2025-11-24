package gov.moda.dw.issuer.vc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Res.
 */
@Entity
@Table(name = "res",schema = "vc_manager")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Res implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 功能類型
   */
  @NotNull
  @Size(max = 20)
  @Column(name = "type_id", length = 20, nullable = false)
  private String typeId;

  /**
   * 功能代碼
   */
  @NotNull
  @Size(max = 20)
  @Column(name = "res_id", length = 20, nullable = false, unique = true)
  private String resId;

  /**
   * 功能群組
   */
  @NotNull
  @Size(max = 20)
  @Column(name = "res_grp", length = 20, nullable = false)
  private String resGrp;

  /**
   * 功能名稱
   */
  @NotNull
  @Size(max = 50)
  @Column(name = "res_name", length = 50, nullable = false)
  private String resName;

  /**
   * 功能描述
   */
  @Size(max = 255)
  @Column(name = "description", length = 255)
  private String description;

  /**
   * 是否啟用
   */
  @NotNull
  @Size(max = 10)
  @Column(name = "state", length = 10, nullable = false)
  private String state;

  /**
   * URI
   */
  @NotNull
  @Size(max = 2048)
  @Column(name = "api_uri", length = 2048, nullable = false)
  private String apiUri;

  /**
   * URL
   */
  @Size(max = 2048)
  @Column(name = "web_url", length = 2048)
  private String webUrl;

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
   * 建立日
   */
  @NotNull
  @Column(name = "create_time", nullable = false)
  private Instant createTime;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public Res id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTypeId() {
    return this.typeId;
  }

  public Res typeId(String typeId) {
    this.setTypeId(typeId);
    return this;
  }

  public void setTypeId(String typeId) {
    this.typeId = typeId;
  }

  public String getResId() {
    return this.resId;
  }

  public Res resId(String resId) {
    this.setResId(resId);
    return this;
  }

  public void setResId(String resId) {
    this.resId = resId;
  }

  public String getResGrp() {
    return this.resGrp;
  }

  public Res resGrp(String resGrp) {
    this.setResGrp(resGrp);
    return this;
  }

  public void setResGrp(String resGrp) {
    this.resGrp = resGrp;
  }

  public String getResName() {
    return this.resName;
  }

  public Res resName(String resName) {
    this.setResName(resName);
    return this;
  }

  public void setResName(String resName) {
    this.resName = resName;
  }

  public String getDescription() {
    return this.description;
  }

  public Res description(String description) {
    this.setDescription(description);
    return this;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getState() {
    return this.state;
  }

  public Res state(String state) {
    this.setState(state);
    return this;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getApiUri() {
    return this.apiUri;
  }

  public Res apiUri(String apiUri) {
    this.setApiUri(apiUri);
    return this;
  }

  public void setApiUri(String apiUri) {
    this.apiUri = apiUri;
  }

  public String getWebUrl() {
    return this.webUrl;
  }

  public Res webUrl(String webUrl) {
    this.setWebUrl(webUrl);
    return this;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  public String getDataRole1() {
    return this.dataRole1;
  }

  public Res dataRole1(String dataRole1) {
    this.setDataRole1(dataRole1);
    return this;
  }

  public void setDataRole1(String dataRole1) {
    this.dataRole1 = dataRole1;
  }

  public String getDataRole2() {
    return this.dataRole2;
  }

  public Res dataRole2(String dataRole2) {
    this.setDataRole2(dataRole2);
    return this;
  }

  public void setDataRole2(String dataRole2) {
    this.dataRole2 = dataRole2;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public Res createTime(Instant createTime) {
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
    if (!(o instanceof Res)) {
      return false;
    }
    return getId() != null && getId().equals(((Res) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Res{" +
            "id=" + getId() +
            ", typeId='" + getTypeId() + "'" +
            ", resId='" + getResId() + "'" +
            ", resGrp='" + getResGrp() + "'" +
            ", resName='" + getResName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", apiUri='" + getApiUri() + "'" +
            ", webUrl='" + getWebUrl() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
