package gov.moda.dw.manager.service.criteria;

import gov.moda.dw.manager.web.rest.custom.DwSandBoxVC301WResource;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.VCItemData} entity. This class is used
 * in {@link DwSandBoxVC301WResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vc-item-data?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemDataCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter crUser;

    private InstantFilter crDatetime;

    private IntegerFilter valid;

    private LongFilter clearScheduleId;

    private InstantFilter clearScheduleDatetime;

    private StringFilter vcCid;

    private StringFilter transactionId;

    private StringFilter businessId;

    private StringFilter vcItemName;

    private InstantFilter expired;

    private StringFilter scheduleRevokeMessage;

    private StringFilter dataTag;

    private LongFilter vcItemId;

    private Boolean distinct;

    public String getVcSerialNo() {
        return vcSerialNo;
    }

    public void setVcSerialNo(String vcSerialNo) {
        this.vcSerialNo = vcSerialNo;
    }

    private String vcSerialNo;

    public VCItemDataCriteria() {}

    public VCItemDataCriteria(VCItemDataCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.crUser = other.optionalCrUser().map(LongFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.valid = other.optionalValid().map(IntegerFilter::copy).orElse(null);
        this.clearScheduleId = other.optionalClearScheduleId().map(LongFilter::copy).orElse(null);
        this.clearScheduleDatetime = other.optionalClearScheduleDatetime().map(InstantFilter::copy).orElse(null);
        this.vcCid = other.optionalVcCid().map(StringFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(StringFilter::copy).orElse(null);
        this.businessId = other.optionalBusinessId().map(StringFilter::copy).orElse(null);
        this.vcItemName = other.optionalVcItemName().map(StringFilter::copy).orElse(null);
        this.expired = other.optionalExpired().map(InstantFilter::copy).orElse(null);
        this.scheduleRevokeMessage = other.optionalScheduleRevokeMessage().map(StringFilter::copy).orElse(null);
        this.dataTag = other.optionalDataTag().map(StringFilter::copy).orElse(null);
        this.vcItemId = other.optionalVcItemId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
        this.vcSerialNo = other.vcSerialNo;
    }

    @Override
    public VCItemDataCriteria copy() {
        return new VCItemDataCriteria(this);
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

    public IntegerFilter getValid() {
        return valid;
    }

    public Optional<IntegerFilter> optionalValid() {
        return Optional.ofNullable(valid);
    }

    public IntegerFilter valid() {
        if (valid == null) {
            setValid(new IntegerFilter());
        }
        return valid;
    }

    public void setValid(IntegerFilter valid) {
        this.valid = valid;
    }

    public LongFilter getClearScheduleId() {
        return clearScheduleId;
    }

    public Optional<LongFilter> optionalClearScheduleId() {
        return Optional.ofNullable(clearScheduleId);
    }

    public LongFilter clearScheduleId() {
        if (clearScheduleId == null) {
            setClearScheduleId(new LongFilter());
        }
        return clearScheduleId;
    }

    public void setClearScheduleId(LongFilter clearScheduleId) {
        this.clearScheduleId = clearScheduleId;
    }

    public InstantFilter getClearScheduleDatetime() {
        return clearScheduleDatetime;
    }

    public Optional<InstantFilter> optionalClearScheduleDatetime() {
        return Optional.ofNullable(clearScheduleDatetime);
    }

    public InstantFilter clearScheduleDatetime() {
        if (clearScheduleDatetime == null) {
            setClearScheduleDatetime(new InstantFilter());
        }
        return clearScheduleDatetime;
    }

    public void setClearScheduleDatetime(InstantFilter clearScheduleDatetime) {
        this.clearScheduleDatetime = clearScheduleDatetime;
    }

    public StringFilter getVcCid() {
        return vcCid;
    }

    public Optional<StringFilter> optionalVcCid() {
        return Optional.ofNullable(vcCid);
    }

    public StringFilter vcCid() {
        if (vcCid == null) {
            setVcCid(new StringFilter());
        }
        return vcCid;
    }

    public void setVcCid(StringFilter vcCid) {
        this.vcCid = vcCid;
    }

    public StringFilter getTransactionId() {
        return transactionId;
    }

    public Optional<StringFilter> optionalTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public StringFilter transactionId() {
        if (transactionId == null) {
            setTransactionId(new StringFilter());
        }
        return transactionId;
    }

    public void setTransactionId(StringFilter transactionId) {
        this.transactionId = transactionId;
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

    public StringFilter getVcItemName() {
        return vcItemName;
    }

    public Optional<StringFilter> optionalVcItemName() {
        return Optional.ofNullable(vcItemName);
    }

    public StringFilter vcItemName() {
        if (vcItemName == null) {
            setVcItemName(new StringFilter());
        }
        return vcItemName;
    }

    public void setVcItemName(StringFilter vcItemName) {
        this.vcItemName = vcItemName;
    }

    public InstantFilter getExpired() {
        return expired;
    }

    public Optional<InstantFilter> optionalExpired() {
        return Optional.ofNullable(expired);
    }

    public InstantFilter expired() {
        if (expired == null) {
            setExpired(new InstantFilter());
        }
        return expired;
    }

    public void setExpired(InstantFilter expired) {
        this.expired = expired;
    }

    public StringFilter getScheduleRevokeMessage() {
        return scheduleRevokeMessage;
    }

    public Optional<StringFilter> optionalScheduleRevokeMessage() {
        return Optional.ofNullable(scheduleRevokeMessage);
    }

    public StringFilter scheduleRevokeMessage() {
        if (scheduleRevokeMessage == null) {
            setScheduleRevokeMessage(new StringFilter());
        }
        return scheduleRevokeMessage;
    }

    public void setScheduleRevokeMessage(StringFilter scheduleRevokeMessage) {
        this.scheduleRevokeMessage = scheduleRevokeMessage;
    }

    public StringFilter getDataTag() {
        return dataTag;
    }

    public Optional<StringFilter> optionalDataTag() {
        return Optional.ofNullable(dataTag);
    }

    public StringFilter dataTag() {
        if (dataTag == null) {
            setDataTag(new StringFilter());
        }
        return dataTag;
    }

    public void setDataTag(StringFilter dataTag) {
        this.dataTag = dataTag;
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
        final VCItemDataCriteria that = (VCItemDataCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(crUser, that.crUser) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(valid, that.valid) &&
            Objects.equals(clearScheduleId, that.clearScheduleId) &&
            Objects.equals(clearScheduleDatetime, that.clearScheduleDatetime) &&
            Objects.equals(vcCid, that.vcCid) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(vcItemName, that.vcItemName) &&
            Objects.equals(expired, that.expired) &&
            Objects.equals(scheduleRevokeMessage, that.scheduleRevokeMessage) &&
            Objects.equals(dataTag, that.dataTag) &&
            Objects.equals(vcItemId, that.vcItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            crUser,
            crDatetime,
            valid,
            clearScheduleId,
            clearScheduleDatetime,
            vcCid,
            transactionId,
            businessId,
            vcItemName,
            expired,
            scheduleRevokeMessage,
            dataTag,
            vcItemId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemDataCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCrUser().map(f -> "crUser=" + f + ", ").orElse("") +
            optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("") +
            optionalValid().map(f -> "valid=" + f + ", ").orElse("") +
            optionalClearScheduleId().map(f -> "clearScheduleId=" + f + ", ").orElse("") +
            optionalClearScheduleDatetime().map(f -> "clearScheduleDatetime=" + f + ", ").orElse("") +
            optionalVcCid().map(f -> "vcCid=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalBusinessId().map(f -> "businessId=" + f + ", ").orElse("") +
            optionalVcItemName().map(f -> "vcItemName=" + f + ", ").orElse("") +
            optionalExpired().map(f -> "expired=" + f + ", ").orElse("") +
            optionalScheduleRevokeMessage().map(f -> "scheduleRevokeMessage=" + f + ", ").orElse("") +
            optionalDataTag().map(f -> "dataTag=" + f + ", ").orElse("") +
            optionalVcItemId().map(f -> "vcItemId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
