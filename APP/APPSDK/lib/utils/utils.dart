import 'dart:convert';
import 'package:pointycastle/export.dart' as pc;
import 'package:dart_jsonwebtoken/dart_jsonwebtoken.dart';
import 'dart:typed_data';
import 'package:crypto/crypto.dart';

/// 工具類別：Utils
///
/// 此類別封裝了 JWT、SD-JWT 解析、編碼、簽章驗證、雜湊與資料轉換等通用功能。
/// 適用於處理可驗證憑證（VC）與可驗證展示（VP）的資料處理與加密驗證流程。
class Utils {
  /// 是否啟用日誌輸出
  bool logEnabled = true;

  /// 解碼 JWT 並回傳 payload
  Map<String, dynamic> jwtDecode(String token) {
    final jwt = JWT.decode(token);
    return jwt.payload;
  }

  /// 解碼 SD-JWT
  ///
  /// SD-JWT（Selective Disclosure JWT）可對部分欄位進行隱私保護，只在必要時揭露。
  /// 此方法會解析 payload 並還原可揭露的欄位資料。
  Map<String, dynamic> sdJwtDecode(String token) {
    // 分割 JWT
    List<String> parts = token.split('.');
    if (parts.length != 3) {
      log('Not a valid JWT: $token');
      return {'code': 999, 'message': 'not a JWT'};
    }
    List<String> sdParts = parts[2].split('~');
    Map<String, dynamic> result = {};

    final vcPayload = jwtDecode(token);
    final credentialSubject = vcPayload['vc']['credentialSubject'];
    for (int i = 1; i < sdParts.length; i++) {
      String part = sdParts[i];

      if (part.isEmpty) {
        log('Skipping empty part');
        continue;
      }

      // 修正 Base64URL 格式並補齊 padding
      String base64Url = part.replaceAll('-', '+').replaceAll('_', '/');
      int padding = 4 - (base64Url.length % 4);
      if (padding < 4) {
        base64Url += '=' * padding;
      }

      try {
        // Base64 Decode
        String decoded = utf8.decode(base64.decode(base64Url));
        List<String> decodedList = List<String>.from(json.decode(decoded));

        if (decodedList.length == 3) {
          // 格式：[hash, 欄位名稱, 欄位值]
          result[decodedList[1]] = decodedList[2];
        } else if (decodedList.length == 2) {
          // 嘗試匹配 Credential Subject 欄位
          credentialSubject.keys.forEach((key) {
            if (key != '_sd_alg' && key != '_sd') {
              final bytes = utf8.encode(part);
              final digest = sha256.convert(bytes);
              String partEncoded = base64.encode(digest.bytes);
              String partfilter = partEncoded
                  .replaceAll('-', '+')
                  .replaceAll('_', '/')
                  .replaceAll('=', '');
              String credentialSubjectfilter = credentialSubject[key][0]['...']
                  .replaceAll('-', '+')
                  .replaceAll('_', '/')
                  .replaceAll('=', '');

              if (partfilter == credentialSubjectfilter) {
                result[key] = decodedList[1];
              }
            }
          });
        }
      } catch (e) {
        return {'code': 1, 'data': {}};
      }
    }
    return {'code': 0, 'data': result};
  }

  /// 編碼 SD-JWT，僅保留指定欄位
  Map<String, dynamic> sdJwtEncode(String token, List<dynamic> fields) {
    try {
      // 分割 JWT
      List<String> parts = token.split('.');
      if (parts.length != 3) {
        log('Not a valid JWT: $token');
        return {'code': 999, 'message': 'token invaild'};
      }

      List<String> sdParts = parts[2].split('~');
      String result = '${parts[0]}.${parts[1]}.${sdParts[0]}~';
      for (int i = 1; i < sdParts.length; i++) {
        String part = sdParts[i];

        if (part.isEmpty) {
          log('Skipping empty part');
          continue;
        }

        // 修正 Base64URL 格式並補齊 padding
        String base64Url = part.replaceAll('-', '+').replaceAll('_', '/');
        int padding = 4 - (base64Url.length % 4);
        if (padding < 4) {
          base64Url += '=' * padding;
        }

        // Base64 Decode
        String decoded = utf8.decode(base64.decode(base64Url));

        // 僅保留包含指定欄位的部分
        for (var field in fields) {
          if (decoded.contains(field)) {
            result += part + '~';
            continue;
          }
        }
      }
      return {'code': 0, 'message': 'SUCCESS', 'data': result};
    } catch (e) {
      return {'code': 999, 'message': 'SDK encode ERROR'};
    }
  }

