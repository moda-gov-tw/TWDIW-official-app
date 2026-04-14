package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum QrcodeLinkType {
    DEEPLINK("1"),
    UNIVERSAL_LINK("2");

    @Getter
    private String code;

    public static QrcodeLinkType toQrcodeLinkType(String code) {
        for (QrcodeLinkType tmp : QrcodeLinkType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
