package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A OtpVerify.
 */
@Entity
@Table(name = "otp_verify")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OtpVerify implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 電字郵件
     */
    @NotNull
    @Size(max = 320)
    @Column(name = "email", length = 320, nullable = false)
    private String email;

    /**
     * OTP 驗證碼
     */
    @NotNull
    @Size(max = 20)
    @Column(name = "otp_token", length = 20, nullable = false)
    private String otpToken;

    /**
     * OTP 到期時間
     */
    @Column(name = "otp_token_expired")
    private Instant otpTokenExpired;

    /**
     * 驗證狀態
     */
    @NotNull
    @Column(name = "is_pass", nullable = false)
    private Boolean isPass;

    /**
     * 建立時間
     */
    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OtpVerify id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public OtpVerify email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpToken() {
        return this.otpToken;
    }

    public OtpVerify otpToken(String otpToken) {
        this.setOtpToken(otpToken);
        return this;
    }

    public void setOtpToken(String otpToken) {
        this.otpToken = otpToken;
    }

    public Instant getOtpTokenExpired() {
        return this.otpTokenExpired;
    }

    public OtpVerify otpTokenExpired(Instant otpTokenExpired) {
        this.setOtpTokenExpired(otpTokenExpired);
        return this;
    }

    public void setOtpTokenExpired(Instant otpTokenExpired) {
        this.otpTokenExpired = otpTokenExpired;
    }

    public Boolean getIsPass() {
        return this.isPass;
    }

    public OtpVerify isPass(Boolean isPass) {
        this.setIsPass(isPass);
        return this;
    }

    public void setIsPass(Boolean isPass) {
        this.isPass = isPass;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public OtpVerify createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OtpVerify)) {
            return false;
        }
        return getId() != null && getId().equals(((OtpVerify) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OtpVerify{" + "id=" + getId() + ", email='" + getEmail() + "'" + ", otpToken='" + getOtpToken() + "'"
                + ", otpTokenExpired='" + getOtpTokenExpired() + "'" + ", isPass='" + getIsPass() + "'"
                + ", createTime='" + getCreateTime() + "'" + "}";
    }
}
