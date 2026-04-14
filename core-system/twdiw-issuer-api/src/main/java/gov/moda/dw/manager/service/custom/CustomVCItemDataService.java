package gov.moda.dw.manager.service.custom;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.VCCredential;
import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.VCCredentialRepository;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.repository.custom.CustomRegularExpressionRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVCCredentialRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemDataRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.VCItemDataQueryService;
import gov.moda.dw.manager.service.VCItemDataService;
import gov.moda.dw.manager.service.dto.DwIssuerVC402iResDTO;
import gov.moda.dw.manager.service.dto.Dwvc201iResDTO;
import gov.moda.dw.manager.service.dto.Dwvc202iResDTO;
import gov.moda.dw.manager.service.dto.Dwvc202iResDTO.Dwvc202iVcData;
import gov.moda.dw.manager.service.dto.Dwvc203iReqDTO;
import gov.moda.dw.manager.service.dto.Dwvc203iResDTO;
import gov.moda.dw.manager.service.dto.Dwvc203iResDTO.Dwvc203iVcData;
import gov.moda.dw.manager.service.dto.VCDataQrcodeReqDTO;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import gov.moda.dw.manager.service.dto.VcItemData305iRequestDTO;
import gov.moda.dw.manager.service.dto.VcItemData305iResponseDTO;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iReq;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iRes;
import gov.moda.dw.manager.service.dto.custom.GetVcCredentialDTO;
import gov.moda.dw.manager.service.dto.custom.GetVcItemDataDTO;
import gov.moda.dw.manager.service.dto.custom.VCCredentialRevokeVcItemDataDTO;
import gov.moda.dw.manager.service.mapper.VCItemDataMapper;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.StatusCodeSandbox;
import gov.moda.dw.manager.type.VcItemDataValidType;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.SandBoxUtil;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.DWException;

@Service
@Transactional
public class CustomVCItemDataService extends VCItemDataService {

    private final CustomVCItemFieldRepository customVCItemFieldRepository;

    private final CustomVCItemDataRepository customVCItemDataRepository;

    @Value("${vcdata-qrcode-alive-time-min}")
    private int vcdataQrcodeAliveTimeMin;

    @Value("${custom.job.cleanVCDataContent}")
    private Boolean cleanVCData;

    private static final Logger log = LoggerFactory.getLogger(VCItemDataQueryService.class);

    private final CustomVCItemRepository customVCItemRepository;

    private final CustomVCItemFieldRepository vCItemFieldRepository;

    private final CustomVCItemDataRepository vCItemDataRepository;

    private final VCItemDataMapper vCItemDataMapper;

    private final VCItemMapper vcItemMapper;

    private final VCCredentialRepository vcCredentialRepository;

    private final RemoteApiService remoteApiService;

    private final CustomVCCredentialRepository customVCCredentialRepository;

    public CustomVCItemDataService(
        CustomVCItemDataRepository vCItemDataRepository,
        VCItemDataMapper vCItemDataMapper,
        CustomVCItemRepository customVCItemRepository,
        VCItemMapper vcItemMapper,
        CustomVCItemFieldRepository customVCItemFieldRepository,
        CustomRegularExpressionRepository regularExpressionRepository,
        VCCredentialRepository vCCredentialRepository,
        CustomUserRepository customUserRepository,
        CustomOrgRepository customOrgRepository,
        CustomVCCredentialRepository customVCCredentialRepository,
        RemoteApiService remoteApiService,
        CustomUserService customUserService,
        CustomVCItemDataRepository customVCItemDataRepository, 
        CustomVCItemFieldRepository vCItemFieldRepository
    ) {
        super(
            vCItemDataRepository,
            vCItemDataMapper,
            customVCItemRepository,
            vcItemMapper,
            customVCItemFieldRepository,
            regularExpressionRepository,
            customUserRepository,
            customOrgRepository,
            customUserService
        );
        this.customVCItemRepository = customVCItemRepository;
        this.vCItemDataRepository = vCItemDataRepository;
        this.vCItemDataMapper = vCItemDataMapper;
        this.vcItemMapper = vcItemMapper;
        this.vcCredentialRepository = vCCredentialRepository;
        this.customVCItemFieldRepository = customVCItemFieldRepository;
        this.customVCItemDataRepository = customVCItemDataRepository;
        this.customVCCredentialRepository = customVCCredentialRepository;
        this.remoteApiService = remoteApiService;
        this.vCItemFieldRepository = vCItemFieldRepository;
    }

