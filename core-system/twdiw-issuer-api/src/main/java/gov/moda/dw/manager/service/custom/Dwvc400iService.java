package gov.moda.dw.manager.service.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;

/**
 * 用於處理VC資料串接
 */
@Service
@Transactional
public class Dwvc400iService {

    @Value("${issuer.oidvci-uri}")
    private String oidVciUrl;

    private final RemoteApiService remoteApiService;

    private final CustomVCItemRepository customVCItemRepository;

    private final CustomVCItemFieldRepository customVCItemFieldRepository;

    private final ObjectMapper objectMapper;

    public Dwvc400iService(RemoteApiService remoteApiService, CustomVCItemRepository customVCItemRepository,
        CustomVCItemFieldRepository customVCItemFieldRepository, ObjectMapper objectMapper) {
        this.remoteApiService = remoteApiService;
        this.customVCItemRepository = customVCItemRepository;
        this.customVCItemFieldRepository = customVCItemFieldRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 用於取得VC資料
     *
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    public String getVcData(String id) throws JsonProcessingException {
        try {
            String url = oidVciUrl + "/api/issuer/" + id + "/.well-known/openid-credential-issuer";
            String apiResult = remoteApiService.getVCInfo(url);
            JsonNode jsonNode = objectMapper.readTree(apiResult);
            JsonNode credentialConfigSupportNode = jsonNode.get("credential_configurations_supported");
            Iterator<String> supportIterator = credentialConfigSupportNode.fieldNames();
            String businessId = "";
            String vcSerialNo = "";
            while (supportIterator.hasNext()) {
                String key = supportIterator.next();
                int i = key.indexOf("_");
                businessId = key.substring(0, i);
                vcSerialNo = key.substring(i + 1, key.length());

                JsonNode vcInfo = credentialConfigSupportNode.get(businessId + "_" + vcSerialNo);
                if (vcInfo == null) {
                    throw new BadRequestAlertException("not usable message", businessId,
                            "vcItemNo not found error for getVCInfo()");
                }
                JsonNode credentialSubjectNode = vcInfo.get("credential_definition").get("credentialSubject");
                VCItem vcItem = customVCItemRepository.findBySerialNoAndBusinessId(vcSerialNo, businessId);
                List<VCItemField> vcItemFields = customVCItemFieldRepository.findByVcItemIdOrderById(vcItem.getId());
                HashMap<String, VCItemField> vcItemFieldMap = new HashMap<>();
                //提升效能
                for(VCItemField vcItemField : vcItemFields){
                    vcItemFieldMap.put(vcItemField.getEname(), vcItemField);
                }
            
                List<String> fieldNames = new ArrayList<>();
                Iterator<String> subjectIterator = credentialSubjectNode.fieldNames();
                while (subjectIterator.hasNext()) {
                    String fieldName = subjectIterator.next();
                    fieldNames.add(fieldName);
                }
                 
                // 處理每個欄位
                for (String fieldName : fieldNames) {
                    JsonNode fieldNode = credentialSubjectNode.get(fieldName); 
                    //IsRequired 加入到 fieldNode 中
                    if (vcItemFieldMap.get(fieldName) != null) {
                        ((com.fasterxml.jackson.databind.node.ObjectNode) fieldNode).put("isRequired",
                                vcItemFieldMap.get(fieldName).getIsRequired());
                    } else {
                        ((com.fasterxml.jackson.databind.node.ObjectNode) fieldNode).put("isRequired", false);
                    }
                }
            }

            // 回傳修改後的完整 JSON
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
