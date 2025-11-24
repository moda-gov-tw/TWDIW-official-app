package gov.moda.dw.issuer.oidvci.web.rest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.interfaces.ECPublicKey;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import gov.moda.dw.issuer.oidvci.domain.*;
import gov.moda.dw.issuer.oidvci.repository.CICRepository;
import gov.moda.dw.issuer.oidvci.service.*;
import org.bitcoinj.base.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;

import gov.moda.dw.issuer.oidvci.config.ErrorCodeConfiguration;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import gov.moda.dw.issuer.oidvci.common.*;

import gov.moda.dw.issuer.oidvci.config.Constants;

@RestController
@RequestMapping("/api/issuer/{id}")
public class CredentialIssuer
{

    @Autowired
    private RestTemplate restTemplate;


    private static final Logger log = LoggerFactory.getLogger(CredentialIssuer.class);

    private final CredentialIssuerConfigService credential_issuer_config_service;

    private final PreAuthCodeService pre_auth_code_service;

    private final AccessTokenService access_token_service;
    private final CredentialPolicyService credential_policy_service;

    private final HTTPService http_service;

    private final OidvciSettingService oidvci_setting_service;

    private final CredentialService credential_service;

    private final int qrcode_side_length = 241;

    private final String CODE_VALID = "VALID";

    private final String CODE_EXPIRED = "EXPIRED";

    private final String CODE_USED = "USED";

    private final String CODE_CHANGE = "CHANGE";

    private final int TOKEN_TTL = 600;


    CICRepository cicRepository;

    public CredentialIssuer(CredentialIssuerConfigService credential_issuer_config_service,PreAuthCodeService pre_auth_code_service,
                            AccessTokenService access_token_service,CredentialPolicyService credential_policy_service,HTTPService http_service,OidvciSettingService oidvci_setting_service,CredentialService credential_service)
    {
        this.credential_issuer_config_service = credential_issuer_config_service;
        this.pre_auth_code_service = pre_auth_code_service;
        this.access_token_service = access_token_service;
        this.credential_policy_service = credential_policy_service;
        this.http_service = http_service;
        this.oidvci_setting_service = oidvci_setting_service;
        this.credential_service = credential_service;
    }

    private JSONObject makeReturnObj(String api, int error_code)
    {

        JSONObject rtn_obj = new JSONObject();
        rtn_obj.put("resp_code", error_code);
        rtn_obj.put("resp_message", ErrorCodeConfiguration.getErrorMessage(error_code));
        log.info("[{}]: {}",api, rtn_obj.toString());

        return rtn_obj;
    }

    private JSONObject makeReturnObj(String api, int error_code, String extra_err_msg)
    {

        JSONObject rtn_obj = new JSONObject();
        rtn_obj.put("resp_code", error_code);
        rtn_obj.put("resp_message", ErrorCodeConfiguration.getErrorMessage(error_code) + extra_err_msg);
        log.info("[{}]: {}",api, rtn_obj.toString());

        return rtn_obj;
    }

    private JSONArray getCredentialConfigurationIds(String credential_offer) throws ParseException
    {
        JSONObject credential_offer_json = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(credential_offer);
        return (JSONArray)credential_offer_json.get("credential_configuration_ids");
    }

