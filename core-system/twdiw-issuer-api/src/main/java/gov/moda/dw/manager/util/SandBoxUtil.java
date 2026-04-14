package gov.moda.dw.manager.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemFieldDTO;
import gov.moda.dw.manager.type.IALType;
import gov.moda.dw.manager.type.StatusCodeSandbox;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ReflectionUtils;
import tech.jhipster.service.filter.StringFilter;

public class SandBoxUtil {

    public static Long getUserId() {
        return 0L;
    }

    public static String getSchemaVersion() {
        return "V1";
    }

    public static String getSchemaSchema() {
        return "https://json-schema.org/draft/2020-12/schema#";
    }

    //    public static String getSchemaId() {
    //        return "http://frontend.service.modadw.dev.webe.hinet.services/api/schema/56801904/VirtualCardCredential/V1/d285201c-56f2-48b8-82a5-40369d595bdb";
    //    }

    //    public static String getMetadataCredentialIssuer() {
    //        return credentialIssuerUri;
    //    }
    //
    //    public static String getMetadataCredentialEndpoint() {
    //        return credentialEndpointUri;
    //    }

    public static String setSchemaForApi104i(String schema, String vcSerialNo) {
        // 轉換為字串並返回
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode schemaNode = (ObjectNode) objectMapper.readTree(schema);

            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("businessId", SecurityUtils.getJwtUserObject().get(0).getOrgId());
            rootNode.put("tag", vcSerialNo);
            rootNode.put("version", SandBoxUtil.getSchemaVersion());
            rootNode.put("schema", schemaNode);

            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createSchema(VCItemDTO vcItemDTO) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 建立 credentialSubjectPropertiesObject
        ObjectNode credentialSubjectPropertiesObject = objectMapper.createObjectNode();

        //        ObjectNode fixFieldObject = objectMapper.createObjectNode();
        //        fixFieldObject.put("title", "id");
        //        fixFieldObject.put("type", "string");
        //        fixFieldObject.put("format", "uri");
        //        credentialSubjectPropertiesObject.set("id", fixFieldObject);

        // 固定要塞的
        ObjectNode fixIdFields = objectMapper.createObjectNode();
        fixIdFields.put("title", "id");
        fixIdFields.put("type", "string");
        fixIdFields.put("format", "uri");
        credentialSubjectPropertiesObject.set("id", fixIdFields);

        for (VCItemFieldDTO vcItemField : vcItemDTO.getVcItemFieldDTOList()) {
            // 欄位名稱(英)，只能輸入英文數字和_(不可輸入id)
            if (!vcItemField.getEname().matches("^(?!id$)[a-zA-Z0-9_]+$")) {
                throw new RuntimeException("請求失敗，請確認輸入的欄位名稱(英)，只能輸入英文數字和_(不可輸入id)，若有疑問，請洽系統管理員。");
            }
            ObjectNode fieldObject = objectMapper.createObjectNode();
            fieldObject.put("title", vcItemField.getEname());
            fieldObject.put("type", "string");
            credentialSubjectPropertiesObject.set(vcItemField.getEname(), fieldObject);
        }

        // 建立 credentialSubject
        ObjectNode credentialSubject = objectMapper.createObjectNode();
        credentialSubject.put("title", "credentialSubject");
        credentialSubject.put("type", "object");
        credentialSubject.set("properties", credentialSubjectPropertiesObject);
        credentialSubject.put("additionalProperties", false);
        ArrayNode requiredArray = objectMapper.createArrayNode();
        requiredArray.add("id");
        credentialSubject.put("required", requiredArray);

        // 建立 properties
        ObjectNode properties = objectMapper.createObjectNode();
        properties.set("credentialSubject", credentialSubject);

        // 建立 schemaObject
        ObjectNode schemaObject = objectMapper.createObjectNode();
        schemaObject.put("$schema", "https://json-schema.org/draft/2020-12/schema#");
        schemaObject.put(
            "description",
            SecurityUtils.getJwtUserObject().get(0).getOrgId() + "_" + vcItemDTO.getSerialNo() + " using JsonSchema"
        );
        schemaObject.put("type", "object");
        schemaObject.set("properties", properties);

        // 建立 rootJsonObject
        ObjectNode rootJsonObject = objectMapper.createObjectNode();
        rootJsonObject.put("$schema", SandBoxUtil.getSchemaSchema());
        rootJsonObject.put("title", SecurityUtils.getJwtUserObject().get(0).getOrgId() + "_" + vcItemDTO.getSerialNo());
        rootJsonObject.put("type", "object");
        rootJsonObject.set("schema", schemaObject);

        // 轉換為字串並返回
        try {
            return objectMapper.writeValueAsString(rootJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //    public static String createSchema(VCItemDTO vcItemDTO) {
    //        ObjectMapper objectMapper = new ObjectMapper();
    //
    //        // 建立 credentialSubjectPropertiesObject
    //        ObjectNode credentialSubjectPropertiesObject = objectMapper.createObjectNode();
    //        for (VCItemFieldDTO vcItemField : vcItemDTO.getVcItemFieldDTOList()) {
    //            ObjectNode fieldObject = objectMapper.createObjectNode();
    //            fieldObject.put("title", vcItemField.getEname());
    //            fieldObject.put("type", "string");
    //            credentialSubjectPropertiesObject.set(vcItemField.getEname(), fieldObject);
    //        }
    //
    //        // 建立 credentialSubject
    //        ObjectNode credentialSubject = objectMapper.createObjectNode();
    //        credentialSubject.put("title", "credentialSubject");
    //        credentialSubject.put("type", "object");
    //        credentialSubject.set("properties", credentialSubjectPropertiesObject);
    //
    //        // 建立 properties
    //        ObjectNode properties = objectMapper.createObjectNode();
    //        properties.set("credentialSubject", credentialSubject);
    //
    //        // 建立 schemaObject
    //        ObjectNode schemaObject = objectMapper.createObjectNode();
    //        schemaObject.put("$schema", "https://json-schema.org/draft/2020-12/schema#");
    //        schemaObject.put("description", "VirtualCardCredential using JsonSchema");
    //        schemaObject.put("type", "object");
    //        schemaObject.set("properties", properties);
    //
    //        // 建立 rootJsonObject
    //        ObjectNode rootJsonObject = objectMapper.createObjectNode();
    //        rootJsonObject.put("businessId", SandBoxUtil.getBusinessId());
    //        rootJsonObject.put("tag", vcItemDTO.getSerialNo());
    //        rootJsonObject.put("version", "V1");
    //        rootJsonObject.set("schema", schemaObject);
    //
    //        // 轉換為字串並返回
    //        try {
    //            return objectMapper.writeValueAsString(rootJsonObject);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return null;
    //        }
    //    }

    public static String createMetadata(VCItemDTO vcItemDTO, String credentialIssuer, String credentialEndPoint, String vcCardCoverUrl) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 建立 credentialSubject
        ObjectNode credentialSubject = objectMapper.createObjectNode();
        for (VCItemFieldDTO vcItemField : vcItemDTO.getVcItemFieldDTOList()) {
            ObjectNode fieldObject = objectMapper.createObjectNode();
            fieldObject.put("mandatory", true);
            fieldObject.put("value_type", "string");

            // 建立 display 陣列
            ArrayNode displayArray = objectMapper.createArrayNode();
            ObjectNode displayObject = objectMapper.createObjectNode();
            displayObject.put("name", vcItemField.getCname());
            displayObject.put("locale", "zh-TW");
            displayArray.add(displayObject);

            fieldObject.set("display", displayArray);
            credentialSubject.set(vcItemField.getEname(), fieldObject);
        }
        
        // 建立 credentialDefinition
        ObjectNode credentialDefinition = objectMapper.createObjectNode();
        ArrayNode credentialDefinitionTypeArray = objectMapper.createArrayNode();
        credentialDefinitionTypeArray.add("VerifiableCredential");
        credentialDefinitionTypeArray.add(SecurityUtils.getJwtUserObject().get(0).getOrgId() + "_" + vcItemDTO.getSerialNo());

        credentialDefinition.set("type", credentialDefinitionTypeArray);
        credentialDefinition.set("credentialSubject", credentialSubject);

        // 建立 virtualCardCredentialDisplayArray
        ArrayNode virtualCardCredentialDisplayArray = objectMapper.createArrayNode();
        ObjectNode virtualCardCredentialDisplay = objectMapper.createObjectNode();
        virtualCardCredentialDisplay.put("name", vcItemDTO.getName());
        virtualCardCredentialDisplay.put("locale", "zh-TW");
        
        ObjectNode descriptionData = objectMapper.createObjectNode();
        descriptionData.put("ial", IALType.getNameByCode(vcItemDTO.getIal())); 
        
        String escapeDescriptionData = "";
        
        // 轉換為字串
		try {
			escapeDescriptionData = objectMapper.writeValueAsString(descriptionData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        
        virtualCardCredentialDisplay.put("description", escapeDescriptionData);
        ObjectNode backgroundImage = objectMapper.createObjectNode();
        backgroundImage.put("uri", vcCardCoverUrl);
        virtualCardCredentialDisplay.put("background_image", backgroundImage);
        virtualCardCredentialDisplayArray.add(virtualCardCredentialDisplay);

        // 建立 proofTypesSupported
        ObjectNode proofTypesSupported = objectMapper.createObjectNode();
        ObjectNode proofTypesSupportedJwt = objectMapper.createObjectNode();
        ArrayNode proofTypesSupportedJwtProofSigningArray = objectMapper.createArrayNode();
        proofTypesSupportedJwtProofSigningArray.add("ES256");
        proofTypesSupportedJwt.set("proof_signing_alg_values_supported", proofTypesSupportedJwtProofSigningArray);
        proofTypesSupported.set("jwt", proofTypesSupportedJwt);

        // 建立 virtualCardCredential
        ObjectNode virtualCardCredential = objectMapper.createObjectNode();
        virtualCardCredential.put("format", "jwt_vc_json");
        virtualCardCredential.put("scope", SecurityUtils.getJwtUserObject().get(0).getOrgId() + "_" + vcItemDTO.getSerialNo());

        // 建立 cryptographicBindingMethodsSupported 陣列
        ArrayNode cryptographicBindingMethodsSupported = objectMapper.createArrayNode();
        cryptographicBindingMethodsSupported.add("jwk");

        // 建立 credentialSigningAlgValuesSupported 陣列
        ArrayNode credentialSigningAlgValuesSupported = objectMapper.createArrayNode();
        credentialSigningAlgValuesSupported.add("ES256");

        // 將屬性添加到 virtualCardCredential
        virtualCardCredential.set("cryptographic_binding_methods_supported", cryptographicBindingMethodsSupported);
        virtualCardCredential.set("credential_signing_alg_values_supported", credentialSigningAlgValuesSupported);
        virtualCardCredential.set("credential_definition", credentialDefinition);
        virtualCardCredential.set("proof_types_supported", proofTypesSupported);
        virtualCardCredential.set("display", virtualCardCredentialDisplayArray);

        // 建立 credentialConfigurationsSupported
        ObjectNode credentialConfigurationsSupported = objectMapper.createObjectNode();
        credentialConfigurationsSupported.set(
            SecurityUtils.getJwtUserObject().get(0).getOrgId() + "_" + vcItemDTO.getSerialNo(),
            virtualCardCredential
        );

        // 建立 metadataObject
        ObjectNode metadataObject = objectMapper.createObjectNode();
        metadataObject.put("credential_issuer", credentialIssuer + SecurityUtils.getJwtUserObject().get(0).getOrgId());
        metadataObject.put("credential_endpoint", credentialEndPoint + SecurityUtils.getJwtUserObject().get(0).getOrgId() + "/credential");
        metadataObject.set("credential_configurations_supported", credentialConfigurationsSupported);

        // 轉換為字串並返回
        try {
            return objectMapper.writeValueAsString(metadataObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String create502iSchemaFrom104iResponse(String respSchema, String unitTypeExpire, int lengthExpire, String metadata)
        throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode credentialSubject = (ObjectNode) objectMapper.readTree(respSchema);

        String schemaId = credentialSubject.get("$id").asText();
        String vcSchemaSchema = credentialSubject.get("$schema").asText();
        //        String vcSchemaId = credentialSubject.get("id").asText();
        String vcSchemaTitle = credentialSubject.get("title").asText();
        String vcSchemaDescription = credentialSubject.get("schema").get("description").asText();
        String vcSchemaType = credentialSubject.get("type").asText();
        ObjectNode vcSchemaProperties = (ObjectNode) credentialSubject.get("schema").get("properties");

        ObjectNode vcSchema = objectMapper.createObjectNode();
        vcSchema.put("$schema", vcSchemaSchema);
        vcSchema.put("$id", schemaId);
        vcSchema.put("title", vcSchemaTitle);
        vcSchema.put("description", vcSchemaDescription);
        vcSchema.put("type", vcSchemaType);
        vcSchema.put("properties", vcSchemaProperties);

        ObjectNode root = objectMapper.createObjectNode();
        root.put("schemaId", schemaId);
        root.put("effectiveTimeUnit", unitTypeExpire);
        root.put("effectiveTimeValue", lengthExpire);
        root.put("vcSchema", vcSchema);

        ObjectNode metadataNode = (ObjectNode) objectMapper.readTree(metadata);
        root.put("metadata", metadataNode);

        // 轉換為字串並返回
        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Claim> getClaims(String jwt) {
        // 解析 JWT，不進行簽章驗證
        DecodedJWT decodedJWT = JWT.decode(jwt);
        // 獲取 payload 的聲明
        Map<String, Claim> claimsMap = decodedJWT.getClaims();
        // 將 Map 轉換為 List<Claim>
        return claimsMap;
    }

    /**
     * 檢查 credential type
     * @param credentialType
     * @return
     */
    public static String[] splitCredentialType(String credentialType, String methodName) {
        if (credentialType == null || credentialType.isEmpty() || credentialType.indexOf("_") <= 0) {
            throw new BadRequestAlertException(
                StatusCodeSandbox.Invalid_Credential_Type.getMsg(),
                methodName + " credential type:" + credentialType,
                StatusCodeSandbox.Invalid_Credential_Type.getErrorKey()
            );
        }

        String[] parts = credentialType.split("_", 2);
        String businessId = parts[0]; // 第一段
        String serialNo = parts[1];

        if (businessId == null || serialNo == null) {
            throw new BadRequestAlertException(
                StatusCodeSandbox.Invalid_Credential_Type.getMsg(),
                methodName + " credential type:" + credentialType,
                StatusCodeSandbox.Invalid_Credential_Type.getErrorKey()
            );
        }

        return parts;
    }

    /**
     * 將字串遮罩，只保留最後 2 個字，前面補 6 個星號。
     * 如果輸入長度小於 2，則直接在前面補上 6 個星號。
     */
    public static String maskVCCid(String input) {
        if (input == null || input.length() < 2) {
            return "******" + input; // 若長度不足 2，直接補 6 個星號
        }
        return "******" + input.substring(input.length() - 2); // 保留最後 2 個字
    }
}
