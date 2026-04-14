package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "VP 群組所選 VC 模板欄位")
public class UpsertVPItemGroupVcDataFieldReqDTO {

    @NotBlank
    @Schema(description = "欄位中文名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("cname")
    private String cname;

    @NotBlank
    @Schema(description = "欄位英文名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("ename")
    private String ename;

    @Schema(description = "是否必填")
    @JsonProperty("isRequired")
    private Boolean isRequired;

    @Schema(description = "是否選擇模板")
    @JsonProperty("isTicked")
    private Boolean isTicked;
    
    @Schema(description = "自定義欄位名稱")
    @JsonProperty("customFieldName")
    private String customFieldName;

}
