package gov.moda.dw.manager.service;

import gov.moda.dw.manager.service.dto.VCItemDTO;
import java.util.List;

public class VCItemListRespDTO {

    public Long getVcSourceType() {
        return vcSourceType;
    }

    public void setVcSourceType(Long vcSourceType) {
        this.vcSourceType = vcSourceType;
    }

    public boolean isIssuerDID() {
        return issuerDID;
    }

    public void setIssuerDID(boolean issuerDID) {
        this.issuerDID = issuerDID;
    }

    public List<VCItemDTO> getData() {
        return data;
    }

    public void setData(List<VCItemDTO> data) {
        this.data = data;
    }

    private Long vcSourceType;

    private boolean issuerDID;

    private List<VCItemDTO> data;
}
