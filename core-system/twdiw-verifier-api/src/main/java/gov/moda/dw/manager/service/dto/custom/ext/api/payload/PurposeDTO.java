
package gov.moda.dw.manager.service.dto.custom.ext.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurposeDTO {

    @JsonProperty("client")
    private String client; // 授權單位名稱

    @JsonProperty("terms_uri")
    private String termsUri; // 授權條款 URL

    @JsonProperty("scenario")
    private String scenario; // 驗證情境主題

    @JsonProperty("purpose")
    private String purpose; // 授權目的

}
