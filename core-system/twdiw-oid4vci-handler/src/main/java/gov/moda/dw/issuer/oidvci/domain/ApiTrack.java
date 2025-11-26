package gov.moda.dw.issuer.oidvci.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ApiTrack.
 */
@Entity
@Table(name = "api_track", schema = "public")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiTrack implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 唯一識別碼
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "uuid", length = 255, nullable = false)
    private String uuid;

    /**
     * 時間戳
     */
    @Column(name = "timestamp")
    private Instant timestamp;

    /**
     * 來源位置
     */
    @Size(max = 255)
    @Column(name = "source", length = 255)
    private String source;

    /**
     * 服務類型
     */
    @Size(max = 255)
    @Column(name = "service_id", length = 255)
    private String serviceId;

    /**
     * 統一資源識別碼
     */
    @Size(max = 255)
    @Column(name = "uri", length = 255)
    private String uri;

    /**
     * 統一資源定位符
     */
    @Size(max = 255)
    @Column(name = "url", length = 255)
    private String url;

    /**
     * 狀態碼
     */
    @Size(max = 255)
    @Column(name = "status_code", length = 255)
    private String statusCode;

    /**
     * 執行耗時(毫秒)
     */
    @Size(max = 255)
    @Column(name = "rtt", length = 255)
    private String rtt;

    /**
     * 請求標頭
     */
    //  @Lob
    @Column(name = "request_header")
    private String requestHeader;

    /**
     * 請求參數
     */
    //  @Lob
    @Column(name = "request_param")
    private String requestParam;

    /**
     * 請求內容
     */
    //  @Lob
    @Column(name = "request_body")
    private String requestBody;

    /**
     * 請求方法
     */
    @Size(max = 255)
    @Column(name = "request_method", length = 255)
    private String requestMethod;

    /**
     * 回應標頭
     */
    //  @Lob
    @Column(name = "response_header")
    private String responseHeader;

    /**
     * 回應內容
     */
    //  @Lob
    @Column(name = "response_body")
    private String responseBody;

    /**
     * 存取憑證1
     */
    @Size(max = 255)
    @Column(name = "access_token_1", length = 255)
    private String accessToken1;

    /**
     * 存取憑證2
     */
    @Size(max = 255)
    @Column(name = "access_token_2", length = 255)
    private String accessToken2;

    /**
     * 使用者
     */
    @Size(max = 255)
    @Column(name = "jhi_from", length = 255)
    private String jhiFrom;

    /**
     * 服務端
     */
    @Size(max = 255)
    @Column(name = "jhi_to", length = 255)
    private String jhiTo;

    /**
     * 花費
     */
    @Size(max = 255)
    @Column(name = "cost", length = 255)
    private String cost;

    /**
     * 是否已完結
     */
    @Size(max = 10)
    @Column(name = "charged", length = 10)
    private String charged;

    /**
     * 是否已同步
     */
    @Size(max = 10)
    @Column(name = "synced", length = 10)
    private String synced;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApiTrack id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public ApiTrack uuid(String uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public ApiTrack timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return this.source;
    }

    public ApiTrack source(String source) {
        this.setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public ApiTrack serviceId(String serviceId) {
        this.setServiceId(serviceId);
        return this;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUri() {
        return this.uri;
    }

    public ApiTrack uri(String uri) {
        this.setUri(uri);
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return this.url;
    }

    public ApiTrack url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public ApiTrack statusCode(String statusCode) {
        this.setStatusCode(statusCode);
        return this;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getRtt() {
        return this.rtt;
    }

    public ApiTrack rtt(String rtt) {
        this.setRtt(rtt);
        return this;
    }

    public void setRtt(String rtt) {
        this.rtt = rtt;
    }

    public String getRequestHeader() {
        return this.requestHeader;
    }

    public ApiTrack requestHeader(String requestHeader) {
        this.setRequestHeader(requestHeader);
        return this;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestParam() {
        return this.requestParam;
    }

    public ApiTrack requestParam(String requestParam) {
        this.setRequestParam(requestParam);
        return this;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getRequestBody() {
        return this.requestBody;
    }

    public ApiTrack requestBody(String requestBody) {
        this.setRequestBody(requestBody);
        return this;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public ApiTrack requestMethod(String requestMethod) {
        this.setRequestMethod(requestMethod);
        return this;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getResponseHeader() {
        return this.responseHeader;
    }

    public ApiTrack responseHeader(String responseHeader) {
        this.setResponseHeader(responseHeader);
        return this;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public ApiTrack responseBody(String responseBody) {
        this.setResponseBody(responseBody);
        return this;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getAccessToken1() {
        return this.accessToken1;
    }

    public ApiTrack accessToken1(String accessToken1) {
        this.setAccessToken1(accessToken1);
        return this;
    }

    public void setAccessToken1(String accessToken1) {
        this.accessToken1 = accessToken1;
    }

    public String getAccessToken2() {
        return this.accessToken2;
    }

    public ApiTrack accessToken2(String accessToken2) {
        this.setAccessToken2(accessToken2);
        return this;
    }

    public void setAccessToken2(String accessToken2) {
        this.accessToken2 = accessToken2;
    }

    public String getJhiFrom() {
        return this.jhiFrom;
    }

    public ApiTrack jhiFrom(String jhiFrom) {
        this.setJhiFrom(jhiFrom);
        return this;
    }

    public void setJhiFrom(String jhiFrom) {
        this.jhiFrom = jhiFrom;
    }

    public String getJhiTo() {
        return this.jhiTo;
    }

    public ApiTrack jhiTo(String jhiTo) {
        this.setJhiTo(jhiTo);
        return this;
    }

    public void setJhiTo(String jhiTo) {
        this.jhiTo = jhiTo;
    }

    public String getCost() {
        return this.cost;
    }

    public ApiTrack cost(String cost) {
        this.setCost(cost);
        return this;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCharged() {
        return this.charged;
    }

    public ApiTrack charged(String charged) {
        this.setCharged(charged);
        return this;
    }

    public void setCharged(String charged) {
        this.charged = charged;
    }

    public String getSynced() {
        return this.synced;
    }

    public ApiTrack synced(String synced) {
        this.setSynced(synced);
        return this;
    }

    public void setSynced(String synced) {
        this.synced = synced;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiTrack)) {
            return false;
        }
        return getId() != null && getId().equals(((ApiTrack) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiTrack{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", source='" + getSource() + "'" +
            ", serviceId='" + getServiceId() + "'" +
            ", uri='" + getUri() + "'" +
            ", url='" + getUrl() + "'" +
            ", statusCode='" + getStatusCode() + "'" +
            ", rtt='" + getRtt() + "'" +
            ", requestHeader='" + getRequestHeader() + "'" +
            ", requestParam='" + getRequestParam() + "'" +
            ", requestBody='" + getRequestBody() + "'" +
            ", requestMethod='" + getRequestMethod() + "'" +
            ", responseHeader='" + getResponseHeader() + "'" +
            ", responseBody='" + getResponseBody() + "'" +
            ", accessToken1='" + getAccessToken1() + "'" +
            ", accessToken2='" + getAccessToken2() + "'" +
            ", jhiFrom='" + getJhiFrom() + "'" +
            ", jhiTo='" + getJhiTo() + "'" +
            ", cost='" + getCost() + "'" +
            ", charged='" + getCharged() + "'" +
            ", synced='" + getSynced() + "'" +
            "}";
    }
}
