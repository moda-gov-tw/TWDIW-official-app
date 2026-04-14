package gov.moda.dw.manager.service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.VCItemDataRepository;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.repository.custom.CustomRegularExpressionRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.custom.CustomUserService;
import gov.moda.dw.manager.service.dto.CreateVCItemDataField;
import gov.moda.dw.manager.service.dto.DwIssuerVC501iRes;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.StatusCodeSandboxWrapDTO;
import gov.moda.dw.manager.service.dto.VCApiErrorResDTO;
import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import gov.moda.dw.manager.service.dto.VcItemData305iResponseDTO;
import gov.moda.dw.manager.service.dto.custom.DwIssuerVC501iReq;
import gov.moda.dw.manager.service.dto.custom.RegularExpressionFieldInvalidDTO;
import gov.moda.dw.manager.service.mapper.VCItemDataMapper;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import gov.moda.dw.manager.type.StatusCodeSandbox;
import gov.moda.dw.manager.type.VCDataSourceType;
import gov.moda.dw.manager.util.RegexUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.InternalErrorAlertException;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.VCItemData}.
 */
@Service
@Transactional
public class VCItemDataService {

    private final CustomOrgRepository customOrgRepository;

    @Value("${metadata-schema.credential-issuer}")
    String credentialIssuerUri;

    @Value("${dwfront.dwfront-301i}")
    private String findIssuerURI;

    @Value("${issuer.security.authentication.jwt.private-key}")
    private String issuerJwtKey;

    @Value("${issuer.dwissuer-oid4vci-101i}")
    private String getDwIssuerOidVci101iUri;

    @Value("${issuer.dwissuer-vc-501i}")
    private String dwissuerVc501iUri;

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    private static final Logger log = LoggerFactory.getLogger(VCItemDataService.class);

    private final CustomVCItemRepository customVCItemRepository;

    private final VCItemDataRepository vCItemDataRepository;

    private final VCItemDataMapper vCItemDataMapper;

    private final VCItemMapper vcItemMapper;

    private final CustomVCItemFieldRepository customVCItemFieldRepository;

    private final CustomRegularExpressionRepository regularExpressionRepository;

    private final CustomUserRepository customUserRepository;

    private final CustomUserService customUserService;

    public VCItemDataService(
        VCItemDataRepository vCItemDataRepository,
        VCItemDataMapper vCItemDataMapper,
        CustomVCItemRepository customVCItemRepository,
        VCItemMapper vcItemMapper,
        CustomVCItemFieldRepository customVCItemFieldRepository,
        CustomRegularExpressionRepository regularExpressionRepository,
        CustomUserRepository customUserRepository,
        CustomOrgRepository customOrgRepository,
        CustomUserService customUserService
    ) {
        this.vCItemDataRepository = vCItemDataRepository;
        this.vCItemDataMapper = vCItemDataMapper;
        this.customVCItemRepository = customVCItemRepository;
        this.vcItemMapper = vcItemMapper;
        this.customVCItemFieldRepository = customVCItemFieldRepository;
        this.regularExpressionRepository = regularExpressionRepository;
        this.customUserRepository = customUserRepository;
        this.customOrgRepository = customOrgRepository;
        this.customUserService = customUserService;
    }



