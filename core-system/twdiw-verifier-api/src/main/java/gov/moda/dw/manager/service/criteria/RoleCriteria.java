package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;

import lombok.Getter;
import lombok.Setter;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@Getter
@Setter
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter roleId;

    private StringFilter roleName;

    private StringFilter description;

    private StringFilter state;

    private StringFilter dataRole1;

    private StringFilter dataRole2;

    private InstantFilter createTime;

    private InstantFilter authChangeTime;

    private Boolean distinct;

    public RoleCriteria() {}

    public RoleCriteria(RoleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.roleId = other.optionalRoleId().map(StringFilter::copy).orElse(null);
        this.roleName = other.optionalRoleName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.state = other.optionalState().map(StringFilter::copy).orElse(null);
        this.dataRole1 = other.optionalDataRole1().map(StringFilter::copy).orElse(null);
        this.dataRole2 = other.optionalDataRole2().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.authChangeTime = other.optionalAuthChangeTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RoleCriteria copy() {
        return new RoleCriteria(this);
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

    public Optional<StringFilter> optionalRoleId() {
        return Optional.ofNullable(roleId);
    }

    public StringFilter roleId() {
        if (roleId == null) {
            setRoleId(new StringFilter());
        }
        return roleId;
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

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
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

    public Optional<StringFilter> optionalDataRole1() {
        return Optional.ofNullable(dataRole1);
    }

    public StringFilter dataRole1() {
        if (dataRole1 == null) {
            setDataRole1(new StringFilter());
        }
        return dataRole1;
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

    public Optional<InstantFilter> optionalCreateTime() {
        return Optional.ofNullable(createTime);
    }

    public InstantFilter createTime() {
        if (createTime == null) {
            setCreateTime(new InstantFilter());
        }
        return createTime;
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

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RoleCriteria that = (RoleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(roleId, that.roleId) &&
            Objects.equals(roleName, that.roleName) &&
            Objects.equals(description, that.description) &&
            Objects.equals(state, that.state) &&
            Objects.equals(dataRole1, that.dataRole1) &&
            Objects.equals(dataRole2, that.dataRole2) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(authChangeTime, that.authChangeTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleId, roleName, description, state, dataRole1, dataRole2, createTime, authChangeTime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRoleId().map(f -> "roleId=" + f + ", ").orElse("") +
            optionalRoleName().map(f -> "roleName=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalDataRole1().map(f -> "dataRole1=" + f + ", ").orElse("") +
            optionalDataRole2().map(f -> "dataRole2=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalAuthChangeTime().map(f -> "authChangeTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
