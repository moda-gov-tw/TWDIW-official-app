package tw.gov.moda.digitalwallet.data.model

import androidx.annotation.StringRes
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential

data class RemindAlert(
    @StringRes
    var title: Int,
    @StringRes
    var message: Int,
    val credentialList: ArrayList<VerifiableCredential> = ArrayList(),
    val subRemind: RemindAlert? = null,
    var isShow: Boolean = false
)