    public Page<VCItemDataDTO> findByCriteria(Page<VCItemData> vcItemDataPages) {
        // 查 qrcode
        List<VCItemData> updateVCCid = new LinkedList<>();
        for (VCItemData vCItemData : vcItemDataPages) {
            // qrcode 存活時間
            Instant crDatetimePlus10Minutes = vCItemData.getCrDatetime().plus(Duration.ofMinutes(vcdataQrcodeAliveTimeMin));

            if ((vCItemData.getVcCid() == null || vCItemData.getVcCid().isEmpty()) && Instant.now().isBefore(crDatetimePlus10Minutes)) {
                DwIssuerVC402iResDTO vc402iRes = remoteApiService.callApi402i(vCItemData.getTransactionId());

                Map<String, Claim> claimMap = null;
                if (vc402iRes != null) {
                    claimMap = SandBoxUtil.getClaims(vc402iRes.getCredential());
                }

                if (claimMap != null && claimMap.containsKey("jti")) {
                    String[] parts = claimMap.get("jti").toString().split("/");
                    String uuid = parts[parts.length - 1].replace("\"", "");
                    vCItemData.setVcCid(uuid);
                    updateVCCid.add(vCItemData);
                }
            }
        }

        if (updateVCCid.size() > 0) {
            vCItemDataRepository.saveAll(updateVCCid);
        }

        Page<VCItemDataDTO> result = vcItemDataPages.map(vcItemData -> {
            VCItemDataDTO itemData = vCItemDataMapper.toDto(vcItemData);

            VCItemDTO item = vcItemMapper.toDto(vcItemData.getVcItem());

            itemData.setVcItem(item);

            return itemData;
        });

        return result;
    }

    public Optional<VCItemDataDTO> findAndUpdateVCDataStatus(Long id) {
        log.debug("Request to get VCItemData : {}", id);

        Optional<VCItemData> vcItemDataOpt = vCItemDataRepository.findById(id);

        if (vcItemDataOpt.isEmpty()) {
            throw new BadRequestAlertException(
                StatusCodeSandbox.Not_Exist.getMsg(),
                "Not_Exist",
                StatusCodeSandbox.Not_Exist.getErrorKey()
            );
        }
        VCItemData vcItemData = vcItemDataOpt.get();

        // qrcode 存活時間
        Instant crDatetimePlus10Minutes = vcItemData.getCrDatetime().plus(Duration.ofMinutes(vcdataQrcodeAliveTimeMin));

        if (
            (vcItemData.getVcCid() == null || vcItemData.getVcCid().isEmpty()) &&
            Instant.now().isBefore(crDatetimePlus10Minutes)
        ) {
            DwIssuerVC402iResDTO vc402iRes = null;
            try {
                vc402iRes = remoteApiService.callApi402i(vcItemData.getTransactionId());
            } catch (Exception e) {
                log.warn("查詢 callApi402i 失敗");
            }

            Map<String, Claim> claimMap = null;
            if (vc402iRes != null) {
                claimMap = SandBoxUtil.getClaims(vc402iRes.getCredential());

                // 取得卡片到期日
                Instant expiredDate = this.findIssuanceDateByTxIds(Arrays.asList(vcItemData.getTransactionId()))
                        .stream().map(VCCredential::getExpirationDate).findFirst().orElse(vcItemData.getExpired());
                vcItemData.setExpired(expiredDate);
            }

            if (claimMap != null && claimMap.containsKey("jti")) {
                String[] parts = claimMap.get("jti").toString().split("/");
                String uuid = parts[parts.length - 1].replace("\"", "");
                vcItemData.setVcCid(uuid);
                vcItemData.setValid(VcItemDataValidType.ACTIVE.getCode());
                vCItemDataRepository.save(vcItemData);
            }
        }

        VCItemDataDTO itemData = vCItemDataMapper.toDto(vcItemData);
        VCItemDTO item = vcItemMapper.toDto(vcItemData.getVcItem());
        itemData.setVcItem(item);
        return Optional.of(itemData);
    }

