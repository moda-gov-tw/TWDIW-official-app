package tw.gov.moda.digitalwallet.data.element

enum class RemindPeriodEnum(val period: Int) {
    NORMAL(0), ONE_DAY(1), SEVEN_DAY(7), EXPIRED(-1)
}