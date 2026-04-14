package tw.gov.moda.digitalwallet.data.model

import com.google.gson.annotations.SerializedName

data class RequestToken(
    @SerializedName("response_uri")
    val responseUri: String?,
    @SerializedName("aud")
    val aud: String?,
    @SerializedName("iss")
    val iss: String?,
    @SerializedName("presentation_definition")
    val presentationDefinition: PresentationDefinition?,
    @SerializedName("response_type")
    val responseType: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("nonce")
    val nonce: String?,
    @SerializedName("client_id")
    val clientId: String?,
    @SerializedName("response_mode")
    val responseMode: String?,
    @SerializedName("client_metadata")
    val clientMetadata: ClientMetadata?
) {
    data class PresentationDefinition(
        @SerializedName("id")
        val id: String?,
        @SerializedName("purpose")
        val purpose: String?,
        @SerializedName("input_descriptors")
        val inputDescriptors: ArrayList<InputDescriptors>?,
    ) {
        data class InputDescriptors(
            @SerializedName("id")
            val id: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("constraints")
            val constraints: Constraints?
        ) {
            data class Constraints(
                @SerializedName("fields")
                val fields: ArrayList<Field>?
            ) {
                data class Field(
                    @SerializedName("path")
                    val path: ArrayList<String>?,

                    @SerializedName("filter")
                    val filter: Filter?
                ) {
                    data class Filter(
                        @SerializedName("contains")
                        val contains: Contains?,
                        @SerializedName("type")
                        val type: String?
                    ) {
                        data class Contains(
                            @SerializedName("const")
                            val const: String?
                        )
                    }
                }
            }
        }
    }

    data class ClientMetadata(
        @SerializedName("jwks_uri")
        val jwksUri: String?,
        @SerializedName("vp_formats")
        val vpFormats: VPFormats?,
        @SerializedName("response_types")
        val responseTypes: ArrayList<String>?
    ) {
        data class VPFormats(
            @SerializedName("jwt_vc")
            val jwtVC: Alg?,
            @SerializedName("jwt_vp")
            val jwtVP: Alg?,
        ) {
            data class Alg(
                @SerializedName("alg")
                val list: ArrayList<String>?
            )
        }
    }
}