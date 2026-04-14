package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.element.SelectVerifiableCredentialStatusEnum

data class VerifiableCredentialCardCheck(
    @SerializedName("title")
    val title: String,
    @SerializedName("card")
    val card: String,
    @SerializedName("issuer")
    val issuer: String,
    @SerializedName("isChecked")
    var isChecked: Boolean,
    @SerializedName("fields")
    var fields: List<VerifiableCredentialField>,
    @SerializedName("isClickable")
    var clickStatus: SelectVerifiableCredentialStatusEnum,
    @SerializedName("uid")
    var uid: Long
)

data class ChangeVerifiableCredentialCardGroup(
    val id: Int,
    val card: String,
    val title: String,
    val issuer: String,
    var isExpand: Boolean,
    var isHidden: Boolean,
    var clickStatus: SelectVerifiableCredentialStatusEnum,
    var cardList: List<MultiChangeVerifiableCredentialCard>
)

data class MultiChangeVerifiableCredentialCard(
    val uid: Long,
    @SerializedName("isChecked")
    var isChecked: Boolean,
    @SerializedName("fieldList")
    val fieldList: List<VerifiableCredentialField>
)

data class VerifiableCredentialField(
    @SerializedName("title")
    var title: String,
    @SerializedName("field")
    var field: String,
    @SerializedName("value")
    var value: String
)



