package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * VCItem VC列表
 */
@Entity
@Table(name = "vc_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * VC編號
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "serial_no", length = 50, nullable = false)
    private String serialNo;

    /**
     * VC名稱
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * 建立者
     */
    @NotNull
    @Column(name = "cr_user", nullable = false)
    private Long crUser;

    /**
     * 建立時間
     */
    @NotNull
    @Column(name = "cr_datetime", nullable = false)
    private Instant crDatetime;

    /**
     * 類別Id
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 單位
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "business_id", length = 255, nullable = false)
    private String businessId;

    /**
     * 上鏈 schema id
     */
    @Size(max = 255)
    @Column(name = "schema_id", length = 255)
    private String schemaId;

    /**
     * 上鏈 schema
     */
    @Column(name = "schema")
    private String schema;

    /**
     * 上鏈 txhash
     */
    @Column(name = "tx_hash")
    private String txHash;

    /**
     * 上鏈 metadata
     */
    @Column(name = "metadata")
    private String metadata;

    /**
     * 過期時間單位類型
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "unit_type_expire", length = 10, nullable = false)
    private String unitTypeExpire;

    /**
     * 過期時間單位長度
     */
    @NotNull
    @Column(name = "length_expire", nullable = false)
    private Integer lengthExpire;

    @NotNull
    @Column(name = "used", nullable = false)
    private Boolean used;

    /**
     * 卡面圖片(base64)
     */
    @Column(name = "card_cover")
    private String cardCover;

    /**
     * 卡面 url
     */
    @Column(name = "card_cover_url")
    private String cardCoverUrl;

    // /**
    //  * 對應的vc模版選用的資料介接類型(501i|901i)
    //  */
    // @Size(max = 20)
    // @Column(name = "api_type", length = 20)
    // private String apiType;

    /**
     * 若為 901i 呼叫時 header 要帶的內容
     */
    @Column(name = "headers")
    private String headers;

    /**
     * 若為 901i 呼叫的 url
     */
    @Column(name = "url")
    private String url;

    /**
     * 901i用的httpMethod
     */
    @Size(max = 10)
    @Column(name = "http_method", length = 10)
    private String httpMethod;

    /**
     * 是否公開
     */
    @Column(name = "expose", nullable = true)
    private Boolean expose;

    @ManyToOne
    @JoinColumn(name = "business_id", referencedColumnName = "org_id", insertable = false, updatable = false)
    private Org org;
    
    /**
     * IAL 等級標記
     */
    @Size(max = 1)
    @Column(name = "ial", length = 1)
    private String ial;

    /**
     * 是否需要驗證碼模式
     */
    @Column(name = "is_verify")
    private Boolean isVerify;

    /**
     * 開啟網頁方式(1: 外開網頁、2: webview)
     */
    @Size(max = 1)
    @Column(name = "type", length = 1)
    private String type;

    /**
     * 發證端 KYC 與輸入資料的 url
     */
    @Column(name = "issuer_service_url")
    private String issuerServiceUrl;

    /**
     * 是否為暫存狀態
     */
    @Column(name = "is_temp")
    private Boolean isTemp;

    /**
     * 是否啟用
     */
    @Column(name = "activated")
    private Boolean activated;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VCItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNo() {
        return this.serialNo;
    }

    public VCItem serialNo(String serialNo) {
        this.setSerialNo(serialNo);
        return this;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return this.name;
    }

    public VCItem name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCrUser() {
        return this.crUser;
    }

    public VCItem crUser(Long crUser) {
        this.setCrUser(crUser);
        return this;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public VCItem crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public VCItem categoryId(Long categoryId) {
        this.setCategoryId(categoryId);
        return this;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public VCItem businessId(String businessId) {
        this.setBusinessId(businessId);
        return this;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getSchemaId() {
        return this.schemaId;
    }

    public VCItem schemaId(String schemaId) {
        this.setSchemaId(schemaId);
        return this;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getSchema() {
        return this.schema;
    }

    public VCItem schema(String schema) {
        this.setSchema(schema);
        return this;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTxHash() {
        return this.txHash;
    }

    public VCItem txHash(String txHash) {
        this.setTxHash(txHash);
        return this;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public VCItem metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getUnitTypeExpire() {
        return this.unitTypeExpire;
    }

    public VCItem unitTypeExpire(String unitTypeExpire) {
        this.setUnitTypeExpire(unitTypeExpire);
        return this;
    }

    public void setUnitTypeExpire(String unitTypeExpire) {
        this.unitTypeExpire = unitTypeExpire;
    }

    public Integer getLengthExpire() {
        return this.lengthExpire;
    }

    public VCItem lengthExpire(Integer lengthExpire) {
        this.setLengthExpire(lengthExpire);
        return this;
    }

    public void setLengthExpire(Integer lengthExpire) {
        this.lengthExpire = lengthExpire;
    }

    public Boolean getUsed() {
        return this.used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public String getCardCover() {
        return this.cardCover;
    }

    public void setCardCover(String cardCover) {
        this.cardCover = cardCover;
    }

    public VCItem cardCover(String cardCover) {
        this.cardCover = cardCover;
        return this;
    }

    public String getCardCoverUrl() {
        return this.cardCoverUrl;
    }

    public void setCardCoverUrl(String cardCoverUrl) {
        this.cardCoverUrl = cardCoverUrl;
    }

    public VCItem cardCoverUrl(String cardCoverUrl) {
        this.cardCoverUrl = cardCoverUrl;
        return this;
    }

    // public String getApiType() {
    //     return this.apiType;
    // }
    //
    // public VCItem apiType(String apiType) {
    //     this.apiType = apiType;
    //     return this;
    // }

    // public void setApiType(String apiType) {
    //     this.apiType = apiType;
    // }

    public String getHeaders() {
        return this.headers;
    }

    public VCItem headers(String headers) {
        this.headers = headers;
        return this;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return this.url;
    }

    public VCItem url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public VCItem httpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Boolean getExpose() {
        return expose;
    }

    public void setExpose(Boolean exposed) {
        this.expose = exposed;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }
    
    public String getIal() {
        return this.ial;
    }

    public VCItem ial(String ial) {
        this.setIal(ial);
        return this;
    }

    public void setIal(String ial) {
        this.ial = ial;
    }

    public Boolean getIsVerify() {
        return this.isVerify;
    }

    public VCItem isVerify(Boolean isVerify) {
        this.setIsVerify(isVerify);
        return this;
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
        return this.isTemp;
    }

    public VCItem isTemp(Boolean isTemp) {
        this.setIsTemp(isTemp);
        return this;
    }

    public void setIsTemp(Boolean isTemp) {
        this.isTemp = isTemp;
    }

    public Boolean getActivated() {
        return activated;
    }

    public VCItem activated(Boolean activated) {
        this.setActivated(activated);
        return this;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCItem)) {
            return false;
        }
        return getId() != null && getId().equals(((VCItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItem{" +
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
            ", expose='" + getExpose() + "'" +
            ", ial='" + getIal() + "'" +
            ", isVerify='" + getIsVerify() + "'" +
            ", type='" + getType() + "'" +
            ", issuerServiceUrl='" + getIssuerServiceUrl() + "'" +
            ", isTemp='" + getIsTemp() + "'" +
            ", activated='" + getActivated() + "'" +
            "}";
    }
}
