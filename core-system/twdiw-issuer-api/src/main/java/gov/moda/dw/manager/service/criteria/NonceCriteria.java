package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NonceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sId;

    private StringFilter nonceId;

    private StringFilter userId;

    private LocalDateFilter createTime;

    private StringFilter captchaCode;

    private Boolean distinct;

    public NonceCriteria() {}

    public NonceCriteria(NonceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sId = other.optionalsId().map(StringFilter::copy).orElse(null);
        this.nonceId = other.optionalNonceId().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(LocalDateFilter::copy).orElse(null);
        this.captchaCode = other.optionalCaptchaCode().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NonceCriteria copy() {
        return new NonceCriteria(this);
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

    public StringFilter getsId() {
        return sId;
    }

    public Optional<StringFilter> optionalsId() {
        return Optional.ofNullable(sId);
    }

    public StringFilter sId() {
        if (sId == null) {
            setsId(new StringFilter());
        }
        return sId;
    }

    public void setsId(StringFilter sId) {
        this.sId = sId;
    }

    public StringFilter getNonceId() {
        return nonceId;
    }

    public Optional<StringFilter> optionalNonceId() {
        return Optional.ofNullable(nonceId);
    }

    public StringFilter nonceId() {
        if (nonceId == null) {
            setNonceId(new StringFilter());
        }
        return nonceId;
    }

    public void setNonceId(StringFilter nonceId) {
        this.nonceId = nonceId;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public Optional<StringFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public StringFilter userId() {
        if (userId == null) {
            setUserId(new StringFilter());
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    public LocalDateFilter getCreateTime() {
        return createTime;
    }

    public Optional<LocalDateFilter> optionalCreateTime() {
        return Optional.ofNullable(createTime);
    }

    public LocalDateFilter createTime() {
        if (createTime == null) {
            setCreateTime(new LocalDateFilter());
        }
        return createTime;
    }

    public void setCreateTime(LocalDateFilter createTime) {
        this.createTime = createTime;
    }

    public StringFilter getCaptchaCode() {
        return captchaCode;
    }

    public Optional<StringFilter> optionalCaptchaCode() {
        return Optional.ofNullable(captchaCode);
    }

    public StringFilter captchaCode() {
        if (captchaCode == null) {
            setCaptchaCode(new StringFilter());
        }
        return captchaCode;
    }

    public void setCaptchaCode(StringFilter captchaCode) {
        this.captchaCode = captchaCode;
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
        final NonceCriteria that = (NonceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sId, that.sId) &&
            Objects.equals(nonceId, that.nonceId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(captchaCode, that.captchaCode) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sId, nonceId, userId, createTime, captchaCode, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NonceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalsId().map(f -> "sId=" + f + ", ").orElse("") +
            optionalNonceId().map(f -> "nonceId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalCaptchaCode().map(f -> "captchaCode=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
