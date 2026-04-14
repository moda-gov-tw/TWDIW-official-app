package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPItem} entity.
 */
@Schema(description = "VPItem VP列表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VP編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNo;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VP名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @Schema(description = "建立者", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long crUser;

    @NotNull
    @Schema(description = "建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant crDatetime;

    @NotNull
    @Size(max = 255)
    @Schema(description = "單位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessId;

    @Schema(description = "呈現定義", requiredMode = Schema.RequiredMode.REQUIRED)
    @Lob
    private String presentationDefinition;

    @NotNull
    @Size(max = 100)
    @Schema(description = "驗證目的", requiredMode = Schema.RequiredMode.REQUIRED)
    private String purpose;

    @Schema(description = "使用者條款", requiredMode = Schema.RequiredMode.REQUIRED)
    @Lob
    private String terms;

    @Schema(description = "修改者")
    private Long upUser;

    @Schema(description = "修改時間")
    private Instant upDatetime;

    @Schema(description = "建立群組資訊")
    @Lob
    private String groupInfo;

    @Schema(description = "是否為靜態 QRCode 模式")
    private Boolean isStatic;

    @Schema(description = "是否為 Offline 模式")
    private Boolean isOffline;

    @Size(max = 100)
    @Schema(description = "標籤")
    private String tag;

    @Schema(description = "欄位資訊")
    @Lob
    private String fieldInfo;

    @Schema(description = "驗證端模組 url")
    @Lob
    private String verifierServiceUrl;

    @Schema(description = "組織業務系統 url")
    @Lob
    private String callbackUrl;

    @Schema(description = "模組加密")
    private Boolean isEncryptEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCrUser() {
        return crUser;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return crDatetime;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getPresentationDefinition() {
        return presentationDefinition;
    }

    public void setPresentationDefinition(String presentationDefinition) {
        this.presentationDefinition = presentationDefinition;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Long getUpUser() {
        return upUser;
    }

    public void setUpUser(Long upUser) {
        this.upUser = upUser;
    }

    public Instant getUpDatetime() {
        return upDatetime;
    }

    public void setUpDatetime(Instant upDatetime) {
        this.upDatetime = upDatetime;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
	}

    public Boolean getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Boolean getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(Boolean isOffline) {
        this.isOffline = isOffline;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(String fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getVerifierServiceUrl() {
        return verifierServiceUrl;
    }

    public void setVerifierServiceUrl(String verifierServiceUrl) {
        this.verifierServiceUrl = verifierServiceUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Boolean getIsEncryptEnabled() {
        return isEncryptEnabled;
    }

    public void setIsEncryptEnabled(Boolean isEncryptEnabled) {
        this.isEncryptEnabled = isEncryptEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VPItemDTO)) {
            return false;
        }

        VPItemDTO vPItemDTO = (VPItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vPItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPItemDTO{" +
            "id=" + getId() +
            ", serialNo='" + getSerialNo() + "'" +
            ", name='" + getName() + "'" +
            ", crUser=" + getCrUser() +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", businessId='" + getBusinessId() + "'" +
            ", presentationDefinition='" + getPresentationDefinition() + "'" +
            ", purpose='" + getPurpose() + "'" +
            ", terms='" + getTerms() + "'" +
            ", upUser=" + getUpUser() +
            ", upDatetime='" + getUpDatetime() + "'" +
            ", groupInfo='" + getGroupInfo() + "'" +
            ", isStatic='" + getIsStatic() + "'" +
            ", isOffline='" + getIsOffline() + "'" +
            ", tag='" + getTag() + "'" +
            ", fieldInfo='" + getFieldInfo() + "'" +
            ", verifierServiceUrl='" + getVerifierServiceUrl() + "'" +
            ", callbackUrl='" + getCallbackUrl() + "'" +
            ", isEncryptEnabled='" + getIsEncryptEnabled() + "'" +
            "}";
    }
}