    public String getJwtForApi101iRequest(String userId, String vcBusinessId, String credentialConfigurationId, String nonceId, String txCode) {
        String token = "";

        try {
            String privateKeyPEM = new String(issuerJwtKey).replaceAll("\\s+", "");

            byte[] decodedKey = java.util.Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

            Algorithm algorithm = Algorithm.RSA256(privateKey);

            LocalDateTime dateTime = LocalDateTime.now().plusMinutes(10);
            Date expireTime = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

            token = JWT.create()
                .withClaim("sub", userId)
                .withClaim("aud", "moda_dw")
                .withClaim("credential_configuration_id", credentialConfigurationId) // credential_configuration_id
                .withClaim("iss", credentialIssuerUri + vcBusinessId)
                .withClaim("iat", new Timestamp(System.currentTimeMillis()).getTime())
                .withClaim("nonce", nonceId)
                .withClaim("tx_code", txCode)
                .withExpiresAt(new Timestamp(expireTime.getTime()))
                .sign(algorithm);
        } catch (Exception ex) {
            log.error("DwSandBoxVc301W-jwtForApi101iRequest-error :{}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
        }

        return token;
    }

    public DwIssuerVC501iRes callApi501i(String transactionId, DwIssuerVC501iReq reqContent) {
        String respCode = "";
        DwIssuerVC501iRes response;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri = dwissuerVc501iUri;
            response = restTemplate.postForObject(uri, reqContent, DwIssuerVC501iRes.class);
        } catch (Exception ex) {
            log.error("DwSandBoxVc301W-callApi501i-error  {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw new InternalErrorAlertException("vc data call 501i failed.", "nonce(transactionId):" + transactionId, "ExternalApiError");
        }

        return response;
    }

    public DwIssuerVC501iRes callApi501iForExternalCall(String transactionId, DwIssuerVC501iReq reqContent) {
        String respCode = "";
        DwIssuerVC501iRes response;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri = dwissuerVc501iUri;
            response = restTemplate.postForObject(uri, reqContent, DwIssuerVC501iRes.class);
        } catch (HttpServerErrorException ex) {
            log.error(
                "DwSandBoxVc301W-callApi501iForExternalCall-HttpServerErrorException  {}",
                ex.getMessage(),
                ExceptionUtils.getStackTrace(ex)
            );
            VCApiErrorResDTO vcApiErrorRes = ex.getResponseBodyAs(VCApiErrorResDTO.class);
            throw new InternalErrorAlertException(
                vcApiErrorRes.getCode() + ":" + vcApiErrorRes.getMessage(),
                vcApiErrorRes.getMessage(),
                vcApiErrorRes.getCode()
            );
        } catch (Exception ex) {
            log.error("DwSandBoxVc301W-callApi501iForExternalCall-error  {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw new InternalErrorAlertException("vc data call 501i failed.", "nonce(transactionId):" + transactionId, "ExternalApiError");
        }

        return response;
    }

    private Map<String, Object> decodeJwtFrom402iResult(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT token format.");
            }
            String payload = parts[1];

            // 解碼 Base64 URL 字串
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String payloadJson = new String(decodedBytes);

            // 使用 ObjectMapper 將 JSON 字串轉換為 Map
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(payloadJson, Map.class);
        } catch (Exception ex) {
            log.error("DwSandBoxVc301W-decodeJwtFrom402iResult-error {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            return new HashMap<>();
        }
    }

    /**
     * Update a vCItemData.
     *
     * @param vCItemDataDTO the entity to save.
     * @return the persisted entity.
     */
    public VCItemDataDTO update(VCItemDataDTO vCItemDataDTO) {
        log.debug("Request to update VCItemData : {}", vCItemDataDTO);
        VCItemData vCItemData = vCItemDataMapper.toEntity(vCItemDataDTO);
        vCItemData = vCItemDataRepository.save(vCItemData);
        return vCItemDataMapper.toDto(vCItemData);
    }

