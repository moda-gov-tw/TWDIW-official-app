package gov.moda.dw.manager.service.dto;

public class VCItemDataFailDTO {

    VCItemDataDTO vcItemData;

    String result;

    public VCItemDataFailDTO(VCItemDataDTO vcItemData, String result) {
        this.vcItemData = vcItemData;
        this.result = result;
    }

    public VCItemDataDTO getVcItemData() {
        return vcItemData;
    }

    public void setVcItemData(VCItemDataDTO vcItemData) {
        this.vcItemData = vcItemData;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
