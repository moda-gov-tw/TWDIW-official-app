package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.domain.OrgKeySetting;
import gov.moda.dw.manager.repository.custom.CustomOidvpConfigRepository;
import gov.moda.dw.manager.repository.custom.CustomOrgKeySettingRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.custom.common.ECCService;
import gov.moda.dw.manager.service.custom.common.HMACService;
import gov.moda.dw.manager.service.custom.common.TOTPService;
import gov.moda.dw.manager.service.dto.custom.CustomOrgKeySettingDTO;
import gov.moda.dw.manager.service.dto.custom.FindVerifierResDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Dwvp403iService {

    private final CustomOrgKeySettingRepository customOrgKeySettingRepository;

    private final CustomOidvpConfigRepository customOidvpConfigRepository;

    private final RemoteApiService remoteApiService;

    private final ECCService eccService;

    private final TOTPService totpService;

    private final HMACService hmacService;

    /**
     * 設定金鑰
     *
     * @param request 設定金鑰請求
     * @return 設定結果
     * @throws Exception
     */
    @Transactional
    public void createOrgSetting(CustomOrgKeySettingDTO request) {
        log.debug("Request to add key");

        // 驗證必要參數
        validateRequest(request);
        
        Optional<OidvpConfig> oidvpConfig = customOidvpConfigRepository.findByPropertyKey("verifier.did");
        if (oidvpConfig.isEmpty()) {
            throw new DWException(StatusCode.DWVP_DID_NOT_REGISTERED);
        }

        FindVerifierResDTO findVerifierResDTO = remoteApiService.getDIDInfoById(oidvpConfig.get().getPropertyValue());
        if (findVerifierResDTO != null && findVerifierResDTO.getData() == null) {
            throw new DWException(StatusCode.DID_INIT_FIND_ISSUER_ERROR);
        }

        String loginUserOrgId = SecurityUtils.getJwtUserObject().get(0).getOrgId();

        // 檢查是否已存在相同的 orgId 和 keyId 組合
        Optional<OrgKeySetting> existingKey = customOrgKeySettingRepository.findByOrgIdAndKeyId(loginUserOrgId, request.getKeyId());

        if (existingKey.isPresent()) {
            throw new DWException(StatusCode.DWVP_IS_PRESENT);
        }

        //找出同組織其它有啟用的金鑰
        List<OrgKeySetting> sameOrgList = customOrgKeySettingRepository.findByOrgIdAndIsActiveTrue(loginUserOrgId);
        OrgKeySetting orgKeySetting = new OrgKeySetting();
        Boolean requestIsActive = request.getIsActive();
        if (!sameOrgList.isEmpty()) {
            //如果有其它啟用金鑰
            if(request.getIsActive()){
                //然後使用者要強制打開這組金鑰 要把其它關起來
                for( OrgKeySetting key : sameOrgList ){
                    key.setIsActive(false);
                    key.setUpDatetime(Instant.now());
                    customOrgKeySettingRepository.save(key);
                }
            }
        }else {
            //如果這是第一筆 強制啟用
            requestIsActive = true;
        }

        // 建立新的金鑰設定
        orgKeySetting.setOrgId(loginUserOrgId);
        orgKeySetting.setKeyId(request.getKeyId());
        orgKeySetting.setDescription(request.getDescription());
        orgKeySetting.setPublicKey(request.getPubKey());
        orgKeySetting.setPrivateKey(request.getPrvKey());
        orgKeySetting.setTotpKey(request.getTotpKey());
        orgKeySetting.setHmacKey(request.getHmacKey());
        orgKeySetting.setIsActive(requestIsActive);
        orgKeySetting.setCrDatetime(Instant.now());
        orgKeySetting.setUpDatetime(Instant.now());
        // 儲存金鑰設定
        customOrgKeySettingRepository.save(orgKeySetting);
    }

    /**
     * 驗證請求參數
     *
     * @param request 請求參數
     * @throws DWException 當參數驗證失敗時拋出異常
     */
    private void validateRequest(CustomOrgKeySettingDTO request) throws DWException {

        if (StringUtils.isBlank(request.getKeyId())) {
            throw new DWException(StatusCode.DWVP_KEY_ID_NOT_EXISTS);
        }

        if (StringUtils.isBlank(request.getPubKey())) {
            throw new DWException(StatusCode.DWVP_PUBLIC_KEY_NOT_EXISTS);
        } else {
            if (!eccService.validKey(request.getPubKey(), true)) {
                throw new DWException(StatusCode.DWVP_PUBLIC_KEY_NOT_VALID);
            }
        }

        if (StringUtils.isNotBlank(request.getPrvKey())) {
            if (!eccService.validKey(request.getPrvKey(), false)) {
                throw new DWException(StatusCode.DWVP_PRIVATE_KEY_NOT_VALID);
            }
        }

        if (StringUtils.isBlank(request.getTotpKey())) {
            throw new DWException(StatusCode.DWVP_TOTP_KEY_NOT_EXISTS);
        } else {
            if (!totpService.validTOTPKey(request.getTotpKey())) {
                throw new DWException(StatusCode.DWVP_TOTP_KEY_NOT_VALID);
            }
        }

        if (StringUtils.isBlank(request.getHmacKey())) {
            throw new DWException(StatusCode.DWVP_HMAC_KEY_NOT_EXISTS);
        } else {
            if (!hmacService.validHMACKey(request.getHmacKey())) {
                throw new DWException(StatusCode.DWVP_HMAC_KEY_NOT_VALID);
            }
        }

        if (request.getIsActive() == null) {
            throw new DWException(StatusCode.DWVP_IS_ACTIVE_NOT_EXISTS);
        }

    }
}
