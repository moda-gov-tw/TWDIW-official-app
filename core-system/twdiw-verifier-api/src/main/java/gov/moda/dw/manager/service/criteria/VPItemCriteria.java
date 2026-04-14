package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.VPItem} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.VPItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vp-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter serialNo;

    private StringFilter name;

    private LongFilter crUser;

    private InstantFilter crDatetime;

    private StringFilter businessId;

    private StringFilter purpose;

    private LongFilter upUser;

    private InstantFilter upDatetime;

    private BooleanFilter isStatic;

    private BooleanFilter isOffline;

    private StringFilter tag;

    private BooleanFilter isEncryptEnabled;

    private Boolean distinct;

    public VPItemCriteria() {}

    public VPItemCriteria(VPItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.serialNo = other.optionalSerialNo().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.crUser = other.optionalCrUser().map(LongFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.businessId = other.optionalBusinessId().map(StringFilter::copy).orElse(null);
        this.purpose = other.optionalPurpose().map(StringFilter::copy).orElse(null);
        this.upUser = other.optionalUpUser().map(LongFilter::copy).orElse(null);
        this.upDatetime = other.optionalUpDatetime().map(InstantFilter::copy).orElse(null);
        this.isStatic = other.optionalIsStatic().map(BooleanFilter::copy).orElse(null);
        this.isOffline = other.optionalIsOffline().map(BooleanFilter::copy).orElse(null);
        this.tag = other.optionalTag().map(StringFilter::copy).orElse(null);
        this.isEncryptEnabled = other.optionalIsEncryptEnabled().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VPItemCriteria copy() {
        return new VPItemCriteria(this);
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

    public StringFilter getPurpose() {
        return purpose;
    }

    public Optional<StringFilter> optionalPurpose() {
        return Optional.ofNullable(purpose);
    }

    public StringFilter purpose() {
        if (purpose == null) {
            setPurpose(new StringFilter());
        }
        return purpose;
    }

    public void setPurpose(StringFilter purpose) {
        this.purpose = purpose;
    }

    public LongFilter getUpUser() {
        return upUser;
    }

    public Optional<LongFilter> optionalUpUser() {
        return Optional.ofNullable(upUser);
    }

    public LongFilter upUser() {
        if (upUser == null) {
            setUpUser(new LongFilter());
        }
        return upUser;
    }

    public void setUpUser(LongFilter upUser) {
        this.upUser = upUser;
    }

    public InstantFilter getUpDatetime() {
        return upDatetime;
    }

    public Optional<InstantFilter> optionalUpDatetime() {
        return Optional.ofNullable(upDatetime);
    }

    public InstantFilter upDatetime() {
        if (upDatetime == null) {
            setUpDatetime(new InstantFilter());
        }
        return upDatetime;
    }

    public void setUpDatetime(InstantFilter upDatetime) {
        this.upDatetime = upDatetime;
    }

    public BooleanFilter getIsStatic() {
        return isStatic;
    }

    public Optional<BooleanFilter> optionalIsStatic() {
        return Optional.ofNullable(isStatic);
    }

    public BooleanFilter isStatic() {
        if (isStatic == null) {
            setIsStatic(new BooleanFilter());
        }
        return isStatic;
    }

    public void setIsStatic(BooleanFilter isStatic) {
        this.isStatic = isStatic;
    }

    public BooleanFilter getIsOffline() {
        return isOffline;
    }

    public Optional<BooleanFilter> optionalIsOffline() {
        return Optional.ofNullable(isOffline);
    }

    public BooleanFilter isOffline() {
        if (isOffline == null) {
            setIsOffline(new BooleanFilter());
        }
        return isOffline;
    }

    public void setIsOffline(BooleanFilter isOffline) {
        this.isOffline = isOffline;
    }

    public StringFilter getTag() {
        return tag;
    }

    public Optional<StringFilter> optionalTag() {
        return Optional.ofNullable(tag);
    }

    public StringFilter tag() {
        if (tag == null) {
            setTag(new StringFilter());
        }
        return tag;
    }

    public void setTag(StringFilter tag) {
        this.tag = tag;
    }

    public BooleanFilter getIsEncryptEnabled() {
        return isEncryptEnabled;
    }

    public Optional<BooleanFilter> optionalIsEncryptEnabled() {
        return Optional.ofNullable(isEncryptEnabled);
    }

    public BooleanFilter isEncryptEnabled() {
        if (isEncryptEnabled == null) {
            setIsEncryptEnabled(new BooleanFilter());
        }
        return isEncryptEnabled;
    }

    public void setIsEncryptEnabled(BooleanFilter isEncryptEnabled) {
        this.isEncryptEnabled = isEncryptEnabled;
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
        final VPItemCriteria that = (VPItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(serialNo, that.serialNo) &&
            Objects.equals(name, that.name) &&
            Objects.equals(crUser, that.crUser) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(purpose, that.purpose) &&
            Objects.equals(upUser, that.upUser) &&
            Objects.equals(upDatetime, that.upDatetime) &&
            Objects.equals(isStatic, that.isStatic) &&
            Objects.equals(isOffline, that.isOffline) &&
            Objects.equals(tag, that.tag) &&
            Objects.equals(isEncryptEnabled, that.isEncryptEnabled) &&
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
            businessId,
            purpose,
            upUser,
            upDatetime,
            isStatic,
            isOffline,
            tag,
            isEncryptEnabled,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSerialNo().map(f -> "serialNo=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCrUser().map(f -> "crUser=" + f + ", ").orElse("") +
            optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("") +
            optionalBusinessId().map(f -> "businessId=" + f + ", ").orElse("") +
            optionalPurpose().map(f -> "purpose=" + f + ", ").orElse("") +
            optionalUpUser().map(f -> "upUser=" + f + ", ").orElse("") +
            optionalUpDatetime().map(f -> "upDatetime=" + f + ", ").orElse("") +
            optionalIsStatic().map(f -> "isStatic=" + f + ", ").orElse("") +
            optionalIsOffline().map(f -> "isOffline=" + f + ", ").orElse("") +
            optionalTag().map(f -> "tag=" + f + ", ").orElse("") +
            optionalIsEncryptEnabled().map(f -> "isEncryptEnabled=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
