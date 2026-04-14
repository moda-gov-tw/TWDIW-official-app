package tw.gov.moda.digitalwallet.data.model.dwsdk

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class Dwsdk201i {
    @Keep
    data class Request(
        @SerializedName("didFile")
        val didFile: String,
        @SerializedName("qrCode")
        val qrCode: String,
        @SerializedName("otp")
        val otp: String
    )

    @Keep
    data class Resposne(
        @SerializedName("credential")
        val credential: String?,

        @SerializedName("credentialDefinition")
        val credentialDefinition: CredentialDefinition?,

        @SerializedName("credentialMetadata")
        val credentialMetadata: CredentialMetadata?
    ) {
        data class CredentialDefinition(
            @SerializedName("format")
            val format: String?,

            @SerializedName("scope")
            val scope: String?,

            @SerializedName("cryptographic_binding_methods_supported")
            val cryptographicBindingMethodsSupported: List<String>?,

            @SerializedName("credential_signing_alg_values_supported")
            val credentialSigningAlgValuesSupported: List<String>?,

            @SerializedName("credential_definition")
            val credentialDefinitionObj: CredentialDefinitionObj?,

            @SerializedName("proof_types_supported")
            val proofTypesSupported: ProofTypesSupported?,

            @SerializedName("display")
            val displayArray: List<DisplayItem>?
        )

        data class CredentialDefinitionObj(
            @SerializedName("type")
            val type: Array<String>?,

            @SerializedName("credentialSubject")
            val credentialSubject: LinkedHashMap<String, CredentialSubjectField>?
        )

        data class CredentialSubjectField(
            @SerializedName("mandatory")
            val mandatory: Boolean?,

            @SerializedName("value_type")
            val valueType: String?,

            @SerializedName("display")
            val displayArray: List<DisplayItem>?
        )

        data class DisplayItem(
            @SerializedName("name")
            val name: String?,

            @SerializedName("locale")
            val locale: String?,

            @SerializedName("description")
            val description: String?,

            @SerializedName("background_image")
            val backgroundImage: BackgroundImage? = null
        )

        data class BackgroundImage(
            @SerializedName("uri")
            val uri: String?
        )

        data class ProofTypesSupported(
            @SerializedName("jwt")
            val jwt: JwtProof?
        )

        data class JwtProof(
            @SerializedName("proof_signing_alg_values_supported")
            val proofSigningAlgValuesSupported: List<String>?
        )

        data class CredentialMetadata(
            @SerializedName("credential_issuer")
            val credentialIssuer: String?,

            @SerializedName("credential_endpoint")
            val credentialEndpoint: String?,

            @SerializedName("display")
            val displayArray: List<DisplayItem>?,

            @SerializedName("credential_configurations_supported")
            val credentialConfigurationsSupported: LinkedHashMap<String, CredentialDefinition>?
        )
    }


}
