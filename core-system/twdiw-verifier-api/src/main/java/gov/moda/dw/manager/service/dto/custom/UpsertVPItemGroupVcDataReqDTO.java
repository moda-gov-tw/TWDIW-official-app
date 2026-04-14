package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "VP 群組所選 VC 模板")
public class UpsertVPItemGroupVcDataReqDTO {

    @Schema(description = "是否選擇模板")
    @JsonProperty("isTicked")
    private Boolean isTicked;

    @NotBlank
    @Schema(description = "模板代碼", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("serialNo")
    private String serialNo;

    @NotBlank
    @Schema(description = "模板名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    private String name;

    @NotBlank
    @Schema(description = "模板建立者統編", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("businessId")
    private String businessId;

    @NotBlank
    @Schema(description = "模板建立者組織中文名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("businessName")
    private String businessName;

    @Valid
    @NotEmpty
    @Schema(description = "模板欄位", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("vcFields")
    private List<UpsertVPItemGroupVcDataFieldReqDTO> vcFields;

}
