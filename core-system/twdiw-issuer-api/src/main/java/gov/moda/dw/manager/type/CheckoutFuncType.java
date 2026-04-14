package gov.moda.dw.manager.type;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CheckoutFuncType {
    singleCheckout("singleCheckout", "單次結帳"), //二聯式
    monthCheckout("monthCheckout", "月結"),
    twoMonthCheckout("twoMonthCheckout", "雙月結"),
    seasonCheckout("seasonCheckout", "季結");

    @Getter
    private String code;

    @Getter
    private String name;

    public static CheckoutFuncType toCheckoutFuncType(String code) {
        for (CheckoutFuncType tmp : CheckoutFuncType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }

    public static List<CheckoutFuncType> genCheckoutFuncList() {
        List<CheckoutFuncType> checkoutFuncList = new ArrayList<>();
        for (CheckoutFuncType tmp : CheckoutFuncType.values()) {
            checkoutFuncList.add(tmp);
        }
        return checkoutFuncList;
    }
}
