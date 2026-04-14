package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "建立 vc qrcode 的 request")
public class VCDataQrcodeReqDTO {

    @NotNull
    private Boolean authenticated;

    @NotNull
    private String id_token;

    public @NotNull Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(@NotNull Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
}
