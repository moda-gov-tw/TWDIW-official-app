package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.VPItemField} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.VPItemFieldResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vp-item-fields?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemFieldCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter vpItemId;

    private StringFilter vcCategoryDescription;

    private StringFilter vcName;

    private StringFilter vcItemFieldType;

    private StringFilter cname;

    private StringFilter ename;

    private StringFilter vcSerialNo;

    private StringFilter vcBusinessId;

    private StringFilter vpBusinessId;

    private BooleanFilter isRequired;

    private Boolean distinct;

    public VPItemFieldCriteria() {}

    public VPItemFieldCriteria(VPItemFieldCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.vpItemId = other.optionalVpItemId().map(LongFilter::copy).orElse(null);
        this.vcCategoryDescription = other.optionalVcCategoryDescription().map(StringFilter::copy).orElse(null);
        this.vcName = other.optionalVcName().map(StringFilter::copy).orElse(null);
        this.vcItemFieldType = other.optionalVcItemFieldType().map(StringFilter::copy).orElse(null);
        this.cname = other.optionalCname().map(StringFilter::copy).orElse(null);
        this.ename = other.optionalEname().map(StringFilter::copy).orElse(null);
        this.vcSerialNo = other.optionalVcSerialNo().map(StringFilter::copy).orElse(null);
        this.vcBusinessId = other.optionalVcBusinessId().map(StringFilter::copy).orElse(null);
        this.vpBusinessId = other.optionalVpBusinessId().map(StringFilter::copy).orElse(null);
        this.isRequired = other.optionalIsRequired().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VPItemFieldCriteria copy() {
        return new VPItemFieldCriteria(this);
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

    public LongFilter getVpItemId() {
        return vpItemId;
    }

    public Optional<LongFilter> optionalVpItemId() {
        return Optional.ofNullable(vpItemId);
    }

    public LongFilter vpItemId() {
        if (vpItemId == null) {
            setVpItemId(new LongFilter());
        }
        return vpItemId;
    }

    public void setVpItemId(LongFilter vpItemId) {
        this.vpItemId = vpItemId;
    }

    public StringFilter getVcCategoryDescription() {
        return vcCategoryDescription;
    }

    public Optional<StringFilter> optionalVcCategoryDescription() {
        return Optional.ofNullable(vcCategoryDescription);
    }

    public StringFilter vcCategoryDescription() {
        if (vcCategoryDescription == null) {
            setVcCategoryDescription(new StringFilter());
        }
        return vcCategoryDescription;
    }

    public void setVcCategoryDescription(StringFilter vcCategoryDescription) {
        this.vcCategoryDescription = vcCategoryDescription;
    }

    public StringFilter getVcName() {
        return vcName;
    }

    public Optional<StringFilter> optionalVcName() {
        return Optional.ofNullable(vcName);
    }

    public StringFilter vcName() {
        if (vcName == null) {
            setVcName(new StringFilter());
        }
        return vcName;
    }

    public void setVcName(StringFilter vcName) {
        this.vcName = vcName;
    }

    public StringFilter getVcItemFieldType() {
        return vcItemFieldType;
    }

    public Optional<StringFilter> optionalVcItemFieldType() {
        return Optional.ofNullable(vcItemFieldType);
    }

    public StringFilter vcItemFieldType() {
        if (vcItemFieldType == null) {
            setVcItemFieldType(new StringFilter());
        }
        return vcItemFieldType;
    }

    public void setVcItemFieldType(StringFilter vcItemFieldType) {
        this.vcItemFieldType = vcItemFieldType;
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

    public StringFilter getVcSerialNo() {
        return vcSerialNo;
    }

    public Optional<StringFilter> optionalVcSerialNo() {
        return Optional.ofNullable(vcSerialNo);
    }

    public StringFilter vcSerialNo() {
        if (vcSerialNo == null) {
            setVcSerialNo(new StringFilter());
        }
        return vcSerialNo;
    }

    public void setVcSerialNo(StringFilter vcSerialNo) {
        this.vcSerialNo = vcSerialNo;
    }

    public StringFilter getVcBusinessId() {
        return vcBusinessId;
    }

    public Optional<StringFilter> optionalVcBusinessId() {
        return Optional.ofNullable(vcBusinessId);
    }

    public StringFilter vcBusinessId() {
        if (vcBusinessId == null) {
            setVcBusinessId(new StringFilter());
        }
        return vcBusinessId;
    }

    public void setVcBusinessId(StringFilter vcBusinessId) {
        this.vcBusinessId = vcBusinessId;
    }

    public StringFilter getVpBusinessId() {
        return vpBusinessId;
    }

    public Optional<StringFilter> optionalVpBusinessId() {
        return Optional.ofNullable(vpBusinessId);
    }

    public StringFilter vpBusinessId() {
        if (vpBusinessId == null) {
            setVpBusinessId(new StringFilter());
        }
        return vpBusinessId;
    }

    public void setVpBusinessId(StringFilter vpBusinessId) {
        this.vpBusinessId = vpBusinessId;
    }

    public BooleanFilter getIsRequired() {
        return isRequired;
    }

    public Optional<BooleanFilter> optionalIsRequired() {
        return Optional.ofNullable(isRequired);
    }

    public BooleanFilter isRequired() {
        if (isRequired == null) {
            setIsRequired(new BooleanFilter());
        }
        return isRequired;
    }

    public void setIsRequired(BooleanFilter isRequired) {
        this.isRequired = isRequired;
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
        final VPItemFieldCriteria that = (VPItemFieldCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(vpItemId, that.vpItemId) &&
            Objects.equals(vcCategoryDescription, that.vcCategoryDescription) &&
            Objects.equals(vcName, that.vcName) &&
            Objects.equals(vcItemFieldType, that.vcItemFieldType) &&
            Objects.equals(cname, that.cname) &&
            Objects.equals(ename, that.ename) &&
            Objects.equals(vcSerialNo, that.vcSerialNo) &&
            Objects.equals(vcBusinessId, that.vcBusinessId) &&
            Objects.equals(vpBusinessId, that.vpBusinessId) &&
            Objects.equals(isRequired, that.isRequired) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            vpItemId,
            vcCategoryDescription,
            vcName,
            vcItemFieldType,
            cname,
            ename,
            vcSerialNo,
            vcBusinessId,
            vpBusinessId,
            isRequired,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPItemFieldCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalVpItemId().map(f -> "vpItemId=" + f + ", ").orElse("") +
            optionalVcCategoryDescription().map(f -> "vcCategoryDescription=" + f + ", ").orElse("") +
            optionalVcName().map(f -> "vcName=" + f + ", ").orElse("") +
            optionalVcItemFieldType().map(f -> "vcItemFieldType=" + f + ", ").orElse("") +
            optionalCname().map(f -> "cname=" + f + ", ").orElse("") +
            optionalEname().map(f -> "ename=" + f + ", ").orElse("") +
            optionalVcSerialNo().map(f -> "vcSerialNo=" + f + ", ").orElse("") +
            optionalVcBusinessId().map(f -> "vcBusinessId=" + f + ", ").orElse("") +
            optionalVpBusinessId().map(f -> "vpBusinessId=" + f + ", ").orElse("") +
            optionalIsRequired().map(f -> "isRequired=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
