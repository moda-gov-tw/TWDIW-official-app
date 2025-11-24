package gov.moda.dw.issuer.vc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * ExtendedUser
 */
@Entity
@Table(name = "extended_user", schema = "vc_manager")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExtendedUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 組織
     */
    @Size(max = 30)
    @Column(name = "org_id", length = 30)
    private String orgId;

    /**
     * 帳號
     */
    @NotNull
    @Size(max = 320)
    @Column(name = "user_id", length = 320, nullable = false)
    private String userId;

    /**
     * 姓名
     */
    @NotNull
    @Size(max = 128)
    @Column(name = "user_name", length = 128, nullable = false)
    //@Convert(converter = AttributeEncryptor.class) // 此欄位若重新刷新，就可以重新加密。
    private String userName;

    /**
     * email
     */
    @Size(max = 320)
    @Column(name = "email", length = 320)
    private String email;

    /**
     * 手機
     */
    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 市話
     */
    @Size(max = 20)
    @Column(name = "tel", length = 20)
    private String tel;

    /**
     * 員工編號
     */
    @Size(max = 20)
    @Column(name = "employee_id", length = 20)
    private String employeeId;

    /**
     * 員工類型
     */
    @Size(max = 10)
    @Column(name = "employee_type_id", length = 10)
    private String employeeTypeId;

    /**
     * 離職日
     */
    @Column(name = "left_date")
    private Instant leftDate;

    /**
     * 到職日
     */
    @Column(name = "onboard_date")
    private Instant onboardDate;

    /**
     * 帳號類型
     */
    @Size(max = 10)
    @Column(name = "user_type_id", length = 10)
    private String userTypeId;

    /**
     * 資料角色1
     */
    @Size(max = 255)
    @Column(name = "data_role_1", length = 255)
    private String dataRole1;

    /**
     * 資料角色2
     */
    @Size(max = 255)
    @Column(name = "data_role_2", length = 255)
    private String dataRole2;

    /**
     * 狀態
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "state", length = 10, nullable = false)
    private String state;

    /**
     * 建立日
     */
    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExtendedUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public ExtendedUser orgId(String orgId) {
        this.setOrgId(orgId);
        return this;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getUserId() {
        return this.userId;
    }

    public ExtendedUser userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public ExtendedUser userName(String userName) {
        this.setUserName(userName);
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return this.email;
    }

    public ExtendedUser email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public ExtendedUser phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTel() {
        return this.tel;
    }

    public ExtendedUser tel(String tel) {
        this.setTel(tel);
        return this;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public ExtendedUser employeeId(String employeeId) {
        this.setEmployeeId(employeeId);
        return this;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeTypeId() {
        return this.employeeTypeId;
    }

    public ExtendedUser employeeTypeId(String employeeTypeId) {
        this.setEmployeeTypeId(employeeTypeId);
        return this;
    }

    public void setEmployeeTypeId(String employeeTypeId) {
        this.employeeTypeId = employeeTypeId;
    }

    public Instant getLeftDate() {
        return this.leftDate;
    }

    public ExtendedUser leftDate(Instant leftDate) {
        this.setLeftDate(leftDate);
        return this;
    }

    public void setLeftDate(Instant leftDate) {
        this.leftDate = leftDate;
    }

    public Instant getOnboardDate() {
        return this.onboardDate;
    }

    public ExtendedUser onboardDate(Instant onboardDate) {
        this.setOnboardDate(onboardDate);
        return this;
    }

    public void setOnboardDate(Instant onboardDate) {
        this.onboardDate = onboardDate;
    }

    public String getUserTypeId() {
        return this.userTypeId;
    }

    public ExtendedUser userTypeId(String userTypeId) {
        this.setUserTypeId(userTypeId);
        return this;
    }

    public void setUserTypeId(String userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getDataRole1() {
        return this.dataRole1;
    }

    public ExtendedUser dataRole1(String dataRole1) {
        this.setDataRole1(dataRole1);
        return this;
    }

    public void setDataRole1(String dataRole1) {
        this.dataRole1 = dataRole1;
    }

    public String getDataRole2() {
        return this.dataRole2;
    }

    public ExtendedUser dataRole2(String dataRole2) {
        this.setDataRole2(dataRole2);
        return this;
    }

    public void setDataRole2(String dataRole2) {
        this.dataRole2 = dataRole2;
    }

    public String getState() {
        return this.state;
    }

    public ExtendedUser state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public ExtendedUser createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtendedUser)) {
            return false;
        }
        return getId() != null && getId().equals(((ExtendedUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExtendedUser{" +
            "id=" + getId() +
            ", orgId='" + getOrgId() + "'" +
            ", userId='" + getUserId() + "'" +
            ", userName='" + getUserName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", tel='" + getTel() + "'" +
            ", employeeId='" + getEmployeeId() + "'" +
            ", employeeTypeId='" + getEmployeeTypeId() + "'" +
            ", leftDate='" + getLeftDate() + "'" +
            ", onboardDate='" + getOnboardDate() + "'" +
            ", userTypeId='" + getUserTypeId() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", state='" + getState() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
