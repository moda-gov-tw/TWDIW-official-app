package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.VCDataStatusLog}
 * entity. This class is used in
 * {@link gov.moda.dw.manager.web.rest.VCDataStatusLogResource} to receive all
 * the possible filtering options from the Http GET request parameters. For
 * example the following could be a valid request:
 * {@code /vc-data-status-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCDataStatusLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter vcCid;

    private LongFilter vcItemId;

    private StringFilter transactionId;

    private IntegerFilter valid;

    private LongFilter crUser;

    private InstantFilter crDatetime;

    private InstantFilter lastUpdateTime;

    private Boolean distinct;

    public VCDataStatusLogCriteria() {
    }

    public VCDataStatusLogCriteria(VCDataStatusLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.vcCid = other.optionalVcCid().map(StringFilter::copy).orElse(null);
        this.vcItemId = other.optionalVcItemId().map(LongFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(StringFilter::copy).orElse(null);
        this.valid = other.optionalValid().map(IntegerFilter::copy).orElse(null);
        this.crUser = other.optionalCrUser().map(LongFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.lastUpdateTime = other.optionalLastUpdateTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VCDataStatusLogCriteria copy() {
        return new VCDataStatusLogCriteria(this);
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

    public InstantFilter getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Optional<InstantFilter> optionalLastUpdateTime() {
        return Optional.ofNullable(lastUpdateTime);
    }

    public InstantFilter lastUpdateTime() {
        if (lastUpdateTime == null) {
            setLastUpdateTime(new InstantFilter());
        }
        return lastUpdateTime;
    }

    public void setLastUpdateTime(InstantFilter lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
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
        final VCDataStatusLogCriteria that = (VCDataStatusLogCriteria) o;
        return (Objects.equals(id, that.id) && Objects.equals(vcCid, that.vcCid)
                && Objects.equals(vcItemId, that.vcItemId) && Objects.equals(transactionId, that.transactionId)
                && Objects.equals(valid, that.valid) && Objects.equals(crUser, that.crUser)
                && Objects.equals(crDatetime, that.crDatetime) && Objects.equals(lastUpdateTime, that.lastUpdateTime)
                && Objects.equals(distinct, that.distinct));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vcCid, vcItemId, transactionId, valid, crUser, crDatetime, lastUpdateTime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCDataStatusLogCriteria{" + optionalId().map(f -> "id=" + f + ", ").orElse("")
                + optionalVcCid().map(f -> "vcCid=" + f + ", ").orElse("")
                + optionalVcItemId().map(f -> "vcItemId=" + f + ", ").orElse("")
                + optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("")
                + optionalValid().map(f -> "valid=" + f + ", ").orElse("")
                + optionalCrUser().map(f -> "crUser=" + f + ", ").orElse("")
                + optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("")
                + optionalLastUpdateTime().map(f -> "lastUpdateTime=" + f + ", ").orElse("")
                + optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") + "}";
    }
}
