package gov.moda.dw.issuer.vc.domain;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * entity of setting
 *
 * @version 20240902
 */
@Entity
@Table(name = "setting")
public class Setting {

    @Id
    @Column(name = "prop_name")
    private String propName;

    @Column(name = "prop_value")
    private String propValue;

    /**
     * default constructor of entity is NECESSARY for JPA
     */
    public Setting() {
    }

    public String getPropName() {
        return propName;
    }

    public Setting setPropName(String propName) {
        this.propName = propName;
        return this;
    }

    public String getPropValue() {
        return propValue;
    }

    public Setting setPropValue(String propValue) {
        this.propValue = propValue;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
