package gov.moda.dw.manager.service;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.repository.custom.CustomRegularExpressionRepository;
import gov.moda.dw.manager.repository.custom.CustomSettingRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemDataRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.custom.CustomRelService;
import gov.moda.dw.manager.service.custom.CustomUserService;
import gov.moda.dw.manager.service.custom.Modadw101wService;
import gov.moda.dw.manager.service.custom.RemoteApiService;
import gov.moda.dw.manager.service.custom.common.QRCodeService;
import gov.moda.dw.manager.service.dto.CustomVCItemSettingDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.VC503iResp;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemExposeDTO;
import gov.moda.dw.manager.service.dto.VCItemFieldDTO;
import gov.moda.dw.manager.service.dto.custom.DwFront104iResDTO;
import gov.moda.dw.manager.service.dto.custom.DwIssuerVc504iReq;
import gov.moda.dw.manager.service.dto.custom.DwIssuerVc504iRes;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVC201WDownloadReqDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVC201WStaticQRCodeResDTO;
import gov.moda.dw.manager.service.dto.custom.GetIssuerDataResDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.service.dto.custom.SwitchesDTO;
import gov.moda.dw.manager.service.dto.custom.VCItemServiceUrlDTO;
import gov.moda.dw.manager.service.dto.custom.VCItemStopIssuingReqDTO;
import gov.moda.dw.manager.service.dto.custom.VCItemTitleDTO;
import gov.moda.dw.manager.service.dto.custom.frontend.api.VCSchemaResDto;
import gov.moda.dw.manager.service.mapper.VCItemFieldMapper;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.StatusCodeSandbox;
import gov.moda.dw.manager.util.SandBoxUtil;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.InternalErrorAlertException;

/**
 * Service Implementation for managing
 * {@link gov.moda.dw.manager.domain.VCItem}.
 */
@Service
@Transactional
public class DwSandBoxVC201WService {

    private final CustomVCItemRepository customVCItemRepository;
    private final CustomVCItemDataRepository customVCItemDataRepository;
    private final RemoteApiService remoteApiService;
    private final Modadw101wService modadw101wService;

    @Value("${dwfront.dwfront-104i}")
    private String getCreateVCSchemaUri;

    @Value("${issuer.dwissuer-vc-502i}")
    private String get502iUri;

    @Value("${issuer.dwissuer-vc-503i}")
    private String get503iUri;

    @Value("${issuer.dwissuer-vc-504i}")
    private String get504iUri;

    @Value("${metadata-schema.credential-issuer}")
    String credentialIssuerUri;

    @Value("${metadata-schema.credential-endpoint}")
    String credentialEndpointUri;

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    @Value("${sandbox.commonAccountOrgId:00000000}")
    private String commonAccountOrgId;

    @Value("${dwfront.url}")
    private String dwfrontUrl;

    // QR Code 的前綴字串，用於Base64編碼的圖片資料
    private static final String QR_CODE_PREFIX = "data:image/png;base64,";

