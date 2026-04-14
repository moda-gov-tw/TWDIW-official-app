package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

public class RegularExpressionFieldInvalidDTO {

    private String ename;
    private String cname;
    private String value;
    private List<String> invalid;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getInvalid() {
        return invalid;
    }

    public void setInvalid(List<String> invalid) {
        this.invalid = invalid;
    }

    @Override
    public String toString() {
        String invalidString = invalid != null ? String.join(",", invalid) : "";
        return (
            "RegularExpressionFieldInvalidDTO{" +
            "ename='" +
            ename +
            '\'' +
            ", cname='" +
            cname +
            '\'' +
            ", value='" +
            value +
            '\'' +
            ", invalid='" +
            invalidString +
            '\'' +
            '}'
        );
    }
}
