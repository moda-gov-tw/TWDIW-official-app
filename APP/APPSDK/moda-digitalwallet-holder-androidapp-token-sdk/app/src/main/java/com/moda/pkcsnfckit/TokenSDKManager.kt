package com.moda.pkcsnfckit

import android.content.Context
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TokenSDKManager(flutterEngine: FlutterEngine, context: Context) {

    private val CHANNEL = "com.flutter_method_channel/gov.moda.dw"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler{ call, result ->
            when (call.method) {
                "init" -> {
                    scope.launch {
                        val tag = call.argument<String>("tag").toString()
                        val type = call.argument<String>("type").toString()
                        val pin = call.argument<ByteArray>("pin")
                        if (pin!= null && init(tag, type, pin, context)) {
                            result.success(true)
                        }else {
                            result.error("UNAVAILABLE", "init error.", null)
                        }
                    }
                }
                "getP256Key" -> {
                    scope.launch {
                        val p256Key = getP256Key()

                        if (p256Key != "") {
                            result.success(p256Key)
                        } else {
                            result.error("UNAVAILABLE", "p256Key error.", null)
                        }
                    }
                }
                "verifyUser" -> {
                    val myPublicKey = call.argument<String>("publicKey")
                    scope.launch {
                        if (myPublicKey != null) {
                            result.success(verifyUser(myPublicKey))
                        }else {
                            result.error("UNAVAILABLE", "Public key not available.", null)
                        }
                    }
                }
                "sign" -> {
                    val header = call.argument<String>("header")
                    val payload = call.argument<String>("payload")
                    scope.launch {
                        if (header != null && payload != null) {
                            result.success(sign(header, payload))
                        }else {
                            result.error("UNAVAILABLE", "Public key not available.", null)
                        }
                    }
                }
                "delete" -> {
                    scope.launch {
                        if (delete()) {
                            result.success(true)
                        } else {
                            result.error("UNAVAILABLE", "delete error.", null)
                        }
                    }
                }
                "cancel" -> {
                    scope.launch {
                        cancel()
                        result.success(true)
                    }
                }
                else -> result.notImplemented()
            }
        }
    }

    private lateinit var modaDidKey: ModaDidKey

    private fun init(tag: String, type: String, pin: ByteArray, context: Context): Boolean{
        val pinString = String(pin)
        modaDidKey = when (type) {
            "platform" -> ModaDidKey(tag, ModaDidKey.SourceType.PLATFORM, pinString, context)
            else -> return false
        }
        return true
    }

    private suspend fun getP256Key(): String{
        val publicKey = modaDidKey.getP256Key()
        return publicKey.toString()
    }

    private suspend fun verifyUser(publicKey: String): Boolean {
        return modaDidKey.verifyUser(publicKey)
    }

    private suspend fun sign(header: String, payload: String): String{
        val signData = modaDidKey.sign(header,payload)
        return signData.toString()
    }

    private suspend fun delete(): Boolean{
        return modaDidKey.delete()
    }

    private suspend fun cancel() {
        modaDidKey.cancel()
    }
}