package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class CustomVCItemSettingDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "vc id")
    @NotNull
    private Long vcId;

    @Schema(description = "vc serial no")
    @NotNull
    private String vcSerialNo;

    // @NotNull
    // @Schema(description = "api類型")
    // private String apiType;

    @Schema(description = "901i呼叫api時要帶的header")
    private String headers;

    @NotNull
    @Schema(description = "901i呼叫api時的url")
    private String url;

    @NotNull
    @Schema(description = "901i呼叫時的httpMethod")
    private String httpMethod;

    public Long getVcId() {
        return vcId;
    }

    public void setVcId(Long vcId) {
        this.vcId = vcId;
    }

    public String getVcSerialNo() {
        return vcSerialNo;
    }

    public void setVcSerialNo(String vcSerialNo) {
        this.vcSerialNo = vcSerialNo;
    }

    // public String getApiType() {
    //     return apiType;
    // }
    //
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

    @Override
    public String toString() {
        return (
            "CustomVCItemSettingDTO{" +
            "vcId=" +
            vcId +
            ", vcSerialNo='" +
            vcSerialNo +
            '\'' +
            // ", apiType='" +
            // apiType +
            // '\'' +
            ", header='" +
            headers +
            '\'' +
            ", url='" +
            url +
            '\'' +
            ", httpMethod='" +
            httpMethod +
            '\'' +
            '}'
        );
    }
}
