package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class VC901iTestRequestDTO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "vc id")
    @NotNull
    private Long vcId;

    @Schema(description = "vc serial no")
    @NotNull
    private String vcSerialNo;

    @Schema(description = "901i url")
    @NotNull
    private String url901i;

    @Schema(description = "901i headers")
    @NotNull
    private String headers;

    @Schema(description = "901i http method")
    @NotNull
    private String httpMethod;

    //    @Schema(description = "901i request credential type")
    //    private String credential_type;
    //
    //    @Schema(description = "901i request nonce")
    //    private String nonce;
    //
    //    @Schema(description = "901i request sub")
    //    private String sub;

    public String getUrl901i() {
        return url901i;
    }

    public void setUrl901i(String url901i) {
        this.url901i = url901i;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

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

    @Override
    public String toString() {
        return (
            "VC901iTestRequestDTO{" +
            "vcId=" +
            vcId +
            ", vcSerialNo='" +
            vcSerialNo +
            '\'' +
            ", url901i='" +
            url901i +
            '\'' +
            ", headers='" +
            headers +
            '\'' +
            ", httpMethod='" +
            httpMethod +
            '\'' +
            '}'
        );
    }
}
