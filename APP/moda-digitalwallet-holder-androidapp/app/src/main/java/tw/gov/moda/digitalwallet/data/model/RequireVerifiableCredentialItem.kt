package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.element.VerifiableCredentialGroupEnum

/**
 * @param title
 * @param group
 * @param rule
 * @param count
 * @param enoughCount 能夠提供的VC最多上限
 * @param cardList
 * @param type
 * @param isShowAuthorizedVCTitle 顯示 目前缺少可授權的憑證
 * @param isShowAuthorizedVC 顯示 申請憑證
 * @param isShowChangeVC 顯示 選擇憑證
 * @param isHidden Value隱藏
 */
data class RequireVerifiableCredentialGroup(
    @SerializedName("title")
    var title: String,
    @SerializedName("group")
    var group: String?,
    @SerializedName("rule")
    var rule: String?,
    @SerializedName("count")
    var count: Int,
    @SerializedName("enoughCount")
    var enoughCount: Int,
    @SerializedName("cardList")
    var cardList: List<RequireVerifiableCredentialCard>,
    @SerializedName("type")
    var type: VerifiableCredentialGroupEnum,
    @SerializedName("isShowAuthorizedVCTitle")
    var isShowAuthorizedVCTitle: Boolean,
    @SerializedName("isShowAuthorizedVC")
    var isShowAuthorizedVC: Boolean,
    @SerializedName("isShowChangeVC")
    var isShowChangeVC: Boolean,
    @SerializedName("isVisible")
    var isHidden: Boolean = false
)

data class RequireVerifiableCredentialCard(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("card")
    var card: String,
    @SerializedName("cardId")
    var cardId: String,
    @SerializedName("issuer")
    var issuer: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("verifiableCredential")
    val verifiableCredential: VerifiableCredential? = null,
    @SerializedName("fields")
    val fields: List<RequireVerifiableCredentialField> = emptyList(),
    @SerializedName("isExpand")
    var isExpand: Boolean = false,
    @SerializedName("isCheckAll")
    var isCheckAll: Boolean = true,
    @SerializedName("isValid")
    var isValid: Boolean = false,
    @SerializedName("ial")
    var ial: String = "0",
)

data class RequireVerifiableCredentialField(
    @SerializedName("title")
    var title: String,
    @SerializedName("field")
    var field: String,
    @SerializedName("value")
    var value: String,
    @SerializedName("isCheck")
    var isCheck: Boolean = true
)