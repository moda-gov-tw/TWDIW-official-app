package tw.gov.moda.digitalwallet.data.model

import tw.gov.moda.diw.R

data class RemindCredentialList(
    var failure: RemindAlert = RemindAlert(
        R.string.credential_update_failed,
        R.string.msg_credential_update_failed
    ),
    var expired: RemindAlert = RemindAlert(
        R.string.credential_has_expired,
        R.string.msg_credential_has_expired
    ),
    var willExpire: RemindAlert = RemindAlert(
        R.string.credential_will_expire,
        R.string.msg_credential_will_expire_for_seven,
    ),
    var disconnect: RemindAlert = RemindAlert(
    R.string.credential_update_failed,
    R.string.credential_cannot_update_please_check_connection
),
)