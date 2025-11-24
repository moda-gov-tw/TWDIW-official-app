package gov.moda.dw.issuer.oidvci.service;

import java.util.List;

import gov.moda.dw.issuer.oidvci.config.ErrorCodeConfiguration;
import gov.moda.dw.issuer.oidvci.repository.MetaDataRepository;
import net.minidev.json.JSONStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.issuer.oidvci.domain.CredentialIssuerConfigEntity;
import gov.moda.dw.issuer.oidvci.repository.CICRepository;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Service
@Transactional
public class CredentialIssuerConfigService {
	private static final Logger log = LoggerFactory.getLogger(CredentialIssuerConfigService.class);

	private final CICRepository cic_repository;

	public CredentialIssuerConfigService(CICRepository cic_repository) {
        this.cic_repository = cic_repository;
    }

	public List<CredentialIssuerConfigEntity> getCredentialIssuerConfig(String vc_id) {
		return cic_repository.findByVcID(vc_id);
	}

    public JSONArray getProofSigningAlgValuesSupported(String credential_issuer_meta, String credential_configuration_id) throws ParseException {

        // 驗證參數
        if (credential_issuer_meta == null || credential_issuer_meta.isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage("credential_issuer_meta cannot be null or empty"));
        }
        if (credential_configuration_id == null || credential_configuration_id.isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage("credential_configuration_id cannot be null or empty"));
        }

        // 解析 JSON 字符串
        JSONObject credential_issuer_meta_json;
        try {
            credential_issuer_meta_json = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(credential_issuer_meta);
        } catch (ParseException e) {
            throw new IllegalArgumentException(buildErrorMessage("credential_issuer_meta is not a valid JSON string"));
        }

        // 檢查 credential_configurations_supported 是否存在
        if (!credential_issuer_meta_json.containsKey("credential_configurations_supported")) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("resp_message", "Missing 'credential_configurations_supported'");
            errorResponse.put("resp_code", 11097);
            errorResponse.put("error", "invalid_proof");

            JSONArray errorArray = new JSONArray();
            errorArray.add(errorResponse);
            return errorArray;
        }
        JSONObject credential_configurations_supported_json =
            (JSONObject) credential_issuer_meta_json.get("credential_configurations_supported");

        // 檢查指定的 credential_configuration_id 是否存在
        if (!credential_configurations_supported_json.containsKey(credential_configuration_id)) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("resp_message", "credential_configuration_id '" + credential_configuration_id + "' not found in 'credential_configurations_supported'");
            errorResponse.put("resp_code", 11098);
            errorResponse.put("error", "invalid_proof");

            JSONArray errorArray = new JSONArray();
            errorArray.add(errorResponse);
            return errorArray;
            //throw new IllegalArgumentException(buildErrorMessage());
        }
        JSONObject vc_type_json = (JSONObject) credential_configurations_supported_json.get(credential_configuration_id);

        // 檢查 proof_types_supported 是否存在
        if (!vc_type_json.containsKey("proof_types_supported")) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("resp_message", "Missing 'proof_types_supported' in credential configuration");
            errorResponse.put("resp_code", 11099);
            errorResponse.put("error", "invalid_proof");

            JSONArray errorArray = new JSONArray();
            errorArray.add(errorResponse);
            return errorArray;
        }
        JSONObject proof_types_supported_json = (JSONObject) vc_type_json.get("proof_types_supported");

        // 檢查 jwt 是否存在
        if (!proof_types_supported_json.containsKey("jwt")) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("resp_message", "Missing 'jwt' in proof types supported");
            errorResponse.put("resp_code", 11100);
            errorResponse.put("error", "invalid_proof");

            JSONArray errorArray = new JSONArray();
            errorArray.add(errorResponse);
            return errorArray;
        }
        JSONObject jwt_json = (JSONObject) proof_types_supported_json.get("jwt");

        // 檢查 proof_signing_alg_values_supported 是否存在
        if (!jwt_json.containsKey("proof_signing_alg_values_supported")) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("resp_message", "Missing 'proof_signing_alg_values_supported' in JWT configuration");
            errorResponse.put("resp_code", 11101);
            errorResponse.put("error", "invalid_proof");

            JSONArray errorArray = new JSONArray();
            errorArray.add(errorResponse);
            return errorArray;
        }
        return (JSONArray) jwt_json.get("proof_signing_alg_values_supported");
    }

    private String buildErrorMessage(String errorMessage) {
        JSONObject errorJson = new JSONObject();
        errorJson.put("error", errorMessage);
        return errorJson.toJSONString();
    }

    public String getCredentialIssuer(String credential_offer) throws ParseException {
		JSONObject credential_offer_json = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(credential_offer);
		return credential_offer_json.getAsString("credential_issuer");
	}

    public CredentialIssuerConfigEntity updateMetaData(String org_id, String metadata) {
        // 使用 ID 查找要更新的 User
        CredentialIssuerConfigEntity credentialIssuerConfigEntity = cic_repository.findById(org_id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 更新字段
        credentialIssuerConfigEntity.setCredentialIssuerMeta(metadata);

        // 保存更新後的 User
        return cic_repository.save(credentialIssuerConfigEntity);
    }
}