    /**
     * Partially update a vCItemData.
     *
     * @param vCItemDataDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VCItemDataDTO> partialUpdate(VCItemDataDTO vCItemDataDTO) {
        log.debug("Request to partially update VCItemData : {}", vCItemDataDTO);

        return vCItemDataRepository
            .findById(vCItemDataDTO.getId())
            .map(existingVCItemData -> {
                vCItemDataMapper.partialUpdate(existingVCItemData, vCItemDataDTO);

                return existingVCItemData;
            })
            .map(vCItemDataRepository::save)
            .map(vCItemDataMapper::toDto);
    }

    /**
     * Get one vCItemData by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VCItemDataDTO> findOne(Long id) {
        log.debug("Request to get VCItemData : {}", id);
        return vCItemDataRepository.findById(id).map(vCItemDataMapper::toDto);
    }

    /**
     * Delete the vCItemData by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete VCItemData : {}", id);
        vCItemDataRepository.deleteById(id);
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


    // 轉換VC資料格式為沙盒用的格式做儲存
    public String convertVCDataToSandBoxFormat(List<VCItemField> vcItemFieldList, LinkedHashMap<String, Object> vcData)
        throws JsonProcessingException {
        List<CreateVCItemDataField> result = new ArrayList<>();

        if (vcData.size() == 0) {
            return "[]";
        }

        vcData.forEach((key, value) -> {
            Optional<VCItemField> vcitemfield = vcItemFieldList.stream().filter(x -> x.getEname().equals(key)).findFirst();

            CreateVCItemDataField item = new CreateVCItemDataField();
            if (vcitemfield.isPresent()) {
                item.setType(vcitemfield.get().getType());
                item.setEname(vcitemfield.get().getEname());
                item.setCname(vcitemfield.get().getCname());
                item.setContent(value.toString());
            } else {
                item.setType("CUSTOM");
                item.setEname(key);
                item.setCname(key);
                item.setContent(value.toString());
            }
            result.add(item);
        });

        String jsonString;
        ObjectMapper objectMapper = new ObjectMapper();
        jsonString = objectMapper.writeValueAsString(result);

        return jsonString;
    }

    /**
     * 檢查，vc模版欄位與vc資料欄位數量一致
     */
    public VcItemData305iResponseDTO checkVCTemplateFieldAndVCDataFieldData(
        List<VCItemField> vcTemplateFieldList,
        LinkedHashMap vcDataFieldAndValue,
        String methodName,
        String credentialType,
        VCDataSourceType vcDataSourceType
    ) {
        VcItemData305iResponseDTO result = null;

        if (vcTemplateFieldList.size() != vcDataFieldAndValue.size()) {
            if (vcDataSourceType == VCDataSourceType._901) {
                result = new VcItemData305iResponseDTO();
                result.setSandbox_code(StatusCodeSandbox.Data_Field_Count_Mismatch_With_VC_Template.getCode());
                result.setSandbox_message(StatusCodeSandbox.Data_Field_Count_Mismatch_With_VC_Template.getMsg());
                result.setSandbox_type(StatusCodeSandbox.Data_Field_Count_Mismatch_With_VC_Template.getErrorKey());
            } else {
                throw new BadRequestAlertException(
                    StatusCodeSandbox.Data_Field_Count_Mismatch_With_VC_Template.getMsg(),
                    methodName + " Credential Type:" + credentialType + " " + vcDataFieldAndValue,
                    StatusCodeSandbox.Data_Field_Count_Mismatch_With_VC_Template.getErrorKey()
                );
            }
        }

        return result;
    }

