package tw.gov.moda.digitalwallet.core.repository.wallet

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.data.model.AddCredential
import tw.gov.moda.digitalwallet.data.model.BaseModel
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredential
import tw.gov.moda.digitalwallet.data.model.VerifiablePresentationResultData
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa201i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i

/**
 * 皮夾的儲存庫
 */
interface WalletRepository {
    fun setContractTitle(title: String)

    fun getContractTitle(): String

    fun setContractContent(content: String)

    fun getContractContent(): String

    fun getContractAgreeLiveData(): MutableLiveData<Boolean>

    fun setWallet(wallet: Wallet?)

    fun getWallet(): Wallet?

    fun setCreatingWallet(wallet: Wallet?)

    fun getCreatingWallet(): Wallet?

    fun setAppLink(uri: Uri?)

    fun getAppLink(): Uri?

    fun setLoginStatus(isLogin: Boolean)

    fun isLogin(): Boolean

    fun setApplyVerifiableCredential(vc: VerifiableCredential?)

    fun getApplyVerifiableCredential(): VerifiableCredential?

    fun setDecodeVerifiableCredential(verifiableCredential: VerifiableCredential?)

    fun getDecodeVerifiableCredential(): VerifiableCredential?

    fun setParseVerifiablePresentation(data: BaseModel<Dwsdk401i.Response>?)

    fun getParseVerifiablePresentation(): BaseModel<Dwsdk401i.Response>?

    fun setVerifiablePresentationCustom(custom: DwModa201i.VPCustom?)

    fun getVerifiablePresentationCustom(): DwModa201i.VPCustom?

    fun setRequireVerifiableCredential(requireVerifiableCredential: RequireVerifiableCredential?)

    fun getRequireVerifiableCredential(): RequireVerifiableCredential?

    fun addRequireVerifiableCredential(requireVerifiableCredential: RequireVerifiableCredential)

    fun clearRequireVerifiableCredential(requireVerifiableCredential: RequireVerifiableCredential)

    fun clearAllOfRequireVerifiableCredentials()

    fun getRequireVerifiableCredentials(): List<RequireVerifiableCredential>

    fun getArgumentsOfVerifiablePresentation(): HashMap<Long, Array<String>>

    fun setVerifiablePresentationResult(result: Boolean)

    fun getVerifiablePresentationResult(): Boolean

    fun setVerifiablePresentationResultData(resultData: VerifiablePresentationResultData)

    fun getVerifiablePresentationResultData(): VerifiablePresentationResultData?

    fun isNewWallet(isNew: Boolean)

    fun isNewWallet(): Boolean

    fun setVerificationCode(otp: String)

    fun getVerificationCode(): String

    fun isContinueUsing(isContinue: Boolean)

    fun isContinueUsing(): Boolean

    fun setVerificationSource(sourceEnum: VerificationSourceEnum)

    fun getVerificationSource(): VerificationSourceEnum

    fun setPageEnum(pageEnum: PageEnum)

    fun getPageEnum(): PageEnum

    fun setAddCredentialList(list: List<AddCredential.VCItem>)

    fun getAddCredentialList(): List<AddCredential.VCItem>?

    fun setIsAddVerifiableCredentialResultFullPage(isFullPage: Boolean)

    fun getIsAddVerifiableCredentialResultFullPage(): Boolean

    fun getImageCache(key: String): String?

    fun putImageCache(key: String, base64: String)
}