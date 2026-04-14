package com.moda.pkcsnfckit

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import android.util.Base64
import android.util.Log
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.util.Formatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ModaDidKey(private val keyTag: String, val type: SourceType, val PIN: String, private val context: Context) {

    private var keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    enum class SourceType {
        PLATFORM
    }

    suspend fun getP256Key(): String? = suspendCoroutine { continuation ->
        when (type) {
            SourceType.PLATFORM ->  continuation.resume(getP256KeyPlatform())
        }
    }
    private fun getP256KeyPlatform(): String? {
        try {
            val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
            if (!ks.isKeyEntry(keyTag)) {
                generateKeyPair()
            }
            keyStore = ks
            if (!keyStore.isKeyEntry(keyTag)) {
                return null
            }

            val entry: KeyStore.Entry = ks.getEntry(keyTag, null)
            if (entry !is KeyStore.PrivateKeyEntry) {
                return null
            }

            //public key
            val publicKey = ks.getCertificate(keyTag).publicKey

            val jwsCreator = JWSCreator()
            return jwsCreator.publicKeyToJwk(publicKey)
        }catch (e: Exception) {
            Log.d("ModaDidKey", e.message.toString())
            throw e
        }
    }

    suspend fun sign(header: String, payload: String): String? = suspendCoroutine { continuation ->
        when (type) {
            SourceType.PLATFORM ->  continuation.resume(signPlatform(header, payload))
        }
    }
    private fun signPlatform(header: String, payload: String): String? {
        try {
            val entry: KeyStore.Entry = keyStore.getEntry(keyTag, null)
            if (entry !is KeyStore.PrivateKeyEntry) {
                return null
            }
            val headerBase64 = Base64.encodeToString(header.encodeToByteArray(),
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val payloadBase64 = Base64.encodeToString(payload.encodeToByteArray(),Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

            val signingInput = "$headerBase64.$payloadBase64"
            val data2BSigned = signingInput.encodeToByteArray()

            val signature: ByteArray = Signature.getInstance("SHA256withECDSA").run {
                initSign(entry.privateKey)
                update(data2BSigned)
                sign()
            }
            val jswCreator = JWSCreator()
            return jswCreator.createJWS(header, payload, signature)
        }catch (e: Exception) {
            Log.d("ModaDidKey", e.message.toString())

            return null
        }
    }

    suspend fun delete(): Boolean = suspendCoroutine { continuation ->
        when (type) {
            SourceType.PLATFORM ->  continuation.resume(deletePlatform())
        }
    }
    private fun deletePlatform(): Boolean {
        try {
            keyStore.deleteEntry(keyTag)
        }catch (e: Exception){
            Log.d("ModaDidKey", e.message.toString())
            return false
        }
        return true
    }

    suspend fun verifyUser(publicKey: String): Boolean = suspendCoroutine { continuation ->
        when (type) {
            SourceType.PLATFORM ->  continuation.resume(verifyUserPlatform(publicKey))
        }
    }
    private fun verifyUserPlatform(publicKey: String): Boolean {
        try {
            val jswCreator = JWSCreator()
            val jwkToPublicKey = jswCreator.jwkToPublicKey(publicKey)
            val mPublicKey = keyStore.getCertificate(keyTag).publicKey

            if (jwkToPublicKey != null) {
                if (jwkToPublicKey == mPublicKey) {
                    return true
                }
            }
            return false
        }catch (e: Exception) {
            Log.d("ModaDidKey", e.message.toString())
            return false
        }
    }

    private fun generateKeyPair() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                generateKeyPairWithStrongBox(true)
            }catch (e: StrongBoxUnavailableException) {
                try {
                    generateKeyPairWithStrongBox(false)
                }catch (e: Exception) {
                    Log.d("ModaDidKey", e.message.toString())
                }
            }catch (e: Exception) {
                Log.d("ModaDidKey", e.message.toString())
                try {
                    generateKeyPairWithStrongBox(false)
                }catch (e: Exception) {
                    Log.d("ModaDidKey", e.message.toString())
                }
            }
        }else {
            try {
                generateKeyPairWithStrongBox(false)
            }catch (e: Exception) {
                Log.d("ModaDidKey", e.message.toString())
            }
        }
    }
    private fun generateKeyPairWithStrongBox(isStrongBox: Boolean) {
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            KeyGenParameterSpec.Builder(
                keyTag,//key tag
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setIsStrongBoxBacked(isStrongBox)
                .run {
                    setDigests(KeyProperties.DIGEST_SHA256)
                    build()
                }
        } else {
            KeyGenParameterSpec.Builder(
                keyTag,//key tag
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .run {
                    setDigests(KeyProperties.DIGEST_SHA256)
                    build()
                }
        }
        kpg.initialize(parameterSpec)

        val kp = kpg.generateKeyPair()
    }
    suspend fun cancel(): Boolean = suspendCoroutine { continuation ->
        continuation.resume(true)
    }
}

fun ByteArray.toHex(uppercase: Boolean = false): String {
    val formatter = Formatter()

    for (b in this) {
        formatter.format(if (uppercase) "%02X" else "%02x", b)
    }
    return formatter.toString()
}