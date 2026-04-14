package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VPItemListResDTO {

    private boolean verifierDID;

    private List<VPItemSearchAllResDTO> data;

}
