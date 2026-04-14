package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VPItemSearchAllResDTO {
    
    private Long id;

    private String serialNo;

    private String name;

    private Long crUser;
    
    private Instant upDatetime;

    private String businessId;

    private String presentationDefinition;

    private boolean owner;

    private String terms;

    private String purpose;
    
    private Map<String, String> model;

    private String verifierServiceUrl;

    private String callBackUrl;
    
    private String tag;
    
    private Boolean isEncryptEnabled;
    
    private List<CustomFieldReqDTO> fields;

    @JsonProperty("isShowEditIcon")
    private boolean isShowEditIcon;

    @JsonProperty("isShowTermsIcon")
    private boolean isShowTermsIcon;

}
