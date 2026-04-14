package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthObjCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private StringFilter id;

    private LongFilter userId;

    private StringFilter resCode;

    private StringFilter login;

    private LongFilter roleId;

    private StringFilter roleCode;

    private StringFilter roleName;

    private Boolean distinct;

    public AuthObjCriteria() {}

    public AuthObjCriteria(AuthObjCriteria other) {
        this.id = other.optionalId().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.resCode = other.optionalResCode().map(StringFilter::copy).orElse(null);
        this.login = other.optionalLogin().map(StringFilter::copy).orElse(null);
        this.roleId = other.optionalRoleId().map(LongFilter::copy).orElse(null);
        this.roleCode = other.optionalRoleCode().map(StringFilter::copy).orElse(null);
        this.roleName = other.optionalRoleName().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AuthObjCriteria copy() {
        return new AuthObjCriteria(this);
    }

    public StringFilter getId() {
        return id;
    }

    public Optional<StringFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public StringFilter id() {
        if (id == null) {
            setId(new StringFilter());
        }
        return id;
    }

    public void setId(StringFilter id) {
        this.id = id;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public StringFilter getResCode() {
        return resCode;
    }

    public Optional<StringFilter> optionalResCode() {
        return Optional.ofNullable(resCode);
    }

    public StringFilter resCode() {
        if (resCode == null) {
            setResCode(new StringFilter());
        }
        return resCode;
    }

    public void setResCode(StringFilter resCode) {
        this.resCode = resCode;
    }

    public StringFilter getLogin() {
        return login;
    }

    public Optional<StringFilter> optionalLogin() {
        return Optional.ofNullable(login);
    }

    public StringFilter login() {
        if (login == null) {
            setLogin(new StringFilter());
        }
        return login;
    }

    public void setLogin(StringFilter login) {
        this.login = login;
    }

    public LongFilter getRoleId() {
        return roleId;
    }

    public Optional<LongFilter> optionalRoleId() {
        return Optional.ofNullable(roleId);
    }

    public LongFilter roleId() {
        if (roleId == null) {
            setRoleId(new LongFilter());
        }
        return roleId;
    }

    public void setRoleId(LongFilter roleId) {
        this.roleId = roleId;
    }

    public StringFilter getRoleCode() {
        return roleCode;
    }

    public Optional<StringFilter> optionalRoleCode() {
        return Optional.ofNullable(roleCode);
    }

    public StringFilter roleCode() {
        if (roleCode == null) {
            setRoleCode(new StringFilter());
        }
        return roleCode;
    }

    public void setRoleCode(StringFilter roleCode) {
        this.roleCode = roleCode;
    }

    public StringFilter getRoleName() {
        return roleName;
    }

    public Optional<StringFilter> optionalRoleName() {
        return Optional.ofNullable(roleName);
    }

    public StringFilter roleName() {
        if (roleName == null) {
            setRoleName(new StringFilter());
        }
        return roleName;
    }

    public void setRoleName(StringFilter roleName) {
        this.roleName = roleName;
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
        final AuthObjCriteria that = (AuthObjCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(resCode, that.resCode) &&
            Objects.equals(login, that.login) &&
            Objects.equals(roleId, that.roleId) &&
            Objects.equals(roleCode, that.roleCode) &&
            Objects.equals(roleName, that.roleName) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, resCode, login, roleId, roleCode, roleName, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuthObjCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalResCode().map(f -> "resCode=" + f + ", ").orElse("") +
            optionalLogin().map(f -> "login=" + f + ", ").orElse("") +
            optionalRoleId().map(f -> "roleId=" + f + ", ").orElse("") +
            optionalRoleCode().map(f -> "roleCode=" + f + ", ").orElse("") +
            optionalRoleName().map(f -> "roleName=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
