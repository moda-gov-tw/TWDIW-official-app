package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BwdParamCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter bwdProfileId;

    private StringFilter ruleId;

    private StringFilter ruleName;

    private StringFilter description;

    private BooleanFilter state;

    private StringFilter strRegular;

    private StringFilter paramValue;

    private StringFilter actionType;

    private StringFilter checkType;

    private StringFilter errorMessage;

    private InstantFilter createTime;

    private InstantFilter updateTime;

    private Boolean distinct;

    public BwdParamCriteria() {}

    public BwdParamCriteria(BwdParamCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.bwdProfileId = other.optionalBwdProfileId().map(StringFilter::copy).orElse(null);
        this.ruleId = other.optionalRuleId().map(StringFilter::copy).orElse(null);
        this.ruleName = other.optionalRuleName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.state = other.optionalState().map(BooleanFilter::copy).orElse(null);
        this.strRegular = other.optionalStrRegular().map(StringFilter::copy).orElse(null);
        this.paramValue = other.optionalParamValue().map(StringFilter::copy).orElse(null);
        this.actionType = other.optionalActionType().map(StringFilter::copy).orElse(null);
        this.checkType = other.optionalCheckType().map(StringFilter::copy).orElse(null);
        this.errorMessage = other.optionalErrorMessage().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.updateTime = other.optionalUpdateTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BwdParamCriteria copy() {
        return new BwdParamCriteria(this);
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

    public StringFilter getBwdProfileId() {
        return bwdProfileId;
    }

    public Optional<StringFilter> optionalBwdProfileId() {
        return Optional.ofNullable(bwdProfileId);
    }

    public StringFilter bwdProfileId() {
        if (bwdProfileId == null) {
            setBwdProfileId(new StringFilter());
        }
        return bwdProfileId;
    }

    public void setBwdProfileId(StringFilter bwdProfileId) {
        this.bwdProfileId = bwdProfileId;
    }

    public StringFilter getRuleId() {
        return ruleId;
    }

    public Optional<StringFilter> optionalRuleId() {
        return Optional.ofNullable(ruleId);
    }

    public StringFilter ruleId() {
        if (ruleId == null) {
            setRuleId(new StringFilter());
        }
        return ruleId;
    }

    public void setRuleId(StringFilter ruleId) {
        this.ruleId = ruleId;
    }

    public StringFilter getRuleName() {
        return ruleName;
    }

    public Optional<StringFilter> optionalRuleName() {
        return Optional.ofNullable(ruleName);
    }

    public StringFilter ruleName() {
        if (ruleName == null) {
            setRuleName(new StringFilter());
        }
        return ruleName;
    }

    public void setRuleName(StringFilter ruleName) {
        this.ruleName = ruleName;
    }

    public StringFilter getDescription() {
        return description;
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

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public BooleanFilter getState() {
        return state;
    }

    public Optional<BooleanFilter> optionalState() {
        return Optional.ofNullable(state);
    }

    public BooleanFilter state() {
        if (state == null) {
            setState(new BooleanFilter());
        }
        return state;
    }

    public void setState(BooleanFilter state) {
        this.state = state;
    }

    public StringFilter getStrRegular() {
        return strRegular;
    }

    public Optional<StringFilter> optionalStrRegular() {
        return Optional.ofNullable(strRegular);
    }

    public StringFilter strRegular() {
        if (strRegular == null) {
            setStrRegular(new StringFilter());
        }
        return strRegular;
    }

    public void setStrRegular(StringFilter strRegular) {
        this.strRegular = strRegular;
    }

    public StringFilter getParamValue() {
        return paramValue;
    }

    public Optional<StringFilter> optionalParamValue() {
        return Optional.ofNullable(paramValue);
    }

    public StringFilter paramValue() {
        if (paramValue == null) {
            setParamValue(new StringFilter());
        }
        return paramValue;
    }

    public void setParamValue(StringFilter paramValue) {
        this.paramValue = paramValue;
    }

    public StringFilter getActionType() {
        return actionType;
    }

    public Optional<StringFilter> optionalActionType() {
        return Optional.ofNullable(actionType);
    }

    public StringFilter actionType() {
        if (actionType == null) {
            setActionType(new StringFilter());
        }
        return actionType;
    }

    public void setActionType(StringFilter actionType) {
        this.actionType = actionType;
    }

    public StringFilter getCheckType() {
        return checkType;
    }

    public Optional<StringFilter> optionalCheckType() {
        return Optional.ofNullable(checkType);
    }

    public StringFilter checkType() {
        if (checkType == null) {
            setCheckType(new StringFilter());
        }
        return checkType;
    }

    public void setCheckType(StringFilter checkType) {
        this.checkType = checkType;
    }

    public StringFilter getErrorMessage() {
        return errorMessage;
    }

    public Optional<StringFilter> optionalErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public StringFilter errorMessage() {
        if (errorMessage == null) {
            setErrorMessage(new StringFilter());
        }
        return errorMessage;
    }

    public void setErrorMessage(StringFilter errorMessage) {
        this.errorMessage = errorMessage;
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
        final BwdParamCriteria that = (BwdParamCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(bwdProfileId, that.bwdProfileId) &&
            Objects.equals(ruleId, that.ruleId) &&
            Objects.equals(ruleName, that.ruleName) &&
            Objects.equals(description, that.description) &&
            Objects.equals(state, that.state) &&
            Objects.equals(strRegular, that.strRegular) &&
            Objects.equals(paramValue, that.paramValue) &&
            Objects.equals(actionType, that.actionType) &&
            Objects.equals(checkType, that.checkType) &&
            Objects.equals(errorMessage, that.errorMessage) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(updateTime, that.updateTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            bwdProfileId,
            ruleId,
            ruleName,
            description,
            state,
            strRegular,
            paramValue,
            actionType,
            checkType,
            errorMessage,
            createTime,
            updateTime,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BwdParamCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBwdProfileId().map(f -> "bwdProfileId=" + f + ", ").orElse("") +
            optionalRuleId().map(f -> "ruleId=" + f + ", ").orElse("") +
            optionalRuleName().map(f -> "ruleName=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalStrRegular().map(f -> "strRegular=" + f + ", ").orElse("") +
            optionalParamValue().map(f -> "paramValue=" + f + ", ").orElse("") +
            optionalActionType().map(f -> "actionType=" + f + ", ").orElse("") +
            optionalCheckType().map(f -> "checkType=" + f + ", ").orElse("") +
            optionalErrorMessage().map(f -> "errorMessage=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalUpdateTime().map(f -> "updateTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
