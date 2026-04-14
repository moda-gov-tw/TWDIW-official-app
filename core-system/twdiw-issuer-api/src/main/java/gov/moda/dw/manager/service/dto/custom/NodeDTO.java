package gov.moda.dw.manager.service.dto.custom;

import java.util.List;
import lombok.Data;

@Data
public class NodeDTO {

    private String label;
    private String value;
    private List<NodeDTO> children;
}
