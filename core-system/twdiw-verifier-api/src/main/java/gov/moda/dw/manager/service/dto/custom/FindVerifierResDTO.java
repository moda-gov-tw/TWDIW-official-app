package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindVerifierResDTO {

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
        private String createdAt;
        private String updatedAt;
    }
}
