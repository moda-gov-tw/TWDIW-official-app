package tw.gov.moda.digitalwallet.data.element

enum class AutoLogoutEnum(val duration: Int) {
    THREE(3), FIVE(5), TEN(10), FIFTEEN(15), NEVER(-1)
}