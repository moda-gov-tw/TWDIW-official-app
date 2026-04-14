package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.repository.AccessTokenRepository;
import gov.moda.dw.manager.repository.custom.CustomAccessTokenRepository;
import gov.moda.dw.manager.service.AccessTokenQueryService;
import gov.moda.dw.manager.service.criteria.AccessTokenCriteria;
import gov.moda.dw.manager.service.criteria.ResCriteria;
import gov.moda.dw.manager.service.dto.AccessTokenDTO;
import gov.moda.dw.manager.service.dto.RelDTO;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wAccessTokenResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wRelReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wResReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wResResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wSearchAllTokenResultDTO;
import gov.moda.dw.manager.type.AuthorityAction;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.InstantFilterUtils;
import gov.moda.dw.manager.util.LongFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;

@Service
@Transactional
public class Ams351wService {

    private final Logger log = LoggerFactory.getLogger(Ams351wService.class);
    private final CustomAccessTokenRepository customAccessTokenRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final CustomRelQueryService relQueryService;
    private final AccessTokenQueryService accessTokenQueryService;
    private final CustomAccessTokenService customAccessTokenService;
    private final CustomResQueryService customResQueryService;
    private final CustomRelService customRelService;

    public Ams351wService(
        AccessTokenRepository accessTokenRepository,
        CustomAccessTokenRepository customAccessTokenRepository,
        CustomRelQueryService relQueryService,
        AccessTokenQueryService accessTokenQueryService,
        CustomAccessTokenService customAccessTokenService,
        CustomResQueryService customResQueryService,
        CustomRelService customRelService
    ) {
        this.accessTokenRepository = accessTokenRepository;
        this.customAccessTokenRepository = customAccessTokenRepository;
        this.relQueryService = relQueryService;
        this.accessTokenQueryService = accessTokenQueryService;
        this.customAccessTokenService = customAccessTokenService;
        this.customResQueryService = customResQueryService;
        this.customRelService = customRelService;
    }

