package gov.moda.dw.manager.service.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class VerifyCertAndDidResDTO {
    private String detailCode;
    private String detailMessage;
    private String OrgEnName;
}
