package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.OtpVerify} entity.
 * This class is used in {@link gov.moda.dw.manager.web.rest.OtpVerifyResource}
 * to receive all the possible filtering options from the Http GET request
 * parameters. For example the following could be a valid request:
 * {@code /otp-verifies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OtpVerifyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter email;

    private StringFilter otpToken;

    private InstantFilter otpTokenExpired;

    private BooleanFilter isPass;

    private InstantFilter createTime;

    private Boolean distinct;

    public OtpVerifyCriteria() {
    }

    public OtpVerifyCriteria(OtpVerifyCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.otpToken = other.optionalOtpToken().map(StringFilter::copy).orElse(null);
        this.otpTokenExpired = other.optionalOtpTokenExpired().map(InstantFilter::copy).orElse(null);
        this.isPass = other.optionalIsPass().map(BooleanFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OtpVerifyCriteria copy() {
        return new OtpVerifyCriteria(this);
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

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getOtpToken() {
        return otpToken;
    }

    public Optional<StringFilter> optionalOtpToken() {
        return Optional.ofNullable(otpToken);
    }

    public StringFilter otpToken() {
        if (otpToken == null) {
            setOtpToken(new StringFilter());
        }
        return otpToken;
    }

    public void setOtpToken(StringFilter otpToken) {
        this.otpToken = otpToken;
    }

    public InstantFilter getOtpTokenExpired() {
        return otpTokenExpired;
    }

    public Optional<InstantFilter> optionalOtpTokenExpired() {
        return Optional.ofNullable(otpTokenExpired);
    }

    public InstantFilter otpTokenExpired() {
        if (otpTokenExpired == null) {
            setOtpTokenExpired(new InstantFilter());
        }
        return otpTokenExpired;
    }

    public void setOtpTokenExpired(InstantFilter otpTokenExpired) {
        this.otpTokenExpired = otpTokenExpired;
    }

    public BooleanFilter getIsPass() {
        return isPass;
    }

    public Optional<BooleanFilter> optionalIsPass() {
        return Optional.ofNullable(isPass);
    }

    public BooleanFilter isPass() {
        if (isPass == null) {
            setIsPass(new BooleanFilter());
        }
        return isPass;
    }

    public void setIsPass(BooleanFilter isPass) {
        this.isPass = isPass;
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
        final OtpVerifyCriteria that = (OtpVerifyCriteria) o;
        return (Objects.equals(id, that.id) && Objects.equals(email, that.email)
                && Objects.equals(otpToken, that.otpToken) && Objects.equals(otpTokenExpired, that.otpTokenExpired)
                && Objects.equals(isPass, that.isPass) && Objects.equals(createTime, that.createTime)
                && Objects.equals(distinct, that.distinct));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, otpToken, otpTokenExpired, isPass, createTime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OtpVerifyCriteria{" + optionalId().map(f -> "id=" + f + ", ").orElse("")
                + optionalEmail().map(f -> "email=" + f + ", ").orElse("")
                + optionalOtpToken().map(f -> "otpToken=" + f + ", ").orElse("")
                + optionalOtpTokenExpired().map(f -> "otpTokenExpired=" + f + ", ").orElse("")
                + optionalIsPass().map(f -> "isPass=" + f + ", ").orElse("")
                + optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("")
                + optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") + "}";
    }
}
