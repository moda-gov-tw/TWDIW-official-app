package gov.moda.dw.verifier.oidvp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "vp_manager", name = "vp_item")
public class VpItemJpa {

    @Id
    private String id;

    @Column(name = "serial_no")
    private String serialNumber;

    @Column(name = "business_id")
    private String businessId;

    @Column(name = "presentation_definition")
    private String presentationDefinition;

    
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPresentationDefinition() {
        return presentationDefinition;
    }

    public void setPresentationDefinition(String presentationDefinition) {
        this.presentationDefinition = presentationDefinition;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
