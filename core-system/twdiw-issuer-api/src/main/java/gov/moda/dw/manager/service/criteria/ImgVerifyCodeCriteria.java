package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImgVerifyCodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter verifyCode;

    private StringFilter verifyUniId;

    private InstantFilter createTime;

    private InstantFilter expireTime;

    private Boolean distinct;

    public ImgVerifyCodeCriteria() {}

    public ImgVerifyCodeCriteria(ImgVerifyCodeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.verifyCode = other.optionalVerifyCode().map(StringFilter::copy).orElse(null);
        this.verifyUniId = other.optionalVerifyUniId().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.expireTime = other.optionalExpireTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ImgVerifyCodeCriteria copy() {
        return new ImgVerifyCodeCriteria(this);
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

    public StringFilter getVerifyCode() {
        return verifyCode;
    }

    public Optional<StringFilter> optionalVerifyCode() {
        return Optional.ofNullable(verifyCode);
    }

    public StringFilter verifyCode() {
        if (verifyCode == null) {
            setVerifyCode(new StringFilter());
        }
        return verifyCode;
    }

    public void setVerifyCode(StringFilter verifyCode) {
        this.verifyCode = verifyCode;
    }

    public StringFilter getVerifyUniId() {
        return verifyUniId;
    }

    public Optional<StringFilter> optionalVerifyUniId() {
        return Optional.ofNullable(verifyUniId);
    }

    public StringFilter verifyUniId() {
        if (verifyUniId == null) {
            setVerifyUniId(new StringFilter());
        }
        return verifyUniId;
    }

    public void setVerifyUniId(StringFilter verifyUniId) {
        this.verifyUniId = verifyUniId;
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

    public InstantFilter getExpireTime() {
        return expireTime;
    }

    public Optional<InstantFilter> optionalExpireTime() {
        return Optional.ofNullable(expireTime);
    }

    public InstantFilter expireTime() {
        if (expireTime == null) {
            setExpireTime(new InstantFilter());
        }
        return expireTime;
    }

    public void setExpireTime(InstantFilter expireTime) {
        this.expireTime = expireTime;
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
        final ImgVerifyCodeCriteria that = (ImgVerifyCodeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(verifyCode, that.verifyCode) &&
            Objects.equals(verifyUniId, that.verifyUniId) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(expireTime, that.expireTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, verifyCode, verifyUniId, createTime, expireTime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImgVerifyCodeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalVerifyCode().map(f -> "verifyCode=" + f + ", ").orElse("") +
            optionalVerifyUniId().map(f -> "verifyUniId=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalExpireTime().map(f -> "expireTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
