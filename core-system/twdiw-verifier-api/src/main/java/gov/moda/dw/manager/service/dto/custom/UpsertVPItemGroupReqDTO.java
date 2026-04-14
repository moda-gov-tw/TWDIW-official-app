package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "VP 群組")
public class UpsertVPItemGroupReqDTO {

    @NotBlank
    @Size(max = 14)
    @Schema(description = "群組名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    private String name;

    @NotBlank
    @Schema(
            description = "群組規則(all: 從 group 中選擇全部、pick: 從 group A 中最多選取 max 個)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("rule")
    private String rule;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(
            description = "群組規則(rule = all 無 count、rule = pick 有 count)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("max")
    private Long max;

    @Valid
    @NotEmpty
    @Schema(description = "VC 模板", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("vcDatas")
    private List<UpsertVPItemGroupVcDataReqDTO> vcDatas;

}
