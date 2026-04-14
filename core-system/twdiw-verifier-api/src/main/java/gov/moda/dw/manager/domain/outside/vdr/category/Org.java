
package gov.moda.dw.manager.domain.outside.vdr.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Org {

    @JsonProperty
    private String info;

    @JsonProperty
    private String name;

    @JsonProperty("name_en")
    private String nameEn;

    @JsonProperty
    private String taxId;

    @JsonProperty("issuerMetadataBaseURL")
    private String issuerMetadataBaseURL;

    @JsonProperty("serviceBaseURL")
    private String serviceBaseURL;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getIssuerMetadataBaseURL() {
        return issuerMetadataBaseURL;
    }

    public void setIssuerMetadataBaseURL(String issuerMetadataBaseURL) {
        this.issuerMetadataBaseURL = issuerMetadataBaseURL;
    }

    public String getServiceBaseURL() {
        return serviceBaseURL;
    }

    public void setServiceBaseURL(String serviceBaseURL) {
        this.serviceBaseURL = serviceBaseURL;
    }

}
