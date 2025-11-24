package gov.moda.dw.issuer.oidvci.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A AccessToken.
 */
@Entity
@Table(name = "access_token" ,schema = "public")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccessToken implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @NotNull
  @Size(max = 140)
  @Column(name = "access_token", length = 140, nullable = false)
  private String accessToken;

  @NotNull
  @Size(max = 140)
  @Column(name = "access_token_name", length = 140, nullable = false)
  private String accessTokenName;

  @Size(max = 140)
  @Column(name = "owner", length = 140)
  private String owner;

  @Size(max = 140)
  @Column(name = "owner_name", length = 140)
  private String ownerName;

  @NotNull
  @Size(max = 140)
  @Column(name = "org_id", length = 140, nullable = false)
  private String orgId;

  @Size(max = 140)
  @Column(name = "org_name", length = 140)
  private String orgName;

  @NotNull
  @Size(max = 20)
  @Column(name = "state", length = 20, nullable = false)
  private String state;

  @NotNull
  @Size(max = 20)
  @Column(name = "actype", length = 20, nullable = false)
  private String actype;

  @Size(max = 140)
  @Column(name = "data_role_1", length = 140)
  private String dataRole1;

  @Size(max = 140)
  @Column(name = "data_role_2", length = 140)
  private String dataRole2;

  @Size(max = 140)
  @Column(name = "secu_layer", length = 140)
  private String secuLayer;

  @Column(name = "expiration_time")
  private Instant expirationTime;

  @NotNull
  @Column(name = "create_time", nullable = false)
  private Instant createTime;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public AccessToken id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAccessToken() {
    return this.accessToken;
  }

  public AccessToken accessToken(String accessToken) {
    this.setAccessToken(accessToken);
    return this;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getAccessTokenName() {
    return this.accessTokenName;
  }

  public AccessToken accessTokenName(String accessTokenName) {
    this.setAccessTokenName(accessTokenName);
    return this;
  }

  public void setAccessTokenName(String accessTokenName) {
    this.accessTokenName = accessTokenName;
  }

  public String getOwner() {
    return this.owner;
  }

  public AccessToken owner(String owner) {
    this.setOwner(owner);
    return this;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getOwnerName() {
    return this.ownerName;
  }

  public AccessToken ownerName(String ownerName) {
    this.setOwnerName(ownerName);
    return this;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getOrgId() {
    return this.orgId;
  }

  public AccessToken orgId(String orgId) {
    this.setOrgId(orgId);
    return this;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getOrgName() {
    return this.orgName;
  }

  public AccessToken orgName(String orgName) {
    this.setOrgName(orgName);
    return this;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getState() {
    return this.state;
  }

  public AccessToken state(String state) {
    this.setState(state);
    return this;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getActype() {
    return this.actype;
  }

  public AccessToken actype(String actype) {
    this.setActype(actype);
    return this;
  }

  public void setActype(String actype) {
    this.actype = actype;
  }

  public String getDataRole1() {
    return this.dataRole1;
  }

  public AccessToken dataRole1(String dataRole1) {
    this.setDataRole1(dataRole1);
    return this;
  }

  public void setDataRole1(String dataRole1) {
    this.dataRole1 = dataRole1;
  }

  public String getDataRole2() {
    return this.dataRole2;
  }

  public AccessToken dataRole2(String dataRole2) {
    this.setDataRole2(dataRole2);
    return this;
  }

  public void setDataRole2(String dataRole2) {
    this.dataRole2 = dataRole2;
  }

  public String getSecuLayer() {
    return this.secuLayer;
  }

  public AccessToken secuLayer(String secuLayer) {
    this.setSecuLayer(secuLayer);
    return this;
  }

  public void setSecuLayer(String secuLayer) {
    this.secuLayer = secuLayer;
  }

  public Instant getExpirationTime() {
    return this.expirationTime;
  }

  public AccessToken expirationTime(Instant expirationTime) {
    this.setExpirationTime(expirationTime);
    return this;
  }

  public void setExpirationTime(Instant expirationTime) {
    this.expirationTime = expirationTime;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public AccessToken createTime(Instant createTime) {
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
    if (!(o instanceof AccessToken)) {
      return false;
    }
    return getId() != null && getId().equals(((AccessToken) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "AccessToken{" +
            "id=" + getId() +
            ", accessToken='" + getAccessToken() + "'" +
            ", accessTokenName='" + getAccessTokenName() + "'" +
            ", owner='" + getOwner() + "'" +
            ", ownerName='" + getOwnerName() + "'" +
            ", orgId='" + getOrgId() + "'" +
            ", orgName='" + getOrgName() + "'" +
            ", state='" + getState() + "'" +
            ", actype='" + getActype() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", secuLayer='" + getSecuLayer() + "'" +
            ", expirationTime='" + getExpirationTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
