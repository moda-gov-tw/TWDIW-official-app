package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 申請帳號類型
 */
@AllArgsConstructor
public enum ApplyType {
    GENERAL_ACCOUNT("1", "一般帳號"),
    ADVANCED_ACCOUNT("2", "進階帳號");

	@Getter
    private String code;

	@Getter
    private String name;

}
