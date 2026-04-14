
package gov.moda.dw.manager.domain.outside.vdr.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataDid {

    @JsonProperty
    private Long createdAt;
    @JsonProperty
    private String did;
    @JsonProperty
    private String orgType;
    @JsonProperty
    private Org org;
    @JsonProperty
    /**
     * 0: 待審核
     * 1: 有效
     * 2: 已註銷
     */
    private Long status;
    @JsonProperty
    private Long updatedAt;

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    /**
     * 0: 待審核
     * 1: 有效
     * 2: 已註銷
     */
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

}
