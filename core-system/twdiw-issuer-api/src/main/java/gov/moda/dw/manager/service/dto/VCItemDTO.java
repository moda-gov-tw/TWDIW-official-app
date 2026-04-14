package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VCItem} entity.
 */
@Schema(description = "VCItem VC列表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VC編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNo;

    @NotNull
    @Size(max = 18)
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

    /**
     * 卡面圖片(base64)
     */
    @Schema(description = "卡面圖片(base64)")
    private String cardCover;

    /**
     * 卡面 url
     */
    @Schema(description = "卡面 url")
    private String cardCoverUrl;

    // /**
    //  * 對應的vc模版選用的資料介接類型(501i|901i)
    //  */
    // @Size(max = 20)
    // @Schema(description = "對應的vc模版選用的資料介接類型(501i|901i)")
    // private String apiType;

    /**
     * 若為 901i 呼叫時 header 要帶的內容
     */
    @Schema(description = "若為 901i 呼叫時 header 要帶的內容")
    private String headers;

    /**
     * 若為 901i 呼叫的 url
     */
    @Schema(description = "若為 901i 呼叫的 url")
    private String url;

    /**
     * 901i用的httpMethod
     */
    @Size(max = 10)
    @Schema(description = "901i用的httpMethod")
    private String httpMethod;

    @Schema(description = "組織英文名")
    private String businessEngName;

    @Schema(description = "組織臺灣名")
    private String businessTWName;

    @Schema(description = "登入者是否為建立者")
    private boolean owner;
    
    @Size(max = 1)
    @Schema(description = "IAL 等級標記")
    private String ial;

    @Schema(description = "是否需要驗證碼模式")
    private Boolean isVerify;
    
    @Size(max = 1)
    @Schema(description = "開啟網頁方式(1: 外開網頁、2: webview)")
    private String type;

    @Schema(description = "發證端 KYC 與輸入資料的 url")
    private String issuerServiceUrl;

    @Schema(description = "是否為暫存狀態")
    private Boolean isTemp;

    @Schema(description = "是否啟用")
    private Boolean activated;

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

    public String getCardCover() {
        return cardCover;
    }

    public void setCardCover(String cardCover) {
        this.cardCover = cardCover;
    }

    public String getCardCoverUrl() {
        return cardCoverUrl;
    }

    public void setCardCoverUrl(String cardCoverUrl) {
        this.cardCoverUrl = cardCoverUrl;
    }

    // public String getApiType() {
    //     return apiType;
    // }

    // public void setApiType(String apiType) {
    //     this.apiType = apiType;
    // }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public boolean isUsed() {
        return used;
    }

    public String getBusinessTWName() {
        return businessTWName;
    }

    public void setBusinessTWName(String businessTWName) {
        this.businessTWName = businessTWName;
    }

    public String getBusinessEngName() {
        return businessEngName;
    }

    public void setBusinessEngName(String businessEngName) {
        this.businessEngName = businessEngName;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
    public String getIal() {
        return ial;
    }

    public void setIal(String ial) {
        this.ial = ial;
    }

    public Boolean getIsVerify() {
        return isVerify;
    }
    
    public void setIsVerify(Boolean isVerify) {
        this.isVerify = isVerify;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssuerServiceUrl() {
        return issuerServiceUrl;
    }

    public void setIssuerServiceUrl(String issuerServiceUrl) {
        this.issuerServiceUrl = issuerServiceUrl;
	}

    public Boolean getIsTemp() {
        return isTemp;
    }

    public void setIsTemp(Boolean isTemp) {
        this.isTemp = isTemp;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCItemDTO)) {
            return false;
        }

        VCItemDTO vCItemDTO = (VCItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vCItemDTO.id);
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
            ", cardCover='" + getCardCover() + "'" +
            ", cardCoverUrl='" + getCardCoverUrl() + "'" +
            // ", apiType='" + getApiType() + "'" +
            ", headers='" + getHeaders() + "'" +
            ", url='" + getUrl() + "'" +
            ", httpMethod='" + getHttpMethod() + "'" +
            ", ial='" + getIal() + "'" +
            ", isVerify='" + getIsVerify() + "'" +
            ", type='" + getType() + "'" +
            ", issuerServiceUrl='" + getIssuerServiceUrl() + "'" +
            ", isTemp='" + getIsTemp() + "'" +
            ", activated='" + getActivated() + "'" +
            "}";
    }
}
