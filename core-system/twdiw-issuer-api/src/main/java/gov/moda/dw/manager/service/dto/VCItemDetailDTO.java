package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class VCItemDetailDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VC編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNo;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VC名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "建立者", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long crUser;

    @Schema(description = "建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant crDatetime;

    @Schema(description = "類別Id")
    private Long categoryId;

    @Size(max = 255)
    @Schema(description = "單位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessId;

    @Size(max = 255)
    @Schema(description = "上鏈 schema id")
    private String schemaId;

    @Schema(description = "上鏈 schema")
    @Lob
    private String schema;

    @Schema(description = "上鏈 txhash")
    @Lob
    private String txHash;

    @Schema(description = "上鏈 metadata")
    @Lob
    private String metadata;

    public @NotNull @Size(max = 10) String getUnitTypeExpire() {
        return unitTypeExpire;
    }

    public void setUnitTypeExpire(@NotNull @Size(max = 10) String unitTypeExpire) {
        this.unitTypeExpire = unitTypeExpire;
    }

    @NotNull
    @Size(max = 10)
    @Schema(description = "過期時間單位類型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String unitTypeExpire;

    public @NotNull Integer getLengthExpire() {
        return lengthExpire;
    }

    public void setLengthExpire(@NotNull Integer lengthExpire) {
        this.lengthExpire = lengthExpire;
    }

    @NotNull
    @Schema(description = "過期時間單位長度", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lengthExpire;

    public List<VCItemFieldDTO> getVcItemFieldDTOList() {
        return vcItemFieldDTOList;
    }

    public void setVcItemFieldDTOList(List<VCItemFieldDTO> vcItemFieldDTOList) {
        this.vcItemFieldDTOList = vcItemFieldDTOList;
    }

    @Schema(description = "已使用")
    private boolean used;

    @Schema(description = "vc item field list")
    private List<VCItemFieldDTO> vcItemFieldDTOList;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public boolean getUsed() {
        return this.used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCItemDTO)) {
            return false;
        }

        VCItemDetailDTO vCItemDetailDTO = (VCItemDetailDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vCItemDetailDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemDTO{" +
            "id=" + getId() +
            ", serialNo='" + getSerialNo() + "'" +
            ", name='" + getName() + "'" +
            ", crUser=" + getCrUser() +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", categoryId=" + getCategoryId() +
            ", businessId='" + getBusinessId() + "'" +
            ", schemaId='" + getSchemaId() + "'" +
            ", schema='" + getSchema() + "'" +
            ", txHash='" + getTxHash() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", unitTypeExpire='" + getUnitTypeExpire() + "'" +
            ", lengthExpire=" + getLengthExpire() +
            ", used='" + getUsed() + "'" +
            "}";
    }
}
