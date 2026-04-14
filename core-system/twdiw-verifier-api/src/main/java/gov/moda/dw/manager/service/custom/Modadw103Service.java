package gov.moda.dw.manager.service.custom;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.OrgKeySetting;
import gov.moda.dw.manager.repository.custom.CustomOidvpConfigRepository;
import gov.moda.dw.manager.repository.custom.CustomOrgKeySettingRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.OrgKeySettingQueryService;
import gov.moda.dw.manager.service.criteria.OrgKeySettingCriteria;
import gov.moda.dw.manager.service.custom.common.ECCService;
import gov.moda.dw.manager.service.custom.common.HMACService;
import gov.moda.dw.manager.service.custom.common.TOTPService;
import gov.moda.dw.manager.service.dto.OrgKeySettingDTO;
import gov.moda.dw.manager.service.dto.custom.CustomOrgKeySettingDTO;
import gov.moda.dw.manager.service.dto.custom.OrgKeyDetailResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgKeyGenerateResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgKeySettingResDTO;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.service.filter.StringFilter;

/**
 * 新增 組織金鑰管理
 */
@Service
@Slf4j
public class Modadw103Service {

    // 取得 金鑰代碼
    private final CustomOrgKeySettingRepository customOrgKeySettingRepository;

    private final OrgKeySettingQueryService orgKeySettingQueryService;

    private final ECCService eccService;

    private final HMACService hmacService;

    private final TOTPService totpService;

    /**
     * 建構子
     * 
     * @param customOidvpConfigRepository   取得 註冊 DID 組織代碼
     * @param customOrgKeySettingRepository 取得 金鑰代碼
     * @param remoteApiService              呼叫 dwfront-301i url 取得 taxId
     */
    public Modadw103Service(CustomOidvpConfigRepository customOidvpConfigRepository,
            CustomOrgKeySettingRepository customOrgKeySettingRepository, RemoteApiService remoteApiService,
            OrgKeySettingQueryService orgKeySettingQueryService, ECCService eccService, TOTPService totpService,
            HMACService hmacService) {
        this.customOrgKeySettingRepository = customOrgKeySettingRepository;
        this.orgKeySettingQueryService = orgKeySettingQueryService;
        this.eccService = eccService;
        this.hmacService = hmacService;
        this.totpService = totpService;
    }

    /**
     * 查詢所有金鑰
     * 
     * @param criteria
     * @param pageable
     * @return
     */
    public Page<OrgKeySettingResDTO> getAllOrgKeySetting(OrgKeySettingCriteria criteria, Pageable pageable) {
        // 僅能看到自己組織的金鑰
        StringFilter sf = new StringFilter();
        sf.setEquals(SecurityUtils.getJwtUserObject().get(0).getOrgId());
        criteria.setOrgId(sf);

        Page<OrgKeySettingDTO> orgKeySettingDtoPage = orgKeySettingQueryService.findByCriteria(criteria, pageable);

        List<OrgKeySettingResDTO> resultList = new ArrayList<>();

        for (OrgKeySettingDTO orgKeySettingDTO : orgKeySettingDtoPage.getContent()) {
            OrgKeySettingResDTO orgKeySettingResDTO = new OrgKeySettingResDTO();
            orgKeySettingResDTO.setId(orgKeySettingDTO.getId());
            orgKeySettingResDTO.setOrgId(orgKeySettingDTO.getOrgId());
            orgKeySettingResDTO.setKeyId(orgKeySettingDTO.getKeyId());
            orgKeySettingResDTO.setDescription(orgKeySettingDTO.getDescription());
            orgKeySettingResDTO.setIsActive(orgKeySettingDTO.getIsActive());
            orgKeySettingResDTO.setCrDatetime(orgKeySettingDTO.getCrDatetime());
            orgKeySettingResDTO.setUpDatetime(orgKeySettingDTO.getUpDatetime());
            resultList.add(orgKeySettingResDTO);
        }
        return new PageImpl<>(resultList, pageable, orgKeySettingDtoPage.getTotalElements());
    }

    /**
     * 查詢金鑰詳情 By id
     * 
     * @param id
     * @return
     */
    public OrgKeyDetailResDTO getKeyItem(Long id) {
        log.debug("Request to get org key detail: {}", id);
        // 查詢指定 ID 的金鑰
        OrgKeySetting keyItem = customOrgKeySettingRepository.findById(id).orElseThrow(() -> {
            throw new BadRequestAlertException("10304", "id", "key not found");
        });

        return toResDTO(keyItem);
    }

    /**
     * 刪除金鑰 By id
     * 
     * @param id
     */
    public void delete(Long id) {
        log.debug("Request to delete org key");
        // 查詢 指定 ID 的金鑰
        OrgKeySetting keyItem = customOrgKeySettingRepository.findById(id).orElseThrow(() -> {
            throw new BadRequestAlertException("10304", "id", "key not found");
        });

        // 查詢 是否已啟用，已啟用不可刪除
        if (keyItem.getIsActive()) {
            throw new BadRequestAlertException("10305", "isActive", "key is active cannot delete");
        }
        customOrgKeySettingRepository.deleteById(id);
    }

