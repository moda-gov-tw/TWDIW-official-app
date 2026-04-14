package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DwIssuerOidVci101iRes {

    @JsonProperty("resp_message")
    private String respMessage;

    @JsonProperty("resp_code")
    public String respCode;

    @JsonProperty("link")
    public String link;

    @JsonProperty("qr_code")
    public String qrCode;

    @JsonProperty("warnings")
    public DwIssuerOidVci101iWarningsRes warnings;

    @Getter
    @Setter
    public static class DwIssuerOidVci101iWarningsRes {

        @JsonProperty("status_revoke")
        public List<String> statusRevoke;

        @JsonProperty("cid_not_found")
        public List<String> cidNotFound;

    }

}
