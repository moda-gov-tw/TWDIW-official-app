package tw.gov.moda.digitalwallet.core.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tw.gov.moda.digitalwallet.data.db.IssuerOrg
import tw.gov.moda.digitalwallet.data.element.AutoLogoutEnum
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.data.element.PageEnum
import tw.gov.moda.digitalwallet.data.element.RemindPeriodEnum
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk201i

class DBTypeConverter {
    @TypeConverter
    fun fromRemindPeriodEnum(value: RemindPeriodEnum): Int {
        return value.period
    }

    @TypeConverter
    fun toRemindPeriodEnum(value: Int): RemindPeriodEnum {
        return RemindPeriodEnum.entries.find { it.period == value } ?: RemindPeriodEnum.NORMAL
    }
    @TypeConverter
    fun fromCardStatusEnum(value: CardStatusEnum): Int {
        return value.code
    }

    @TypeConverter
    fun toCardStatusEnum(value: Int): CardStatusEnum {
        return CardStatusEnum.entries.find { it.code == value } ?: CardStatusEnum.Unknown
    }

    @TypeConverter
    fun fromCardRecordEnum(value: OperationEnum): Int {
        return value.code
    }

    @TypeConverter
    fun toCardRecordEnum(value: Int): OperationEnum {
        return OperationEnum.entries.find { it.code == value } ?: OperationEnum.UNKNOWN
    }

    @TypeConverter
    fun fromAutoLogoutEnum(value: AutoLogoutEnum): Int {
        return value.duration
    }

    @TypeConverter
    fun toAutoLogoutEnum(value: Int): AutoLogoutEnum {
        return AutoLogoutEnum.entries.find { it.duration == value } ?: AutoLogoutEnum.NEVER
    }

    @TypeConverter
    fun fromJson(value: String): Array<String> {
        if (value.isNotBlank() && value.startsWith("[")) {
            val type = object : TypeToken<Array<String>>() {}.type
            return Gson().fromJson(value, type)
        } else {
            return arrayOf()
        }
    }

    @TypeConverter
    fun toJson(value: Array<String>?): String {
        if (value != null) {
            return Gson().toJson(value)
        } else {
            return "[]"
        }
    }

    @TypeConverter
    fun fromJsonToCredentialSubjectFieldMap(value: String): LinkedHashMap<String, Dwsdk201i.Resposne.CredentialSubjectField> {
        if (value.isNotBlank() && value.startsWith("{")) {
            val type = object : TypeToken<LinkedHashMap<String, Dwsdk201i.Resposne.CredentialSubjectField>>() {}.type
            return Gson().fromJson(value, type)
        } else {
            return linkedMapOf()
        }
    }

    @TypeConverter
    fun fromCredentialSubjectFieldMapToJson(value: LinkedHashMap<String, Dwsdk201i.Resposne.CredentialSubjectField>?): String {
        if (value != null) {
            return Gson().toJson(value)
        } else {
            return "{}"
        }
    }

    @TypeConverter
    fun fromJsonToCredentialDefinitionMap(value: String): LinkedHashMap<String, Dwsdk201i.Resposne.CredentialDefinition> {
        if (value.isNotBlank() && value.startsWith("{")) {
            val type = object : TypeToken<LinkedHashMap<String, Dwsdk201i.Resposne.CredentialDefinition>>() {}.type
            return Gson().fromJson(value, type)
        } else {
            return linkedMapOf()
        }
    }

    @TypeConverter
    fun fromCredentialDefinitionMapToJson(value: LinkedHashMap<String, Dwsdk201i.Resposne.CredentialDefinition>?): String {
        if (value != null) {
            return Gson().toJson(value)
        } else {
            return "{}"
        }
    }

    @TypeConverter
    fun fromJsonToIssuerOrg(value: String): IssuerOrg? {
        if (value.isNotBlank() && value.startsWith("{")) {
            val type = object : TypeToken<IssuerOrg>() {}.type
            return Gson().fromJson(value, type)
        } else {
            return null
        }
    }

    @TypeConverter
    fun issuerOrgToJson(value: IssuerOrg?): String? {
        if (value != null) {
            return Gson().toJson(value)
        } else {
            return null
        }
    }

    @TypeConverter
    fun fromPageEnum(value: PageEnum): String = value.name

    @TypeConverter
    fun toPageEnum(value: String): PageEnum = PageEnum.entries.find { it.name == value } ?: PageEnum.ShowCredential
}