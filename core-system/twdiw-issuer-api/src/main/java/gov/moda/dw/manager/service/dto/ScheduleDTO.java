package gov.moda.dw.manager.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.Schedule} entity.
 */
@Schema(description = "Schedule 排程列表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    @Schema(description = "類型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Size(max = 5)
    @Schema(description = "時間(時:分)")
    private String time;

    @Size(max = 2)
    @Schema(description = "日(幾號)")
    private String date;

    @Size(max = 20)
    @Schema(description = "月(哪幾月)")
    private String month;

    @Size(max = 1)
    @Schema(description = "週(星期幾)")
    private String week;

    @Schema(description = "最近一次執行時間")
    private Instant lastRunDatetime;

    @Schema(description = "建立時間")
    private Instant crDatetime;

    @Schema(description = "建立者")
    private Long crUser;

    @Size(max = 30)
    @Schema(description = "時區")
    private String timezone;

    @Size(max = 255)
    @Schema(description = "單位")
    private String businessId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public Instant getLastRunDatetime() {
        return lastRunDatetime;
    }

    public void setLastRunDatetime(Instant lastRunDatetime) {
        this.lastRunDatetime = lastRunDatetime;
    }

    public Instant getCrDatetime() {
        return crDatetime;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Long getCrUser() {
        return crUser;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleDTO)) {
            return false;
        }

        ScheduleDTO scheduleDTO = (ScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleDTO{" +
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
