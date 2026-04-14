package gov.moda.dw.manager.domain.outside.vdr.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectCategoryReq {

    @JsonProperty("taxId")
    String taxId;

}
