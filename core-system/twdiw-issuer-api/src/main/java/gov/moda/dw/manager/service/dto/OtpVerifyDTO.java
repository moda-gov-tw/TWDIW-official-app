package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.OtpVerify} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OtpVerifyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 320)
    @Schema(description = "電字郵件", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull
    @Size(max = 20)
    @Schema(description = "OTP 驗證碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otpToken;

    @Schema(description = "OTP 到期時間")
    private Instant otpTokenExpired;

    @NotNull
    @Schema(description = "驗證狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isPass;

    @NotNull
    @Schema(description = "建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpToken() {
        return otpToken;
    }

    public void setOtpToken(String otpToken) {
        this.otpToken = otpToken;
    }

    public Instant getOtpTokenExpired() {
        return otpTokenExpired;
    }

    public void setOtpTokenExpired(Instant otpTokenExpired) {
        this.otpTokenExpired = otpTokenExpired;
    }

    public Boolean getIsPass() {
        return isPass;
    }

    public void setIsPass(Boolean isPass) {
        this.isPass = isPass;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OtpVerifyDTO)) {
            return false;
        }

        OtpVerifyDTO otpVerifyDTO = (OtpVerifyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, otpVerifyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OtpVerifyDTO{" + "id=" + getId() + ", email='" + getEmail() + "'" + ", otpToken='" + getOtpToken() + "'"
                + ", otpTokenExpired='" + getOtpTokenExpired() + "'" + ", isPass='" + getIsPass() + "'"
                + ", createTime='" + getCreateTime() + "'" + "}";
    }
}
