package gov.moda.dw.issuer.oidvci.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import gov.moda.dw.issuer.oidvci.domain.Res;
import gov.moda.dw.issuer.oidvci.web.rest.ResResource;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link Res} entity. This class is used
 * in {@link ResResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /res?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter typeId;

  private StringFilter resId;

  private StringFilter resGrp;

  private StringFilter resName;

  private StringFilter description;

  private StringFilter state;

  private StringFilter apiUri;

  private StringFilter webUrl;

  private StringFilter dataRole1;

  private StringFilter dataRole2;

  private InstantFilter createTime;

  private Boolean distinct;

  public ResCriteria() {}

  public ResCriteria(ResCriteria other) {
    this.id = other.optionalId().map(LongFilter::copy).orElse(null);
    this.typeId = other.optionalTypeId().map(StringFilter::copy).orElse(null);
    this.resId = other.optionalResId().map(StringFilter::copy).orElse(null);
    this.resGrp = other.optionalResGrp().map(StringFilter::copy).orElse(null);
    this.resName = other.optionalResName().map(StringFilter::copy).orElse(null);
    this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
    this.state = other.optionalState().map(StringFilter::copy).orElse(null);
    this.apiUri = other.optionalApiUri().map(StringFilter::copy).orElse(null);
    this.webUrl = other.optionalWebUrl().map(StringFilter::copy).orElse(null);
    this.dataRole1 = other.optionalDataRole1().map(StringFilter::copy).orElse(null);
    this.dataRole2 = other.optionalDataRole2().map(StringFilter::copy).orElse(null);
    this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
    this.distinct = other.distinct;
  }

  @Override
  public ResCriteria copy() {
    return new ResCriteria(this);
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

  public StringFilter getTypeId() {
    return typeId;
  }

  public Optional<StringFilter> optionalTypeId() {
    return Optional.ofNullable(typeId);
  }

  public StringFilter typeId() {
    if (typeId == null) {
      setTypeId(new StringFilter());
    }
    return typeId;
  }

  public void setTypeId(StringFilter typeId) {
    this.typeId = typeId;
  }

  public StringFilter getResId() {
    return resId;
  }

  public Optional<StringFilter> optionalResId() {
    return Optional.ofNullable(resId);
  }

  public StringFilter resId() {
    if (resId == null) {
      setResId(new StringFilter());
    }
    return resId;
  }

  public void setResId(StringFilter resId) {
    this.resId = resId;
  }

  public StringFilter getResGrp() {
    return resGrp;
  }

  public Optional<StringFilter> optionalResGrp() {
    return Optional.ofNullable(resGrp);
  }

  public StringFilter resGrp() {
    if (resGrp == null) {
      setResGrp(new StringFilter());
    }
    return resGrp;
  }

  public void setResGrp(StringFilter resGrp) {
    this.resGrp = resGrp;
  }

  public StringFilter getResName() {
    return resName;
  }

  public Optional<StringFilter> optionalResName() {
    return Optional.ofNullable(resName);
  }

  public StringFilter resName() {
    if (resName == null) {
      setResName(new StringFilter());
    }
    return resName;
  }

  public void setResName(StringFilter resName) {
    this.resName = resName;
  }

  public StringFilter getDescription() {
    return description;
  }

  public Optional<StringFilter> optionalDescription() {
    return Optional.ofNullable(description);
  }

  public StringFilter description() {
    if (description == null) {
      setDescription(new StringFilter());
    }
    return description;
  }

  public void setDescription(StringFilter description) {
    this.description = description;
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

  public StringFilter getApiUri() {
    return apiUri;
  }

  public Optional<StringFilter> optionalApiUri() {
    return Optional.ofNullable(apiUri);
  }

  public StringFilter apiUri() {
    if (apiUri == null) {
      setApiUri(new StringFilter());
    }
    return apiUri;
  }

  public void setApiUri(StringFilter apiUri) {
    this.apiUri = apiUri;
  }

  public StringFilter getWebUrl() {
    return webUrl;
  }

  public Optional<StringFilter> optionalWebUrl() {
    return Optional.ofNullable(webUrl);
  }

  public StringFilter webUrl() {
    if (webUrl == null) {
      setWebUrl(new StringFilter());
    }
    return webUrl;
  }

  public void setWebUrl(StringFilter webUrl) {
    this.webUrl = webUrl;
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
//    this.dataRole1 = dataRole1;
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
    this.dataRole2 = dataRole2;
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
    final ResCriteria that = (ResCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(typeId, that.typeId) &&
      Objects.equals(resId, that.resId) &&
      Objects.equals(resGrp, that.resGrp) &&
      Objects.equals(resName, that.resName) &&
      Objects.equals(description, that.description) &&
      Objects.equals(state, that.state) &&
      Objects.equals(apiUri, that.apiUri) &&
      Objects.equals(webUrl, that.webUrl) &&
      Objects.equals(dataRole1, that.dataRole1) &&
      Objects.equals(dataRole2, that.dataRole2) &&
      Objects.equals(createTime, that.createTime) &&
      Objects.equals(distinct, that.distinct)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, typeId, resId, resGrp, resName, description, state, apiUri, webUrl, dataRole1, dataRole2, createTime, distinct);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ResCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTypeId().map(f -> "typeId=" + f + ", ").orElse("") +
            optionalResId().map(f -> "resId=" + f + ", ").orElse("") +
            optionalResGrp().map(f -> "resGrp=" + f + ", ").orElse("") +
            optionalResName().map(f -> "resName=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalApiUri().map(f -> "apiUri=" + f + ", ").orElse("") +
            optionalWebUrl().map(f -> "webUrl=" + f + ", ").orElse("") +
            optionalDataRole1().map(f -> "dataRole1=" + f + ", ").orElse("") +
            optionalDataRole2().map(f -> "dataRole2=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
