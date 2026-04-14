package gov.moda.dw.manager.service.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTypeIdDTO {

    private String code; // 代碼
    private String name; // 中文名稱

}
