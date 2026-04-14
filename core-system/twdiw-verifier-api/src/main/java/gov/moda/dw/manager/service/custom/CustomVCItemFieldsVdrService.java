package gov.moda.dw.manager.service.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.outside.vdr.category.SelectCategoryReq;
import gov.moda.dw.manager.domain.outside.vdr.category.SelectCategoryVCItemReq;
import gov.moda.dw.manager.domain.outside.vdr.vcitem.VCItemFieldVdr;
import gov.moda.dw.manager.domain.outside.vdr.vcitem.VCItemVdr;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;

@Service
@Transactional
public class CustomVCItemFieldsVdrService {

    private static final Logger log = LoggerFactory.getLogger(CustomVCItemFieldsVdrService.class);
    @Value("${remote-url.vdr-issuer-metadata}")
    private String vdrIssuerMetadata;

    private final RemoteApiService remoteApiService;

    private final CustomCategoryService customCategoryService;

    private final CustomVdrHelperService customVdrHelperService;

    private final CustomVPItemAuthHelperService customVPItemAuthHelperService;

    private final ObjectMapper objectMapper;

    public CustomVCItemFieldsVdrService(RemoteApiService remoteApiService, CustomCategoryService customCategoryService,
            CustomVdrHelperService customVdrHelperService, CustomVPItemAuthHelperService customVPItemAuthHelperService,
            ObjectMapper objectMapper) {
        this.remoteApiService = remoteApiService;
        this.customCategoryService = customCategoryService;
        this.customVdrHelperService = customVdrHelperService;
        this.customVPItemAuthHelperService = customVPItemAuthHelperService;
        this.objectMapper = objectMapper;
    }

    public String getVdrUrlFactory(SelectCategoryReq req) {
        return customVdrHelperService.getVdrUrlFactory(req, this);
    }

    /**
     * from 正式環境
     * 
     * @param taxId
     * @return
     */
    public String findVcItemsFromVdrInNormal(String taxId) {
        Map<String, String> allCategoryToDtoMap = customCategoryService.getAllCategoryToDtoMap();
        String metaUrl = allCategoryToDtoMap.get(taxId);
        if (StringUtils.isBlank(metaUrl)) {
            throw new BadRequestAlertException("ApiError", "taxId=" + taxId, "not found MetaUrl error");
        }

        return metaUrl + "/api/getVcData/" + taxId + "/.well-known/openid-credential-issuer";
    }

    /**
     * for 沙盒環境
     * 
     * @param taxId
     * @return
     */
    public String findVcItemsFromVdrInSandbox(String taxId) {
        return vdrIssuerMetadata.replace("{businessId}", taxId);
    }

    /**
     * 調整為字母優先 (字母優先： A a c 1 2 3 4 _)
     * 
     * @return
     */
    public List<VCItemVdr> findVcItemsFromVdrUrl(String url, String taxId) {
        List<VCItemVdr> result = new ArrayList<VCItemVdr>();
        try {
            String apiResult = remoteApiService.getVCInfo(url);
            log.debug("====apiResult====");
            JsonNode jsonNode = objectMapper.readTree(apiResult);
            JsonNode credentialConfigSupportNode = jsonNode.get("credential_configurations_supported");
            Iterator<String> supportIterator = credentialConfigSupportNode.fieldNames();
            while (supportIterator.hasNext()) {
                String key = supportIterator.next();
                int i = key.indexOf("_");
                String businessId = key.substring(0, i);
                String vcSerialNo = key.substring(i + 1, key.length());
                JsonNode vcInfo = credentialConfigSupportNode.get(key);
                int zhTwIndex = findNodeIndexForLocale(vcInfo.get("display"), "zh-TW");
                String cname = vcInfo.get("display").get(zhTwIndex).get("name").asText();
                var vcItem = new VCItemVdr();
                vcItem.setBusinessId(businessId);
                vcItem.setName(cname);
                vcItem.setSerialNo(vcSerialNo);
                result.add(vcItem);
            }

            // 過濾
            result = customVPItemAuthHelperService.queryVCFilterByAuth(taxId, result);

            if (!result.isEmpty()) {
                result.sort((o1, o2) -> {
                    String s1 = o1.getSerialNo();
                    String s2 = o2.getSerialNo();
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
            }

            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<VCItemFieldVdr> findFieldsByPageForOneVCItem(SelectCategoryVCItemReq req, Pageable pageable) {
        List<VCItemFieldVdr> fieldsForOneVCItem = findFieldsForOneVCItem(req.getTaxId(), req.getSerialNo(),
                getVdrUrlFactory(req));
        List<VCItemFieldVdr> pageContent = fieldsForOneVCItem.stream()
                .skip(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize()).toList();
        return new PageImpl<>(pageContent, pageable, fieldsForOneVCItem.size());
    }

    public List<VCItemFieldVdr> findFieldsForOneVCItem(String businessId, String serialNo, String url) {
        String vcItemNo = businessId + "_" + serialNo;
        JsonNode jsonNode = null;
        try {
            String apiResult = remoteApiService.getVCInfo(url);
            if (apiResult == null) {
                throw new BadRequestAlertException("ApiError", businessId,
                        "businessId not found error for getVCInfo()");
            }
            jsonNode = objectMapper.readTree(apiResult);
            JsonNode credentialConfigSupportNode = jsonNode.get("credential_configurations_supported");
            JsonNode vcInfo = credentialConfigSupportNode.get(vcItemNo);

            if (vcInfo == null) {
                throw new BadRequestAlertException("not usable message", vcItemNo,
                        "vcItemNo not found error for getVCInfo()");
            }

            JsonNode credentialSubjectNode = vcInfo.get("credential_definition").get("credentialSubject");

            // fields
            var fieldsList = new ArrayList<VCItemFieldVdr>();
            Iterator<String> subjectIterator = credentialSubjectNode.fieldNames();
            while (subjectIterator.hasNext()) {
                var vcItemFieldVdr = new VCItemFieldVdr();
                String fieldName = subjectIterator.next();
                JsonNode fieldNode = credentialSubjectNode.get(fieldName);
                int zhTwIndex = findNodeIndexForLocale(fieldNode.get("display"), "zh-TW");
                vcItemFieldVdr.setCname(fieldNode.get("display").get(zhTwIndex).get("name").asText());
                vcItemFieldVdr.setEname(fieldName);
                vcItemFieldVdr.setValueType(fieldNode.get("value_type").asText());
                vcItemFieldVdr.setMandatory(fieldNode.get("mandatory").asBoolean());
                vcItemFieldVdr.setIsRequired(fieldNode.get("isRequired").asBoolean());
                fieldsList.add(vcItemFieldVdr);
            }

            return fieldsList;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private int findNodeIndexForLocale(JsonNode jsonNode, String locale) {
        if (jsonNode.isArray()) {
            Iterator<JsonNode> iterator = jsonNode.iterator();
            int result = -1;
            int index = 0;
            while (iterator.hasNext()) {
                JsonNode next = iterator.next();
                if (locale.equals(next.get("locale").asText())) {
                    result = index;
                    break;
                }
                index++;
            }
            return result;
        } else {
            return -1;
        }
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

}
