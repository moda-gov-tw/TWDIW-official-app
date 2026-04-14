package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.manager.service.dto.RelDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams351wReqDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("accessToken")
    private String authKey;

    @JsonProperty("accessTokenName")
    private String authKeyName;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("ownerName")
    private String ownerName;

    @JsonProperty("orgId")
    private String orgId;

    @JsonProperty("orgName")
    private String orgName;

    @JsonProperty("state")
    private String state;

    @JsonProperty("actype")
    private String actype;

    @JsonProperty("dataRole1")
    private String dataCategory1;

    @JsonProperty("createTime")
    private Instant createTime;

    @JsonProperty("authRes")
    private List<RelDTO> authRes;

    @JsonProperty("beginDate")
    private String beginDate;

    @JsonProperty("endDate")
    private String endDate;

}
