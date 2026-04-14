package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoginCountCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter userId;

    private IntegerFilter failCount;

    private InstantFilter updateTime;

    private Boolean distinct;

    public LoginCountCriteria() {}

    public LoginCountCriteria(LoginCountCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.failCount = other.optionalFailCount().map(IntegerFilter::copy).orElse(null);
        this.updateTime = other.optionalUpdateTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LoginCountCriteria copy() {
        return new LoginCountCriteria(this);
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

    public IntegerFilter getFailCount() {
        return failCount;
    }

    public Optional<IntegerFilter> optionalFailCount() {
        return Optional.ofNullable(failCount);
    }

    public IntegerFilter failCount() {
        if (failCount == null) {
            setFailCount(new IntegerFilter());
        }
        return failCount;
    }

    public void setFailCount(IntegerFilter failCount) {
        this.failCount = failCount;
    }

    public InstantFilter getUpdateTime() {
        return updateTime;
    }

    public Optional<InstantFilter> optionalUpdateTime() {
        return Optional.ofNullable(updateTime);
    }

    public InstantFilter updateTime() {
        if (updateTime == null) {
            setUpdateTime(new InstantFilter());
        }
        return updateTime;
    }

    public void setUpdateTime(InstantFilter updateTime) {
        this.updateTime = updateTime;
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
        final LoginCountCriteria that = (LoginCountCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(failCount, that.failCount) &&
            Objects.equals(updateTime, that.updateTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, failCount, updateTime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoginCountCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalFailCount().map(f -> "failCount=" + f + ", ").orElse("") +
            optionalUpdateTime().map(f -> "updateTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
