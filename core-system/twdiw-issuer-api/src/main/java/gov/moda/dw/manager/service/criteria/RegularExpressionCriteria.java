package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.RegularExpression} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.RegularExpressionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /regular-expressions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RegularExpressionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter type;

    private StringFilter name;

    private StringFilter regularExpression;

    private StringFilter description;

    private StringFilter errorMsg;

    private StringFilter ruleType;

    public RegularExpressionCriteria() {}

    public RegularExpressionCriteria(RegularExpressionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.regularExpression = other.regularExpression == null ? null : other.regularExpression.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.errorMsg = other.errorMsg == null ? null : other.errorMsg.copy();
        this.ruleType = other.ruleType == null ? null : other.ruleType.copy();
    }

    @Override
    public RegularExpressionCriteria copy() {
        return new RegularExpressionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getType() {
        return type;
    }

    public StringFilter type() {
        if (type == null) {
            type = new StringFilter();
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getRegularExpression() {
        return regularExpression;
    }

    public StringFilter regularExpression() {
        if (regularExpression == null) {
            regularExpression = new StringFilter();
        }
        return regularExpression;
    }

    public void setRegularExpression(StringFilter regularExpression) {
        this.regularExpression = regularExpression;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getErrorMsg() {
        return errorMsg;
    }

    public StringFilter errorMsg() {
        if (errorMsg == null) {
            errorMsg = new StringFilter();
        }
        return errorMsg;
    }

    public void setErrorMsg(StringFilter errorMsg) {
        this.errorMsg = errorMsg;
    }

    public StringFilter getRuleType() {
        return ruleType;
    }

    public StringFilter ruleType() {
        if (ruleType == null) {
            ruleType = new StringFilter();
        }
        return ruleType;
    }

    public void setRuleType(StringFilter ruleType) {
        this.ruleType = ruleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RegularExpressionCriteria that = (RegularExpressionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(name, that.name) &&
            Objects.equals(regularExpression, that.regularExpression) &&
            Objects.equals(description, that.description) &&
            Objects.equals(errorMsg, that.errorMsg) &&
            Objects.equals(ruleType, that.ruleType)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, regularExpression, description, errorMsg, ruleType);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegularExpressionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (regularExpression != null ? "regularExpression=" + regularExpression + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (errorMsg != null ? "errorMsg=" + errorMsg + ", " : "") +
            (ruleType != null ? "ruleType=" + ruleType + ", " : "") +
            "}";
    }
}
