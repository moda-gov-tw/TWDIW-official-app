package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
public class SettingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private StringFilter propName;
    private StringFilter propValue;

    public SettingCriteria() {}

    public SettingCriteria(SettingCriteria other) {
        this.propName = other.optionalPropName().map(StringFilter::copy).orElse(null);
        this.propValue = other.optionalPropValue().map(StringFilter::copy).orElse(null);
    }

    @Override
    public SettingCriteria copy() {
        return new SettingCriteria(this);
    }

    public StringFilter getPropName() {
        return propName;
    }

    public Optional<StringFilter> optionalPropName() {
        return Optional.ofNullable(propName);
    }

    public StringFilter propName() {
        if (propName == null) {
            setPropName(new StringFilter());
        }
        return propName;
    }

    public void setPropName(StringFilter propName) {
        this.propName = propName;
    }

    public StringFilter getPropValue() {
        return propValue;
    }

    public Optional<StringFilter> optionalPropValue() {
        return Optional.ofNullable(propValue);
    }

    public StringFilter propValue() {
        if (propValue == null) {
            setPropName(new StringFilter());
        }
        return propValue;
    }

    public void setPropValue(StringFilter propValue) {
        this.propValue = propValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingCriteria that = (SettingCriteria) o;
        return Objects.equals(propName, that.propName) && Objects.equals(propValue, that.propValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propName, propValue);
    }

    @Override
    public String toString() {
        return "SettingCriteria{" +
            "propName=" + propName +
            ", propValue=" + propValue +
            '}';
    }
}