    // QR Code 的 LOGO，用於Base64編碼的圖片資料
    private static final String QR_CODE_LOGO = "img/dwIcon.png";

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC201WService.class);

    private final CustomVCItemRepository vCItemRepository;

    private final CustomVCItemFieldRepository vCItemFieldRepository;

    private final VCItemMapper vCItemMapper;

    private final VCItemFieldMapper vCItemFieldMapper;

    private final CustomOrgRepository orgRepository;

    private final CustomUserService customUserService;

    private final CustomRegularExpressionRepository regularExpressionRepository;

    private final CustomUserRepository customUserRepository;

    private final CustomSettingRepository customSettingRepository;

    private final QRCodeService qrCodeService;

    public DwSandBoxVC201WService(CustomVCItemRepository vCItemRepository,
            CustomVCItemFieldRepository vCItemFieldRepository, VCItemMapper vCItemMapper,
            VCItemFieldMapper vCItemFieldMapper, CustomOrgRepository orgRepository, CustomRelService customRelService,
            CustomUserService customUserService, CustomVCItemRepository customVCItemRepository,
            CustomRegularExpressionRepository regularExpressionRepository, CustomUserRepository customUserRepository,
            CustomSettingRepository customSettingRepository, CustomVCItemDataRepository customVCItemDataRepository,
            RemoteApiService remoteApiService, Modadw101wService modadw101wService, QRCodeService qrCodeService) {
        this.vCItemRepository = vCItemRepository;
        this.vCItemFieldRepository = vCItemFieldRepository;
        this.vCItemMapper = vCItemMapper;
        this.vCItemFieldMapper = vCItemFieldMapper;
        this.orgRepository = orgRepository;
        this.customUserService = customUserService;
        this.customVCItemRepository = customVCItemRepository;
        this.regularExpressionRepository = regularExpressionRepository;
        this.customUserRepository = customUserRepository;
        this.customSettingRepository = customSettingRepository;
        this.customVCItemDataRepository = customVCItemDataRepository;
        this.remoteApiService = remoteApiService;
        this.modadw101wService = modadw101wService;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Save a vCItem.
     *
     * @param vCItemDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public VCItemDTO save(VCItemDTO vCItemDTO) throws JsonProcessingException {
        log.debug("Request to save VCItem : {}", vCItemDTO);

        ObjectMapper objectMapper = new ObjectMapper();

        // 檢查 vc.setting.issuer-did 有沒有值，沒值(未註冊did)的話不允許新增 vc
        Optional<Setting> issuerDIDSetting = customSettingRepository.findByPropName("issuer.did");

        if (issuerDIDSetting.isEmpty() || issuerDIDSetting.get().getPropValue().equals("")) {
            throw new BadRequestAlertException("Unregistered DID", vCItemDTO.getSerialNo(), "UnregisteredDID");
        }

        // 檢查同組織 VC 模板代碼不可重複(依 DID 註冊組織判斷)
        this.checkVcSerialNoByDidOrg(vCItemDTO.getSerialNo());

        // 檢查是否為正式模板
        VCItem vcItem = vCItemRepository.findBySerialNoAndBusinessIdAndIsTempFalse(vCItemDTO.getSerialNo(),
                SecurityUtils.getJwtUserObject().get(0).getOrgId());

        if (vcItem != null) {
            log.debug("VCItem already exists : {}", vcItem);
            throw new BadRequestAlertException("A new vCItem cannot already have an serial no ",
                    vCItemDTO.getSerialNo(), "exists");
        }
        VCItem tempItem = null;
        // 已是暫存模板，使用 id 搜尋
        if (vCItemDTO.getId() != null) {
            tempItem = vCItemRepository.findByIdAndIsTempTrue(vCItemDTO.getId());
        }

        if (tempItem != null) {
            updateVCItemAndFields(tempItem, vCItemDTO);
            vcItem = tempItem;
        } else {
            vcItem = vCItemMapper.toEntity(vCItemDTO);
        }

        // 卡片封面的 url
        String vcCardCoverUrl = "";
        if (vCItemDTO.getCardCover() != null && !vCItemDTO.getCardCover().isEmpty()) {
            vcCardCoverUrl = getVCCardCoverUrl(vcItem.getSerialNo());
        }

        String schema = SandBoxUtil.createSchema(vCItemDTO);

        // 生成這個新建的 vc 的 metadata
        String newVCMetadata = SandBoxUtil.createMetadata(vCItemDTO, credentialIssuerUri, credentialEndpointUri,
                vcCardCoverUrl);

        // 取同組織下 vc 清單
        List<VCItem> vcItemList = customVCItemRepository
                .findAllByBusinessIdAndIsTempFalseOrderByIdDesc(SecurityUtils.getJwtUserObject().get(0).getOrgId());

        // 把既存的 vc 模版中的 metadata 取出，連同新的 vc 組合成一個包含所有 vc 的 metadata
        ObjectNode newAllVCMetadata = collectMetadataToJson(vcItemList, newVCMetadata);
        String metadata = objectMapper.writeValueAsString(newAllVCMetadata);

        String schemaForRequest = SandBoxUtil.setSchemaForApi104i(schema, vcItem.getSerialNo());

        String respSchema = "";
        String responseMsg = "";
        try {
            DwFront104iResDTO response = callApi104i(schemaForRequest);
            responseMsg = "呼叫 104i 上鏈結果:" + response.getCode() + "," + response.getMsg() + "," + response.getData();
            log.info(responseMsg);
            respSchema = objectMapper.writeValueAsString(response.getData().getSchema());
            String schemaId = ((LinkedHashMap) response.getData().getSchema()).get("$id").toString();
            vcItem.setSchemaId(schemaId);
            vcItem.setTxHash(response.getData().getTxHash());
            vcItem.setSchema(respSchema);
        } catch (Exception ex) {
            log.error("DwSandBoxVC201WService save call (104i) vc schema on chain，error:{}",
                    ex.getMessage() + " " + responseMsg, ExceptionUtils.getStackTrace(ex));
            throw new InternalErrorAlertException("VC schema on-chain failed.", vCItemDTO.getSerialNo(),
                    "ExternalApiError");
        }

        try {
            String schemaFor502i = SandBoxUtil.create502iSchemaFrom104iResponse(respSchema, vcItem.getUnitTypeExpire(),
                    vcItem.getLengthExpire(), metadata);
            String api502iResponse = callApi502i(schemaFor502i, SecurityUtils.getJwtUserObject().get(0).getOrgId(),
                    vcItem.getSerialNo());
        } catch (Exception ex) {
            log.info("call 502i error", respSchema);
            log.error("DwSandBoxVC201WService save call (502i)，error:{}", ex.getMessage(),
                    ExceptionUtils.getStackTrace(ex));
            throw new InternalErrorAlertException("VC schema on-chain failed.", vCItemDTO.getSerialNo(),
                    "ExternalApiError");
        }

        vcItem.setCardCoverUrl(vcCardCoverUrl);
        vcItem.setBusinessId(SecurityUtils.getJwtUserObject().get(0).getOrgId());
        vcItem.setMetadata(newVCMetadata);
        vcItem.crUser(getLoginUserId());
        vcItem.crDatetime(Instant.ofEpochMilli(System.currentTimeMillis()));
        vcItem.setIsTemp(false);
        vcItem.setActivated(true);
        vcItem = vCItemRepository.save(vcItem);

        String credentialType = SecurityUtils.getJwtUserObject().get(0).getOrgId() + "_" + vcItem.getSerialNo();

        // 驗證碼模式開關
        DwIssuerVc504iReq req = new DwIssuerVc504iReq();
        SwitchesDTO switchesDTO = new SwitchesDTO();
        switchesDTO.setEnableTxCode(vCItemDTO.getIsVerify());
        switchesDTO.setEnableVcTransfer(false);
        req.setSwitches(switchesDTO);

        try {
            DwIssuerVc504iRes response = callApi504i(credentialType, req);
            log.info("Call DwIssuerVc504i success，CredentialType: {}", response.getCredentialType());

        } catch (Exception ex) {
            log.error("callApi504iForExternalCall-HttpServerErrorException  {}", ex.getMessage(),
                    ExceptionUtils.getStackTrace(ex));
            delete(vcItem.getId());
            throw ex;
        }

        VCItemDTO result = vCItemMapper.toDto(vcItem);

        List<VCItemFieldDTO> resultVCItemFieldDTO = new ArrayList<>();
        result.setVcItemFieldDTOList(resultVCItemFieldDTO);

        for (VCItemFieldDTO item : vCItemDTO.getVcItemFieldDTOList()) {
            // 調整這張vc模版上，不是卡面資料的欄位，設定 CardCoverData 註記為 99999
            VCItemField vcItemField = vCItemFieldMapper.toEntity(item);
            vcItemField.setVcItemId(vcItem.getId());
            if (vcItemField.getCardCoverData() == null) {
                vcItemField.setCardCoverData(99999);
            }

            // 將該vc模版上建立的欄位，所指定的正規表達式指定進去
            RegularExpression regularExpression = null;
            if (item.getRegularExpressionId() != null) {
                Optional<RegularExpression> regularExpressionOptional = regularExpressionRepository
                        .findById(item.getRegularExpressionId());
                if (regularExpressionOptional.isPresent()) {
                    regularExpression = regularExpressionOptional.get();
                }
            }
            vcItemField.setRegularExpression(regularExpression);

            vCItemFieldRepository.save(vcItemField);
            resultVCItemFieldDTO.add(vCItemFieldMapper.toDto(vcItemField));
        }

        return result;
    }

    /**
     * Temp a vCItem.
     *
     * @param vCItemDTO the entity to Temp.
     * @return the persisted entity.
     */
    @Transactional
    public VCItemDTO temp(VCItemDTO vCItemDTO) throws JsonProcessingException {
        log.debug("Request to temp VCItem : {}", vCItemDTO);

        // 檢查 vc.setting.issuer-did 有沒有值，沒值(未註冊did)的話不允許暫存 vc
        Optional<Setting> issuerDIDSetting = customSettingRepository.findByPropName("issuer.did");

        if (issuerDIDSetting.isEmpty() || issuerDIDSetting.get().getPropValue().equals("")) {
            throw new BadRequestAlertException("Unregistered DID", vCItemDTO.getSerialNo(), "UnregisteredDID");
        }

        VCItem vcItem = null;

        // 已是暫存模板，使用 id 搜尋
        if (vCItemDTO.getId() != null) {
            vcItem = vCItemRepository.findByIdAndIsTempTrue(vCItemDTO.getId());
        }

        // 更新 vc_item，重新產生 vc_item_field
        if (vcItem != null) {
            updateVCItemAndFields(vcItem, vCItemDTO);
        } else {
            vcItem = vCItemMapper.toEntity(vCItemDTO);
        }

        // 卡片封面的 url
        String vcCardCoverUrl = "";
        if (vCItemDTO.getCardCover() != null && !vCItemDTO.getCardCover().isEmpty()) {
            vcCardCoverUrl = getVCCardCoverUrl(vcItem.getSerialNo());
        }

        vcItem.setCardCoverUrl(vcCardCoverUrl);
        vcItem.setBusinessId(SecurityUtils.getJwtUserObject().get(0).getOrgId());
        vcItem.crUser(getLoginUserId());
        vcItem.crDatetime(Instant.ofEpochMilli(System.currentTimeMillis()));
        vcItem.setActivated(true);

        vcItem = vCItemRepository.save(vcItem);

        VCItemDTO result = vCItemMapper.toDto(vcItem);

        List<VCItemFieldDTO> resultVCItemFieldDTO = new ArrayList<>();
        result.setVcItemFieldDTOList(resultVCItemFieldDTO);

        for (VCItemFieldDTO item : vCItemDTO.getVcItemFieldDTOList()) {
            // 調整這張vc模版上，不是卡面資料的欄位，設定 CardCoverData 註記為 99999
            VCItemField vcItemField = vCItemFieldMapper.toEntity(item);
            vcItemField.setVcItemId(vcItem.getId());
            if (vcItemField.getCardCoverData() == null) {
                vcItemField.setCardCoverData(99999);
            }

            // 將該vc模版上建立的欄位，所指定的正規表達式指定進去
            RegularExpression regularExpression = null;
            if (item.getRegularExpressionId() != null) {
                Optional<RegularExpression> regularExpressionOptional = regularExpressionRepository
                        .findById(item.getRegularExpressionId());
                if (regularExpressionOptional.isPresent()) {
                    regularExpression = regularExpressionOptional.get();
                }
            }
            vcItemField.setRegularExpression(regularExpression);

            vCItemFieldRepository.save(vcItemField);
            resultVCItemFieldDTO.add(vCItemFieldMapper.toDto(vcItemField));
        }

        return result;
    }

    private ObjectNode collectMetadataToJson(List<VCItem> vcItemList, String newVCItemMetadata)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = null;
        ObjectNode metadataColNode = null;

        if ((vcItemList == null || vcItemList.size() == 0)
                && (newVCItemMetadata == null || newVCItemMetadata.isEmpty())) {
            objectNode = objectMapper.createObjectNode();
            String credentialIssuer = credentialIssuerUri + SecurityUtils.getJwtUserObject().get(0).getOrgId();
            String credentialEndpoint = credentialEndpointUri + SecurityUtils.getJwtUserObject().get(0).getOrgId();
            objectNode = objectMapper.createObjectNode();
            objectNode.put("credentialIssuer", credentialIssuer);
            objectNode.put("credentialEndpoint", credentialEndpoint);
            // objectNode.set("credential_configurations_supported",
            // objectMapper.createObjectNode());
            metadataColNode = objectMapper.createObjectNode();
        } else if (newVCItemMetadata == null || newVCItemMetadata.isEmpty()) {
            VCItem vcitem = vcItemList.get(0);
            objectNode = (ObjectNode) objectMapper.readTree(vcitem.getMetadata());
            metadataColNode = (ObjectNode) objectNode.get("credential_configurations_supported");
            vcItemList.remove(vcitem);
        } else {
            objectNode = (ObjectNode) objectMapper.readTree(newVCItemMetadata);
            metadataColNode = (ObjectNode) objectNode.get("credential_configurations_supported");
        }

        for (int i = 0; i < vcItemList.size(); i++) {
            JsonNode jsonNode = objectMapper.readTree(vcItemList.get(i).getMetadata());
            String credentialType = vcItemList.get(i).getBusinessId() + "_" + vcItemList.get(i).getSerialNo();
            JsonNode metadata = jsonNode.get("credential_configurations_supported").get(credentialType);
            metadataColNode.put(credentialType, metadata);
        }

        objectNode.put("credential_configurations_supported", metadataColNode);

        return objectNode;
    }

    private DwFront104iResDTO callApi104i(String postContent) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(postContent, headers);
        DwFront104iResDTO response = restTemplate.postForObject(getCreateVCSchemaUri, request, DwFront104iResDTO.class);
        return response;
    }

    private String callApi502i(String postContent, String businessId, String vcid) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(postContent, headers);
        String uri = get502iUri + "/" + businessId + "_" + vcid;
        String response = restTemplate.postForObject(uri, request, String.class);
        return response;
    }

    // 發行端(issuer) VC 配合後台進行功能設定開關
    private DwIssuerVc504iRes callApi504i(String credentialType, DwIssuerVc504iReq reqContent) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(reqContent, headers);
        String uri = get504iUri + "/" + credentialType;
        DwIssuerVc504iRes response = restTemplate.postForObject(uri, request, DwIssuerVc504iRes.class);
        return response;
    }

    /**
     * Update a vCItem.
     *
     * @param vCItemDTO the entity to save.
     * @return the persisted entity.
     */
    public VCItemDTO update(VCItemDTO vCItemDTO) {
        log.debug("Request to update VCItem : {}", vCItemDTO);
        VCItem vCItem = vCItemMapper.toEntity(vCItemDTO);
        vCItem = vCItemRepository.save(vCItem);
        return vCItemMapper.toDto(vCItem);
    }

    /**
     * Partially update a vCItem.
     *
     * @param vCItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VCItemDTO> partialUpdate(VCItemDTO vCItemDTO) {
        log.debug("Request to partially update VCItem : {}", vCItemDTO);

        return vCItemRepository.findById(vCItemDTO.getId()).map(existingVCItem -> {
            vCItemMapper.partialUpdate(existingVCItem, vCItemDTO);

            return existingVCItem;
        }).map(vCItemRepository::save).map(vCItemMapper::toDto);
    }

    /**
     * Get one vCItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VCItemDTO> findOne(Long id) {
        log.debug("Request to get VCItem : {}", id);
        return vCItemRepository.findById(id).map(vCItemMapper::toDto);
    }

    /**
     * Delete the vCItem by id.
     *
     * @param id the id of the entity.
     */
    public VC503iResp delete(Long id) throws JsonProcessingException {
        log.debug("Request to delete VCItem : {}", id);

        Optional<VCItem> vcItem = vCItemRepository.findById(id);

        // vc模版存在
        if (vcItem.isEmpty()) {
            // throw new BadRequestAlertException("vc_item does not exist", id.toString(),
            // "notexist");
            throw new BadRequestAlertException(StatusCodeSandbox.VC_Template_Not_Found.getMsg(),
                    "VC_Template_Not_Found vcid:" + id, StatusCodeSandbox.VC_Template_Not_Found.getErrorKey());
        }

        // 帳號組織與模版組織一致
        if (!vcItem.get().getBusinessId().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
            // throw new BadRequestAlertException("Not the organization that originally
            // created", id.toString(), "NonOwners");
            throw new BadRequestAlertException(StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getMsg(),
                    "OrgId_Account_OrgId_Mismatch vcid:" + id,
                    StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getErrorKey());
        }

        // 如果是一般帳號的特殊組織，則只允許建立者本身修改
        if (SecurityUtils.getJwtUserObject().get(0).getOrgId().equals(commonAccountOrgId)) {
            if (getLoginUserId() != vcItem.get().getCrUser()) {
                throw new BadRequestAlertException(StatusCodeSandbox.Non_Original_Creator.getMsg(),
                        "updateVCItemExpose vcid:" + vcItem.get().getSerialNo(),
                        StatusCodeSandbox.Non_Original_Creator.getErrorKey());
            }
        }

        // VC模版未被使用
        if (vcItem.get().getUsed() != null && vcItem.get().getUsed()) {
            // throw new BadRequestAlertException("this vc_item be used", id.toString(),
            // "used");
            throw new BadRequestAlertException(StatusCodeSandbox.The_VC_Template_Has_Been_Used.getMsg(),
                    "The_VC_Template_Has_Been_Used vcid:" + id,
                    StatusCodeSandbox.The_VC_Template_Has_Been_Used.getErrorKey());
        } else {
            boolean exist = customVCItemDataRepository.existsByVcItemId(vcItem.get().getId());
            if (exist) {
                // throw new BadRequestAlertException("this vc_item be used", id.toString(),
                // "used");
                throw new BadRequestAlertException(StatusCodeSandbox.The_VC_Template_Has_Been_Used.getMsg(),
                        "The_VC_Template_Has_Been_Used vcid:" + id,
                        StatusCodeSandbox.The_VC_Template_Has_Been_Used.getErrorKey());
            }
        }

        // 刪除 vc_item_id 相對應的 vc_item_field
        vCItemFieldRepository.deleteByVcItemId(id);
        // 刪除 vc_item
        vCItemRepository.deleteById(id);

        VC503iResp resp = null;

        // 如果為一般已建立的模板，才走503i
        if (!vcItem.get().getIsTemp()) {

            // 取同組織下 vc 清單
            List<VCItem> vcItemList = customVCItemRepository
                    .findAllByBusinessIdAndIsTempFalseOrderByIdDesc(SecurityUtils.getJwtUserObject().get(0).getOrgId());

            vcItemList.removeIf(x -> x.getId().equals(id));

            // 把舊 vc 的 metadata 重新兜成一個大的
            ObjectNode newMetadata = collectMetadataToJson(vcItemList, null);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode metadataPackage = objectMapper.createObjectNode();
            metadataPackage.set("metadata", newMetadata);
            String metadata = objectMapper.writeValueAsString(metadataPackage);

            String credentialType = vcItem.get().getBusinessId() + "_" + vcItem.get().getSerialNo();

            resp = remoteApiService.callApi503i(get503iUri, credentialType, metadata);
        }
        return resp;
    }

    public List<VCItemTitleDTO> findVCItemTitleList(String orgId) {
        List<VCItem> vcItemList = new ArrayList<>();
        String orgIdForQuery = orgId;

        Long userId = getLoginUserId();

        if (!checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId())) {
            if (commonAccountOrgId.equals(orgId)) {
                // 00000000 組織 (一般帳號)，模版只顯示自己建立的
                vcItemList = customVCItemRepository.findAllByBusinessIdAndCrUserOrderByIdDesc(orgIdForQuery, userId);
            } else {
                orgIdForQuery = SecurityUtils.getJwtUserObject().get(0).getOrgId();
                vcItemList = customVCItemRepository.findAllByBusinessIdOrderById(orgIdForQuery);
            }
        } else {
            customVCItemRepository.findAllByBusinessIdOrderById(orgIdForQuery);
        }

        Optional<Org> org = orgRepository.findByOrgId(orgIdForQuery);

        List<VCItemTitleDTO> result = new ArrayList<>();

        for (VCItem vCItem : vcItemList) {
            VCItemTitleDTO item = new VCItemTitleDTO();
            item.setOrgId(vCItem.getBusinessId());
            item.setOrgName(org.get().getOrgEnName());
            item.setVcItemId(vCItem.getId());
            item.setVcItemSerialNo(vCItem.getSerialNo());
            item.setVcItemName(vCItem.getName());
            result.add(item);
        }

        result.sort((o1, o2) -> {
            String s1 = o1.getVcItemSerialNo();
            String s2 = o2.getVcItemSerialNo();
            int len1 = s1.length();
            int len2 = s2.length();
            int minLen = Math.min(len1, len2);

            for (int i = 0; i < minLen; i++) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);

                int weight1 = getWeight(c1);
                int weight2 = getWeight(c2);

                if (weight1 != weight2) {
                    return Integer.compare(weight1, weight2);
                }

                if (c1 != c2) {
                    return Character.compare(c1, c2);
                }
            }

            return Integer.compare(len1, len2);
        });

        return result;
    }

    public Boolean checkIsPrivilegedAccount(String userId) {
        List<RoleDTO> roles = customUserService.getRoles(userId);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }

    public long getLoginUserId() {
        String loginId = null;
        Long userId = 0L;
        Optional<JwtUserObject> first = SecurityUtils.getJwtUserObject().stream().findFirst();
        if (first.isPresent()) {
            loginId = first.get().getUserId();
            userId = findUserId(loginId);
        }
        return userId;
    }

    public Long findUserId(String login) {
        return customUserRepository.findOneByLogin(login).orElseThrow().getId();
    }

    public CustomVCItemSettingDTO createVCItemSetting(Long vcId, CustomVCItemSettingDTO vcItemSettingDTO) {
        VCItem vcItem = customVCItemRepository.findByIdAndSerialNo(vcId, vcItemSettingDTO.getVcSerialNo());

        // vc 不存在
        if (vcItem == null) {
            throw new BadRequestAlertException(StatusCodeSandbox.VC_Template_Not_Found.getMsg(),
                    "vc901iTest vcid:" + vcId, StatusCodeSandbox.VC_Template_Not_Found.getErrorKey());
        }

        // 帳號組織與VC組織不一致
        if (!vcItem.getBusinessId().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
            throw new BadRequestAlertException(StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getMsg(),
                    "saveVcItemDataFrom304i businessId:" + vcItem.getBusinessId() + " orgId:"
                            + SecurityUtils.getJwtUserObject().get(0).getOrgId(),
                    StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getErrorKey());
        }

        // vcItem.setApiType(vcItemSettingDTO.getApiType());
        vcItem.setHeaders(vcItemSettingDTO.getHeaders());
        vcItem.setUrl(vcItemSettingDTO.getUrl());
        vcItem.setHttpMethod(vcItemSettingDTO.getHttpMethod());

        customVCItemRepository.save(vcItem);

        return vcItemSettingDTO;
    }

    private static int getWeight(char c) {
        if (c >= 'A' && c <= 'Z') {
            return 1; // 大寫英文字母
        } else if (c >= 'a' && c <= 'z') {
            return 2; // 小寫英文字母
        } else if (c >= '0' && c <= '9') {
            return 3; // 數字
        } else {
            return 4; // 其他字符
        }
    }

    public VCItemExposeDTO updateVCItemExpose(VCItemExposeDTO request) {
        Optional<VCItem> vcItem = customVCItemRepository.findByIdAndIsTempFalse(request.getVcId());

        // vc 不存在
        if (vcItem.isEmpty()) {
            throw new BadRequestAlertException(StatusCodeSandbox.VC_Template_Not_Found.getMsg(),
                    "updateVCItemExpose vcid:" + request.getVcId(),
                    StatusCodeSandbox.VC_Template_Not_Found.getErrorKey());
        }

        // 帳號組織與VC組織不一致
        if (!vcItem.get().getBusinessId().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
            throw new BadRequestAlertException(StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getMsg(),
                    "updateVCItemExpose businessId:" + vcItem.get().getBusinessId() + " orgId:"
                            + SecurityUtils.getJwtUserObject().get(0).getOrgId(),
                    StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getErrorKey());
        }

        // 如果是一般帳號的特殊組織，則只允許建立者本身修改
        if (SecurityUtils.getJwtUserObject().get(0).getOrgId().equals(commonAccountOrgId)) {
            if (getLoginUserId() != vcItem.get().getCrUser()) {
                throw new BadRequestAlertException(StatusCodeSandbox.Non_Original_Creator.getMsg(),
                        "updateVCItemExpose vcid:" + vcItem.get().getSerialNo(),
                        StatusCodeSandbox.Non_Original_Creator.getErrorKey());
            }
        }

        vcItem.get().setExpose(request.getExpose());

        customVCItemRepository.save(vcItem.get());

        return request;
    }

    public VCItemDTO getVCItem(Long id) {
        Optional<VCItem> vcItem = vCItemRepository.findById(id);

        if (vcItem.isPresent()) {
            // 非特權帳號只能看到同組織建立的 VC Schema
            if (!checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId())) {
                // 非同組織，且模版也沒開 expose
                if (!vcItem.get().getBusinessId().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())
                        && (vcItem.get().getExpose() != null && !vcItem.get().getExpose())) {
                    throw new BadRequestAlertException(StatusCodeSandbox.No_View_Permission.getMsg(),
                            "no view permission", StatusCodeSandbox.No_View_Permission.getErrorKey());
                }
            }
        }

        return vCItemMapper.toDto(vcItem.get());
    }

    // 組成卡面要用的 url (/api/iamges/cover/{business_id}/{vc_id})
    private String getVCCardCoverUrl(String serialNo) {
        Optional<Setting> urlVCBasePathSetting = customSettingRepository.findByPropName("url.vc.base_path");

        if (urlVCBasePathSetting.isPresent()) {
            return (urlVCBasePathSetting.get().getPropValue() + "/api/images/cover/"
                    + SecurityUtils.getJwtUserObject().get(0).getOrgId() + "/" + serialNo);
        }

        return "";
    }

    /**
     * 檢查同組織 VC 模板代碼不可重複(依 DID 註冊組織判斷)
     * 
     * @param serialNo
     */
    private void checkVcSerialNoByDidOrg(String serialNo) {
        // 登入者統編
        String orgId = SecurityUtils.getJwtUserObject().get(0).getOrgId();

        // 查詢 DID 註冊組織代碼
        ResponseDTO<GetIssuerDataResDTO> didDataResponse = modadw101wService.getInitData(orgId);
        GetIssuerDataResDTO didData = didDataResponse.getData();
        if (!didData.isRegistered()) {
            log.warn("Unregistered DID");
            throw new BadRequestAlertException("Unregistered DID", serialNo, "UnregisteredDID");
        }

        // DID 註冊組織代碼
        String taxId = didData.getIssuerData().getData().getOrg().getTaxId();

        // 查詢已上鏈 VC Schema 列表
        VCSchemaResDto vcSchema = remoteApiService.getVCSchema(taxId);

        // 檢查該組織模板代碼是否存在
        boolean exists = vcSchema.getData().getSchemas().stream()
                .map(schema -> schema.getSchema().getSchemaDetail().getDescription())
                .filter(StringUtils::isNotBlank)
                .map(description -> {
                    // 格式為「數字_要比對的部分 using JsonSchema」
                    String withoutPrefix = StringUtils.substringAfter(description, "_");
                    return StringUtils.substringBefore(withoutPrefix, " using JsonSchema");
                })
                .anyMatch(onChainSerialNo -> StringUtils.equals(serialNo, onChainSerialNo));

        // 如有查到則為重複
        if (exists) {
            log.debug("SerialNo already exists : {}", serialNo);
            throw new BadRequestAlertException("Cannot Create a new VCItem already have an serial no ", serialNo,
                    "VC_SerialNo_Already_Exists");
        }
    }

    /**
     * 產製靜態QR Code
     * 
     * @param credentialType
     * @return String
     */
    public DwSandBoxVC201WStaticQRCodeResDTO getStaticQRCode(String credentialType) {
        DwSandBoxVC201WStaticQRCodeResDTO response = new DwSandBoxVC201WStaticQRCodeResDTO();

        // [檢核] 判空
        if (StringUtils.isEmpty(credentialType)) {
            throw new BadRequestAlertException(StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getMsg(), credentialType,
                    StatusCode.REQUEST_MISSING_REQUIRED_PARAM.getCode());
        }

        try {
            // QR Code內容
            String url = dwfrontUrl + "/api/moda/qrcode?type=staticqrcode&mode=vc&vcUid=" + credentialType;
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
     * 匯出靜態QR Code
     * 
     * @param base64
     * @return byte[]
     */
    public byte[] downloadStaticQRCode(DwSandBoxVC201WDownloadReqDTO req) {
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

    private void updateVCItemAndFields(VCItem vcItem, VCItemDTO vCItemDTO) {
        if (!vCItemDTO.getSerialNo().equals(vcItem.getSerialNo())) {
            log.debug("VCItem serial number change error : {}", vcItem);
            throw new BadRequestAlertException("serial no cannot change ", vCItemDTO.getSerialNo(), "change");
        }

        vcItem.setName(vCItemDTO.getName());
        vcItem.setUnitTypeExpire(vCItemDTO.getUnitTypeExpire());
        vcItem.setLengthExpire(vCItemDTO.getLengthExpire());
        vcItem.setCardCover(vCItemDTO.getCardCover());
        vcItem.setIsVerify(vCItemDTO.getIsVerify());
        vcItem.setIal(vCItemDTO.getIal());
        vcItem.setType(vCItemDTO.getType());
        vcItem.setIssuerServiceUrl(vCItemDTO.getIssuerServiceUrl());

        // 刪除 vc_item_id 相對應的 vc_item_field
        vCItemFieldRepository.deleteByVcItemId(vcItem.getId());
    }

    public void checkSerialNumber(String serialNo) {
        String orgId = SecurityUtils.getJwtUserObject().get(0).getOrgId();

        VCItem vcItem = vCItemRepository.findBySerialNoAndBusinessId(serialNo, orgId);

        if (vcItem != null) {
            log.debug("VCItem already exists : {}", vcItem);
            throw new BadRequestAlertException("A new vCItem cannot already have an serial no ", serialNo, "exists");
        }
    }

    public VCItemServiceUrlDTO updateVCItemIssuerServiceUrl(VCItemServiceUrlDTO request) {
        Optional<VCItem> vcItem = customVCItemRepository.findByIdAndIsTempFalse(request.getId());

        // vc 不存在
        if (vcItem.isEmpty()) {
            throw new BadRequestAlertException(StatusCodeSandbox.VC_Template_Not_Found.getMsg(),
                    "updateVCItemServiceUrl id:" + request.getId(),
                    StatusCodeSandbox.VC_Template_Not_Found.getErrorKey());
        }

        // 帳號組織與VC組織不一致
        if (!vcItem.get().getBusinessId().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
            throw new BadRequestAlertException(StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getMsg(),
                    "updateVCItemServiceUrl businessId:" + vcItem.get().getBusinessId() + " orgId:"
                            + SecurityUtils.getJwtUserObject().get(0).getOrgId(),
                    StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getErrorKey());
        }

        // 如果是一般帳號的特殊組織，則只允許建立者本身修改
        if (SecurityUtils.getJwtUserObject().get(0).getOrgId().equals(commonAccountOrgId)) {
            if (getLoginUserId() != vcItem.get().getCrUser()) {
                throw new BadRequestAlertException(StatusCodeSandbox.Non_Original_Creator.getMsg(),
                        "updateVCItemServiceUrl vcid:" + vcItem.get().getSerialNo(),
                        StatusCodeSandbox.Non_Original_Creator.getErrorKey());
            }
        }

        String type = request.getType();
        String issuerServiceUrl = request.getIssuerServiceUrl();

        // [檢核] 如果 type 有值，就必須是 "1" 或 "2"
        if (StringUtils.isNotBlank(type)) {
            if (!"1".equals(type) && !"2".equals(type)) {
                throw new BadRequestAlertException(StatusCodeSandbox.Invalid_Type_Value.getMsg(),
                        "updateVCItemServiceUrl vcid:" + vcItem.get().getSerialNo(),
                        StatusCodeSandbox.Invalid_Type_Value.getErrorKey());
            }
        }
        // [檢核] 兩者只能同時存在或同時為空，否則丟錯
        if (StringUtils.isNotBlank(type) && StringUtils.isBlank(issuerServiceUrl)) {
            throw new BadRequestAlertException(StatusCodeSandbox.Type_Or_IssuerServiceUrl_Missing.getMsg(),
                    "updateVCItemServiceUrl vcid:" + vcItem.get().getSerialNo(),
                    StatusCodeSandbox.Type_Or_IssuerServiceUrl_Missing.getErrorKey());
        }
        if (StringUtils.isBlank(type) && StringUtils.isNotBlank(issuerServiceUrl)) {
            throw new BadRequestAlertException(StatusCodeSandbox.Type_Or_IssuerServiceUrl_Missing.getMsg(),
                    "updateVCItemServiceUrl vcid:" + vcItem.get().getSerialNo(),
                    StatusCodeSandbox.Type_Or_IssuerServiceUrl_Missing.getErrorKey());
        }

        vcItem.get().setType(request.getType());
        vcItem.get().setIssuerServiceUrl(request.getIssuerServiceUrl());

        customVCItemRepository.save(vcItem.get());

        return request;
    }

    /**
     * 停止發行 VCItem
     *
     * @param request
     */
    public void stopIssuing(VCItemStopIssuingReqDTO request) {
        Long id = request.getId();
        Optional<VCItem> vcItemOpt = vCItemRepository.findById(id);

        // VC 模版不存在
        if (vcItemOpt.isEmpty()) {
            throw new BadRequestAlertException(StatusCodeSandbox.VC_Template_Not_Found.getMsg(),
                    "VC_Template_Not_Found vcid:" + id, StatusCodeSandbox.VC_Template_Not_Found.getErrorKey());
        }
        VCItem vcItem = vcItemOpt.get();

        // 帳號組織與模版組織一致
        if (!vcItem.getBusinessId().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
            throw new BadRequestAlertException(StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getMsg(),
                    "OrgId_Account_OrgId_Mismatch vcid:" + id,
                    StatusCodeSandbox.OrgId_Account_OrgId_Mismatch.getErrorKey());
        }

        // VC 模版未使用，可直接刪除
        if (Boolean.FALSE.equals(vcItem.getUsed())) {
            throw new BadRequestAlertException(
                    StatusCodeSandbox.The_VC_Template_Is_Not_Used_And_Can_Be_Deleted_Directly.getMsg(),
                    "The_VC_Template_Is_Not_Used_And_Can_Be_Deleted_Directly vcid:" + id,
                    StatusCodeSandbox.The_VC_Template_Is_Not_Used_And_Can_Be_Deleted_Directly.getErrorKey());
        }

        // 儲存 vc_item
        vcItem.setActivated(false);
        vCItemRepository.save(vcItem);
    }

}
