package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VCDataSourceType {
    // 機關主動傳入
    _501("501"),
    // vc建立(生效)時向機關取得
    _901("901");

    @Getter
    private final String code;

    public static VCDataSourceType toStatusCode(String code) {
        for (VCDataSourceType tmp : VCDataSourceType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
