package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvc101iVcItemsDTO {

    /**
     * 統編_模板代碼
     */
    @JsonProperty("vcUid")
    private String vcUid;

    /**
     * VC 模板名稱
     */
    @JsonProperty("name")
    private String name;

    /**
     * 開啟網頁方式
     */
    @JsonProperty("type")
    private String type;

    /**
     * 發證端KYC與輸入資料的URL
     */
    @JsonProperty("issuerServiceUrl")
    private String issuerServiceUrl;

}
