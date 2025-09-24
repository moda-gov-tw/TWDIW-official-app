import 'package:flutter/services.dart';
import 'dart:convert';
import 'did_key_gen.dart';
import 'openid_vc_vp.dart';

/// 用來處理 Flutter 與原生平台 (Android/iOS) 之間的方法通訊。
/// 提供各種 DID 與 VC/VP 操作功能，透過 MethodChannel 與原生互動。
class MessageHandler {
  // 建立平台通道，供原生端呼叫 Dart 方法使用
  static const platform = MethodChannel('did_sdk_channel');

  // SDK 版本資訊，可供原生查詢
  static const SDK_VERSION = '0.0.1';

  // DID 金鑰產生器 (使用 keyXentic)
  DIDKeyGenerator didKeyGenerator = DIDKeyGenerator();

  // OpenID VC/VP 處理工具
  OpenidVcVp openidVcVp = OpenidVcVp();

  /// 註冊 MethodChannel 並設定處理各種原生傳來的 method call
  void setupMethodChannel() {
    platform.setMethodCallHandler((MethodCall call) async {
      print(call.method); // 印出呼叫的 method 名稱，方便除錯

      switch (call.method) {
        // 回傳 SDK 版本資訊
        case 'getVersion':
          return jsonEncode({'code': '0', 'message': SDK_VERSION});

        // 初始化 KeyXentic 裝置
        case 'initKx':
          String arguments = call.arguments['initKx'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String keyTag = jsonMap['keyTag'];
          String type = jsonMap['type'];
          String pin = jsonMap['PIN'];
          String response = await didKeyGenerator.initKx(keyTag, type, pin);
          return response;

        // 產生公開金鑰 (P256)
        case 'generateKey':
          String publicKey = await didKeyGenerator.generateKeyKx();
          return publicKey;

        // 根據 JWK 公鑰產生 DID Document
        case 'generateDID':
          String publicKey = call.arguments['generateDID'];
          String response = await didKeyGenerator.generateDID(publicKey);
          return response;

        // 向 Issuer 請求憑證 VC（使用 OTP 驗證）
        case 'applyVC':
          String arguments = call.arguments['applyVC'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.applyVCKx(
              jsonMap['didFile'], jsonMap['qrCode'], jsonMap['otp']);
          return response;

        // 解碼 VC 資料
        case 'decodeVC':
          String arguments = call.arguments['decodeVC'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = openidVcVp.decodeVC(jsonMap['credential']);
          return response;

        // 線上驗證 VC
        case 'verifyVC':
          String arguments = call.arguments['verifyVC'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.verifyVC(
              jsonMap['credential'], jsonMap['didFile'], jsonMap['frontUrl']);
          return response;

        // 離線驗證 VC（不需要透過外部網路）
        case 'verifyVCOffline':
          String arguments = call.arguments['verifyVCOffline'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.verifyVCOffline(
              jsonMap['credential'],
              jsonMap['didFile'],
              jsonMap['issList'],
              jsonMap['vcList']);
          return response;

        // 解析 VP 掃描 QRCode 後取得的內容
        case 'parseVPQrcode':
          String arguments = call.arguments['parseVPQrcode'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.parseVPQrcode(
              jsonMap['qrCode'], jsonMap['frontUrl']);
          return response;

        // 產生 VP
        case 'generateVP':
          String arguments = call.arguments['generateVP'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.generateVPKx(jsonMap['didFile'],
              jsonMap['request_token'], jsonMap['vcs'], jsonMap['custom_data']);
          return response;

        // 產生 NFC 格式的 VP（支援透過 NFC 傳遞 VP）
        case 'generateVPNFC':
          String arguments = call.arguments['generateVPNFC'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response =
              await openidVcVp.generateVPNFC(jsonMap['didFile'], jsonMap['vc']);
          return response;

        // 驗證來自 NFC 的 VP
        case 'verifyVPNFC':
          String arguments = call.arguments['verifyVPNFC'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.verifyVPNFC(jsonMap['vp']);
          return response;

        // 從遠端下載 Issuer 清單
        case 'downloadIssList':
          String arguments = call.arguments['downloadIssList'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = '';
          response = await openidVcVp.downloadIssList(jsonMap['url']);

          return response;

        // 下載所有 VC 清單
        case 'downloadAllVCList':
          String arguments = call.arguments['downloadAllVCList'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.downloadAllVCList(
              jsonMap['vcs'], jsonMap['vcList']);
          return response;

        // 傳送 VC 給其他裝置
        case 'transferVC':
          String arguments = call.arguments['transferVC'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.transferVC(
              jsonMap['didFile'], jsonMap['credential']);
          return response;

        // 發送一般 JSON 格式的請求
        case 'sendRequest':
          String arguments = call.arguments['sendRequest'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.sendRequest(
              jsonMap['url'], jsonMap['type'], jsonMap['body']);
          return response;

        // 發送帶有 JWT 的請求
        case 'sendJWTRequest':
          String arguments = call.arguments['sendJWTRequest'];
          Map<String, dynamic> jsonMap = jsonDecode(arguments);
          String response = await openidVcVp.sendJWTRequest(
              jsonMap['url'], jsonMap['payload'], jsonMap['didFile']);
          return response;

        // 未實作的方法會丟出例外
        default:
          throw MissingPluginException('Not implemented: ${call.method}');
      }
    });
  }
}
