package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ams321wRoleResDTO {

    private Long id; // 角色table id
    private String roleId; // 角色代碼
    private String roleName; // 角色名稱
    private String description; // 角色描述
    private String state; // 角色啟停狀態
    private Instant createTime; // 建立時間
}
