package tw.gov.moda.digitalwallet.extension

import kotlin.reflect.KClass


fun KClass<*>.hashTag(): String {
    return this.qualifiedName?.sha256() ?: ""
}

