package gov.moda.dw.verifier.vc.service.dto.vc;

import gov.moda.dw.verifier.vc.util.JsonUtils;
import java.io.Serial;
import java.io.Serializable;

public class StatusListResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String statusList;

    public StatusListResponseDTO() {
    }

    public StatusListResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            StatusListResponseDTO statusListResponseDTO = JsonUtils.jsToVo(respJson, getClass());
            if (statusListResponseDTO != null) {
                statusList = statusListResponseDTO.getStatusList();
            }
        }
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
