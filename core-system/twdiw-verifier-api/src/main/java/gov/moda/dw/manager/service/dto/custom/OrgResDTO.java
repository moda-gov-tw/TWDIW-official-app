package gov.moda.dw.manager.service.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgResDTO {

    private Long id; // 組織 table id
    private String orgId; // 組織代碼
    private String orgTwName; // 中文名稱
    private String orgEnName; // 英文名稱
    private Instant createTime; // 建立日期
    private Instant updateTime; // 更新日期
    private Boolean isDidOrg; // 是否為註冊DID之組織
}
