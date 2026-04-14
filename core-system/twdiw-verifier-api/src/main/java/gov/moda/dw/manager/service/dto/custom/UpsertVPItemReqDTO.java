package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "VPItem VP列表")
public class UpsertVPItemReqDTO {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "VP編號", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("serialNo")
    private String serialNo;

    @NotBlank
    @Size(max = 16)
    @Schema(description = "VP 名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    private String name;

    @NotBlank
    @Size(max = 30)
    @Schema(description = "VP 目的", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("purpose")
    private String purpose;

    @NotBlank
    @Schema(description = "VP 條款", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("terms")
    private String terms;

    @Schema(description = "VP 群組", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("groups")
    private List<UpsertVPItemGroupReqDTO> groups;

    @Schema(description = "模式")
    @JsonProperty("model")
    private String model;

    @Schema(description = "組織業務系統URL")
    @JsonProperty("verifierServiceUrl")
    private String verifierServiceUrl;

    @Schema(description = "Call Back URL")
    @JsonProperty("callBackUrl")
    private String callBackUrl;

    @Size(max = 18)
    @Schema(description = "標籤")
    @JsonProperty("tag")
    private String tag;

    @Schema(description = "模組加密")
    @JsonProperty("isEncryptEnabled")
    private Boolean isEncryptEnabled;

    @JsonProperty("isCustomFields")
    private Boolean isCustomFields;

    @Schema(description = "靜態 QR Code 模式欄位")
    @JsonProperty("fields")
    private List<CustomFieldReqDTO> fields;

}
