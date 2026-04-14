
package gov.moda.dw.manager.service.dto.custom.frontend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 已上鏈 VC Schema Data.Schemas.Schema.Schema 列表 Response
 */
@Getter
@Setter
public class SchemaDetail {

    @JsonProperty("$schema")
    private String schemaLink;

    @JsonProperty("description")
    private String description;

    @JsonProperty("type")
    private String type;

}