    /**
     * 產生金鑰
     * 
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     */
    public OrgKeyGenerateResDTO generateKey()
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        OrgKeyGenerateResDTO result = new OrgKeyGenerateResDTO();

        String[] keyPair = eccService.generateKeyPair();
        result.setPublicKey(keyPair[0]);
        result.setPrivateKey(keyPair[1]);

        Map<String, String> hmacKeyMap = hmacService.generateHMACKey();
        result.setHmacKey(hmacKeyMap.get("hmacKey"));

        Map<String, String> totpKeyMap = totpService.generateTOTPKey();
        result.setTotpKey(totpKeyMap.get("totpKey"));

        return result;
    }

    /**
     * 檢核金鑰
     * 
     * @param customOrgKeySettingDTO
     * @return
     */
    public boolean validateKey(CustomOrgKeySettingDTO customOrgKeySettingDTO) {

        boolean isPublicKeyValidate = ECCService.validKey(customOrgKeySettingDTO.getPubKey(), true);

        // 有填私鑰才檢核
        if (customOrgKeySettingDTO.getPrvKey() != null
                && !customOrgKeySettingDTO.getPrvKey().trim().isEmpty()) {
            boolean isPrivateKeyValidate = ECCService.validKey(customOrgKeySettingDTO.getPrvKey(), false);
            if (!isPrivateKeyValidate) {
                throw new BadRequestAlertException("10397", "privateKey", "privateKey error");
            }
        }

        boolean isHMACKeyValidate = HMACService.validHMACKey(customOrgKeySettingDTO.getHmacKey());
        boolean isTOTPKeyValidate = TOTPService.validTOTPKey(customOrgKeySettingDTO.getTotpKey());

        if (!isPublicKeyValidate) {
            throw new BadRequestAlertException("10396", "publicKey", "publicKey error");
        }

        if (!isHMACKeyValidate) {
            throw new BadRequestAlertException("10398", "hmacKey", "hmacKey error");
        }

        if (!isTOTPKeyValidate) {
            throw new BadRequestAlertException("10399", "totpKey", "totpKey error");
        }

        return true;
    }

    /**
     * 啟用金鑰, 且檢核僅一筆啟用
     * 
     * @param id
     * @return
     */
    public void setOrgKeyActive(Long id) {
        log.debug("Request to setOrgKeyActive");
        String loginUserOrgId = SecurityUtils.getJwtUserObject().get(0).getOrgId();

        // 該組織的金鑰筆數
        Long keyCount = customOrgKeySettingRepository.countByOrgId(loginUserOrgId);

        // 1. 查詢指定 ID 的金鑰
        OrgKeySetting keyToActivate = customOrgKeySettingRepository.findByIdAndOrgId(id, loginUserOrgId)
                .orElseThrow(() -> {
                    throw new BadRequestAlertException("10304", "id", "key not found");
                });

        // 2. 檢查啟用狀態，應為 未啟用
        if (Boolean.TRUE.equals(keyToActivate.getIsActive())) {
            throw new BadRequestAlertException("10393", "id", "key already activated");
        }

        // 3. 查詢目前已啟用的金鑰(isActive = true), 應僅有一筆, 設為未啟用
        // 建立第一筆金鑰時跳過此檢核
        if (keyCount > 1) {
            List<OrgKeySetting> currentActiveKeys = customOrgKeySettingRepository
                    .findByOrgIdAndIsActiveTrue(loginUserOrgId);

            if (currentActiveKeys.isEmpty()) {
                throw new BadRequestAlertException("10394", "orgId", "active key not found");
            }

            if (currentActiveKeys.size() > 1) {
                throw new IllegalStateException("Multiple active keys found: " + loginUserOrgId);
            }

            OrgKeySetting activeKey = currentActiveKeys.get(0);
            activeKey.setIsActive(false);
            activeKey.setUpDatetime(Instant.now());
            customOrgKeySettingRepository.save(activeKey);
        }

        // 4. 啟用指定金鑰
        keyToActivate.setIsActive(true);
        keyToActivate.setUpDatetime(Instant.now());
        customOrgKeySettingRepository.save(keyToActivate);
    }

    private OrgKeyDetailResDTO toResDTO(OrgKeySetting orgKeySetting) {
        OrgKeyDetailResDTO dto = new OrgKeyDetailResDTO();
        dto.setId(orgKeySetting.getId());
        dto.setOrgId(orgKeySetting.getOrgId());
        dto.setKeyId(orgKeySetting.getKeyId());
        dto.setDescription(orgKeySetting.getDescription());
        dto.setPublicKey(orgKeySetting.getPublicKey());
        dto.setPrivateKey(orgKeySetting.getPrivateKey());
        dto.setTotpKey(orgKeySetting.getTotpKey());
        dto.setHmacKey(orgKeySetting.getHmacKey());
        dto.setIsActive(orgKeySetting.getIsActive());
        dto.setCrDatetime(orgKeySetting.getCrDatetime());
        dto.setUpDatetime(orgKeySetting.getUpDatetime());

        return dto;
    }

}
