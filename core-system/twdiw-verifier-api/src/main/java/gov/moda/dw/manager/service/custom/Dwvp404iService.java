package gov.moda.dw.manager.service.custom;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.OrgKeySetting;
import gov.moda.dw.manager.repository.custom.CustomOrgKeySettingRepository;
import gov.moda.dw.manager.service.custom.common.ECCService;
import gov.moda.dw.manager.service.custom.common.HMACService;
import gov.moda.dw.manager.service.custom.common.TOTPService;
import gov.moda.dw.manager.service.dto.custom.Dwvp404iReqDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp404iResDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvp404iService {

    private final CustomOrgKeySettingRepository customOrgKeySettingRepository;
    private final ECCService eccService;
    private final TOTPService totpService;
    private final HMACService hmacService;
    private final ObjectMapper objectMapper;

    /**
     * 解密資料
     *
     * @param request 解密請求
     * @return 解密結果
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Dwvp404iResDTO getDecryptionData(Dwvp404iReqDTO request) {
        log.debug("Request to decrypt data");
        try {
            // 驗證必要參數
            validateRequest(request);

            // 根據 keyId 查詢金鑰設定
            List<OrgKeySetting> orgKeySettings = customOrgKeySettingRepository
                    .findByKeyIdAndIsActiveTrue(request.getKeyId());

            if (orgKeySettings.isEmpty()) {
                throw new DWException(StatusCode.DWVP_ORG_KEY_SETTING_NOT_FOUND);
            }

            OrgKeySetting orgKeySetting = orgKeySettings.get(0);

            // 使用私鑰解密資料
            final String prvKey = orgKeySetting.getPrivateKey();
            if (StringUtils.isBlank(prvKey)) {
                throw new DWException(StatusCode.DWVP_PRIVATE_KEY_BLANK);
            }

            // 將解密後的資料轉換為 JSON 物件
            final String orgData = eccService.decrypt(request.getData(), prvKey);

            // 解析 JSON 資料
            JsonNode dataNode = objectMapper.readTree(orgData);
            log.info("JSON 解析完成");

            // 2. 驗證必要欄位是否存在
            log.info("開始驗證必要欄位");
            if (!validateRequiredFields(dataNode)) {
                throw new DWException(StatusCode.DWVP_DECRYPTION_FAILED);
            }
            log.info("必要欄位驗證通過");

            // 取得各欄位值
            String totp = dataNode.path("totp").asText();

            // 3. 驗證 TOTP 碼
            if (!totpService.verifyTOTP(totp, orgKeySetting.getTotpKey())) {
                throw new DWException(StatusCode.DWVP_TOTP_VERIFICATION_FAILED);
            }
            log.info("TOTP 驗證通過");

            // 4. 驗證 HMAC 值
            String calculatedHmac = hmacService.calculateHMAC(orgData, orgKeySetting.getHmacKey());

            if (!calculatedHmac.equals(request.getHmac())) {
                log.error("HMAC 驗證失敗");
                throw new DWException(StatusCode.DWVP_HMAC_VERIFICATION_FAILED);
            }
            log.info("HMAC 驗證通過");

            // 5. 所有驗證都通過，回傳解密後的資料
            log.info("所有驗證都通過，準備回傳結果");

            // 建立回應
            Dwvp404iResDTO response = new Dwvp404iResDTO();
            response.setDecryptionData(dataNode);
            return response;
        } catch (DWException e) {
            log.warn("解密資料失敗 : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("解密資料失敗 : {}", e.getMessage());
            throw new DWException(StatusCode.DWVP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 驗證請求參數
     *
     * @param request 請求物件
     */
    private void validateRequest(Dwvp404iReqDTO request) {
        if (StringUtils.isBlank(request.getTag())) {
            throw new DWException(StatusCode.DWVP_TAG_IS_REQUIRED);
        }
        if (StringUtils.isBlank(request.getData())) {
            throw new DWException(StatusCode.DWVP_DATA_IS_REQUIRED);
        }
        if (StringUtils.isBlank(request.getHmac())) {
            throw new DWException(StatusCode.DWVP_HMAC_IS_REQUIRED);
        }
        if (StringUtils.isBlank(request.getKeyId())) {
            throw new DWException(StatusCode.DWVP_KEY_ID_IS_REQUIRED);
        }
    }

    /**
     * 驗證解密後的資料是否包含所有必要欄位
     * 
     * @param dataNode JSON 資料節點
     * @return 如果所有必要欄位都存在且不為空則回傳 true，否則回傳 false
     */
    private boolean validateRequiredFields(JsonNode dataNode) {
        return dataNode != null &&
               dataNode.has("totp") &&
               dataNode.get("totp").asText() != null;
    }
}
