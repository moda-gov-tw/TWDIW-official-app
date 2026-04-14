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
public class LoginViewCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter userId;

    private IntegerFilter failCount;

    private InstantFilter lastLogin;

    private StringFilter bwdHash;

    private InstantFilter bwdDate;

    private StringFilter loginIdState;

    private Boolean distinct;

    public LoginViewCriteria() {}

    public LoginViewCriteria(LoginViewCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.failCount = other.optionalFailCount().map(IntegerFilter::copy).orElse(null);
        this.lastLogin = other.optionalLastLogin().map(InstantFilter::copy).orElse(null);
        this.bwdHash = other.optionalBwdHash().map(StringFilter::copy).orElse(null);
        this.bwdDate = other.optionalBwdDate().map(InstantFilter::copy).orElse(null);
        this.loginIdState = other.optionalLoginIdState().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LoginViewCriteria copy() {
        return new LoginViewCriteria(this);
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

    public InstantFilter getLastLogin() {
        return lastLogin;
    }

    public Optional<InstantFilter> optionalLastLogin() {
        return Optional.ofNullable(lastLogin);
    }

    public InstantFilter lastLogin() {
        if (lastLogin == null) {
            setLastLogin(new InstantFilter());
        }
        return lastLogin;
    }

    public void setLastLogin(InstantFilter lastLogin) {
        this.lastLogin = lastLogin;
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

    public InstantFilter getBwdDate() {
        return bwdDate;
    }

    public Optional<InstantFilter> optionalBwdDate() {
        return Optional.ofNullable(bwdDate);
    }

    public InstantFilter bwdDate() {
        if (bwdDate == null) {
            setBwdDate(new InstantFilter());
        }
        return bwdDate;
    }

    public void setBwdDate(InstantFilter bwdDate) {
        this.bwdDate = bwdDate;
    }

    public StringFilter getLoginIdState() {
        return loginIdState;
    }

    public Optional<StringFilter> optionalLoginIdState() {
        return Optional.ofNullable(loginIdState);
    }

    public StringFilter loginIdState() {
        if (loginIdState == null) {
            setLoginIdState(new StringFilter());
        }
        return loginIdState;
    }

    public void setLoginIdState(StringFilter loginIdState) {
        this.loginIdState = loginIdState;
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
        final LoginViewCriteria that = (LoginViewCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(failCount, that.failCount) &&
            Objects.equals(lastLogin, that.lastLogin) &&
            Objects.equals(bwdHash, that.bwdHash) &&
            Objects.equals(bwdDate, that.bwdDate) &&
            Objects.equals(loginIdState, that.loginIdState) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, failCount, lastLogin, bwdHash, bwdDate, loginIdState, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoginViewCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalFailCount().map(f -> "failCount=" + f + ", ").orElse("") +
            optionalLastLogin().map(f -> "lastLogin=" + f + ", ").orElse("") +
            optionalBwdHash().map(f -> "bwdHash=" + f + ", ").orElse("") +
            optionalBwdDate().map(f -> "bwdDate=" + f + ", ").orElse("") +
            optionalLoginIdState().map(f -> "loginIdState=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
