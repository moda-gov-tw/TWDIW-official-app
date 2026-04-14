package gov.moda.dw.manager.domain.outside.vdr.vcitem;

import java.io.Serializable;
import java.util.Objects;


public class VCItemFieldVdr implements Serializable {

    private static final long serialVersionUID = 1L;

    //private Long id;

    /**
     * VC id 外鍵
     */
    //private Long vcItemId;

    /**
     * 欄位類別(BASIC NORMAL NORMAL)
     */
    //private String type;

    /**
     * 欄位對外名稱(中/英)
     */
    private String cname;

    /**
     * 欄位名稱(英)
     */
    private String ename;

    /**
     * 其外額外屬性
     */
    private Boolean mandatory;
    private String valueType;
    private Boolean isRequired;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VCItemFieldVdr that = (VCItemFieldVdr) o;
        return Objects.equals(cname, that.cname) && Objects.equals(ename, that.ename) && Objects.equals(mandatory, that.mandatory) && Objects.equals(valueType, that.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cname, ename, mandatory, valueType);
    }
}