    @GetMapping(path = "version", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getVersion() {
        return "{\"version\":\"" + Constants.version + "\"}";
    }

    @PostMapping(path = "/qr-code", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> GetQRCode(@RequestBody Map<String, Object> request_map, @PathVariable String id)
    {

        String api = "qr-code";
        int error_code = -1;
        String extra_error_msg = "";
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        date_format.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        CryptoHelper crypto_util = new CryptoHelper();
        JSONObject req_data = null;

        // input parameters
        boolean authenticated = false;
        String id_token = null;
        String[] cid = null;

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            String json_string = objectMapper.writeValueAsString(request_map);
            req_data = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(json_string);

            log.info("[{}]: 1. input params: ", api);
            log.info("[{}]: req_data: {}", api, req_data.toJSONString(JSONStyle.LT_COMPRESS));

            // check input parameters
            try
            {
                if(req_data.containsKey("authenticated"))
                    authenticated = (boolean)req_data.get("authenticated");

                else
                {
                    log.error("[{}]: {}",api, "missing parameter : authenticated");
                    error_code = 11001;
                    extra_error_msg = "missing parameter : authenticated";
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code, extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                }

                if(req_data.containsKey("id_token"))
                    id_token = req_data.getAsString("id_token");

                else
                {
                    log.error("[{}]: {}",api, "missing parameter : id_token");
                    error_code = 13000;
                    extra_error_msg = "missing parameter : id_token";
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code, extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                }
            }

            catch(ClassCastException e1)
            {
                log.error(e1.getMessage());
                error_code = 11001;
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }

            if(authenticated)
            {
                if(id_token == null)	// 沒收到id_token，回覆錯誤
                {
                    error_code = 11001;
                    log.error("[{}]: {}",api, "id_token is null...");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                }

                else	// 收到id_token，解析並產製pre-auth code
                {
                    final String[] split_IDT = id_token.split("\\.");
                    if(split_IDT.length != 3)
                    {
                        error_code = 11003;
                        log.error("[{}]: {}",api, "The format of the id_token is incorrect");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    final String IDT_header = split_IDT[0];
                    final String IDT_payload = split_IDT[1];
                    final String IDT_sig = split_IDT[2];

                    long exp = 0L;
                    String uid = null;
                    String client_id = null;
                    String nonce = null;
                    String credential_configuration_id = null;
                    String tx_code = null;
                    JSONObject IDT_payload_obj = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(Base64.getDecoder().decode(IDT_payload));

                    try
                    {
                        if(IDT_payload_obj.containsKey("sub"))
                            uid = IDT_payload_obj.getAsString("sub");

                        if(IDT_payload_obj.containsKey("aud"))
                            client_id = IDT_payload_obj.getAsString("aud");

                        if(IDT_payload_obj.containsKey("exp"))
                            exp = Integer.toUnsignedLong((int)IDT_payload_obj.get("exp")) * 1000;

                        if(IDT_payload_obj.containsKey("nonce"))
                            nonce = IDT_payload_obj.getAsString("nonce");

                        if(IDT_payload_obj.containsKey("credential_configuration_id"))
                            credential_configuration_id = IDT_payload_obj.getAsString("credential_configuration_id");
                    }

                    catch(ClassCastException e1)
                    {
                        error_code = 11003;
                        log.error("[{}]: {}",api, e1.getMessage());
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    if(uid == null || uid.isEmpty())
                    {
                        error_code = 11003;
                        extra_error_msg = "missing parameter : sub";
                        log.error("[{}]: {}",api, "missing parameter : sub");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code, extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    if(client_id == null || client_id.isEmpty())
                    {
                        error_code = 11003;
                        extra_error_msg = "missing parameter : aud";
                        log.error("[{}]: {}",api, "missing parameter : aud");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code, extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    if(exp == 0L)
                    {
                        error_code = 11003;
                        extra_error_msg = "missing parameter : exp";
                        log.error("[{}]: {}",api, "missing parameter : exp");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code, extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    if(nonce == null || nonce.isEmpty())
                    {
                        error_code = 11003;
                        extra_error_msg = "missing parameter : nonce";
                        log.error("[{}]: {}",api, "missing parameter : nonce");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code, extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    if(credential_configuration_id == null || credential_configuration_id.isEmpty())
                    {
                        error_code = 11003;
                        extra_error_msg = "missing parameter : credential_configuration_id";
                        log.error("[{}]: {}",api, "missing parameter : credential_configuration_id");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code, extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    //找尋DB是否存在Credential Type
                    List<CredentialPolicyEntity> credential_policy_entity = credential_policy_service.getCredentialPolicyByType(credential_configuration_id);
                    if (credential_policy_entity == null || credential_policy_entity.isEmpty())
                    {
                        error_code = 11021;
                        String errorMessage = "credential_policy_entity is empty or null";
                        log.warn("[{}]: {}",api, "credential_policy_entity is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String switch_str_Qrcode = credential_policy_entity.get(0).getFuncSwitch();
                    log.info("switch_str_Qrcode " + switch_str_Qrcode);

                    boolean OTP_Switch_QRcode = false; // 預設開關關閉

                    // 解析 func_switch 的 JSON 內容
                    if (switch_str_Qrcode != null && !switch_str_Qrcode.isEmpty())
                    {
                        try
                        {
                            JsonObject switchObj = JsonParser.parseString(switch_str_Qrcode).getAsJsonObject();
                            if (switchObj.has("enable_tx_code"))
                            {
                                OTP_Switch_QRcode = switchObj.get("enable_tx_code").getAsBoolean();
                                log.info("OTP_Switch_QRcode " + OTP_Switch_QRcode);
                            }

                            else
                            {
                                // enable_tx_code 不存在，設為 false
                                OTP_Switch_QRcode = false;
                            }
                        }

                        catch (Exception e)
                        {
                            int errorCode = 11033;
                            String errorMsg = "Invalid func_switch JSON format: " + switch_str_Qrcode;
                            log.error("[{}]: {}", api, errorMsg, e);
                            return ResponseEntity.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(makeReturnObj(api, errorCode, errorMsg).toJSONString(JSONStyle.LT_COMPRESS));
                        }
                    }

                    // 根據開關狀態處理 tx_code
                    if (OTP_Switch_QRcode)
                    {
                        // 開關開啟，tx_code 必須存在且不為空
                        if (!IDT_payload_obj.containsKey("tx_code") ||
                            (tx_code = IDT_payload_obj.getAsString("tx_code")) == null ||
                            tx_code.isEmpty())
                        {
                            int errorCode = 11031;
                            String errorMsg = "missing parameter : tx_code";
                            log.error("[{}]: {}", api, errorMsg);
                            return ResponseEntity.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(makeReturnObj(api, errorCode, errorMsg).toJSONString(JSONStyle.LT_COMPRESS));
                        }
                    }

                    else
                    {
                        // 開關關閉，tx_code 可以為 null 或空
                        if (IDT_payload_obj.containsKey("tx_code"))
                        {
                            tx_code = IDT_payload_obj.getAsString("tx_code");
                        }
                    }

                    // check IDT 是否過期
                    long req_time = new Date().getTime();
                    log.info("[{}]: req_time: {}", api, date_format.format(new Date(req_time)));
                    long timeShift = (req_time - exp);
//					if(timeShift > 0)// 逾期，無法使用
//		    		{
//						error_code = 11004;
//						return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
//		    		}

                    // Get DB config
                    List<CredentialIssuerConfigEntity> credential_issuer_config_entity = credential_issuer_config_service.getCredentialIssuerConfig(id);

                    if (credential_issuer_config_entity == null || credential_issuer_config_entity.isEmpty())
                    {
                        error_code = 11901;
                        String errorMessage = "Query not found: No data found matching the criteria.";
                        log.warn("[{}]: {}",api, "Query not found: No data found matching the criteria.");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String credential_offer_str = credential_issuer_config_entity.get(0).getCredentialOffer();
                    if(credential_offer_str == null || credential_offer_str.isEmpty())
                    {
                        error_code = 12000;
                        String errorMessage = "credential_offer is empty or null";
                        log.error("[{}]: {}",api, "credential_offer is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String credential_issuer_identifier = credential_issuer_config_entity.get(0).getCredentialIssuerIdentifier();
                    if(credential_issuer_identifier==null || credential_issuer_identifier.isEmpty())
                    {
                        error_code = 12001;
                        String errorMessage = "credential_issuer_identifier is empty or null";
                        log.error("[{}]: {}",api, "credential_issuer_identifier is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String credential_offer_uri_path = credential_issuer_config_entity.get(0).getCredentialOfferUriPath();
                    if(credential_offer_uri_path==null || credential_offer_uri_path.isEmpty())
                    {
                        error_code = 12002;
                        String errorMessage = "credential_offer_uri_path is empty or null";
                        log.error("[{}]: {}",api, "credential_offer_uri_path is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String db_secret = credential_issuer_config_entity.get(0).getDbSecret();
                    if(db_secret==null || db_secret.isEmpty())
                    {
                        error_code = 12003;
                        String errorMessage = "db_secret is empty or null";
                        log.error("[{}]: {}",api, "db_secret is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String db_iv = credential_issuer_config_entity.get(0).getDbIv();
                    if(db_iv==null || db_iv.isEmpty())
                    {
                        error_code = 12004;
                        String errorMessage = "db_iv is empty or null";
                        log.error("[{}]: {}",api, "db_iv is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String app_url_scheme = credential_issuer_config_entity.get(0).getAppUrlScheme();
                    if(app_url_scheme==null || app_url_scheme.isEmpty())
                    {
                        error_code = 12005;
                        String errorMessage = "app_url_scheme is empty or null";
                        log.error("[{}]: {}",api, "app_url_scheme is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String credential_url = credential_issuer_config_entity.get(0).getCredentialUrl();
                    if(credential_url==null || credential_url.isEmpty())
                    {
                        error_code = 12009;
                        String errorMessage = "credential_url is empty or null";
                        log.error("[{}]: {}",api, "credential_url is empty or null");
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    // Check if credential_configuration_id is in Credential Offer's credential_configuration_ids
                    JSONObject credential_offer_json = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(credential_offer_str);
                    log.info("[{}]: credential_offer: {}", api, credential_offer_json.toJSONString(JSONStyle.LT_COMPRESS));
                    JSONArray credential_configuration_ids_array = (JSONArray)credential_offer_json.get("credential_configuration_ids");

                    // IDT中的credential_configuration_id沒有在Credential Offer 的 credential_configuration_ids中，代表不支援此種VC
                    if(!credential_configuration_ids_array.contains(credential_configuration_id))
                    {
                        error_code = 11011;
                        log.error("[{}]: {}",api, "The VC type is unsupported(credential_configuration_id)");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    // 1. 提取 cid（不驗證是否存在或類型）
                    Object cidObj = request_map.get("cid");
                    JSONArray cidArray = new JSONArray();
                    JSONArray statusRevokeArray = new JSONArray(); // 已撤銷
                    JSONArray cidNotFoundArray = new JSONArray();  //  查無此 cid 或非法

                    // 2. 處理 cid（允許缺失或非 List）
                    if (cidObj instanceof List<?>)
                    {
                        List<?> list = (List<?>) cidObj;
                        if (list.isEmpty())
                        {
                            log.info("[{}]: cid is an empty list", api);
                        }

                        else
                        {
                            for (Object obj : list)
                            {
                                String obj_str = (obj == null) ? "null" : obj.toString().trim();

                                if (obj_str.isEmpty() || "null".equalsIgnoreCase(obj_str))
                                {
                                    log.info("[{}]: cid is null or empty, adding to cid_not_found", api);
                                    cidNotFoundArray.add("null");
                                    continue;
                                }

                                try
                                {
                                    List<CredentialEntity> credential_entity = credential_service.getCredentialCID(obj_str);
                                    if (!credential_entity.isEmpty())
                                    {
                                        String str_credential_status = credential_entity.get(0).getCredentialStatus();
                                        if (!"2".equals(str_credential_status))
                                        {
                                            cidArray.add(obj_str); //  有效 cid
                                        }

                                        else
                                        {
                                            statusRevokeArray.add(obj_str); //  已撤銷
                                        }
                                    }

                                    else
                                    {
                                        cidNotFoundArray.add(obj_str); // 查無此 cid
                                    }
                                }

                                catch (Exception e)
                                {
                                    log.info("[{}]: Error querying credential for cid: {}", api, obj_str, e);
                                    cidNotFoundArray.add(obj_str);
                                }
                            }
                        }
                    }

                    else if (cidObj != null)
                    {
                        // 若不是 List，當單一字串處理
                        String obj_str = cidObj.toString().trim();
                        if (obj_str.isEmpty() || "null".equalsIgnoreCase(obj_str))
                        {
                            log.info("[{}]: Single cid is null or empty", api);
                            cidNotFoundArray.add("null");
                        }

                        else
                        {
                            try
                            {
                                List<CredentialEntity> credential_entity = credential_service.getCredentialCID(obj_str);
                                if (!credential_entity.isEmpty())
                                {
                                    String str_credential_status = credential_entity.get(0).getCredentialStatus();
                                    if (!"2".equals(str_credential_status))
                                    {
                                        cidArray.add(obj_str);
                                    }

                                    else
                                    {
                                        statusRevokeArray.add(obj_str);
                                    }
                                }

                                else
                                {
                                    cidNotFoundArray.add(obj_str);
                                }
                            }

                            catch (Exception e)
                            {
                                log.error("[{}]: Error querying credential for cid: {}", api, obj_str, e);
                                cidNotFoundArray.add(obj_str);
                            }
                        }
                    }

                    else
                    {
                        // cid 缺失，記錄並儲存空陣列
                        log.info("[{}]: cid is missing", api);
                    }

                    // 3. 準備回傳用 JSON
                    JSONObject responseJson = new JSONObject();
                    responseJson.put("cids", cidArray);

                    if (!statusRevokeArray.isEmpty())
                    {
                        responseJson.put("status_revoke", statusRevokeArray);
                    }

                    if (!cidNotFoundArray.isEmpty()) {
                        responseJson.put("cid_not_found", cidNotFoundArray);
                    }

                    // 4. 準備 DB 用 JSON（只存有效 cids）
                    JSONObject dbJson = new JSONObject();
                    dbJson.put("cids", cidArray);
                    String jsonString = dbJson.toJSONString(JSONStyle.LT_COMPRESS);
                    log.info("[{}]: DB JSON to be stored: {}", api, jsonString);

                    // 可選：log 給前端的完整 response
                    log.info("[{}]: Full Response: {}", api, responseJson.toJSONString(JSONStyle.LT_COMPRESS));

                    // Gen pre-auth code
                    Timestamp create_time = null;
                    Timestamp expire_time = null;
                    String code_status = CODE_VALID;
                    long minutes_in_millis = 10 * 60 * 1000;//10 minutes = 600,000 millisecond
                    AuthorizationCode pre_auth_code_obj = new AuthorizationCode();
                    String uid_enc = null;

                    uid_enc = crypto_util.GCMEncrypt(uid, db_secret, db_iv);
                    log.info("[{}]: req_time: {}", api, date_format.format(new Date(req_time)));
                    create_time = new Timestamp(req_time);
//					expire_time = new Timestamp(exp);
                    expire_time = new Timestamp(req_time + minutes_in_millis);

                    boolean generated = pre_auth_code_service.genPreAuthCode(uid_enc, nonce, client_id, create_time, expire_time, code_status, credential_configuration_id, pre_auth_code_obj.toString(),tx_code,jsonString);
                    log.info("genPreAuthCode() tx_code is ...."+tx_code);
                    if(!generated)
                    {
                        error_code = 11005;
                        log.error("[{}]: {}",api, "generated is false");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    // Gen QRCode、link
                    QRCodeUtil qr_util = new QRCodeUtil();
                    String qr_code = null;
                    String credential_offer_uri = credential_issuer_identifier + credential_offer_uri_path + "?nonce=" + nonce +
                        "&sub=" + uid_enc;
                    String link = app_url_scheme + "credential_offer_uri=" + URLEncoder.encode(credential_offer_uri, "UTF-8");

                    try
                    {
                        qr_code = qr_util.genQRCodeb64(link, qrcode_side_length, qrcode_side_length);
                    }

                    catch(Exception qr_e)
                    {
                        error_code = 11006;
                        log.error("[{}]: {}",api, qr_e.getMessage());
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    // 5. 構建 ret_json
                    error_code = 10000;
                    JSONObject ret_json = makeReturnObj(api, error_code);
                    ret_json.put("qr_code", qr_code);
                    ret_json.put("link", link);

                    // 動態構建 warningsObject，僅包含非空陣列
                    JSONObject warningsObjectForRet = new JSONObject();
                    if (!statusRevokeArray.isEmpty())
                    {
                        warningsObjectForRet.put("status_revoke", statusRevokeArray);
                    }

                    if (!cidNotFoundArray.isEmpty())
                    {
                        warningsObjectForRet.put("cid_not_found", cidNotFoundArray);
                    }

                    // 僅在 warningsObjectForRet 非空時添加 warnings
                    if (!warningsObjectForRet.isEmpty())
                    {
                        ret_json.put("warnings", warningsObjectForRet);
                    }
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));                }
            }

            else
            {
                error_code = 11002;
                log.error("[{}]: {}",api, "authenticated is false");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }
        }

        catch(Exception e)
        {
            log.error("[{}]: {}",api, e.getMessage());
            e.printStackTrace();
            error_code = 11500;
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
        }
    }

    @GetMapping(path = "/credential-offer-object", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> GetCredentialOffer(
        @RequestParam(value = "nonce") String nonce,
        @RequestParam(value = "sub") String uid_enc,
        @PathVariable String id)
    {

        String api = "credential-offer-object";
        int error_code = -1;
        String extra_error_msg = "";
        Optional<PreAuthCodeEntity> pre_auth_code_entity;

        log.info("[{}]: request_data{nonce={}, sub={}}", api, nonce, uid_enc);

        try
        {
            // Get DB config
            List<CredentialIssuerConfigEntity> credential_issuer_config_entity = credential_issuer_config_service.getCredentialIssuerConfig(id);

            if (credential_issuer_config_entity == null || credential_issuer_config_entity.isEmpty())
            {
                error_code = 11901;
                String errorMessage = "Query not found: No data found matching the criteria.";
                log.warn("[{}]: {}",api, "Query not found: No data found matching the criteria.");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }



            // Get credential offer
            String credential_offer_str = credential_issuer_config_entity.get(0).getCredentialOffer();
            if(credential_offer_str == null || credential_offer_str.isEmpty())
            {
                error_code = 12000;
                String errorMessage = "credential_offer_str is empty or null";
                log.error("[{}]: {}",api, "credential_offer_str is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // str to json，parsing the json
            JSONObject credential_offer_json = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(credential_offer_str);
            JSONObject grants_json = (JSONObject) credential_offer_json.get("grants");
            JSONObject pre_auth_code_json = (JSONObject) grants_json.get("urn:ietf:params:oauth:grant-type:pre-authorized_code");


            // create tx_code 的 JSON
            pre_auth_code_entity = pre_auth_code_service.getPreAuthCode(uid_enc, nonce);
            if (pre_auth_code_entity.isEmpty()) {
                error_code = 12030;
                String errorMessage = "No PreAuthCodeEntity found for given uid and nonce";
                log.error("[{}]: {}", api, errorMessage);
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            String cid_str = pre_auth_code_entity.get().getCID();
            if (cid_str == null || cid_str.isEmpty())
            {
                error_code = 12031;
                String errorMessage = "cid_str is empty or null";
                log.error("[{}]: {}", api, errorMessage);
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try
            {
                root = mapper.readTree(cid_str); // 嘗試解析 JSON
            }

            catch (Exception e)
            {
                error_code = 12032; // 新增錯誤碼：JSON 解析失敗
                String errorMessage = "Failed to parse cid_str JSON: " + e.getMessage();
                log.error("[{}]: {}", api, errorMessage);
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            JsonNode cidsNode = root.get("cids");
            if (cidsNode == null || !cidsNode.isArray())
            {
                error_code = 12033; // 新增錯誤碼：缺少 cids 陣列或格式錯誤
                String errorMessage = "cid_str JSON missing or invalid cids array";
                log.error("[{}]: {}", api, errorMessage);
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            List<String> cidList = new ArrayList<>();
            if (cidsNode.isArray())
            {
                for (JsonNode cid : cidsNode)
                {
                    String cidStr = cid.asText(); // 乾淨的 UUID 字串
                    if (cidStr == null || cidStr.isEmpty())
                    {
                        error_code = 12034; // 新增錯誤碼：單個 cid 為空
                        String errorMessage = "Invalid or empty cid value in cids array";
                        log.error("[{}]: {}", api, errorMessage);
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }
                    cidList.add(cidStr);
                    log.info("cid..." + cidStr);

                    String url = credential_issuer_config_entity.get(0).getCredentialUrl();
                    if (url == null || url.isEmpty())
                    {
                        error_code = 12035;
                        String errorMessage = "credential_url is empty or null";
                        log.error("[{}]: {}", api, errorMessage);
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    String cleanUrl = url + "/" + cidStr + "/revocation";
                    String retBody = http_service.sendPutRequest(cleanUrl);

                    if (retBody == null || retBody.isEmpty())
                    {
                        error_code = 12036; // 新增錯誤碼：HTTP 請求返回空
                        String errorMessage = "HTTP request to " + cleanUrl + " returned null or empty response";
                        log.error("[{}]: {}", api, errorMessage);
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }

                    try
                    {
                        ObjectMapper obj_mapper = new ObjectMapper();
                        JsonNode jsonNode = obj_mapper.readTree(retBody);

                        int respCode = jsonNode.has("resp_code") ? jsonNode.get("resp_code").asInt() : -1;
                        JsonNode responseBodyNode = jsonNode.get("response_body");

                        if (respCode != 10000 || responseBodyNode == null)
                        {
                            error_code = 12037; // 新增錯誤碼：回應碼無效或缺少 response_body
                            String errorMessage = "Invalid response or missing response_body";
                            log.error("[{}]: {}", api, errorMessage);
                            return ResponseEntity
                                .badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                        }

                        JsonNode innerBody = obj_mapper.readTree(responseBodyNode.asText());
                        String credentialStatus = innerBody.has("credentialStatus") ? innerBody.get("credentialStatus").asText() : null;

                        if (credentialStatus == null || !"REVOKED".equalsIgnoreCase(credentialStatus))
                        {
                            error_code = 12038; // 現有錯誤碼：憑證狀態無效
                            String errorMessage = "Revocation response invalid or status not REVOKED: " + (credentialStatus != null ? credentialStatus : "null");
                            log.error("[{}]: {}", api, errorMessage);
                            return ResponseEntity
                                .badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                        }

                        log.info("Credential successfully revoked: {}", credentialStatus);
                    }

                    catch (Exception e)
                    {
                        error_code = 12039; // 新增錯誤碼：JSON 解析或內部錯誤
                        String errorMessage = "Credential revocation verification failed: " + e.getMessage();
                        log.error("[{}]: {}", api, errorMessage);
                        return ResponseEntity
                            .badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                    }
                }
            }

            String credential_configuration_id = pre_auth_code_entity.get().getCredentialConfigurationId();
            if(credential_configuration_id == null || credential_configuration_id.isEmpty())
            {
                error_code = 12011;
                String errorMessage = "credential_configuration_id is empty or null";
                log.error("[{}]: {}",api, "credential_configuration_id is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            List<CredentialPolicyEntity> credential_policy_entity = credential_policy_service.getCredentialPolicyByType(credential_configuration_id);
            if (credential_policy_entity == null || credential_policy_entity.isEmpty())
            {
                error_code = 11021;
                String errorMessage = "credential_policy_entity is empty or null";
                log.warn("[{}]: {}",api, "credential_policy_entity is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }
            String switch_str_credential_offer = credential_policy_entity.get(0).getFuncSwitch();

            log.info("switch_str_credential_offer " + switch_str_credential_offer);

            boolean OTP_Switch_credential_offer = false; // 預設開關關閉

            // 解析 func_switch 的 JSON 內容
            if (switch_str_credential_offer != null && !switch_str_credential_offer.isEmpty())
            {
                try
                {
                    JsonObject switchObj = JsonParser.parseString(switch_str_credential_offer).getAsJsonObject();
                    if (switchObj.has("enable_tx_code"))
                    {
                        OTP_Switch_credential_offer = switchObj.get("enable_tx_code").getAsBoolean();
                        log.info("OTP_Switch_credential_offer " + OTP_Switch_credential_offer);
                    }

                    else
                    {
                        // enable_tx_code 不存在，設為 false
                        OTP_Switch_credential_offer = false;
                    }
                }

                catch (Exception e)
                {
                    int errorCode = 11033;
                    String errorMsg = "Invalid func_switch JSON format: " + OTP_Switch_credential_offer;
                    log.error("[{}]: {}", api, errorMsg, e);
                    return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(makeReturnObj(api, errorCode, errorMsg).toJSONString(JSONStyle.LT_COMPRESS));
                }
            }

            // 檢查開關是否開啟
            if (OTP_Switch_credential_offer)
            {
                List<OidvciSettingEntity> oidvci_setting_entity = oidvci_setting_service.getOidvciSetting("tx_code_credential_offer");
                if (oidvci_setting_entity == null || oidvci_setting_entity.isEmpty())
                {
                    error_code = 11022;
                    String errorMessage = "oidvci_setting_entity is empty or null";
                    log.warn("[{}]: {}",api, "oidvci_setting_entity is empty or null");
                    return ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                }

                //取得Credential offer object(tx_code)
                String credential_offer_txcode = oidvci_setting_entity.get(0).getSettingValue();
                if(credential_offer_txcode == null || credential_offer_txcode.isEmpty())
                {
                    error_code = 12007;
                    String errorMessage = "credential_offer_txcode setting_value is empty or null";
                    log.warn("[{}]: {}",api, "credential_offer_txcode setting_value is empty or null");
                    return ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
                }
                log.info("credential_offer_txcode..."+credential_offer_txcode);
                JSONObject credential_offer_txcode_json = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(credential_offer_txcode);
                pre_auth_code_json.put("tx_code",credential_offer_txcode_json);
            }

            // 加入 synchronized 區塊
            synchronized (this)
            {
                // get pre_auth_code
                pre_auth_code_entity = pre_auth_code_service.getPreAuthCode(uid_enc, nonce);
                if (pre_auth_code_entity==null || pre_auth_code_entity.isEmpty())
                {
                    error_code = 11007;
                    log.error("[{}]: {}",api, "pre_auth_code is empty or null");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                }

                // check if pre_auth_code has expired
                String code_status = pre_auth_code_service.checkPreAuthCodeValidity(pre_auth_code_entity.get());
                if (code_status.equals(CODE_EXPIRED))
                {
                    error_code = 11008;
                    log.error("[{}]: {}",api, "CODE_EXPIRED");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                }

                if (code_status.equals(CODE_USED))
                {
                    error_code = 11009;
                    log.error("[{}]: {}",api, "CODE_USED");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                }

                if (code_status.equals(CODE_CHANGE))
                {
                    error_code = 11023;
                    extra_error_msg = "pre-authorizated code has already been change";
                    log.error("[{}]: {}",api, "CODE_CHANGE");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code,extra_error_msg).toJSONString(JSONStyle.LT_COMPRESS));
                }

                // 將Pre-auth code放進Credential Offer
                String pre_auth_code = pre_auth_code_entity.get().getPreCode();
                if (pre_auth_code==null || pre_auth_code.isEmpty())
                {
                    error_code = 11020;
                    log.error("[{}]: {}",api, "pre_auth_code is empty or null");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
                }

                log.info("[{}]: pre_auth_code: {}", api, pre_auth_code);
                pre_auth_code_json.replace("pre-authorized_code", pre_auth_code);
                pre_auth_code_service.updatePreAuthCodeCodeStatus(pre_auth_code_entity.get(), CODE_CHANGE);
            }

            // 將credential_configuration_ids中的值換成目前要申請的VC
            //String credential_configuration_id = pre_auth_code_entity.get().getCredentialConfigurationId();
            JSONArray credential_configuration_ids_array = (JSONArray) credential_offer_json.get("credential_configuration_ids");
            if (credential_configuration_ids_array.contains(credential_configuration_id))
            {
                credential_configuration_ids_array.clear();
                credential_configuration_ids_array.add(credential_configuration_id);
            }

            else
            {
                error_code = 11011;
                log.error("[{}]: {}",api,"credential_configuration_ids change fail");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }
            log.info("[{}]: CredentialOffer: {}", api, credential_offer_json.toJSONString(JSONStyle.LT_COMPRESS));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(credential_offer_json.toJSONString(JSONStyle.LT_COMPRESS));
        }

        catch (Exception e)
        {
            log.error("[{}]: {}", api, e.getMessage());
            e.printStackTrace();
            error_code = 11500;
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
        }
    }

    @GetMapping(path = "/.well-known/openid-credential-issuer" , produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> GetCredentialIssuerMetadata(@PathVariable String id)
    {
        String api = ".well-known/openid-credential-issuer";
        int error_code = -1;
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        date_format.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        //JSONObject req_data = null;

        try
        {
            //ObjectMapper objectMapper = new ObjectMapper();
            //String json_string = objectMapper.writeValueAsString(request_map);
            //req_data = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(json_string);

            log.info("[{}]: input params: ", api);
            //log.info("[{}]: req_data: {}", api, req_data.toJSONString(JSONStyle.LT_COMPRESS));

            // Get DB config
            List<CredentialIssuerConfigEntity> credential_issuer_config_entity = credential_issuer_config_service.getCredentialIssuerConfig(id);
            if (credential_issuer_config_entity == null || credential_issuer_config_entity.isEmpty())
            {
                error_code = 11901;
                String errorMessage = "Query not found: No data found matching the criteria.";
                log.warn("[{}]: {}",api, "Query not found: No data found matching the criteria.");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Get Credential Issuer Metadata
            String credential_issuer_meta = credential_issuer_config_entity.get(0).getCredentialIssuerMeta();
            if(credential_issuer_meta==null || credential_issuer_meta.isEmpty())
            {
                error_code = 12006;
                String errorMessage = "credential_issuer_meta is empty or null";
                log.error("[{}]: {}",api, "credential_issuer_meta is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(credential_issuer_meta);
        }

        catch(Exception e)
        {
            log.error("[{}]: {}",api, e.getMessage());
            e.printStackTrace();
            error_code = 11500;
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
        }
    }

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> GetAccessToken(
        @PathVariable("id") String id,
        @RequestParam(value="grant_type", required=false) String grant_type,
        @RequestParam(value="client_id", required=false) String client_id,
        @RequestParam(value="pre-authorized_code", required=false) String pre_auth_code,
        @RequestParam(value="authorization_details", required=false) String authorization_details,
        @RequestParam(value="tx_code", required=false) String tx_code)
    {
        String api = "token";
        int error_code = -1;
        String extra_error_msg = null;
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        date_format.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        String error_value = null;
        Optional<PreAuthCodeEntity> pre_auth_code_entity;

        log.info("[{}]: request data {grant_type={}, client_id={}, pre_authorized_code={}, authorization_details={},tx_code={}",
            api, grant_type, client_id, pre_auth_code, authorization_details,tx_code);

        try
        {
            // check parameters
            if(grant_type == null)
            {
                error_code = 11001;
                extra_error_msg = "missing parameter : grant_type";
                error_value = "invalid_request";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "missing parameter : grant_type");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            if(client_id == null)
            {
                error_code = 11001;
                extra_error_msg = "missing parameter : client_id";
                error_value = "invalid_request";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "missing parameter : client_id");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            if(pre_auth_code == null)
            {
                error_code = 11001;
                extra_error_msg = "missing parameter : pre-authorized_code";
                error_value = "invalid_request";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "missing parameter : pre-authorized_code");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            if(authorization_details == null)
            {
                error_code = 11001;
                extra_error_msg = "missing parameter : authorization_details";
                error_value = "invalid_request";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "missing parameter : authorization_details");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            if(!grant_type.equals("urn:ietf:params:oauth:grant-type:pre-authorized_code"))
            {
                error_code = 11010;
                error_value = "unsupported_grant_type";
                JSONObject ret_json = makeReturnObj(api, error_code);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "unsupported_grant_type");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Find Pre-auth code record from DB
            pre_auth_code_entity = pre_auth_code_service.getPreAuthCodeByClientIDAndPreAuthCode(client_id, pre_auth_code);
            if(pre_auth_code_entity.isEmpty())
            {
                error_code = 11007;
                error_value = "invalid_grant";
                JSONObject ret_json = makeReturnObj(api, error_code);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "pre_auth_code_entity is empty");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Check Pre-auth code 有效性
            String code_status = pre_auth_code_service.checkPreAuthCodeValidity(pre_auth_code_entity.get());
            if(code_status.equals(CODE_EXPIRED))
            {
                error_code = 11008;
                error_value = "invalid_grant";
                JSONObject ret_json = makeReturnObj(api, error_code);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "CODE_EXPIRED");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            if(code_status.equals(CODE_USED))
            {
                error_code = 11009;
                error_value = "invalid_grant";
                JSONObject ret_json = makeReturnObj(api, error_code);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "CODE_USED");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Check authorization_details with Pre-auth code record
            String credential_configuration_id = pre_auth_code_entity.get().getCredentialConfigurationId();
            if(credential_configuration_id == null || credential_configuration_id.isEmpty())
            {
                error_code = 11024;
                String errorMessage = "credential_configuration_id is empty or null";
                log.error("[{}]: {}",api, "credential_configuration_id is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            JSONArray authorization_details_json = new JSONArray();
            try
            {
                authorization_details_json = (JSONArray)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(URLDecoder.decode(authorization_details, "UTF-8"));
            }

            catch(Exception e1)
            {
                error_code = 11001;
                error_value = "invalid_request";
                extra_error_msg = "invalid parameter : authorization_details";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, e1.getMessage());
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Check tx_code
            String DB_tx_code = pre_auth_code_entity.get().getTxCode();
            List<CredentialPolicyEntity> credential_policy_entity = credential_policy_service.getCredentialPolicyByType(credential_configuration_id);
            if (credential_policy_entity == null || credential_policy_entity.isEmpty())
            {
                error_code = 11021;
                String errorMessage = "credential_policy_entity is empty or null";
                log.warn("[{}]: {}",api, "credential_policy_entity is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }
            String switch_str_token = credential_policy_entity.get(0).getFuncSwitch();

            log.info("switch_str_token " + switch_str_token);

            boolean OTP_Switch_Token = false;

            if (switch_str_token != null && !switch_str_token.isEmpty())
            {
                try
                {
                    JsonObject switchObj = JsonParser.parseString(switch_str_token).getAsJsonObject();
                    if (switchObj.has("enable_tx_code"))
                    {
                        OTP_Switch_Token = switchObj.get("enable_tx_code").getAsBoolean();
                        log.info("OTP_Switch_Token " + OTP_Switch_Token);
                    }

                    else
                    {
                        // enable_tx_code 不存在，設為 false
                        OTP_Switch_Token = false;
                    }
                }

                catch (Exception e)
                {
                    int errorCode = 11033;
                    String errorMsg = "Invalid func_switch JSON format: " + switch_str_token;
                    log.error("[{}]: {}", api, errorMsg, e);
                    return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(makeReturnObj(api, errorCode, errorMsg).toJSONString(JSONStyle.LT_COMPRESS));
                }
            }

            // 檢查開關是否開啟
            if (OTP_Switch_Token)
            {
                // 開關開啟，tx_code 必須存在且與 DB_tx_code 一致
                if (tx_code == null || tx_code.isEmpty() || DB_tx_code == null || !DB_tx_code.equals(tx_code)) {
                    int errorCode = 11032;
                    String errorMsg = "tx_code is required and must match DB_tx_code when enable_tx_code is true";
                    log.warn("[{}]: {}", api, errorMsg);
                    return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(makeReturnObj(api, errorCode, errorMsg).toJSONString(JSONStyle.LT_COMPRESS));
                }
                log.info("tx_code matches DB_tx_code. Check passed.");
            }

            else
            {
                // 開關關閉，tx_code 不需要存在，直接通過
                log.info("tx_code check skipped because enable_tx_code is false or not set.");
            }

            Iterator<Object> iterator = authorization_details_json.iterator();
            while (iterator.hasNext())
            {
                JSONObject authorization_json = (JSONObject) iterator.next();
                // 解析每個 JSONObject
                String details_type = authorization_json.getAsString("type");
                String details_credential_configuration_id = authorization_json.getAsString("credential_configuration_id");
                if(!details_type.equals("openid_credential"))
                {
                    error_code = 11001;
                    error_value = "invalid_request";
                    extra_error_msg = "invalid authorization_details parameter : type";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    log.error("[{}]: {}",api, "invalid authorization_details parameter : type");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }

                if(!details_credential_configuration_id.equals(credential_configuration_id))
                {
                    error_code = 11012;
                    error_value = "invalid_request";
                    JSONObject ret_json = makeReturnObj(api, error_code);
                    ret_json.put("error", error_value);
                    log.error("[{}]: {}",api, "credential_configuration_id not equal details_credential_configuration_id");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }
            }

            // Gen response authorization_details
            JSONArray resp_authorization_details_json = new JSONArray();
            JSONObject resp_authorization_details_field_json = new JSONObject();
            resp_authorization_details_field_json.put("type", "openid_credential");
            resp_authorization_details_field_json.put("credential_configuration_id", credential_configuration_id);
            JSONArray credential_identifiers = new JSONArray();
            credential_identifiers.add(credential_configuration_id);
            resp_authorization_details_field_json.put("credential_identifiers", credential_identifiers);
            resp_authorization_details_json.add(resp_authorization_details_field_json);

            // Gen Access Token & c_nonce
            BearerAccessToken access_token = new BearerAccessToken(TOKEN_TTL, null);
            Nonce c_nonce = new Nonce();
            long req_time = new Date().getTime();
            Timestamp create_time = null;
            Timestamp expire_time = null;
            Timestamp c_nonce_create_time = null;
            Timestamp c_nonce_expire_time = null;
            long minutes_in_millis = TOKEN_TTL * 1000;//10 minutes = 600,000 millisecond
            create_time = new Timestamp(req_time);
            expire_time = new Timestamp(req_time + minutes_in_millis);
            c_nonce_create_time = new Timestamp(req_time);
            c_nonce_expire_time = new Timestamp(req_time + minutes_in_millis);

            // insert into database (Access Token & c_nonce & nonce from idp)
            String nonce = pre_auth_code_entity.get().getPre_auth_code_id().getNonce();
            if(nonce == null || nonce.isEmpty())
            {
                error_code = 11025;
                String errorMessage = "nonce is empty or null";
                log.error("[{}]: {}",api, "nonce is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }
            String user_id = pre_auth_code_entity.get().getPre_auth_code_id().getUser_id();
            if(user_id == null || user_id.isEmpty())
            {
                error_code = 11026;
                String errorMessage = "user_id is empty or null";
                log.error("[{}]: {}",api, "user_id is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }
            boolean generated = access_token_service.genAccessToken(access_token.getValue(), client_id, user_id, create_time, expire_time, nonce,
                c_nonce.getValue(), c_nonce_create_time, c_nonce_expire_time,
                resp_authorization_details_json.toJSONString(JSONStyle.LT_COMPRESS), pre_auth_code, CODE_VALID, "");

            if(!generated)
            {
                error_code = 11013;
                log.error("[{}]: {}",api, "generated fail");
                return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Gen token_response
            JSONObject token_response = new JSONObject();
            token_response.put("access_token", access_token.getValue());
            token_response.put("token_type", access_token.getType());
            token_response.put("expires_in", access_token.getLifetime());
            token_response.put("c_nonce", c_nonce);
            token_response.put("c_nonce_expires_in", access_token.getLifetime());
            token_response.put("authorization_details", resp_authorization_details_json);

            // Update code_status of Pre-auth code record
            pre_auth_code_service.updatePreAuthCodeCodeStatus(pre_auth_code_entity.get(), CODE_USED);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(token_response.toJSONString(JSONStyle.LT_COMPRESS));
        }

        catch(Exception e)
        {
            log.error("[{}]: {}",api, e.getMessage());
            e.printStackTrace();
            error_code = 11500;
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
        }
    }

    @PostMapping(path = "/credential", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> GetCredential(@RequestHeader(value = "Authorization", required = false) String auth_header ,
                                                @RequestBody Map<String, Object> request_map, @PathVariable String id)
    {
        String api = "credential";
        int error_code = -1;
        String extra_error_msg = null;
        DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        date_format.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        String realm_value = null;
        String error_value = null;
        String error_description_value = null;
        String error_header_content = null;
        Optional<AccessTokenEntity> access_token_entity;
        List<CredentialIssuerConfigEntity> credential_issuer_config_entity;
        String credential_issuer = null;
        String credential_identifier = null;
        JSONObject proofs = null;
        JSONArray jwt_ary = null;
        String jwt = null;
        String jwt_kid = null;
        String jwt_client_id = null;
        String jwt_credential_identifier = null;
        String jwt_c_nonce = null;
        JSONObject holder_pub_key = null;
        JSONObject req_data = null;
        CryptoHelper crypto_util = new CryptoHelper();

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            String json_string = objectMapper.writeValueAsString(request_map);
            req_data = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(json_string);
            log.info("[{}]: req_data: {}", api, req_data.toJSONString(JSONStyle.LT_COMPRESS));

            // Read DB config
            credential_issuer_config_entity = credential_issuer_config_service.getCredentialIssuerConfig(id);

            if (credential_issuer_config_entity == null || credential_issuer_config_entity.isEmpty())
            {
                error_code = 11901;
                String errorMessage = "Query not found: No data found matching the criteria.";
                log.warn("[{}]: {}",api, "Query not found: No data found matching the criteria.");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Get credential_configuration_ids from credential offer
            String credential_offer = credential_issuer_config_entity.get(0).getCredentialOffer();
            if(credential_offer==null || credential_offer.isEmpty())
            {
                error_code = 12000;
                String errorMessage = "credential_offer is empty or null";
                log.error("[{}]: {}",api, "credential_offer is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }
            JSONArray credential_configuration_ids = getCredentialConfigurationIds(credential_offer);

            // Get credential issuer from credential offer
            credential_issuer = credential_issuer_config_service.getCredentialIssuer(credential_offer);
            String credential_issuer_meta = credential_issuer_config_entity.get(0).getCredentialIssuerMeta();
            if(credential_issuer_meta==null || credential_issuer_meta.isEmpty())
            {
                error_code = 12006;
                String errorMessage = "credential_issuer_meta is empty or null";
                log.error("[{}]: {}",api, "credential_issuer_meta is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // check parameters
            if(req_data.containsKey("credential_identifier"))
            {
                credential_identifier = req_data.getAsString("credential_identifier");

                // check parameters
                // 檢查credential_identifier在不在credential_offer定義中
                if(!credential_configuration_ids.contains(credential_identifier))
                {
                    error_code = 11011;
                    error_value = "unsupported_credential_type";
                    JSONObject ret_json = makeReturnObj(api, error_code);
                    ret_json.put("error", error_value);
                    log.error("[{}]: {}",api, "unsupported_credential_type");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }
            }

            else
            {
                error_code = 11001;
                error_value = "invalid_credential_request";
                extra_error_msg = "missing parameter : credential_identifier";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "missing parameter : credential_identifier");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            if(req_data.containsKey("proofs"))
            {
                proofs = (JSONObject)req_data.get("proofs");
                if(!proofs.containsKey("jwt"))
                {
                    error_code = 11001;
                    error_value = "invalid_credential_request";
                    extra_error_msg = "missing parameter : jwt";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    log.error("[{}]: {}",api, "missing parameter : jwt");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }
            }

            else
            {
                error_code = 11001;
                error_value = "invalid_credential_request";
                extra_error_msg = "missing parameter : proofs";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "missing parameter : proofs");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // 缺少Authorization
            if(auth_header == null || !auth_header.startsWith("Bearer "))
            {
                error_code = 11014;
                error_value = "invalid_credential_request";
                JSONObject ret_json = makeReturnObj(api, error_code);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "missing parameter : Bearer");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // Get access_token
            String access_token = auth_header.substring(7);	// 去掉前端的"Bearer "
            if(access_token.isEmpty())
            {
                error_code = 11015;
                realm_value = credential_identifier;
                error_value = "invalid_token";
                error_description_value = "The access token is missing";
                error_header_content = "Bearer realm=\"" + realm_value + "\"," +
                    "error=\"" + error_value + "\"," +
                    "error_description=\"" + error_description_value + "\"";
                log.error("[{}]: {}",api, "The access token is missing");
                return ResponseEntity.status(401).header("WWW-Authenticate", error_header_content).contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // 驗證access_token
            // Find access_token record from DB
            access_token_entity = access_token_service.getAccessTokenByTokenValue(access_token);
            if(access_token_entity.isEmpty())
            {
                error_code = 11017;
                realm_value = credential_identifier;
                error_value = "invalid_token";
                error_description_value = "The access_token not found";
                error_header_content = "Bearer realm=\"" + realm_value + "\"," +
                    "error=\"" + error_value + "\"," +
                    "error_description=\"" + error_description_value + "\"";
                log.error("[{}]: {}",api, "The access_token not found");
                return ResponseEntity.status(401).header("WWW-Authenticate", error_header_content).contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // 檢查access_token有效性
            String token_status = access_token_service.checkAccessTokenValidity(access_token_entity.get());
            if(token_status.equals(CODE_EXPIRED) || token_status.equals(CODE_USED))
            {
                error_code = 11018;
                realm_value = credential_identifier;
                error_value = "invalid_token";
                error_description_value = "The access_token has either expired or has already been used.";
                error_header_content = "Bearer realm=\"" + realm_value + "\"," +
                    "error=\"" + error_value + "\"," +
                    "error_description=\"" + error_description_value + "\"";
                log.error("[{}]: {}",api, "The access_token has either expired or has already been used.");
                return ResponseEntity.status(401).header("WWW-Authenticate", error_header_content).contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }

            // 驗證帶進來的credential_identifier跟access_token record中說要申請的一不一樣
            JSONArray authorization_details_json_db = (JSONArray)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(access_token_entity.get().getAuthorizationDetails());
            Iterator<Object> iterator = authorization_details_json_db.iterator();
            boolean credential_identifier_is_valid = false;
            while (iterator.hasNext())
            {
                JSONObject authorization_json = (JSONObject) iterator.next();
                // 解析每個 JSONObject
                String details_credential_configuration_id = authorization_json.getAsString("credential_configuration_id");
                if(details_credential_configuration_id.equals(credential_identifier))
                {
                    credential_identifier_is_valid = true;
                    break;
                }
            }

            if(!credential_identifier_is_valid)
            {
                error_code = 11019;
                error_value = "invalid_credential_request";
                JSONObject ret_json = makeReturnObj(api, error_code);
                ret_json.put("error", error_value);
                log.error("[{}]: {}",api, "credential_identifier_is_valid is false");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // 解析jwt
            try
            {
                jwt_ary = (JSONArray)proofs.get("jwt");
                jwt = (String)jwt_ary.get(0);
                final String[] split_jwt = jwt.split("\\.");
                final String header_str = split_jwt[0];
                final String payload_str = split_jwt[1];
                final String signature_str = split_jwt[2];

                JSONObject header_obj = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(Base64.getDecoder().decode(header_str));
                jwt_kid = header_obj.getAsString("kid");
                String typ = header_obj.getAsString("typ");
                String alg = header_obj.getAsString("alg");

                // 解析kid
                String[] kid_token = jwt_kid.split(":");
                String did = null;
                if(kid_token.length == 3)
                    did = kid_token[2];

                else
                {
                    String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                    error_code = 11016;
                    error_value = "invalid_proof";
                    extra_error_msg = "DID format is incorrect.";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    ret_json.put("c_nonce", updated_cnonce);
                    ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                    log.error("[{}]: {}",api, "DID format is incorrect.");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }

                String did_hex_prefix = CryptoHelper.convertBinaryToHexString(Base58.decode(did.substring(1)));
                log.info("[{}]: DID HEX with Prefix = {}", api, did_hex_prefix);
                String did_hex = did_hex_prefix.substring(6);	// 去掉prefix D1D603
                log.info("[{}]: DID HEX = {}", api, did_hex);
                String did_jwk = new String(CryptoHelper.hexStringToByteArray(did_hex));
                log.info("[{}]: DID JWK = {}", api, did_jwk);
                holder_pub_key = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(did_jwk);

                // 檢查typ
                if(!typ.equals("openid4vci-proof+jwt"))
                {
                    String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                    error_code = 11016;
                    error_value = "invalid_proof";
                    extra_error_msg = "header typ is invalid";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    ret_json.put("c_nonce", updated_cnonce);
                    ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                    log.error("[{}]: {}",api, "header typ is invalid");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }

                //檢查alg，與credential issuer metadata相比
                JSONArray proof_signing_alg_values = credential_issuer_config_service.getProofSigningAlgValuesSupported(credential_issuer_meta, credential_identifier);

                if (proof_signing_alg_values.get(0) instanceof JSONObject &&
                    ((JSONObject) proof_signing_alg_values.get(0)).containsKey("error"))
                {
                    log.error("[{}]: {}",api,"Unexpected 'error' key found in proof_signing_alg_values");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(((JSONObject) proof_signing_alg_values.get(0)).toJSONString(JSONStyle.LT_COMPRESS));
                }

                if(!proof_signing_alg_values.contains(alg))
                {
                    String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                    error_code = 11016;
                    error_value = "invalid_proof";
                    extra_error_msg = "header alg is invalid";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    ret_json.put("c_nonce", updated_cnonce);
                    ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                    log.error("[{}]: {}",api,"header alg is invalid");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }

                JSONObject payload_obj = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(Base64.getDecoder().decode(payload_str));
                jwt_client_id = payload_obj.getAsString("iss");
                jwt_credential_identifier = payload_obj.getAsString("aud");
                jwt_c_nonce = payload_obj.getAsString("nonce");

                // 驗證iss
                if(!jwt_client_id.equals(access_token_entity.get().getClientID()))
                {
                    String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                    error_code = 11016;
                    error_value = "invalid_proof";
                    extra_error_msg = "payload iss is invalid";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    ret_json.put("c_nonce", updated_cnonce);
                    ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                    log.error("[{}]: {}",api,"payload iss is invalid");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }

                // 驗證aud
                if(!jwt_credential_identifier.equals(credential_issuer))
                {
                    String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                    error_code = 11016;
                    error_value = "invalid_proof";
                    extra_error_msg = "payload aud is invalid";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    ret_json.put("c_nonce", updated_cnonce);
                    ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                    log.error("[{}]: {}",api,"payload aud is invalid");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }

                // 驗證nonce
                if(!access_token_service.checkCNonceValidity(access_token_entity.get(), jwt_c_nonce))
                {
                    String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                    error_code = 11016;
                    error_value = "invalid_proof";
                    extra_error_msg = "payload nonce is invalid";
                    JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                    ret_json.put("error", error_value);
                    ret_json.put("c_nonce", updated_cnonce);
                    ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                    log.error("[{}]: {}",api,"payload nonce is invalid");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                }

                // 驗證 Key proof
                if(alg.equals("ES256"))
                {
                    ECPublicKey publicKey = null;
                    ECKey ec_key = ECKey.parse(holder_pub_key);
                    publicKey = ec_key.toECPublicKey();

                    SignedJWT signedJWT;
                    signedJWT = SignedJWT.parse(jwt);
                    JWSVerifier verifier = new ECDSAVerifier((ECPublicKey) publicKey);
                    if(!signedJWT.verify(verifier))
                    {
                        String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                        error_code = 11016;
                        error_value = "invalid_proof";
                        extra_error_msg = "JWT signature verification failed";
                        JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                        ret_json.put("error", error_value);
                        ret_json.put("c_nonce", updated_cnonce);
                        ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                        log.error("[{}]: {}",api,"JWT signature verification failed");
                        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
                    }
                }
            }

            catch(Exception e1)
            {
                String updated_cnonce = access_token_service.updateCNonceAndTTL(access_token_entity.get(), TOKEN_TTL);
                error_code = 11016;
                error_value = "invalid_proof";
                extra_error_msg = "The JWT format is incorrect.";
                JSONObject ret_json = makeReturnObj(api, error_code, extra_error_msg);
                ret_json.put("error", error_value);
                ret_json.put("c_nonce", updated_cnonce);
                ret_json.put("c_nonce_expires_in", TOKEN_TTL);
                log.error("[{}]: {}",api,"The JWT format is incorrect.");
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ret_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            // 呼叫VC以取得credential
            String db_secret = credential_issuer_config_entity.get(0).getDbSecret();
            if(db_secret==null || db_secret.isEmpty())
            {
                error_code = 12003;
                String errorMessage = "db_secret is empty or null";
                log.error("[{}]: {}",api,"db_secret is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            JSONObject request = new JSONObject();
            request.put("credentialType", credential_identifier);
            request.put("holderUid", crypto_util.GCMDecrypt(access_token_entity.get().getUserID(), db_secret));
            request.put("holderDid", jwt_kid);
            request.put("holderPublicKey", holder_pub_key);
            request.put("nonce", access_token_entity.get().getNonceFromIdp());

            String url = credential_issuer_config_entity.get(0).getCredentialUrl();
            if(url==null || url.isEmpty())
            {
                error_code = 12009;
                String errorMessage = "credential_url is empty or null";
                log.error("[{}]: {}",api,"credential_url is empty or null");
                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(makeReturnObj(api, error_code, errorMessage).toJSONString(JSONStyle.LT_COMPRESS));
            }

            String ret_body = http_service.sendPostRequest(url, request.toJSONString(JSONStyle.LT_COMPRESS));
            log.info("[{}]: post result: [{}]", api, ret_body);
            JSONObject ret_body_json = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(ret_body);
            int resp_code = (int)ret_body_json.get("resp_code");
            if(resp_code == 10000)
            {
                // 變更access_token status
                access_token_service.updateAccessTokenTokenStatus(access_token_entity.get(), CODE_USED);
                String resp_credential = ret_body_json.getAsString("response_body");
                JSONObject resp_credential_json = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(resp_credential);
                log.info("[{}]: return credential: {}", api, resp_credential_json.toJSONString(JSONStyle.LT_COMPRESS));
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp_credential_json.toJSONString(JSONStyle.LT_COMPRESS));
            }

            else
            {
                error_code = resp_code;
                log.error("[{}]: Failed to process request. Response code: {}, Error code: {}, Original response: {}",
                    api, resp_code, error_code, ret_body_json);
                return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
            }
        }

        catch(Exception e)
        {
            log.error("[{}]: {}",api, e.getMessage());
            e.printStackTrace();
            error_code = 11500;
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(makeReturnObj(api, error_code).toJSONString(JSONStyle.LT_COMPRESS));
        }
    }
}
