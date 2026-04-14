
package gov.moda.dw.manager.service.dto.custom.frontend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 已上鏈 VC Schema 列表 Response
 */
@Getter
@Setter
public class VCSchemaResDto {

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private Data data;

}
