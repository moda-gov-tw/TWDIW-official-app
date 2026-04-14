package gov.moda.dw.manager.service.dto;

import java.time.LocalDateTime;
import lombok.*;

/**
 * @author AlexChang
 * @create 2024/07/26
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualAccount {

    /**
     * 虛擬帳號
     */
    private String account;

    /**
     * 業務別碼
     */
    private String BusinessCode;

    /**
     * 入帳金額
     */
    private String Amount;

    /**
     * 可入帳起始日期（起）
     */
    private LocalDateTime payStartDate;

    /**
     * 入帳截止日期，原則上當超過此日期，則此帳號會自動註銷
     */
    private LocalDateTime payEndDate;

    /**
     * 備註，但此欄根據目前約定會以業務別碼+專案代碼進行識別 填寫專案代碼(目前訂為兩碼如 01)，而不是一般的備註欄
     * 若沒有識別成功，則會無法成功建立虛擬帳號
     */
    private String remark;
}