    public List<VCItemData> findVcItemDataByScheduleId(Long scheduleIdLong, Instant clearScheduleDatetime) {
        return vCItemDataRepository.findByClearScheduleIdAndClearScheduleDatetime(scheduleIdLong, clearScheduleDatetime);
    }

    public int updateContentNull(Long scheduleIdLong, Instant clearScheduleDatetime, String businessId) {
        if (cleanVCData) {
            return vCItemDataRepository.updateContentNullWithClean(scheduleIdLong, clearScheduleDatetime, businessId);
        } else {
            return vCItemDataRepository.updateContentNull(scheduleIdLong, clearScheduleDatetime, businessId);
        }
    }

    public int updateValidAndMsgById(VcItemDataValidType valid, VCItemData vCItemData) {
        return vCItemDataRepository.updateValidAndMessageById(valid.getCode(), vCItemData.getId(), vCItemData.getScheduleRevokeMessage());
    }

    public int updateMessageById(VCItemData vCItemData) {
        return vCItemDataRepository.updateMessageById(vCItemData.getId(), vCItemData.getScheduleRevokeMessage());
    }

    public int updateValidByVcCid(Integer valid, Long id) {
        return vCItemDataRepository.updateValidByVcCid(valid, id);
    }

    public VcItemData305iResponseDTO apiForTest901i(VcItemData305iRequestDTO request) throws JsonProcessingException {
        VcItemData305iResponseDTO result = new VcItemData305iResponseDTO();
        result.setCode("200");
        result.setMessage("成功");
        result.setCredential_type(request.getCredentialType());
        result.setNonce(request.getNonce());

        VCItemData vcItemData = vCItemDataRepository.findBytransactionId(request.getNonce());

        ObjectMapper objectMapper = new ObjectMapper();

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        if (vcItemData == null || vcItemData.getContent() == null || vcItemData.equals("")) {
           if(vcItemData != null && vcItemData.getVcItem() != null && vcItemData.getVcItem().getId() != null){
               List<VCItemField> VCItemField = vCItemFieldRepository.findByVcItemId(vcItemData.getVcItem().getId());
               for (VCItemField temField : VCItemField) {
                   String key = temField.getEname(); // 使用 "ename" 作為
                   resultMap.put(key, key); // 放入目標 Map
               }
           }
        } else {
            List<Map<String, String>> dataList = objectMapper.readValue(
                vcItemData.getContent(),
                new TypeReference<List<Map<String, String>>>() {}
            );
            for (Map<String, String> item : dataList) {
                String key = item.get("ename"); // 使用 "ename" 作為鍵
                Object value = item.get("content"); // 使用 "content" 作為值
                resultMap.put(key, value); // 放入目標 Map
            }
        }

        result.setData(resultMap);

        return result;
    }

    public List<VCCredential> findIssuanceDateByTxIds(List<String> txIds) {
        return vcCredentialRepository.findAllByNonceIn(txIds);
    }

