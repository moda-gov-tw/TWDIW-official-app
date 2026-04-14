package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgKeySettingResDTO {

    private Long id;

    private String orgId;

    private String keyId;

    private String description;

    private Boolean isActive;

    private Instant crDatetime;

    private Instant upDatetime;
}
