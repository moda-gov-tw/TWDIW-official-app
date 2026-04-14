package tw.gov.moda.digitalwallet.data.annotation

import androidx.annotation.StringDef

@StringDef(APIMethod.GET, APIMethod.POST)
@Retention(AnnotationRetention.SOURCE)
annotation class APIMethod {
    companion object {
        const val GET = "GET"
        const val POST = "POST"
    }
}
