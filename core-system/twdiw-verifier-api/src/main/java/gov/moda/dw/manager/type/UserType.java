package gov.moda.dw.manager.type;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserType {
    personal("personal", "個人"),
    company("company", "公司/機構"),
    school("school", "學校");

    @Getter
    private String code;

    @Getter
    private String name;

    public static UserType toUserType(String code) {
        for (UserType tmp : UserType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }

    public static List<UserType> genUserTypeList() {
        List<UserType> userTypeList = new ArrayList<>();
        for (UserType tmp : UserType.values()) {
            userTypeList.add(tmp);
        }
        return userTypeList;
    }
}
