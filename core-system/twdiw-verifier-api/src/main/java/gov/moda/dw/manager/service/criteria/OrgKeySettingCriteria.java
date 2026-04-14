package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.OrgKeySetting} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.OrgKeySettingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /org-key-settings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrgKeySettingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter orgId;

    private StringFilter keyId;

    private StringFilter description;

    private BooleanFilter isActive;

    private InstantFilter crDatetime;

    private InstantFilter upDatetime;

    private Boolean distinct;

    public OrgKeySettingCriteria() {}

    public OrgKeySettingCriteria(OrgKeySettingCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orgId = other.optionalOrgId().map(StringFilter::copy).orElse(null);
        this.keyId = other.optionalKeyId().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.upDatetime = other.optionalUpDatetime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrgKeySettingCriteria copy() {
        return new OrgKeySettingCriteria(this);
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

    public StringFilter getKeyId() {
        return keyId;
    }

    public Optional<StringFilter> optionalKeyId() {
        return Optional.ofNullable(keyId);
    }

    public StringFilter keyId() {
        if (keyId == null) {
            setKeyId(new StringFilter());
        }
        return keyId;
    }

    public void setKeyId(StringFilter keyId) {
        this.keyId = keyId;
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

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
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
        final OrgKeySettingCriteria that = (OrgKeySettingCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orgId, that.orgId) &&
            Objects.equals(keyId, that.keyId) &&
            Objects.equals(description, that.description) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(upDatetime, that.upDatetime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orgId, keyId, description, isActive, crDatetime, upDatetime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgKeySettingCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrgId().map(f -> "orgId=" + f + ", ").orElse("") +
            optionalKeyId().map(f -> "keyId=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("") +
            optionalUpDatetime().map(f -> "upDatetime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
