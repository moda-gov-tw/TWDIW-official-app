package com.moda.pkcsnfckit

import android.util.Base64
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec

class JWSCreator {

    fun publicKeyToJwk(publicKey: PublicKey):String {
        if (publicKey is ECPublicKey){
            val ecPublicKey: ECPublicKey = publicKey
            var xCoord = ecPublicKey.w.affineX.toByteArray()
            var yCoord = ecPublicKey.w.affineY.toByteArray()

            val jwkJson = JSONObject().apply {
                put("kty","EC")
                put("crv","P-256")
                put("x",Base64.encodeToString(xCoord, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
                put("y",Base64.encodeToString(yCoord, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
            }
            return jwkJson.toString()
        }
        throw IOException()
    }

    fun createJWS(header: String, payload: String, signature: ByteArray): String {
        val headerBase64 = Base64.encodeToString(header.encodeToByteArray(),
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val payloadBase64 = Base64.encodeToString(payload.encodeToByteArray(),Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

        val (r, s) = parseDerSignature(signature)

        val rBytes = toFixedLengthBytes(r, 32)
        val sBytes = toFixedLengthBytes(s, 32)
        val base64UrlSignature = Base64.encodeToString(rBytes+sBytes,Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

        return "$headerBase64.$payloadBase64.$base64UrlSignature"
    }

    fun jwkToPublicKey(publicKey: String): PublicKey? {
        try {
            val jsonObject = JSONObject(publicKey)
            if (jsonObject.get("kty")!="EC" || jsonObject.get("crv")!="P-256") {
                throw IllegalArgumentException("UNSUPPORTED_KEY_TYPE")
            }

            val x = BigInteger(1, Base64.decode(jsonObject.get("x").toString(), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP ))
            val y = BigInteger(1, Base64.decode(jsonObject.get("y").toString(), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))

            val ecSpec = ECGenParameterSpec("secp256r1")
            val keyPairGenerator = KeyPairGenerator.getInstance("EC")
            keyPairGenerator.initialize(ecSpec)
            val ecParameterSpec = (keyPairGenerator.generateKeyPair().public as ECPublicKey).params

            val ecPoint = ECPoint(x, y)
            val publicKeySpec = ECPublicKeySpec(ecPoint, ecParameterSpec)

            val keyFactory = KeyFactory.getInstance("EC")
            return keyFactory.generatePublic(publicKeySpec)
        }catch (e: Exception) {
            throw e
        }
    }

    private fun parseDerSignature(signature: ByteArray): Pair<BigInteger, BigInteger> {
        var offset = 0

        if (signature[offset++] != 0x30.toByte()) {
            throw IllegalArgumentException("Invalid DER signature format")
        }
        val sequenceLength = signature[offset++].toInt() and 0xFF

        if (signature[offset++] != 0x02.toByte()) {
            throw IllegalArgumentException("Expected INTEGER for r")
        }
        val rLength = signature[offset++].toInt() and 0xFF
        val r = BigInteger(1, signature.copyOfRange(offset, offset + rLength))
        offset += rLength

        if (signature[offset++] != 0x02.toByte()) {
            throw IllegalArgumentException("Expected INTEGER for s")
        }
        val sLength = signature[offset++].toInt() and 0xFF
        val s = BigInteger(1, signature.copyOfRange(offset, offset + sLength))
        offset += sLength

        return r to s
    }

    private fun toFixedLengthBytes(value: BigInteger, length: Int): ByteArray {
        val bytes = value.toByteArray()
        return if (bytes.size == length) {
            bytes
        } else if (bytes.size < length) {
            ByteArray(length - bytes.size) { 0x00 } + bytes
        } else {
            bytes.copyOfRange(bytes.size - length, bytes.size)
        }
    }


}