package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindIssuerResDTO {

    private DataDTO data;
    private int code;
    private String msg;

    @Getter
    @Setter
    @ToString
    public static class DataDTO {

        private String did;
        private DIDregisterOrgDTO org;
        private String status;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
