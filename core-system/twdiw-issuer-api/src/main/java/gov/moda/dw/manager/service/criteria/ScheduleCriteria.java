package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.Schedule} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.ScheduleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /schedules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter type;

    private StringFilter time;

    private StringFilter date;

    private StringFilter month;

    private StringFilter week;

    private InstantFilter lastRunDatetime;

    private InstantFilter crDatetime;

    private LongFilter crUser;

    private StringFilter timezone;

    private StringFilter businessId;

    private Boolean distinct;

    public ScheduleCriteria() {}

    public ScheduleCriteria(ScheduleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.time = other.optionalTime().map(StringFilter::copy).orElse(null);
        this.date = other.optionalDate().map(StringFilter::copy).orElse(null);
        this.month = other.optionalMonth().map(StringFilter::copy).orElse(null);
        this.week = other.optionalWeek().map(StringFilter::copy).orElse(null);
        this.lastRunDatetime = other.optionalLastRunDatetime().map(InstantFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.crUser = other.optionalCrUser().map(LongFilter::copy).orElse(null);
        this.timezone = other.optionalTimezone().map(StringFilter::copy).orElse(null);
        this.businessId = other.optionalBusinessId().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScheduleCriteria copy() {
        return new ScheduleCriteria(this);
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

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getTime() {
        return time;
    }

    public Optional<StringFilter> optionalTime() {
        return Optional.ofNullable(time);
    }

    public StringFilter time() {
        if (time == null) {
            setTime(new StringFilter());
        }
        return time;
    }

    public void setTime(StringFilter time) {
        this.time = time;
    }

    public StringFilter getDate() {
        return date;
    }

    public Optional<StringFilter> optionalDate() {
        return Optional.ofNullable(date);
    }

    public StringFilter date() {
        if (date == null) {
            setDate(new StringFilter());
        }
        return date;
    }

    public void setDate(StringFilter date) {
        this.date = date;
    }

    public StringFilter getMonth() {
        return month;
    }

    public Optional<StringFilter> optionalMonth() {
        return Optional.ofNullable(month);
    }

    public StringFilter month() {
        if (month == null) {
            setMonth(new StringFilter());
        }
        return month;
    }

    public void setMonth(StringFilter month) {
        this.month = month;
    }

    public StringFilter getWeek() {
        return week;
    }

    public Optional<StringFilter> optionalWeek() {
        return Optional.ofNullable(week);
    }

    public StringFilter week() {
        if (week == null) {
            setWeek(new StringFilter());
        }
        return week;
    }

    public void setWeek(StringFilter week) {
        this.week = week;
    }

    public InstantFilter getLastRunDatetime() {
        return lastRunDatetime;
    }

    public Optional<InstantFilter> optionalLastRunDatetime() {
        return Optional.ofNullable(lastRunDatetime);
    }

    public InstantFilter lastRunDatetime() {
        if (lastRunDatetime == null) {
            setLastRunDatetime(new InstantFilter());
        }
        return lastRunDatetime;
    }

    public void setLastRunDatetime(InstantFilter lastRunDatetime) {
        this.lastRunDatetime = lastRunDatetime;
    }

    public InstantFilter getCrDatetime() {
        return crDatetime;
    }

    public Optional<InstantFilter> optionalCrDatetime() {
        return Optional.ofNullable(crDatetime);
    }

    public InstantFilter crDatetime() {
        if (crDatetime == null) {
            setCrDatetime(new InstantFilter());
        }
        return crDatetime;
    }

    public void setCrDatetime(InstantFilter crDatetime) {
        this.crDatetime = crDatetime;
    }

    public LongFilter getCrUser() {
        return crUser;
    }

    public Optional<LongFilter> optionalCrUser() {
        return Optional.ofNullable(crUser);
    }

    public LongFilter crUser() {
        if (crUser == null) {
            setCrUser(new LongFilter());
        }
        return crUser;
    }

    public void setCrUser(LongFilter crUser) {
        this.crUser = crUser;
    }

    public StringFilter getTimezone() {
        return timezone;
    }

    public Optional<StringFilter> optionalTimezone() {
        return Optional.ofNullable(timezone);
    }

    public StringFilter timezone() {
        if (timezone == null) {
            setTimezone(new StringFilter());
        }
        return timezone;
    }

    public void setTimezone(StringFilter timezone) {
        this.timezone = timezone;
    }

    public StringFilter getBusinessId() {
        return businessId;
    }

    public Optional<StringFilter> optionalBusinessId() {
        return Optional.ofNullable(businessId);
    }

    public StringFilter businessId() {
        if (businessId == null) {
            setBusinessId(new StringFilter());
        }
        return businessId;
    }

    public void setBusinessId(StringFilter businessId) {
        this.businessId = businessId;
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
        final ScheduleCriteria that = (ScheduleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(time, that.time) &&
            Objects.equals(date, that.date) &&
            Objects.equals(month, that.month) &&
            Objects.equals(week, that.week) &&
            Objects.equals(lastRunDatetime, that.lastRunDatetime) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(crUser, that.crUser) &&
            Objects.equals(timezone, that.timezone) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, time, date, month, week, lastRunDatetime, crDatetime, crUser, timezone, businessId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalTime().map(f -> "time=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalMonth().map(f -> "month=" + f + ", ").orElse("") +
            optionalWeek().map(f -> "week=" + f + ", ").orElse("") +
            optionalLastRunDatetime().map(f -> "lastRunDatetime=" + f + ", ").orElse("") +
            optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("") +
            optionalCrUser().map(f -> "crUser=" + f + ", ").orElse("") +
            optionalTimezone().map(f -> "timezone=" + f + ", ").orElse("") +
            optionalBusinessId().map(f -> "businessId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
