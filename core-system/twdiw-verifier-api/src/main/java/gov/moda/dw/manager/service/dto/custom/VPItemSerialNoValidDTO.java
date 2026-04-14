package gov.moda.dw.manager.service.dto.custom;

import com.sun.istack.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "VPItem SerialNo")
public class VPItemSerialNoValidDTO {

    @NotNull
    String serialNo;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
