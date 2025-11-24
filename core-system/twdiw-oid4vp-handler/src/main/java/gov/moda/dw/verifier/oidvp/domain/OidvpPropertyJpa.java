package gov.moda.dw.verifier.oidvp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "oidvp_config")
public class OidvpPropertyJpa {

    @Id
    @Column(name = "property_key")
    String key;

    @Column(name = "property_value")
    String value;

    public String getKey() {
        return key;
    }

    public OidvpPropertyJpa setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public OidvpPropertyJpa setValue(String value) {
        this.value = value;
        return this;
    }
}
