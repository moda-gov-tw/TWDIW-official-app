package gov.moda.dw.manager.type;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TBBClientResponseStatusCode {
    OK("0000", "OK"),
    API_NOT_FOUND("A001", "無此API"),
    REQUEST_FORMAT_ERROR("A002", "請求訊息格式錯誤"),
    BUSINESS_CODE_NOT_FOUND("B001", "業務別碼不存在"),
    BUSINESS_CODE_EXPIRED("B002", "業務別碼已過期"),
    INPUT_AMOUNT_FORMAT_ERROR("B003", "輸入金額格式異常"),
    INPUT_VALIDITY_FORMAT_ERROR("B004", "輸入有效期限格式不正確"),
    PAIDDATE_EXPIRED("B005", "繳款起訖日區間超過一年"),
    CERTIFICATE_VERIFY_ERROR("C001", "驗章失敗"),
    DATABASE_ERROR("9998", "資料庫異常"),
    SYSTEM_ERROR("9999", "系統異常");

    private final String code;

    private final String msg;

    public static Optional<TBBClientResponseStatusCode> of(String code) {
        for (TBBClientResponseStatusCode status : TBBClientResponseStatusCode.values()) {
            if (status.getCode().equals(code)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
