package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IvpasVerifyResDTO {
    private Header header;
    private Body body;

    @Getter
    @Setter
    @ToString
    public static class Header {
        private String returnCode;
        private String returnDesc;
    }

    @Getter
    @Setter
    @ToString
    public static class Body {
        private String orgEnName;
    }
}
