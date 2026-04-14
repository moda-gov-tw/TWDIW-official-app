package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.repository.custom.CustomExtendedUserRepository;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.repository.custom.CustomVPItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.OidvpConfigService;
import gov.moda.dw.manager.service.criteria.OrgCriteria;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.dto.OrgTitleResDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.custom.FindVerifierResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgReqDTO;
import gov.moda.dw.manager.service.dto.custom.OrgResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgUpdateResultDTO;
import gov.moda.dw.manager.service.mapper.OrgMapper;
import gov.moda.dw.manager.type.OrgType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.StringFilterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.service.filter.RangeFilter;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOrgService {

    private final CustomOrgRepository orgRepository;

    private final CustomOrgQueryService orgQueryService;

    private final OrgMapper orgMapper;

    private final CustomUserService customUserService;

    private final CustomExtendedUserRepository extendedUserRepository;

    private final OidvpConfigService oidvpConfigService;

    private final CustomVPItemRepository customVPItemRepository;

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    @Value("${dwfront.dwfront-301i}")
    private String findVerifierURL;

    /**
     * 查詢組織, 沒有塞入條件就查全部
     *
     * @param orgReqDTO
     * @param pageable
     * @return
     */
    public Page<OrgDTO> getAllOrg(OrgReqDTO orgReqDTO, Pageable pageable) {
        log.info("CustomOrgService-getAllOrg 開始查詢組織");
        try {
            OrgCriteria orgCriteria = new OrgCriteria();

            if (StringUtils.isNotEmpty(orgReqDTO.getOrgId())) {
                orgCriteria.setOrgId(StringFilterUtils.toContainStringFilter(orgReqDTO.getOrgId()));
            }

            if (StringUtils.isNotEmpty(orgReqDTO.getOrgTwName())) {
                orgCriteria.setOrgTwName(StringFilterUtils.toContainStringFilter(orgReqDTO.getOrgTwName()));
            }

            if (StringUtils.isNotEmpty(orgReqDTO.getOrgEnName())) {
                orgCriteria.setOrgEnName(StringFilterUtils.toContainStringFilter(orgReqDTO.getOrgEnName()));
            }

            if (orgReqDTO.getCreateTimeFrom() != null || orgReqDTO.getCreateTimeTo() != null) {
                RangeFilter<LocalDateTime> createTimeFilter = new RangeFilter<>();
                if (orgReqDTO.getCreateTimeFrom() != null) {
                    createTimeFilter.setGreaterThanOrEqual(orgReqDTO.getCreateTimeFrom().atStartOfDay());
                }
                if (orgReqDTO.getCreateTimeTo() != null) {
                    createTimeFilter.setLessThanOrEqual(orgReqDTO.getCreateTimeTo().atTime(LocalTime.MAX));
                }
                orgCriteria.setCreateTime(createTimeFilter);
            }

            Page<OrgDTO> findAll = orgQueryService.findByCriteria(orgCriteria, pageable);

            // 查詢 DID 組織
            String didOrgId = getDidOrgId();
            if (StringUtils.isNotEmpty(didOrgId)) {
                for (OrgDTO dto : findAll.getContent()) {
                    dto.setIsDidOrg(didOrgId.equals(dto.getOrgId()));
                }
            }

            return findAll;
        } catch (Exception ex) {
            log.error("CustomOrgService-getAllOrg 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 查詢 DID 組織
     * 
     * @return
     */
    private String getDidOrgId() {
        try {
            Optional<OidvpConfig> verifierDID = oidvpConfigService.findOne("verifier.did");

            if (verifierDID.isEmpty()) {
                return null;
            }

            String did = verifierDID.get().getPropertyValue();
            if (StringUtils.isBlank(did)) {
                return null;
            }

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = findVerifierURL + "/" + did;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<FindVerifierResDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    FindVerifierResDTO.class);

            if (response.getBody() == null || response.getBody().getCode() != 0) {
                log.warn("CustomOrgService-getAllOrg-front301i查詢DID資訊失敗, code={}, msg={}",
                        response.getBody() != null ? response.getBody().getCode() : "null",
                        response.getBody() != null ? response.getBody().getMsg() : "null");
                return null;
            }

            return response.getBody().getData().getOrg().getTaxId();
        } catch (Exception e) {
            log.error("getDidOrgId 發生錯誤", e);
            return null;
        }
    }

    /**
     * 新增組織
     *
     * @param orgReqDTO
     * @return
     */
    public StatusCode createOrg(OrgReqDTO orgReqDTO) {
        log.info("CustomOrgService-createOrg 準備新增組織");
        try {
            // 檢查組織代碼是否重複
            OrgCriteria orgCriteria = new OrgCriteria();
            orgCriteria.setOrgId(StringFilterUtils.toEqualStringFilter(orgReqDTO.getOrgId()));
            List<OrgDTO> orgDTOList = orgQueryService.findByCriteria(orgCriteria);
            if (CollectionUtils.isNotEmpty(orgDTOList)) {
                log.info("CustomOrgService-createOrg 輸入的組織代碼已存在, 不可新增組織");
                return StatusCode.ORG_ORGID_EXIST;
            }

            // 檢查組織英文名稱是否重複
            OrgCriteria orgENCriteria = new OrgCriteria();
            orgENCriteria.setOrgEnName(StringFilterUtils.toEqualStringFilter(orgReqDTO.getOrgEnName()));
            List<OrgDTO> orgDTOENList = orgQueryService.findByCriteria(orgENCriteria);
            if (CollectionUtils.isNotEmpty(orgDTOENList)) {
                log.info("CustomOrgService-createOrg 輸入的組織英文名稱已存在, 不可新增組織");
                return StatusCode.ORG_ORGENNAME_EXIST;
            }

            // 開始新增組織
            OrgDTO orgDTO = this.setToOrgDTO(orgReqDTO);
            // metadata 後續要用，確保清空該欄
            this.save(orgDTO);
            log.info("CustomOrgService-createOrg 新增組織成功");

            return StatusCode.SUCCESS;
        } catch (Exception ex) {
            log.error("CustomOrgService-createOrg 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return StatusCode.FAIL;
        }
    }

    /**
     * 修改組織
     *
     * @param orgReqDTO
     * @return
     */
    public OrgUpdateResultDTO updateOrg(OrgReqDTO orgReqDTO) {
        log.info("CustomOrgService-updateOrg 準備修改組織");
        OrgUpdateResultDTO orgResponseDTO = new OrgUpdateResultDTO();
        try {
            // 開始修改組織
            Optional<Org> opOrg = orgRepository.findById(orgReqDTO.getId());
            if (opOrg.isPresent()) {
                OrgDTO orgDTO = orgMapper.toDto(opOrg.get());
                // 如為預設組織
                if (OrgType.DEFAULT_ORG.getCode().equals(orgDTO.getOrgId())) {
                    log.info("CustomOrgService-updateOrg 預設組織不可修改");
                    orgResponseDTO.setStatusCode(StatusCode.ORG_DEFAULT_ORG_CANNOT_BE_EDIT);
                    return orgResponseDTO;
                }

                // 檢查組織代碼是否重複
                OrgCriteria orgCriteria = new OrgCriteria();
                orgCriteria.setOrgId(StringFilterUtils.toEqualStringFilter(orgReqDTO.getOrgId()));
                List<OrgDTO> orgDTOList = orgQueryService.findByCriteria(orgCriteria);
                if (CollectionUtils.isNotEmpty(orgDTOList) && !orgReqDTO.getId().equals(orgDTOList.get(0).getId())) {
                    log.info("CustomOrgService-updateOrg 輸入的組織代碼已存在, 不可編輯組織");
                    orgResponseDTO.setStatusCode(StatusCode.ORG_ORGID_EXIST);
                    return orgResponseDTO;
                }

                // 修改組織
                this.setToUpdateOrg(orgDTO, orgReqDTO);
                OrgDTO updateResult = this.save(orgDTO);

                // 整理回傳內容
                OrgResDTO responseOrg = this.orgDTOToOrgResDTO(updateResult);
                orgResponseDTO.setOrgResDTO(responseOrg);
                orgResponseDTO.setStatusCode(StatusCode.SUCCESS);
                log.info("CustomOrgService-updateOrg 修改組織成功");
            } else {
                log.warn("CustomOrgService-updateOrg 查不到該組織, 無法執行編輯組織");
                orgResponseDTO.setStatusCode(StatusCode.ORG_NOT_EXISTS);
            }

            return orgResponseDTO;
        } catch (Exception ex) {
            log.error("CustomOrgService-updateOrg 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 刪除組織
     *
     * @param orgReqDTO
     * @return
     */
    public StatusCode deleteOrg(OrgReqDTO orgReqDTO) {
        log.info("CustomOrgService-deleteOrg 準備刪除組織");
        try {
            // 查詢要刪除的組織是否存在
            Optional<Org> opOrg = orgRepository.findByOrgId(orgReqDTO.getOrgId());
            if (opOrg.isPresent()) {
                OrgDTO orgDTO = orgMapper.toDto(opOrg.get());

                // 如為預設組織, 不可刪除
                if (OrgType.DEFAULT_ORG.getCode().equals(orgDTO.getOrgId())) {
                    log.info("CustomOrgService-deleteOrg 預設組織不可刪除");
                    return StatusCode.ORG_DEFAULT_ORG_CANNOT_BE_DELETE;
                }

                // 如為 DID 組織, 不可刪除
                String didOrgId = getDidOrgId();
                if (orgDTO.getOrgId().equals(didOrgId)) {
                    log.info("CustomOrgService-deleteOrg 該組織不可刪除");
                    return StatusCode.ORG_CANNOT_BE_DELETE;
                }

                // 該組織有建立VP模板，無法刪除該組織
                if (customVPItemRepository.existsByBusinessId(orgReqDTO.getOrgId())) {
                    log.info("CustomOrgService-deleteOrg 該組織有建立VP模板，無法刪除該組織");
                    return StatusCode.ORG_ORG_HAS_VP_SCHEMA_CANNOT_BE_DELETE;
                }

                // 刪除組織
                orgRepository.deleteByOrgId(orgDTO.getOrgId());
                log.info("CustomOrgService-deleteOrg 刪除組織完成");

                List<ExtendedUser> affectedUsers = extendedUserRepository.findAllByOrgId(orgDTO.getOrgId());
                if (!affectedUsers.isEmpty()) {
                    for (ExtendedUser user : affectedUsers) {
                        user.setOrgId("default");
                    }
                    extendedUserRepository.saveAll(affectedUsers);
                    log.info("CustomOrgService-deleteOrg 已將 {} 個 extendedUser 的 orgId 更新為 'default'",
                            affectedUsers.size());
                } else {
                    log.info("CustomOrgService-deleteOrg 未發現需要更新的 extendedUser 賬號 orgId={}", orgDTO.getOrgId());
                }

                return StatusCode.SUCCESS;
            } else {
                log.warn("CustomOrgService-deleteOrg 查不到該組織, 無法執行刪除組織");
                return StatusCode.ORG_NOT_EXISTS;
            }
        } catch (Exception ex) {
            log.error("CustomOrgService-deleteOrg 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return StatusCode.FAIL;
        }
    }

    private OrgDTO setToOrgDTO(OrgReqDTO orgReqDTO) {
        OrgDTO orgDTO = new OrgDTO();
        orgDTO.setOrgId(orgReqDTO.getOrgId());
        orgDTO.setOrgTwName(orgReqDTO.getOrgTwName());
        orgDTO.setOrgEnName(orgReqDTO.getOrgEnName());
        orgDTO.setCreateTime(Instant.now());
        orgDTO.setUpdateTime(Instant.now());
        orgDTO.setLogoSquare(orgReqDTO.getLogoSquare());
        orgDTO.setLogoRectangle(orgReqDTO.getLogoRectangle());
        return orgDTO;
    }

    private void setToUpdateOrg(OrgDTO orgDTO, OrgReqDTO orgReqDTO) {
        orgDTO.setOrgId(orgReqDTO.getOrgId());
        orgDTO.setOrgTwName(orgReqDTO.getOrgTwName());
        orgDTO.setOrgEnName(orgReqDTO.getOrgEnName());
        orgDTO.setUpdateTime(Instant.now());
    }

    /**
     * 設定角色前端回傳的所有顯示欄位
     * 
     * @param orgDTO
     * @return
     */
    private OrgResDTO orgDTOToOrgResDTO(OrgDTO orgDTO) {
        OrgResDTO orgSearchAllResDTO = new OrgResDTO();
        orgSearchAllResDTO.setId(orgDTO.getId());
        orgSearchAllResDTO.setOrgId(orgDTO.getOrgId());
        orgSearchAllResDTO.setOrgTwName(orgDTO.getOrgTwName());
        orgSearchAllResDTO.setOrgEnName(orgDTO.getOrgEnName());
        orgSearchAllResDTO.setCreateTime(orgDTO.getCreateTime());
        orgSearchAllResDTO.setUpdateTime(orgDTO.getUpdateTime());

        return orgSearchAllResDTO;
    }

    /**
     * 設定角色前端回傳的所有顯示欄位
     * 
     * @param orgDTO
     * @return
     */
    private OrgTitleResDTO orgEntityToOrgResDTO(Org orgDTO) {
        OrgTitleResDTO orgSearchAllResDTO = new OrgTitleResDTO();
        orgSearchAllResDTO.setId(orgDTO.getId());
        orgSearchAllResDTO.setOrgId(orgDTO.getOrgId());
        orgSearchAllResDTO.setOrgTwName(orgDTO.getOrgTwName());
        orgSearchAllResDTO.setOrgEnName(orgDTO.getOrgEnName());
        return orgSearchAllResDTO;
    }

    /**
     * Save a org.
     *
     * @param orgDTO the entity to save.
     * @return the persisted entity.
     */
    public OrgDTO save(OrgDTO orgDTO) {
        log.debug("Request to save Org : {}", orgDTO);
        Org org = orgMapper.toEntity(orgDTO);
        org = orgRepository.save(org);
        return orgMapper.toDto(org);
    }

    /**
     * Update a org.
     *
     * @param orgDTO the entity to save.
     * @return the persisted entity.
     */
    public OrgDTO update(OrgDTO orgDTO) {
        log.debug("Request to update Org : {}", orgDTO);
        Org org = orgMapper.toEntity(orgDTO);
        org = orgRepository.save(org);
        return orgMapper.toDto(org);
    }

    /**
     * Partially update a org.
     *
     * @param orgDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrgDTO> partialUpdate(OrgDTO orgDTO) {
        log.debug("Request to partially update Org : {}", orgDTO);

        return orgRepository.findById(orgDTO.getId()).map(existingOrg -> {
            orgMapper.partialUpdate(existingOrg, orgDTO);

            return existingOrg;
        }).map(orgRepository::save).map(orgMapper::toDto);
    }

    /**
     * Get one org by org.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrgDTO> findOne(Long id) {
        log.debug("Request to get Org : {}", id);
        return orgRepository.findById(id).map(orgMapper::toDto);
    }

    public OrgDTO findOneByOrgId(String orgId) {
        log.debug("Request to get Org by orgId : {}", orgId);
        Org org = orgRepository.findByOrgId(orgId).orElse(null);
        if (org == null) {
            log.warn("CustomOrgService-findOneByOrgId 查不到該組織, orgId= {}", orgId);
        }
        return orgMapper.toDto(org);
    }

    /**
     * Delete the org by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Org : {}", id);
        orgRepository.deleteById(id);
    }

    public List<OrgTitleResDTO> getOrgList() {
        List<OrgTitleResDTO> result = new ArrayList<>();

        if (checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId())) {
            List<Org> orgList = orgRepository.findAll();
            for (Org org : orgList) {
                OrgTitleResDTO item = orgEntityToOrgResDTO(org);
                result.add(item);
            }
        } else {
            Optional<Org> org = orgRepository.findByOrgId(SecurityUtils.getJwtUserObject().get(0).getOrgId());
            if (org.isPresent()) {
                OrgTitleResDTO item = orgEntityToOrgResDTO(org.get());
                result.add(item);
            }
        }

        return result;
    }

    private Boolean checkIsPrivilegedAccount(String userId) {
        List<RoleDTO> roles = customUserService.getRoles(userId);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }
}
