package tw.gov.moda.digitalwallet.core.wallet

import androidx.room.Transaction
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.identifier.IdentifierManager
import tw.gov.moda.digitalwallet.core.pref.ModaSharedPreferences
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk102i
import tw.gov.moda.digitalwallet.extension.sha256
import javax.inject.Inject

/**
 * 皮夾管理器
 *
 * 負責皮夾的建立/修改/刪除
 *
 * @constructor [VerifiableManager], [ModaSharedPreferences], [DigitalWalletDB]
 */
class WalletManagerImpl @Inject constructor(
    private val mVerifiableManager: VerifiableManager,
    private val mPref: ModaSharedPreferences,
    private val mIdentifierManager: IdentifierManager,
    private val mGson: Gson,
    private val mDatabase: DigitalWalletDB
) : WalletManager {

    /**
     * 新增皮夾資料
     *
     * @param wallet 名稱
     * @return [Wallet]
     */
    override suspend fun create(wallet: Wallet) = withContext(Dispatchers.Default) {
        val uid = mDatabase.walletDao().insert(wallet)
        wallet.uid = uid
        return@withContext wallet
    }

    /**
     * 建立皮夾
     *
     * @param wallet [Wallet]
     */
    override suspend fun init(wallet: Wallet): Wallet = withContext(Dispatchers.Default) {
        // 呼叫”dwsdk-101i”、”dwsdk-102i”來取得DID金鑰與DID，並由App自行對DID與DID金鑰加密保存。
        // dwsdk-103i 建立 kx 金鑰
        return@withContext mVerifiableManager.createDecentralizedIdentifier(wallet)
    }

    override suspend fun login(wallet: Wallet): Boolean = withContext(Dispatchers.Default) {
        // 通知Flutter SDK 載入與 wallet.keyTag 相關的身份識別環境/密鑰。
        mIdentifierManager.dwsdk103i(wallet.keyTag)
        // 1. 生成新的金鑰對(dwsdk-101i) 預期返回 Pair 物件。
        mIdentifierManager.dwsdk101i()?.publicKey?.also { publicKey ->
            val did = mGson.fromJson(wallet.did, Dwsdk102i.Response::class.java)
            did.verificationMethod?.let { array ->
                for (i in 0 until array.size) {
                    did.verificationMethod.getOrNull(i)?.publicKeyJwk?.also { publicKeyJwk ->
                        if (publicKeyJwk.x == publicKey.x &&
                            publicKeyJwk.y == publicKey.y &&
                            publicKeyJwk.crv == publicKey.crv &&
                            publicKeyJwk.kty == publicKey.kty
                        ) {
                            // 比對出相同公鑰
                            return@withContext true
                        }
                    }
                }
            }
        }
        return@withContext false
    }

    override suspend fun update(wallet: Wallet): Int = withContext(Dispatchers.Default) {
        return@withContext mDatabase.walletDao().update(wallet)
    }

    /**
     * 刪除皮夾與對應的所有資料
     *
     * @param wallet 欲刪除的皮夾
     * @return 是否完成刪除
     */
    @Transaction
    override suspend fun delete(wallet: Wallet) {
        mDatabase.operationRecordDao().deletByWalletId(wallet.uid)
        mDatabase.credentialRecordDao().deleteByWalletId(wallet.uid)
        mDatabase.verifiableCredentialDao().deleteByWalletId(wallet.uid)
        mDatabase.walletDao().delete(wallet)
    }

    @Transaction
    override suspend fun deleteVerifiableCredential(verifiableCredential: VerifiableCredential, text: String) {
        val operationRecord = OperationRecord(
            walletId = verifiableCredential.walletId,
            vcId = verifiableCredential.uid,
            text = text,
            status = OperationEnum.DELETE_CARD,
            datetime = System.currentTimeMillis()
        )
        mDatabase.verifiableCredentialDao().delete(verifiableCredential)
        mDatabase.operationRecordDao().insert(operationRecord)
    }

    override suspend fun changePinccode(wallet: Wallet, pincode: String) {
        var randomSalt = mPref.randomSalt
        val hashPinCode = (pincode + randomSalt).sha256()
        wallet.pincode = hashPinCode
        mDatabase.walletDao().update(wallet)
    }

    override suspend fun getCount(): Int = withContext(Dispatchers.Default) {
        return@withContext mDatabase.walletDao().getAll().size
    }

    override suspend fun getWallet(uid: Long): Wallet? = withContext(Dispatchers.Default) {
        return@withContext mDatabase.walletDao().findByUID(uid) ?: mDatabase.walletDao().getAll().firstOrNull()
    }

    override suspend fun detectNicknameRepeat(name: String): Boolean = withContext(Dispatchers.Default) {
        val wallet = mDatabase.walletDao().getAll().find { it.nickname == name }
        return@withContext wallet != null
    }

    override suspend fun getAll(): List<Wallet> = withContext(Dispatchers.Default) {
        val list = mDatabase.walletDao().getAll()
        return@withContext list
    }
}