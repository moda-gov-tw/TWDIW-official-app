package tw.gov.moda.digitalwallet.core.identifier

import com.google.gson.reflect.TypeToken
import tw.gov.moda.digitalwallet.data.annotation.APIMethod
import tw.gov.moda.digitalwallet.data.model.BaseModel
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk101i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk102i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk201i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk301i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk402i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk501i
import tw.gov.moda.digitalwallet.data.model.dwsdk.moda.DwsdkModa101i
import tw.gov.moda.digitalwallet.data.model.dwsdk.moda.DwsdkModa201i

/**
 * Flutter Channel Method 介面
 */
interface IdentifierManager {
    suspend fun dwsdk101i(): Dwsdk101i.Response?

    suspend fun dwsdk102i(publicKey: Dwsdk101i.Response.PublicKey): Dwsdk102i.Response?

    suspend fun dwsdk103i(keyTag: String): Any?

    suspend fun dwsdk201i(otp: String, qrCode: String, didJson: String): Dwsdk201i.Resposne?

    suspend fun dwsdk301i(credential: String, didJson: String): BaseModel<Dwsdk301i.Response>?

    suspend fun dwsdk302i(credential: String, didJson: String, issuerListJson: String, verifiableCredentialListJson: String): BaseModel<Dwsdk301i.Response>?

    suspend fun dwsdk401i(qrCode: String): BaseModel<Dwsdk401i.Response>?

    suspend fun dwsdk402i(vpToken: String, vpData: List<Dwsdk402i.Request.VPData>, didJson: String, customData: String?): Any?

    suspend fun dwsdk501i(credential: String): Dwsdk501i.Response?

    suspend fun dwsdk601i(): String?

    suspend fun dwsdk602i(vcs: List<String>, vcList: String): String?

    suspend fun <RQ, RS> dwsdkModa101i(url: String, @APIMethod type: String, body: RQ, typeToken: TypeToken<RS>): DwsdkModa101i.Response<RS>?

    suspend fun <RQ, RS> dwsdkModa201i(url: String, payload: RQ, didFile: String, typeToken: TypeToken<RS>): DwsdkModa201i.Response<RS>?
}