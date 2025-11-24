package gov.moda.dw.issuer.oidvci.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import gov.moda.dw.issuer.oidvci.domain.Rel;
import gov.moda.dw.issuer.oidvci.web.rest.RelResource;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link Rel} entity. This class is used
 * in {@link RelResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rels?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter leftTbl;

  private LongFilter leftId;

  private StringFilter rightTbl;

  private LongFilter rightId;

  private StringFilter leftCode;

  private StringFilter rightCode;

  private StringFilter state;

  private StringFilter dataRole1;

  private StringFilter dataRole2;

  private StringFilter dataAuth;

  private InstantFilter createTime;

  private Boolean distinct;

  public RelCriteria() {}

  public RelCriteria(RelCriteria other) {
    this.id = other.optionalId().map(LongFilter::copy).orElse(null);
    this.leftTbl = other.optionalLeftTbl().map(StringFilter::copy).orElse(null);
    this.leftId = other.optionalLeftId().map(LongFilter::copy).orElse(null);
    this.rightTbl = other.optionalRightTbl().map(StringFilter::copy).orElse(null);
    this.rightId = other.optionalRightId().map(LongFilter::copy).orElse(null);
    this.leftCode = other.optionalLeftCode().map(StringFilter::copy).orElse(null);
    this.rightCode = other.optionalRightCode().map(StringFilter::copy).orElse(null);
    this.state = other.optionalState().map(StringFilter::copy).orElse(null);
    this.dataRole1 = other.optionalDataRole1().map(StringFilter::copy).orElse(null);
    this.dataRole2 = other.optionalDataRole2().map(StringFilter::copy).orElse(null);
    this.dataAuth = other.optionalDataAuth().map(StringFilter::copy).orElse(null);
    this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
    this.distinct = other.distinct;
  }

  @Override
  public RelCriteria copy() {
    return new RelCriteria(this);
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

  public StringFilter getLeftTbl() {
    return leftTbl;
  }

  public Optional<StringFilter> optionalLeftTbl() {
    return Optional.ofNullable(leftTbl);
  }

  public StringFilter leftTbl() {
    if (leftTbl == null) {
      setLeftTbl(new StringFilter());
    }
    return leftTbl;
  }

  public void setLeftTbl(StringFilter leftTbl) {
    this.leftTbl = leftTbl;
  }

  public LongFilter getLeftId() {
    return leftId;
  }

  public Optional<LongFilter> optionalLeftId() {
    return Optional.ofNullable(leftId);
  }

  public LongFilter leftId() {
    if (leftId == null) {
      setLeftId(new LongFilter());
    }
    return leftId;
  }

  public void setLeftId(LongFilter leftId) {
    this.leftId = leftId;
  }

  public StringFilter getRightTbl() {
    return rightTbl;
  }

  public Optional<StringFilter> optionalRightTbl() {
    return Optional.ofNullable(rightTbl);
  }

  public StringFilter rightTbl() {
    if (rightTbl == null) {
      setRightTbl(new StringFilter());
    }
    return rightTbl;
  }

  public void setRightTbl(StringFilter rightTbl) {
    this.rightTbl = rightTbl;
  }

  public LongFilter getRightId() {
    return rightId;
  }

  public Optional<LongFilter> optionalRightId() {
    return Optional.ofNullable(rightId);
  }

  public LongFilter rightId() {
    if (rightId == null) {
      setRightId(new LongFilter());
    }
    return rightId;
  }

  public void setRightId(LongFilter rightId) {
    this.rightId = rightId;
  }

  public StringFilter getLeftCode() {
    return leftCode;
  }

  public Optional<StringFilter> optionalLeftCode() {
    return Optional.ofNullable(leftCode);
  }

  public StringFilter leftCode() {
    if (leftCode == null) {
      setLeftCode(new StringFilter());
    }
    return leftCode;
  }

  public void setLeftCode(StringFilter leftCode) {
    this.leftCode = leftCode;
  }

  public StringFilter getRightCode() {
    return rightCode;
  }

  public Optional<StringFilter> optionalRightCode() {
    return Optional.ofNullable(rightCode);
  }

  public StringFilter rightCode() {
    if (rightCode == null) {
      setRightCode(new StringFilter());
    }
    return rightCode;
  }

  public void setRightCode(StringFilter rightCode) {
    this.rightCode = rightCode;
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

  public StringFilter getDataRole1() {
    return dataRole1;
  }

  public Optional<StringFilter> optionalDataRole1() {
    return Optional.ofNullable(dataRole1);
  }

  public StringFilter dataRole1() {
    if (dataRole1 == null) {
      setDataRole1(new StringFilter());
    }
    return dataRole1;
  }

  public void setDataRole1(StringFilter dataRole1) {
    this.dataRole1 = dataRole1;
  }

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

  public StringFilter getDataAuth() {
    return dataAuth;
  }

  public Optional<StringFilter> optionalDataAuth() {
    return Optional.ofNullable(dataAuth);
  }

  public StringFilter dataAuth() {
    if (dataAuth == null) {
      setDataAuth(new StringFilter());
    }
    return dataAuth;
  }

  public void setDataAuth(StringFilter dataAuth) {
    this.dataAuth = dataAuth;
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
    final RelCriteria that = (RelCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(leftTbl, that.leftTbl) &&
      Objects.equals(leftId, that.leftId) &&
      Objects.equals(rightTbl, that.rightTbl) &&
      Objects.equals(rightId, that.rightId) &&
      Objects.equals(leftCode, that.leftCode) &&
      Objects.equals(rightCode, that.rightCode) &&
      Objects.equals(state, that.state) &&
      Objects.equals(dataRole1, that.dataRole1) &&
      Objects.equals(dataRole2, that.dataRole2) &&
      Objects.equals(dataAuth, that.dataAuth) &&
      Objects.equals(createTime, that.createTime) &&
      Objects.equals(distinct, that.distinct)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      leftTbl,
      leftId,
      rightTbl,
      rightId,
      leftCode,
      rightCode,
      state,
      dataRole1,
      dataRole2,
      dataAuth,
      createTime,
      distinct
    );
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "RelCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLeftTbl().map(f -> "leftTbl=" + f + ", ").orElse("") +
            optionalLeftId().map(f -> "leftId=" + f + ", ").orElse("") +
            optionalRightTbl().map(f -> "rightTbl=" + f + ", ").orElse("") +
            optionalRightId().map(f -> "rightId=" + f + ", ").orElse("") +
            optionalLeftCode().map(f -> "leftCode=" + f + ", ").orElse("") +
            optionalRightCode().map(f -> "rightCode=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalDataRole1().map(f -> "dataRole1=" + f + ", ").orElse("") +
            optionalDataRole2().map(f -> "dataRole2=" + f + ", ").orElse("") +
            optionalDataAuth().map(f -> "dataAuth=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
