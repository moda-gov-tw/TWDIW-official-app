package tw.gov.moda.digitalwallet.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.data.element.RemindPeriodEnum
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk201i

@Entity(tableName = AppConstants.Database.TABLE_VERIFIABLE_CREDENTIAL)
data class VerifiableCredential(
    @PrimaryKey(autoGenerate = true) @SerializedName("uid") var uid: Long = 0L,
    @SerializedName("walletId") var walletId: Long,
    @SerializedName("display") var display: String = "",
    @SerializedName("issuingUnit") var issuingUnit: String = "",
    @SerializedName("credential") var credential: String = "",
    @SerializedName("types") var types: Array<String> = arrayOf(),
    @SerializedName("credentialSubject") var credentialSubject: LinkedHashMap<String, Dwsdk201i.Resposne.CredentialSubjectField> = linkedMapOf(),
    // 狀態: 0 = 失效, 1 = 正常, 2 = 過期, 3 = 未知
    @SerializedName("status") var status: CardStatusEnum = CardStatusEnum.Valid,
    @SerializedName("invalidReason") var invalidReason: String = "",
    @SerializedName("imageBase64") var imageBase64: String = "",
    @SerializedName("previewData") var previewData: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("trustBadge") var trustBadge: Boolean = false,
    @SerializedName("updateDatetime") var updateDatetime: Long = System.currentTimeMillis(),
    @SerializedName("remind") var remind: RemindPeriodEnum = RemindPeriodEnum.NORMAL

)