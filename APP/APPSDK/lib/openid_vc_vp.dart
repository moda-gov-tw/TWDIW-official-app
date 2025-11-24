import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:did_sdk_module/did_key_gen.dart';
import 'package:archive/archive.dart';
import 'package:pointycastle/export.dart';
import 'package:uuid/uuid.dart';
import 'utils/utils.dart';
import 'http_service.dart';

/// OpenidVcVp 類別
/// 此類別用於封裝 OpenID VC/VP 的相關資料與邏輯。
class OpenidVcVp {
  static const kxPlatform =
      MethodChannel('com.example.flutter_method_channel_example/keyxentic');
  HttpService httpService = HttpService();
  Utils utils = Utils();
//VC
  /// applyVC 方法
  /// 此方法負責申請VC
  Future<String> applyVCKx(String didFile, String qrCode, String txCode) async {
    try {
      final encode103i = qrCode.split('credential_offer_uri=')[1];
      final decoded103i = Uri.decodeFull(encode103i);

      // dwissuer-oidvci-101i
      Map<String, dynamic> credentialObject =
          await httpService.getCredentialObject(decoded103i);
      if (credentialObject['error'] != null) {
        return utils.response(code: '2011', message: credentialObject['error']);
      }
      // dwissuer-oidvci-102i
      Map<String, dynamic> credentialMetadata =
          await httpService.getCredentialMetadata(
              credentialObject['applyVCUrl'],
              credentialObject['credential_configuration_ids'][0]);
      if (credentialMetadata['error'] != null) {
        return utils.response(
            code: '2012', message: credentialMetadata['error']);
      }
      // dwissuer-oidvci-103i
      Map<String, dynamic> credentialDefinition =
          credentialMetadata['credentialMetadata']
                  ['credential_configurations_supported']
              [credentialObject['credential_configuration_ids'][0]];

      // dwissuer-oidvci-104i
      Map<String, dynamic> accessToken = await httpService.getAccessToken(
          credentialObject['applyVCUrl'],
          credentialObject['pre-authorized_code'],
          credentialObject['credential_configuration_ids'][0],
          txCode);
      if (accessToken['error'] != null) {
        return utils.response(code: '2013', message: accessToken['error']);
      }

      Map<String, dynamic> didMap = jsonDecode(didFile);

      final header = {
        'alg': 'ES256',
        'typ': 'openid4vci-proof+jwt',
        'kid': didMap['id'],
      };

      final payload = {
        'iss': 'moda_dw', // issuer
        'aud': credentialObject['applyVCUrl'], // audience
        'iat': DateTime.now().millisecondsSinceEpoch ~/
            1000, // issued at (current time in seconds)
        'nonce': accessToken['c_nonce'] // unique nonce
      };

      String jwtToken = await kxPlatform.invokeMethod('sign',
          {'header': jsonEncode(header), 'payload': jsonEncode(payload)});

      // dwissuer-oidvci-105i
      Map<String, dynamic> credential = await httpService.getVC(
          credentialObject['applyVCUrl'],
          accessToken['access_token'],
          credentialObject['credential_configuration_ids'][0],
          jwtToken);
      if (credential['error'] != null) {
        return utils.response(code: '2014', message: credential['error']);
      }

      final data = {
        'credential': credential['credential'],
        'credentialDefinition': credentialDefinition,
        'credentialMetadata': credentialMetadata['credentialMetadata']
      };

      return utils.response(code: '0', message: 'SUCCESS', data: data);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// decodeVC 方法
  /// 此方法負責執行 解析VC 的核心邏輯。
  String decodeVC(String jwToken) {
    try {
      final vcPayload = utils.jwtDecode(jwToken);
      Map<String, dynamic> field = utils.sdJwtDecode(jwToken);
      vcPayload['vc']['credentialSubject']['field'] = field;

      return utils.response(code: '0', message: 'SUCCESS', data: vcPayload);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// verifyVC 方法
  /// 此方法負責執行 驗證VC
  Future<String> verifyVC(
      String jwToken, String didFile, String frontUrl) async {
    try {
      final vcPayload = utils.jwtDecode(jwToken);

      var trustFlg = false;
      var vcFlg = false;
      var expFlg = false;
      var holderFlg = false;
      var issFlg = false;
      var badgeFlg = false;

      //Issuer 是否在清單中
      Map<String, dynamic> issStatus =
          await httpService.getVCIssStatus(frontUrl, vcPayload['iss']);
      if (issStatus['error'] == null) {
        if (issStatus['data']['status'] == 1) {
          trustFlg = true;
        }
        if (issStatus['data']['org'].containsKey('x509_type')) {
          badgeFlg = true;
        }
      } else if (issStatus['error'] == 9999) {
        return utils.response(code: '2', message: 'API FAIL');
      }

      // VC是否有效
      if (vcPayload['vc']['credentialStatus'] is Map) {
        Map<String, dynamic> vcListToken = await httpService.getVCList(
            vcPayload['vc']['credentialStatus']['statusListCredential']);
        if (vcListToken['error'] == null) {
          vcFlg = await checkVCValid(
              vcPayload['vc']['credentialStatus'], vcListToken);
        } else if (vcListToken['error'] == 9999) {
          return utils.response(code: '2', message: 'API FAIL');
        }
      } else {
        for (var item in vcPayload['vc']['credentialStatus']) {
          Map<String, dynamic> vcListToken =
              await httpService.getVCList(item['statusListCredential']);
          if (vcListToken['error'] == null) {
            final valid = await checkVCValid(item, vcListToken);
            if (!valid) {
              vcFlg = false;
              break;
            } else {
              vcFlg = true;
            }
          } else if (vcListToken['error'] == 9999) {
            return utils.response(code: '2', message: 'API FAIL');
          }
        }
      }

      // 驗VC sub 是否與DID一致
      Map<String, dynamic> didMap = jsonDecode(didFile);
      if (vcPayload['sub'] == didMap['id']) {
        holderFlg = true;
      }

      // 驗VC是否過期
      int exp = vcPayload['exp'];
      int currentTime = DateTime.now().millisecondsSinceEpoch ~/ 1000;
      if (currentTime < exp) {
        expFlg = true;
      }

      // VC簽章是否通過
      List<String> parts = jwToken.split('.');
      List<String> sdParts = parts[2].split('~');
      String token = '${parts[0]}.${parts[1]}.${sdParts[0]}';
      if (issStatus['data'] != null) {
        final didPayload = utils.jwtDecode(issStatus['data']['did']);
        ECPublicKey publicKey = DIDKeyGenerator().convertJwkToECPublicKey(
            didPayload['verificationMethod'][0]['publicKeyJwk']);

        issFlg = utils.verifyJwt(token, publicKey);
      }

      var data = {};
      if (trustFlg & vcFlg & expFlg & holderFlg & issFlg) {
        data = {'trust_badge': badgeFlg};
        return utils.response(code: '0', message: 'SUCCESS', data: data);
      } else {
        data = {
          'trust': trustFlg,
          'vc': vcFlg,
          'issuer': issFlg,
          'exp': expFlg,
          'holder': holderFlg,
          'trust_badge': badgeFlg
        };
        return utils.response(code: '3', message: '驗證失敗', data: data);
      }
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// verifyVCOffline 方法
  /// 此方法負責執行 離線驗證
  Future<String> verifyVCOffline(
      String jwToken, String didFile, List issList, List vcList) async {
    try {
      final vcPayload = utils.jwtDecode(jwToken);

      bool? trustFlg = false;
      bool? vcFlg = false;
      bool? issFlg = false;
      var expFlg = false;
      var holderFlg = false;
      var badgeFlg = false;

      var issuer = {};
      //Issuer 是否在清單中
      if (issList.isEmpty) {
        trustFlg = null;
      } else {
        for (var iss in issList) {
          final issPayload = utils.jwtDecode(iss['did']);
          if (vcPayload['iss'] == issPayload['id']) {
            issuer = iss;
            if (iss['status'] == 1) {
              trustFlg = true;
            }
            if (iss['org'].containsKey('x509_type')) {
              badgeFlg = true;
            }
          }
        }
      }

      // VC是否有效, 轉成sha256比對
      final vcTokenHash = utils.sha256Hash(jwToken);
      if (vcList.isEmpty) {
        vcFlg = null;
      } else {
        for (var vcListToken in vcList) {
          if (vcListToken.containsKey(vcTokenHash)) {
            // for (var list in vcListToken[vcTokenHash]) {
            for (int i = 0; i < vcListToken[vcTokenHash].length; i++) {
              final valid = await checkVCValid(
                  vcPayload['vc']['credentialStatus'][i],
                  vcListToken[vcTokenHash][i]);
              if (!valid) {
                vcFlg = false;
                break;
              } else {
                vcFlg = true;
              }
            }
          }
        }
      }

      // 驗VC sub 是否與DID一致
      Map<String, dynamic> didMap = jsonDecode(didFile);
      if (vcPayload['sub'] == didMap['id']) {
        holderFlg = true;
      }

      // 驗VC是否過期
      int exp = vcPayload['exp'];
      int currentTime = DateTime.now().millisecondsSinceEpoch ~/ 1000;
      if (currentTime < exp) {
        expFlg = true;
      }

      // VC簽章是否通過
      List<String> parts = jwToken.split('.');
      List<String> sdParts = parts[2].split('~');
      String token = '${parts[0]}.${parts[1]}.${sdParts[0]}';
      if (issList.isEmpty) {
        issFlg = null;
      } else {
        if (issuer['did'] != null) {
          final didPayload = utils.jwtDecode(issuer['did']);
          ECPublicKey publicKey = DIDKeyGenerator().convertJwkToECPublicKey(
              didPayload['verificationMethod'][0]['publicKeyJwk']);

          issFlg = utils.verifyJwt(token, publicKey);
        }
      }

      var data = {};
      if (trustFlg == true &&
          vcFlg == true &&
          issFlg == true &&
          expFlg &&
          holderFlg) {
        data = {'trust_badge': badgeFlg};
        return utils.response(code: '0', message: 'SUCCESS', data: data);
      } else {
        data = {
          'trust': trustFlg,
          'vc': vcFlg,
          'issuer': issFlg,
          'exp': expFlg,
          'holder': holderFlg,
          'trust_badge': badgeFlg
        };
        return utils.response(code: '3', message: '驗證失敗', data: data);
      }
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// downloadIssList 方法
  /// 此方法負責執行 下載信任清冊已用於離線驗證VC
  Future<String> downloadIssList(String url) async {
    try {
      Map<String, dynamic> issList = await httpService.getVCIssList(url, 20, 0);

      if (issList['error'] != null) {
        return utils.response(code: '2', message: issList['error']);
      }
      if (issList['count'] > 20) {
        for (int i = 0; i < issList['count'] ~/ 20; i++) {
          Map<String, dynamic> moreList =
              await httpService.getVCIssList(url, 20, i + 1);
          issList['dids'] = issList['dids'] + moreList['dids'];
        }
      }
      return utils.response(
          code: '0', message: 'SUCCESS', data: issList['dids']);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// downloadAllVCList 方法
  /// 此方法負責執行 下VC狀態清冊已用於離線驗證VC
  Future<String> downloadAllVCList(List vcs, List vcList) async {
    try {
      List resultList = [];
      final vcMap = _vcListMap(vcList);

      for (var vcToken in vcs) {
        Map response = {};
        List allList = [];

        final vcPayload = utils.jwtDecode(vcToken);
        final vcTokenHash = utils.sha256Hash(vcToken);

        if (vcPayload['vc']['credentialStatus'] is Map) {
          Map<String, dynamic> vcListToken = await httpService.getVCList(
            vcPayload['vc']['credentialStatus']['statusListCredential'],
          );

          if (vcMap.containsKey(vcTokenHash) && vcListToken['error'] != null) {
            allList.add(vcMap[vcTokenHash]![0]); // Map 裡的第一個元素
          } else {
            allList.add(vcListToken);
          }
        } else {
          int index = 0;
          for (var list in vcPayload['vc']['credentialStatus']) {
            Map<String, dynamic> vcListToken = await httpService.getVCList(
              list['statusListCredential'],
            );

            if (vcMap.containsKey(vcTokenHash) &&
                vcListToken['error'] != null &&
                index < vcMap[vcTokenHash]!.length) {
              allList.add(vcMap[vcTokenHash]![index]);
            } else {
              allList.add(vcListToken);
            }
            index++;
          }
        }

        response[vcTokenHash] = allList;
        resultList.add(response);
      }
      return utils.response(code: '0', message: 'SUCCESS', data: resultList);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

//VP
  /// parseVPQrcode 方法
  /// 此方法負責執行 解析申請VP Qrcode
  Future<String> parseVPQrcode(String qrCode, String frontUrl) async {
    try {
      final verifier102i = qrCode.split('request_uri=')[1].split('&')[0];
      final decoded102i = Uri.decodeFull(verifier102i);

      Map<String, dynamic> objectToken =
          await httpService.getVPObject(decoded102i);
      if (objectToken['error'] != null) {
        return utils.response(code: '4011', message: objectToken['error']);
      }
      final objectPayload = utils.jwtDecode(objectToken['token']);

      var data = {};

      final requestData = parsePresentationDefinition(objectPayload);

      //Issuer 是否在清單中
      if (objectPayload['client_id'].contains('did:')) {
        Map<String, dynamic> issStatus = await httpService.getVCIssStatus(
            frontUrl, objectPayload['client_id']);
        if (issStatus['error'] != null) {
          data = {
            'request_token': objectToken['token'],
            'request_data': requestData
          };
          return utils.response(
              code: '4012', message: 'Verifier DID Fail', data: data);
        }
      }

      data = {
        'request_token': objectToken['token'],
        'request_data': requestData
      };

      return utils.response(code: '0', message: 'SUCCESS', data: data);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// generateVPKx 方法
  /// 此方法負責執行 申請VP
  Future<String> generateVPKx(String didFile, String requestToken,
      List<dynamic> vcs, String customData) async {
    try {
      Map<String, dynamic> didMap = jsonDecode(didFile);
      final objectPayload = utils.jwtDecode(requestToken);

      List vcsResult = [];
      List descriptorMap = [];

      //根據field產生VC之sdJWT, Presentation Submission之descriptor_map
      vcs.asMap().forEach((index, vc) {
        Map<String, dynamic> result = {};
        result = utils.sdJwtEncode(vc['vc'], vc['field']);
        vcsResult.add(result['data']);
        //建立descriptor_map
        final cardId = (vc['card_id'] ?? '').toString();
        final id = cardId.isNotEmpty
            ? cardId
            : objectPayload['presentation_definition']['input_descriptors']
                [index]['id'];
        descriptorMap.add({
          'id': id,
          'format': 'jwt_vp',
          'path': '\$',
          'path_nested': {
            'id': id,
            'format': 'jwt_vc',
            'path': '\$.vp.verifiableCredential[$index]'
          }
        });
      });

      final header = {
        'typ': 'JWT',
        'alg': 'ES256',
        "jwk": didMap['verificationMethod'][0]['publicKeyJwk']
      };

      const uuid = Uuid();
      final payload = {
        'sub': didMap['id'], // subject
        'aud': objectPayload['client_id'], // audience
        'iss': didMap['id'], // issuer
        'nbf': DateTime.now().millisecondsSinceEpoch ~/
            1000, // issued at (current time in seconds)
        'vp': {
          'context': ['https://www.w3.org/2018/credentials/v1'],
          'type': ['VerifiablePresentation'],
          'verifiableCredential': vcsResult
        },
        'exp': DateTime.now().add(Duration(days: 30)).millisecondsSinceEpoch ~/
            1000,
        'nonce': objectPayload['nonce'], // unique nonce
        'jti':
            'https://digitalwallet.moda:8443/vp/api/presentation/${uuid.v4()}'
      };

      String jwtToken = await kxPlatform.invokeMethod('sign',
          {'header': jsonEncode(header), 'payload': jsonEncode(payload)});

      //建立Presentation Submission
      final presetationSubmission = {
        //隨機uuid
        'id': uuid.v4(),
        'definition_id': objectPayload['presentation_definition']['id'],
        'descriptor_map': descriptorMap
      };

      String customJwt = "";
      if (customData != "") {
        Map<String, dynamic> customPayload = jsonDecode(customData);
        customJwt = await kxPlatform.invokeMethod('sign', {
          'header': jsonEncode(header),
          'payload': jsonEncode(customPayload)
        });
      }

      Map<String, dynamic> response = await httpService.sendVP(
          objectPayload['response_uri'],
          objectPayload['state'],
          jwtToken,
          jsonEncode(presetationSubmission),
          customJwt);
      if (response['error'] != null) {
        return utils.response(
            code: '4021', message: response['error'], data: jwtToken);
      }

      return utils.response(code: '0', message: 'SUCCESS');
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// generateVPNFC 方法
  /// 此方法負責執行 建立用於NFC傳輸的VP
  Future<String> generateVPNFC(String didFile, String vc) async {
    try {
      Map<String, dynamic> didMap = jsonDecode(didFile);

      final header = {
        'typ': 'JWT',
        'alg': 'ES256',
        "jwk": didMap['verificationMethod'][0]['publicKeyJwk']
      };

      const uuid = Uuid();
      final payload = {
        'sub': didMap['id'],
        'aud': didMap['id'],
        'iss': didMap['id'],
        'nbf': DateTime.now().millisecondsSinceEpoch ~/ 1000,
        'vp': {
          'context': ['https://www.w3.org/2018/credentials/v1'],
          'type': ['VerifiablePresentation'],
          'verifiableCredential': [vc]
        },
        'exp': DateTime.now().add(Duration(days: 30)).millisecondsSinceEpoch ~/
            1000,
        'nonce': uuid.v4(),
        'jti':
            'https://digitalwallet.moda:8443/vp/api/presentation/${uuid.v4()}'
      };

      String jwtToken = await kxPlatform.invokeMethod('sign',
          {'header': jsonEncode(header), 'payload': jsonEncode(payload)});

      return utils.response(code: '0', message: jwtToken);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// verifyVPNFC 方法
  /// 此方法負責執行 驗證NFC傳送來的VP
  Future<String> verifyVPNFC(String vp) async {
    try {
      var vpFlg = false;
      final vpPayload = utils.jwtDecode(vp);
      final vcToken = vpPayload['vp']['verifiableCredential'][0];
      final vcPayload = utils.jwtDecode(vcToken);

      print('xxxxxx');
      print(vcPayload);
      return utils.response(code: '0', message: 'SUCCESS');
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// checkVCValid 方法
  /// 此方法負責執行 確認VC是否有效
  Future<bool> checkVCValid(Map vcStatus, Map vcListToken) async {
    final vcListPayload = utils.jwtDecode(vcListToken['statusList']);

    Uint8List decodedBytes =
        base64.decode(vcListPayload['vc']['credentialSubject']['encodedList']);
    // GZIP unzip
    Uint8List decompressedBytes =
        Uint8List.fromList(GZipDecoder().decodeBytes(decodedBytes));

    // transfer bytes to bits
    List<int> bitArray = utils.bytesToBits(decompressedBytes);
    if (bitArray[int.parse(vcStatus['statusListIndex'])] == 0) {
      return true;
    } else {
      return false;
    }
  }

  /// parsePresentationDefinition 方法
  /// 此方法負責執行 解析presentation definition
  List parsePresentationDefinition(Map<String, dynamic> objectPayload) {
    final definition = objectPayload['presentation_definition'];
    var result = [];

    if (definition.containsKey('submission_requirements')) {
      for (var req in definition['submission_requirements']) {
        var request = {
          'name': req['name'],
          'group': req['from'],
          'rule': req['rule'],
          'count': req.containsKey('count') ? req['count'] : null,
          'max': req.containsKey('max') ? req['max'] : null,
          'cards': []
        };
        for (var vc in definition['input_descriptors']) {
          if (vc['group'].contains(req['from'])) {
            var card = {
              'card': vc['constraints']['fields'][0]['filter']['contains']
                  ['const'],
              'card_id': vc['id'],
              'name': vc['name'],
              'fields': []
            };
            for (int i = 1; i < vc['constraints']['fields'].length; i++) {
              final path = vc['constraints']['fields'][i]['path'][0];
              final parts = path.split('credentialSubject.');
              if (parts.length > 1) {
                card['fields'].add(parts[1]);
              }
            }
            request['cards'].add(card);
          }
        }
        result.add(request);
      }
    } else {
      var request = {'cards': []};
      for (var vc in definition['input_descriptors']) {
        var card = {
          'card': vc['constraints']['fields'][0]['filter']['contains']['const'],
          'name': vc['name'],
          'fields': []
        };
        for (int i = 1; i < vc['constraints']['fields'].length; i++) {
          final path = vc['constraints']['fields'][i]['path'][0];
          final parts = path.split('credentialSubject.');
          if (parts.length > 1) {
            card['fields'].add(parts[1]);
          }
        }
        request['cards']?.add(card);
      }
      result.add(request);
    }
    return result;
  }

  /// transferVC 方法
  /// 此方法負責執行 傳送VC
  Future<String> transferVC(String didFile, String vc) async {
    try {
      Map<String, dynamic> didMap = jsonDecode(didFile);
      final objectPayload = utils.jwtDecode(vc);

      final header = {
        'typ': 'JWT',
        'alg': 'ES256',
        "jwk": didMap['verificationMethod'][0]['publicKeyJwk']
      };

      const uuid = Uuid();
      final payload = {
        'sub': didMap['id'],
        'aud': objectPayload['sub'],
        'iss': didMap['id'],
        'nbf': DateTime.now().millisecondsSinceEpoch ~/ 1000,
        'vp': {
          'context': ['https://www.w3.org/2018/credentials/v1'],
          'type': ['VerifiablePresentation'],
          'verifiableCredential': [vc]
        },
        'exp': DateTime.now().add(Duration(days: 30)).millisecondsSinceEpoch ~/
            1000,
        'nonce': objectPayload['nonce'],
        'jti':
            'https://digitalwallet.moda:8443/vp/api/presentation/${uuid.v4()}'
      };

      String jwtToken = await kxPlatform.invokeMethod('sign',
          {'header': jsonEncode(header), 'payload': jsonEncode(payload)});

      String url = objectPayload['jti'];
      Map<String, dynamic> response =
          await httpService.transferVC(url, jwtToken);
      if (response['error'] != null) {
        return utils.response(
            code: '7011', message: response['error'], data: jwtToken);
      }

      return utils.response(code: '0', message: 'SUCCESS', data: response);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// sendRequest 方法
  /// 此方法負責執行 API
  Future<String> sendRequest(String url, String type, String body) async {
    try {
      Map<String, dynamic> bodyMap = jsonDecode(body);
      Map<String, dynamic> response;
      response = await httpService.sendRequest(url, type, bodyMap);
      if (response['error'] != null) {
        return utils.response(code: '8011', message: response['error']);
      }

      return utils.response(code: '0', message: 'SUCCESS', data: response);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  /// sendJWTRequest 方法
  /// 此方法負責執行 帶JWT的API
  Future<String> sendJWTRequest(String url, String body, String didFile) async {
    try {
      Map<String, dynamic> didMap = jsonDecode(didFile);
      Map<String, dynamic> payload = jsonDecode(body);
      final header = {
        'typ': 'JWT', // token 類型
        'alg': 'ES256', // 演算法
        "kid": didMap['id']
      };

      String jwtToken = await kxPlatform.invokeMethod('sign',
          {'header': jsonEncode(header), 'payload': jsonEncode(payload)});

      Map<String, dynamic> response =
          await httpService.sendRequest(url, 'POST', {'jwt': jwtToken});

      if (response['error'] != null) {
        return utils.response(code: '8012', message: response['error']);
      }

      return utils.response(
          code: '0', message: 'SUCCESS', data: response['response']);
    } catch (e) {
      return utils.response(code: '1', message: e.toString());
    }
  }

  // 假設 vcList 格式為 [{vcTokenHash: [vcListToken, ...]}, ...]
  Map<String, List<dynamic>> _vcListMap(List vcList) {
    final map = <String, List<dynamic>>{};
    for (var item in vcList) {
      if (item is Map<String, dynamic>) {
        item.forEach((key, value) {
          if (value is List) {
            map[key] = value;
          }
        });
      }
    }
    return map;
  }
}
