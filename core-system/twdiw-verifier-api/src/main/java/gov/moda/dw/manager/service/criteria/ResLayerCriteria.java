package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResLayerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter parentId;

    private LongFilter childId;

    private StringFilter parentCode;

    private StringFilter childCode;

    private InstantFilter createTime;

    private StringFilter orderval;

    private Boolean distinct;

    public ResLayerCriteria() {}

    public ResLayerCriteria(ResLayerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.parentId = other.optionalParentId().map(LongFilter::copy).orElse(null);
        this.childId = other.optionalChildId().map(LongFilter::copy).orElse(null);
        this.parentCode = other.optionalParentCode().map(StringFilter::copy).orElse(null);
        this.childCode = other.optionalChildCode().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.orderval = other.optionalOrderval().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ResLayerCriteria copy() {
        return new ResLayerCriteria(this);
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

    public LongFilter getParentId() {
        return parentId;
    }

    public Optional<LongFilter> optionalParentId() {
        return Optional.ofNullable(parentId);
    }

    public LongFilter parentId() {
        if (parentId == null) {
            setParentId(new LongFilter());
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getChildId() {
        return childId;
    }

    public Optional<LongFilter> optionalChildId() {
        return Optional.ofNullable(childId);
    }

    public LongFilter childId() {
        if (childId == null) {
            setChildId(new LongFilter());
        }
        return childId;
    }

    public void setChildId(LongFilter childId) {
        this.childId = childId;
    }

    public StringFilter getParentCode() {
        return parentCode;
    }

    public Optional<StringFilter> optionalParentCode() {
        return Optional.ofNullable(parentCode);
    }

    public StringFilter parentCode() {
        if (parentCode == null) {
            setParentCode(new StringFilter());
        }
        return parentCode;
    }

    public void setParentCode(StringFilter parentCode) {
        this.parentCode = parentCode;
    }

    public StringFilter getChildCode() {
        return childCode;
    }

    public Optional<StringFilter> optionalChildCode() {
        return Optional.ofNullable(childCode);
    }

    public StringFilter childCode() {
        if (childCode == null) {
            setChildCode(new StringFilter());
        }
        return childCode;
    }

    public void setChildCode(StringFilter childCode) {
        this.childCode = childCode;
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

    public StringFilter getOrderval() {
        return orderval;
    }

    public Optional<StringFilter> optionalOrderval() {
        return Optional.ofNullable(orderval);
    }

    public StringFilter orderval() {
        if (orderval == null) {
            setOrderval(new StringFilter());
        }
        return orderval;
    }

    public void setOrderval(StringFilter orderval) {
        this.orderval = orderval;
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
        final ResLayerCriteria that = (ResLayerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(childId, that.childId) &&
            Objects.equals(parentCode, that.parentCode) &&
            Objects.equals(childCode, that.childCode) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(orderval, that.orderval) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, childId, parentCode, childCode, createTime, orderval, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResLayerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalParentId().map(f -> "parentId=" + f + ", ").orElse("") +
            optionalChildId().map(f -> "childId=" + f + ", ").orElse("") +
            optionalParentCode().map(f -> "parentCode=" + f + ", ").orElse("") +
            optionalChildCode().map(f -> "childCode=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalOrderval().map(f -> "orderval=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
