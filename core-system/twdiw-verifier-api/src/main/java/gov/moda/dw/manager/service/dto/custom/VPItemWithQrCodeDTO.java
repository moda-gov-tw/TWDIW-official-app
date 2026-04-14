package gov.moda.dw.manager.service.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPItem} entity.
 */
@Schema(description = "VPItem VP列表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemWithQrCodeDTO implements Serializable {

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
    @Size(max = 10)
    @Schema(description = "單位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessId;

    @Schema(description = "呈現定義", requiredMode = Schema.RequiredMode.REQUIRED)
    @Lob
    private String presentationDefinition;

    @Schema(description = "單位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String QrCode;

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

    public String getQrCode() {
        return QrCode;
    }

    public void setQrCode(String qrCode) {
        QrCode = qrCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VPItemWithQrCodeDTO that = (VPItemWithQrCodeDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(serialNo, that.serialNo) && Objects.equals(name, that.name) && Objects.equals(crUser, that.crUser) && Objects.equals(crDatetime, that.crDatetime) && Objects.equals(businessId, that.businessId) && Objects.equals(presentationDefinition, that.presentationDefinition) && Objects.equals(QrCode, that.QrCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serialNo, name, crUser, crDatetime, businessId, presentationDefinition, QrCode);
    }
}
