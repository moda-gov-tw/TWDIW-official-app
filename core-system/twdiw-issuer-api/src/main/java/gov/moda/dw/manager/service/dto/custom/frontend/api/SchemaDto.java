
package gov.moda.dw.manager.service.dto.custom.frontend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 已上鏈 VC Schema Data.Schemas.Schema 列表 Response
 */
@Getter
@Setter
public class SchemaDto {

    @JsonProperty("$id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("$schema")
    private String schemaLink;

    @JsonProperty("type")
    private String type;

    @JsonProperty("schema")
    private SchemaDetail schemaDetail;

}
