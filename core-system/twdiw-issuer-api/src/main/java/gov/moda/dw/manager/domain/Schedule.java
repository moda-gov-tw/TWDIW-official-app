package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Schedule 排程列表
 */
@Entity
@Table(name = "schedule")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 類型
     */
    @NotNull
    @Size(max = 20)
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    /**
     * 時間(時:分)
     */
    @Size(max = 5)
    @Column(name = "time", length = 5)
    private String time;

    /**
     * 日(幾號)
     */
    @Size(max = 2)
    @Column(name = "date", length = 2)
    private String date;

    /**
     * 月(哪幾月)
     */
    @Size(max = 20)
    @Column(name = "month", length = 20)
    private String month;

    /**
     * 週(星期幾)
     */
    @Size(max = 1)
    @Column(name = "week", length = 1)
    private String week;

    /**
     * 最近一次執行時間
     */
    @Column(name = "last_run_datetime")
    private Instant lastRunDatetime;

    /**
     * 建立時間
     */
    @Column(name = "cr_datetime")
    private Instant crDatetime;

    /**
     * 建立者
     */
    @Column(name = "cr_user")
    private Long crUser;

    /**
     * 時區
     */
    @Size(max = 30)
    @Column(name = "timezone", length = 30)
    private String timezone;

    /**
     * 單位
     */
    @Size(max = 255)
    @Column(name = "business_id", length = 255)
    private String businessId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Schedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public Schedule type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return this.time;
    }

    public Schedule time(String time) {
        this.setTime(time);
        return this;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return this.date;
    }

    public Schedule date(String date) {
        this.setDate(date);
        return this;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return this.month;
    }

    public Schedule month(String month) {
        this.setMonth(month);
        return this;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getWeek() {
        return this.week;
    }

    public Schedule week(String week) {
        this.setWeek(week);
        return this;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public Instant getLastRunDatetime() {
        return this.lastRunDatetime;
    }

    public Schedule lastRunDatetime(Instant lastRunDatetime) {
        this.setLastRunDatetime(lastRunDatetime);
        return this;
    }

    public void setLastRunDatetime(Instant lastRunDatetime) {
        this.lastRunDatetime = lastRunDatetime;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public Schedule crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Long getCrUser() {
        return this.crUser;
    }

    public Schedule crUser(Long crUser) {
        this.setCrUser(crUser);
        return this;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public Schedule timezone(String timezone) {
        this.setTimezone(timezone);
        return this;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public Schedule businessId(String businessId) {
        this.setBusinessId(businessId);
        return this;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }
        return getId() != null && getId().equals(((Schedule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Schedule{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", time='" + getTime() + "'" +
            ", date='" + getDate() + "'" +
            ", month='" + getMonth() + "'" +
            ", week='" + getWeek() + "'" +
            ", lastRunDatetime='" + getLastRunDatetime() + "'" +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", crUser=" + getCrUser() +
            ", timezone='" + getTimezone() + "'" +
            ", businessId='" + getBusinessId() + "'" +
            "}";
    }
}
