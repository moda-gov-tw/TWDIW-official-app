package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTypeIdDTO {

    private String code; // 代碼
    private String name; // 中文名稱
}
