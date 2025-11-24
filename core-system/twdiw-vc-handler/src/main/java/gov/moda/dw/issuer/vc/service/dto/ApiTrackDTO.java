package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.domain.ApiTrack;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ApiTrack} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiTrackDTO implements Serializable {

  private Long id;

  @NotNull
  @Size(max = 255)
  @Schema(description = "唯一識別碼", requiredMode = Schema.RequiredMode.REQUIRED)
  private String uuid;

  @Schema(description = "時間戳")
  private Instant timestamp;

  @Size(max = 255)
  @Schema(description = "來源位置")
  private String source;

  @Size(max = 255)
  @Schema(description = "服務類型")
  private String serviceId;

  @Size(max = 255)
  @Schema(description = "統一資源識別碼")
  private String uri;

  @Size(max = 255)
  @Schema(description = "統一資源定位符")
  private String url;

  @Size(max = 255)
  @Schema(description = "狀態碼")
  private String statusCode;

  @Size(max = 255)
  @Schema(description = "執行耗時(毫秒)")
  private String rtt;

  @Schema(description = "請求標頭")
//  @Lob
  private String requestHeader;

  @Schema(description = "請求參數")
//  @Lob
  private String requestParam;

  @Schema(description = "請求內容")
//  @Lob
  private String requestBody;

  @Size(max = 255)
  @Schema(description = "請求方法")
  private String requestMethod;

  @Schema(description = "回應標頭")
//  @Lob
  private String responseHeader;

  @Schema(description = "回應內容")
//  @Lob
  private String responseBody;

  @Size(max = 255)
  @Schema(description = "存取憑證1")
  private String accessToken1;

  @Size(max = 255)
  @Schema(description = "存取憑證2")
  private String accessToken2;

  @Size(max = 255)
  @Schema(description = "使用者")
  private String jhiFrom;

  @Size(max = 255)
  @Schema(description = "服務端")
  private String jhiTo;

  @Size(max = 255)
  @Schema(description = "花費")
  private String cost;

  @Size(max = 10)
  @Schema(description = "是否已完結")
  private String charged;

  @Size(max = 10)
  @Schema(description = "是否已同步")
  private String synced;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  public String getRtt() {
    return rtt;
  }

  public void setRtt(String rtt) {
    this.rtt = rtt;
  }

  public String getRequestHeader() {
    return requestHeader;
  }

  public void setRequestHeader(String requestHeader) {
    this.requestHeader = requestHeader;
  }

  public String getRequestParam() {
    return requestParam;
  }

  public void setRequestParam(String requestParam) {
    this.requestParam = requestParam;
  }

  public String getRequestBody() {
    return requestBody;
  }

  public void setRequestBody(String requestBody) {
    this.requestBody = requestBody;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  public String getResponseHeader() {
    return responseHeader;
  }

  public void setResponseHeader(String responseHeader) {
    this.responseHeader = responseHeader;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public void setResponseBody(String responseBody) {
    this.responseBody = responseBody;
  }

  public String getAccessToken1() {
    return accessToken1;
  }

  public void setAccessToken1(String accessToken1) {
    this.accessToken1 = accessToken1;
  }

  public String getAccessToken2() {
    return accessToken2;
  }

  public void setAccessToken2(String accessToken2) {
    this.accessToken2 = accessToken2;
  }

  public String getJhiFrom() {
    return jhiFrom;
  }

  public void setJhiFrom(String jhiFrom) {
    this.jhiFrom = jhiFrom;
  }

  public String getJhiTo() {
    return jhiTo;
  }

  public void setJhiTo(String jhiTo) {
    this.jhiTo = jhiTo;
  }

  public String getCost() {
    return cost;
  }

  public void setCost(String cost) {
    this.cost = cost;
  }

  public String getCharged() {
    return charged;
  }

  public void setCharged(String charged) {
    this.charged = charged;
  }

  public String getSynced() {
    return synced;
  }

  public void setSynced(String synced) {
    this.synced = synced;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ApiTrackDTO)) {
      return false;
    }

    ApiTrackDTO apiTrackDTO = (ApiTrackDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, apiTrackDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ApiTrackDTO{" +
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
