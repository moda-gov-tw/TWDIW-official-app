
package gov.moda.dw.manager.service.dto.custom.ext.api.payload;


public class Filter {

    private Contains contains;
    private String type;

    public Contains getContains() {
        return contains;
    }

    public void setContains(Contains contains) {
        this.contains = contains;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
