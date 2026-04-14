package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvc101iResDTO {

    /**
     * VC 模板 list
     */
    @JsonProperty("vcItems")
    private List<Dwvc101iVcItemsDTO> vcItems;

}
