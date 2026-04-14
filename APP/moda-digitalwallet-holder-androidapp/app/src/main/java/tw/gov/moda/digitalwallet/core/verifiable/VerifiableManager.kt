package tw.gov.moda.digitalwallet.core.verifiable

import android.graphics.Bitmap
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.model.AddCredential
import tw.gov.moda.digitalwallet.data.model.BaseModel
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa201i
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa401i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk301i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk402i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk501i
import tw.gov.moda.digitalwallet.data.model.dwsdk.moda.DwsdkModa101i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMock101i

/**
 * 授權管理器
 */
interface VerifiableManager {
    suspend fun createDecentralizedIdentifier(wallet: Wallet): Wallet

    suspend fun createVerifiableCredential(wallet: Wallet, qrcode: String): VerifiableCredential?

    suspend fun verifyVerifiableCredential(wallet: Wallet, vc: VerifiableCredential): BaseModel<Dwsdk301i.Response>?

    suspend fun getVerifiableCredential(credential: String): Dwsdk501i.Response?

    suspend fun parseVerifiablePresentation(qrcode: String): BaseModel<Dwsdk401i.Response>?

    suspend fun getIssuerServiceData(vcUid: String, mode: String): DwModa201i.VCResponse?

    suspend fun getVerifiableServiceData(vpUid: String, mode: String): DwModa201i.VPResponse?

    suspend fun getVerifiableServiceDeepLink(verifierServiceUrl: String, vpUid: String): DwVerifierMock101i.Response?

    suspend fun verifyVerifiablePresentation(wallet: Wallet, vpToken: String, vpData: List<Dwsdk402i.Request.VPData>, customData: String?)

    suspend fun detectVerifiableCredentialStatus(wallet: Wallet, vc: VerifiableCredential, data: BaseModel<Dwsdk301i.Response>): VerifiableCredential

    suspend fun getIssuerList(): String?

    suspend fun getAllVerifiableCredentialList(vcs: List<VerifiableCredential>, vcList: String): String?

    suspend fun verifyVerifiableCredentialOffline(wallet: Wallet, vc: VerifiableCredential): BaseModel<Dwsdk301i.Response>?

    suspend fun parseIsserListToName(): HashMap<String, String>?

    suspend fun getIssuerName(issuerMap: HashMap<String, String>?, decodeVCData: Dwsdk501i.Response): String

    suspend fun getShowCredentialList(page: Int, size: Int, name: String?): DwsdkModa101i.Response<DwModa401i.Response>?

    suspend fun sortShowCredentialList(list: List<DwModa401i.Response.VPItem>): List<DwModa401i.Response.VPItem>

    suspend fun getAddCredentialList(page: Int, size: Int, name: String?): AddCredential

    suspend fun sortAddCredentialList(list: List<AddCredential.VCItem>): List<AddCredential.VCItem>

    suspend fun loadImageBitmapForList(url: String): Bitmap?
}
