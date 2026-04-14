package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "VPItem VP列表檢核")
public class ValidVPItemByStepReqDTO extends UpsertVPItemReqDTO {

    @NotNull
    @Schema(description = "步驟", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("step")
    private Integer step;

    @NotNull
    @Schema(description = "是否為編輯", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("isEdit")
    private Boolean isEdit;

}
