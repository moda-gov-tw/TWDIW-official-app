package com.moda.pkcsnfckit

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log


class ModaNFCReader(private val context: Context) {
    private var nfcAdapter: NfcAdapter? = null

    val SELECT_APDU_TagInfo = byteArrayOf(
        0x00, 0xA4.toByte(), 0x04, 0x00, 0x06,
        0xF2.toByte(), 0x23, 0x34, 0x45, 0x56, 0x68
    )

    val SELECT_GET_RESPONSE = byteArrayOf(
        0x00.toByte(), 0xA4.toByte(), 0x00.toByte(), 0x00.toByte(), 0x04.toByte(),
        0x72.toByte(), 0x65.toByte(), 0x73.toByte(), 0x70.toByte()
    )

    interface ScanCallback {
        fun onFinish(response: ByteArray)
        fun onFailed()
    }
    fun startSending(fullData: ByteArray, callback: ScanCallback) {
        val readerCallback = NfcAdapter.ReaderCallback { tag: Tag ->
            tag.let {
                try {
                    val techList = it.techList
                    for (tech in techList) {
                        if (tech.equals(IsoDep::class.java.name)) {
                            val isoDep = IsoDep.get(tag)
                            isoDep.timeout = 6000
                            isoDep.connect()

                            //select apdu start
                            val selectAid = SELECT_APDU_TagInfo

                            val response = isoDep.transceive(selectAid)
                            if (response.size < 2 || response[response.size - 2] != 0x90.toByte() || response[response.size - 1] != 0x00.toByte()) {
                                Log.d("ModaNFCReader", "selectApdu failed")
                                disableNfcForegroundDispatch()
                                callback.onFailed()
                                return@ReaderCallback
                            }

                            //sending data start
                            val MAX_CHUNK_SIZE = 255
                            var offset = 0

                            while (offset < fullData.size) {
                                val length = minOf(MAX_CHUNK_SIZE, fullData.size - offset)
                                val chunk = fullData.copyOfRange(offset, offset + length)

                                val apdu = chunk
                                val response = isoDep.transceive(apdu)

                                if (response.size < 2 || response[response.size - 2] != 0x90.toByte() || response[response.size - 1] != 0x00.toByte()) {
                                    disableNfcForegroundDispatch()
                                    callback.onFailed()
                                    return@ReaderCallback
                                }

                                offset += length

                                //sending data end
                                if (length < 255) {
                                    val end = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
                                    val responseEnd = isoDep.transceive(end)

                                    if (responseEnd.contentEquals(byteArrayOf(0x90.toByte(), 0x00))) {
                                        var fullResponse = ByteArray(0)
                                        //waiting response
                                        var getResponse = isoDep.transceive(SELECT_GET_RESPONSE)

                                        while (getResponse.contentEquals(byteArrayOf(0x90.toByte(), 0x00))) {
                                            getResponse = isoDep.transceive(SELECT_GET_RESPONSE)
                                        }

                                        //first response
                                        fullResponse += getResponse

                                        while (true) {
                                            getResponse = isoDep.transceive(SELECT_GET_RESPONSE)
                                            if (getResponse.contentEquals(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()))) {
                                                break
                                            }
                                            Log.d("SELECT_GET_RESPONSE", "Received response data")
                                            fullResponse += getResponse
                                        }

                                        disableNfcForegroundDispatch()
                                        callback.onFinish(fullResponse)
                                        return@ReaderCallback
                                    }
                                }
                            }

                            disableNfcForegroundDispatch()
                            callback.onFinish("".toByteArray())
                        }
                    }
                } catch (e: Exception) {
                    disableNfcForegroundDispatch()
                    e.printStackTrace()
                    callback.onFailed()
                }

            }
        }
        val options = NfcAdapter.FLAG_READER_NFC_A

        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (nfcAdapter == null) {
            return
        }
        if (nfcAdapter!!.isEnabled) {
            nfcAdapter!!.enableReaderMode(context as Activity?, readerCallback, options, null)
        }
    }

    fun cancelSending() {
        disableNfcForegroundDispatch()
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (nfcAdapter == null) {
            return
        }
        if (nfcAdapter!!.isEnabled) {
            nfcAdapter!!.disableReaderMode(context as Activity)
        }
    }
}