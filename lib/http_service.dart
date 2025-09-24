import 'dart:convert';
import 'package:http/http.dart' as http;

class HttpService {
  final delayTime = const Duration(seconds: 10);
//申請VC
  Future<Map<String, dynamic>> getCredentialObject(String url) async {
    final uri = Uri.parse(url);
    try {
      final response = await http.get(uri).timeout(delayTime);

      final data = json.decode(response.body);
      if (response.statusCode == 200) {
        final preAuthorizedCode = data['grants']
                ['urn:ietf:params:oauth:grant-type:pre-authorized_code']
            ['pre-authorized_code'];
        final credentialConfigIds = data['credential_configuration_ids'];
        final applyVCUrl = data['credential_issuer'];

        return {
          'pre-authorized_code': preAuthorizedCode,
          'credential_configuration_ids': credentialConfigIds,
          'applyVCUrl': applyVCUrl
        };
      } else {
        if (data['resp_code'] != null) {
          return {'error': data['resp_code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

  Future<Map<String, dynamic>> getCredentialMetadata(
      String url, String vcType) async {
    Uri uri = Uri.parse('$url.well-known/openid-credential-issuer');
    try {
      final response = await http.get(uri).timeout(delayTime);

      final data = json.decode(utf8.decode(response.bodyBytes));
      if (response.statusCode == 200) {
        return {'credentialMetadata': data};
      } else {
        if (data['resp_code'] != null) {
          return {'error': data['resp_code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

  Future<Map<String, dynamic>> getAccessToken(String url, String preAuthCode,
      String credentialId, String txCode) async {
    Uri uri = Uri.parse('${url}token');
    try {
      final response = await http.post(
        uri,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: {
          'grant_type': 'urn:ietf:params:oauth:grant-type:pre-authorized_code',
          'client_id': 'moda_dw',
          'pre-authorized_code': preAuthCode,
          'authorization_details':
              '[{"type": "openid_credential", "credential_configuration_id": "$credentialId"}]',
          'tx_code': txCode
        },
      ).timeout(delayTime);

      final data = json.decode(response.body);
      if (response.statusCode == 200) {
        final accessToken = data['access_token'];
        final c_nonce = data['c_nonce'];

        return {'access_token': accessToken, 'c_nonce': c_nonce};
      } else {
        if (data['resp_code'] != null) {
          return {'error': data['resp_code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

  Future<Map<String, dynamic>> getVC(String url, String accessToken,
      String credentialId, String jwtToken) async {
    Uri uri = Uri.parse('${url}credential');

    try {
      final Map<String, dynamic> requestData = {
        "credential_identifier": credentialId,
        "proofs": {
          "jwt": [jwtToken]
        }
      };

      final response = await http
          .post(
            uri,
            headers: {
              'Authorization': 'Bearer $accessToken',
              'Content-Type': 'application/json',
            },
            body: jsonEncode(requestData),
          )
          .timeout(delayTime);
      final data = json.decode(response.body);
      if (response.statusCode == 200) {
        final credential = data['credential'];
        return {'credential': credential};
      } else {
        if (data['resp_code'] != null) {
          return {'error': data['resp_code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

//驗證VC
  Future<Map<String, dynamic>> getVCIssStatus(String url, String issDID) async {
    final uri = Uri.parse('$url/api/did/$issDID');
    try {
      final response = await http.get(uri).timeout(delayTime);

      if (response.statusCode == 200) {
        final data = json.decode(utf8.decode(response.bodyBytes));
        if (data['code'] == '0') {
          final status = data['data'];

          return {'data': status};
        } else {
          return {'error': data['code']};
        }
      } else {
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

  Future<Map<String, dynamic>> getVCList(String url) async {
    final uri = Uri.parse(url);
    try {
      final response = await http.get(uri).timeout(delayTime);
      final data = json.decode(response.body);
      if (response.statusCode == 200) {
        final statusList = data['statusList'];
        return {'statusList': statusList};
      } else {
        if (data['code'] != null) {
          return {'error': data['code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

  //下載Iss 信任清單
  Future<Map<String, dynamic>> getVCIssList(String url, size, page) async {
    final uri =
        Uri.parse('$url/api/did?size=$size&page=$page&orgType=1&status=1');
    try {
      final response = await http.get(uri).timeout(delayTime);
      if (response.statusCode == 200) {
        final data = json.decode(utf8.decode(response.bodyBytes));
        if (data['code'] == '0') {
          final dids = data['data']['dids'];
          return {'dids': dids, 'count': data['data']['count']};
        } else {
          return {'error': data['code']};
        }
      } else {
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

//申請VP
  Future<Map<String, dynamic>> getVPObject(String url) async {
    final uri = Uri.parse(url);
    try {
      final response = await http.get(uri).timeout(delayTime);

      if (response.statusCode == 200) {
        final data = response.body;
        return {'token': data};
      } else {
        final data = json.decode(response.body);
        if (data['code'] != null) {
          return {'error': data['code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

  Future<Map<String, dynamic>> sendVP(String url, String state, String vpToken,
      String presentationSubmission, String customJwt) async {
    Uri uri = Uri.parse(url);
    try {
      final response = await http.post(
        uri,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: {
          'state': state,
          'vp_token': vpToken,
          'presentation_submission': presentationSubmission,
          if (customJwt != "") 'custom_data': customJwt,
        },
      ).timeout(delayTime);

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 0) {
          return data;
        } else {
          return {'error': data['code']};
        }
      } else {
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

//VC轉移
  Future<Map<String, dynamic>> transferVC(String url, String vp) async {
    final uri = Uri.parse('$url/transfer');
    try {
      final Map<String, dynamic> requestData = {
        "vp": vp,
      };

      final response = await http
          .post(
            uri,
            headers: {
              'Content-Type': 'application/json',
            },
            body: jsonEncode(requestData),
          )
          .timeout(delayTime);

      final data = json.decode(response.body);
      if (response.statusCode == 200) {
        return {'qr_code': data['qr_code'], 'link': data['link']};
      } else {
        if (data['code'] != null) {
          return {'error': data['code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }

  Future<Map<String, dynamic>> sendRequest(
      String url, String type, Map body) async {
    final uri = Uri.parse(url);
    try {
      final http.Response response;
      if (type == 'GET') {
        response = await http.get(uri).timeout(delayTime);
      } else {
        response = await http
            .post(
              uri,
              headers: {
                'Content-Type': 'application/json',
              },
              body: jsonEncode(body),
            )
            .timeout(delayTime);
      }
      if (response.statusCode == 200) {
        final data = utf8.decode(response.bodyBytes);
        return {'response': data};
      } else {
        final data = json.decode(utf8.decode(response.bodyBytes));
        if (data['code'] != null) {
          return {'error': data['code']};
        }
        return {'error': response.statusCode.toString()};
      }
    } catch (e) {
      return {'error': 9999};
    }
  }
}
