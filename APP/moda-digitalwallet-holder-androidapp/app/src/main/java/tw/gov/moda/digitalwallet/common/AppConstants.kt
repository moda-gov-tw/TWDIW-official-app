package tw.gov.moda.digitalwallet.common

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import tw.gov.moda.diw.BuildConfig

/**
 * 常數檔案
 */
object AppConstants {

    /**
     * 基本常數
     */
    object Common {
        const val MARK_TEXT = "************"
        const val LOCKED_PIN_CODE = "LOCKED_PIN_CODE"
        const val AUTO_LOGOUT_THREAD = "AUTO_LOGOUT_THREAD"
    }

    /**
     * Dialog 的 tag
     */
    object Dialog {
        const val MESSAGE_DIALOG = "MESSAGE_DIALOG"
        const val SDK_MESSAGE_DIALOG = "SDK_MESSAGE_DIALOG"
        const val NETWORK_ERROR_DIALOG = "NETWORK_ERROR_DIALOG"
    }

    /**
     * Flutter 用的 Channel Method
     */
    object DID {
        const val CHANNEL_NAME = "did_sdk_channel"

        const val GENERATE_KEY = "generateKey"

        const val GENERATE_DID = "generateDID"

        const val GENERATE_KX = "initKx"

        const val APPLY_VC = "applyVC"

        const val DECODE_VC = "decodeVC"

        const val VERIFY_VC = "verifyVC"

        const val PARSE_VP_QRCODE = "parseVPQrcode"

        const val GENERATE_VP = "generateVP"

        const val DOWNLOAD_ISSUER_LIST = "downloadIssList"

        const val DOWNLOAD_ALL_VC_LIST = "downloadAllVCList"

        const val VERIFY_VC_OFFLINE = "verifyVCOffline"

        const val SEND_REQUEST = "sendRequest"

        const val SEND_JWT_REQUEST = "sendJWTRequest"
    }

    object Metadata {
        const val ISSUER_URL = "issuer_url"
        const val IAL_LEVEL = "ial"
    }

    /**
     * SharedPreferences 的 Table Name
     */
    object PREF {
        const val TABLE_NAME = "MODA_DATA"

        const val WALLET_VC_LIST_PREFIX = "MODA_WALLET_"
    }

    /**
     * Deeplink用的辨別符
     */
    object Deeplink {
        const val SCHEME = "modadigitalwallet"
        const val ApplyVC = "credential_offer"
        const val ApplyVP = "authorize"
        const val StaticQRCode = "staticqrcode"
        const val Mode = "mode"
        const val VPUid = "vpUid"
        const val VCUid = "vcUid"
        const val ModeVC = "vc"
        const val ModeVP = "vp"
    }

    /**
     * 網頁用常數
     */
    object Net {
        const val DID_ISSUER_URL = BuildConfig.DID_ISSUER_URL

        const val IMAGE_WEB_URL = BuildConfig.OFFICIAL_IMAGE_URL

        const val CITIZEN_DIGITAL_CREDENTIAL_URL = BuildConfig.CITIZEN_DIGITAL_URL

        const val REPORT_QUESTION = "https://docs.google.com/forms/d/e/1FAIpQLSfCHZJNelSpM-GbanVfpHKs5IuPIZzq1XFiN76AMuER3kmqwA/viewform"

        const val COMMON_QUESTION = "https://www.wallet.gov.tw/zh-tw/qa.html"

        const val SCRIPT_NAME = "mobile"
    }

    /**
     * 資料庫用常數
     */
    object Database {
        const val NAME = "DigitalWallet_Secret"
        const val OLD_NAME = "DigitalWallet"
        const val TABLE_WALLET = "wallet"
        const val TABLE_VERIFIABLE_CREDENTIAL = "verifiable_credential"
        const val TABLE_ISSUER = "issuer"
        const val TABLE_CARD_RECORD = "card_record"
        const val TABLE_CREDENTIAL_RECORD = "credential_record"
        const val TABLE_PRESENTATION_RECORD = "presentation_record"
        const val TABLE_OPERATION_RECORD = "operation_record"
        const val TABLE_FAVORITE_SHOW_CREDENTIAL = "favorite_show_credential"
        const val TABLE_SEARCH_RECORD = "search_record"
        const val DB_VERSION = 11

        val MIGRATION_FROM_5 = object : Migration(5, DB_VERSION) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `verifiable_credential` ADD COLUMN `trustBadge` INTEGER DEFAULT 0 NOT NULL")
                MIGRATION_FROM_6.migrate(db)
            }
        }
        val MIGRATION_FROM_6 = object : Migration(6, DB_VERSION) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `favorite_show_credential` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `walletId` INTEGER NOT NULL, `vpUid` TEXT NOT NULL, `name` TEXT NOT NULL, `verifierModuleUrl` TEXT NOT NULL, `logoUrl` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `search_record` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `walletId` INTEGER NOT NULL, `keyword` TEXT NOT NULL, `recordTime` INTEGER NOT NULL)")
                MIGRATION_FROM_7.migrate(db)
            }
        }
        val MIGRATION_FROM_7 = object : Migration(7, DB_VERSION) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `search_record` ADD COLUMN `sourceType` TEXT NOT NULL DEFAULT 'ShowCredential'")
                MIGRATION_FROM_8.migrate(db)
            }
        }

        val MIGRATION_FROM_8 = object : Migration(8, DB_VERSION) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `credential_record` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `walletId` INTEGER NOT NULL, `vcId` INTEGER NOT NULL, `text` TEXT NOT NULL, `authorizationUnit` TEXT NOT NULL, `authorizationPurpose` TEXT NOT NULL, `authorizationField` TEXT NOT NULL, `datetime` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `operation_record` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `walletId` INTEGER NOT NULL, `vcId` INTEGER, `text` TEXT NOT NULL, `status` INTEGER NOT NULL, `datetime` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `presentation_record` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `walletId` INTEGER NOT NULL, `text` TEXT NOT NULL, `vcIds` TEXT NOT NULL, `vcNames` TEXT NOT NULL, `authorizationUnit` TEXT NOT NULL, `authorizationPurpose` TEXT NOT NULL, `authorizationFields` TEXT NOT NULL, `datetime` INTEGER NOT NULL)")
                MIGRATION_FROM_9.migrate(db)
            }
        }

        val MIGRATION_FROM_9 = object : Migration(9, DB_VERSION) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `verifiable_credential` ADD COLUMN `remind` INTEGER DEFAULT 0 NOT NULL")
                db.execSQL("UPDATE `verifiable_credential` SET `remind` = -1 WHERE `status` IN (0, 2)")
                MIGRATION_FROM_10.migrate(db)
            }
        }
        val MIGRATION_FROM_10 = object : Migration(10, DB_VERSION) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `favorite_show_credential_new` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `walletId` INTEGER NOT NULL, `vpUid` TEXT, `name` TEXT, `verifierModuleUrl` TEXT, `logoUrl` TEXT)")
                db.execSQL("INSERT INTO `favorite_show_credential_new` (`uid`, `walletId`, `vpUid`, `name`, `verifierModuleUrl`, `logoUrl`) SELECT `uid`, `walletId`, `vpUid`, `name`, `verifierModuleUrl`, `logoUrl` FROM `favorite_show_credential`")
                db.execSQL("DROP TABLE `favorite_show_credential`")
                db.execSQL("ALTER TABLE `favorite_show_credential_new` RENAME TO `favorite_show_credential`")
            }
        }
    }
}