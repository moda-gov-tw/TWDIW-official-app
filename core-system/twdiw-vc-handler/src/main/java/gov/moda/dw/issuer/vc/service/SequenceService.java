package gov.moda.dw.issuer.vc.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import gov.moda.dw.issuer.vc.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.issuer.vc.domain.CredentialIssuerConfigEntity;
import gov.moda.dw.issuer.vc.domain.CredentialPolicyEntity;
import gov.moda.dw.issuer.vc.repository.CredentialIssuerConfigRepository;
import gov.moda.dw.issuer.vc.repository.CredentialPolicyRepository;
import gov.moda.dw.issuer.vc.util.DateUtils;
import gov.moda.dw.issuer.vc.util.DateUtils.TimeUnit;
import gov.moda.dw.issuer.vc.util.JsonUtils;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.CredentialIssuerMetadata;
import gov.moda.dw.issuer.vc.vo.PreloadSetting;
import gov.moda.dw.issuer.vc.vo.VcException;
import gov.moda.dw.issuer.vc.vo.CredentialIssuerMetadata.CredentialConfigurationSupported;
import gov.moda.dw.issuer.vc.vo.CredentialIssuerMetadata.CredentialDefinition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.fasterxml.jackson.core.*;


@Service
public class SequenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SequenceService.class);

	private final PreloadSetting preloadSetting;
	private final CredentialPolicyRepository credentialPolicyRepository;
	private final CredentialIssuerConfigRepository credentialIssuerConfigRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	//private Set<String> whiteList = new HashSet<>();

	public SequenceService(
			PreloadSetting preloadSetting,
			CredentialPolicyRepository credentialPolicyRepository,
			CredentialIssuerConfigRepository credentialIssuerConfigRepository)
	{
		this.preloadSetting = preloadSetting;
		this.credentialPolicyRepository = credentialPolicyRepository;
		this.credentialIssuerConfigRepository = credentialIssuerConfigRepository;
	}

	@PersistenceContext
    private EntityManager entityManager;

	@Transactional
	public Tuple.Pair<String, HttpStatus> createSeqAndPolicyAndUpdateCredentialIssuerConfig(String credentialType,
			int startValue, int incrementValue, SequenceRequestDTO sequenceRequest, String sequenceRequestStr) {

		String result;
        HttpStatus httpStatus;
		try {
			// step 1: validate request
			if(sequenceRequest == null || !sequenceRequest.validate()) {
				throw new VcException(VcException.ERR_SEQ_INVALID_SEQUENCE_SETTING_REQUEST, "invalid sequence setting request");
			}

			// step 1.1: 檢查有效年限input值是否合理
			if(!isValidTimeUnit(sequenceRequest.getEffectiveTimeUnit())) {
				throw new VcException(VcException.ERR_SEQ_INVALID_SEQUENCE_SETTING_REQUEST, "invalid time unit");
			}

			if(!checkValidityPeriod(sequenceRequest.getEffectiveTimeUnit(), sequenceRequest.getEffectiveTimeValue())) {
				throw new VcException(VcException.ERR_SEQ_INVALID_SEQUENCE_SETTING_REQUEST, "invalid validity period");
			}

			// step 2: 檢查序列名稱、credentialType是否合規
			// step 2.1: 將seqName用UUID命名
			String seqName = "seq_" + UUID.randomUUID().toString();
//			String seqName = "seq_" + credentialType.toLowerCase();
			if (!seqName.matches("^[a-zA-Z_][a-zA-Z0-9_-]*$")) {
			    throw new VcException(VcException.ERR_DB_INVALID_SEQUENCE_NAME, "Invalid sequence name");
			}
			// credentialType中不包含"_"，報錯
			if(credentialType.split("_").length == 1) {
				throw new VcException(VcException.ERR_SEQ_INVALID_CREDENTIAL_TYPE, "invalid credentialType");
			}

			// 解析credentialType中的businessId
			String businessld = credentialType.split("_")[0];

			// step 3: 從sequenceRequest中取得 metadata
			// 檢查metadata是否有重複的key

			if (checkDuplicatedKeyInMetadata(sequenceRequestStr)) {
				throw new VcException(VcException.ERR_SEQ_KEY_DUPLICATED_IN_ISSUER_METADATA,
						"key duplicated in issuer metadata");
			}

			String metadata = JsonUtils.mapToJs(sequenceRequest.getMetadata());
			if(metadata == null || metadata.isBlank()) {
				throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "issuer's metadata in request is null or blank");
			}
			LOGGER.info("[createSeqAndPolicyAndUpdateCredentialIssuerConfig]:: metadata: {}", metadata);

			// step 3.1: 檢查metadata的證件資料欄位是否包含id，若包含id欄位，則報錯
			if(isCredentialSubjectInMetadataContainsId(credentialType, metadata)) {
				throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA_DATA_FIELD, "invalid CredentialSubject data field('id' is not allowed)");
			}

