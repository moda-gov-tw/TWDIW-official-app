package gov.moda.dw.manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "oidvp_config", schema = "vp")
public class OidvpConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Size(max = 100)
    @Column(name = "property_key", length = 100, nullable = false)
    private String propertyKey;

    @NotNull
    @Column(name = "property_value", columnDefinition = "TEXT", nullable = false)
    private String propertyValue;

    public OidvpConfig() {
    }

    public OidvpConfig(String propertyKey, String propertyValue) {
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
    }

    public String getPropertyKey() { return propertyKey; }

    public void setPropertyKey(String propertyKey) { this.propertyKey = propertyKey; }

    public String getPropertyValue() { return propertyValue; }

    public void setPropertyValue(String propertyValue) { this.propertyValue = propertyValue; }
}