    public DwIssuerOidVci101iRes createQrcode(VCDataQrcodeReqDTO request) {
        Map<String, Claim> jwtClaimMap = SandBoxUtil.getClaims(request.getId_token());

        // 檢查 jwt 中必要的 payload
        if (
            jwtClaimMap == null ||
            jwtClaimMap.isEmpty() ||
            !jwtClaimMap.containsKey("nonce") ||
            jwtClaimMap.get("nonce").equals("") ||
            !jwtClaimMap.containsKey("credential_configuration_id") ||
            jwtClaimMap.get("credential_configuration_id").equals("")
        ) {
            throw new BadRequestAlertException(
                StatusCodeSandbox.Missing_Argument.getMsg(),
                "createQrcode missing argument",
                StatusCodeSandbox.Missing_Argument.getErrorKey()
            );
        }

        String nonce = jwtClaimMap.get("nonce").asString();
        String credentialConfigurationId = jwtClaimMap.get("credential_configuration_id").asString();

        String[] credentialType = SandBoxUtil.splitCredentialType(credentialConfigurationId, "createQrcode");
        String businessId = credentialType[0];
        String serialNo = credentialType[1];

        VCItem vcItem = customVCItemRepository.findBySerialNoAndBusinessId(serialNo, businessId);

        if (vcItem == null) {
            throw new BadRequestAlertException(
                StatusCodeSandbox.VC_Template_Not_Found.getMsg(),
                "credential_configuration_id:" + credentialConfigurationId,
                StatusCodeSandbox.VC_Template_Not_Found.getErrorKey()
            );
        }

        // 要生 qrcode 的 vc 模版的權限檢查
        // 同組織 or 模版為開放 or 特權帳號
        boolean privilege = checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId());
        if (!SecurityUtils.getJwtUserObject().get(0).getOrgId().equals(vcItem.getBusinessId()) && !vcItem.getExpose() && !privilege) {
            throw new BadRequestAlertException(
                StatusCodeSandbox.No_Create_Permission.getMsg(),
                "No_Create_Permission " + SecurityUtils.getJwtUserObject().get(0).getOrgId() + vcItem.getExpose(),
                StatusCodeSandbox.No_Create_Permission.getErrorKey()
            );
        }

        VCItemData vcItemData = vCItemDataRepository.findBytransactionId(nonce);

        String vcOrgId = vcItem.getBusinessId();

        DwIssuerOidVci101iReq req101i = new DwIssuerOidVci101iReq();
        req101i.setAuthenticated(request.getAuthenticated());
        req101i.setIdToken(request.getId_token());

        DwIssuerOidVci101iRes resp = remoteApiService.callApi101i(nonce, vcOrgId, req101i);

        if (vcItemData == null) {
            vcItemData = new VCItemData();
            vcItemData.setVcItem(vcItem);
            vcItemData.setValid(VcItemDataValidType.INACTIVE.getCode());
            vcItemData.setTransactionId(nonce);
            vcItemData.setQrCode(resp.getQrCode());
            vcItemData.setBusinessId(businessId);
            vcItemData.setVcItemName(vcItem.getName());
            vcItemData.setCrUser(getLoginUserId());
            vcItemData.crDatetime(Instant.ofEpochMilli(System.currentTimeMillis()));
        } else {
            vcItemData.setQrCode(resp.getQrCode());
        }

        customVCItemDataRepository.save(vcItemData);

