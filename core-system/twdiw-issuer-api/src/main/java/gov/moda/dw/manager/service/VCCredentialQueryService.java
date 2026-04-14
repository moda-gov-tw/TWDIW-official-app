package gov.moda.dw.manager.service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import gov.moda.dw.manager.domain.VCCredential;
import gov.moda.dw.manager.domain.VCCredential_;
import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.repository.VCCredentialRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemDataRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.criteria.VCCredentialCriteria;
import gov.moda.dw.manager.service.custom.CustomUserService;
import gov.moda.dw.manager.service.custom.common.CustomVCDataStatusLogService;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.VCCredentialDTO;
import gov.moda.dw.manager.service.dto.VCItemDataEffectDTO;
import gov.moda.dw.manager.service.dto.VCItemDataFieldDTO;
import gov.moda.dw.manager.service.dto.custom.CustomVcDataStatusLogDTO;
import gov.moda.dw.manager.service.dto.custom.VCCredentialQueryDTO;
import gov.moda.dw.manager.service.dto.custom.VcItemDataFieldDataResDTO;
import gov.moda.dw.manager.type.StatusCodeSandbox;
import gov.moda.dw.manager.type.VcItemDataValidType;
import gov.moda.dw.manager.util.RSAUtils;
import gov.moda.dw.manager.util.SandBoxUtil;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.InternalErrorAlertException;
import tech.jhipster.service.QueryService;

@Service
@Transactional
public class VCCredentialQueryService extends QueryService<VCCredential> {

    private final Logger log = LoggerFactory.getLogger(VCCredentialQueryService.class);

    private final VCCredentialRepository vCCredentialRepository;

    private final CustomVCItemRepository customVCItemRepository;

    private final CustomVCItemDataRepository customVCItemDataRepository;

    private final CustomVCItemFieldRepository customVCItemFieldRepository;

    private final CustomUserService customUserService;

    private final CustomVCDataStatusLogService customVCDataStatusLogService;

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    @Value("${sandbox.commonAccountOrgId:00000000}")
    private String commonAccountOrgId;

    @Value("${fin.rsaUtil.prvKy}")
    private String privateKey;

    @Value("${fin.rsaUtil.pky}")
    private String publicKey;

    public VCCredentialQueryService(
        VCCredentialRepository vCCredentialRepository,
        CustomVCItemRepository customVCItemRepository,
        CustomVCItemDataRepository customVCItemDataRepository,
        CustomVCItemFieldRepository customVCItemFieldRepository,
        CustomUserService customUserService,
        CustomVCDataStatusLogService customVCDataStatusLogService
    ) {
        this.vCCredentialRepository = vCCredentialRepository;
        this.customVCItemRepository = customVCItemRepository;
        this.customVCItemDataRepository = customVCItemDataRepository;
        this.customVCItemFieldRepository = customVCItemFieldRepository;
        this.customUserService = customUserService;
        this.customVCDataStatusLogService = customVCDataStatusLogService;
    }

    @Transactional(readOnly = true)
    public Page<VCItemDataEffectDTO> findBySQL(VCCredentialQueryDTO request, Pageable pageable)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.debug("find by sql : {}", request);

        List<VCItemDataEffectDTO> vcItemDataEffectDTOS = new ArrayList<>();

        if (StringUtils.isNotEmpty(request.getVcSerialNo())) {
            request.setVcSerialNo(request.getVcSerialNo().replace("_", "\\_").replace("%", "\\%"));
        }

        if (StringUtils.isNotEmpty(request.getDataTag())) {
            request.setDataTag(request.getDataTag().replace("_", "\\_").replace("%", "\\%"));
        }

        if (StringUtils.isNotEmpty(request.getTransactionId())) {
            request.setTransactionId(request.getTransactionId().replace("_", "\\_").replace("%", "\\%"));
        }

        Page<VCCredentialDTO> vcCredentials = vCCredentialRepository.findBySql(request, pageable);

