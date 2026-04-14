package gov.moda.dw.manager.domain.outside.vdr.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectCategoryVCItemReq extends SelectCategoryReq{

    @JsonProperty("serialNo")
    String serialNo;
}
