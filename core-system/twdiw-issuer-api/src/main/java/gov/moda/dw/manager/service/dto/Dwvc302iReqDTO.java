package gov.moda.dw.manager.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvc302iReqDTO {

    @JsonProperty("action")
    private String action;

    @JsonProperty("cids")
    private List<String> cids;

}
