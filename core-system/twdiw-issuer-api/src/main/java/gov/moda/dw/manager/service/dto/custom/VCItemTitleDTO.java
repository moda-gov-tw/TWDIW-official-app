package gov.moda.dw.manager.service.dto.custom;

public class VCItemTitleDTO {

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getVcItemSerialNo() {
        return vcItemSerialNo;
    }

    public void setVcItemSerialNo(String vcItemSerialNo) {
        this.vcItemSerialNo = vcItemSerialNo;
    }

    public String getVcItemName() {
        return vcItemName;
    }

    public void setVcItemName(String vcItemName) {
        this.vcItemName = vcItemName;
    }

    public Long getVcItemId() {
        return vcItemId;
    }

    public void setVcItemId(Long vcItemId) {
        this.vcItemId = vcItemId;
    }

    private String orgId;
    private String orgName;
    private Long vcItemId;
    private String vcItemSerialNo;
    private String vcItemName;
}
