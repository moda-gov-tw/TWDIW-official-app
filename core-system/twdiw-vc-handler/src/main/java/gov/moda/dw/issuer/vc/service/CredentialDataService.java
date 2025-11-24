package gov.moda.dw.issuer.vc.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import gov.moda.dw.issuer.vc.service.dto.ErrorResponseProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.issuer.vc.domain.CredentialDataEntity;
import gov.moda.dw.issuer.vc.domain.CredentialIssuerConfigEntity;
import gov.moda.dw.issuer.vc.domain.CredentialPolicyEntity;
import gov.moda.dw.issuer.vc.repository.CredentialDataRepository;
import gov.moda.dw.issuer.vc.repository.CredentialIssuerConfigRepository;
import gov.moda.dw.issuer.vc.repository.CredentialPolicyRepository;
import gov.moda.dw.issuer.vc.service.dto.CredentialDataRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.CredentialDataResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.ErrorResponseDTO;
import gov.moda.dw.issuer.vc.util.DateUtils;
import gov.moda.dw.issuer.vc.util.DateUtils.TimeUnit;
import gov.moda.dw.issuer.vc.util.JsonUtils;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.CredentialIssuerMetadata;
import gov.moda.dw.issuer.vc.vo.DataStatus;
import gov.moda.dw.issuer.vc.vo.Policy;
import gov.moda.dw.issuer.vc.vo.PreloadSetting;
import gov.moda.dw.issuer.vc.vo.VcException;
import gov.moda.dw.issuer.vc.vo.VcSchema;
import gov.moda.dw.issuer.vc.vo.VcSchema.CredentialSubject;
import gov.moda.dw.issuer.vc.vo.VcSchema.PropertiesDef;
import gov.moda.dw.issuer.vc.vo.CredentialIssuerMetadata.CredentialConfigurationSupported;
import gov.moda.dw.issuer.vc.vo.CredentialIssuerMetadata.CredentialDefinition;

