package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvp101iResDTO{

    @JsonProperty("vpItems")
    private List<Dwvp101iVpItemResDTO> vpItems;

    @Getter
    @Setter
    public static class Dwvp101iVpItemResDTO {

        @JsonProperty("vpUid")
        private String vpUid;

        @JsonProperty("name")
        private String name;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("verifierServiceUrl")
        private String verifierServiceUrl;

        @JsonProperty("isStatic")
        private Boolean isStatic;

        @JsonProperty("isOffline")
        private Boolean isOffline;

        @JsonProperty("custom")
        private Dwvp101iResFieldsDTO custom;

    }

    @Getter
    @Setter
    public static class Dwvp101iResFieldsDTO {

        @JsonProperty("fields")
        private List<CustomFieldReqDTO> fields;

    }

}
