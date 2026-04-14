package tw.gov.moda.digitalwallet.core.wallet

import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.db.Wallet

/**
 * 皮夾管理器
 */
interface WalletManager {
    suspend fun init(wallet: Wallet): Wallet

    suspend fun login(wallet: Wallet): Boolean

    suspend fun create(wallet: Wallet): Wallet

    suspend fun update(wallet: Wallet): Int

    suspend fun delete(wallet: Wallet)

    suspend fun deleteVerifiableCredential(verifiableCredential: VerifiableCredential, text: String)

    suspend fun changePinccode(wallet: Wallet, pincode: String)

    suspend fun getCount(): Int

    suspend fun getWallet(uid: Long): Wallet?

    suspend fun detectNicknameRepeat(name: String): Boolean

    suspend fun getAll(): List<Wallet>
}