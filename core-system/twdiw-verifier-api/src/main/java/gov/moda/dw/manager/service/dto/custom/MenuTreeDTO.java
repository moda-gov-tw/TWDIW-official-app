package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTreeDTO implements Serializable {

    private List<String> tickedNodes;
    private List<NodeDTO> resNodeDTO;
}