        return resp;
    }

    /**
     * 發行端VC卡片單一資料查詢
     * 
     * @param nonce
     * @return
     */
    public Dwvc201iResDTO findAndUpdateVCDataStatusByNonce(String nonce) {
        log.debug("Request to get VCItemData transaction id: {}", nonce);

        // 查詢 VC 資料
        VCItemData vcItemData = vCItemDataRepository.findBytransactionId(nonce);

        // 檢核 VC 資料
        if (vcItemData == null) {
            throw new DWException(StatusCode.DWVC_TRANSACTION_ID_NOT_FOUND);
        }

        // 呼叫 402i API
        DwIssuerVC402iResDTO vc402iRes = remoteApiService.callApi402i(vcItemData.getTransactionId());

        if (null == vc402iRes) {
            throw new DWException(StatusCode.DWVC_VC_ITEM_DATA_NOT_FOUND);
        }

        // 依取得資料進行解析
        Map<String, Claim> claimMap = SandBoxUtil.getClaims(vc402iRes.getCredential());

        // 更新 VC 資料
        if (claimMap != null && claimMap.containsKey("jti")) {
            String[] parts = claimMap.get("jti").toString().split("/");
            String uuid = parts[parts.length - 1].replace("\"", "");
            vcItemData.setVcCid(uuid);
            if (vcItemData.getValid().equals(VcItemDataValidType.INACTIVE.getCode())) {
                vcItemData.setValid(VcItemDataValidType.ACTIVE.getCode());
            }
            customVCItemDataRepository.save(vcItemData);
        }

        // 建立回傳物件
        Dwvc201iResDTO  dwvc201iResDTO = new Dwvc201iResDTO();
        dwvc201iResDTO.setCredential(vc402iRes.getCredential());

        return dwvc201iResDTO;
    }

    public List<VCCredentialRevokeVcItemDataDTO> findRevokeVcItemDataList(int validType) {
        return customVCCredentialRepository.findRevokeVcItemDataList(validType);
    }

    /**
     * 查詢發行端 VC 卡片 by dataTag
     * 
     * @param dataTag
     * @param pageable
     * @return
     */
    public Dwvc202iResDTO findVcItemDataByDataTag(String dataTag, Pageable pageable) {
        // 檢核 dataTag
        if (!dataTag.matches("^[A-Za-z0-9]+$")) {
            throw new DWException(StatusCode.DWVC_DATA_TAG_NOT_VALID);
        }

        // 查詢 vcCredential by dataTag
        String format = "yyyy-MM-dd HH:mm:ss";
        Page<GetVcCredentialDTO> resultList = vCItemDataRepository.findByDataTag(dataTag, pageable);
        Page<Dwvc202iVcData> dwvc202iVcData = resultList.map(credential -> {
            Dwvc202iVcData vcData = new Dwvc202iVcData();
            vcData.setCid(credential.getCid());
            vcData.setCredentialStatus(credential.getCredentialStatus());
            vcData.setVcUid(credential.getCredentialType());
            vcData.setIssuranceDate(DateUtils.convertDate(credential.getIssuanceDate(), format));
            vcData.setExpirationDate(DateUtils.convertDate(credential.getExpirationDate(), format));
            return vcData;
        });

        Dwvc202iResDTO dwvc202iResDTO = new Dwvc202iResDTO();
        dwvc202iResDTO.setDataTag(dataTag);
        dwvc202iResDTO.setVcList(dwvc202iVcData.getContent());

        return dwvc202iResDTO;
    }

    /**
     * 透過 VC 卡片相關資訊查詢發行端 VC 卡片
     * 
     * @param request
     * @return
     */
    public Dwvc203iResDTO findVcItemData(Dwvc203iReqDTO request) {
        // 檢核 dataTag
        if (StringUtils.isNotBlank(request.getDataTag()) && !request.getDataTag().matches("^[A-Za-z0-9]+$")) {
            throw new DWException(StatusCode.DWVP_DATA_TAG_NOT_VALID);
        }

        // 檢核 credentialStatus
        String credentialStatus = request.getCredentialStatus();
        if (StringUtils.isNotBlank(credentialStatus)) {
            VcItemDataValidType vcItemDataValidType = VcItemDataValidType
                    .getByCodeIgnoreInactiveAndUnknown(credentialStatus);
            if (vcItemDataValidType == VcItemDataValidType.UNKNOWN) {
                throw new DWException(StatusCode.DWVP_CREDENTIAL_STATUS_NOT_VALID);
            }
        }

        // 分頁
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // 查詢 vcItemData & vcCredential
        String format = "yyyy-MM-dd HH:mm:ss";
        Page<GetVcItemDataDTO> resultList = vCItemDataRepository.findVcItemData(request.getDataTag(),
                request.getVcUid(), credentialStatus, pageable);
        Page<Dwvc203iVcData> dwvc203iVcData = resultList.map(credential -> {
            Dwvc203iVcData vcData = new Dwvc203iVcData();
            vcData.setDataTag(credential.getDataTag());
            vcData.setCid(credential.getCid());
            vcData.setCredentialStatus(credential.getCredentialStatus());
            vcData.setVcUid(credential.getCredentialType());
            vcData.setIssuranceDate(DateUtils.convertDate(credential.getIssuanceDate(), format));
            vcData.setExpirationDate(DateUtils.convertDate(credential.getExpirationDate(), format));
            return vcData;
        });

        Dwvc203iResDTO dwvc203iResDTO = new Dwvc203iResDTO();
        dwvc203iResDTO.setVcList(dwvc203iVcData.getContent());

        return dwvc203iResDTO;
    }

}
