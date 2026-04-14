package gov.moda.dw.manager.service.custom;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.VCCredential;
import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVCCredentialRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemDataRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.custom.common.CustomVCDataStatusLogService;
import gov.moda.dw.manager.service.dto.DwissuerVC203iResp;
import gov.moda.dw.manager.service.dto.Dwvc302iReqDTO;
import gov.moda.dw.manager.service.dto.Dwvc302iResDTO;
import gov.moda.dw.manager.service.dto.Dwvc302iResDTO.Dwvc302iFailCidResDTO;
import gov.moda.dw.manager.service.dto.Dwvc302iResDTO.Dwvc302iFailResDTO;
import gov.moda.dw.manager.service.dto.IssuerVC203iRes;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.VCItemDataActionDTO;
import gov.moda.dw.manager.service.dto.VCItemDataUpdateFailDTO;
import gov.moda.dw.manager.service.dto.custom.VCCredentialRevokeVcItemDataDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.VcItemDataValidType;
import gov.moda.dw.manager.util.RSAUtils;
import gov.moda.dw.manager.util.SandBoxUtil;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.DWException;

@Service
public class DwSandBoxVC302WService {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC302WService.class);
    private final CustomVCItemDataRepository customVCItemDataRepository;
    private final CustomVCItemDataService customVCItemDataService;
    private final RemoteApiService remoteApiService;
    private final CustomUserRepository customUserRepository;
    private final CustomVCItemRepository customVCItemRepository;
    private final CustomVCCredentialRepository customVCCredentialRepository;
    private final CustomUserService customUserService;
    private final CustomVCDataStatusLogService customVCDataStatusLogService;

    @Value("${fin.rsaUtil.prvKy}")
    private String privateKey;

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    @Value("${sandbox.commonAccountOrgId:00000000}")
    String commonAccountOrgId;

    public DwSandBoxVC302WService(CustomVCItemDataRepository customVCItemDataRepository,
            CustomVCItemDataService customVCItemDataService, RemoteApiService remoteApiService,
            CustomUserRepository customUserRepository, CustomVCItemRepository customVCItemRepository,
            CustomVCCredentialRepository customVCCredentialRepository, CustomUserService customUserService,
            CustomVCDataStatusLogService customVCDataStatusLogService) {
        this.customVCItemDataRepository = customVCItemDataRepository;
        this.customVCItemDataService = customVCItemDataService;
        this.remoteApiService = remoteApiService;
        this.customUserRepository = customUserRepository;
        this.customVCItemRepository = customVCItemRepository;
        this.customVCCredentialRepository = customVCCredentialRepository;
        this.customUserService = customUserService;
        this.customVCDataStatusLogService = customVCDataStatusLogService;
    }

    /**
     * 撤銷或變更電子證件狀態
     * 
     * @param cid    電子證件識別碼
     * @param action 要執行的動作（例如：revocation、suspension、recovery 等）
     * @return
     */
    public DwissuerVC203iResp updateVcCredentialStatus(String cid, String action) {
        // 檢核 action 是否合法
        VcItemDataValidType vcItemDataValidType = VcItemDataValidType.getByActionIgnoreInactiveAndUnknown(action);
        if (vcItemDataValidType == VcItemDataValidType.UNKNOWN) {
            return this.buildFailResult(StatusCode.DWVP_ACTION_NOT_VALID);
        }

        VCItemData vcData = customVCItemDataRepository.findFirstByVcCid(cid);
        // 檢查權限
        if (!checkAuthSuccess(vcData)) {
            return this.buildFailResult(StatusCode.DWVP_PERMISSION_NOT_VALID);
        }

        // 檢核 憑證狀態
        StatusCode statusCode = null;
        if (null != vcData) {
            Integer valid = vcData.getValid();
            // 檢核 已撤銷憑證無法停用、復用
            if (VcItemDataValidType.REVOKED.getCode().equals(valid)) {
                statusCode = StatusCode.DWVP_STATUS_REVOKED_ACTION_NOT_VALID;
            }

            // 檢核 已啟用憑證無法再次復用
            if (VcItemDataValidType.ACTIVE.getCode().equals(valid)
                    && StringUtils.equals(VcItemDataValidType.ACTIVE.getAction(), action)) {
                statusCode = StatusCode.DWVP_STATUS_ACTIVE_ACTION_NOT_VALID;
            }

            // 檢核 已停用憑證無法再次停用
            if (VcItemDataValidType.SUSPENDED.getCode().equals(valid)
                    && StringUtils.equals(VcItemDataValidType.SUSPENDED.getAction(), action)) {
                statusCode = StatusCode.DWVP_STATUS_SUSPENDED_ACTION_NOT_VALID;
            }
        }

        // 依 action 進行 VC 撤銷、停用、復用，成功後回寫 vc_item_data valid，並建立一筆 VC 資料狀態紀錄
        DwissuerVC203iResp result = this.executeVcActionAndRecord(vcData, cid, null, action);

        // 如有錯誤訊息
        if (null != statusCode) {
            result.setCode(statusCode.getCode());
            result.setMessage(statusCode.getMsg());
        }

        // 如錯誤代碼不為 61006, 61010, 61050, 69004，依 cid 查詢 credentialStatus
        result.setCredentialStatus(this.getCredentialStatus(cid, result.getCode()));

        return result;
    }

    /**
     * 發行端 VC 多筆卡片狀態變更
     * 
     * @param request cid 電子證件識別碼、action 要執行的動作（例如：revocation、suspension、recovery 等）
     * @return
     */
    public Dwvc302iResDTO multiaction(Dwvc302iReqDTO request) {
        String action = request.getAction();
        List<String> cids = request.getCids();

        // 檢核 action
        this.vaildMultiactionRequest(action, cids);

        List<String> successList = new ArrayList<>();
        List<Dwvc302iFailResDTO> failList = new ArrayList<>();

        // 一次撈出所有 VCItemData
        List<String> nonceList = customVCCredentialRepository.findAllByCidIn(cids).stream().map(VCCredential::getNonce)
                .toList();
        Map<String, VCItemData> vcDataMap = customVCItemDataRepository.findAllByTransactionIdIn(nonceList).stream()
                .collect(Collectors.toMap(VCItemData::getVcCid, Function.identity()));

        // 檢核 憑證操作權限、狀態
        for (String cid : cids) {
            VCItemData vcData = vcDataMap.get(cid);

            if (null != vcData) {
                this.validVcCredential(action, cid, vcData, failList);
            } 
        }

        // 移除檢核未通過 cid
        for (Dwvc302iFailResDTO fail : failList) {
            List<String> failCids = fail.getCids().stream().map(Dwvc302iFailCidResDTO::getCid).toList();
            cids.removeAll(failCids);
        }

        // 檢核通過 cid 依 action 進行 VC 撤銷、停用、復用
        for (String cid : cids) {
            // 依 action 進行 VC 撤銷、停用、復用，成功後回寫 vc_item_data valid，並建立一筆 VC 資料狀態紀錄
            IssuerVC203iRes actionResult = this.executeVcActionAndRecordFor302(vcDataMap.get(cid), cid, null, action);
            if (StringUtils.isNotBlank(actionResult.getCredentialStatus())) {
                successList.add(cid);
            } else {
                // 建立 fail list
                this.setFailList(failList, actionResult.getCode(), actionResult.getMessage(),
                        actionResult.getInfo().getCid(), true);
            }
        }

        Dwvc302iResDTO result = new Dwvc302iResDTO();
        result.setAction(action);
        result.setSuccess(successList);
        result.setFail(failList);
        return result;
    }

    /**
     * 依 action 進行 VC 撤銷、停用、復用，成功後回寫 vc_item_data valid，並建立一筆 VC 資料狀態紀錄
     * 
     * @param vcData
     * @param cid
     * @param lastUpdateTime
     * @param action
     * @return
     */
    public DwissuerVC203iResp executeVcActionAndRecord(VCItemData vcData, String cid, Instant lastUpdateTime,
            String action) {
        // 呼叫 API 依 action 進行 VC 撤銷、停用、復用
        DwissuerVC203iResp result = remoteApiService.callApi203i(cid, action);
        VcItemDataValidType vcStatus = VcItemDataValidType.getByName(result.getCredentialStatus());

        if (null == vcData) {
            vcData = customVCItemDataRepository.findFirstByVcCid(cid);
        }

        // VC 成功撤銷、停用、復用後回寫 vcItemData valid
        if (null != vcData && StringUtils.equalsAny(vcStatus.name(), VcItemDataValidType.ACTIVE.name(),
                VcItemDataValidType.SUSPENDED.name(), VcItemDataValidType.REVOKED.name())) {
            vcData.setValid(vcStatus.getCode());
            customVCItemDataRepository.save(vcData);

            // 建立一筆 VC 資料狀態紀錄
            customVCDataStatusLogService.createStatusLog(cid, vcData.getId(), vcData.getTransactionId(),
                    vcStatus.getCode(), lastUpdateTime, true);
        }

        return result;
    }

    /**
     * Update by multiAction.
     *
     * @param request the entities to save.
     * @return the persisted entity.
     */
    public VCItemDataUpdateFailDTO multiAction(List<VCItemDataActionDTO> request) {
        log.debug("Request to update VCItemData : {}", request);
        VCItemDataUpdateFailDTO response = new VCItemDataUpdateFailDTO();
        List<String> failVcCidList = new ArrayList<>();

        // 傳 id 的撤銷
        List<Long> ids = request.stream().map(VCItemDataActionDTO::getId).filter(id -> id != null).toList();
        if (CollectionUtils.isNotEmpty(ids)) {
            throw new BadRequestAlertException("multiActionError", "id count=" + ids.size(),
                    "revoke by id is already forbidden.");
        }

        // 檢核 action 是否合乎規定
        String action = request.stream().map(VCItemDataActionDTO::getAction).findFirst().orElse("");
        VcItemDataValidType vcStatus = VcItemDataValidType.getByValue(action);
        if (StringUtils.equals(VcItemDataValidType.UNKNOWN.name(), vcStatus.name())) {
            throw new BadRequestAlertException("multiActionError", "VCItemData", "action not found");
        }

        // 檢核 該批 action 是否都相同
        boolean actionNotSame = request.stream().map(VCItemDataActionDTO::getAction).distinct().count() > 1;
        if (actionNotSame) {
            throw new BadRequestAlertException("multiActionError", "VCItemData", "action not same");
        }

        // 檢核 憑證狀態
        List<String> validVcStatusList = this.validVcStatus(request, failVcCidList, vcStatus);

        // 如憑證狀態有不合規則，直接 return
        if (CollectionUtils.isNotEmpty(validVcStatusList)) {
            response.setFailList(validVcStatusList);
            return response;
        }

        // 傳 VcCid 的撤銷
        request.stream().map(VCItemDataActionDTO::getEncryptedVcCid).filter(encryptedVcCid -> encryptedVcCid != null)
                .map(encryptedVcCid -> {
                    try {
                        return RSAUtils.privateDecryptFromBase32(encryptedVcCid, RSAUtils.getPrivateKey(privateKey));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(vcCid -> {
                    VCItemData vcItemData = null;
                    int oldValid = -1;
                    try {
                        Optional<VCCredentialRevokeVcItemDataDTO> revokeVcItemDataOpt = customVCCredentialRepository
                                .findRevokeVcItemDataByCid(vcCid);
                        if (revokeVcItemDataOpt.isPresent()) {
                            VCCredentialRevokeVcItemDataDTO revokeVcItemData = revokeVcItemDataOpt.get();
                            vcItemData = new VCItemData();
                            vcItemData.setId(revokeVcItemData.getId());
                            vcItemData.setContent(revokeVcItemData.getContent());
                            vcItemData.setPureContent(revokeVcItemData.getPureContent());
                            vcItemData.setCrUser(revokeVcItemData.getCrUser());
                            vcItemData.setCrDatetime(revokeVcItemData.getCrDatetime());
                            vcItemData.setValid(revokeVcItemData.getValid());
                            vcItemData.setClearScheduleId(revokeVcItemData.getClearScheduleId());
                            vcItemData.setClearScheduleDatetime(revokeVcItemData.getClearScheduleDatetime());
                            vcItemData.setVcCid(revokeVcItemData.getCid());
                            vcItemData.setTransactionId(revokeVcItemData.getTransactionId());
                            vcItemData.setBusinessId(revokeVcItemData.getBusinessId());
                            vcItemData.setVcItemName(revokeVcItemData.getVcItemName());
                            vcItemData.setQrCode(revokeVcItemData.getQrCode());
                            vcItemData.setExpired(revokeVcItemData.getExpired());
                            vcItemData.setScheduleRevokeMessage(revokeVcItemData.getScheduleRevokeMessage());
                            // 檢查權限
                            boolean checkSuccess = checkAuthSuccess(vcItemData);
                            if (!checkSuccess) {
                                throw new AccessDeniedException("You do not have permission to access this resource");
                            } else {
                                oldValid = vcItemData.getValid().intValue();

                                // 呼叫 API 依 action 進行 VC 撤銷、停用、復用
                                DwissuerVC203iResp result = remoteApiService.callApi203i(vcCid, vcStatus.getAction());
                                log.info("vc-item-data.id:{} , result.credentialStatus:{}", vcCid,
                                        result.getCredentialStatus());
                                if (!StringUtils.equalsAny(result.getCredentialStatus(),
                                        VcItemDataValidType.ACTIVE.name(), VcItemDataValidType.SUSPENDED.name(),
                                        VcItemDataValidType.REVOKED.name())) {
                                    String credentialStatus = result.getCredentialStatus();
                                    log.warn("其他結果 {} ", credentialStatus);
                                    throw new OtherAPIHandleResultException(new StringBuilder("vc_item_data_id:")
                                            .append(vcCid).append(",message :").append(credentialStatus).toString());
                                }

                                // 更新 VCItemData Valid
                                customVCItemDataService.updateValidByVcCid(vcStatus.getCode(), vcItemData.getId());

                                // 建立一筆 VC 資料狀態紀錄
                                customVCDataStatusLogService.createStatusLog(vcCid, vcItemData.getId(),
                                        vcItemData.getTransactionId(), vcStatus.getCode(), null, false);
                            }
                        } else {
                            throw new RuntimeException("this vcItemData not found.");
                        }
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                        if (ex instanceof OtherAPIHandleResultException && vcItemData != null) {
                            customVCItemDataService.updateValidByVcCid(oldValid, vcItemData.getId());
                        }

                        failVcCidList.add(SandBoxUtil.maskVCCid(vcCid));
                    }
                });

        response.setFailList(failVcCidList);
        return response;
    }

    /**
     * 檢核 憑證狀態
     * 
     * @param request
     * @param failVcCidList
     * @param vcStatus
     * @return
     */
    private List<String> validVcStatus(List<VCItemDataActionDTO> request, List<String> failVcCidList,
            VcItemDataValidType vcStatus) {
        request.stream().map(VCItemDataActionDTO::getEncryptedVcCid).filter(encryptedVcCid -> encryptedVcCid != null)
                .map(encryptedVcCid -> {
                    try {
                        return RSAUtils.privateDecryptFromBase32(encryptedVcCid, RSAUtils.getPrivateKey(privateKey));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(vcCid -> {
                    Optional<VCCredentialRevokeVcItemDataDTO> revokeVcItemDataOpt = customVCCredentialRepository
                            .findRevokeVcItemDataByCid(vcCid);
                    if (revokeVcItemDataOpt.isPresent()) {
                        VCCredentialRevokeVcItemDataDTO revokeVcItemData = revokeVcItemDataOpt.get();
                        Integer valid = revokeVcItemData.getValid();
                        String cidMask = SandBoxUtil.maskVCCid(vcCid);
                        // 檢核 已撤銷憑證無法停用、復用
                        if (VcItemDataValidType.REVOKED.getCode().equals(valid)) {
                            log.info("檢核 已撤銷憑證無法停用、復用 : {}", cidMask);
                            failVcCidList.add(cidMask);
                        }

                        // 檢核 此憑證狀態已為啟用
                        if (VcItemDataValidType.ACTIVE.getCode().equals(valid)
                                && StringUtils.equals(VcItemDataValidType.ACTIVE.getAction(), vcStatus.getAction())) {
                            log.info("檢核 此憑證狀態已為啟用 : {}", cidMask);
                            failVcCidList.add(cidMask);
                        }

                        // 檢核 此憑證狀態已為停用
                        if (VcItemDataValidType.SUSPENDED.getCode().equals(valid) && StringUtils
                                .equals(VcItemDataValidType.SUSPENDED.getAction(), vcStatus.getAction())) {
                            log.info("檢核 此憑證狀態已為停用 : {}", cidMask);
                            failVcCidList.add(cidMask);
                        }
                    }
                });
        return failVcCidList;
    }

    private boolean checkAuthSuccess(VCItemData vcItemData) {
        if (vcItemData != null) {
            String loginUserOrgId = SecurityUtils.getJwtUserObject().get(0).getOrgId();
            String login = SecurityUtils.getJwtUserObject().get(0).getUserId();
            Boolean isAdmin = checkIsPrivilegedAccount(login);

            if (!isAdmin) {
                Optional<VCItem> vcItemOpt = customVCItemRepository.searchByIdByDataId(vcItemData.getId());
                if (vcItemOpt.isPresent()) {
                    // 只要是模板擁有者就可以撤銷
                    VCItem vcItem = vcItemOpt.get();
                    if (vcItem.getCrUser().equals(findUserId(login))) {
                        return true;
                    }

                    // 一般帳號建的資料,只有建的人可以撤銷,若別人公開的模板仍不可撤銷
                    if (vcItem.getBusinessId().equals(commonAccountOrgId)) {
                        return vcItem.getCrUser().equals(findUserId(login));
                    }

                    // 進階帳號模板下的資料 , 同組織才可以撤銷
                    if (!(vcItem.getBusinessId().equals(loginUserOrgId))) {
                        return false;
                    }
                } else {
                    // 找不到VCItem的暫定只有admin權限才可以撤銷
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean checkIsPrivilegedAccount(String userId) {
        List<RoleDTO> roles = customUserService.getRoles(userId);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }

    private Long findUserId(String login) {
        return customUserRepository.findOneByLogin(login).orElseThrow().getId();
    }

    /**
     * 檢核 發行端 VC 多筆卡片狀態變更 request
     * 
     * @param action
     * @param cids
     */
    private void vaildMultiactionRequest(String action, List<String> cids) {
        // 檢核 action
        if (StringUtils.isBlank(action)) {
            throw new DWException(StatusCode.DWVP_ACTION_IS_REQUIRED);
        }

        // 檢核 cids
        if (CollectionUtils.isEmpty(cids)) {
            throw new DWException(StatusCode.DWVP_CIDS_IS_REQUIRED);
        }

        // 檢核 action 是否合法
        VcItemDataValidType vcItemDataValidType = VcItemDataValidType.getByActionIgnoreInactiveAndUnknown(action);
        if (vcItemDataValidType == VcItemDataValidType.UNKNOWN) {
            throw new DWException(StatusCode.DWVP_ACTION_NOT_VALID);
        }
    }

    /**
     * 建立 fail list
     * 
     * @param failList
     * @param code
     * @param message
     * @param cid
     * @param isNeedCredentialStatus
     */
    private void setFailList(List<Dwvc302iFailResDTO> failList, String code, String message, String cid,
            boolean isNeedCredentialStatus) {
        Optional<Dwvc302iFailResDTO> existingFailOpt = failList.stream()
                .filter(it -> StringUtils.equals(it.getCode(), code))
                .findFirst();

        String credentialStatus = null;
        if (isNeedCredentialStatus) {
            // 如錯誤代碼不為 61006, 61010, 61050, 69004，依 cid 查詢 credentialStatus
            credentialStatus = this.getCredentialStatus(cid, code);
        }

        // 建立新的 failCid 物件
        Dwvc302iFailCidResDTO failCid = new Dwvc302iFailCidResDTO();
        failCid.setCid(cid);
        failCid.setCredentialStatus(credentialStatus);

        // 若已存在同 code，直接加入 cid
        if (existingFailOpt.isPresent()) {
            Dwvc302iFailResDTO existingFail = existingFailOpt.get();

            List<Dwvc302iFailCidResDTO> existingCids =
                    Optional.ofNullable(existingFail.getCids())
                            .map(ArrayList::new)
                            .orElse(new ArrayList<>());

            // 檢查是否已存在相同 cid
            boolean alreadyExists = existingCids.stream().anyMatch(c -> StringUtils.equals(c.getCid(), cid));

            if (!alreadyExists) {
                existingCids.add(failCid);
                existingFail.setCids(existingCids);
            }
        } else {
            Dwvc302iFailResDTO failDto = new Dwvc302iFailResDTO();
            failDto.setCode(code);
            failDto.setMessage(message);
            failDto.setCids(Collections.singletonList(failCid));
            failList.add(failDto);
        }
    }

    /**
     * 檢核 憑證操作權限、狀態
     * 
     * @param action
     * @param cid
     * @param vcData
     * @param failList
     */
    private void validVcCredential(String action, String cid, VCItemData vcData, List<Dwvc302iFailResDTO> failList) {
        // 檢查權限
        if (!checkAuthSuccess(vcData)) {
            // 建立 fail list
            this.setFailList(failList, StatusCode.DWVP_PERMISSION_NOT_VALID.getCode(),
                    StatusCode.DWVP_PERMISSION_NOT_VALID.getMsg(), cid, false);
            return;
        }


        // 檢核 憑證狀態
        StatusCode statusCode = null;
        if (null != vcData) {
            Integer valid = vcData.getValid();
            // 檢核 已撤銷憑證無法停用、復用
            if (VcItemDataValidType.REVOKED.getCode().equals(valid)) {
                log.warn("已撤銷憑證無法停用、復用 cid : {}", cid);
                statusCode = StatusCode.DWVP_STATUS_REVOKED_ACTION_NOT_VALID;
            }

            // 檢核 此憑證狀態已為啟用
            if (VcItemDataValidType.ACTIVE.getCode().equals(valid)
                    && StringUtils.equals(VcItemDataValidType.ACTIVE.getAction(), action)) {
                log.warn("已啟用憑證無法再次復用 cid : {}", cid);
                statusCode = StatusCode.DWVP_STATUS_ACTIVE_ACTION_NOT_VALID;
            }

            // 檢核 此憑證狀態已為停用
            if (VcItemDataValidType.SUSPENDED.getCode().equals(valid)
                    && StringUtils.equals(VcItemDataValidType.SUSPENDED.getAction(), action)) {
                log.warn("已停用憑證無法再次停用 cid : {}", cid);
                statusCode = StatusCode.DWVP_STATUS_SUSPENDED_ACTION_NOT_VALID;
            }

        }

        // 建立 fail list
        if (null != statusCode) {
            this.setFailList(failList, statusCode.getCode(), statusCode.getMsg(), cid, true);
        }
    }

    /**
     * 依 action 進行 VC 撤銷、停用、復用，成功後回寫 vc_item_data valid，並建立一筆 VC 資料狀態紀錄
     * 
     * @param vcData
     * @param cid
     * @param lastUpdateTime
     * @param action
     * @return
     */
    public IssuerVC203iRes executeVcActionAndRecordFor302(VCItemData vcData, String cid, Instant lastUpdateTime,
            String action) {
        // 呼叫 API 依 action 進行 VC 撤銷、停用、復用
        IssuerVC203iRes result = remoteApiService.callApiVc203i(cid, action);
        VcItemDataValidType vcStatus = VcItemDataValidType.getByName(result.getCredentialStatus());

        if (null == vcData) {
            vcData = customVCItemDataRepository.findFirstByVcCid(cid);
        }

        // VC 成功撤銷、停用、復用後回寫 vcItemData valid
        if (null != vcData && StringUtils.equalsAny(vcStatus.name(), VcItemDataValidType.ACTIVE.name(),
                VcItemDataValidType.SUSPENDED.name(), VcItemDataValidType.REVOKED.name())) {
            vcData.setValid(vcStatus.getCode());
            customVCItemDataRepository.save(vcData);

            // 建立一筆 VC 資料狀態紀錄
            customVCDataStatusLogService.createStatusLog(cid, vcData.getId(), vcData.getTransactionId(),
                    vcStatus.getCode(), lastUpdateTime, true);
        }

        return result;
    }

    /**
     * 如錯誤代碼不為 61006, 61010, 61050, 69004，依 cid 查詢 credentialStatus
     * 
     * @param cid
     * @param errorCode
     * @return
     */
    private String getCredentialStatus(String cid, String errorCode) {
        String credentialStatus = null;
        if (!StringUtils.equalsAny(errorCode, "61006", "61010", "61050", "69004")) {
            VCCredential vcCredential = customVCCredentialRepository.findByCid(cid);
            if (null != vcCredential) {
                credentialStatus = VcItemDataValidType.getByValue(vcCredential.getCredentialStatus()).name();
            }
        }

        return credentialStatus;
    }

    /**
     * 建立 fail result
     * 
     * @param statusCode
     * @return
     */
    private DwissuerVC203iResp buildFailResult(StatusCode statusCode) {
        DwissuerVC203iResp result = new DwissuerVC203iResp();
        result.setCode(statusCode.getCode());
        result.setMessage(statusCode.getMsg());
        return result;
    }

}
