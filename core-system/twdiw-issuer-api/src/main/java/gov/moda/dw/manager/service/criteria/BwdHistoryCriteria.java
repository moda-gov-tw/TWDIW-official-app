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
public class BwdHistoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter userId;

    private InstantFilter createTime;

    private StringFilter bwdHash;

    private StringFilter bwdCode;

    private Boolean distinct;

    public BwdHistoryCriteria() {}

    public BwdHistoryCriteria(BwdHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.bwdHash = other.optionalBwdHash().map(StringFilter::copy).orElse(null);
        this.bwdCode = other.optionalBwdCode().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BwdHistoryCriteria copy() {
        return new BwdHistoryCriteria(this);
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

    public StringFilter getBwdHash() {
        return bwdHash;
    }

    public Optional<StringFilter> optionalBwdHash() {
        return Optional.ofNullable(bwdHash);
    }

    public StringFilter bwdHash() {
        if (bwdHash == null) {
            setBwdHash(new StringFilter());
        }
        return bwdHash;
    }

    public void setBwdHash(StringFilter bwdHash) {
        this.bwdHash = bwdHash;
    }

    public StringFilter getBwdCode() {
        return bwdCode;
    }

    public Optional<StringFilter> optionalBwdCode() {
        return Optional.ofNullable(bwdCode);
    }

    public StringFilter bwdCode() {
        if (bwdCode == null) {
            setBwdCode(new StringFilter());
        }
        return bwdCode;
    }

    public void setBwdCode(StringFilter bwdCode) {
        this.bwdCode = bwdCode;
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
        final BwdHistoryCriteria that = (BwdHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(bwdHash, that.bwdHash) &&
            Objects.equals(bwdCode, that.bwdCode) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, createTime, bwdHash, bwdCode, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BwdHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalBwdHash().map(f -> "bwdHash=" + f + ", ").orElse("") +
            optionalBwdCode().map(f -> "bwdCode=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
