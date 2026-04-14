package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "setting", schema = "vc")
public class Setting implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Size(max = 100)
    @Column(name = "prop_name", length = 100, nullable = false)
    private String propName;

    @NotNull
    @Size(max = 300)
    @Column(name = "prop_value", length = 300, nullable = false)
    private String propValue;

    public Setting() {
    }

    public Setting(String propName, String propValue) {
        this.propName = propName;
        this.propValue = propValue;
    }

    public @NotNull @Size(max = 100) String getPropName() { return propName; }

    public void setPropName(@NotNull @Size(max = 100) String propName) { this.propName = propName; }

    public @NotNull @Size(max = 300) String getPropValue() { return propValue; }

    public void setPropValue(@NotNull @Size(max = 300) String propValue) { this.propValue = propValue; }
}
