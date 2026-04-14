package gov.moda.dw.manager.domain.outside.vdr.vcitem;

import java.io.Serializable;
import java.util.Objects;

public class VCItemVdr implements Serializable {

    private static final long serialVersionUID = 1L;

    //private Long id;

    /**
     * VC編號
     */
    private String serialNo;

    /**
     * VC名稱
     */
    private String name;

    /**
     * 建立者
     */
    //private Long crUser;

    /**
     * 建立時間
     */
    //private Instant crDatetime;

    /**
     * 類別Id
     */
    private Long categoryId;

    /**
     * 單位
     */
    private String businessId;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VCItemVdr vcItemVdr = (VCItemVdr) o;
        return Objects.equals(serialNo, vcItemVdr.serialNo) && Objects.equals(name, vcItemVdr.name) && Objects.equals(categoryId, vcItemVdr.categoryId) && Objects.equals(businessId, vcItemVdr.businessId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNo, name, categoryId, businessId);
    }
}
