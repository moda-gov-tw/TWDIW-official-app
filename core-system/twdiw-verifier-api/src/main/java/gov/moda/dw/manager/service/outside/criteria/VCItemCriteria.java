package gov.moda.dw.manager.service.outside.criteria;

import gov.moda.dw.manager.domain.outside.VcManagerVCItem;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Criteria class for the {@link VcManagerVCItem} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.outside.VCItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vc-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter serialNo;

    private StringFilter name;

    private LongFilter crUser;

    private InstantFilter crDatetime;

    private LongFilter categoryId;

    private StringFilter businessId;

    private StringFilter schemaId;

    private StringFilter unitTypeExpire;

    private IntegerFilter lengthExpire;

    private Boolean distinct;

    public VCItemCriteria() {}

    public VCItemCriteria(VCItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.serialNo = other.optionalSerialNo().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.crUser = other.optionalCrUser().map(LongFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.businessId = other.optionalBusinessId().map(StringFilter::copy).orElse(null);
        this.schemaId = other.optionalSchemaId().map(StringFilter::copy).orElse(null);
        this.unitTypeExpire = other.optionalUnitTypeExpire().map(StringFilter::copy).orElse(null);
        this.lengthExpire = other.optionalLengthExpire().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VCItemCriteria copy() {
        return new VCItemCriteria(this);
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

    public StringFilter getSerialNo() {
        return serialNo;
    }

    public Optional<StringFilter> optionalSerialNo() {
        return Optional.ofNullable(serialNo);
    }

    public StringFilter serialNo() {
        if (serialNo == null) {
            setSerialNo(new StringFilter());
        }
        return serialNo;
    }

    public void setSerialNo(StringFilter serialNo) {
        this.serialNo = serialNo;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getCrUser() {
        return crUser;
    }

    public Optional<LongFilter> optionalCrUser() {
        return Optional.ofNullable(crUser);
    }

    public LongFilter crUser() {
        if (crUser == null) {
            setCrUser(new LongFilter());
        }
        return crUser;
    }

    public void setCrUser(LongFilter crUser) {
        this.crUser = crUser;
    }

    public InstantFilter getCrDatetime() {
        return crDatetime;
    }

    public Optional<InstantFilter> optionalCrDatetime() {
        return Optional.ofNullable(crDatetime);
    }

    public InstantFilter crDatetime() {
        if (crDatetime == null) {
            setCrDatetime(new InstantFilter());
        }
        return crDatetime;
    }

    public void setCrDatetime(InstantFilter crDatetime) {
        this.crDatetime = crDatetime;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
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

    public StringFilter getSchemaId() {
        return schemaId;
    }

    public Optional<StringFilter> optionalSchemaId() {
        return Optional.ofNullable(schemaId);
    }

    public StringFilter schemaId() {
        if (schemaId == null) {
            setSchemaId(new StringFilter());
        }
        return schemaId;
    }

    public void setSchemaId(StringFilter schemaId) {
        this.schemaId = schemaId;
    }

    public StringFilter getUnitTypeExpire() {
        return unitTypeExpire;
    }

    public Optional<StringFilter> optionalUnitTypeExpire() {
        return Optional.ofNullable(unitTypeExpire);
    }

    public StringFilter unitTypeExpire() {
        if (unitTypeExpire == null) {
            setUnitTypeExpire(new StringFilter());
        }
        return unitTypeExpire;
    }

    public void setUnitTypeExpire(StringFilter unitTypeExpire) {
        this.unitTypeExpire = unitTypeExpire;
    }

    public IntegerFilter getLengthExpire() {
        return lengthExpire;
    }

    public Optional<IntegerFilter> optionalLengthExpire() {
        return Optional.ofNullable(lengthExpire);
    }

    public IntegerFilter lengthExpire() {
        if (lengthExpire == null) {
            setLengthExpire(new IntegerFilter());
        }
        return lengthExpire;
    }

    public void setLengthExpire(IntegerFilter lengthExpire) {
        this.lengthExpire = lengthExpire;
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
        final VCItemCriteria that = (VCItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(serialNo, that.serialNo) &&
            Objects.equals(name, that.name) &&
            Objects.equals(crUser, that.crUser) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(schemaId, that.schemaId) &&
            Objects.equals(unitTypeExpire, that.unitTypeExpire) &&
            Objects.equals(lengthExpire, that.lengthExpire) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            serialNo,
            name,
            crUser,
            crDatetime,
            categoryId,
            businessId,
            schemaId,
            unitTypeExpire,
            lengthExpire,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSerialNo().map(f -> "serialNo=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCrUser().map(f -> "crUser=" + f + ", ").orElse("") +
            optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalBusinessId().map(f -> "businessId=" + f + ", ").orElse("") +
            optionalSchemaId().map(f -> "schemaId=" + f + ", ").orElse("") +
            optionalUnitTypeExpire().map(f -> "unitTypeExpire=" + f + ", ").orElse("") +
            optionalLengthExpire().map(f -> "lengthExpire=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
