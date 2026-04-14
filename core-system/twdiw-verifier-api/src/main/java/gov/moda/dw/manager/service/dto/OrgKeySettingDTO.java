package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.OrgKeySetting} entity.
 */
@Schema(description = "OrgKeySetting 組織金鑰設定")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrgKeySettingDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "組織代碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgId;

    @NotNull
    @Size(max = 50)
    @Schema(description = "金鑰代碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keyId;

    @Size(max = 100)
    @Schema(description = "金鑰備註")
    private String description;

    @Schema(description = "Curve25519 公鑰", requiredMode = Schema.RequiredMode.REQUIRED)
    @Lob
    private String publicKey;

    @Schema(description = "Curve25519 私鑰")
    @Lob
    private String privateKey;

    @Schema(description = "TOTP 金鑰", requiredMode = Schema.RequiredMode.REQUIRED)
    @Lob
    private String totpKey;

    @Schema(description = "HMAC 金鑰", requiredMode = Schema.RequiredMode.REQUIRED)
    @Lob
    private String hmacKey;

    @NotNull
    @Schema(description = "是否啟用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isActive;

    @NotNull
    @Schema(description = "建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant crDatetime;

    @Schema(description = "修改時間")
    private Instant upDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getTotpKey() {
        return totpKey;
    }

    public void setTotpKey(String totpKey) {
        this.totpKey = totpKey;
    }

    public String getHmacKey() {
        return hmacKey;
    }

    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCrDatetime() {
        return crDatetime;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Instant getUpDatetime() {
        return upDatetime;
    }

    public void setUpDatetime(Instant upDatetime) {
        this.upDatetime = upDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgKeySettingDTO)) {
            return false;
        }

        OrgKeySettingDTO orgKeySettingDTO = (OrgKeySettingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orgKeySettingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgKeySettingDTO{" +
            "id=" + getId() +
            ", orgId='" + getOrgId() + "'" +
            ", keyId='" + getKeyId() + "'" +
            ", description='" + getDescription() + "'" +
            ", publicKey='" + getPublicKey() + "'" +
            ", privateKey='" + getPrivateKey() + "'" +
            ", totpKey='" + getTotpKey() + "'" +
            ", hmacKey='" + getHmacKey() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", upDatetime='" + getUpDatetime() + "'" +
            "}";
    }
}
