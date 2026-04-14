package gov.moda.dw.manager.service.dto.custom.ext.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputDescriptorNameDTO {

    @JsonProperty("org_tw_name")
    private String orgTwName; // 組織中文名稱

    @JsonProperty("vc_name")
    private String vcName; // VC名稱
}