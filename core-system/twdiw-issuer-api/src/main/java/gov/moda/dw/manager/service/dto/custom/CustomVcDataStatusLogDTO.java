package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomVcDataStatusLogDTO {

    @Schema(description = "狀態 (0、1、2)")
    private String status;

    @Schema(description = "狀態 (有效、停用、撤銷)")
    private String statusName;

    @Schema(description = "最後更新時間")
    private Instant logDatetime;

}
