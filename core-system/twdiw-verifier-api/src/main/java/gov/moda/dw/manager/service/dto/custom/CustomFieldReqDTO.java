package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "靜態 QR Code 模式欄位")
public class CustomFieldReqDTO {

    @Size(max = 12)
    @Schema(description = "描述")
    @JsonProperty("description")
    private String description;

    @Schema(description = "欄位對外名稱")
    @JsonProperty("cname")
    private String cname;

    @Schema(description = "欄位名稱(英)")
    @JsonProperty("ename")
    private String ename;

    @Schema(description = "限定資料格式")
    @JsonProperty("regex")
    private String regex;

    @Schema(description = "資料內容")
    @JsonProperty("value")
    private String value;
}
