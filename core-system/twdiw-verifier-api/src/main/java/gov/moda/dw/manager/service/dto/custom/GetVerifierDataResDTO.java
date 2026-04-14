package gov.moda.dw.manager.service.dto.custom;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetVerifierDataResDTO {
    private boolean isRegistered;
    private FindVerifierResDTO verifierData;
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
