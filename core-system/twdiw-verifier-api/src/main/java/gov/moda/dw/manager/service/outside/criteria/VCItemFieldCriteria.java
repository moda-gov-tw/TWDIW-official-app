package gov.moda.dw.manager.service.outside.criteria;

import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.outside.VCItemField} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.outside.VCItemFieldResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vc-item-fields?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemFieldCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter vcItemId;

    private StringFilter type;

    private StringFilter cname;

    private StringFilter ename;

    private Boolean distinct;

    public VCItemFieldCriteria() {}

    public VCItemFieldCriteria(VCItemFieldCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.vcItemId = other.optionalVcItemId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.cname = other.optionalCname().map(StringFilter::copy).orElse(null);
        this.ename = other.optionalEname().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VCItemFieldCriteria copy() {
        return new VCItemFieldCriteria(this);
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

    public LongFilter getVcItemId() {
        return vcItemId;
    }

    public Optional<LongFilter> optionalVcItemId() {
        return Optional.ofNullable(vcItemId);
    }

    public LongFilter vcItemId() {
        if (vcItemId == null) {
            setVcItemId(new LongFilter());
        }
        return vcItemId;
    }

    public void setVcItemId(LongFilter vcItemId) {
        this.vcItemId = vcItemId;
    }

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getCname() {
        return cname;
    }

    public Optional<StringFilter> optionalCname() {
        return Optional.ofNullable(cname);
    }

    public StringFilter cname() {
        if (cname == null) {
            setCname(new StringFilter());
        }
        return cname;
    }

    public void setCname(StringFilter cname) {
        this.cname = cname;
    }

    public StringFilter getEname() {
        return ename;
    }

    public Optional<StringFilter> optionalEname() {
        return Optional.ofNullable(ename);
    }

    public StringFilter ename() {
        if (ename == null) {
            setEname(new StringFilter());
        }
        return ename;
    }

    public void setEname(StringFilter ename) {
        this.ename = ename;
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
        final VCItemFieldCriteria that = (VCItemFieldCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(vcItemId, that.vcItemId) &&
            Objects.equals(type, that.type) &&
            Objects.equals(cname, that.cname) &&
            Objects.equals(ename, that.ename) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vcItemId, type, cname, ename, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemFieldCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalVcItemId().map(f -> "vcItemId=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalCname().map(f -> "cname=" + f + ", ").orElse("") +
            optionalEname().map(f -> "ename=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
