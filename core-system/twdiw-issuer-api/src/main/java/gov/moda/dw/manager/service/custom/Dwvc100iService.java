package gov.moda.dw.manager.service.custom;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Base64URL;

import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.VCItemDataRepository;
import gov.moda.dw.manager.repository.custom.CustomRegularExpressionRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.service.custom.common.QRCodeService;
import gov.moda.dw.manager.service.dto.CreateVCItemDataDTO;
import gov.moda.dw.manager.service.dto.CreateVCItemDataField;
import gov.moda.dw.manager.service.dto.Dwvc100iResDTO;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemDataFieldDTO;
import gov.moda.dw.manager.service.dto.custom.CustomVCItemDataDTO;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iReq;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iRes;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iRes.DwIssuerOidVci101iWarningsRes;
import gov.moda.dw.manager.service.dto.custom.DwIssuerVC501iReq;
import gov.moda.dw.manager.service.dto.custom.GetQrCodeWarningsDTO;
import gov.moda.dw.manager.service.dto.custom.OptionsDTO;
import gov.moda.dw.manager.service.dto.custom.RegularExpressionFieldInvalidDTO;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import gov.moda.dw.manager.type.QrcodeLinkType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.VcItemDataValidType;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.HttpXxxErrorExceptionHandler;
import gov.moda.dw.manager.util.RegexUtils;
import gov.moda.dw.manager.util.SandBoxUtil;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.DWException;
import gov.moda.dw.manager.web.rest.errors.InternalErrorAlertException;

/**
 * 電子證件資料服務實作類別
 * 負責處理電子證件資料的建立、驗證和儲存等相關操作
 * 包含有個資和無個資兩種版本的處理邏輯
 */
@Service
@Transactional
public class Dwvc100iService {

    // 記錄器，用於記錄系統日誌
    private static final Logger log = LoggerFactory.getLogger(Dwvc100iService.class);

    // QR Code 的前綴字串，用於Base64編碼的圖片資料
    private static final String QR_CODE_PREFIX = "data:image/png;base64,";

    // QR Code 的 LOGO，用於Base64編碼的圖片資料
    private static final String QR_CODE_LOGO = "img/dwIcon.png";

    // 電子證件項目資料庫存取介面
    private final CustomVCItemRepository customVCItemRepository;

    // 正則表達式資料庫存取介面
    private final CustomRegularExpressionRepository regularExpressionRepository;

    // 電子證件欄位資料庫存取介面
    private final CustomVCItemFieldRepository customVCItemFieldRepository;

    // 電子證件資料服務
    private final CustomVCItemDataService vcItemDataService;

    // 遠端API服務
    private final RemoteApiService remoteApiService;

    // QR碼服務
    private final QRCodeService qrCodeService;

    // 電子證件資料資料庫存取介面
    private final VCItemDataRepository vCItemDataRepository;

    // 電子證件項目DTO轉換器
    private final VCItemMapper vcItemMapper;

    // 前台服務 URL
    @Value("${dwfront.url}")
    private String dwfrontUrl;

    // 動態 Qr Code 型態，1: 原始 deeplink, 2: Universal/App link
    @Value("${qrcode.link.type:1}")
    private String qrcodeLinkType;

    /**
     * 建構子，注入所需的服務和資料庫存取介面
     */
    public Dwvc100iService(
            CustomVCItemRepository customVCItemRepository,
            CustomRegularExpressionRepository regularExpressionRepository,
            CustomVCItemFieldRepository customVCItemFieldRepository,
            CustomVCItemDataService vcItemDataService,
            RemoteApiService remoteApiService,
            QRCodeService qrCodeService,
            VCItemDataRepository vCItemDataRepository,
            VCItemMapper vcItemMapper
    ) {
        this.customVCItemRepository = customVCItemRepository;
        this.regularExpressionRepository = regularExpressionRepository;
        this.customVCItemFieldRepository = customVCItemFieldRepository;
        this.vcItemDataService = vcItemDataService;
        this.remoteApiService = remoteApiService;
        this.qrCodeService = qrCodeService;
        this.vCItemDataRepository = vCItemDataRepository;
        this.vcItemMapper = vcItemMapper;
    }

