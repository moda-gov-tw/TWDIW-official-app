package gov.moda.dw.manager.service.dto.outside;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VCItemDataDTO {
    private String ename;
    private String vcBusinessId;
    private String vcSerialNo;
    private String cname;
    private String data;

}
