package gov.moda.dw.manager.service.custom;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.util.Base64URL;

import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.domain.VPItemField;
import gov.moda.dw.manager.domain.VPVerifyResult;
import gov.moda.dw.manager.domain.outside.vdr.category.CategoryVdrSimpleDto;
import gov.moda.dw.manager.repository.VPItemRepository;
import gov.moda.dw.manager.repository.custom.CustomOidvpConfigRepository;
import gov.moda.dw.manager.repository.custom.CustomVPItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVPItemRepository;
import gov.moda.dw.manager.repository.custom.CustomVPVerifyResultRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.OidvpConfigService;
import gov.moda.dw.manager.service.custom.common.QRCodeService;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.dto.VPItemDTO;
import gov.moda.dw.manager.service.dto.custom.CustomFieldReqDTO;
import gov.moda.dw.manager.service.dto.custom.CustomVPItemDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxV401WCreateAndEditResDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVP401RequestDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVP401WDownloadStaticQRCodeReqDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVP401WStaticQRCodeResDTO;
import gov.moda.dw.manager.service.dto.custom.GetVPItemDataResDTO;
import gov.moda.dw.manager.service.dto.custom.ModelTypeDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemGroupReqDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemGroupVcDataFieldReqDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemGroupVcDataReqDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemReqDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemSaveTermsDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemSearchAllResDTO;
import gov.moda.dw.manager.service.dto.custom.ValidVPItemByStepReqDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.VerifierOid4vp101iRespDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.VerifierOid4vp101iV2RespDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.Constraints;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.Contains;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.Field;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.Filter;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.InputDescriptor;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.InputDescriptorNameDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.PresentationDefinitionDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.PurposeDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.payload.SubmissionRequirement;
import gov.moda.dw.manager.service.dto.outside.VCItemDataDTO;
import gov.moda.dw.manager.service.mapper.VPItemMapper;
import gov.moda.dw.manager.type.LoginSourceType;
import gov.moda.dw.manager.type.ModelType;
import gov.moda.dw.manager.type.QrcodeLinkType;
import gov.moda.dw.manager.type.RuleType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.DWException;

