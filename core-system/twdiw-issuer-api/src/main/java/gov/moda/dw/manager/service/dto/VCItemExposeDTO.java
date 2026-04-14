package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class VCItemExposeDTO implements Serializable {

    @NotNull
    @Schema(description = "vcid")
    private Long vcId;

    @NotNull
    @Schema(description = "是否開放")
    private Boolean expose;

    public @NotNull Long getVcId() {
        return vcId;
    }

    public void setVcId(@NotNull Long vcId) {
        this.vcId = vcId;
    }

    public @NotNull Boolean getExpose() {
        return expose;
    }

    public void setExpose(@NotNull Boolean expose) {
        this.expose = expose;
    }

    @Override
    public String toString() {
        return "VCItemExposeDTO{" + "vcid=" + vcId + ", expose=" + expose + '}';
    }
}
