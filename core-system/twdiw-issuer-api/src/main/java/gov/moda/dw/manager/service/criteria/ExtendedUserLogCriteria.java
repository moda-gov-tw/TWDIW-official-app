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
public class ExtendedUserLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter actor;

    private StringFilter logType;

    private InstantFilter logTime;

    private StringFilter orgId;

    private StringFilter userId;

    private StringFilter userName;

    private StringFilter email;

    private StringFilter phone;

    private StringFilter tel;

    private StringFilter employeeId;

    private StringFilter employeeTypeId;

    private InstantFilter leftDate;

    private InstantFilter onboardDate;

    private StringFilter userTypeId;

    private StringFilter dataRole1;

    private StringFilter dataRole2;

    private StringFilter state;

    private InstantFilter createTime;

    private InstantFilter authChangeTime;

    private InstantFilter pwdResetTime;

    private Boolean distinct;

    public ExtendedUserLogCriteria() {}

    public ExtendedUserLogCriteria(ExtendedUserLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.actor = other.optionalActor().map(StringFilter::copy).orElse(null);
        this.logType = other.optionalLogType().map(StringFilter::copy).orElse(null);
        this.logTime = other.optionalLogTime().map(InstantFilter::copy).orElse(null);
        this.orgId = other.optionalOrgId().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.userName = other.optionalUserName().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.tel = other.optionalTel().map(StringFilter::copy).orElse(null);
        this.employeeId = other.optionalEmployeeId().map(StringFilter::copy).orElse(null);
        this.employeeTypeId = other.optionalEmployeeTypeId().map(StringFilter::copy).orElse(null);
        this.leftDate = other.optionalLeftDate().map(InstantFilter::copy).orElse(null);
        this.onboardDate = other.optionalOnboardDate().map(InstantFilter::copy).orElse(null);
        this.userTypeId = other.optionalUserTypeId().map(StringFilter::copy).orElse(null);
        this.dataRole1 = other.optionalDataRole1().map(StringFilter::copy).orElse(null);
        this.dataRole2 = other.optionalDataRole2().map(StringFilter::copy).orElse(null);
        this.state = other.optionalState().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.authChangeTime = other.optionalAuthChangeTime().map(InstantFilter::copy).orElse(null);
        this.pwdResetTime = other.optionalPwdResetTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ExtendedUserLogCriteria copy() {
        return new ExtendedUserLogCriteria(this);
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

    public StringFilter getActor() {
        return actor;
    }

    public Optional<StringFilter> optionalActor() {
        return Optional.ofNullable(actor);
    }

    public StringFilter actor() {
        if (actor == null) {
            setActor(new StringFilter());
        }
        return actor;
    }

    public void setActor(StringFilter actor) {
        this.actor = actor;
    }

    public StringFilter getLogType() {
        return logType;
    }

    public Optional<StringFilter> optionalLogType() {
        return Optional.ofNullable(logType);
    }

    public StringFilter logType() {
        if (logType == null) {
            setLogType(new StringFilter());
        }
        return logType;
    }

    public void setLogType(StringFilter logType) {
        this.logType = logType;
    }

    public InstantFilter getLogTime() {
        return logTime;
    }

    public Optional<InstantFilter> optionalLogTime() {
        return Optional.ofNullable(logTime);
    }

    public InstantFilter logTime() {
        if (logTime == null) {
            setLogTime(new InstantFilter());
        }
        return logTime;
    }

    public void setLogTime(InstantFilter logTime) {
        this.logTime = logTime;
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

    public StringFilter getUserName() {
        return userName;
    }

    public Optional<StringFilter> optionalUserName() {
        return Optional.ofNullable(userName);
    }

    public StringFilter userName() {
        if (userName == null) {
            setUserName(new StringFilter());
        }
        return userName;
    }

    public void setUserName(StringFilter userName) {
        this.userName = userName;
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

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getTel() {
        return tel;
    }

    public Optional<StringFilter> optionalTel() {
        return Optional.ofNullable(tel);
    }

    public StringFilter tel() {
        if (tel == null) {
            setTel(new StringFilter());
        }
        return tel;
    }

    public void setTel(StringFilter tel) {
        this.tel = tel;
    }

    public StringFilter getEmployeeId() {
        return employeeId;
    }

    public Optional<StringFilter> optionalEmployeeId() {
        return Optional.ofNullable(employeeId);
    }

    public StringFilter employeeId() {
        if (employeeId == null) {
            setEmployeeId(new StringFilter());
        }
        return employeeId;
    }

    public void setEmployeeId(StringFilter employeeId) {
        this.employeeId = employeeId;
    }

    public StringFilter getEmployeeTypeId() {
        return employeeTypeId;
    }

    public Optional<StringFilter> optionalEmployeeTypeId() {
        return Optional.ofNullable(employeeTypeId);
    }

    public StringFilter employeeTypeId() {
        if (employeeTypeId == null) {
            setEmployeeTypeId(new StringFilter());
        }
        return employeeTypeId;
    }

    public void setEmployeeTypeId(StringFilter employeeTypeId) {
        this.employeeTypeId = employeeTypeId;
    }

    public InstantFilter getLeftDate() {
        return leftDate;
    }

    public Optional<InstantFilter> optionalLeftDate() {
        return Optional.ofNullable(leftDate);
    }

    public InstantFilter leftDate() {
        if (leftDate == null) {
            setLeftDate(new InstantFilter());
        }
        return leftDate;
    }

    public void setLeftDate(InstantFilter leftDate) {
        this.leftDate = leftDate;
    }

    public InstantFilter getOnboardDate() {
        return onboardDate;
    }

    public Optional<InstantFilter> optionalOnboardDate() {
        return Optional.ofNullable(onboardDate);
    }

    public InstantFilter onboardDate() {
        if (onboardDate == null) {
            setOnboardDate(new InstantFilter());
        }
        return onboardDate;
    }

    public void setOnboardDate(InstantFilter onboardDate) {
        this.onboardDate = onboardDate;
    }

    public StringFilter getUserTypeId() {
        return userTypeId;
    }

    public Optional<StringFilter> optionalUserTypeId() {
        return Optional.ofNullable(userTypeId);
    }

    public StringFilter userTypeId() {
        if (userTypeId == null) {
            setUserTypeId(new StringFilter());
        }
        return userTypeId;
    }

    public void setUserTypeId(StringFilter userTypeId) {
        this.userTypeId = userTypeId;
    }

    public StringFilter getDataRole1() {
        return dataRole1;
    }

    public Optional<StringFilter> optionalDataRole1() {
        return Optional.ofNullable(dataRole1);
    }

    public StringFilter dataRole1() {
        if (dataRole1 == null) {
            setDataRole1(new StringFilter());
        }
        return dataRole1;
    }

    public void setDataRole1(StringFilter dataRole1) {
        this.dataRole1 = dataRole1;
    }

    public StringFilter getDataRole2() {
        return dataRole2;
    }

    public Optional<StringFilter> optionalDataRole2() {
        return Optional.ofNullable(dataRole2);
    }

    public StringFilter dataRole2() {
        if (dataRole2 == null) {
            setDataRole2(new StringFilter());
        }
        return dataRole2;
    }

    public void setDataRole2(StringFilter dataRole2) {
        this.dataRole2 = dataRole2;
    }

    public StringFilter getState() {
        return state;
    }

    public Optional<StringFilter> optionalState() {
        return Optional.ofNullable(state);
    }

    public StringFilter state() {
        if (state == null) {
            setState(new StringFilter());
        }
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
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

    public InstantFilter getAuthChangeTime() {
        return authChangeTime;
    }

    public Optional<InstantFilter> optionalAuthChangeTime() {
        return Optional.ofNullable(authChangeTime);
    }

    public InstantFilter authChangeTime() {
        if (authChangeTime == null) {
            setAuthChangeTime(new InstantFilter());
        }
        return authChangeTime;
    }

    public void setAuthChangeTime(InstantFilter authChangeTime) {
        this.authChangeTime = authChangeTime;
    }

    public InstantFilter getPwdResetTime() {
        return pwdResetTime;
    }

    public Optional<InstantFilter> optionalPwdResetTime() {
        return Optional.ofNullable(pwdResetTime);
    }

    public InstantFilter pwdResetTime() {
        if (pwdResetTime == null) {
            setPwdResetTime(new InstantFilter());
        }
        return pwdResetTime;
    }

    public void setPwdResetTime(InstantFilter pwdResetTime) {
        this.pwdResetTime = pwdResetTime;
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
        final ExtendedUserLogCriteria that = (ExtendedUserLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(actor, that.actor) &&
            Objects.equals(logType, that.logType) &&
            Objects.equals(logTime, that.logTime) &&
            Objects.equals(orgId, that.orgId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(userName, that.userName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(tel, that.tel) &&
            Objects.equals(employeeId, that.employeeId) &&
            Objects.equals(employeeTypeId, that.employeeTypeId) &&
            Objects.equals(leftDate, that.leftDate) &&
            Objects.equals(onboardDate, that.onboardDate) &&
            Objects.equals(userTypeId, that.userTypeId) &&
            Objects.equals(dataRole1, that.dataRole1) &&
            Objects.equals(dataRole2, that.dataRole2) &&
            Objects.equals(state, that.state) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(authChangeTime, that.authChangeTime) &&
            Objects.equals(pwdResetTime, that.pwdResetTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            actor,
            logType,
            logTime,
            orgId,
            userId,
            userName,
            email,
            phone,
            tel,
            employeeId,
            employeeTypeId,
            leftDate,
            onboardDate,
            userTypeId,
            dataRole1,
            dataRole2,
            state,
            createTime,
            authChangeTime,
            pwdResetTime,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExtendedUserLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalActor().map(f -> "actor=" + f + ", ").orElse("") +
            optionalLogType().map(f -> "logType=" + f + ", ").orElse("") +
            optionalLogTime().map(f -> "logTime=" + f + ", ").orElse("") +
            optionalOrgId().map(f -> "orgId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalUserName().map(f -> "userName=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalTel().map(f -> "tel=" + f + ", ").orElse("") +
            optionalEmployeeId().map(f -> "employeeId=" + f + ", ").orElse("") +
            optionalEmployeeTypeId().map(f -> "employeeTypeId=" + f + ", ").orElse("") +
            optionalLeftDate().map(f -> "leftDate=" + f + ", ").orElse("") +
            optionalOnboardDate().map(f -> "onboardDate=" + f + ", ").orElse("") +
            optionalUserTypeId().map(f -> "userTypeId=" + f + ", ").orElse("") +
            optionalDataRole1().map(f -> "dataRole1=" + f + ", ").orElse("") +
            optionalDataRole2().map(f -> "dataRole2=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalAuthChangeTime().map(f -> "authChangeTime=" + f + ", ").orElse("") +
            optionalPwdResetTime().map(f -> "pwdResetTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