@Service
public class DwSandBoxVP401WService {
    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVP401WService.class);

    @Value("${verifierOid4vpUrl}")
    private String verifierOid4vpUrl;

    @Value("${dwfront.url}")
    private String dwfrontUrl;
    
    @Value("${qrcode.link.type:1}")
    private String qrcodeLinkType;

    private static final String VDR = "VDR";

    // QR Code 的前綴字串，用於Base64編碼的圖片資料
    private static final String QR_CODE_PREFIX = "data:image/png;base64,";

    // QR Code 的 LOGO，用於Base64編碼的圖片資料
    private static final String QR_CODE_LOGO = "img/dwIcon.png";

    private final VPItemRepository vpItemRepository;
    private final CustomVPItemRepository customVPItemRepository;
    private final CustomVPItemFieldRepository customVpItemFieldRepository;
    private final CustomVPVerifyResultRepository customVPVerifyResultRepository;
    private final CustomOidvpConfigRepository customOidvpConfigRepository;
    private final VPItemMapper vpItemMapper;
    private final CustomVPItemAuthHelperService customVPItemAuthHelperService;
    private final RemoteApiService remoteApiService;
    private final CustomOrgService customOrgService;
    private final CustomCategoryService customCategoryService;
    private final QRCodeService qrCodeService;
    private ObjectMapper objectMapper;

    public DwSandBoxVP401WService(VPItemRepository vpItemRepository, CustomVPItemRepository customVPItemRepository,
            CustomVPItemFieldRepository customVpItemFieldRepository,
            CustomVPVerifyResultRepository customVPVerifyResultRepository,
            CustomOidvpConfigRepository customOidvpConfigRepository, VPItemMapper vpItemMapper,
            CustomVPItemAuthHelperService customVPItemAuthHelperService, RemoteApiService remoteApiService,
            CustomOrgService customOrgService, CustomCategoryService customCategoryService, QRCodeService qrCodeService,
            OidvpConfigService oidvpConfigService) {
        this.customVPItemAuthHelperService = customVPItemAuthHelperService;
        this.vpItemRepository = vpItemRepository;
        this.customVPItemRepository = customVPItemRepository;
        this.customVpItemFieldRepository = customVpItemFieldRepository;
        this.customVPVerifyResultRepository = customVPVerifyResultRepository;
        this.customOidvpConfigRepository = customOidvpConfigRepository;
        this.vpItemMapper = vpItemMapper;
        this.remoteApiService = remoteApiService;
        this.customOrgService = customOrgService;
        this.customCategoryService = customCategoryService;
        this.qrCodeService = qrCodeService;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    @Transactional(readOnly = true)
    public int countBySerialNo(String serialNo) {
        log.debug("Request to get VPItem By serialNo : {}", serialNo);
        return customVPItemRepository.countBySerialNo(serialNo);
    }

    public Page<VPItemSearchAllResDTO> getAllVPItems(DwSandBoxVP401RequestDTO req, Pageable pageable) {

        // VP代碼
        String serialNo = null;

        // VP名稱
        String name = null;

        // 最後更新時間(起)
        Instant startDate = null;

        // 最後更新時間(迄)
        Instant endDate = null;

        if (req.getSerialNo() != null) {
            serialNo = req.getSerialNo();
        }
        if (req.getName() != null) {
            name = req.getName();
        }
        if (req.getStartDate() != null) {
            startDate = req.getStartDate();
        }
        if (req.getEndDate() != null) {
            endDate = req.getEndDate();
        }

        // 初始化
        String loginName = SecurityUtils.getJwtUserObject().get(0).getUserId();
        Long userId = customVPItemAuthHelperService.getLoginUserId();
        Long crUser = null;
        String businessId = null;
        List<VPItemSearchAllResDTO> resultList = new ArrayList<>();

        // 確認登入者身份
        Pair<String, Object> sqlCondition = checkIdentity(loginName);
        if (sqlCondition.getLeft() != null) {
            if ("crUser".equals(sqlCondition.getLeft()))
                crUser = Long.valueOf(String.valueOf(sqlCondition.getRight()));
            else if ("business".equals(sqlCondition.getLeft()))
                businessId = String.valueOf(sqlCondition.getRight());
        }

        // [查詢] VPItem
        Page<CustomVPItemDTO> vpItemDtoPage = customVPItemRepository.getVPPage(crUser, businessId, serialNo, name,
                startDate, endDate, pageable);
        // 查DID

        for (CustomVPItemDTO vpItemDTO : vpItemDtoPage) {
            boolean owner = false;
            boolean isShowEditIcon = false;
            boolean isShowTermsIcon = false;
            VPItemSearchAllResDTO vpItemSearchAllResDTO = new VPItemSearchAllResDTO();
            vpItemSearchAllResDTO.setId(vpItemDTO.getId());
            vpItemSearchAllResDTO.setSerialNo(vpItemDTO.getSerialNo());
            vpItemSearchAllResDTO.setName(vpItemDTO.getName());
            vpItemSearchAllResDTO.setCrUser(vpItemDTO.getCrUser());
            vpItemSearchAllResDTO.setUpDatetime(vpItemDTO.getUpDatetime());
            vpItemSearchAllResDTO.setBusinessId(vpItemDTO.getBusinessId());
            vpItemSearchAllResDTO.setPresentationDefinition(vpItemDTO.getPresentationDefinition());
            vpItemSearchAllResDTO.setTerms(vpItemDTO.getTerms());
            vpItemSearchAllResDTO.setPurpose(vpItemDTO.getPurpose());
            vpItemSearchAllResDTO.setVerifierServiceUrl(vpItemDTO.getVerifierServiceUrl());
            vpItemSearchAllResDTO.setCallBackUrl(vpItemDTO.getCallBackUrl());
            vpItemSearchAllResDTO.setTag(vpItemDTO.getTag());
            vpItemSearchAllResDTO.setIsEncryptEnabled(vpItemDTO.getIsEncryptEnabled());
            // 處理模式
            Map<String, String> modelMap = getModelMap(vpItemDTO.getModel());
            vpItemSearchAllResDTO.setModel(modelMap);

            // 處理描述與欄位
            String jsonFieldInfo = vpItemDTO.getFieldInfo();
            List<CustomFieldReqDTO> fieldList = new ArrayList<>();

            if (StringUtils.isNotEmpty(jsonFieldInfo)) {
                // 讀取Json取得欄位
                fieldList = getCustomFields(jsonFieldInfo);
            }

            vpItemSearchAllResDTO.setFields(fieldList);

            if (userId.equals(vpItemDTO.getCrUser())) {
                owner = true;
            }
            vpItemSearchAllResDTO.setOwner(owner);

            boolean hasTerms = StringUtils.isNotBlank(vpItemDTO.getTerms());
            boolean hasGroupInfo = StringUtils.isNotBlank(vpItemDTO.getGroupInfo());

            if (hasTerms) {
                isShowTermsIcon = true;

                if (hasGroupInfo) {
                    // 新版 VP：有條款＋有 group_info
                    isShowEditIcon = true;
                }
            }
            vpItemSearchAllResDTO.setShowEditIcon(isShowEditIcon);
            vpItemSearchAllResDTO.setShowTermsIcon(isShowTermsIcon);

            resultList.add(vpItemSearchAllResDTO);
        }

        return new PageImpl<>(resultList, pageable, vpItemDtoPage.getTotalElements());
    }

    /**
     * 取得模式下拉選單
     * 
     * @return List<ModelTypeDTO>
     */
    public List<ModelTypeDTO> getModelTypeSelect() {
        List<ModelTypeDTO> modelTypeDTOs = new ArrayList<>();

        for (ModelType type : ModelType.values()) {
            ModelTypeDTO dto = new ModelTypeDTO();
            dto.setLabel(type.getLabel());
            dto.setValue(type.getValue());
            modelTypeDTOs.add(dto);
        }

        return modelTypeDTOs;
    }

    @Transactional
    public DwSandBoxV401WCreateAndEditResDTO save(UpsertVPItemReqDTO request) {
        log.debug("Request to save VPItem");

        // 檢核 VPItem by step
        ValidVPItemByStepReqDTO validVPItemByStepReqDTO = new ValidVPItemByStepReqDTO();
        validVPItemByStepReqDTO.setSerialNo(request.getSerialNo());
        validVPItemByStepReqDTO.setName(request.getName());
        validVPItemByStepReqDTO.setPurpose(request.getPurpose());
        validVPItemByStepReqDTO.setTerms(request.getTerms());
        validVPItemByStepReqDTO.setIsEdit(false);
        validVPItemByStepReqDTO.setGroups(request.getGroups());
        validVPItemByStepReqDTO.setIsEncryptEnabled(request.getIsEncryptEnabled());
        this.validVPItemByStep(validVPItemByStepReqDTO, true);

        String businessId = customVPItemAuthHelperService.getBusinessId();
        long crUser = customVPItemAuthHelperService.getLoginUserId();
        String serialNo = request.getSerialNo();

        int countBySerialNoAndBusinessId = customVPItemRepository.countBySerialNoAndBusinessId(serialNo, businessId);
        if (countBySerialNoAndBusinessId > 0) {
            throw new BadRequestAlertException("exists error", "vpItem", "serialNo already exists");
        }

        // 清除不安全的 HTML 標籤與屬性
        request.setTerms(Encode.forHtml(request.getTerms()));

        // 建立一個新的 group 移除 vcDatas.isTicked 為 false 的資料
        List<UpsertVPItemGroupReqDTO> filteredGroups = request.getGroups().stream().map(group -> {
            // 移除 isTicked 為 false 的資料
            List<UpsertVPItemGroupVcDataReqDTO> filteredVcDatas = group.getVcDatas().stream()
                    .filter(UpsertVPItemGroupVcDataReqDTO::getIsTicked).toList();

            // 建立新的 group
            UpsertVPItemGroupReqDTO newGroup = new UpsertVPItemGroupReqDTO();
            newGroup.setName(group.getName());
            newGroup.setRule(group.getRule());
            newGroup.setMax(group.getMax());
            newGroup.setVcDatas(filteredVcDatas);

            return newGroup;
        }).toList();

        // 模式資料
        boolean isStatic = false;
        boolean isOffline = false;
        ModelType modelType = ModelType.toModelType(request.getModel());
        if (modelType != null) {
            isStatic = modelType.isStatic();
            isOffline = modelType.isOffline();
        }

        // 建立 vpItem
        VPItem vpItem = new VPItem();
        vpItem.setCrUser(crUser);
        vpItem.setSerialNo(serialNo);
        vpItem.setName(request.getName());
        vpItem.setCrDatetime(Instant.now());
        vpItem.setUpDatetime(Instant.now());
        vpItem.setBusinessId(businessId);
        vpItem.setPurpose(request.getPurpose());
        vpItem.setTerms(request.getTerms());
        vpItem.setPresentationDefinition(this.generatePresentationDefinition(businessId, serialNo, request.getName(),
                request.getPurpose(), filteredGroups));
        vpItem.setGroupInfo(this.objectToEscapeString(filteredGroups));
        vpItem.setIsStatic(isStatic);
        vpItem.setIsOffline(isOffline);

        // 靜態 QRCode 模式
        if (isStatic) {
            // callBackUrl
            if (StringUtils.isNotEmpty(request.getCallBackUrl())) {
                vpItem.setCallbackUrl(request.getCallBackUrl());
            }

            // 組織業務系統URL
            if (StringUtils.isNotEmpty(request.getVerifierServiceUrl())) {
                vpItem.setVerifierServiceUrl(request.getVerifierServiceUrl());
            }

            // 欄位
            if (!CollectionUtils.isEmpty(request.getFields())) {
                // 組成 map
                Map<String, Object> map = new HashMap<>();
                map.put("fields", request.getFields());

                vpItem.setFieldInfo(combineIntoJson(map));
            }
        }

        // Offline 模式
        if (isOffline) {
            // 模組加密
            vpItem.setIsEncryptEnabled(request.getIsEncryptEnabled());

            // tag
            if (Boolean.TRUE.equals(request.getIsEncryptEnabled()) && StringUtils.isNotEmpty(request.getTag())) {
                vpItem.setTag(request.getTag());
            }
        }

        VPItem vpItemSaved = vpItemRepository.save(vpItem);

        // 建立 vpItemField
        List<VPItemField> vpItemFields = filteredGroups.stream().flatMap(group -> group.getVcDatas().stream().flatMap(
                vcData -> vcData.getVcFields().stream().filter(vcField -> vcField.getIsTicked()).map(vcField -> {
                    VPItemField vpf = new VPItemField();
                    vpf.setVcName(vcData.getName());
                    vpf.setCname(vcField.getCname());
                    vpf.setEname(vcField.getEname());
                    vpf.setVcItemFieldType(VDR);
                    vpf.setVpItemId(vpItemSaved.getId());
                    vpf.setVcSerialNo(vcData.getSerialNo());
                    vpf.setVcCategoryDescription(VDR);
                    vpf.setVcBusinessId(vcData.getBusinessId());
                    vpf.setVpBusinessId(businessId);
                    vpf.setIsRequired(vcField.getIsRequired());
                    return vpf;
                }))).toList();
        customVpItemFieldRepository.saveAll(vpItemFields);

        // 建立回傳
        VPItemDTO vpItemDTO = vpItemMapper.toDto(vpItem);
        return buildCreateAndEditResponse(vpItemDTO, request.getModel());
    }

    @Transactional
    public void deleteWithField(Long id) {
        log.debug("Request to delete VPItem : {}", id);
        customVPItemAuthHelperService.checkVpItemAuthByVpId(id, "deleteWithField");
        List<VPItemField> fields = customVpItemFieldRepository.findByVpItemId(id);
        customVpItemFieldRepository.deleteAll(fields);
        vpItemRepository.deleteById(id);
    }

    public VerifierOid4vp101iV2RespDTO getQrcodeExternal(String ref, String transactionId, String isCallback) {
        // 檢核 ref 是否為空
        if (StringUtils.isBlank(ref)) {
            throw new DWException(StatusCode.DWVP_REF_NOT_FOUND);
        }

        // 檢核 transactionId 是否為空
        if (StringUtils.isBlank(transactionId)) {
            throw new DWException(StatusCode.DWVP_TRANSACTION_ID_NOT_FOUND);
        }

        // 檢核 ref 格式
        int firstIndex = ref.indexOf("_");
        if (firstIndex < 0) {
            throw new DWException(StatusCode.DWVP_REF_NOT_VALID);
        }

        // 查詢 VP 模板
        String businessId = ref.substring(0, firstIndex);
        String serialNo = ref.substring(firstIndex + 1, ref.length());
        Optional<VPItem> vpItemOpt = customVPItemRepository.findBySerialNoAndBusinessId(serialNo, businessId);

        // 檢核 VP 模板是否存在
        if (vpItemOpt.isEmpty()) {
            throw new DWException(StatusCode.DWVP_VP_ITEM_NOT_FOUND);
        } else {
            VPItem vpItem = vpItemOpt.get();
            VerifierOid4vp101iRespDTO qrcodeResult = this.getQrcode(vpItem.getId(), transactionId, isCallback);
            VerifierOid4vp101iV2RespDTO response = new VerifierOid4vp101iV2RespDTO();
            response.setTransactionId(qrcodeResult.getTransactionId());
            response.setQrcodeImage(qrcodeResult.getQrcodeImage());
            response.setAuthUri(qrcodeResult.getAuthUri());

            return response;
        }
    }

    public VerifierOid4vp101iRespDTO getQrcode(Long id, String transactionId, String isCallback) {
        customVPItemAuthHelperService.checkVpItemAuthByVpId(id, "getQrcode");
        Optional<VPItem> byId = customVPItemRepository.findById(id);
        VerifierOid4vp101iRespDTO verifierOid4vp101iRespDTO = new VerifierOid4vp101iRespDTO();
        Long vpItemId = null;
        if (byId.isPresent()) {
            VPItem vpItem = byId.get();
            vpItemId = vpItem.getId();
            String ref = vpItem.getBusinessId() + "_" + vpItem.getSerialNo();
            String callback = "";
            // 判斷是否需 callback
            if ("Y".equals(isCallback) && Boolean.TRUE.equals(vpItem.getIsStatic())) {
                String oidvpDomainUri = customOidvpConfigRepository.findByPropertyKey("oidvp.domain-uri")
                        .map(OidvpConfig::getPropertyValue)
                        .orElseThrow(() -> new DWException(StatusCode.DWVP_DOMAIN_URI_NOT_FOUND));
                callback = oidvpDomainUri + "/api/ext/result";
            }
            log.info("callback : {}", callback);

            verifierOid4vp101iRespDTO = remoteApiService.verifierOid4vp101i(ref, transactionId, callback);
            if (StringUtils.isNotBlank(verifierOid4vp101iRespDTO.getCode())
                    && !StringUtils.equals("0", verifierOid4vp101iRespDTO.getCode())) {
                throw new DWException(verifierOid4vp101iRespDTO.getCode(), verifierOid4vp101iRespDTO.getMessage());
            }

            if (verifierOid4vp101iRespDTO.getAuthUri() != null) {
                QrcodeLinkType qrcodeLinkTypeEnum = QrcodeLinkType.toQrcodeLinkType(qrcodeLinkType);
                if (QrcodeLinkType.UNIVERSAL_LINK.equals(qrcodeLinkTypeEnum)) {
                    String universalLink = dwfrontUrl + "/api/moda/vpqrcode?mode=vp01&deeplink=" + Base64URL.encode(verifierOid4vp101iRespDTO.getAuthUri());
                    verifierOid4vp101iRespDTO.setAuthUri(universalLink);
                }
                verifierOid4vp101iRespDTO
                        .setQrcodeImage(this.generateQRCodeFromLink(verifierOid4vp101iRespDTO.getAuthUri()));
            }
        }

        // 寫入 verifyResult表
        VPVerifyResult vpVerifyResult = new VPVerifyResult();
        vpVerifyResult.setTransactionId(transactionId);
        vpVerifyResult.setVpItemId(vpItemId);
        vpVerifyResult.setCrDatetime(Instant.now());
        customVPVerifyResultRepository.save(vpVerifyResult);

        return verifierOid4vp101iRespDTO;
    }

    /**
     * 取得驗證結果
     * 
     * @param transactionId
     * @param responseCode
     * @return
     */
    public JsonNode getVerifyResult(String transactionId, String responseCode) {
        customVPItemAuthHelperService.checkVpItemAuthByTxId(transactionId, "getVerifyResult");
        return this.getPayloadResult(transactionId, responseCode);
    }
    
    /**
     * 取得驗證結果
     * 供 WEB 使用
     * 
     * @param transactionId
     * @param responseCode
     * @return
     */
    public JsonNode getVerifyResultForWeb(String transactionId, String responseCode) {
        customVPItemAuthHelperService.checkVpItemAuthByTxId(transactionId, "getVerifyResult");
        return this.getPayloadResultForWeb(transactionId, responseCode);
    }

    /**
     * 修改使用者條款
     *
     * @param vpItemSaveTermsDTO
     * @return
     */
    @Transactional
    public VPItemDTO saveTerms(VPItemSaveTermsDTO vpItemSaveTermsDTO) {
        log.debug("Request to saveTerms");
        VPItem vpItem = vpItemRepository.findById(vpItemSaveTermsDTO.getId())
                .orElseThrow(() -> new BadRequestAlertException("not found error", "Input Error",
                        "找不到相對應的VCItem: " + vpItemSaveTermsDTO.getSerialNo()));

        Long currentUserId = customVPItemAuthHelperService.getLoginUserId();

        // 清除不安全的 HTML 標籤與屬性
        vpItem.setTerms(Encode.forHtml(vpItemSaveTermsDTO.getTerms()));
        vpItem.setUpUser(currentUserId);
        vpItem.setUpDatetime(Instant.now());

        // 儲存 vpItem
        vpItemRepository.save(vpItem);

        return vpItemMapper.toDto(vpItem);
    }

    /**
     * 查詢使用者條款
     *
     * @param serialNo
     * @param bussinessId
     * @return
     */
    public String getTerms(String serialNo, String bussinessId) {
        String result = "";
        Optional<VPItem> vpItemOpt = customVPItemRepository.findBySerialNoAndBusinessId(serialNo, bussinessId);

        if (vpItemOpt.isPresent()) {
            result = vpItemOpt.get().getTerms();
        }

        return result;
    }

    /**
     * Update a VPItem. By VDR
     *
     * @param request
     * @param id
     * @return the persisted entity.
     */
    @Transactional
    public DwSandBoxV401WCreateAndEditResDTO update(UpsertVPItemReqDTO request, Long id) {
        log.debug("Request to update VPItem");

        // 檢核 VPItem by step
        ValidVPItemByStepReqDTO validVPItemByStepReqDTO = new ValidVPItemByStepReqDTO();
        validVPItemByStepReqDTO.setSerialNo(request.getSerialNo());
        validVPItemByStepReqDTO.setName(request.getName());
        validVPItemByStepReqDTO.setPurpose(request.getPurpose());
        validVPItemByStepReqDTO.setTerms(request.getTerms());
        validVPItemByStepReqDTO.setIsEdit(true);
        validVPItemByStepReqDTO.setGroups(request.getGroups());
        validVPItemByStepReqDTO.setIsEncryptEnabled(request.getIsEncryptEnabled());
        this.validVPItemByStep(validVPItemByStepReqDTO, true);

        Long currentUserId = customVPItemAuthHelperService.getLoginUserId();

        // 檢查權限
        customVPItemAuthHelperService.checkVpItemAuthByVpId(id, "update");

        // 查找現有的 VPItem
        VPItem existingVPItem = vpItemRepository.findById(id)
                .orElseThrow(() -> new BadRequestAlertException("Entity not found", "VPItem", "idnotfound"));

        // 清除不安全的 HTML 標籤與屬性
        request.setTerms(Encode.forHtml(request.getTerms()));

        // 建立一個新的 group 移除 vcDatas.isTicked 為 false 的資料
        List<UpsertVPItemGroupReqDTO> filteredGroups = request.getGroups().stream().map(group -> {
            // 移除 isTicked 為 false 的資料
            List<UpsertVPItemGroupVcDataReqDTO> filteredVcDatas = group.getVcDatas().stream()
                    .filter(UpsertVPItemGroupVcDataReqDTO::getIsTicked).toList();

            // 建立新的 group
            UpsertVPItemGroupReqDTO newGroup = new UpsertVPItemGroupReqDTO();
            newGroup.setName(group.getName());
            newGroup.setRule(group.getRule());
            newGroup.setMax(group.getMax());
            newGroup.setVcDatas(filteredVcDatas);

            return newGroup;
        }).toList();

        // 模式資料
        boolean isStatic = false;
        boolean isOffline = false;
        ModelType modelType = ModelType.toModelType(request.getModel());
        if (modelType != null) {
            isStatic = modelType.isStatic();
            isOffline = modelType.isOffline();
        }

        // 更新 vpItem
        existingVPItem.setName(request.getName());
        existingVPItem.setPurpose(request.getPurpose());
        existingVPItem.setTerms(request.getTerms());
        existingVPItem.setUpUser(currentUserId);
        existingVPItem.setUpDatetime(Instant.now());
        existingVPItem.setPresentationDefinition(this.generatePresentationDefinition(existingVPItem.getBusinessId(),
                existingVPItem.getSerialNo(), request.getName(), request.getPurpose(), filteredGroups));
        existingVPItem.setGroupInfo(this.objectToEscapeString(filteredGroups));
        existingVPItem.setIsStatic(isStatic);
        existingVPItem.setIsOffline(isOffline);

        // 靜態 QRCode 模式
        if (isStatic) {
            existingVPItem.setCallbackUrl(request.getCallBackUrl());
            existingVPItem.setVerifierServiceUrl(request.getVerifierServiceUrl());

            // 欄位
            if (!CollectionUtils.isEmpty(request.getFields())) {
                // 組成 map
                Map<String, Object> map = new HashMap<>();
                map.put("fields", request.getFields());

                existingVPItem.setFieldInfo(combineIntoJson(map));
            } else {
                existingVPItem.setFieldInfo(null);
            }

            existingVPItem.setIsEncryptEnabled(null);
            existingVPItem.setTag(null);
        } else if (isOffline) {
            existingVPItem.setIsEncryptEnabled(request.getIsEncryptEnabled());
            existingVPItem.setTag(request.getTag());
            existingVPItem.setCallbackUrl(null);
            existingVPItem.setVerifierServiceUrl(null);
            existingVPItem.setFieldInfo(null);
        } else {
            existingVPItem.setIsEncryptEnabled(null);
            existingVPItem.setTag(null);
            existingVPItem.setCallbackUrl(null);
            existingVPItem.setVerifierServiceUrl(null);
            existingVPItem.setFieldInfo(null);
        }

        VPItem vpItemSaved = vpItemRepository.save(existingVPItem);

        // 刪除舊的 vpItemField
        List<VPItemField> existingFields = customVpItemFieldRepository.findByVpItemId(id);
        customVpItemFieldRepository.deleteAll(existingFields);

        // 建立新的 vpItemField
        List<VPItemField> vpItemFields = filteredGroups.stream().flatMap(group -> group.getVcDatas().stream().flatMap(
                vcData -> vcData.getVcFields().stream().filter(vcField -> vcField.getIsTicked()).map(vcField -> {
                    VPItemField vpf = new VPItemField();
                    vpf.setVcName(vcData.getName());
                    vpf.setCname(vcField.getCname());
                    vpf.setEname(vcField.getEname());
                    vpf.setVcItemFieldType(VDR);
                    vpf.setVpItemId(vpItemSaved.getId());
                    vpf.setVcSerialNo(vcData.getSerialNo());
                    vpf.setVcCategoryDescription(VDR);
                    vpf.setVcBusinessId(vcData.getBusinessId());
                    vpf.setVpBusinessId(vpItemSaved.getBusinessId());
                    vpf.setIsRequired(vcField.getIsRequired());
                    return vpf;
                }))).toList();
        customVpItemFieldRepository.saveAll(vpItemFields);

        // 建立回傳
        VPItemDTO vpItemDTO = vpItemMapper.toDto(vpItemSaved);
        return buildCreateAndEditResponse(vpItemDTO, request.getModel());
    }

    /**
     * 取得 VPItem by id
     * 
     * @param vpItemId
     * @return
     */
    public GetVPItemDataResDTO getVPItem(Long vpItemId) {
        // 檢查權限
        customVPItemAuthHelperService.checkVpItemAuthByVpId(vpItemId, "getVPItem");

        // 查詢 vpItem by id
        Optional<VPItem> vpItemOpt = customVPItemRepository.findById(vpItemId);
        if (vpItemOpt.isEmpty()) {
            throw new BadRequestAlertException("not found error", "Input Error", "找不到相對應的VPItem");
        }

        // 取得 vpItem
        VPItem vpItem = vpItemOpt.get();

        // 建立 groups
        List<UpsertVPItemGroupReqDTO> groups = this.setGroups(vpItem);

        // 取得模式
        String model = "";
        if (vpItem.getIsStatic()) {
            model = ModelType.MODEL_2.getValue();
        } else if (vpItem.getIsOffline()) {
            model = ModelType.MODEL_3.getValue();
        } else {
            model = ModelType.MODEL_1.getValue();
        }

        // 處理描述與欄位
        String jsonFieldInfo = vpItem.getFieldInfo();
        List<CustomFieldReqDTO> fieldList = new ArrayList<>();

        if (StringUtils.isNotEmpty(jsonFieldInfo)) {
            // 讀取Json取得欄位
            fieldList = getCustomFields(jsonFieldInfo);
        }

        // 回傳 response
        GetVPItemDataResDTO response = new GetVPItemDataResDTO();
        response.setVpItemId(vpItemId);
        response.setSerialNo(vpItem.getSerialNo());
        response.setName(vpItem.getName());
        response.setPurpose(vpItem.getPurpose());
        response.setTerms(vpItem.getTerms());
        response.setGroups(groups);
        response.setModel(model);
        response.setVerifierServiceUrl(vpItem.getVerifierServiceUrl());
        response.setCallBackUrl(vpItem.getCallbackUrl());
        response.setTag(vpItem.getTag());
        response.setIsEncryptEnabled(vpItem.getIsEncryptEnabled());
        response.setFields(fieldList);

        return response;
    }

    /**
     * 檢核 VPItem by step
     * 
     * @param request
     * @param isAllValid
     * @return
     */
    public void validVPItemByStep(ValidVPItemByStepReqDTO request, boolean isAllValid) {
        Integer step = request.getStep();
        boolean checkStep1 = isAllValid || 1 == step;
        boolean checkStep2 = isAllValid || 2 == step;
        boolean checkStep3 = isAllValid || 3 == step;

        if (Boolean.FALSE.equals(request.getIsEdit()) && checkStep1) {
            this.validStep1(request);
        }

        if (!checkStep2 && !checkStep3) {
            return;
        }

        if (CollectionUtils.isEmpty(request.getGroups())) {
            throw new BadRequestAlertException("not found error", "vpItem", "groups not found");
        }

        if (checkStep2) {
            this.validStep2(request);
        }

        if (checkStep3) {
            this.validStep3(request);
        }
    }

    /**
     * 產製靜態QRCode
     * 
     * @param credentialType
     * @return QR 碼的 base64 格式字串，包含 data:image/png;base64, 前綴
     */
    public DwSandBoxVP401WStaticQRCodeResDTO getStaticQRCode(String credentialType) {
        DwSandBoxVP401WStaticQRCodeResDTO response = new DwSandBoxVP401WStaticQRCodeResDTO();
        // 如果 credentialType 為空，回傳空字串
        if (StringUtils.isBlank(credentialType)) {
            throw new BadRequestAlertException(StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getMsg(),
                    "credentialType  為空: " + credentialType, StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getCode());
        }

        try {
            // 組合
            String url = dwfrontUrl + "/api/moda/qrcode?type=staticqrcode&mode=vp&vpUid=" + credentialType;
            // 轉換Logo String -> BufferedImage
            BufferedImage bufferedImage = ImageIO.read(new ClassPathResource(QR_CODE_LOGO).getInputStream());
            // 製作QRCode
            String result = qrCodeService.generateQRCodeWithLogoSpace(url, bufferedImage);

            response.setBase64(QR_CODE_PREFIX + result);
            response.setUrl(url);

            return response;
        } catch (Exception e) {
            log.error("生成 QR 碼失敗，DeepLink: {}, 錯誤: {}", credentialType, ExceptionUtils.getStackTrace(e));
            throw new BadRequestAlertException("生成 QR 碼失敗", e.getMessage(), "QRCodeGenerationError");
        }
    }

    /**
     * 下載靜態QRCode
     * 
     * @param req
     * @return
     */
    public byte[] downloadStaticQRCode(DwSandBoxVP401WDownloadStaticQRCodeReqDTO req) {
        String base64 = req.getBase64();
        // [檢核] 判空
        if (StringUtils.isEmpty(base64)) {
            throw new BadRequestAlertException(StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getMsg(), base64,
                    StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getCode());
        }

        // 分割 前綴 data:image/png;base64,
        String[] splitBase64 = base64.split(",");

        // [檢核] 判斷base64分割後長度
        if (splitBase64.length <= 1) {
            throw new BadRequestAlertException(StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getMsg(), base64,
                    StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getCode());
        }

        String data = splitBase64[1];
        byte[] imageByte = Base64.getDecoder().decode(data);

        return imageByte;
    }

    /**
     * 建立 PresentationDefinition
     * 
     * @param businessId
     * @param serialNo
     * @param name
     * @param purpose
     * @param groups
     * @return
     */
    private String generatePresentationDefinition(String businessId, String serialNo, String name, String purpose,
            List<UpsertVPItemGroupReqDTO> groups) {
        // 授權單位名稱、授權條款 URL、驗證情境主題、授權目的
        OrgDTO orgDto = customOrgService.findOneByOrgId(businessId);
        String termsUri = verifierOid4vpUrl + "/api/terms/search/" + serialNo + "/" + businessId;
        PurposeDTO purposeDTO = new PurposeDTO();
        purposeDTO.setClient(orgDto.getOrgTwName());
        purposeDTO.setTermsUri(termsUri);
        purposeDTO.setScenario(name);
        purposeDTO.setPurpose(purpose);

        PresentationDefinitionDTO presentationDefinitionDTO = new PresentationDefinitionDTO();
        presentationDefinitionDTO.setId(businessId + "_" + serialNo);
        try {
            presentationDefinitionDTO.setPurpose(objectMapper.writeValueAsString(purposeDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<SubmissionRequirement> submissionRequirements = new ArrayList<>();
        List<InputDescriptor> inputDescriptors = new ArrayList<>();
        int groupIdx = 1;
        int inputDescriptorIdx = 1;
        for (UpsertVPItemGroupReqDTO group : groups) {
            String groupIdxStr = StringUtils.join("Group_", groupIdx++);
            // 建立 submission_requirements
            SubmissionRequirement submissionRequirement = new SubmissionRequirement();
            submissionRequirement.setName(group.getName());
            submissionRequirement.setRule(group.getRule());
            submissionRequirement.setMax(group.getMax());
            submissionRequirement.setFrom(groupIdxStr);
            submissionRequirements.add(submissionRequirement);

            for (UpsertVPItemGroupVcDataReqDTO vcData : group.getVcDatas()) {
                // 建立 input_descriptors
                String inputDescriptorIdxStr = StringUtils
                        .join(vcData.getBusinessId() + "_" + vcData.getSerialNo() + "_", inputDescriptorIdx++);
                InputDescriptor inputDescriptor = new InputDescriptor();
                inputDescriptor.setId(inputDescriptorIdxStr);

                // 建立 name
                InputDescriptorNameDTO nameDTO = new InputDescriptorNameDTO();
                nameDTO.setOrgTwName(vcData.getBusinessName());
                nameDTO.setVcName(vcData.getName());
                try {
                    inputDescriptor.setName(objectMapper.writeValueAsString(nameDTO));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                inputDescriptor.setGroup(Arrays.asList(groupIdxStr));

                // 建立 constraints
                Constraints constraints = new Constraints();
                // 建立 $.type field
                Field typeField = new Field();
                Filter typeFilter = new Filter();
                Contains typeContains = new Contains();
                typeContains.set$$const(vcData.getBusinessId() + "_" + vcData.getSerialNo());
                typeFilter.setContains(typeContains);
                typeFilter.setType("array");
                typeField.setPath(List.of("$.type"));
                typeField.setFilter(typeFilter);

                // 建立 全部 fields
                List<Field> vcAllFields = new ArrayList<>();
                vcAllFields.add(typeField);
                List<Field> vcOptionalFields = vcData.getVcFields().stream().filter(vcf -> vcf.getIsTicked())
                        .map(vcf -> {
                            Field field = new Field();
                            field.setPath(List.of("$.credentialSubject." + vcf.getEname()));
                            return field;
                        }).toList();
                vcAllFields.addAll(vcOptionalFields);

                constraints.setFields(vcAllFields);
                constraints.setLimitDisclosure("required");
                inputDescriptor.setConstraints(constraints);
                inputDescriptors.add(inputDescriptor);
            }
        }
        presentationDefinitionDTO.setSubmissionRequirements(submissionRequirements);
        presentationDefinitionDTO.setInputDescriptors(inputDescriptors);

        String result = "";
        try {
            result = objectMapper.writeValueAsString(presentationDefinitionDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String afterResult = result.replace("$$const", "const");
        log.info("generatePresentationDefinition => {}", afterResult);
        return afterResult;
    }

    /**
     * 呼叫 oid4vp301i format 後回傳，並移除對應的 vpVerifyResult
     * 
     * @param transactionId
     * @param responseCode
     * @return
     */
    public JsonNode getPayloadResult(String transactionId, String responseCode) {
        String payloadString = remoteApiService.verifierOid4vp301i(transactionId, responseCode);

        return this.formatPayloadResult(transactionId, payloadString, null);
    }

    /**
     * 呼叫 oid4vp301i format 後回傳，並移除對應的 vpVerifyResult
     * 供 WEB 使用
     * 
     * @param transactionId
     * @param responseCode
     * @return
     */
    public JsonNode getPayloadResultForWeb(String transactionId, String responseCode) {
        String payloadString = remoteApiService.verifierOid4vp301i(transactionId, responseCode);

        return this.formatPayloadResult(transactionId, payloadString, LoginSourceType.WEB);
    }

    /**
     * format payloadString
     * 
     * @param transactionId
     * @param payloadString
     * @param source WEB 供 WEB 使用，提供 VC 模板中文名稱
     * @return
     */
    public JsonNode formatPayloadResult(String transactionId, String payloadString, LoginSourceType source) {
        JsonNode result;
        // 找到 VPVerifyResult 資料
        List<VPVerifyResult> resultFromDB = customVPVerifyResultRepository.findByTransactionId(transactionId);
        Optional<VPVerifyResult> vpVerifyResultByTxIdOpt = resultFromDB.stream().findFirst();
        Map<String, VCItemDataDTO> colToCnameMap = new LinkedHashMap<>();
        Map<String,String> vcNameMap = new LinkedHashMap<>();
        vpVerifyResultByTxIdOpt.ifPresent(vpResult -> {
            Long vpItemId = vpResult.getVpItemId(); // 拿到 vp_item_id
            // 查 VpItemField 表 , 會含多個 vc_field
            List<VPItemField> fieldsByVpItemId = customVpItemFieldRepository.findByVpItemId(vpItemId);
            fieldsByVpItemId.forEach(vpField -> {
                colToCnameMap.putIfAbsent(
                        vpField.getVcBusinessId() + "_" + vpField.getVcSerialNo() + "_" + vpField.getEname(),
                        VCItemDataDTO.builder().vcBusinessId(vpField.getVcBusinessId())
                                .vcSerialNo(vpField.getVcSerialNo()).ename(vpField.getEname()).cname(vpField.getCname())
                                .build());
                
                // 組 VC 模板中文名稱 Map
                vcNameMap.putIfAbsent(vpField.getVcBusinessId() + "_" + vpField.getVcSerialNo(), vpField.getVcName());
            });
            
        });

        // 加入中文欄位名
        try {
            result = objectMapper.readTree(payloadString);
            JsonNode dataNode = result.get("data");
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode vcNode : dataNode) { // n 張vc
                    JsonNode credentialTypeNode = vcNode.get("credentialType");
                    String credTypeValue = credentialTypeNode.asText();
                    int i = credTypeValue.indexOf("_");
                    String vcBusinessId = credTypeValue.substring(0, i);
                    String vcSerialNo = credTypeValue.substring(i + 1, credTypeValue.length());
                    
                    // 加入 VC 模板中文名稱
                    if (source == LoginSourceType.WEB) {
                        ((ObjectNode) vcNode).put("vcName", vcNameMap.getOrDefault(credTypeValue, ""));
                    }

                    JsonNode claimsNode = vcNode.get("claims");
                    Iterator<String> claimNodeIterator = claimsNode.fieldNames();
                    ArrayNode claimsArray = objectMapper.createArrayNode();

                    // 創建claims底下內容
                    while (claimNodeIterator.hasNext()) {
                        String claimKey = claimNodeIterator.next();
                        String claimValue = claimsNode.get(claimKey).asText(); // 屬性的值
                        VCItemDataDTO vcItemDataDTO = colToCnameMap
                                .get(vcBusinessId + "_" + vcSerialNo + "_" + claimKey);
                        if (vcItemDataDTO != null) {
                            vcItemDataDTO.setData(claimValue);
                            
                            ObjectNode newClaimsNode = objectMapper.createObjectNode();
                            newClaimsNode.put("ename", vcItemDataDTO.getEname());
                            newClaimsNode.put("cname", vcItemDataDTO.getCname());
                            newClaimsNode.put("value", vcItemDataDTO.getData());
                            claimsArray.add(newClaimsNode);
                        }
                    }
                    ((ObjectNode) vcNode).set("claims", claimsArray);
                }
            }

            // 移除 code
            if (result.has("code")) {
                ((ObjectNode) result).remove("code");
            }

            // 檢查並更名 verify_result -> verifyResult
            if (result.has("verify_result")) {
                JsonNode verifyResultNode = result.get("verify_result");
                ((ObjectNode) result).remove("verify_result");
                ((ObjectNode) result).set("verifyResult", verifyResultNode);
            }

            // 檢查並更名 result_description -> resultDescription
            if (result.has("result_description")) {
                JsonNode resultDescriptionNode = result.get("result_description");
                ((ObjectNode) result).remove("result_description");
                ((ObjectNode) result).set("resultDescription", resultDescriptionNode);
            }

            // 檢查並更名 transaction_id -> transactionId
            if (result.has("transaction_id")) {
                JsonNode txIdNode = result.get("transaction_id");
                ((ObjectNode) result).remove("transaction_id");
                ((ObjectNode) result).set("transactionId", txIdNode);
            }
        } catch (JsonProcessingException e) {
            log.error("查詢驗證結果失敗: {}", ExceptionUtils.getStackTrace(e));
            throw new DWException(StatusCode.DWVP_QUERY_VP_RESULT_ERROR);
        }

        // 更新 verifyResult 表 
        updateVerifyResult(vpVerifyResultByTxIdOpt);

        return result;
    }
    
    /**
     * 更新 verifyResult 表，紀錄驗證成功時間
     * 
     * @param vpVerifyResultByTxIdOpt
     */
    private void updateVerifyResult(Optional<VPVerifyResult> vpVerifyResultByTxIdOpt) {
        try {
            if (vpVerifyResultByTxIdOpt.isPresent()) {
                VPVerifyResult vpVerifyResult = vpVerifyResultByTxIdOpt.get();
                vpVerifyResult.setVerifyDatetime(Instant.now());
                customVPVerifyResultRepository.save(vpVerifyResult);
            }
        } catch (Exception e) {
            log.warn("RestResourceAop-checkNotNull，發生異常:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /*
     * 建立 groups
     * 
     * @param vpItemId
     * @param vpItem
     * @return
     */
    private List<UpsertVPItemGroupReqDTO> setGroups(VPItem vpItem) {
        String groupInfo = vpItem.getGroupInfo();
        Long vpItemId = vpItem.getId();

        // 舊模板(無 groupInfo)
        if (StringUtils.isBlank(groupInfo)) {
            log.info("舊模板 setGroups");
            // 查詢 vpItemField by vpItemId
            List<VPItemField> vpItemFieldList = customVpItemFieldRepository.findByVpItemId(vpItemId);
            if (CollectionUtils.isEmpty(vpItemFieldList)) {
                throw new BadRequestAlertException("not found error", "Input Error", " VP 模板: " + vpItemId);
            }

            // group by vcSerialNo
            Map<String, List<VPItemField>> vpItemFieldGroupByVcSerialNoMap = vpItemFieldList.stream()
                    .collect(Collectors.groupingBy(VPItemField::getVcSerialNo));

            // 取得 VC 組織中文名稱
            Map<String, String> vcOrgTwNameMap = this.getVcOrgTwName();

            // 建立 vcDataList
            List<UpsertVPItemGroupVcDataReqDTO> vcDatas = new ArrayList<>();
            vpItemFieldGroupByVcSerialNoMap.forEach((vcSerialNo, vpItemFields) -> {
                // 建立 vcFields
                List<UpsertVPItemGroupVcDataFieldReqDTO> vcFields = vpItemFields.stream().map(vpItemField -> {
                    UpsertVPItemGroupVcDataFieldReqDTO vcField = new UpsertVPItemGroupVcDataFieldReqDTO();
                    vcField.setCname(vpItemField.getCname());
                    vcField.setEname(vpItemField.getEname());
                    vcField.setIsRequired(vpItemField.getIsRequired());
                    vcField.setIsTicked(true);
                    return vcField;
                }).toList();

                VPItemField vpItemField = vpItemFields.get(0);
                String vcOrgTwName = vcOrgTwNameMap.getOrDefault(vpItemField.getVcBusinessId(), "");

                // 建立 vcData
                UpsertVPItemGroupVcDataReqDTO vcData = new UpsertVPItemGroupVcDataReqDTO();
                vcData.setSerialNo(vcSerialNo);
                vcData.setName(vpItemField.getVcName());
                vcData.setBusinessId(vpItemField.getVcBusinessId());
                vcData.setBusinessName(vcOrgTwName);
                vcData.setVcFields(vcFields);
                vcData.setIsTicked(true);
                vcDatas.add(vcData);
            });

            // 建立 groupList
            List<UpsertVPItemGroupReqDTO> groups = new ArrayList<>();
            UpsertVPItemGroupReqDTO group = new UpsertVPItemGroupReqDTO();
            group.setName(vpItem.getName());
            group.setRule("all");
            group.setVcDatas(vcDatas);
            groups.add(group);
            return groups;
        }

        // 新模板
        log.info("新模板 setGroups");
        return this.jsonToObject(groupInfo, new TypeReference<List<UpsertVPItemGroupReqDTO>>() {
        });
    }

    /**
     * 取得 VC 組織中文名稱
     * 
     * @return
     */
    private Map<String, String> getVcOrgTwName() {
        Map<String, String> nameMap = new HashMap<>();
        for (CategoryVdrSimpleDto dto : customCategoryService.getFromVDRStatus1ToDto()) {
            nameMap.putIfAbsent(dto.getTaxId(), dto.getName());
        }
        return nameMap;
    }

    /**
     * 將 Object 轉為 String 並 escape
     * 
     * @param object
     * @return
     */
    private String objectToEscapeString(Object object) {
        try {
            return Encode.forHtmlContent(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new BadRequestAlertException("objectToString error", "vpItem", "parse error");
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
            throw new BadRequestAlertException("jsonToObject error", "vpItem", "parse error");
        }
    }

    /**
     * step1 檢核 VP 代碼不可重複
     * 
     * @param serialNo
     */
    private void validStep1(ValidVPItemByStepReqDTO request) {
        log.info("validStep1");
        String serialNo = request.getSerialNo();
        String model = request.getModel();

        ModelType modelType = ModelType.toModelType(model);
        if (StringUtils.isNotBlank(model) && modelType == null) {
            throw new BadRequestAlertException("not found error", "vpItem", "modelType not found");
        }

        if (modelType != null && modelType == ModelType.MODEL_2) {
            if (StringUtils.isBlank(request.getVerifierServiceUrl())) {
                throw new BadRequestAlertException("input error", "vpItem", "validation");
            }

            if (StringUtils.isBlank(request.getCallBackUrl())) {
                throw new BadRequestAlertException("input error", "vpItem", "validation");
            }

            if (request.getIsCustomFields()) {
                for (CustomFieldReqDTO field : request.getFields()) {
                    if (StringUtils.isBlank(field.getCname()) || StringUtils.isBlank(field.getEname())
                            || StringUtils.isBlank(field.getRegex()) || StringUtils.isBlank(field.getDescription())) {
                        throw new BadRequestAlertException("input error", "vpItem", "validation");
                    }
                }
            }

        } else if (modelType != null && modelType == ModelType.MODEL_3) {
            if (Boolean.TRUE.equals(request.getIsEncryptEnabled()) && StringUtils.isBlank(request.getTag())) {
                throw new BadRequestAlertException("input error", "vpItem", "validation");
            }
        }

        String businessId = customVPItemAuthHelperService.getBusinessId();
        int count = customVPItemRepository.countBySerialNoAndBusinessId(serialNo, businessId);
        if (count > 0) {
            throw new BadRequestAlertException("exists error", "vpItem", "serialNo already exists");
        }
    }

    /**
     * step2 檢核 rule = pick，max 不可大於 vcDatas size
     * 
     * @param group
     */
    private void validStep2(ValidVPItemByStepReqDTO request) {
        log.info("validStep2");

        // 檢核 群組數 不可大於 10 個
        if (request.getGroups().size() > 10) {
            throw new BadRequestAlertException("input error", "vpItem", "group count exceeds 10");
        }

        request.getGroups().forEach(group -> {
            RuleType ruleType = RuleType.toRuleType(group.getRule());
            if (ruleType == null) {
                throw new BadRequestAlertException("not found error", "vpItem", "ruleType not found");
            }

            // 檢核 群組內模板數 不可大於 10 個
            if (group.getVcDatas().size() > 10) {
                throw new BadRequestAlertException("input error", "vpItem", "group count exceeds vcDatas 10");
            }

            // rule = pick 檢核
            if (RuleType.PICK.getCode().equals(ruleType.getCode()) && group.getMax() > group.getVcDatas().size()) {
                throw new BadRequestAlertException("input error", "vpItem", "group max exceeds vcDatas size");
            }
        });

    }

    /**
     * step3 檢核 rule = pick， vcDatas isTicked 需大於等於 max
     * 
     * @param group
     */
    private void validStep3(ValidVPItemByStepReqDTO request) {
        log.info("validStep3");

        ModelType modelType = ModelType.toModelType(request.getModel());

        // 檢核若為 VP05（APP 出示憑證模式），必填自定義欄位，並且不可重複
        if (modelType != null && modelType == ModelType.MODEL_3) {
            // 有勾選的欄位資料
            List<UpsertVPItemGroupVcDataFieldReqDTO> tickedFields = request.getGroups().stream()
                    .flatMap(groups -> groups.getVcDatas().stream()).flatMap(vcDatas -> vcDatas.getVcFields().stream())
                    .filter(UpsertVPItemGroupVcDataFieldReqDTO::getIsTicked).toList();

            // 必填檢查
            for (UpsertVPItemGroupVcDataFieldReqDTO tickedField : tickedFields) {
                if (StringUtils.isBlank(tickedField.getCustomFieldName())) {
                    throw new BadRequestAlertException("not found error", "customFieldName",
                            "CustomFieldName not found");
                }
            }
        }

        request.getGroups().forEach(group -> {
            RuleType ruleType = RuleType.toRuleType(group.getRule());

            // rule = pick 檢核，統計 vcDatas isTicked count
            long isTickedCount = group.getVcDatas().stream().filter(UpsertVPItemGroupVcDataReqDTO::getIsTicked).count();
            if (RuleType.PICK.getCode().equals(ruleType.getCode()) && group.getMax() > isTickedCount) {
                throw new BadRequestAlertException("input error", "vpItem", "group max exceeds vcDatas isTicked count");
            }
        });

    }

    /**
     * 新增與編輯response
     * 
     * @param vpItemDTO
     * @param modelVal
     * @return
     */
    public DwSandBoxV401WCreateAndEditResDTO buildCreateAndEditResponse(VPItemDTO vpItemDTO, String modelVal) {
        // 初始化回傳物件
        DwSandBoxV401WCreateAndEditResDTO response = new DwSandBoxV401WCreateAndEditResDTO();

        BeanUtils.copyProperties(vpItemDTO, response);
        // 處理模式
        Map<String, String> modelStringMap = getModelMap(modelVal);
        // 處理描述與欄位
        String jsonFieldInfo = vpItemDTO.getFieldInfo();
        List<CustomFieldReqDTO> fieldList = new ArrayList<>();

        if (StringUtils.isNotEmpty(jsonFieldInfo)) {
            // 讀取Json取得描述與欄位
            fieldList = getCustomFields(jsonFieldInfo);
        }

        response.setFields(fieldList);
        response.setModel(modelStringMap);
        response.setCallBackUrl(vpItemDTO.getCallbackUrl());
        response.setIsEncryptEnabled(vpItemDTO.getIsEncryptEnabled());
        
        return response;
    }

    /**
     * 將資料組合成Json字串
     * 
     * @param dataMap
     * @return String
     */
    private String combineIntoJson(Map<String, Object> dataMap) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    /**
     * 組成模式物件
     * 
     * @param value
     * @return
     */
    private Map<String, String> getModelMap(String value) {
        Map<String, String> modelMap = new HashMap<>();
        ModelType modelType;
        if (StringUtils.isNotEmpty(value)) {
            modelType = ModelType.toModelType(value);
        } else {
            modelType = ModelType.toModelType("0");
        }

        if (modelType != null) {
            modelMap.put("value", modelType.getValue());
            modelMap.put("label", modelType.getLabel());
        }

        return modelMap;
    }

    /**
     * 讀取Json取得欄位
     * 
     * @param data
     * @return List<UpsertVPItemOfflineFieldReqDTO>
     */
    private List<CustomFieldReqDTO> getCustomFields(String jsonData) {
        List<CustomFieldReqDTO> fieldList = new ArrayList<>();

        Map<String, Object> map = jsonToObject(jsonData, new TypeReference<Map<String, Object>>() {
        });
        // 欄位
        Object fieldObject = map.get("fields");

        if (fieldObject instanceof List) {
            List<?> list = (List<?>) fieldObject;

            for (Object data : list) {
                CustomFieldReqDTO dto = objectMapper.convertValue(data, CustomFieldReqDTO.class);
                fieldList.add(dto);
            }
        }

        return fieldList;
    }

    /**
     * 登入者身份驗證取得資料權限
     * 
     * @param loginName
     * @return
     */
    public Pair<String, Object> checkIdentity(String loginName) {
        Object result = null;
        String identity = null;
        boolean isNotAdmin = customVPItemAuthHelperService.isNotAdmin(loginName);

        // 處理查詢條件
        // 不是admin角色
        if (isNotAdmin) {
            if (customVPItemAuthHelperService.isCommonAccount()) {
                // 一般帳號只能看自己的
                result = customVPItemAuthHelperService.getLoginUserId();
                identity = "crUser";
            } else {
                // 非 admin 且非一般帳號 → 進階帳號，只能查同組織資料
                result = customVPItemAuthHelperService.getBusinessId();
                identity = "business";
            }
        }

        return Pair.of(identity, result);
    }

    /**
     * 將 DeepLink 轉換成 QR 碼的 base64 格式
     *
     * @param deepLink DeepLink 字串
     * @return QR 碼的 base64 格式字串，包含 data:image/png;base64, 前綴
     */
    private String generateQRCodeFromLink(String deepLink) {
        // 如果 DeepLink 為空，回傳空字串
        if (StringUtils.isBlank(deepLink)) {
            return "";
        }
        try {
            BufferedImage logoImage = ImageIO.read(new ClassPathResource(QR_CODE_LOGO).getInputStream());
            String result = qrCodeService.generateQRCodeWithLogoSpace(deepLink, logoImage);
            return QR_CODE_PREFIX + result;
        } catch (Exception ex) {
            log.error("生成 QR 碼失敗，DeepLink: {}, 錯誤: {}", deepLink, ExceptionUtils.getStackTrace(ex));
            throw new DWException(StatusCode.DWVP_GENERATOR_QRCODE_ERROR);
        }
    }

}
