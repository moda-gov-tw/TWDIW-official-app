package tw.gov.moda.digitalwallet.data.element

enum class OperationEnum(val code: Int) {
    UNKNOWN(-1),
    ADD_CARD(1), //加入憑證
    INVAILD_CARD(2), //失效憑證
    AUTHORIZATION_CARD(3), //驗證憑證
    DELETE_CARD(4), //移除憑證
    EDIT_WALLET_PINCODE(5), //修改皮夾密碼
}