  /// 分割 JWT 並返回三部分（header, payload, signature）
  List<String> splitJwt(String token) {
    return token.split('.');
  }

  /// 驗證 ECDSA 簽章是否正確
  ///
  /// [publicKey] - 發行者的公鑰
  /// [signedData] - 被簽章的資料
  /// [signature] - 簽章結果
  /// 回傳 - `true` 表示驗證通過，`false` 表示失敗
  bool verifySignature(
      pc.ECPublicKey publicKey, Uint8List signedData, Uint8List signature) {
    final signer = pc.ECDSASigner(
        pc.SHA256Digest(), pc.HMac(pc.SHA256Digest(), 64)); // 使用 SHA256 驗證
    final pubKeyParams = pc.PublicKeyParameter<pc.ECPublicKey>(publicKey);
    signer.init(false, pubKeyParams);

    // 將簽名拆分為 r 和 s
    final rLength = signature.length ~/ 2;
    final r = bytesToBigInt(signature.sublist(0, rLength));
    final s = bytesToBigInt(signature.sublist(rLength));
    final sig = pc.ECSignature(r, s);

    return signer.verifySignature(signedData, sig);
  }

  /// 驗證 JWT 簽章（離線）
  ///
  /// [token] - JWT 字串
  /// [publicKey] - 發行者公鑰
  /// 回傳 - 驗證結果布林值
  bool verifyJwt(String token, pc.ECPublicKey publicKey) {
    try {
      final parts = splitJwt(token);
      final header = parts[0];
      final payload = parts[1];

      final signature = base64Url.decode(addBase64Padding(parts[2]));

      final signedData = utf8.encode('$header.$payload');
      final isValid = verifySignature(publicKey, signedData, signature);

      return isValid;
    } catch (e) {
      return false;
    }
  }

  /// 為 Base64 字串補齊 padding
  String addBase64Padding(String str) {
    final padLength = (4 - str.length % 4) % 4;
    return str + '=' * padLength;
  }

  /// 將位元組陣列轉為 BigInt（用於 ECDSA 簽章 r, s 轉換）
  BigInt bytesToBigInt(Uint8List bytes) {
    BigInt result = BigInt.zero;
    for (var byte in bytes) {
      result = (result << 8) | BigInt.from(byte);
    }
    return result;
  }

  /// 計算字串的 SHA256 雜湊值（Hex 字串格式）
  String sha256Hash(String input) {
    final bytes = utf8.encode(input);
    final digest = sha256.convert(bytes);
    return digest.toString();
  }

  /// 將位元組轉換成位元（bit）陣列
  List<int> bytesToBits(Uint8List byteArray) {
    List<int> bits = [];
    for (int byte in byteArray) {
      for (int i = 7; i >= 0; i--) {
        bits.add((byte >> i) & 1);
      }
    }
    return bits;
  }

  /// 生成標準化 JSON 回應格式
  ///
  /// [code] - 狀態碼
  /// [message] - 訊息
  /// [data] - 可選，附加資料
  String response({
    required String code,
    required String message,
    dynamic data,
  }) {
    final result = {
      'code': code,
      'message': message,
      if (data != null) 'data': data,
    };
    return jsonEncode(result);
  }

  /// 輸出日誌
  ///
  /// 僅在 [logEnabled] 為 `true` 時輸出
  void log(String message) {
    if (logEnabled) {
      print('[LOG] $message');
    }
  }
}
