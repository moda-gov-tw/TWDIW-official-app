package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams311wReqDTO {

    // jhi_user。
    @JsonProperty("id")
    private Long id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("activated")
    private Boolean activated;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("createdDate")
    private Instant createdDate;

    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @JsonProperty("lastModifiedDate")
    private Instant lastModifiedDate;

    @JsonProperty("authorities")
    private Set<String> authorities;

    // extended_user。
    @JsonProperty("extendedId")
    private Long extendedId;

    @JsonProperty("orgId")
    private String orgId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("tel")
    private String tel;

    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("employeeTypeId")
    private String employeeTypeId;

    @JsonProperty("leftDate")
    private String leftDate;

    @JsonProperty("onboardDate")
    private String onboardDate;

    @JsonProperty("userTypeId")
    private String userTypeId;

    @JsonProperty("dataRole1")
    private String dataCategory1;

    @JsonProperty("state")
    private String state;

    @JsonProperty("beginDate")
    private String beginDate;

    @JsonProperty("endDate")
    private String endDate;

}
