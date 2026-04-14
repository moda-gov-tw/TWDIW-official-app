package tw.gov.moda.digitalwallet.data.element

enum class CardStatusEnum (val code: Int){
    Invalid(0),
    Valid(1),
    Expired(2),
    Unknown(3)
}