//			// 查詢org表格中是否存在發證機關(businessId)的紀錄
//			Optional<OrgEntity> orgEntity = orgRepository.findByOrgId(businessId);
//			if(orgEntity.isEmpty()) {
//				throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get metadata from org");
//			}
//			else {
//				String metadata = orgEntity.get().getMetadata();
//				if(metadata == null || metadata.isBlank()) {
//					throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "issuer's metadata in DB(org) is null or blank");
//				}
//			}

			// step 4: 創建credential_issuer_config中的發證機關紀錄 or 更新發證機關紀錄中的credential_offer欄位
			// 查詢是否存在發證機關(businessId)的credential_issuer_config紀錄
			Optional<CredentialIssuerConfigEntity> credentialIssuerConfigEntity = credentialIssuerConfigRepository.findByVcID(
					businessld);

			// 如果不存在該發證機關(businessId)的credential_issuer_config紀錄，新增一筆紀錄
			if(credentialIssuerConfigEntity.isEmpty()) {
				// 取得template紀錄
				Optional<CredentialIssuerConfigEntity> templateCredentialIssuerConfigEntity = credentialIssuerConfigRepository.findByVcID("template");
				if(templateCredentialIssuerConfigEntity.isEmpty()) {
					throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get template CredentialIssuerConfig from DB");
				}

				// 取得credential_issuer_identifier的值，結尾加上businessId
				String credentialIssuerIdentifier = templateCredentialIssuerConfigEntity.get().getCredentialIssuerIdentifier();
				credentialIssuerIdentifier = credentialIssuerAppendBusinessId(credentialIssuerIdentifier, businessld);

				CredentialIssuerConfigEntity newCredentialIssuerConfigEntity = new CredentialIssuerConfigEntity();
				// 組credential_offer，從template紀錄中取得credential_offer，並新增credential_configuration_ids的值
				Map<String, Object> mapCredentialOffer = JsonUtils.jsToMap(templateCredentialIssuerConfigEntity.get().getCredentialOffer());

				// 找出credential_configuration_ids，新增值
				List<String> credentialConfigurationIds = (List<String>) mapCredentialOffer.get("credential_configuration_ids");
				credentialConfigurationIds.add(credentialType);

				// 將credential_issuer欄位替換為加上businessId後的值
				mapCredentialOffer.put("credential_issuer", credentialIssuerIdentifier);


				// store new CredentialIssuerConfig record
				newCredentialIssuerConfigEntity.setVcID(businessld)
												.setCredentialOffer(JsonUtils.mapToJs(mapCredentialOffer))
												.setCredentialIssuerMeta(metadata)
												.setCredentialIssuerIdentifier(credentialIssuerIdentifier)
												.setCredentialOfferUriPath(templateCredentialIssuerConfigEntity.get().getCredentialOfferUriPath())
												.setDb5ecret(templateCredentialIssuerConfigEntity.get().getDb5ecret())
												.setDbIv(templateCredentialIssuerConfigEntity.get().getDbIv())
												.setAppUrlScheme(templateCredentialIssuerConfigEntity.get().getAppUrlScheme())
												.setCredentialUrl(templateCredentialIssuerConfigEntity.get().getCredentialUrl());
				newCredentialIssuerConfigEntity = credentialIssuerConfigRepository.save(newCredentialIssuerConfigEntity);

				if(!businessld.equals(newCredentialIssuerConfigEntity.getVcID())) {
					throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store credential issuer config data");
				}
			}else {// 存在credential_issuer_config紀錄，更新credential_offer欄位
				Map<String, Object> mapCredentialOffer = JsonUtils.jsToMap(credentialIssuerConfigEntity.get().getCredentialOffer());

				// 找出credential_configuration_ids，新增值
				List<String> credentialConfigurationIds = (List<String>) mapCredentialOffer.get("credential_configuration_ids");
				if(!credentialConfigurationIds.contains(credentialType))
					credentialConfigurationIds.add(credentialType);

				// store updated CredentialIssuerConfig record
				CredentialIssuerConfigEntity updatedCredentialIssuerConfigEntity = credentialIssuerConfigEntity.get();
				updatedCredentialIssuerConfigEntity.setCredentialOffer(JsonUtils.mapToJs(mapCredentialOffer));
				updatedCredentialIssuerConfigEntity.setCredentialIssuerMeta(metadata);
				updatedCredentialIssuerConfigEntity = credentialIssuerConfigRepository.save(updatedCredentialIssuerConfigEntity);
			}

			// step 5: 創建Policy
			String pid = UUID.randomUUID().toString();
			String signatureAlg = "ES256";
			CredentialPolicyEntity credentialPolicyEntity = new CredentialPolicyEntity();
			credentialPolicyEntity.setPid(pid);
			credentialPolicyEntity.setCredentialType(credentialType);
			credentialPolicyEntity.setEffectiveDurationTimeUnit(TimeUnit.valueOf(sequenceRequest.getEffectiveTimeUnit()));
			credentialPolicyEntity.setEffectiveDurationTimeValue(sequenceRequest.getEffectiveTimeValue());
			credentialPolicyEntity.setSignatureAlg(signatureAlg);
			credentialPolicyEntity.setCreatedTime(new Timestamp(DateUtils.toDate(LocalDateTime.now()).getTime()));
			credentialPolicyEntity.setSeqName(seqName);
			credentialPolicyEntity.setSchemaId(sequenceRequest.getSchemaId());
			credentialPolicyEntity.setVcSchema(new ObjectMapper().writeValueAsString(sequenceRequest.getVcSchema()));
			// 將policy存進DB
			credentialPolicyEntity = credentialPolicyRepository.save(credentialPolicyEntity);

			if(!pid.equals(credentialPolicyEntity.getPid())) {
				throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store credential policy data");
			}


			// step 6: 創建序列
	        // 創建序列的 SQL 語句，參數化
	        String createSequenceSql = "CREATE SEQUENCE "+ "\"" + seqName + "\"" +
	        							" START WITH " + startValue +
	        							" INCREMENT BY " + incrementValue +
	        							" NO MINVALUE NO MAXVALUE CACHE 1";

	        // 執行原生 SQL，並設置參數
	        Query query = entityManager.createNativeQuery(createSequenceSql);
	        query.executeUpdate();

