package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "VCItemDataVcCidFailDTO 輸入資訊")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemDataVcCidFailDTO {

    String vcCid;

    String result;

    public VCItemDataVcCidFailDTO(String vcCid, String result) {
        this.vcCid = vcCid;
        this.result = result;
    }

    public String getVcCid() {
        return vcCid;
    }

    public void setVcCid(String vcCid) {
        this.vcCid = vcCid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