    /**
     * 檢查卡片日期格式
     * 格式必須為 YYYYMMDD，例如：20241231
     *
     * @param date 日期字串
     * @throws BadRequestAlertException 當格式不正確時拋出
     */
    private void checkDate(String date) {
        // 如果日期為空，則不進行檢查
        if (StringUtils.isBlank(date)) {
            return;
        }

        boolean isOK = true;
        // 檢查是否符合YYYYMMDD格式
        if (!date.matches("\\d{8}")) {
            isOK = false;
        } else {
            // 嘗試解析日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            try {
                LocalDate.parse(date, formatter);
                isOK = true;
            } catch (DateTimeParseException e) {
                isOK = false;
            }
        }

        // 如果格式不正確，拋出例外
        if (!isOK) {
            throw new DWException(StatusCode.DWVC_DATE_NOT_VALID);
        }
    }

    /**
     * 建立有個資版本的電子證件
     *
     * @param vCItemDataDTO 電子證件資料
     * @return 建立成功的電子證件資料
     */
    public Dwvc100iResDTO createVCHasDataForAPI(CreateVCItemDataDTO request) {
        log.debug("Request to createVCHasDataForAPI VCItemData");

        // 檢核 憑證唯一識別碼
        if (StringUtils.isBlank(request.getVcUid())) {
            throw new DWException(StatusCode.DWVC_PARAM_NOT_VALID);
        }

        // 驗證並取得VC項目
        VCItem vcItemEntity = this.validateAndGetVCItem(request.getVcUid());

        // 因 API 沒有給 cname，所以需要回補 cname
        this.checkCname(vcItemEntity, request);

        // 建立有個資的電子證件
        CustomVCItemDataDTO customVCItemDataDTO = this.createVCHasData(vcItemEntity, request);

        // 建立回傳物件
        Dwvc100iResDTO response = new Dwvc100iResDTO();
        response.setTransactionId(customVCItemDataDTO.getTransactionId());
        response.setQrCode(customVCItemDataDTO.getQrCode());
        response.setDeepLink(customVCItemDataDTO.getDeepLink());
        response.setWarnings(customVCItemDataDTO.getWarnings());

        return response;
    }

    /**
     * 因 API 沒有給 cname，所以需要回補 cname
     *
     * @param vcItemEntity VC項目實體
     * @param vCItemDataDTO 電子證件資料DTO
     */
    private void checkCname(VCItem vcItemEntity, CreateVCItemDataDTO vCItemDataDTO) {
        if (CollectionUtils.isNotEmpty(vCItemDataDTO.getFields())) {
            List<VCItemField> vcItemFields = customVCItemFieldRepository.findByVcItemId(vcItemEntity.getId());
            Map<String, String> enameToChameMap = vcItemFields.stream().filter(
                    field -> StringUtils.isNotBlank(field.getEname()) && StringUtils.isNotBlank(field.getCname()))
                    .collect(Collectors.toMap(VCItemField::getEname, VCItemField::getCname));

            for (CreateVCItemDataField field : vCItemDataDTO.getFields()) {
                if (StringUtils.isBlank(field.getCname()) && StringUtils.isNotBlank(field.getEname())) {
                    String cname = enameToChameMap.get(field.getEname());
                    if (StringUtils.isNotBlank(cname)) {
                        field.setCname(cname);
                    }
                }
            }
        }
    }

    /**
     * 建立有個資版本的電子證件（Web版本）
     *
     * @param vCItemDataDTO 電子證件資料DTO
     * @return 建立成功的電子證件資料DTO
     * @throws JsonProcessingException 當JSON處理失敗時拋出
     */
    public CustomVCItemDataDTO createVCHasDataForWeb(CreateVCItemDataDTO vCItemDataDTO) {
        log.debug("Request to createVCHasDataForWeb VCItemData");

        // 根據ID查詢VC項目
        Optional<VCItem> vcitemEntityOpt = customVCItemRepository.findByIdAndIsTempFalse(vCItemDataDTO.getVcId());

        // 如果找不到VC項目，拋出例外
        if (vcitemEntityOpt.isEmpty()) {
            throw HttpXxxErrorExceptionHandler.genHttpClientErrorException(HttpStatus.NOT_FOUND, "11001", "vcUid不存在");
        }

        // 建立有個資的電子證件
        return this.createVCHasData(vcitemEntityOpt.get(), vCItemDataDTO);
    }

    /**
     * 建立有個資版本的電子證件
     *
     * @param vcitemEntity VC項目實體
     * @param vCItemDataDTO 電子證件資料
     * @return 建立成功的電子證件資料
     */
    private CustomVCItemDataDTO createVCHasData(VCItem vcitemEntity, CreateVCItemDataDTO vcItemDataDTO) {
        log.debug("Request to save VCItemData");

        // 檢查 VC 模板是否停止發行
        if (Boolean.FALSE.equals(vcitemEntity.getActivated())) {
            throw new DWException(StatusCode.DWVC_VC_ITEM_NOT_ACTIVE);
        }

        // 檢查卡片發行日格式
        this.checkDate(vcItemDataDTO.getIssuanceDate());

        // 檢查卡片到期日格式
        this.checkDate(vcItemDataDTO.getExpiredDate());

        // 驗證欄位資料
        this.validateFields(vcItemDataDTO.getFields(), vcitemEntity.getId());

        // 準備儲存資料
        String transactionId = UUID.randomUUID().toString();
        Instant createDatetime = Instant.ofEpochMilli(System.currentTimeMillis());

        // 計算過期時間
        Instant expireDatetime;
        if (StringUtils.isBlank(vcItemDataDTO.getExpiredDate())) {
            // 如果沒有指定到期日，使用預設的過期時間計算方式
            expireDatetime = DateUtils.calculate(createDatetime, vcitemEntity.getUnitTypeExpire(),
                    vcitemEntity.getLengthExpire());
        } else {
            // 如果指定了到期日，使用指定的到期日
            expireDatetime = this.getExpireDatetime(vcItemDataDTO.getExpiredDate());
        }

        // 呼叫 501i API
        this.call501iApi(transactionId, vcitemEntity, vcItemDataDTO);

        // 取得 QR 碼和 DeepLink 和 Warnings
        DwIssuerOidVci101iRes qrCodeAndDeepLinkAndWarnings = this.getQrCodeAndDeepLinkAndWarnings(vcitemEntity,
                transactionId, vcItemDataDTO.getTxCode(), vcItemDataDTO.getCids());
        String deepLink = Optional.ofNullable(qrCodeAndDeepLinkAndWarnings).map(DwIssuerOidVci101iRes::getLink).orElse("");

        // 依動態 Qr Code 型態回傳不同 link
        QrcodeLinkType qrcodeLinkTypeEnum = QrcodeLinkType.toQrcodeLinkType(qrcodeLinkType);
        if (QrcodeLinkType.UNIVERSAL_LINK.equals(qrcodeLinkTypeEnum)) {
            Base64URL universalLink = Base64URL.encode(deepLink);
            deepLink = dwfrontUrl + "/api/moda/vcqrcode?mode=vc01&deeplink=" + universalLink;
        }

        DwIssuerOidVci101iWarningsRes warningsRes = Optional.ofNullable(qrCodeAndDeepLinkAndWarnings)
                .map(DwIssuerOidVci101iRes::getWarnings)
                .orElse(null);

        // 儲存資料
        VCItemData vCItemData = this.saveVCItemData(
                transactionId,
                this.generateQRCodeFromDeepLink(deepLink),
                vcitemEntity,
                vcItemDataDTO,
                expireDatetime,
                createDatetime
        );

        // 更新VC項目使用狀態
        this.updateVCItemUsageStatus(vcitemEntity);

        // 轉換並回傳結果
        return this.convertToDTO(vCItemData, vcItemDataDTO, deepLink, warningsRes);
    }

    /**
     * 將到期日字串轉換為Instant物件
     *
     * @param expireDate 到期日字串（格式：YYYYMMDD）
     * @return 到期時間的Instant物件
     */
    private Instant getExpireDatetime(String expireDate) {
        // 將字串轉為LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(expireDate, formatter);

        // 設定時間為當天的 23:59:59
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        // 加入時區並轉換為Instant
        return endOfDay.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * 驗證並取得 VC 項目
     *
     * @param vcUid VC項目UID
     * @return VC項目實體
     */
    private VCItem validateAndGetVCItem(String vcUid) {
        String[] parts = vcUid.split("_", 2);
        VCItem vcItem = customVCItemRepository.findBySerialNoAndBusinessId(parts[1], parts[0]);

        if (vcItem == null) {
            throw new DWException(StatusCode.DWVC_VC_ITEM_NOT_FOUND);
        }

        return vcItem;
    }

    /**
     * 驗證欄位資料
     *
     * @param fields 欄位資料列表
     * @param vcItemId VC項目ID
     * @throws JsonProcessingException 當JSON處理失敗時拋出
     */
    private void validateFields(List<CreateVCItemDataField> fields, Long vcItemId) {
        // 取得VC項目的欄位定義
        List<VCItemField> vcItemFieldList = customVCItemFieldRepository.findByVcItemId(vcItemId);

        // 驗證欄位資料
        List<RegularExpressionFieldInvalidDTO> invalidFields = this.validateFieldsWithRegex(fields, vcItemFieldList);

        // 如果有無效的欄位，拋出例外
        if (!invalidFields.isEmpty()) {
            String result = invalidFields.stream().flatMap(field -> field.getInvalid().stream())
                    .collect(Collectors.joining(","));

            throw new DWException(StatusCode.DWVC_REGEX_NOT_VALID.getCode(), result);
        }
    }

    /**
     * 使用正則表達式驗證欄位
     *
     * @param fields 欄位資料列表
     * @param vcItemFieldList VC項目欄位列表
     * @return 無效欄位列表
     */
    private List<RegularExpressionFieldInvalidDTO> validateFieldsWithRegex(
            List<CreateVCItemDataField> fields,
            List<VCItemField> vcItemFieldList
    ) {

        // 先檢核是否所有的必填欄位都有值
        this.validateRequiredFieldsData(fields, vcItemFieldList);

        // 取得必要的正則表達式規則
        List<RegularExpression> regularExpressionList = regularExpressionRepository.findByType("require");
        List<Pattern> requireRegularPattern = regularExpressionList.stream()
                .map(re -> RegexUtils.matchWithTimeout(re.getRegularExpression()))
                .collect(Collectors.toList());
        List<RegularExpressionFieldInvalidDTO> invalidFields = new ArrayList<>();

        // 將使用者沒有填的非必填欄位補上
        if (fields.size() != vcItemFieldList.size()) {
            for (VCItemField vcItemField : vcItemFieldList) {
                if (fields.stream().noneMatch(field -> field.getEname().equals(vcItemField.getEname()))) {
                    CreateVCItemDataField newField = new CreateVCItemDataField();
                    newField.setEname(vcItemField.getEname());
                    newField.setContent("");
                    newField.setType(vcItemField.getType());
                    newField.setCname(vcItemField.getCname());
                    fields.add(newField);
                }
            }
        }

        // 驗證每個欄位
        for (CreateVCItemDataField field : fields) {
            // 尋找對應的VC項目欄位定義
            VCItemField vcItemField = this.findVCItemField(vcItemFieldList, field.getEname());
            if (vcItemField == null) {
                throw new DWException(StatusCode.DWVC_VC_TEMPLATE_FIELD_NOT_FOUND);
            }

            // 若 isRequired = false(非必填) 且欄位值為空，則跳過此欄位驗證
            if (Boolean.FALSE.equals(vcItemField.getIsRequired()) && StringUtils.isBlank(field.getContent())) {
                continue;
            }

            // 驗證欄位資料
            RegularExpressionFieldInvalidDTO fieldCheckResult = this.validateFieldWithRegex(
                    field,
                    vcItemField,
                    regularExpressionList,
                    requireRegularPattern
            );

            // 如果有無效的欄位，加入無效欄位列表
            if (CollectionUtils.isNotEmpty(fieldCheckResult.getInvalid())) {
                invalidFields.add(fieldCheckResult);
            }
        }

        return invalidFields;
    }

    /**
     * 尋找VC項目欄位
     *
     * @param vcItemFieldList VC項目欄位列表
     * @param ename 英文名稱
     * @return VC項目欄位
     */
    private VCItemField findVCItemField(List<VCItemField> vcItemFieldList, String ename) {
        return vcItemFieldList.stream()
                .filter(field -> field.getEname().equals(ename))
                .findFirst()
                .orElse(null);
    }

    /**
     * 使用正則表達式驗證單一欄位
     *
     * @param field 欄位資料
     * @param vcItemField VC項目欄位
     * @param regularExpressionList 正則表達式列表
     * @param requireRegularPattern 必要正則表達式模式列表
     * @return 欄位驗證結果
     */
    private RegularExpressionFieldInvalidDTO validateFieldWithRegex(
            CreateVCItemDataField field,
            VCItemField vcItemField,
            List<RegularExpression> regularExpressionList,
            List<Pattern> requireRegularPattern
    ) {
        // 建立欄位驗證結果物件
        RegularExpressionFieldInvalidDTO fieldCheckResult = new RegularExpressionFieldInvalidDTO();
        fieldCheckResult.setEname(vcItemField.getEname());
        fieldCheckResult.setCname(vcItemField.getCname());
        fieldCheckResult.setValue(field.getContent());
        fieldCheckResult.setInvalid(new ArrayList<>());

        // 檢查通用正則表達式
        for (int i = 0; i < requireRegularPattern.size(); i++) {
            String ruleType = regularExpressionList.get(i).getRuleType();
            boolean matches = RegexUtils.isMatchWithTimeout(requireRegularPattern.get(i), field.getContent());

            // 根據規則類型檢查是否符合
            if ((ruleType.equals("allow") && !matches) || (ruleType.equals("deny") && matches)) {
                fieldCheckResult.getInvalid().add(regularExpressionList.get(i).getErrorMsg());
            }
        }

        // 檢查欄位特定正則表達式
        Pattern pattern = RegexUtils.matchWithTimeout(vcItemField.getRegularExpression().getRegularExpression());
        boolean matches = RegexUtils.isMatchWithTimeout(pattern, field.getContent());
        String type = vcItemField.getRegularExpression().getRuleType();

        // 根據規則類型檢查是否符合
        if ((type.equals("allow") && !matches) || (type.equals("deny") && matches)) {
            fieldCheckResult.getInvalid().add(vcItemField.getRegularExpression().getErrorMsg());
        }

        return fieldCheckResult;
    }

    /**
     * 呼叫 501i API
     *
     * @param transactionId 交易ID
     * @param vcItem VC項目
     * @param vCItemDataDTO 電子證件資料DTO
     */
    private void call501iApi(String transactionId, VCItem vcItem, CreateVCItemDataDTO vCItemDataDTO) {
        // 準備API請求資料
        DwIssuerVC501iReq req501i = new DwIssuerVC501iReq();
        req501i.setTransactionId(transactionId);
        req501i.setCredentialType(vcItem.getBusinessId() + "_" + vcItem.getSerialNo());
        req501i.setData(vCItemDataDTO.fieldToJsonObject());

        // 若有傳入指定發行日、到期日
        String issuanceDateReq = vCItemDataDTO.getIssuanceDate();
        String expiredDateReq = vCItemDataDTO.getExpiredDate();

        Boolean hasIssuanceDate = StringUtils.isNotBlank(issuanceDateReq);
        Boolean hasExpiredDate = StringUtils.isNotBlank(expiredDateReq);
        if (hasIssuanceDate || hasExpiredDate) {
            OptionsDTO options = new OptionsDTO();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate now = LocalDate.now();
            LocalDate vcCrDate = vcItem.getCrDatetime().atZone(ZoneId.systemDefault()).toLocalDate();

            if (Boolean.TRUE.equals(hasIssuanceDate)) {
                LocalDate issuanceDate = LocalDate.parse(issuanceDateReq, formatter);
                // 發行日不可以是未來時間
                if (issuanceDate.isAfter(now)) {
                    throw new DWException(StatusCode.DWVC_ISSUSNCE_DATE_NOT_ALLOWED);
                }
                options.setIssuanceDate(issuanceDateReq);
            }

            if (Boolean.TRUE.equals(hasExpiredDate)) {
                LocalDate expiredDate = LocalDate.parse(expiredDateReq, formatter);
                // 到期日需大於今日及 VC 建立日期
                if (expiredDate.isBefore(now) || expiredDate.isBefore(vcCrDate)) {
                    throw new DWException(StatusCode.DWVC_EXPIRED_DATE_NOT_ALLOWED);
                }
                options.setExpirationDate(expiredDateReq);
            }

            req501i.setOptions(options);
        }

        // 呼叫 API
        vcItemDataService.callApi501i(transactionId, req501i);
    }

    /**
     * 取得QR碼和DeepLink
     *
     * @param vcItem VC項目
     * @param transactionId 交易ID
     * @param txCode 交易代碼
     * @param cids 欲廢止卡片的 id 清單
     * @return QR碼和DeepLink陣列
     */
    private DwIssuerOidVci101iRes getQrCodeAndDeepLinkAndWarnings(VCItem vcItem, String transactionId, String txCode,
            List<String> cids) {
        // 產生JWT
        String jwtForApi101i = vcItemDataService.getJwtForApi101iRequest(
                SandBoxUtil.getUserId().toString(),
                vcItem.getBusinessId(),
                vcItem.getBusinessId() + "_" + vcItem.getSerialNo(),
                transactionId,
                txCode
        );
        return this.getQrCodeAndDeepLinkAndWarnings(jwtForApi101i, transactionId, vcItem, cids);
    }

    /**
     * 取得QR碼和DeepLink（無個資版本）
     *
     * @param jwtForApi101i API 101i的JWT
     * @param transactionId 交易ID
     * @param vcItem VC項目
     * @param cids 欲廢止卡片的 id 清單
     * @return QR碼和DeepLink陣列
     */
    private DwIssuerOidVci101iRes getQrCodeAndDeepLinkAndWarnings(String jwtForApi101i, String transactionId,
            VCItem vcItem, List<String> cids) {
        // 如果JWT為空，回傳空字串
        if (jwtForApi101i.isEmpty()) {
            return null;
        }

        // 準備API請求資料
        DwIssuerOidVci101iReq req = new DwIssuerOidVci101iReq();
        req.setAuthenticated(true);
        req.setIdToken(jwtForApi101i);
        req.setCid(cids);

        // 呼叫API取得QR碼和DeepLink
        return remoteApiService.callApi101i(transactionId, vcItem.getBusinessId(), req);
    }

    /**
     * 儲存VC項目資料（有個資版本）
     *
     * @param transactionId 交易ID
     * @param qrcode QR碼
     * @param vcItem VC項目
     * @param vCItemDataDTO 電子證件資料DTO
     * @param expireDatetime 過期時間
     * @param createDatetime 建立時間
     * @return 儲存的VC項目資料
     */
    private VCItemData saveVCItemData(
            String transactionId,
            String qrcode,
            VCItem vcItem,
            CreateVCItemDataDTO vCItemDataDTO,
            Instant expireDatetime,
            Instant createDatetime
    ) {
        // 建立VC項目資料實體
        VCItemData vCItemData = new VCItemData();
        vCItemData.setTransactionId(transactionId);
        vCItemData.setQrCode(qrcode);
        vCItemData.setVcItem(vcItem);
        vCItemData.setValid(VcItemDataValidType.INACTIVE.getCode());
        vCItemData.setExpired(expireDatetime);
        vCItemData.setBusinessId(vcItem.getBusinessId());
        vCItemData.setCrUser(vcItemDataService.getLoginUserId());
        vCItemData.crDatetime(createDatetime);
        vCItemData.setVcItemName(vcItem.getName());
        vCItemData.setDataTag(vCItemDataDTO.getDataTag());

        // 儲存資料
        return vCItemDataRepository.save(vCItemData);
    }

    /**
     * 更新VC項目使用狀態
     *
     * @param vcItem VC項目
     */
    private void updateVCItemUsageStatus(VCItem vcItem) {
        // 如果VC項目尚未被使用，則標記為已使用
        if (vcItem.getUsed() == null || !vcItem.getUsed()) {
            vcItem.setUsed(true);
            customVCItemRepository.save(vcItem);
        }
    }

    /**
     * 轉換為DTO
     *
     * @param vCItemData VC項目資料
     * @param vCItemDataDTO
     * @param deepLink DeepLink
     * @param warningsRes Warnings
     * @return VC項目資料DTO
     */
    private CustomVCItemDataDTO convertToDTO(VCItemData vCItemData, CreateVCItemDataDTO vCItemDataDTO, String deepLink,
            DwIssuerOidVci101iWarningsRes warningsRes) {
        // 轉換為 CustomDTO
        CustomVCItemDataDTO customVCItemDataDTO = new CustomVCItemDataDTO();
        BeanUtils.copyProperties(vCItemData, customVCItemDataDTO);

        // 自組 content, 加上 欄位是否必填
        String customContent = this.rebuildVcItemFieldContentJson(vCItemData, vCItemDataDTO);
        customVCItemDataDTO.setContent(customContent);
        customVCItemDataDTO.setDeepLink(deepLink);

        if (null != warningsRes) {
            GetQrCodeWarningsDTO warnings = new GetQrCodeWarningsDTO();
            warnings.setStatusRevoke(warningsRes.getStatusRevoke());
            warnings.setCidNotFound(warningsRes.getCidNotFound());
            customVCItemDataDTO.setWarnings(warnings);
        }

        VCItemDTO vcItemDTO = vcItemMapper.toDto(vCItemData.getVcItem());
        customVCItemDataDTO.setVcItem(vcItemDTO);

        return customVCItemDataDTO;
    }

    /**
     * 將 vc_data content 資料重新組合，補上欄位是否必填
     *
     * @param vcItemData
     * @param vCItemDataDTO
     * @return
     */
    private String rebuildVcItemFieldContentJson(VCItemData vcItemData, CreateVCItemDataDTO vCItemDataDTO) {
        List<VCItemDataFieldDTO> vcItemDataFieldDTOList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 將 vc_data content 資料重新組合，補上欄位是否必填
            vcItemDataFieldDTOList = objectMapper.readValue(vCItemDataDTO.fieldsToJson(),
                    new TypeReference<List<VCItemDataFieldDTO>>() {
                    });

            for (VCItemDataFieldDTO vcItemDataFieldDTO : vcItemDataFieldDTOList) {
                VCItemField vcItemField = customVCItemFieldRepository
                        .findByVcItemIdAndEname(vcItemData.getVcItem().getId(), vcItemDataFieldDTO.getEname());

                vcItemDataFieldDTO.setIsRequired(vcItemField.getIsRequired());
            }

            String escapeCustomContent = "";

            // 轉換為字串
            escapeCustomContent = objectMapper.writeValueAsString(vcItemDataFieldDTOList);

            return escapeCustomContent;

        } catch (Exception ex) {
            log.debug("Failed to rebuild VCItemFieldContentJson, content: {}", vcItemDataFieldDTOList);
            throw new InternalErrorAlertException("error to rebuild VCItemFieldContentJson: ", ex.getMessage(), "");
        }

    }

    /**
     * 將 DeepLink 轉換成 QR 碼的 base64 格式
     *
     * @param deepLink DeepLink 字串
     * @return QR 碼的 base64 格式字串，包含 data:image/png;base64, 前綴
     */
    private String generateQRCodeFromDeepLink(String deepLink) {
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
            throw new InternalErrorAlertException("生成 QR 碼失敗", ex.getMessage(), "QRCodeGenerationError");
        }
    }

    private void validateRequiredFieldsData(List<CreateVCItemDataField> fields, List<VCItemField> vcItemFieldList) {
        // 檢查必填欄位是否有值
        for (VCItemField vcItemField : vcItemFieldList) {
            // 只檢查必填欄位
            if (vcItemField.getIsRequired() != null && vcItemField.getIsRequired()) {
                // 在使用者傳入的 fields 中尋找對應的欄位
                CreateVCItemDataField userField = fields.stream()
                        .filter(field -> field.getEname().equals(vcItemField.getEname()))
                        .findFirst()
                        .orElse(null);

                // 如果找不到對應欄位或欄位內容為空，則拋出異常
                if (userField == null || StringUtils.isBlank(userField.getContent())) {
                    throw HttpXxxErrorExceptionHandler.genHttpClientErrorException(HttpStatus.BAD_REQUEST, "11001",
                            "缺少參數或參數不合法");
                }
            }
        }
    }

}