//	        Map<String, Object> map = new HashMap<>();
//	        map.put("sequenceName", sequenceName);
	        result = new SequenceResponseDTO(credentialType).toString();
            httpStatus = HttpStatus.CREATED;
		} catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch(NullPointerException e) {
        	LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_SEQ_PARSE_ISSUER_METADATA_ERROR, "fail to parse issuer metadata");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        } catch(IOException e) {
        	LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_SEQ_PARSE_ISSUER_METADATA_ERROR, "fail to parse issuer metadata");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to create sequence or policy");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }
		return Tuple.collect(result, httpStatus);
    }

	@Transactional
	public Tuple.Pair<String, HttpStatus> deleteSeqAndPolicyAndUpdateCredentialIssuerConfig(String credentialType,
			SequenceDelRequestDTO sequenceDelRequest){

		String result;
        HttpStatus httpStatus;

        try {
			// step 1: validate request
			if(sequenceDelRequest == null || !sequenceDelRequest.validate()) {
				throw new VcException(VcException.ERR_SEQ_INVALID_SEQUENCE_DELETING_REQUEST, "invalid sequence deleting request");
			}

			// step 2: 檢查序列名稱、credentialType是否合規
			// step 2.1: 從credential_policy中取得對應的sequence名稱
			Optional<CredentialPolicyEntity> credentialPolicyEntity = credentialPolicyRepository.findByCredentialType(credentialType);
	    	if(credentialPolicyEntity.isEmpty()) {
	    		throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get credential policy from DB");
	    	}

			String seqName = credentialPolicyEntity.get().getSeqName();
//			String seqName = "seq_" + credentialType.toLowerCase();
			if (!seqName.matches("^[a-zA-Z_][a-zA-Z0-9_-]*$")) {
			    throw new VcException(VcException.ERR_DB_INVALID_SEQUENCE_NAME, "Invalid sequence name");
			}
			// credentialType中不包含"_"，報錯
			if(credentialType.split("_").length == 1) {
				throw new VcException(VcException.ERR_SEQ_INVALID_CREDENTIAL_TYPE, "invalid credentialType");
			}

			// 解析credentialType中的businessId
			String businessld = credentialType.split("_")[0];

			// step 3: 從sequenceRequest中取得 metadata

			String metadata = JsonUtils.mapToJs(sequenceDelRequest.getMetadata());
			if(metadata == null || metadata.isBlank()) {
				throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "issuer's metadata in request is null or blank");
			}
			LOGGER.info("[deleteSeqAndPolicyAndUpdateCredentialIssuerConfig]:: metadata: {}", metadata);

			// step 4: 更新發證機關紀錄中的credential_offer欄位
			// 查詢是否存在發證機關(businessId)的credential_issuer_config紀錄
			Optional<CredentialIssuerConfigEntity> credentialIssuerConfigEntity = credentialIssuerConfigRepository.findByVcID(businessld);

			// 如果不存在該發證機關(businessId)的credential_issuer_config紀錄，報錯
			if(credentialIssuerConfigEntity.isEmpty()) {
				throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to find credential issuer config data");
			}else {// 存在credential_issuer_config紀錄，更新credential_offer欄位
				Map<String, Object> mapCredentialOffer = JsonUtils.jsToMap(credentialIssuerConfigEntity.get().getCredentialOffer());

				// 找出credential_configuration_ids，刪除值
				List<String> credentialConfigurationIds = (List<String>) mapCredentialOffer.get("credential_configuration_ids");
				if(credentialConfigurationIds.contains(credentialType))
					credentialConfigurationIds.remove(credentialType);
				else {
					throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to find credential policy in credential issuer config data");
				}

				// 如果該發政機關(businessId)的credential_offer已不包含任何credentialType，刪除該record
				if(credentialConfigurationIds.size() == 0) {
					credentialIssuerConfigRepository.deleteById(businessld);
				}else {
					// store updated CredentialIssuerConfig record
					CredentialIssuerConfigEntity updatedCredentialIssuerConfigEntity = credentialIssuerConfigEntity.get();
					updatedCredentialIssuerConfigEntity.setCredentialOffer(JsonUtils.mapToJs(mapCredentialOffer));
					updatedCredentialIssuerConfigEntity.setCredentialIssuerMeta(metadata);
					updatedCredentialIssuerConfigEntity = credentialIssuerConfigRepository.save(updatedCredentialIssuerConfigEntity);
				}
			}

			// step 5: 刪除Policy
			int deleteCount = credentialPolicyRepository.deleteByCredentialType(credentialType);
			LOGGER.info("[deleteSeqAndPolicyAndUpdateCredentialIssuerConfig]:: delete policy: deleteCount: {}", deleteCount);
			if(deleteCount != 1) {
				throw new VcException(VcException.ERR_DB_DELETE_ERROR, "fail to delete credential policy");
			}

			// step 6: 刪除序列
	        // 刪除序列的 SQL 語句，參數化
			jdbcTemplate.update("CALL drop_sequence_safe(?)", seqName);

	        result = new SequenceDelResponseDTO(credentialType).toString();
            httpStatus = HttpStatus.OK;
		} catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch(NullPointerException e) {
        	LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_SEQ_PARSE_ISSUER_METADATA_ERROR, "fail to parse issuer metadata");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_DB_DELETE_ERROR, "fail to delete sequence or policy");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }
		return Tuple.collect(result, httpStatus);
	}

	public Tuple.Pair<String, HttpStatus> setFunctionSwitch(String credentialType,
			FunctionSwitchRequestDTO functionSwitchRequest){

		String result;
        HttpStatus httpStatus;

        try {


        	if(functionSwitchRequest == null || !functionSwitchRequest.validate()) {
        		throw new VcException(VcException.ERR_SEQ_INVALID_FUNCTION_SWITCH_SETTING_REQUEST, "invalid function switch setting request");
        	}

        	// credentialType中不包含"_"，報錯
			if(credentialType.split("_").length == 1) {
				throw new VcException(VcException.ERR_SEQ_INVALID_CREDENTIAL_TYPE, "invalid credentialType");
			}

        	LinkedHashMap<String, Object> switches = functionSwitchRequest.getSwitches();

        	LOGGER.info("""
                    [setFunctionSwitch]::
                    credentialType = {}
                    switches = {}
                    """, credentialType, switches);

        	// find credentialType's function switch setting
        	Optional<CredentialPolicyEntity> credentialPolicyEntity = credentialPolicyRepository.findByCredentialType(credentialType);
        	if(credentialPolicyEntity.isEmpty()) {
        		throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get credential policy from DB");
        	}

        	// 解析DB中的設定字串，轉為Map
        	LinkedHashMap<String, Object> switchesInDB = new LinkedHashMap<>();
        	String switchesStrInDB = credentialPolicyEntity.get().getFuncSwitch();
        	if(switchesStrInDB != null && !switchesStrInDB.isBlank())
        		switchesInDB = (LinkedHashMap<String, Object>) JsonUtils.jsToMap(switchesStrInDB);

        	// 合併傳入資料
        	switchesInDB.putAll(switches);

        	// 檢查 enable_tx_code 與 enable_vc_transfer，不能都是true
        	if(Boolean.TRUE.equals(switchesInDB.get("enable_tx_code")) && Boolean.TRUE.equals(switchesInDB.get("enable_vc_transfer"))) {
        		throw new VcException(VcException.ERR_SEQ_TX_CODE_AND_VC_TRANSFER_BOTH_TRUE_ERROR, "enable_tx_code and enable_vc_transfer cannot both be true");
        	}

        	// 存回資料庫
        	String mergedSwitchesStr = JsonUtils.mapToJs(switchesInDB);
        	credentialPolicyEntity.get().setFuncSwitch(mergedSwitchesStr);
        	credentialPolicyRepository.save(credentialPolicyEntity.get());

        	if(!mergedSwitchesStr.equals(credentialPolicyEntity.get().getFuncSwitch()))
        		throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store func_switch");

        	result = new FunctionSwitchResponseDTO(credentialType).toString();
        	httpStatus = HttpStatus.OK;

        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update function switch");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }
		return Tuple.collect(result, httpStatus);
	}

	private String credentialIssuerAppendBusinessId(String credentialIssuer, String businessId) throws VcException {
		if(credentialIssuer.endsWith("api/issuer/")) {
			credentialIssuer = credentialIssuer + businessId + "/";
		}else if(credentialIssuer.endsWith("api/issuer")) {
			credentialIssuer = credentialIssuer + "/" + businessId + "/";
		}else {
			throw new VcException(VcException.ERR_SEQ_INVALID_CREDENTIAL_ISSUER_IDENTIFIER, "invalid credential issuer identifier");
		}
		return credentialIssuer;
	}

	private boolean checkDuplicatedKeyInMetadata(String metadataStr) throws IOException {
		Map<String, Set<String>> duplicateKeys = findDuplicateKeys(metadataStr);
		boolean isDuplicated = false;
        if (duplicateKeys.isEmpty()) {
        	LOGGER.info("[checkDeplicatedKeyInMetadata]:: No duplicate keys found.");
        } else {
        	LOGGER.info("[checkDeplicatedKeyInMetadata]:: Duplicate keys found:");
            for (Map.Entry<String, Set<String>> entry : duplicateKeys.entrySet()) {
            	LOGGER.info("[checkDeplicatedKeyInMetadata]:: Path: {}", entry.getKey());
            	LOGGER.info("[checkDeplicatedKeyInMetadata]:: Duplicate keys: {}", entry.getValue());
            	if("request/metadata/credential_configurations_supported".equals(entry.getKey())) {
            		isDuplicated = true;
            		break;
            	}
            }
        }
        return isDuplicated;
	}
	

	private Map<String, Set<String>> findDuplicateKeys(String json) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(json);

        Map<String, Set<String>> duplicateKeys = new HashMap<>();
        Deque<Map<String, Boolean>> stack = new ArrayDeque<>();
        Deque<String> pathStack = new ArrayDeque<>();
        pathStack.push("");

        while (!parser.isClosed()) {
            JsonToken token = parser.nextToken();

            if (token == JsonToken.START_OBJECT) {
                stack.push(new HashMap<>());
//                pathStack.push(pathStack.peek() + "/");
                pathStack.push(pathStack.peek() + (parser.currentName() == null ? "request" : "/" + parser.currentName()));
            } else if (token == JsonToken.END_OBJECT) {
                stack.pop();
                pathStack.pop();
            } else if (token == JsonToken.FIELD_NAME) {
                String fieldName = parser.currentName();
                String currentPath = pathStack.peek();

                Map<String, Boolean> currentLevel = stack.peek();
                if (currentLevel.containsKey(fieldName)) {
                    duplicateKeys
                        .computeIfAbsent(currentPath, k -> new HashSet<>())
                        .add(fieldName);
                } else {
                    currentLevel.put(fieldName, true);
                }
            }
        }

        return duplicateKeys;
    }

	private boolean isValidTimeUnit(String timeUnit) {

	    return Arrays.stream(TimeUnit.values())
                .anyMatch(unit -> unit.name().equalsIgnoreCase(timeUnit));
	}

	// 檢查輸入的有效期限是否超過1000年
	private boolean checkValidityPeriod(String timeUnit, int timeValue) {

		int years = 0;
		switch(timeUnit.toUpperCase()) {
			case "YEAR":
				years = timeValue;
				break;
			case "MONTH":
				years = timeValue / 12;
				break;
			case "DAY":
				years = timeValue / 365;
				break;
			case "HOUR":
				years = timeValue / 8760;
				break;
			case "MINUTE":
				years = timeValue / 525600;
				break;
		}
		return years < 1000;
	}

	// 檢查metadata的證件資料欄位是否包含id
	private boolean isCredentialSubjectInMetadataContainsId(String credentialType, String credentialIssuerMetadataStr) throws VcException{

		LOGGER.info("""
                [isCredentialSubjectInMetadataContainsId]::
                Credential Issuer Metadata = {}
                """,
                credentialIssuerMetadataStr);
		if(credentialIssuerMetadataStr == null || credentialIssuerMetadataStr.isBlank()) {
			throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "issuer's metadata in DB is null or blank");
		}
		else {
			CredentialIssuerMetadata credentialIssuerMetadata = new CredentialIssuerMetadata(credentialIssuerMetadataStr);
			Map<String, CredentialConfigurationSupported> credentialConfigurationsSupported = Optional.of(credentialIssuerMetadata)
	                .map(CredentialIssuerMetadata::getCredentialConfigurationsSupported)
	                .orElse(null);
			LOGGER.info("""
                    [isCredentialSubjectInMetadataContainsId]::
                    credential_configurations_supported = {}
                    """,
                    credentialConfigurationsSupported);
			if(credentialConfigurationsSupported == null || credentialConfigurationsSupported.isEmpty()) {
				throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "credential_configurations_supported in issuer's metadata is invalid");
			}
			else {
				if(!credentialConfigurationsSupported.containsKey(credentialType)) {
					throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "issuer's metadata does not include this credential type");
				}
				else {
					CredentialConfigurationSupported theCredentialConfigurationSupported = credentialConfigurationsSupported.get(credentialType);
					LOGGER.info("""
		                    [isCredentialSubjectInMetadataContainsId]::
		                    theCredentialConfigurationSupported = {}
		                    """,
		                    theCredentialConfigurationSupported);
					CredentialDefinition credentialDefinition = Optional.of(theCredentialConfigurationSupported)
			                .map(CredentialConfigurationSupported::getCredentialDefinition)
			                .orElse(null);
					if(credentialDefinition == null || !credentialDefinition.validate()) {
						throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "issuer's metadata does not include credential_definition or credential_definition is invalid");
					}
					else {
						LinkedHashMap<String, Object> credentialSubject = Optional.of(credentialDefinition)
				                .map(CredentialDefinition::getCredentialSubject)
				                .orElse(null);
						if(credentialSubject == null || credentialSubject.isEmpty()) {
							throw new VcException(VcException.ERR_SEQ_INVALID_ISSUER_METADATA, "issuer's metadata does not include credentialSubject or credentialSubject is invalid");
						}
						else {
							return credentialSubject.containsKey("id");
						}
					}
				}
			}
		}
	}
}
