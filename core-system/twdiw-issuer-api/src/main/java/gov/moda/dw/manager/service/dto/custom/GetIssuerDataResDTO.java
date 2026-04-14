package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class GetIssuerDataResDTO {
    private boolean isRegistered;
    private FindIssuerResDTO issuerData;
    private String orgTwName;
    private String token;
    private String baseUrl;
    private Regex regex_json;
    private Regex regex_xss;
    private Regex regex_sql;
    private Regex regex_enAndNum;
    private Regex regex_url;

    @Getter
    @Setter
    @ToString
    public static class Regex {
        private String regularExpression;
        private String errorMessage;
    }
}