@Service
public class CredentialDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CredentialDataService.class);
	
	private final CredentialDataRepository credentialDataRepository;
	
	private final CredentialIssuerConfigRepository credentialIssuerConfigRepository;
	
	private final CredentialPolicyRepository credentialPolicyRepository;
	
	private final PreloadSetting preloadSetting;
	
	public CredentialDataService(CredentialDataRepository credentialDataRepository, 
			CredentialIssuerConfigRepository credentialIssuerConfigRepository,
			CredentialPolicyRepository credentialPolicyRepository,
			PreloadSetting preloadSetting) {
		this.credentialDataRepository = credentialDataRepository;
		this.credentialIssuerConfigRepository = credentialIssuerConfigRepository;
		this.credentialPolicyRepository = credentialPolicyRepository;
		this.preloadSetting = preloadSetting;
	}
	
	@Transactional
	public Tuple.Pair<String, HttpStatus> putDataToDB(CredentialDataRequestDTO credentialDataRequest) {
		
		String result;
        HttpStatus httpStatus;
        try {
        	// step 1: validate request
            if (credentialDataRequest == null || !credentialDataRequest.validate()) {
                throw new VcException(VcException.ERR_CRED_DATA_INVALID_CREDENTIAL_DATA_SETTING_REQUEST, "invalid credential data setting request");
            }
            
            // step 1.1: 檢查request中的data是否包含"id"欄位，若有，則報錯
            if(credentialDataRequest.getData().containsKey("id")) {
            	throw new VcException(VcException.ERR_CRED_DATA_INVALID_DATA_FIELD, "invalid credential data field('id' is not allowed)");
            }
            
            // step 1.2: 檢查是否有指定發行日期、逾期日期
            Policy policy = loadCredentialPolicy(credentialDataRequest.getCredentialType());
            // calculate issuance time and expiration time
            Policy.Duration duration = policy.getEffectiveDuration();
            LinkedHashMap<String, Object> options = credentialDataRequest.getOptions();
            String selectedIssuanceDateStr = null;
        	String selectedExpirationDateStr = null;
        	LocalDateTime selectedIssuanceDate = null;
        	LocalDateTime selectedExpirationDate = null;
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        	
        	if(options != null && !options.isEmpty()) {
        		if(options.containsKey("issuanceDate")) {
        			selectedIssuanceDateStr = (String)options.get("issuanceDate");
        			if(selectedIssuanceDateStr != null && !selectedIssuanceDateStr.isBlank()) {
    	    			try {
    		    			LocalDate localIssuanceDate = LocalDate.parse(selectedIssuanceDateStr, formatter);
    		    			selectedIssuanceDate = localIssuanceDate.atStartOfDay();// 加上 00:00:00
    	    			}catch(Exception e) {
    	    				throw new VcException(VcException.ERR_CRED_INVALID_ISSUANCE_DATE_FORMAT, "IssuanceDate format is invalid.");
    	    			}
        			}
        		}
        		
        		if(selectedIssuanceDate == null)
        			selectedIssuanceDate = LocalDateTime.now();
        		
        		if(options.containsKey("expirationDate")) {
        			selectedExpirationDateStr = (String)options.get("expirationDate");
        			if(selectedExpirationDateStr != null && !selectedExpirationDateStr.isBlank()) {
        				LocalDate localExpirationDate = null;
    	    			try {
    		    			localExpirationDate = LocalDate.parse(selectedExpirationDateStr, formatter);
    		    			selectedExpirationDate = localExpirationDate.atTime(23, 59, 59);
    	    			}catch(Exception e) {
    	    				throw new VcException(VcException.ERR_CRED_INVALID_EXPIRATION_DATE_FORMAT, "ExpirationDate format is invalid.");
    	    			}
    	    			
    	    			// 檢查傳入值有沒有小於「指定發行時間 (有傳值)」或「當前時間(未傳值)」，若有此情況則報錯
    	            	if(selectedExpirationDate.isBefore(selectedIssuanceDate)) {
    	            		throw new VcException(VcException.ERR_CRED_INVALID_EXPIRATION_DATE, "ExpirationDate is not valid: The expiration date cannot be earlier than the issuance date.");
	        			}
        			}
        		}
        	}
        	
        	if(selectedIssuanceDate == null)
    			selectedIssuanceDate = LocalDateTime.now();
        	
        	if(selectedExpirationDate == null) {
        		selectedExpirationDate = DateUtils.calculate(selectedIssuanceDate, duration.getUnit(), duration.getValue());
        	}
        	
        	// 檢查日期合理性
            if(selectedIssuanceDate.isAfter(LocalDateTime.now())) {
            	throw new VcException(VcException.ERR_CRED_INVALID_ISSUANCE_DATE, "IssuanceDate is not valid: The issuance date cannot be later than the current time.");
            }
            
            if(selectedExpirationDate.isBefore(LocalDateTime.now())) {
            	throw new VcException(VcException.ERR_CRED_INVALID_EXPIRATION_DATE, "ExpirationDate is not valid: The expirationDate date cannot be earlier than the current time.");
            }
            
            // step 2: 檢查data、issuer's metadata、vc schema中記載欄位是否相同?
            LinkedHashMap<String, Object> credentialSubjectInMeta = getCredentialSubjectFromMetadataInDB(credentialDataRequest.getCredentialType());           
            LOGGER.info("""
                    [putDataToDB]::
                    credentialSubjectInMeta = {}
                    """,
                    JsonUtils.mapToJs(credentialSubjectInMeta));
            
            LinkedHashMap<String, Object> propertiesInSchema = getPropertiesFromVcSchemaInDB(credentialDataRequest.getCredentialType());
            LOGGER.info("""
                    [putDataToDB]::
                    propertiesInSchema = {}
                    """,
                    JsonUtils.mapToJs(propertiesInSchema));
            
//            for(int i = 0 ; i < 100 ; i++) {
//            	Map<String, Object> propertiesTest = getPropertiesFromVcSchemaInDB(credentialDataRequest.getCredentialType());
//            	LOGGER.info("""
//                        [putDataToDB]::
//                        propertiesTest = {}
//                        """,
//                        JsonUtils.mapToJs(propertiesTest));
//            }
            
            // 比對issuer's metadata、vc schema中記載欄位是否相同
            // 先將vc schema中的固定欄位"id"去除
            if(!propertiesInSchema.containsKey("id")) {
            	throw new VcException(VcException.ERR_CRED_DATA_INVALID_VC_SCHEMA, "properties in vc schema is invalid");
            }
            else {
            	propertiesInSchema.remove("id");
            	LOGGER.info("""
                        [putDataToDB]::
                        propertiesInSchema(remove 'id') = {}
                        """,
                        JsonUtils.mapToJs(propertiesInSchema));
            	
            	if(!compareKeySet(propertiesInSchema, credentialSubjectInMeta)) {
            		throw new VcException(VcException.ERR_CRED_DATA_FIELDS_IN_SCHEMA_AND_METADATA_NOT_IDENTICAL, "data fields in the schema and metadata are not identical");
            	}
            	
            	// 比較vc schema、issuer's metadata之資料欄位順序
            	Iterator<Map.Entry<String, Object>> iteratorSchema = propertiesInSchema.entrySet().iterator();
            	Iterator<Map.Entry<String, Object>> iteratorMetadata = credentialSubjectInMeta.entrySet().iterator();
            	
            	while(iteratorSchema.hasNext() && iteratorMetadata.hasNext()) {
            		Map.Entry<String, Object> entrySchema = iteratorSchema.next();
            		Map.Entry<String, Object> entryMetadata = iteratorMetadata.next();
            		
            		if(!entrySchema.getKey().equals(entryMetadata.getKey())) {
            			throw new VcException(VcException.ERR_CRED_DATA_FIELDS_IN_SCHEMA_AND_METADATA_NOT_IDENTICAL, "the order of data fields in the schema and metadata are not identical");
            		}
            	}
                
                // issuer's metadata、vc schema欄位相同，再比較data 與 vc schema
            	if(!compareKeySet(propertiesInSchema, credentialDataRequest.getData())) {
            		throw new VcException(VcException.ERR_CRED_DATA_FIELDS_IN_SCHEMA_AND_VC_DATA_NOT_IDENTICAL, "data fields in the schema and vc data are not identical");
            	}
            	
            	// 比較data與vc schema之資料順序
            	LOGGER.info("""
                        [putDataToDB]::
                        vc data(original) = {}
                        """,
                        JsonUtils.mapToJs(credentialDataRequest.getData()));
            	
            	boolean isTheSameOrder = true;
            	
            	iteratorSchema = null;
            	iteratorSchema = propertiesInSchema.entrySet().iterator();
            	Iterator<Map.Entry<String, Object>> iteratorData = credentialDataRequest.getData().entrySet().iterator();
            	
            	while (iteratorSchema.hasNext() && iteratorData.hasNext()) {
                    Map.Entry<String, Object> entrySchema = iteratorSchema.next();
                    Map.Entry<String, Object> entryData = iteratorData.next();

                    if (!entrySchema.getKey().equals(entryData.getKey())) {
                    	isTheSameOrder = false;
                    }
                }
            	
            	if(!isTheSameOrder) {
            		// 將data資料順序調整至與vc schema中定義相同
                	LinkedHashMap<String, Object> vcDataInOrder = new LinkedHashMap<>();
                	iteratorSchema = null;
                	iteratorSchema = propertiesInSchema.entrySet().iterator();
                	while (iteratorSchema.hasNext()) {
                    	Map.Entry<String, Object> entrySchema = iteratorSchema.next();
                    	vcDataInOrder.put(entrySchema.getKey(), credentialDataRequest.getData().get(entrySchema.getKey()));
                    }
                	LOGGER.info("""
                            [putDataToDB]::
                            vc data(rearranging the order) = {}
                            """,
                            JsonUtils.mapToJs(vcDataInOrder));
                	// 更新credentialDataRequest中的data
                	credentialDataRequest.setData(vcDataInOrder);
            	}
            }
        	
            // step 3: 新增DB紀錄
        	// *****************新增一筆資料紀錄*****************
        	String transactionId = credentialDataRequest.getTransactionId();
        	
        	if(credentialDataRepository.existsById(transactionId)) {
        		throw new VcException(VcException.ERR_DB_INSERT_ERROR, "the data already exists");
        	}
        	
        	CredentialDataEntity credentialDataEntity = new CredentialDataEntity();
        	credentialDataEntity.setTransactionID(transactionId);
        	credentialDataEntity.setCredentialType(credentialDataRequest.getCredentialType());
        	credentialDataEntity.setData(new ObjectMapper().writeValueAsString(credentialDataRequest.getData()));
        	LocalDateTime issuanceTime = LocalDateTime.now();
        	credentialDataEntity.setCreatedTime(new Timestamp(DateUtils.toDate(issuanceTime).getTime()));
        	// 10分鐘後過期
        	LocalDateTime expirationTime = DateUtils.calculate(issuanceTime, TimeUnit.MINUTE, 10);
        	credentialDataEntity.setExpiredTime(new Timestamp(DateUtils.toDate(expirationTime).getTime()));
        	credentialDataEntity.setStatus(DataStatus.VALID.getValue());
        	credentialDataEntity.setOptions((credentialDataRequest.getOptions() != null && !credentialDataRequest.getOptions().isEmpty()) ? 
        			JsonUtils.mapToJs(credentialDataRequest.getOptions()) : null);
        	
        	credentialDataEntity = credentialDataRepository.save(credentialDataEntity);
        	
        	if(!transactionId.equals(credentialDataEntity.getTransactionID()))
        		throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store credential data");
        	
        	result = new CredentialDataResponseDTO(transactionId).toString();
        	httpStatus = HttpStatus.CREATED;
        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store credential data");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }
		return Tuple.collect(result, httpStatus);
	}
	
	public LinkedHashMap<String, Object> getCredentialSubjectFromMetadataInDB(String credentialType) throws VcException{
		
		// credentialType中不包含"_"，報錯
		if(credentialType.split("_").length == 1) {
			throw new VcException(VcException.ERR_CRED_DATA_INVALID_CREDENTIAL_TYPE, "invalid credentialType");
		}
		String businessld = credentialType.split("_")[0];
		
		Optional<CredentialIssuerConfigEntity> credentialIssuerConfigEntity = credentialIssuerConfigRepository.findByVcID(
				businessld);
		if(credentialIssuerConfigEntity.isEmpty()) {
			throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get issuer's metadata from DB");
		}
		else {
			String credentialIssuerMetadataStr = credentialIssuerConfigEntity.get().getCredentialIssuerMeta();
			LOGGER.info("""
                    [getCredentialSubjectFromMetadataInDB]::
                    Credential Issuer Metadata = {}
                    """,
                    credentialIssuerMetadataStr);
			if(credentialIssuerMetadataStr == null || credentialIssuerMetadataStr.isBlank()) {
				throw new VcException(VcException.ERR_CRED_DATA_INVALID_ISSUER_METADATA, "issuer's metadata in DB is null or blank");
			}
			else {
				CredentialIssuerMetadata credentialIssuerMetadata = new CredentialIssuerMetadata(credentialIssuerMetadataStr);
				Map<String, CredentialConfigurationSupported> credentialConfigurationsSupported = Optional.of(credentialIssuerMetadata)
		                .map(CredentialIssuerMetadata::getCredentialConfigurationsSupported)
		                .orElse(null);
				LOGGER.info("""
	                    [getCredentialSubjectFromMetadataInDB]::
	                    credential_configurations_supported = {}
	                    """,
	                    credentialConfigurationsSupported);
				if(credentialConfigurationsSupported == null || credentialConfigurationsSupported.isEmpty()) {
					throw new VcException(VcException.ERR_CRED_DATA_INVALID_ISSUER_METADATA, "credential_configurations_supported in issuer's metadata is invalid");
				}
				else {
					if(!credentialConfigurationsSupported.containsKey(credentialType)) {
						throw new VcException(VcException.ERR_CRED_DATA_INVALID_ISSUER_METADATA, "issuer's metadata does not include this credential type");
					}
					else {
						CredentialConfigurationSupported theCredentialConfigurationSupported = credentialConfigurationsSupported.get(credentialType);
						LOGGER.info("""
			                    [getCredentialSubjectFromMetadataInDB]::
			                    theCredentialConfigurationSupported = {}
			                    """,
			                    theCredentialConfigurationSupported);
						CredentialDefinition credentialDefinition = Optional.of(theCredentialConfigurationSupported)
				                .map(CredentialConfigurationSupported::getCredentialDefinition)
				                .orElse(null);
						if(credentialDefinition == null || !credentialDefinition.validate()) {
							throw new VcException(VcException.ERR_CRED_DATA_INVALID_ISSUER_METADATA, "issuer's metadata does not include credential_definition or credential_definition is invalid");
						}
						else {
							LinkedHashMap<String, Object> credentialSubject = Optional.of(credentialDefinition)
					                .map(CredentialDefinition::getCredentialSubject)
					                .orElse(null);
							if(credentialSubject == null || credentialSubject.isEmpty()) {
								throw new VcException(VcException.ERR_CRED_DATA_INVALID_ISSUER_METADATA, "issuer's metadata does not include credentialSubject or credentialSubject is invalid");
							}
							else {
								return credentialSubject;
							}
						}
					}
				}
			}
		}
	}
	
	public LinkedHashMap<String, Object> getPropertiesFromVcSchemaInDB(String credentialType) throws VcException{
		
		Optional<CredentialPolicyEntity> credentialPolicyEntity = credentialPolicyRepository.findByCredentialType(credentialType);
		if(credentialPolicyEntity.isEmpty()) {
			throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get credential policy from DB");
		}
		else {
			String vcSchemaStr = credentialPolicyEntity.get().getVcSchema();
			LOGGER.info("""
                    [getPropertiesFromVcSchemaInDB]::
                    VC schema = {}
                    """,
                    vcSchemaStr);
			if(vcSchemaStr == null || vcSchemaStr.isBlank()) {
				throw new VcException(VcException.ERR_CRED_DATA_INVALID_VC_SCHEMA, "VC schema in DB is null or blank");
			}
			else {
				VcSchema vcSchema = new VcSchema(vcSchemaStr);
				PropertiesDef propertiesDef = Optional.of(vcSchema)
		                .map(VcSchema::getProperties)
		                .orElse(null);
				LOGGER.info("""
	                    [getPropertiesFromVcSchemaInDB]::
	                    propertiesDef = {}
	                    """,
	                    propertiesDef);
				if(propertiesDef == null || !propertiesDef.validate()) {
					throw new VcException(VcException.ERR_CRED_DATA_INVALID_VC_SCHEMA, "properties in vc schema is invalid");
				}
				else {
					CredentialSubject credentialSubject = propertiesDef.getCredentialSubject();
					LOGGER.info("""
		                    [getPropertiesFromVcSchemaInDB]::
		                    credentialSubject = {}
		                    """,
		                    credentialSubject);
					LinkedHashMap<String, Object> properties = Optional.of(credentialSubject)
			                .map(CredentialSubject::getProperties)
			                .orElse(null);
					if(properties == null || properties.isEmpty()) {
						throw new VcException(VcException.ERR_CRED_DATA_INVALID_VC_SCHEMA, "properties in vc schema is invalid");
					}
					else {
						return properties;
					}
				}
			}
		}
	}
	
	public boolean compareKeySet(LinkedHashMap<String, Object> linkedHashMap1, LinkedHashMap<String, Object> linkedHashMap2) throws VcException{
		// 提取鍵集合
        Set<String> fields1 = linkedHashMap1.keySet();
        Set<String> fields2 = linkedHashMap2.keySet();
        
        LOGGER.info("""
                [compareKeySet]::
                fields1 = {}
                fields2 = {}
                """,
                fields1, fields2);
        // 比較鍵集合
        if(!fields1.equals(fields2)) {
        	return false;
        }
        else
        	return true;
	}
	
	/**
     * load credential policy
     *
     * @param credentialType associated credential type, ex: VirtualCardCredential
     * @return policy
     * @throws VcException
     */
    private Policy loadCredentialPolicy(String credentialType) throws VcException {

    	Optional<CredentialPolicyEntity> credentialPolicyEntity = credentialPolicyRepository.findByCredentialType(credentialType);
    	if(credentialPolicyEntity.isEmpty()) {
    		throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get credential policy from DB");
    	}

    	return new Policy()
                .setPid(credentialPolicyEntity.get().getPid())
                .setCredentialType(credentialType)
                .setSchemaName("")
                // generated from `SchemaUtil.generateSchema()`
                .setSchemaContent(credentialPolicyEntity.get().getVcSchema())
                // effective duration: 5 years
                .setEffectiveDuration(new Policy.Duration(credentialPolicyEntity.get().getEffectiveDurationTimeValue(), credentialPolicyEntity.get().getEffectiveDurationTimeUnit()))
                .setSignatureAlgorithm(credentialPolicyEntity.get().getSignatureAlg())
                .setSeqName(credentialPolicyEntity.get().getSeqName())
                .setSchemaId(credentialPolicyEntity.get().getSchemaId());
    }

	// delete expired data from table credential_data
	@Transactional
	@Scheduled(cron = "#{@preloadSetting.getScheduleCronDeleteData()}")
	public void deleteExpiredData() {
		// 從DB setting獲取要刪除幾分鐘前的資料
		LocalDateTime nMinutesAgo = LocalDateTime.now().minusMinutes(Long.valueOf(preloadSetting.getDataDeleteNMinutesAgo()));
		LOGGER.info("nMinutesAgo:{}", nMinutesAgo);
		// 將LocalDateTime轉為Timestamp
		Timestamp nMinutesAgoTimestamp = Timestamp.valueOf(nMinutesAgo);
		// 刪除資料
		credentialDataRepository.deleteByExpiredTime(nMinutesAgoTimestamp);
	}
}
