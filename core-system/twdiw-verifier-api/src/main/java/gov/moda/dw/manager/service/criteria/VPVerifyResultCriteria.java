package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.VPVerifyResult} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.VPVerifyResultResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vp-verify-results?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPVerifyResultCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter transactionId;

    private LongFilter vpItemId;

    private InstantFilter crDatetime;

    private InstantFilter verifyDatetime;

    private Boolean distinct;

    public VPVerifyResultCriteria() {}

    public VPVerifyResultCriteria(VPVerifyResultCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(StringFilter::copy).orElse(null);
        this.vpItemId = other.optionalVpItemId().map(LongFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.verifyDatetime = other.optionalVerifyDatetime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VPVerifyResultCriteria copy() {
        return new VPVerifyResultCriteria(this);
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

    public InstantFilter getVerifyDatetime() {
        return verifyDatetime;
    }

    public Optional<InstantFilter> optionalVerifyDatetime() {
        return Optional.ofNullable(verifyDatetime);
    }

    public InstantFilter verifyDatetime() {
        if (verifyDatetime == null) {
            setVerifyDatetime(new InstantFilter());
        }
        return verifyDatetime;
    }

    public void setVerifyDatetime(InstantFilter verifyDatetime) {
        this.verifyDatetime = verifyDatetime;
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
        final VPVerifyResultCriteria that = (VPVerifyResultCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(vpItemId, that.vpItemId) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(verifyDatetime, that.verifyDatetime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionId, vpItemId, crDatetime, verifyDatetime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPVerifyResultCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalVpItemId().map(f -> "vpItemId=" + f + ", ").orElse("") +
            optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("") +
            optionalVerifyDatetime().map(f -> "verifyDatetime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
