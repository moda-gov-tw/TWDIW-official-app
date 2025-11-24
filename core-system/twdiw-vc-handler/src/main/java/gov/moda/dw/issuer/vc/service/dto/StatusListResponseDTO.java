package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * status list response
 *
 * @version 20240902
 */
public class StatusListResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String statusList;

    public StatusListResponseDTO() {
    }

    public StatusListResponseDTO(String statusList) {
        this.statusList = statusList;
    }

    public String getStatusList() {
        return statusList;
    }

    public StatusListResponseDTO setStatusList(String statusList) {
        this.statusList = statusList;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
