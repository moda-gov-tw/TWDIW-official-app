package gov.moda.dw.issuer.oidvci.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import gov.moda.dw.issuer.oidvci.domain.AccessToken;
import gov.moda.dw.issuer.oidvci.web.rest.AccessTokenResource;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link AccessToken} entity. This class is used
 * in {@link AccessTokenResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /access-tokens?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccessTokenCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter accessToken;

  private StringFilter accessTokenName;

  private StringFilter owner;

  private StringFilter ownerName;

  private StringFilter orgId;

  private StringFilter orgName;

  private StringFilter state;

  private StringFilter actype;

  private StringFilter dataRole1;

  private StringFilter dataRole2;

  private StringFilter secuLayer;

  private InstantFilter expirationTime;

  private InstantFilter createTime;

  private Boolean distinct;

  public AccessTokenCriteria() {}

  public AccessTokenCriteria(AccessTokenCriteria other) {
    this.id = other.optionalId().map(LongFilter::copy).orElse(null);
    this.accessToken = other.optionalAccessToken().map(StringFilter::copy).orElse(null);
    this.accessTokenName = other.optionalAccessTokenName().map(StringFilter::copy).orElse(null);
    this.owner = other.optionalOwner().map(StringFilter::copy).orElse(null);
    this.ownerName = other.optionalOwnerName().map(StringFilter::copy).orElse(null);
    this.orgId = other.optionalOrgId().map(StringFilter::copy).orElse(null);
    this.orgName = other.optionalOrgName().map(StringFilter::copy).orElse(null);
    this.state = other.optionalState().map(StringFilter::copy).orElse(null);
    this.actype = other.optionalActype().map(StringFilter::copy).orElse(null);
    this.dataRole1 = other.optionalDataRole1().map(StringFilter::copy).orElse(null);
    this.dataRole2 = other.optionalDataRole2().map(StringFilter::copy).orElse(null);
    this.secuLayer = other.optionalSecuLayer().map(StringFilter::copy).orElse(null);
    this.expirationTime = other.optionalExpirationTime().map(InstantFilter::copy).orElse(null);
    this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
    this.distinct = other.distinct;
  }

  @Override
  public AccessTokenCriteria copy() {
    return new AccessTokenCriteria(this);
  }

  public LongFilter getId() {
    return id;
  }

  public Optional<LongFilter> optionalId() {
    return Optional.ofNullable(id);
  }

  public LongFilter id() {
    if (id == null) {
      setId(new LongFilter());
    }
    return id;
  }

  public void setId(LongFilter id) {
    this.id = id;
  }

  public StringFilter getAccessToken() {
    return accessToken;
  }

  public Optional<StringFilter> optionalAccessToken() {
    return Optional.ofNullable(accessToken);
  }

//  public StringFilter accessToken() {
//    if (accessToken == null) {
//      setAccessToken(new StringFilter());
//    }
//    return accessToken;
//  }

//  public void setAccessToken(StringFilter accessToken) {
////    this.accessToken = accessToken;
//  }

  public StringFilter getAccessTokenName() {
    return accessTokenName;
  }

  public Optional<StringFilter> optionalAccessTokenName() {
    return Optional.ofNullable(accessTokenName);
  }

//  public StringFilter accessTokenName() {
//    if (accessTokenName == null) {
//      setAccessTokenName(new StringFilter());
//    }
//    return accessTokenName;
//  }

//  public void setAccessTokenName(StringFilter accessTokenName) {
////    this.accessTokenName = accessTokenName;
//  }

  public StringFilter getOwner() {
    return owner;
  }

  public Optional<StringFilter> optionalOwner() {
    return Optional.ofNullable(owner);
  }

  public StringFilter owner() {
    if (owner == null) {
      setOwner(new StringFilter());
    }
    return owner;
  }

  public void setOwner(StringFilter owner) {
    this.owner = owner;
  }

  public StringFilter getOwnerName() {
    return ownerName;
  }

  public Optional<StringFilter> optionalOwnerName() {
    return Optional.ofNullable(ownerName);
  }

  public StringFilter ownerName() {
    if (ownerName == null) {
      setOwnerName(new StringFilter());
    }
    return ownerName;
  }

  public void setOwnerName(StringFilter ownerName) {
    this.ownerName = ownerName;
  }

  public StringFilter getOrgId() {
    return orgId;
  }

  public Optional<StringFilter> optionalOrgId() {
    return Optional.ofNullable(orgId);
  }

  public StringFilter orgId() {
    if (orgId == null) {
      setOrgId(new StringFilter());
    }
    return orgId;
  }

  public void setOrgId(StringFilter orgId) {
    this.orgId = orgId;
  }

  public StringFilter getOrgName() {
    return orgName;
  }

  public Optional<StringFilter> optionalOrgName() {
    return Optional.ofNullable(orgName);
  }

  public StringFilter orgName() {
    if (orgName == null) {
      setOrgName(new StringFilter());
    }
    return orgName;
  }

  public void setOrgName(StringFilter orgName) {
    this.orgName = orgName;
  }

  public StringFilter getState() {
    return state;
  }

  public Optional<StringFilter> optionalState() {
    return Optional.ofNullable(state);
  }

  public StringFilter state() {
    if (state == null) {
      setState(new StringFilter());
    }
    return state;
  }

  public void setState(StringFilter state) {
    this.state = state;
  }

  public StringFilter getActype() {
    return actype;
  }

  public Optional<StringFilter> optionalActype() {
    return Optional.ofNullable(actype);
  }

  public StringFilter actype() {
    if (actype == null) {
      setActype(new StringFilter());
    }
    return actype;
  }

  public void setActype(StringFilter actype) {
    this.actype = actype;
  }

  public StringFilter getDataRole1() {
    return dataRole1;
  }

  public Optional<StringFilter> optionalDataRole1() {
    return Optional.ofNullable(dataRole1);
  }

//  public StringFilter dataRole1() {
//    if (dataRole1 == null) {
//      setDataRole1(new StringFilter());
//    }
//    return dataRole1;
//  }

//  public void setDataRole1(StringFilter dataRole1) {
////    this.dataRole1 = dataRole1;
//  }

  public StringFilter getDataRole2() {
    return dataRole2;
  }

  public Optional<StringFilter> optionalDataRole2() {
    return Optional.ofNullable(dataRole2);
  }

  public StringFilter dataRole2() {
    if (dataRole2 == null) {
      setDataRole2(new StringFilter());
    }
    return dataRole2;
  }

  public void setDataRole2(StringFilter dataRole2) {
//    this.dataRole2 = dataRole2;
  }

  public StringFilter getSecuLayer() {
    return secuLayer;
  }

  public Optional<StringFilter> optionalSecuLayer() {
    return Optional.ofNullable(secuLayer);
  }

  public StringFilter secuLayer() {
    if (secuLayer == null) {
      setSecuLayer(new StringFilter());
    }
    return secuLayer;
  }

  public void setSecuLayer(StringFilter secuLayer) {
    this.secuLayer = secuLayer;
  }

  public InstantFilter getExpirationTime() {
    return expirationTime;
  }

  public Optional<InstantFilter> optionalExpirationTime() {
    return Optional.ofNullable(expirationTime);
  }

  public InstantFilter expirationTime() {
    if (expirationTime == null) {
      setExpirationTime(new InstantFilter());
    }
    return expirationTime;
  }

  public void setExpirationTime(InstantFilter expirationTime) {
    this.expirationTime = expirationTime;
  }

  public InstantFilter getCreateTime() {
    return createTime;
  }

  public Optional<InstantFilter> optionalCreateTime() {
    return Optional.ofNullable(createTime);
  }

  public InstantFilter createTime() {
    if (createTime == null) {
      setCreateTime(new InstantFilter());
    }
    return createTime;
  }

  public void setCreateTime(InstantFilter createTime) {
    this.createTime = createTime;
  }

  public Boolean getDistinct() {
    return distinct;
  }

  public Optional<Boolean> optionalDistinct() {
    return Optional.ofNullable(distinct);
  }

  public Boolean distinct() {
    if (distinct == null) {
      setDistinct(true);
    }
    return distinct;
  }

  public void setDistinct(Boolean distinct) {
    this.distinct = distinct;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final AccessTokenCriteria that = (AccessTokenCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(accessToken, that.accessToken) &&
      Objects.equals(accessTokenName, that.accessTokenName) &&
      Objects.equals(owner, that.owner) &&
      Objects.equals(ownerName, that.ownerName) &&
      Objects.equals(orgId, that.orgId) &&
      Objects.equals(orgName, that.orgName) &&
      Objects.equals(state, that.state) &&
      Objects.equals(actype, that.actype) &&
      Objects.equals(dataRole1, that.dataRole1) &&
      Objects.equals(dataRole2, that.dataRole2) &&
      Objects.equals(secuLayer, that.secuLayer) &&
      Objects.equals(expirationTime, that.expirationTime) &&
      Objects.equals(createTime, that.createTime) &&
      Objects.equals(distinct, that.distinct)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      accessToken,
      accessTokenName,
      owner,
      ownerName,
      orgId,
      orgName,
      state,
      actype,
      dataRole1,
      dataRole2,
      secuLayer,
      expirationTime,
      createTime,
      distinct
    );
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "AccessTokenCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAccessToken().map(f -> "accessToken=" + f + ", ").orElse("") +
            optionalAccessTokenName().map(f -> "accessTokenName=" + f + ", ").orElse("") +
            optionalOwner().map(f -> "owner=" + f + ", ").orElse("") +
            optionalOwnerName().map(f -> "ownerName=" + f + ", ").orElse("") +
            optionalOrgId().map(f -> "orgId=" + f + ", ").orElse("") +
            optionalOrgName().map(f -> "orgName=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalActype().map(f -> "actype=" + f + ", ").orElse("") +
            optionalDataRole1().map(f -> "dataRole1=" + f + ", ").orElse("") +
            optionalDataRole2().map(f -> "dataRole2=" + f + ", ").orElse("") +
            optionalSecuLayer().map(f -> "secuLayer=" + f + ", ").orElse("") +
            optionalExpirationTime().map(f -> "expirationTime=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
