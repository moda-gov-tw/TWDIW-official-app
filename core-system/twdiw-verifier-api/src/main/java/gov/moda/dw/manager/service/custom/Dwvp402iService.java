package gov.moda.dw.manager.service.custom;

import java.awt.image.BufferedImage;
import java.security.PublicKey;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;

import gov.moda.dw.manager.config.AppConfiguration;
import gov.moda.dw.manager.domain.OrgKeySetting;
import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.domain.VPVerifyResult;
import gov.moda.dw.manager.repository.custom.CustomOrgKeySettingRepository;
import gov.moda.dw.manager.repository.custom.CustomVPItemRepository;
import gov.moda.dw.manager.repository.custom.CustomVPVerifyResultRepository;
import gov.moda.dw.manager.service.custom.common.DidKeyService;
import gov.moda.dw.manager.service.custom.common.ECCService;
import gov.moda.dw.manager.service.custom.common.HMACService;
import gov.moda.dw.manager.service.custom.common.QRCodeService;
import gov.moda.dw.manager.service.custom.common.TOTPService;
import gov.moda.dw.manager.service.dto.custom.Dwvp402iReqDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp402iResDTO;
import gov.moda.dw.manager.service.dto.custom.EncryptedDataDTO;
import gov.moda.dw.manager.service.dto.custom.JwtPayloadDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemGroupReqDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemGroupVcDataFieldReqDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemGroupVcDataReqDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyResultDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyResultDTO.VerifyResultDataClaimDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyResultDTO.VerifyResultDataDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvp402iService {

    private final DidKeyService didKeyService;

    private final QRCodeService qrCodeService;

    private final TOTPService totpService;

    private final HMACService hmacService;

    private final ECCService eccService;

    private final DwSandBoxVP401WService dwSandBoxVP401WService;

    private final ObjectMapper objectMapper;

    private final AppConfiguration appConfiguration;

    private final CustomVPVerifyResultRepository customVPVerifyResultRepository;

    private final CustomVPItemRepository customVPItemRepository;

    private final CustomOrgKeySettingRepository customOrgKeySettingRepository;

    /**
     * 取得加密資料 QR Code
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public Dwvp402iResDTO getEncryptionData(Dwvp402iReqDTO request) {
        log.debug("Request to get encryption data");

        String jwt = request.getJwt();
        if (StringUtils.isBlank(jwt)) {
            throw new DWException(StatusCode.DWVP_JWT_NOT_FOUND);
        }

        // 產生 QrCode 內容
        String qrCodeContent = this.generateQrCodeContent(jwt);

        // 產生帶有 logo QRCode
        String base64ImgPrefix = "data:image/png;base64,";
        String qrCode = "";
        try {
            // 讀取 TWDIW logo 圖片
            ClassPathResource logoResource = new ClassPathResource("img/twdiw.png");
            BufferedImage logoImage = ImageIO.read(logoResource.getInputStream());
            qrCode = base64ImgPrefix + qrCodeService.generateQRCodeWithLogoSpace(qrCodeContent, logoImage);
        } catch (Exception e) {
            log.warn("{} : {}", StatusCode.DWVP_GENERATOR_ENCRYPT_QRCODE_CONTENT.getMsg(), e.getMessage());
            throw new DWException(StatusCode.DWVP_GENERATOR_ENCRYPT_QRCODE_CONTENT);
        }

        // 建立 response
        Dwvp402iResDTO res = new Dwvp402iResDTO();
        res.setQrcode(qrCode);
        res.setTotptimeout(String.valueOf(appConfiguration.getTotpTimeout()));
        return res;
    }

    /**
     * 驗證 JWT 並產生 QrCode 內容
     * 
     * @param jwt
     * @return
     */
    private String generateQrCodeContent(String jwt) {
        log.info("驗證 JWT 並產生 QrCode 內容");
        // step1 解析 JWT 內容
        SignedJWT signedJWT = didKeyService.parseSignedJWT(jwt);
        String jwtPayload = signedJWT.getPayload().toString();
        JwtPayloadDTO jwtPayloadDTO = this.jsonToObject(jwtPayload, new TypeReference<>() {
        });
        String transactionId = jwtPayloadDTO.getTransactionId();

        // step2 查詢 VP 驗證結果，並檢查是否過期
        VPVerifyResult vpVerifyResult = customVPVerifyResultRepository.findByTransactionId(transactionId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new DWException(StatusCode.DWVP_VP_VERIFY_RESULT_NOT_FOUND));

        Instant createdAt = vpVerifyResult.getCrDatetime();
        Instant expireAt = createdAt.plusSeconds(appConfiguration.getJwtTimeout());
        if (Instant.now().isAfter(expireAt)) {
            throw new DWException(StatusCode.DWVP_JWT_TIMEOUT);
        }

        // step3 呼叫 dwverifier-oidvp-301i 取得驗證結果資料與 Holder DID
        JsonNode verifyResultJson = dwSandBoxVP401WService.getPayloadResult(transactionId, null);
        VerifyResultDTO verifyResult = this.jsonNodeToObject(verifyResultJson, VerifyResultDTO.class);
        String holderDid = verifyResult.getHolderDid();

        // step4 使用 holderDid 對 jwt 進行驗簽
        this.verifyJwt(holderDid, jwt);

        // step5 產生 QrCode 內容
        Optional<VPItem> vpItemOpt = customVPItemRepository.findById(vpVerifyResult.getVpItemId());
        if (vpItemOpt.isEmpty()) {
            throw new DWException(StatusCode.DWVP_VP_ITEM_NOT_FOUND);
        }
        VPItem vpItem = vpItemOpt.get();

        // 判斷模組加密 QrCode 內容
        if (Boolean.FALSE.equals(vpItem.getIsEncryptEnabled())) {
            // 產生明碼 QrCode 內容
            return this.generatorPlainQrCodeContent(vpItem, verifyResult);
        } else {
            // 產生加密 QrCode 內容
            return this.generatorEncryptQrCodeContent(vpItem, verifyResult);
        }
    }

    /**
     * 產生加密 QrCode 內容
     * 
     * @param vpItemId
     * @param verifyResult
     * @return
     */
    private String generatorEncryptQrCodeContent(VPItem vpItem, VerifyResultDTO verifyResult) {
        log.info("產生加密 QrCode 內容");
        try {
            String businessId = vpItem.getBusinessId();

            // 查詢 orgKeySetting by businessId(僅能有一筆)
            List<OrgKeySetting> orgKeySettingList = customOrgKeySettingRepository.findByOrgIdAndIsActiveTrue(businessId);
            if (orgKeySettingList.size() > 1) {
                throw new DWException(StatusCode.DWVP_ORG_KEY_SETTING_MULTIPLE_ACTIVED_ERROR);
            }

            OrgKeySetting orgKeySetting = orgKeySettingList.stream()
                    .findFirst().orElseThrow(() -> new DWException(StatusCode.DWVP_ORG_KEY_SETTING_NOT_FOUND));
            String totpKey = orgKeySetting.getTotpKey();
            String hmacKey = orgKeySetting.getHmacKey();
            String publicKey = orgKeySetting.getPublicKey();
            String keyId = orgKeySetting.getKeyId();

            // 建立明碼資料
            Map<String, Object> plainDataMap = this.buildPlainDataMap(vpItem.getGroupInfo(), verifyResult);

            // 產生 TOTP 碼
            String totp = totpService.generateTOTP(totpKey, 0);
            plainDataMap.put("totp", totp);
            log.info("totp success");

            // 使用 明碼資料和 TOTP 計算 HMAC 值
            String encryptKey = objectMapper.writeValueAsString(plainDataMap);
            String calculatedHmac = hmacService.calculateHMAC(encryptKey, hmacKey);
            log.info("calculatedHmac success");

            // 將明碼資料轉換為 JSON 字串
            String plainDataJson = objectMapper.writeValueAsString(plainDataMap);

            // 使用 公鑰加密資料
            String encryptedData = eccService.encrypt(plainDataJson, publicKey);

            // 準備加密後的資料結構
            EncryptedDataDTO encryptedDataDTO = new EncryptedDataDTO();
            encryptedDataDTO.setTag(vpItem.getTag());
            encryptedDataDTO.setData(encryptedData);
            encryptedDataDTO.setHmac(calculatedHmac);
            encryptedDataDTO.setKeyId(keyId);

            // 加密 QrCode 內容，並移除所有空白符號
            return objectMapper.writeValueAsString(encryptedDataDTO).replaceAll("\\s+", "");
        } catch (DWException e) {
            throw e;
        } catch (Exception e) {
            log.warn("{} : {}", StatusCode.DWVP_GENERATOR_ENCRYPT_QRCODE_CONTENT.getMsg(), e.getMessage());
            throw new DWException(StatusCode.DWVP_GENERATOR_ENCRYPT_QRCODE_CONTENT);
        }
    }

    /**
     * 產生明碼 QrCode 內容
     * 
     * @param vpItem
     * @param verifyResult
     */
    private String generatorPlainQrCodeContent(VPItem vpItem, VerifyResultDTO verifyResult) {
        log.info("產生明碼 QrCode 內容");
        try {
            String result = null;

            // 如有多個欄位回傳 JSON
            long claimsCount = verifyResult.getData().stream()
                    .filter(d -> null != d.getClaims())
                    .mapToLong(d -> d.getClaims().size())
                    .sum();
            if (claimsCount > 1) {
                // 建立明碼資料
                Map<String, Object> plainDataMap = this.buildPlainDataMap(vpItem.getGroupInfo(), verifyResult);
                // 將明碼資料轉換為 JSON 字串
                result = objectMapper.writeValueAsString(plainDataMap);
            }
            // 如只有一個欄位回傳字串
            else {
                result = verifyResult.getData().stream()
                        .flatMap(data -> data.getClaims().stream().map(claim -> claim.getValue())).findFirst()
                        .orElse(null);
            }

            // 移除所有空白符號
            if (StringUtils.isNotBlank(result)) {
                result = result.replaceAll("\\s+", "");
            }

            return result;
        } catch (DWException e) {
            throw e;
        } catch (Exception e) {
            log.warn("{} : {}", StatusCode.DWVP_GENERATOR_QRCODE_CONTENT.getMsg(), e.getMessage());
            throw new DWException(StatusCode.DWVP_GENERATOR_QRCODE_CONTENT);
        }
    }

    /**
     * 建立明碼資料 Map (plainDataMap)，用來存放經過驗證並且勾選的欄位值。
     *
     * 流程：
     * 1. 解析前端傳入的 JSON → 群組資料
     * 2. 建立 credentialType → claims 的快取 (避免重複遍歷 verifyResult)
     * 3. 遍歷群組資料，逐一處理每個 vcData
     * 4. 將符合條件 (isTicked = true) 且驗證結果存在的欄位，放入 plainDataMap
     *
     * @param groupInfoJson  前端上傳的群組資料 JSON，每組包含多個 VC 資料 (vcData)
     * @param verifyResult   驗證結果，內含多筆 credential 對應的 claims
     * @return Map<customFieldName, claimValue> 經過驗證並勾選的明文資料
     */
    private Map<String, Object> buildPlainDataMap(String groupInfoJson, VerifyResultDTO verifyResult) {
        // Step 1. 解析群組資料 JSON
        List<UpsertVPItemGroupReqDTO> groupInfoList = this.jsonToObjectThrowException(groupInfoJson,
                new TypeReference<>() {
                });

        // Step 2. 建立 credentialType → claims 的快取 Map
        Map<String, List<VerifyResultDataClaimDTO>> credentialClaimsMap = this.buildCredentialClaimsMap(verifyResult);

        // Step 3. 準備輸出結果 (LinkedHashMap 可保持欄位插入順序)
        Map<String, Object> plainDataMap = new LinkedHashMap<>();

        // Step 4. 遍歷群組資料，逐一處理 vcData
        for (UpsertVPItemGroupReqDTO group : groupInfoList) {
            for (UpsertVPItemGroupVcDataReqDTO vcData : group.getVcDatas()) {
                this.processVcData(vcData, credentialClaimsMap, plainDataMap);
            }
        }

        return plainDataMap;
    }

    /**
     * 建立 credentialType → claims 的快取 Map
     *
     * 好處：
     * - credentialType (businessId_serialNo) 可以快速定位對應的 claims
     * - 避免每次處理 vcData 都重新遍歷 verifyResult
     *
     * @param verifyResult 驗證結果
     * @return Map<credentialType, claims>
     */
    private Map<String, List<VerifyResultDataClaimDTO>> buildCredentialClaimsMap(VerifyResultDTO verifyResult) {
        return verifyResult.getData().stream()
                .collect(Collectors.groupingBy(
                        VerifyResultDataDTO::getCredentialType,                                  // key: credentialType
                        LinkedHashMap::new,                                                      // 保留原始順序
                        Collectors.flatMapping(v -> v.getClaims().stream(), Collectors.toList()) // value: 若 credentialType 重複，保留全部
                    ));
    }

    /**
     * 處理單一 vcData，將符合條件的欄位放入 plainDataMap
     *
     * 條件：
     * 1. 欄位有被勾選 (isTicked = true)
     * 2. customFieldName 不可為空
     * 3. 驗證結果中有對應的 ename
     *
     * @param vcData              當前的 VC 資料
     * @param credentialClaimsMap credentialType 對應的 claims 快取
     * @param plainDataMap        輸出結果 Map
     */
    private void processVcData(UpsertVPItemGroupVcDataReqDTO vcData,
            Map<String, List<VerifyResultDataClaimDTO>> credentialClaimsMap, Map<String, Object> plainDataMap) {
        // 計算 credentialType (唯一 key)
        String credentialType = vcData.getBusinessId() + "_" + vcData.getSerialNo();

        // 取出驗證結果 claims
        List<VerifyResultDataClaimDTO> claims = credentialClaimsMap.get(credentialType);
        if (claims == null) {
            return; // 沒找到 → 跳過
        }

        // claims list → Map (key: ename, value: claim value)
        Map<String, Object> claimMap = claims.stream()
                .collect(Collectors.toMap(
                        VerifyResultDataClaimDTO::getEname, // key: ename
                        VerifyResultDataClaimDTO::getValue, // value: value
                        (a, b) -> b,                        // 若 ename 重複，保留最後一個
                        LinkedHashMap::new                  // 保留原始順序
                ));
        // 加入明碼資料
        plainDataMap.putAll(claimMap);

        // 遍歷 vcFields
        for (UpsertVPItemGroupVcDataFieldReqDTO vcField : vcData.getVcFields()) {
            // 欄位有被勾選 (isTicked = true)
            if (Boolean.TRUE.equals(vcField.getIsTicked())) {
                // customFieldName 為必填，否則拋出例外
                if (StringUtils.isBlank(vcField.getCustomFieldName())) {
                    throw new DWException(StatusCode.DWVP_CUSTOM_FIELD_NAME_NOT_VALID);
                }

                // 如果驗證結果有對應的 ename → 放入 plainDataMap
                if (claimMap.containsKey(vcField.getEname())) {
                    // 移除現有相同 key 明碼資料，並以輸入自定義欄位名稱為 key
                    plainDataMap.remove(vcField.getEname());
                    plainDataMap.put(vcField.getCustomFieldName(), claimMap.get(vcField.getEname()));
                }
            }
        }
    }

    /**
     * 使用 holderDid 對 jwt 進行驗簽
     * 
     * @param holderDid
     * @param jwt
     * @return
     */
    private void verifyJwt(String holderDid, String jwt) {
        // Jwt 驗簽開啟才執行
        if (!appConfiguration.isSwitchVerifyJwt()) {
            return;
        }

        log.info("使用 holderDid 對 jwt 進行驗簽");
        try {
            // 從 DID Key 提取公鑰
            PublicKey publicKey = didKeyService.extractPublicKeyFromDid(holderDid);

            // 驗證 JWT 簽章
            boolean isValid = didKeyService.verify(jwt, publicKey);
            if (!isValid) {
                throw new DWException(StatusCode.DWVP_JWT_NOT_VALID);
            }
        } catch (DWException e) {
            throw e;
        } catch (Exception e) {
            log.warn("{} : {}", StatusCode.DWVP_JWT_NOT_VALID.getMsg(), e.getMessage());
            throw new DWException(StatusCode.DWVP_JWT_NOT_VALID);
        }
    }

    /**
     * 將 json 轉為 T 物件
     * 
     * @param <T>
     * @param json
     * @param typeRef
     * @return
     */
    private <T> T jsonToObjectThrowException(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.warn("jsonToObjectThrowException : {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 將 json 轉為 T 物件
     * 
     * @param <T>
     * @param json
     * @param typeRef
     * @return
     */
    private <T> T jsonToObject(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.warn("{} : {}", StatusCode.DWVP_JWT_PAYLOAD_PARSE_ERROR.getMsg(), e.getMessage());
            throw new DWException(StatusCode.DWVP_JWT_PAYLOAD_PARSE_ERROR);
        }
    }

    /**
     * 將 json 轉為 T 物件
     * 
     * @param <T>
     * @param jsonNode
     * @param clazz
     * @return
     */
    private <T> T jsonNodeToObject(JsonNode jsonNode, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            log.warn("{} : {}", StatusCode.DWVP_VERIFY_RESULT_PARSE_ERROR.getMsg(), e.getMessage());
            throw new DWException(StatusCode.DWVP_VERIFY_RESULT_PARSE_ERROR);
        }
    }

}
