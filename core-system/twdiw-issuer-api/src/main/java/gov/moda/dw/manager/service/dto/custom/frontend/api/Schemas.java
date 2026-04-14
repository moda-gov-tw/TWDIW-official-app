
package gov.moda.dw.manager.service.dto.custom.frontend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 已上鏈 VC Schema Data.Schemas 列表 Response
 */
@Getter
@Setter
public class Schemas {

    @JsonProperty("schema")
    private SchemaDto schema;

}
