
package gov.moda.dw.manager.service.dto.custom.frontend.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 已上鏈 VC Schema Data 列表 Response
 */
@Getter
@Setter
public class Data {

    @JsonProperty("count")
    private Long count;

    @JsonProperty("schemas")
    private List<Schemas> schemas;

}
