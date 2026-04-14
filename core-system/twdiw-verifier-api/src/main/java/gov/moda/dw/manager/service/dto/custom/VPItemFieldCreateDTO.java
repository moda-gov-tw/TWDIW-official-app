package gov.moda.dw.manager.service.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPItemField} entity.
 */
@Schema(description = "VPItemField VP欄位")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemFieldCreateDTO implements Serializable {

    private Long vcFieldId;

    public Long getVcFieldId() {
        return vcFieldId;
    }

    public void setVcFieldId(Long vcFieldId) {
        this.vcFieldId = vcFieldId;
    }
}
