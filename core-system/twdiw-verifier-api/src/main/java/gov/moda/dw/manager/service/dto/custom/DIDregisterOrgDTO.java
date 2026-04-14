package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DIDregisterOrgDTO {
    private String name;
    private String name_en;
    private String info;
    private String taxId;
    private String serviceBaseURL;
    private String x509_subject;
    private String x509_serial;
    private String x509_type;
}
