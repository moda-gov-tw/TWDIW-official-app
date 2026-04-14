package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "VPItem VP 列表")
public class GetVPItemDataResDTO {

    @Schema(description = "VP 模板 id")
    @JsonProperty("vpItemId")
    private Long vpItemId;

    @Schema(description = "VP 編號")
    @JsonProperty("serialNo")
    private String serialNo;

    @Schema(description = "VP 名稱")
    @JsonProperty("name")
    private String name;

    @Schema(description = "VP 目的")
    @JsonProperty("purpose")
    private String purpose;

    @Schema(description = "VP 條款")
    @JsonProperty("terms")
    private String terms;

    @Schema(description = "VP 群組")
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
    
    @Schema(description = "標籤")
    @JsonProperty("tag")
    private String tag;
    
    @Schema(description = "模組加密")
    @JsonProperty("isEncryptEnabled")
    private Boolean isEncryptEnabled;
    
    @Schema(description = "Offline 模式欄位")
    @JsonProperty("fields")
    private List<CustomFieldReqDTO> fields;

}
