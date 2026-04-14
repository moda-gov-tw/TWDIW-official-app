package gov.moda.dw.manager.service.criteria;

import gov.moda.dw.manager.web.rest.custom.DwSandBoxVC101WResource;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.Field} entity. This class is used
 * in {@link DwSandBoxVC101WResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /fields?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FieldCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter cname;

    private StringFilter ename;

    private StringFilter type;

    private BooleanFilter visible;

    private StringFilter businessId;

    private Boolean distinct;

    private LongFilter regularExpressionId;

    public FieldCriteria() {}

    public FieldCriteria(FieldCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.cname = other.cname == null ? null : other.cname.copy();
        this.ename = other.ename == null ? null : other.ename.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.visible = other.visible == null ? null : other.visible.copy();
        this.businessId = other.businessId == null ? null : other.businessId.copy();
        this.distinct = other.distinct;
        this.regularExpressionId = other.regularExpressionId == null ? null : other.regularExpressionId.copy();
    }

    @Override
    public FieldCriteria copy() {
        return new FieldCriteria(this);
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

    public BooleanFilter getVisible() {
        return visible;
    }

    public Optional<BooleanFilter> optionalVisible() {
        return Optional.ofNullable(visible);
    }

    public BooleanFilter visible() {
        if (visible == null) {
            setVisible(new BooleanFilter());
        }
        return visible;
    }

    public void setVisible(BooleanFilter visible) {
        this.visible = visible;
    }

    public StringFilter getBusinessId() {
        return businessId;
    }

    public Optional<StringFilter> optionalBusinessId() {
        return Optional.ofNullable(businessId);
    }

    public StringFilter businessId() {
        if (businessId == null) {
            setBusinessId(new StringFilter());
        }
        return businessId;
    }

    public void setBusinessId(StringFilter businessId) {
        this.businessId = businessId;
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
        final FieldCriteria that = (FieldCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cname, that.cname) &&
            Objects.equals(ename, that.ename) &&
            Objects.equals(type, that.type) &&
            Objects.equals(visible, that.visible) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cname, ename, type, visible, businessId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FieldCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCname().map(f -> "cname=" + f + ", ").orElse("") +
            optionalEname().map(f -> "ename=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalVisible().map(f -> "visible=" + f + ", ").orElse("") +
            optionalBusinessId().map(f -> "businessId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            (regularExpressionId != null ? "regularExpressionId=" + regularExpressionId + ", " : "") +
        "}";
    }
}