    public List<RegularExpressionFieldInvalidDTO> checkVCTemplateFieldAndVCDataFieldDataRegularExpression(
        List<VCItemField> vcTemplateFieldList,
        LinkedHashMap vcDataFieldAndValue,
        String methodName,
        String credentialType,
        VCDataSourceType vcDataSourceType,
        StatusCodeSandboxWrapDTO returnErrorStatus
    ) throws JsonProcessingException {
        // 正規表達式檢核
        List<RegularExpression> regularExpressionList = regularExpressionRepository.findByType("require");

        List<RegularExpressionFieldInvalidDTO> regularExpressionFieldInvalidDTOS = new ArrayList<>();
        List<Pattern> requireRegularPattern = new ArrayList<>();
        for (RegularExpression regularExpression : regularExpressionList) {
            requireRegularPattern.add(RegexUtils.matchWithTimeout(regularExpression.getRegularExpression()));
        }

        List<String> vcDataKeyList = new ArrayList<>(vcDataFieldAndValue.keySet());

        int fieldIndex = 0;
        for (VCItemField vcItemField : vcTemplateFieldList) {
            // 確認vc模版中的欄位在vc資料中存在
            if (!vcDataFieldAndValue.containsKey(vcItemField.getEname())) {
                if (vcDataSourceType == VCDataSourceType._901) {
                    returnErrorStatus.setStatusCode(StatusCodeSandbox.VC_Template_Field_Not_Found_In_Data);
                    return null;
                } else {
                    throw new BadRequestAlertException(
                        StatusCodeSandbox.VC_Template_Field_Not_Found_In_Data.getMsg(),
                        methodName + "Credential Type:" + credentialType,
                        StatusCodeSandbox.VC_Template_Field_Not_Found_In_Data.getErrorKey()
                    );
                }
            }

            // 確認vc資料欄位順序與模版設定的欄位一致
            if (!vcItemField.getEname().equals(vcDataKeyList.get(fieldIndex))) {
                if (vcDataSourceType == VCDataSourceType._901) {
                    returnErrorStatus.setStatusCode(StatusCodeSandbox.Data_Field_Order_Mismatch_With_VC_Template);
                    return null;
                } else {
                    throw new BadRequestAlertException(
                        StatusCodeSandbox.Data_Field_Order_Mismatch_With_VC_Template.getMsg(),
                        methodName + " Credential Type:" + credentialType,
                        StatusCodeSandbox.Data_Field_Order_Mismatch_With_VC_Template.getErrorKey()
                    );
                }
            }

            String fieldContent = vcDataFieldAndValue.get(vcItemField.getEname()).toString();

            // 針對單一欄位上設定的正規表達式做檢核
            RegularExpressionFieldInvalidDTO fieldCheckResult = new RegularExpressionFieldInvalidDTO();
            fieldCheckResult.setEname(vcItemField.getEname());
            fieldCheckResult.setCname(vcItemField.getCname());
            fieldCheckResult.setValue(fieldContent);
            fieldCheckResult.setInvalid(new ArrayList<>());

            // require 類型正則對所有資料檢核
            for (int i = 0; i < requireRegularPattern.size(); i++) {
                String ruleType = regularExpressionList.get(i).getRuleType();
                boolean regularExpressionMatchResult = RegexUtils
                        .matchWithTimeout(requireRegularPattern.get(i), fieldContent).matches();

                // allow 正向表, 白名單, 有 match，才給過
                // deny 負向表, 黑名稱, 不 match，才給過
                if (
                    (ruleType.equals("allow") && regularExpressionMatchResult == false) ||
                        (ruleType.equals("deny") && regularExpressionMatchResult == true)
                ) {
                    // 違反正則設定
                    fieldCheckResult.getInvalid().add(regularExpressionList.get(i).getErrorMsg());
                }
            }

            // 附加在欄位上的正則的檢核
            Pattern pattern = RegexUtils.matchWithTimeout(vcItemField.getRegularExpression().getRegularExpression());
            boolean regularExpressionMatchResult = RegexUtils.isMatchWithTimeout(pattern, fieldContent);
            String type = vcItemField.getRegularExpression().getRuleType();
            if (
                (type.equals("allow") && regularExpressionMatchResult == false) ||
                    (type.equals("deny") && regularExpressionMatchResult == true)
            ) {
                // 違反正則設定
                fieldCheckResult.getInvalid().add(vcItemField.getRegularExpression().getErrorMsg());
            }

            if (!fieldCheckResult.getInvalid().isEmpty()) {
                regularExpressionFieldInvalidDTOS.add(fieldCheckResult);
            }

            fieldIndex++;
        }

        return regularExpressionFieldInvalidDTOS;
    }

    private Org getOrgInfo() {

        log.info("登入者的組織是:{}", SecurityUtils.getJwtUserObject().get(0).getOrgId());
        Optional<Org> org = customOrgRepository.findByOrgId(SecurityUtils.getJwtUserObject().get(0).getOrgId());
        return org.get();
    }

    public Boolean checkIsPrivilegedAccount(String userId) {
        List<RoleDTO> roles = customUserService.getRoles(userId);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }

}
