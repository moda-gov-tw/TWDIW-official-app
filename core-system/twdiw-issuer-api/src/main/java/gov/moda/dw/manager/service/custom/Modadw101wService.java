package gov.moda.dw.manager.service.custom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.repository.custom.CustomAccessTokenRepository;
import gov.moda.dw.manager.service.RegularExpressionService;
import gov.moda.dw.manager.service.SettingService;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.dto.RegularExpressionDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wReqDTO;
import gov.moda.dw.manager.service.dto.custom.FindIssuerResDTO;
import gov.moda.dw.manager.service.dto.custom.GetIssuerDataResDTO;
import gov.moda.dw.manager.service.dto.custom.IvpasVerifyResDTO;
import gov.moda.dw.manager.service.dto.custom.Modadw101ReqDTO;
import gov.moda.dw.manager.service.dto.custom.OrgReqDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyCertAndDidResDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyCertReqDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyDidReqDTO;
import gov.moda.dw.manager.service.dto.custom.callVaResDTO;
import gov.moda.dw.manager.type.AccountActionType;
import gov.moda.dw.manager.type.OrgType;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class Modadw101wService {

    @Value("${issuer.dwissuer-vc-101i}")
    private String DIDregisterURL;

    @Value("${dwfront.dwfront-301i}")
    private String findIssuerURI;

    @Value("${va.verify-url:}")
    private String vaVerifyURL;

    @Value("${issuer.host.name}")
    private String serviceBaseUrl;

    @Value("${ivpas.verify-did}")
    private String ivpasVerfiyUrl;

    @Value("${ivpas.health}")
    private String ivpasHealthUrl;

    @Value("${defaultLogoSmallImgPath}")
    private String defaultLogoSmallImgPath;

    @Autowired
    private CustomOrgService customOrgService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private RegularExpressionService regularExpressionService;

    @Autowired
    private CustomExtendedUserService customExtendedUserService;

    @Autowired
    private CustomAccessTokenRepository customAccessTokenRepository;

    @Autowired
    private Modadw102wService modadw102wService;

    public ResponseDTO<GetIssuerDataResDTO> getInitData(String req) {
        GetIssuerDataResDTO data = new GetIssuerDataResDTO();

        try {
            // 1. 檢查DB中vc的schema內，setting的issuer.did是否有值
            Optional<Setting> setting = settingService.findOne("issuer.did");

            if (!Objects.equals(setting.get().getPropValue(), "")) {

                data.setRegistered(true);

                // 2-a. 若issuer.did有值，則拿issuer.did的值呼叫 dwfront-301i 取得issuer相關資訊
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                String did = setting.get().getPropValue();
                String url = findIssuerURI + "/" + did;
                log.info("findIssuer URL: {}", url);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<FindIssuerResDTO> findIssuerResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    FindIssuerResDTO.class
                );

                if (findIssuerResponse.getBody() == null || findIssuerResponse.getBody().getCode() != 0) {
                    log.error("Modadw101wService-getInitData-front301i查詢DID資訊失敗，錯誤碼: {}, 錯誤訊息: {}", findIssuerResponse.getBody().getCode(), findIssuerResponse.getBody().getMsg());
                    return new ResponseDTO<>(StatusCode.DID_INIT_FIND_ISSUER_ERROR);
                }
                data.setIssuerData(findIssuerResponse.getBody());

                // 2-b. 取得org表中的 501i/ 901i  (已刪除，現已固定為501i)

                // 2-c. 取得setting表中的access token
                Optional<Setting> tokenSetting = settingService.findOne("access_token.frontend.register_did");
                if(tokenSetting.isEmpty()) {
                    log.error("Modadw101wService-getInitData-vc.setting表格沒有access-token:");
                    return new ResponseDTO<>(StatusCode.DID_INIT_FIND_ACCESSTOKEN_ERROR);
                }
                data.setToken(tokenSetting.get().getPropValue());

            } else {

                data.setRegistered(false);

                // 3-a. 若issuer.did沒有值，則去DB中的regular_expression拿正規表示法
                assignRegex(data, "regex_json", 1L);
                assignRegex(data, "regex_xss", 2L);
                assignRegex(data, "regex_sql", 3L);
                assignRegex(data, "regex_url", 101L);
                GetIssuerDataResDTO.Regex regexEnAndNum = new GetIssuerDataResDTO.Regex();
                regexEnAndNum.setRegularExpression("^[a-zA-Z0-9.,&\\s-]+$");
                regexEnAndNum.setErrorMessage("只允許輸入英文大小寫、數字和.,&-");
                data.setRegex_enAndNum(regexEnAndNum);

                // 3-b. 去DB的org table中拿使用者(req.getOrgId())的組織名稱
                OrgDTO org = customOrgService.findOneByOrgId(req);
                if (org == null) {
                    log.error("Modadw101wService-getInitData-查詢使用者對應組織失敗");
                    return new ResponseDTO<>(StatusCode.DID_INIT_FIND_USERORG_ERROR);
                }
                data.setOrgTwName(org.getOrgTwName());

                // 3-c. 拿環境變數的baseUrl
                data.setBaseUrl("https://" + serviceBaseUrl);
            }
            ResponseDTO<GetIssuerDataResDTO> response = new ResponseDTO<>(StatusCode.SUCCESS);
            response.setData(data);
            return response;
        } catch (IllegalStateException ex) {
            log.error("Modadw101wService-getInitData-取得欄位規則發生錯誤，原因: {}", ex.getMessage());
            return new ResponseDTO<>(StatusCode.DID_INIT_FIND_REGULAR_EXPRESSION_ERROR);
        } catch (Exception ex) {
            log.error("Modadw101wService-getInitData-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return new ResponseDTO<>(StatusCode.DID_INIT_EXCEPTION);
        }
    }

    private void assignRegex(GetIssuerDataResDTO data, String fieldName, Long id) {
        Optional<RegularExpressionDTO> opt = regularExpressionService.findOne(id);
        if (opt.isPresent()) {
            GetIssuerDataResDTO.Regex regex = new GetIssuerDataResDTO.Regex();
            regex.setRegularExpression(opt.get().getRegularExpression());
            regex.setErrorMessage(opt.get().getErrorMsg());

            switch (fieldName) {
                case "regex_json" -> data.setRegex_json(regex);
                case "regex_xss" -> data.setRegex_xss(regex);
                case "regex_sql" -> data.setRegex_sql(regex);
                case "regex_enAndNum" -> data.setRegex_enAndNum(regex);
                case "regex_url" -> data.setRegex_url(regex);
            }
        } else {
            throw new IllegalStateException("找不到 regular_expression，ID: "+ id);
        }
    }

    public StatusCode register(Modadw101ReqDTO req) {
        try {
            // 檢查DID註冊欄位
            if (
                !req.getOrg().getTaxId().matches("^[0-9\\-]+$") ||
                !req.getOrg().getName_en().matches("^[a-zA-Z0-9.,&\\s-]+$") ||
                !req.getOrg().getInfo().matches("^(https?:\\/\\/)?([\\w\\-]+\\.)+[\\w\\-]+(\\/[\\w\\-\\.~:\\/?#\\[\\]@!$&'()*+,;=%]*)?$")
            ) {
                log.error("Modadw101wService-register-欄位驗證失敗，req:{}", req);
                return StatusCode.DID_FIELD_VALIDATION_ERROR;
            }

            // 呼叫 issuer-vc-101i註冊DID
            HttpHeaders headers = new HttpHeaders();
            String url = DIDregisterURL;
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> orgBody = new HashMap<>();
            orgBody.put("name", req.getOrg().getName());
            orgBody.put("name_en", req.getOrg().getName_en());
            orgBody.put("info", req.getOrg().getInfo());
            orgBody.put("taxId", req.getOrg().getTaxId());
            orgBody.put("serviceBaseURL", req.getOrg().getServiceBaseURL());
            if (req.getOrg().getX509_subject() != null && req.getOrg().getX509_serial() != null && req.getOrg().getX509_type() != null) {
                orgBody.put("x509_subject", req.getOrg().getX509_subject());
                orgBody.put("x509_serial", req.getOrg().getX509_serial());
                orgBody.put("x509_type", req.getOrg().getX509_type());
            }
            requestBody.put("org", orgBody);
            requestBody.put("p7data", req.getSignature());

            ObjectMapper objectMapper = new ObjectMapper();
            log.info("Modadw101wService-register 呼叫issuer-vc註冊DID，Request Body:{},", objectMapper.writeValueAsString(requestBody));
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode responseBody = objectMapper.readTree(apiResponse.getBody());
            if (!responseBody.has("did")) {
                log.error("Modadw101wService-register issuer-vc註冊DID失敗，錯誤碼: {}, 錯誤訊息: {}", responseBody.get("code").asText(), responseBody.get("message").asText());
                return StatusCode.DID_REGISTER_ERROR;
            }
            log.info("Modadw101wService-register issuer-vc註冊DID成功，Response Body:{},", responseBody);

            // 建立組織
            OrgReqDTO orgReqDTO = new OrgReqDTO();
            orgReqDTO.setOrgId(req.getOrg().getTaxId());
            orgReqDTO.setOrgTwName(req.getOrg().getName());
            orgReqDTO.setOrgEnName(req.getOrg().getName_en());
            orgReqDTO.setVcDataSource(501L);

            // 預設 組織 Logo (正方形 base64)
            String base64LogoSquare = imageToBase64(defaultLogoSmallImgPath);

            // 預設 組織 Logo (長方形 base64)
            String base64LogoRectangle = modadw102wService.getLogoWithNameGenerator(req.getOrg().getName());

            orgReqDTO.setLogoSquare(base64LogoSquare);
            orgReqDTO.setLogoRectangle(base64LogoRectangle);

            StatusCode statusCode = customOrgService.createOrg(orgReqDTO);
            if (!Objects.equals(statusCode.getCode(), StatusCode.SUCCESS.getCode())) {
                log.error("Modadw101wService-register 發生錯誤:{}", StatusCode.DID_CREATE_ORG_ERROR.getMsg());
                return StatusCode.DID_CREATE_ORG_ERROR;
            }

            // 修改預設登入帳號、預設ap帳號為該組織
            List<ExtendedUser> defaultUsers = customExtendedUserService.findAllByOrgId(OrgType.DEFAULT_ORG.getCode());
            for (ExtendedUser user : defaultUsers) {
                Ams311wReqDTO ams311wReqDTO = new Ams311wReqDTO();
                ams311wReqDTO.setLogin(user.getUserId());
                ams311wReqDTO.setUserName(user.getUserName());
                ams311wReqDTO.setOrgId(req.getOrg().getTaxId());
                ams311wReqDTO.setTel(user.getTel());
                ams311wReqDTO.setUserTypeId(user.getUserTypeId());
                ExtendedUser extendedSaveResult = customExtendedUserService.updateExtendedUser(
                    ams311wReqDTO,
                    AccountActionType.UPDATE,
                    user.getState().equals("1")
                );
                if (extendedSaveResult == null) {
                    log.error("Modadw101wService-register 更新使用者組織失敗:{}", StatusCode.DID_UPDATE_ORG_ERROR.getMsg());
                    return StatusCode.DID_UPDATE_ORG_ERROR;
                }
            }

            // 修改預設 Access Token 組織
            List<AccessToken> defaultTokens = customAccessTokenRepository.findAllByOrgId(OrgType.DEFAULT_ORG.getCode());
            for (AccessToken token : defaultTokens) {
                token.setOrgId(req.getOrg().getTaxId());
                token.setOrgName(req.getOrg().getName());
                try {
                    customAccessTokenRepository.save(token);
                } catch (Exception e) {
                    log.error("Modadw101wService-register 更新 access token 失敗:{}", StatusCode.DID_UPDATE_ACCESSTOKEN_ERROR.getMsg());
                    return StatusCode.DID_UPDATE_ACCESSTOKEN_ERROR;
                }
            }

            return StatusCode.SUCCESS;
        } catch (Exception ex) {
            log.error("Modadw101wService-register-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    public ResponseDTO<VerifyCertAndDidResDTO> verifyCert(VerifyCertReqDTO reqDTO, String type) {

        if (vaVerifyURL == null || vaVerifyURL.isEmpty()) {
            return new ResponseDTO<>(StatusCode.SUCCESS);
        }

        // 呼叫VA驗簽憑證
        callVaResDTO certResponse = callVaService(reqDTO, type);
        if(certResponse == null) {
            return new ResponseDTO<>(StatusCode.DID_CERT_VERIFY_EXCEPTION);
        }
        if(certResponse.getResultErrorCode() != 0) {
            log.error("Modadw101wService-verifyCert 憑證驗簽失敗，VA錯誤代碼:{}, VA錯誤訊息:{}, 詳細訊息:{}", certResponse.getResultErrorCode(), certResponse.getResultMSG(), certResponse.getResultException());
            VerifyCertAndDidResDTO verifyCertAndDidResDTO = new VerifyCertAndDidResDTO(String.valueOf(certResponse.getResultErrorCode()), certResponse.getResultMSG(), null);
            return new ResponseDTO<>(verifyCertAndDidResDTO, StatusCode.DID_CERT_VERIFY_ERROR);
        }
        log.info("Modadw101wService-verifyCert 驗證憑證通過");

        return new ResponseDTO<>(StatusCode.SUCCESS);
    }

    public ResponseDTO<VerifyCertAndDidResDTO> verifyDid(VerifyDidReqDTO reqDTO) {

        // 呼叫IVPAS API檢查DID及accesstoken
        IvpasVerifyResDTO ivpasResponse = callIvpasService(reqDTO);
        if(ivpasResponse == null) {
            return new ResponseDTO<>(StatusCode.DID_IVPAS_VERIFY_EXCEPTION);
        }
        if(!Objects.equals(ivpasResponse.getHeader().getReturnCode(), "0000")) {
            log.error("Modadw101wService-verifyDid IVPAS檢查未通過，代碼:{}, 錯誤訊息:{}", ivpasResponse.getHeader().getReturnCode(), ivpasResponse.getHeader().getReturnDesc());
            VerifyCertAndDidResDTO verifyCertAndDidResDTO = new VerifyCertAndDidResDTO(ivpasResponse.getHeader().getReturnCode(), ivpasResponse.getHeader().getReturnDesc(), null);
            return new ResponseDTO<>(verifyCertAndDidResDTO, StatusCode.DID_IVPAS_VERIFY_ERROR);
        }
        log.info("Modadw101wService-verifyDid IVPAS檢查通過");

        // access token 存入 vc.setting DB
        try {
            settingService.update(new Setting("access_token.frontend.register_did", reqDTO.getToken()));
        } catch (Exception ex) {
            return new ResponseDTO<>(StatusCode.DID_UPDATE_ACCESSTOKEN_ERROR);
        }

        VerifyCertAndDidResDTO verifyCertAndDidResDTO = new VerifyCertAndDidResDTO(ivpasResponse.getHeader().getReturnCode(), ivpasResponse.getHeader().getReturnDesc(), ivpasResponse.getBody().getOrgEnName());
        return new ResponseDTO<>(verifyCertAndDidResDTO);
    }

    private callVaResDTO callVaService(VerifyCertReqDTO reqDTO, String type) {
        log.info("Modadw101wService-callVaService 呼叫VA驗證憑證，reqDTO:{},", reqDTO.toString());
        HttpURLConnection con = null;
        try {
            // 整理 request body
            HashMap<String, String> verifyRequestMap = new HashMap<>();

            verifyRequestMap.put("appID", "1004");
            verifyRequestMap.put("certB64", reqDTO.getB64Data());


            ObjectMapper objectMapper = new ObjectMapper();
            String verifyReqJson = objectMapper.writeValueAsString(verifyRequestMap);

            // 呼叫 VA 檢驗憑證
            String url = vaVerifyURL;
            con = (HttpURLConnection) ( new URL(url)).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes("vaRequest=" + URLEncoder.encode(verifyReqJson, StandardCharsets.UTF_8));
                wr.flush();
            }

            StringBuffer buffer = new StringBuffer();

            try (InputStream is = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            }

            return objectMapper.readValue(buffer.toString(), callVaResDTO.class);
        } catch (Exception ex) {
            log.error("Modadw101wService-callVaService-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private IvpasVerifyResDTO callIvpasService(VerifyDidReqDTO reqDTO) {
        log.info("Modadw101wService-callIvpasService 呼叫ivpas檢驗token及DID，reqDTO:{},", reqDTO.toString());
        try {
            String url = ivpasVerfiyUrl;
            HttpHeaders headers = new HttpHeaders();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("accessToken", reqDTO.getToken());
            requestBody.put("certb64", reqDTO.getB64Data());
            requestBody.put("baseUrl", reqDTO.getBaseUrl());
            requestBody.put("orgType", 1);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<IvpasVerifyResDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                IvpasVerifyResDTO.class
            );

            return response.getBody();

        } catch (Exception ex) {
            log.error("Modadw101wService-callIvpasService 發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    public ResponseEntity<String> callFront302i() {
        log.info("Modadw101wService-callFront302i 開始呼叫frontend API");
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = findIssuerURI + "?size=1";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("Modadw101wService-callFront302i frontend API 呼叫結果: {}", response.getStatusCode());
            return response;
        } catch (HttpStatusCodeException ex) {
            log.warn("Modadw101wService-callFront302i 發生 HTTP 錯誤，狀態碼: {}, 原因: {}",
                ex.getStatusCode(), ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.status(ex.getStatusCode()).body("呼叫外部 API 時發生錯誤");
        } catch (Exception ex) {
            log.error("Modadw101wService-callFront302i 發生其他錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("呼叫外部 API 時發生錯誤");
        }
    }

    public ResponseEntity<String> callIvpasTest() {
        log.info("Modadw101wService-callIvpasTest 開始呼叫ivpas API");
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                ivpasHealthUrl,
                HttpMethod.GET,
                null,
                String.class
            );
            log.info("Modadw101wService-callIvpasTest ivpas API 呼叫結果: {}", response.getStatusCode());
            return response;
        } catch (HttpStatusCodeException ex) {
            log.warn("Modadw101wService-callIvpasTest 發生 HTTP 錯誤，狀態碼: {}, 原因: {}",
                ex.getStatusCode(), ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.status(ex.getStatusCode()).body("呼叫外部 API 時發生錯誤");
        } catch (Exception ex) {
            log.error("Modadw101wService-callIvpasTest 發生其他錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("呼叫外部 API 時發生錯誤");
        }
    }

    public String imageToBase64(String classpathLocation) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception ex) {
            log.warn("Modadw101wService-imageToBase64-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }
}
