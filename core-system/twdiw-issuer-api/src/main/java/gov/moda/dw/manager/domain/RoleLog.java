package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A RoleLog.
 */
@Entity
@Table(name = "role_log")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 異動人
     */
    @NotNull
    @Size(max = 140)
    @Column(name = "actor", length = 140, nullable = false)
    private String actor;

    /**
     * 異動類型
     */
    @NotNull
    @Size(max = 20)
    @Column(name = "log_type", length = 20, nullable = false)
    private String logType;

    /**
     * Log建立時間
     */
    @NotNull
    @Column(name = "log_time", nullable = false)
    private Instant logTime;

    /**
     * 角色代碼
     */
    @NotNull
    @Size(max = 20)
    @Column(name = "role_id", length = 20, nullable = false)
    private String roleId;

    /**
     * 角色名稱
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "role_name", length = 50, nullable = false)
    private String roleName;

    /**
     * 描述
     */
    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 狀態
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "state", length = 10, nullable = false)
    private String state;

    /**
     * 預留欄位1
     */
    @Size(max = 255)
    @Column(name = "data_role_1", length = 255)
    private String dataRole1;

    /**
     * 預留欄位2
     */
    @Size(max = 255)
    @Column(name = "data_role_2", length = 255)
    private String dataRole2;

    /**
     * 角色建立時間
     */
    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    /**
     * 上次變更權限時間
     */
    @Column(name = "auth_change_time")
    private Instant authChangeTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RoleLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActor() {
        return this.actor;
    }

    public RoleLog actor(String actor) {
        this.setActor(actor);
        return this;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getLogType() {
        return this.logType;
    }

    public RoleLog logType(String logType) {
        this.setLogType(logType);
        return this;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public Instant getLogTime() {
        return this.logTime;
    }

    public RoleLog logTime(Instant logTime) {
        this.setLogTime(logTime);
        return this;
    }

    public void setLogTime(Instant logTime) {
        this.logTime = logTime;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public RoleLog roleId(String roleId) {
        this.setRoleId(roleId);
        return this;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public RoleLog roleName(String roleName) {
        this.setRoleName(roleName);
        return this;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return this.description;
    }

    public RoleLog description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return this.state;
    }

    public RoleLog state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDataRole1() {
        return this.dataRole1;
    }

    public RoleLog dataRole1(String dataRole1) {
        this.setDataRole1(dataRole1);
        return this;
    }

    public void setDataRole1(String dataRole1) {
        this.dataRole1 = dataRole1;
    }

    public String getDataRole2() {
        return this.dataRole2;
    }

    public RoleLog dataRole2(String dataRole2) {
        this.setDataRole2(dataRole2);
        return this;
    }

    public void setDataRole2(String dataRole2) {
        this.dataRole2 = dataRole2;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public RoleLog createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getAuthChangeTime() {
        return this.authChangeTime;
    }

    public RoleLog authChangeTime(Instant authChangeTime) {
        this.setAuthChangeTime(authChangeTime);
        return this;
    }

    public void setAuthChangeTime(Instant authChangeTime) {
        this.authChangeTime = authChangeTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleLog)) {
            return false;
        }
        return getId() != null && getId().equals(((RoleLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleLog{" +
            "id=" + getId() +
            ", actor='" + getActor() + "'" +
            ", logType='" + getLogType() + "'" +
            ", logTime='" + getLogTime() + "'" +
            ", roleId='" + getRoleId() + "'" +
            ", roleName='" + getRoleName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", authChangeTime='" + getAuthChangeTime() + "'" +
            "}";
    }
}