        for (VCCredentialDTO item : vcCredentials) {
            VCItemDataEffectDTO vcItemData = getVcItemDataEffectDTO(item);
            vcItemDataEffectDTOS.add(vcItemData);
        }

        return new PageImpl<>(vcItemDataEffectDTOS, pageable, vcCredentials.getTotalElements());
    }

    private VCItemDataEffectDTO getVcItemDataEffectDTO(VCCredentialDTO item) throws NoSuchAlgorithmException, InvalidKeySpecException {
        VCItemDataEffectDTO vcItemData = new VCItemDataEffectDTO();

        String cidEncode = RSAUtils.publicEncryptReturnBase32(item.getCid(), RSAUtils.getPublicKey(publicKey));
        String cidMask = SandBoxUtil.maskVCCid(item.getCid());
        String dataTag = "";
        if (StringUtils.isNotBlank(item.getDataTag())) {
            dataTag = SandBoxUtil.maskVCCid(item.getDataTag());
        }
        VcItemDataValidType vcStatus = VcItemDataValidType.getByValue(item.getCredentialStatus());

        vcItemData.setVcSerialNo(item.getSerialNo());
        vcItemData.setVcCid(cidEncode);
        vcItemData.setVcCidMask(cidMask);
        vcItemData.setIssuanceDate(item.getIssuanceDate());
        vcItemData.setStatus(item.getCredentialStatus());
        vcItemData.setOrgId(item.getOrgId());
        vcItemData.setOrgTwName(item.getOrgTwName());
        vcItemData.setCredentialType(item.getCredentialType());
        vcItemData.setVcName(item.getName());
        vcItemData.setClearScheduleDatetime(item.getClearScheduleDatetime());
        vcItemData.setClearScheduleId(item.getClearScheduleId());
        vcItemData.setStatusName(vcStatus.getValidName());
        vcItemData.setExpiredDate(item.getExpirationDate());
        vcItemData.setIsExpired(Instant.now().isAfter(item.getExpirationDate()));
        vcItemData.setDataTag(dataTag);
        vcItemData.setTransactionId(item.getTransactionId());

        return vcItemData;
    }

    @Transactional(readOnly = true)
    public Page<VCItemDataEffectDTO> findByCriteria(VCCredentialCriteria criteria, Pageable pageable) {
        log.debug("find by criteria : {}", criteria);
        final Specification<VCCredential> spec = createSpecification(criteria);

        String fullString = criteria.getCredentialType().getEquals();
        int separatorIndex = fullString.indexOf('_'); // 找到第一個 _ 的索引
        String businessId = fullString.substring(0, separatorIndex); // 前部分
        String vcSerialNo = fullString.substring(separatorIndex + 1); // 後部分

        VCItem vcItem = customVCItemRepository.findBySerialNoAndBusinessId(vcSerialNo, businessId);

        Page<VCCredential> vcCredentials = vCCredentialRepository.findAll(spec, pageable);

        List<VCItemDataEffectDTO> vcItemDataEffectDTOS = new ArrayList<>();

        for (VCCredential item : vcCredentials) {
            VCItemDataEffectDTO vcItemData = new VCItemDataEffectDTO();
            vcItemData.setVcSerialNo(vcSerialNo);
            vcItemData.setVcCid(item.getCid());
            vcItemData.setIssuanceDate(item.getIssuanceDate());
            vcItemData.setStatus(item.getCredentialStatus());
            if (vcItem != null) {
                vcItemData.setVcName(vcItem.getName());
            }
            vcItemDataEffectDTOS.add(vcItemData);
        }

        return new PageImpl<>(vcItemDataEffectDTOS, pageable, vcCredentials.getTotalElements());
    }

    protected Specification<VCCredential> createSpecification(VCCredentialCriteria criteria) {
        Specification<VCCredential> spec = Specification.where(null);
        if (criteria != null) {
            if (criteria.getCredentialType() != null) {
                spec = spec.and(buildStringSpecification(criteria.getCredentialType(), VCCredential_.credentialType));
            }
            if (criteria.getIssuanceDate() != null) {
                spec = spec.and(buildRangeSpecification(criteria.getIssuanceDate(), VCCredential_.issuanceDate));
            }
            if (criteria.getCredentialStatus() != null) {
                spec = spec.and(buildStringSpecification(criteria.getCredentialStatus(), VCCredential_.credentialStatus));
            }
        }
        return spec;
    }

    public static String decodeJwtHeader(String jwt) {
        // 分割 JWT，取出 Header 部分
        String[] parts = jwt.split("\\.");

        // Base64Url 解碼 Header
        byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[0]);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    private Map<String, Claim> getClaims(String jwt) {
        // 解析 JWT，不進行簽章驗證
        DecodedJWT decodedJWT = JWT.decode(jwt);
        // 獲取 payload 的聲明
        Map<String, Claim> claimsMap = decodedJWT.getClaims();
        // 將 Map 轉換為 List<Claim>
        return claimsMap;
    }

    public VcItemDataFieldDataResDTO findVcItemDetail(String vccid) {
        try {
            VCCredential vcCredential = vCCredentialRepository.findByCid(vccid);

            if (vcCredential == null) {
                throw new BadRequestAlertException(
                    StatusCodeSandbox.VC_Data_Not_Found.getMsg(),
                    "cid:" + vccid,
                    StatusCodeSandbox.VC_Data_Not_Found.getErrorKey()
                );
            }

            VcItemDataFieldDataResDTO result = new VcItemDataFieldDataResDTO();

            String fullString = vcCredential.getCredentialType();
            int separatorIndex = fullString.indexOf('_'); // 找到第一個 _ 的索引
            String businessId = fullString.substring(0, separatorIndex); // 前部分
            String vcSerialNo = fullString.substring(separatorIndex + 1); // 後部分

            // 拿模版資料，後面用來帶出該模版有哪些資料欄位
            VCItem vcItem = customVCItemRepository.findBySerialNoAndBusinessId(vcSerialNo, businessId);

            // 非特權帳號只能看到同組織建立的 VC 資料，非特權但又是一般帳號專屬組織的只能看到自己建的模版的
            if (!checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId())) {
                if (SecurityUtils.getJwtUserObject().get(0).getOrgId().equals(commonAccountOrgId)) {
                    if (
                        !businessId.equals(SecurityUtils.getJwtUserObject().get(0).getOrgId()) ||
                        !vcItem.getCrUser().equals(customUserService.getLoginUserId())
                    ) {
                        throw new BadRequestAlertException(
                            StatusCodeSandbox.No_View_Permission.getMsg(),
                            "cid:" + vccid,
                            StatusCodeSandbox.No_View_Permission.getErrorKey()
                        );
                    }
                } else {
                    if (!businessId.equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
                        throw new BadRequestAlertException(
                            StatusCodeSandbox.No_View_Permission.getMsg(),
                            "cid:" + vccid,
                            StatusCodeSandbox.No_View_Permission.getErrorKey()
                        );
                    }
                }
            }

            // 查詢 VC 資料狀態紀錄 By vcCid
            if (vcItem != null) {
                result.setSerialNo(vcItem.getSerialNo());
                result.setName(vcItem.getName());

                VCItemData vcItemData = customVCItemDataRepository.findFirstByTransactionId(vcCredential.getNonce());
                List<VCItemDataFieldDTO> vcItemDataFieldDTOList = new ArrayList<>();

                // 當 credential 為撤銷且 vc_data_status_log 無撤銷紀錄時，建立一筆 VC 資料狀態紀錄
                if (null != vcItemData) {
                    Integer revoked = VcItemDataValidType.REVOKED.getCode();
                    long revokedCount = customVCDataStatusLogService.getStatusLogByVcCidOrderByCrDatetimeDesc(vccid)
                            .stream().filter(it -> Objects.equals(revoked, it.getValid())).count();

                    if (StringUtils.equals(String.valueOf(revoked), vcCredential.getCredentialStatus())
                            && revokedCount == 0) {
                        // VC 成功撤銷後回寫 vcItemData valid
                        vcItemData.setValid(revoked);
                        customVCItemDataRepository.save(vcItemData);

                        // 建立一筆 VC 資料狀態紀錄
                        customVCDataStatusLogService.createStatusLog(vccid, vcItemData.getId(),
                                vcItemData.getTransactionId(), revoked, vcCredential.getLastUpdateTime(), false);
                    }
                }

                // 將 vc 資料重新組合，補上欄位中文名稱資料
                vcItemDataFieldDTOList = customVCItemFieldRepository.findByVcItemId(vcItem.getId()).stream().map(field -> {
                    VCItemDataFieldDTO vcItemDataFieldDTO = new VCItemDataFieldDTO();
                    vcItemDataFieldDTO.setType(field.getType());
                    vcItemDataFieldDTO.setCname(field.getCname());
                    vcItemDataFieldDTO.setEname(field.getEname());
                    vcItemDataFieldDTO.setIsRequired(field.getIsRequired());
                    return vcItemDataFieldDTO;
                }).toList();

                result.setContent(vcItemDataFieldDTOList);

                // 查詢 VC 資料狀態紀錄 By vcCid
                List<CustomVcDataStatusLogDTO> statusLogList = new ArrayList<>();
                customVCDataStatusLogService.getStatusLogByVcCidOrderByCrDatetimeDesc(vccid).forEach(statusLog -> {
                    Instant logDatetime = Objects.requireNonNullElse(statusLog.getLastUpdateTime(),
                            statusLog.getCrDatetime());

                    CustomVcDataStatusLogDTO statusLogDTO = new CustomVcDataStatusLogDTO();
                    statusLogDTO.setStatus(String.valueOf(statusLog.getValid()));
                    statusLogDTO.setStatusName(this.getValidName(statusLog.getValid()));
                    statusLogDTO.setLogDatetime(logDatetime);
                    statusLogList.add(statusLogDTO);
                });

                // 預設加入一筆有效
                CustomVcDataStatusLogDTO customVcDataStatusLogDTO = new CustomVcDataStatusLogDTO();
                customVcDataStatusLogDTO.setStatus(String.valueOf(VcItemDataValidType.ACTIVE.getCode()));
                customVcDataStatusLogDTO.setStatusName(VcItemDataValidType.ACTIVE.getValidName());
                customVcDataStatusLogDTO.setLogDatetime(vcCredential.getIssuanceDate());
                statusLogList.add(customVcDataStatusLogDTO);

                result.setStatusLogList(statusLogList);
            }

            return result;
        } catch (Exception ex) {
            log.error("VCCredentialQueryService-findVcItemDetail-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            throw new InternalErrorAlertException("get vc.credential content decode error: ", ex.getMessage(), "");
        }
    }

    private String stringMasker(String input) {
        if (input == null) {
            return "";
        }

        if (input.length() == 1) {
            return "*";
        }

        if (input.length() == 2) {
            return input.charAt(0) + "*";
        }

        // 取得第一個和最後一個字元
        char firstChar = input.charAt(0);
        char lastChar = input.charAt(input.length() - 1);

        // 建立中間的 * 部分，長度等於被取代的字元數
        int middleLength = input.length() - 2;
        String maskedMiddle = "*".repeat(middleLength);

        // 組合並返回結果
        return firstChar + maskedMiddle + lastChar;
    }

    public Boolean checkIsPrivilegedAccount(String userId) {
        List<RoleDTO> roles = customUserService.getRoles(userId);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }

    /**
     * 將 valid 轉為中文
     * 
     * @param valid
     * @return
     */
    private String getValidName(Integer valid) {
        return VcItemDataValidType.getByValue(valid).getValidName();
    }
}
