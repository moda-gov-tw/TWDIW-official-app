package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.Setting;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Setting} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SettingDTO implements Serializable {

    @NotNull
    @Size(max = 100)
    @Schema(description = "欄位名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String propName;

    @NotNull
    @Size(max = 300)
    @Schema(description = "欄位資料", requiredMode = Schema.RequiredMode.REQUIRED)
    private String propValue;

    public @NotNull @Size(max = 100) String getPropName() {
        return propName;
    }

    public void setPropName(@NotNull @Size(max = 100) String propName) {
        this.propName = propName;
    }

    public @NotNull @Size(max = 300) String getPropValue() {
        return propValue;
    }

    public void setPropValue(@NotNull @Size(max = 300) String propValue) {
        this.propValue = propValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingDTO that = (SettingDTO) o;
        return Objects.equals(propName, that.propName) && Objects.equals(propValue, that.propValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propName, propValue);
    }

    @Override
    public String toString() {
        return "SettingDTO{" +
            "propName='" + propName + '\'' +
            ", propValue='" + propValue + '\'' +
            '}';
    }
}