    /**
     * 取得所有AccessToken清單by搜尋條件。
     */
    @Transactional(readOnly = true)
    public Ams351wSearchAllTokenResultDTO getAllAccessToken(Ams351wReqDTO ams351WReqDTO, Pageable pageable) {
        try {
            // 確認前端輸入日期資料是否輸入正確。
            StatusCode result = this.checkSearchDate(ams351WReqDTO);

            if (result.getCode().equals(StatusCode.SUCCESS.getCode())) {
                AccessTokenCriteria accessTokenCriteria = this.setCriteriaFromSearch(ams351WReqDTO);
                Page<AccessTokenDTO> accessTokenDTOPage = this.accessTokenQueryService.findByCriteria(accessTokenCriteria, pageable);

                List<Ams351wAccessTokenResDTO> ams351wAccessTokenResDTOList = new ArrayList<>();
                for (AccessTokenDTO dto : accessTokenDTOPage.getContent()) {
                    ams351wAccessTokenResDTOList.add(new Ams351wAccessTokenResDTO(dto));
                }
                Ams351wSearchAllTokenResultDTO resultDTO = new Ams351wSearchAllTokenResultDTO(
                    new PageImpl<>(ams351wAccessTokenResDTOList, accessTokenDTOPage.getPageable(), accessTokenDTOPage.getTotalElements())
                );
                log.info(
                    "Ams351wService-getAllAccessToken，已查詢AccessTokenList，List:{}",
                    resultDTO.getAms351wAccessTokenResDTOPage().getContent()
                );
                return resultDTO;
            } else {
                return new Ams351wSearchAllTokenResultDTO(result);
            }
        } catch (Exception ex) {
            log.error("Ams351wService-getAllAccessToken-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    /**
     * 驗證搜尋條件的起始時間及結束時間。
     */
    private StatusCode checkSearchDate(Ams351wReqDTO ams351wReqDTO) {
        Instant begin = ams351wReqDTO.getBeginDate() != null
            ? DateUtils.toInstantDate(ams351wReqDTO.getBeginDate().replace("/", ""))
            : null;
        Instant end = ams351wReqDTO.getEndDate() != null ? DateUtils.toInstantDate(ams351wReqDTO.getEndDate().replace("/", "")) : null;

        if (begin != null || end != null) {
            if (begin != null && begin.isAfter(Instant.now())) {
                log.error("Ams351wService-checkSearchDate，查詢起始時間不正確");
                return StatusCode.SEARCH_BEGIN_TIME_ERROR;
            }

            if (begin != null && end != null && end.isBefore(begin)) {
                log.error("Ams351wService-checkSearchDate，查詢結束時間不正確");
                return StatusCode.SEARCH_END_TIME_ERROR;
            }
        }
        return StatusCode.SUCCESS;
    }

    /**
     * 設定查詢條件。
     */
    private AccessTokenCriteria setCriteriaFromSearch(Ams351wReqDTO ams351wReqDTO) {
        AccessTokenCriteria accessTokenCriteria = new AccessTokenCriteria();

        if (StringUtils.isNotBlank(ams351wReqDTO.getAuthKeyName())) {
            accessTokenCriteria.setAccessTokenName(
                StringFilterUtils.toContainStringFilter(ams351wReqDTO.getAuthKeyName().toLowerCase())
            );
        }
        if (StringUtils.isNotBlank(ams351wReqDTO.getOwnerName())) {
            accessTokenCriteria.setOwnerName(StringFilterUtils.toContainStringFilter(ams351wReqDTO.getOwnerName()));
        }
        if (StringUtils.isNotBlank(ams351wReqDTO.getOrgId())) {
            accessTokenCriteria.setOrgId(StringFilterUtils.toContainStringFilter(ams351wReqDTO.getOrgId()));
        }
        if (StringUtils.isNotBlank(ams351wReqDTO.getState())) {
            accessTokenCriteria.setState(StringFilterUtils.toContainStringFilter(ams351wReqDTO.getState()));
        }

        Instant begin = ams351wReqDTO.getBeginDate() != null
            ? DateUtils.toInstantDate(ams351wReqDTO.getBeginDate().replace("/", ""))
            : null;
        Instant end = ams351wReqDTO.getEndDate() != null ? DateUtils.toInstantDate(ams351wReqDTO.getEndDate().replace("/", "")) : null;

        if (begin != null && end != null && end.isBefore(begin)) {
            throw new BadRequestAlertException("Data row error.", "accessTokenManagement", "endTimeError");
        }

        if (begin == null) {
            begin = Instant.EPOCH;
        } else if (begin.isAfter(Instant.now())) {
            throw new BadRequestAlertException("Data row error.", "accessTokenManagement", "beginTimeError");
        }

        if (end == null) {
            // 取得當日23:59:59。
            Date date = new Date();
            end = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault())
                .with(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        } else {
            end = end.plusSeconds(24 * 60 * 60 - 1);
        }
        accessTokenCriteria.setCreateTime(InstantFilterUtils.toInstantFilter("Ams351wService", begin, end));

        return accessTokenCriteria;
    }

    /**
     * 建立、更新AccessToken。
     */
    public Ams351wResultDTO upsertAccessToken(Ams351wReqDTO ams351wReqDTO, AuthorityAction actionType) {
        try {
            // 確認前端輸入資料是否輸入正確。
            StatusCode result = this.checkData(ams351wReqDTO, actionType);

            if (result.getCode().equals(StatusCode.SUCCESS.getCode())) {
                // 新增或更新AccessToken。
                AccessToken accessToken;

                if (actionType.equals(AuthorityAction.ACCESS_TOKEN_CREATE)) {
                    accessToken = this.customAccessTokenService.saveAccessToken(ams351wReqDTO);
                } else {
                    accessToken = this.customAccessTokenService.updateAccessToken(ams351wReqDTO, actionType);
                }
                if (accessToken != null) {
                    StatusCode resultStatus;
                    if (actionType.equals(AuthorityAction.ACCESS_TOKEN_CREATE)) {
                        resultStatus = this.updateRel(accessToken.getAccessToken(), actionType);
                        if (resultStatus != null) {
                            return new Ams351wResultDTO(resultStatus);
                        }
                    } else {
                        resultStatus = this.updateRel(accessToken.getAccessToken(), actionType);
                        if (resultStatus != null) {
                            return new Ams351wResultDTO(new Ams351wAccessTokenResDTO(accessToken));
                        }
                    }
                }
            } else {
                return new Ams351wResultDTO(result);
            }
        } catch (Exception ex) {
            log.error("Ams351wService-upsertAccessToken-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        if (actionType.equals(AuthorityAction.ACCESS_TOKEN_CREATE)) {
            return new Ams351wResultDTO(StatusCode.TOKEN_CREATE_EXCEPTION);
        } else {
            return new Ams351wResultDTO(StatusCode.TOKEN_UPDATE_EXCEPTION);
        }
    }

    /**
     * 驗證建立及更新AccessToken的參數。
     */
    private StatusCode checkData(Ams351wReqDTO ams351wReqDTO, AuthorityAction actionType) {
        if (actionType.equals(AuthorityAction.ACCESS_TOKEN_CREATE) || actionType.equals(AuthorityAction.ACCESS_TOKEN_UPDATE)) {
            if (StringUtils.isBlank(ams351wReqDTO.getAuthKeyName())) {
                log.error("Ams351wService-checkData-發生錯誤，AccessTokenName不符合格式");
                return StatusCode.TOKEN_INVALID_NAME;
            } else if (StringUtils.isBlank(ams351wReqDTO.getOrgId())) {
                log.error("Ams351wService-checkData-發生錯誤，AccessTokenOrgId不符合格式");
                return StatusCode.TOKEN_INVALID_ORG_ID;
            } else if (ams351wReqDTO.getState() == null) {
                log.error("Ams351wService-checkData-發生錯誤，AccessToken啟停狀態異常");
                return StatusCode.TOKEN_INVALID_ACTIVATED;
            }
        }
        if (actionType.equals(AuthorityAction.ACCESS_TOKEN_UPDATE) || actionType.equals(AuthorityAction.ACCESS_TOKEN_CHANGE_ACTIVATED)) {
            if (ams351wReqDTO.getId() == null) {
                log.error("Ams351wService-checkData-發生錯誤，AccessTokenID不得無值");
                return StatusCode.TOKEN_ID_NOT_EXISTS;
            } else if (this.customAccessTokenRepository.findOneById(ams351wReqDTO.getId()).isEmpty()) {
                log.error("Ams351wService-checkData-發生錯誤，AccessToken不存在");
                return StatusCode.TOKEN_NOT_EXISTS;
            }
        }
        if (actionType.equals(AuthorityAction.ACCESS_TOKEN_UPDATE) && StringUtils.isBlank(ams351wReqDTO.getAuthKey())) {
            log.error("Ams351wService-checkData-發生錯誤，AccessToken不符合格式");
            return StatusCode.TOKEN_INVALID_TOKEN;
        }

        return StatusCode.SUCCESS;
    }

    /**
     * Delete the accessToken by id.
     *
     * @param listId the listId of the entity List.
     */
    public StatusCode delete(List<Long> listId) {
        for (Long id : listId) {
            Optional<AccessToken> accessTokenOpt = accessTokenRepository.findById(id);
            if (accessTokenOpt.isPresent()) {
                AccessToken accessToken = accessTokenOpt.get();
                this.updateRel(accessToken.getAccessToken(), AuthorityAction.ACCESS_TOKEN_DELETE);
                accessTokenRepository.deleteById(accessToken.getId());
                log.info("Ams351wService-delete，已刪除AccessToken，AccessTokenId:{}", accessToken.getId());
            }
        }
        return StatusCode.SUCCESS;
    }

    /**
     * updateRel方法重載。
     */
    public StatusCode updateRel(String accessTokenStr, AuthorityAction actionType) {
        Ams351wRelReqDTO ams351wRelReqDTO = new Ams351wRelReqDTO();
        ams351wRelReqDTO.setAuthKey(accessTokenStr);
        return updateRel(ams351wRelReqDTO, actionType);
    }

    /**
     * AccessToken更新授權功能。
     */
    public StatusCode updateRel(Ams351wRelReqDTO ams351wRelReqDTO, AuthorityAction actionType) {
        AccessToken accessToken = customAccessTokenRepository.findByAccessToken(ams351wRelReqDTO.getAuthKey());
        if (accessToken == null) {
            log.info("Ams351wService-updateRel，AccessToken不存在");
            return StatusCode.TOKEN_NOT_EXISTS;
        }
        return this.customRelService.accessTokenAction(ams351wRelReqDTO, accessToken, actionType);
    }

    /**
     * 取得AccessToken所擁有的功能權限清單。
     */
    public Ams351wResResultDTO getAccessTokenAllRes(Ams351wResReqDTO request) {
        Optional<AccessToken> optionalAccessToken = this.accessTokenRepository.findById(request.getId());
        if (optionalAccessToken.isPresent()) {
            List<ResDTO> resDTOListMap = new ArrayList<>();
            AccessToken accessToken = optionalAccessToken.get();
            List<RelDTO> relDTOList = this.relQueryService.findRelDTOListForAccessTokenStr(accessToken.getAccessToken());
            for (RelDTO relDTO : relDTOList) {
                ResCriteria resCriteria = new ResCriteria();
                resCriteria.setId(LongFilterUtils.toEqualLongFilter(relDTO.getRightId()));
                List<ResDTO> resDTOList = this.customResQueryService.findByCriteria(resCriteria);
                if (!resDTOList.isEmpty()) {
                    resDTOListMap.add(resDTOList.get(0));
                }
            }
            log.error("Ams351wService-getAccessTokenAllRes，AccessToken賦予功能清單已查詢");
            return new Ams351wResResultDTO(resDTOListMap);
        } else {
            log.error("Ams351wService-getAccessTokenAllRes-發生錯誤，AccessToken不存在");
            return new Ams351wResResultDTO(StatusCode.TOKEN_NOT_EXISTS);
        }
    }
}
