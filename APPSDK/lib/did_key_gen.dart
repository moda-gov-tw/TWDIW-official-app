import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:pointycastle/export.dart';
import 'package:convert/convert.dart';
import 'package:base_x/base_x.dart';
import 'utils/utils.dart';

/// 負責與原生層互動以產生金鑰，並產出符合 W3C DID 標準的 DID 文件
class DIDKeyGenerator {
  // 定義與原生端通信的 MethodChannel，keyxentic 為通訊標籤
  static const kxPlatform =
      MethodChannel('com.example.flutter_method_channel_example/keyxentic');

  Utils utils = Utils();

  /// 初始化 Kx 模組
  ///
  /// [keyTag] 金鑰的識別標籤
  /// [type] 金鑰類型，例如 "PLATFORM", "kx701"
  /// [pin] 使用者 PIN 碼
  ///
  /// 成功回傳格式化成功訊息，失敗則回傳錯誤訊息
  Future<String> initKx(String keyTag, String type, String pin) async {
    try {
      Uint8List pinBytes = Uint8List.fromList(utf8.encode(pin));
      final bool response = await kxPlatform
          .invokeMethod('init', {'tag': keyTag, 'type': type, 'pin': pinBytes});
      return utils.response(code: '0', message: response.toString());
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// 透過 Kx 安全模組產生 P-256 金鑰，並回傳其 JWK 格式的公鑰
  ///
  /// 成功時包含 `publicKey` 欄位；失敗回傳錯誤訊息
  Future<String> generateKeyKx() async {
    try {
      final String response = await kxPlatform.invokeMethod('getP256Key');

      Map<String, dynamic> jwk = jsonDecode(response);
      final data = {
        'publicKey': jwk,
      };
      return utils.response(code: '0', message: 'SUCCESS', data: data);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// 根據傳入的 JWK 格式公鑰，產生 DID 文件
  ///
  /// [publicKeyJwk] 是 JSON 字串格式的 JWK 公鑰
  ///
  /// 成功時符合 W3C DID 規範的 DID 文件資料；失敗回傳錯誤訊息
  Future<String> generateDID(String publicKeyJwk) async {
    try {
      Map<String, dynamic> publicKey = jsonDecode(publicKeyJwk);
      String did = generateDidFromJwk(publicKeyJwk);

      final context = [
        "https://www.w3.org/ns/did/v1",
        "https://w3id.org/security/suites/jws-2020/v1"
      ];

      List<Map<String, dynamic>> verificationMethodEntry = [
        {
          'id': 'did:key:$did#$did',
          'type': 'JsonWebKey2020',
          'controller': 'did:key:$did',
          'publicKeyJwk': publicKey
        }
      ];

      Map<String, dynamic> response = {
        '@context': context,
        'id': 'did:key:$did',
        'verificationMethod': verificationMethodEntry
      };

      return utils.response(code: '0', message: 'SUCCESS', data: response);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// 將 JWK 格式的公鑰轉換為 PointyCastle 使用的 ECPublicKey
  ///
  /// [jwk] 是 Map 格式的 JWK，包含 'x' 與 'y' 欄位（Base64Url 編碼）
  ECPublicKey convertJwkToECPublicKey(Map<String, dynamic> jwk) {
    final x = base64Url
        .decode(Utils().addBase64Padding(jwk['x'])); // Base64Url 轉 byte
    final y = base64Url
        .decode(Utils().addBase64Padding(jwk['y'])); // Base64Url 轉 byte

    final eccParams = ECCurve_secp256r1(); // 使用 P-256 曲線參數
    final curve = eccParams.curve;
    final xBigInt = BigInt.parse(hex.encode(x), radix: 16);
    final yBigInt = BigInt.parse(hex.encode(y), radix: 16);
    final ecPoint = curve.createPoint(xBigInt, yBigInt);

    return ECPublicKey(ecPoint, eccParams);
  }

  /// 將 JWK 轉換為 DID 字串格式
  ///
  /// 1. 將 JWK 轉成 UTF8 byte array 並編碼為 Hex
  /// 2. 加上 Hex 前綴 `d1d603`（Multicodec prefix for P-256 JWK?）
  /// 3. 使用 Base58 編碼
  ///
  /// 最後回傳形如 `did:key:z...` 的 DID
  String generateDidFromJwk(String jwk) {
    // 將 JWK JSON 字串轉成 byte array 並轉 hex
    List<int> bytes = utf8.encode(jwk);
    String hexString = hex.encode(bytes);

    // Multicodec 前綴
    String hexPrefix = "d1d603";
    String combinedHex = hexPrefix + hexString;

    // 轉為 bytes 再進行 Base58 編碼
    const String base58Alphabet =
        "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    final BaseXCodec base58 = BaseXCodec(base58Alphabet);
    Uint8List combinedHexBytes = Uint8List.fromList(hex.decode(combinedHex));
    String didKey = "z${base58.encode(combinedHexBytes)}";

    return didKey;
  }
}
