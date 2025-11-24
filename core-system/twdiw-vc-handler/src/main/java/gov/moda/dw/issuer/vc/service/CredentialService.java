package gov.moda.dw.issuer.vc.service;

import com.authlete.sd.Disclosure;
import com.authlete.sd.SDJWT;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import gov.moda.dw.issuer.vc.domain.Credential;
import gov.moda.dw.issuer.vc.domain.CredentialDataEntity;
import gov.moda.dw.issuer.vc.domain.CredentialIssuerConfigEntity;
import gov.moda.dw.issuer.vc.domain.CredentialPolicyEntity;
import gov.moda.dw.issuer.vc.domain.CredentialSuspensionEntity;
import gov.moda.dw.issuer.vc.domain.CredentialTransferEntity;
import gov.moda.dw.issuer.vc.domain.OrgEntity;
import gov.moda.dw.issuer.vc.domain.Ticket;
import gov.moda.dw.issuer.vc.repository.CredentialDataRepository;
import gov.moda.dw.issuer.vc.repository.CredentialIssuerConfigRepository;
import gov.moda.dw.issuer.vc.repository.CredentialPolicyRepository;
import gov.moda.dw.issuer.vc.repository.CredentialRepository;
import gov.moda.dw.issuer.vc.repository.CredentialSuspensionRepository;
import gov.moda.dw.issuer.vc.repository.CredentialTransferRepository;
import gov.moda.dw.issuer.vc.repository.OrgRepository;
import gov.moda.dw.issuer.vc.repository.SettingRepository;
import gov.moda.dw.issuer.vc.repository.StatusListRepository;
import gov.moda.dw.issuer.vc.repository.TicketRepository;
import gov.moda.dw.issuer.vc.service.dto.*;
import gov.moda.dw.issuer.vc.service.dto.demo.GetHolderDataRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.demo.GetHolderDataResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidGetIssuerInfoResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.oidvci.OidvciQrcodeRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.oidvci.OidvciQrcodeResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.push.PushNotifyStatusChangeRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.push.PushNotifyStatusChangeResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.vp.VpValidationErrorResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.vp.VpValidationResponseDTO;
import gov.moda.dw.issuer.vc.task.*;
import gov.moda.dw.issuer.vc.util.*;
import gov.moda.dw.issuer.vc.util.DateUtils.TimeUnit;
import gov.moda.dw.issuer.vc.vo.*;

import org.apache.http.entity.StringEntity;
import org.bitcoinj.base.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPrivateKey;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * credential service
 *
 * @version 20250526
 */
@Service
public class CredentialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialService.class);

    private final PreloadSetting preloadSetting;
    private final Uris uris;
    private final TicketRepository ticketRepository;
    private final CredentialRepository credentialRepository;
    private final CredentialPolicyRepository credentialPolicyRepository;
    private final CredentialDataRepository credentialDataRepository;
    private final SettingRepository settingRepository;
    private final StatusListService statusListService;
    private final OrgRepository orgRepository;
    private final CredentialTransferRepository credentialTransferRepository;
    private final CredentialDataService credentialDataService;
    private final CredentialIssuerConfigRepository credentialIssuerConfigRepository;
    private final CredentialSuspensionRepository credentialSuspensionRepository;

    @Value("${vc.account.token}")
    private String token;
    
    @Value("${vc.key.enc}")
    private String vcKeyEnc;


    public CredentialService(
        PreloadSetting preloadSetting,
        Uris uris,
        TicketRepository ticketRepository,
        StatusListRepository statusListRepository,
        CredentialRepository credentialRepository,
        CredentialPolicyRepository credentialPolicyRepository,
        CredentialDataRepository credentialDataRepository,
        SettingRepository settingRepository,
        StatusListService statusListService,
        OrgRepository orgRepository,
        CredentialTransferRepository credentialTransferRepository,
        CredentialDataService credentialDataService,
        CredentialIssuerConfigRepository credentialIssuerConfigRepository,
        CredentialSuspensionRepository credentialSuspensionRepository) {
        this.preloadSetting = preloadSetting;
        this.uris = uris;
        this.ticketRepository = ticketRepository;
        this.credentialRepository = credentialRepository;
        this.credentialPolicyRepository = credentialPolicyRepository;
        this.credentialDataRepository = credentialDataRepository;
        this.settingRepository = settingRepository;
        this.statusListService = statusListService;
        this.orgRepository = orgRepository;
        this.credentialTransferRepository = credentialTransferRepository;
        this.credentialDataService = credentialDataService;
        this.credentialIssuerConfigRepository = credentialIssuerConfigRepository;
        this.credentialSuspensionRepository = credentialSuspensionRepository;
    }

    /**
     * [dwissuer-104i] generate credential
     *
     * @param credentialRequest credential request
     * @return (credential response, http status)
     */
    public Tuple.Pair<String, HttpStatus> generate(CredentialRequestDTO credentialRequest) {

        String result;
        HttpStatus httpStatus;

        try {
        	// step 0: check issuer's key && issuer's Did
        	String issuerKeyInDB = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_KEY);
            if(issuerKeyInDB == null || issuerKeyInDB.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR, "issuer has not yet registered a DID");
            }
            // step 0.1: if issuer key is encrypted, decrypt it
            String issuerKey = null;
            try {
            	if(!KeyUtils.isPossiblyJwk(issuerKeyInDB)) {
            		issuerKey = KeyUtils.GCMDecrypt(issuerKeyInDB, vcKeyEnc);
            	}
            	else
            		issuerKey = issuerKeyInDB;
            }catch(Exception e) {
            	LOGGER.error(e.getMessage(), e);
            	throw new VcException(VcException.ERR_SYS_DECRYPT_CIPHER_ERROR, "Failed to decrypt key cipher");
            }
            if(issuerKey == null || issuerKey.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_DECRYPT_RESULT_NULL_ERROR, "The decrypted value is empty or null");
            }

            String issuerDid = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_DID);
            if(issuerDid == null || issuerDid.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR, "issuer has not yet registered a DID");
            }

            // check issuer's Did status
            DidGetIssuerInfoResponseDTO didGetIssuerInfoResponseDTO = callFrontendToGetIssuerInfo(issuerDid);
            Console.showJson(didGetIssuerInfoResponseDTO.toString(), "Get issuer's DID infomation response");

            // check content
            int code = didGetIssuerInfoResponseDTO.getCode();
            DidGetIssuerInfoResponseDTO.Data data = didGetIssuerInfoResponseDTO.getData();
            int didStatus = Optional.ofNullable(data)
            		.map(DidGetIssuerInfoResponseDTO.Data::getStatus)
            		.orElse(-1);

            if(code != 0 || data == null || didStatus == -1) {
            	String errorMessage = "fail to get issuer's DID info. (code = " + code + ")";
            	throw new VcException(VcException.ERR_DID_FRONTEND_GET_ISSUER_DID_INFO_ERROR, errorMessage);
            }
            // 1: 代表Did有效
            if(didStatus != 1) {
            	throw new VcException(VcException.ERR_SYS_ISSUER_DID_NOT_VALID_ERROR, "issuer's Did is not VALID");
            }

            // step 1: validate request
            if (credentialRequest == null || !credentialRequest.validate()) {
                throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_GENERATION_REQUEST, "invalid credential generation request");
            }

            String credentialType = credentialRequest.getCredentialType();
            String holderUid = credentialRequest.getHolderUid();
            String holderDid = credentialRequest.getHolderDid();
            Map<String, Object> holderPublicKey = credentialRequest.getHolderPublicKey();
            String nonce = credentialRequest.getNonce();
            
            // 從holderDid解析成holder public key(可順便檢查holderDid格式是否正確)
            Map<String, Object> holderPublicKeyFromDid = didToJWK(holderDid); 

            LOGGER.info("""
                    [credential generate request]::
                    credential type = {}
                    holder UID = {}
                    holder DID = {}
                    holder public key = {}
                    nonce = {}
                    holder public key from holder DID = {}
                    """,
                credentialType, holderUid, holderDid, holderPublicKey, nonce, holderPublicKeyFromDid);

            // step 2: get holder's data
            int vcDataSource = getVcDataSourceFromDB(credentialType);

            // 檢查此案件是一般genVC或者是手機移轉
            // CredentialTransferEntity中的資料是舊的VC
            CredentialTransferEntity credentialTransferEntity = null;
            credentialTransferEntity = checkVcTransfer(nonce, credentialType);
            boolean isVcTransfer = false;
            if(credentialTransferEntity == null) {
            	isVcTransfer = false;
            	LOGGER.info("[credential generate request]:: This case is GenVC!");
            } else {
            	isVcTransfer = true;
            	if(credentialTransferEntity.getData() == null || credentialTransferEntity.getData().isBlank() ||
            			credentialTransferEntity.getOldCid() == null || credentialTransferEntity.getOldCid().isBlank()) {
            		throw new VcException(VcException.ERR_CRED_INVALID_TRANSFER_DATA, "invalid transfer data");
            	}
            	LOGGER.info("[credential generate request]:: This case is VC transfer!");
            }

            Map<String, Object> holderData = null;
            CredentialDataEntity credentialDataEntity = null;
            Map<String, Object> options = null;

            // 手機移轉的情況，holderData為舊VC資料中的credentialSubject(去掉id欄位)
            if(isVcTransfer) {
            	// get holder's data from DB
	            credentialDataEntity = getHolderDataFromDB(nonce, credentialType);

	            try {
	            	holderData = JsonUtils.jsToMap(credentialDataEntity.getData());
	            }catch (Exception e) {
	            	LOGGER.error(e.getMessage(), e);
	            	throw new VcException(VcException.ERR_CRED_DATA_CREDENTIAL_DATA_CONVERT_ERROR, "Failed to convert JSON string into Map<String, Object>");
	            }
            }else {
	            // use 901i API to request holder's data
	            if(vcDataSource == 901) {
		            GetHolderDataRequestDTO getHolderDataRequestDTO = new GetHolderDataRequestDTO(credentialType, nonce, holderUid);
		            GetHolderDataResponseDTO getHolderDataResponseDTO = callDemoToGetHolderData(getHolderDataRequestDTO);
		            holderData = Optional.of(getHolderDataResponseDTO)
		                .map(GetHolderDataResponseDTO::getData)
		                .orElse(null);
		            options = Optional.of(getHolderDataResponseDTO)
			                .map(GetHolderDataResponseDTO::getOptions)
			                .orElse(null);
	            }
	            else {
		            // get holder's data from DB
		            credentialDataEntity = getHolderDataFromDB(nonce, credentialType);
		            String optionsStr = Optional.ofNullable(credentialDataEntity.getOptions()).orElse(null);

		            try {
		            	holderData = JsonUtils.jsToMap(credentialDataEntity.getData());
		            	if(optionsStr != null) {
		            		options = JsonUtils.jsToMap(optionsStr);
		            	}
		            }catch (Exception e) {
		            	LOGGER.error(e.getMessage(), e);
		            	throw new VcException(VcException.ERR_CRED_DATA_CREDENTIAL_DATA_CONVERT_ERROR, "Failed to convert JSON string into Map<String, Object>");
		            }
	            }
            }

            if (holderData == null || holderData.isEmpty()) {
                throw new VcException(VcException.ERR_CRED_GET_HOLDER_DATA_ERROR, "fail to get holder's data");
            }

            // step 3.1: load credential policy from DB
            Policy policy = loadCredentialPolicy(credentialType);
            String sequenceName = policy.getSeqName();
            LOGGER.info("sequenceName: {}", sequenceName);
            
            // step 3.2: 檢查是否有指定發行日期、逾期日期
            // calculate issuance time and expiration time
            Policy.Duration duration = policy.getEffectiveDuration();
            String selectedIssuanceDateStr = null;
        	String selectedExpirationDateStr = null;
        	LocalDateTime selectedIssuanceDate = null;
        	LocalDateTime selectedExpirationDate = null;
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        	
        	if(!isVcTransfer) {
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
        	}

            // step 3.3: take ticket
            Ticket ticket = ticketRepository.takeTicket(credentialType, sequenceName);
            if (ticket == null) {
                throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to take ticket");
            }

            Tuple.Pair<Integer, Integer> tnPair = calculateFromTicketNumber(ticket.getTicketNumber());
            int gid = tnPair.getA();
            int statusListIndex = tnPair.getB();

            // step 4.1: collect necessary information for credential

            String cid = UUID.randomUUID().toString();
            URI issuerId = URI.create(issuerDid);
            URI credentialId = uris.generateCredentialId(cid);
            URI statusListId = uris.generateStatusListId(credentialType, Definition.StatusListType.revocation.getTag().concat(String.valueOf(gid)));
            URI statusListIdSuspension = uris.generateStatusListId(credentialType, Definition.StatusListType.suspension.getTag().concat(String.valueOf(gid)));
            URI schemaUri = URI.create(policy.getSchemaId());
            URI publicKeysUri = uris.generatePublicKeysUri();

            // step 4.2: prepare new credential or VC transfer
            Tuple.Pair<VerifiableCredential, List<Disclosure>> pair = null;
            VerifiableCredential vc = null;
            List<Disclosure> disclosureList = null;

            // 手機移轉的情況
            if(isVcTransfer) {
            	// get SchemaId、expirationDate from transfer data
            	VpValidationResponseDTO.Vcs.Credential oldCredential = new VpValidationResponseDTO.Vcs.Credential(credentialTransferEntity.getData());
            	if(oldCredential == null || !oldCredential.validate()) {
            		throw new VcException(VcException.ERR_CRED_INVALID_TRANSFER_DATA_CREDENTIAL, "invalid transfer data(credential)");
            	}
            	LinkedHashMap<String, Object> credentialSchema = oldCredential.getCredentialSchema();
            	if(!credentialSchema.containsKey("id")) {
            		throw new VcException(VcException.ERR_CRED_INVALID_TRANSFER_DATA_CREDENTIAL_SCHEMA, "invalid transfer data(credentialSchema)");
            	}
            	String schemaId = credentialSchema.get("id").toString();
            	String expirationDateStr = oldCredential.getExpirationDate();

            	CredentialTransferPrepareTask credentialTransferPrepareTask = new CredentialTransferPrepareTask.Builder()
            			.setHolderDid(holderDid)
            			.setHolderData(holderData)
            			.setStatusListIndex(statusListIndex)
            			.setCredentialType(credentialType)
            			.setIssuerId(issuerId)
            			.setCredentialId(credentialId)
            			.setStatusListId(statusListId)
            			.setSchemaUri(URI.create(schemaId))
            			.setExpirationTimeStr(expirationDateStr)
            			.build();
            	pair = credentialTransferPrepareTask.start();
            	vc = pair.getA();
	            disclosureList = pair.getB();

            }else {
            	
	            CredentialPrepareTask credentialPrepareTask = new CredentialPrepareTask.Builder()
	                .setHolderDid(holderDid)
	                .setHolderData(holderData)
	                .setStatusListIndex(statusListIndex)
	                .setPolicy(policy)
	                .setIssuerId(issuerId)
	                .setCredentialId(credentialId)
	                .setStatusListId(statusListId)
	                .setSchemaUri(schemaUri)
	                .setSelectedIssuanceDate(selectedIssuanceDate)
	                .setSelectedExpirationDate(selectedExpirationDate)
	                .setStatusListIdSuspension(statusListIdSuspension)
	                .build();
	            pair = credentialPrepareTask.start();
	            vc = pair.getA();
	            disclosureList = pair.getB();
            }
            Console.showJson(vc.toJson(), "SD VC JSON");
            LOGGER.info("disclosures");
            disclosureList.forEach(d -> LOGGER.info(d.getJson()));

            // step 4.3: sign credential
            CredentialSignTask credentialSignTask = new CredentialSignTask.Builder()
                .setVerifiableCredential(vc)
                .setDisclosureList(disclosureList)
                .setHolderPublicKey(holderPublicKeyFromDid)
                .setIssuerKey(issuerKey)
                .setPublicKeysUri(publicKeysUri)
                .build();
            String sdJwtVc = credentialSignTask.start();
            LOGGER.info("\nSD-JWT VC (final) = {}\n", sdJwtVc);

            // step 4.4: validate credential
            CredentialValidateTask credentialValidateTask = new CredentialValidateTask.Builder()
                .setCredentialType(credentialType)
                .setCredential(sdJwtVc)
                .setIssuerKey(issuerKey)
                .build();
            CredentialValidateTask.Result cvtResult = credentialValidateTask.start();
            LOGGER.info("credential validate task result = {}", cvtResult.toString());

            // step 5: prepare、sign、validate、store status list
            OperateStatusListRequestDTO operateStatusListRequest = new OperateStatusListRequestDTO()
					.setOpType(Definition.STATUS_LIST_GEN_VC)
					.setCredentialType(credentialType)
					.setStatusListType(Definition.StatusListType.revocation.name())
					.setGid(gid)
					.setIssuerDid(issuerDid)
					.setStatusListIndex(statusListIndex)
					.setVcStatus(VcStatus.ACTIVE);
			String jwtSvc = statusListService.operateStatusList(operateStatusListRequest);
			
			// step 5.1: prepare、sign、validate、store status list (suspension)
			OperateStatusListRequestDTO operateStatusListSuspensionRequest = new OperateStatusListRequestDTO()
					.setOpType(Definition.STATUS_LIST_GEN_VC)
					.setCredentialType(credentialType)
					.setStatusListType(Definition.StatusListType.suspension.name())
					.setGid(gid)
					.setIssuerDid(issuerDid)
					.setStatusListIndex(statusListIndex)
					.setVcStatus(VcStatus.ACTIVE);
			String jwtSvcSuspension = statusListService.operateStatusList(operateStatusListSuspensionRequest);

            // step 6: store new credential to database
			// step 6.1: remove personal information from sdJwtVc
			// re-construct JWT verifiable credential
            SDJWT sdJwt = SDJWT.parse(sdJwtVc);
            String credentialJwt = sdJwt.getCredentialJwt();
			
            Credential credential = new Credential()
                .setCid(cid)
                .setCredentialType(credentialType)
                .setCredentialSubjectId(holderDid)

                // TODO: use UTC time or local time (UTC+8) ?
                .setIssuanceDate(Timestamp.from(vc.getIssuanceDate().toInstant()))
                .setExpirationDate(Timestamp.from(vc.getExpirationDate().toInstant()))

                .setContent(credentialJwt)
                .setTicketNumber(ticket.getTicketNumber())
                .setLastUpdateTime(Timestamp.from(new Date().toInstant()))
                .setCredentialStatus(VcStatus.ACTIVE.getValue())
                .setNonce(nonce);
            credential = credentialRepository.save(credential);
            if (!cid.equals(credential.getCid())) {
                throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store credential");
            }

            // step 6.1: if it is VC transfer, revoke old VC
            if(isVcTransfer) {
            	CredentialStatusResponseDTO credentialStatusResponse = JsonUtils.jsToVo(callRevokeService(credentialTransferEntity.getOldCid()), CredentialStatusResponseDTO.class);
            	// check response
            	if(credentialStatusResponse == null || !credentialStatusResponse.validate()) {
            		throw new VcException(VcException.ERR_CRED_REVOKE_SERVICE_RETURN_ERROR, "invalid revoke vc response");
            	}
            	Console.showJson(credentialStatusResponse.toString(), "revoke service response");

            }

            // step 8: update credential data status/credential transfer status
            if(isVcTransfer) {
            	// update credential data status, then delete it
            	credentialDataEntity.setStatus(DataStatus.USED.getValue());
	            credentialDataEntity = credentialDataRepository.save(credentialDataEntity);
	            if(!DataStatus.USED.getValue().equals(credentialDataEntity.getStatus())) {
	            	throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update credential data status");
	            }
				credentialDataRepository.deleteById(credentialDataEntity.getTransactionID());

	            // update credential transfer status, then delete it
	            credentialTransferEntity.setStatus(DataStatus.USED.getValue());
	            credentialTransferEntity = credentialTransferRepository.save(credentialTransferEntity);
	            if(!DataStatus.USED.getValue().equals(credentialTransferEntity.getStatus())) {
	            	throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update credential transfer status");
	            }
				credentialTransferRepository.deleteById(credentialTransferEntity.getTransactionID());
            }else {
	            if(vcDataSource == 501) {
		            credentialDataEntity.setStatus(DataStatus.USED.getValue());
		            credentialDataEntity = credentialDataRepository.save(credentialDataEntity);
		            if(!DataStatus.USED.getValue().equals(credentialDataEntity.getStatus())) {
		            	throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update credential data status");
		            }
					credentialDataRepository.deleteById(credentialDataEntity.getTransactionID());
	            }
            }


            result = new CredentialResponseDTO().setCredential(sdJwtVc).toString();
            httpStatus = HttpStatus.CREATED;

        } catch (VcException e) {

        	if(e.getCode() == VcException.ERR_DB_QUERY_ERROR)
        		LOGGER.warn(e.getMessage(), e);
        	else
        		LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_GENERATE_VC_ERROR, "fail to generate vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwissuer-106i] revoke credential
     *
     * @param cid credential identifier, ex: 99928d83-f1c1-4eb8-935a-9a165e15d654
     * @return (credential status response, http status)
     */
    public Tuple.Pair<String, HttpStatus> revoke(String cid) {

        String result;
        HttpStatus httpStatus;

        try {
        	// step 0: check issuer's DID
        	String issuerDid = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_DID);
            if(issuerDid == null || issuerDid.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR, "issuer has not yet registered a DID");
            }

            // step 1: validate request
            if (cid == null || cid.isBlank()) {
                throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_ID, "invalid credential id");
            }

            // step 2: query credential
            Credential credential = credentialRepository.findByCid(cid);
            if (credential == null) {
                throw new VcException(VcException.ERR_CRED_CREDENTIAL_NOT_FOUND, "credential not found");
            }

            String credentialStatus = credential.getCredentialStatus();
            
            if (VcStatus.ACTIVE.equals(VcStatus.getByValue(credentialStatus)) ||
            		VcStatus.SUSPENDED.equals(VcStatus.getByValue(credentialStatus))) {
            	// continue revoke process
            	;
            } else if (VcStatus.REVOKED.equals(VcStatus.getByValue(credentialStatus))) {
                // credential is already revoked
                result = new CredentialStatusResponseDTO().setCredentialStatus(VcStatus.REVOKED.name()).toString();
                httpStatus = HttpStatus.OK;
                return Tuple.collect(result, httpStatus);
            } else {
            	throw new VcException(VcException.ERR_CRED_CREDENTIAL_STATUS_UNKNOWN_ERROR, "credential status unknown");
            }

            String credentialType = credential.getCredentialType();
            int ticketNumber = credential.getTicketNumber();
            Tuple.Pair<Integer, Integer> tnPair = calculateFromTicketNumber(ticketNumber);
            int gid = tnPair.getA();
            int statusListIndex = tnPair.getB();

            // step 3: prepare、sign、validate、store status list
            OperateStatusListRequestDTO operateStatusListRequest = new OperateStatusListRequestDTO()
					.setOpType(Definition.STATUS_LIST_REVOKE_VC)
					.setCredentialType(credentialType)
					.setStatusListType(Definition.StatusListType.revocation.name())
					.setGid(gid)
					.setIssuerDid(issuerDid)
					.setStatusListIndex(statusListIndex)
					.setVcStatus(VcStatus.REVOKED);
			String jwtSvc = statusListService.operateStatusList(operateStatusListRequest);
			
			// step 3.1: if the VC is suspended, need to modify status list for suspension
			if (VcStatus.SUSPENDED.equals(VcStatus.getByValue(credentialStatus))) {
				OperateStatusListRequestDTO operateSuspensionStatusListRequest = new OperateStatusListRequestDTO()
						.setOpType(Definition.STATUS_LIST_RECOVER_VC)
						.setCredentialType(credentialType)
						.setStatusListType(Definition.StatusListType.suspension.name())
						.setGid(gid)
						.setIssuerDid(issuerDid)
						.setStatusListIndex(statusListIndex)
						.setVcStatus(VcStatus.ACTIVE);
				jwtSvc = statusListService.operateStatusList(operateSuspensionStatusListRequest);
				// delete suspend vc record from DB
				Optional<CredentialSuspensionEntity> credentialSuspensionEntity = credentialSuspensionRepository.findByCid(cid);
				if(credentialSuspensionEntity.isEmpty()) {
		        	throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get suspension record from DB");
		        } else {
		        	credentialSuspensionRepository.deleteById(cid);
		        }
			}

            // step 4: update credential status
            int count = credentialRepository.updateStatusByCid(cid, VcStatus.REVOKED.getValue(), Timestamp.valueOf(LocalDateTime.now()));
            if (count != 1) {
                throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update credential status");
            }

            result = new CredentialStatusResponseDTO().setCredentialStatus(VcStatus.REVOKED.name()).toString();
            httpStatus = HttpStatus.OK;

        } catch (VcException e) {

        	if(e.getCode() == VcException.ERR_CRED_CREDENTIAL_NOT_FOUND)
        		LOGGER.warn(e.getMessage(), e);
        	else
        		LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_REVOKE_VC_ERROR, "fail to revoke vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwissuer-108i] query credential
     *
     * @param cid credential identifier, ex: 99928d83-f1c1-4eb8-935a-9a165e15d654
     * @return (credential response, http status)
     */
    public Tuple.Pair<String, HttpStatus> query(String cid) {

        String result;
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            // step 1: validate request
            if (cid == null || cid.isBlank()) {
                throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_ID, "invalid credential id");
            }

            // step 2: query credential
            Credential credential = credentialRepository.findByCid(cid);
            if (credential == null) {
                throw new VcException(VcException.ERR_CRED_CREDENTIAL_NOT_FOUND, "credential not found");
            }

            // step 2: extract content
            String content = credential.getContent();
            result = new CredentialResponseDTO().setCredential(content).toString();

        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_QUERY_VC_ERROR, "fail to query vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwissuer-108i] query credential by nonce
     *
     * @param nonce nonce, ex: 99928d83-f1c1-4eb8-935a-9a165e15d654
     * @return (credential response, http status)
     */
    public Tuple.Pair<String, HttpStatus> queryByNonce(String nonce) {

        String result;
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            // step 1: validate request
            if (nonce == null || nonce.isBlank()) {
                throw new VcException(VcException.ERR_CRED_INVALID_NONCE, "invalid nonce");
            }

            // step 2: query credential
            Credential credential = credentialRepository.findTopByNonceOrderByIssuanceDateDesc(nonce);
            if (credential == null) {
                throw new VcException(VcException.ERR_CRED_CREDENTIAL_NOT_FOUND, "credential not found");
            }

            // step 2: extract content
            String content = credential.getContent();
            result = new CredentialResponseDTO().setCredential(content).toString();

        } catch (VcException e) {

        	if(e.getCode() == VcException.ERR_CRED_CREDENTIAL_NOT_FOUND)
        		LOGGER.warn(e.getMessage());
        	else
        		LOGGER.error(e.getMessage());
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_QUERY_VC_ERROR, "fail to query vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwissuer-107i] query all credential
     *
     * @param page page
     * @param size page size
     * @param direction sorting direction, default by `issuance_date`
     * @return (credential response, http status)
     */
    public Tuple.Pair<String, HttpStatus> queryAll(String credentialType, int page, int size, String direction) {

        String result;
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            // credential type
            if (credentialType == null || credentialType.isBlank()) {
                throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_TYPE, "invalid credential type (" + credentialType + ")");
            }

            // page
            if (page < 0) {
                page = 0;
            }

            // size
            if (size <= 0) {
                size = 10;
            }

            // sort & direction
            String sortBy = "issuance_date";
            Sort sort;
            try {
                sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
            } catch (Exception e) {
                sort = Sort.by(Sort.Direction.ASC, sortBy);
            }

            Pageable pageable = PageRequest.of(page, size, sort);

            // step 1: query credential (order by issuance date desc)
            // List<Credential> credentialList = credentialRepository.findAllByOrderByIssuanceDateDesc();
            Page<Credential> credentialPage = credentialRepository.findAllByPages(credentialType, pageable);
            int totalPages = credentialPage.getTotalPages();

            List<Credential> credentialList = credentialPage.getContent();
            if (credentialList.isEmpty()) {
                throw new VcException(VcException.ERR_CRED_CREDENTIAL_NOT_FOUND, "credential not found");
            }

            // step 2: extract content
            List<CredentialListResponseDTO.CredentialInfo> credentialInfoList = credentialList.stream()
                .filter(Objects::nonNull)
                .map(CredentialListResponseDTO.CredentialInfo::new)
                .toList();
            result = new CredentialListResponseDTO().setCredentials(credentialInfoList)
            		.setTotalPages(totalPages)
            		.toString();

        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_QUERY_VC_ERROR, "fail to query vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwissuer-204i] transfer credential
     *
     * @param cid cid
     * @param credentialTransferRequest credential transfer request
     * @return (QRCode, http status)
     */
    public Tuple.Pair<String, HttpStatus> transfer(String cid, CredentialTransferRequestDTO credentialTransferRequest) {
    	String result;
        HttpStatus httpStatus;

        try {

        	// step 0: check issuer's key && issuer's Did
        	String issuerKeyInDB = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_KEY);
            if(issuerKeyInDB == null || issuerKeyInDB.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR, "issuer has not yet registered a DID");
            }
            
            // step 0.1: if issuer key is encrypted, decrypt it
            String issuerKey = null;
            try {
            	if(!KeyUtils.isPossiblyJwk(issuerKeyInDB)) {
            		issuerKey = KeyUtils.GCMDecrypt(issuerKeyInDB, vcKeyEnc);
            	}
            	else
            		issuerKey = issuerKeyInDB;
            }catch(Exception e) {
            	LOGGER.error(e.getMessage(), e);
            	throw new VcException(VcException.ERR_SYS_DECRYPT_CIPHER_ERROR, "Failed to decrypt key cipher");
            }
            if(issuerKey == null || issuerKey.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_DECRYPT_RESULT_NULL_ERROR, "The decrypted value is empty or null");
            }

            String issuerDid = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_DID);
            if(issuerDid == null || issuerDid.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR, "issuer has not yet registered a DID");
            }

            // check issuer's Did status
            DidGetIssuerInfoResponseDTO didGetIssuerInfoResponseDTO = callFrontendToGetIssuerInfo(issuerDid);
            Console.showJson(didGetIssuerInfoResponseDTO.toString(), "Get issuer's DID infomation response");

            // check content
            int code = didGetIssuerInfoResponseDTO.getCode();
            DidGetIssuerInfoResponseDTO.Data data = didGetIssuerInfoResponseDTO.getData();
            int didStatus = Optional.ofNullable(data)
            		.map(DidGetIssuerInfoResponseDTO.Data::getStatus)
            		.orElse(-1);

            if(code != 0 || data == null || didStatus == -1) {
            	String errorMessage = "fail to get issuer's DID info. (code = " + code + ")";
            	throw new VcException(VcException.ERR_DID_FRONTEND_GET_ISSUER_DID_INFO_ERROR, errorMessage);
            }
            // 1: 代表Did有效
            if(didStatus != 1) {
            	throw new VcException(VcException.ERR_SYS_ISSUER_DID_NOT_VALID_ERROR, "issuer's Did is not VALID");
            }

        	if(cid == null || cid.isBlank()) {
        		throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_ID, "invalid credential id");
        	}

        	if(credentialTransferRequest == null || !credentialTransferRequest.validate()) {
        		throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_TRANSFER_REQUEST, "invalid credential transfer request");
        	}

        	// step 1: 驗證vp
        	List<String> validateRequest = new ArrayList<>();
        	validateRequest.add(credentialTransferRequest.getVp());

        	VpValidationResponseDTO vpValidationResponse = callVpToValidate(validateRequest);

        	// check response
        	if(vpValidationResponse == null || !vpValidationResponse.validate()) {
        		throw new VcException(VcException.ERR_CRED_VP_RETURN_ERROR, "invalid vp validation response");
        	}
        	Console.showJson(vpValidationResponse.toString(), "Vp validation response");

        	// step 2: 取得舊卡欄位資料
        	VpValidationResponseDTO.Vcs vcs = vpValidationResponse.getVcs().get(0);
        	if(vcs == null || !vcs.validate()) {
        		throw new VcException(VcException.ERR_CRED_VP_RETURN_VCS_ERROR, "invalid vp validation response(vcs)");
        	}

        	VpValidationResponseDTO.Vcs.Credential transCredential = vcs.getCredential();

        	// step 3: 產製nonce，在credential_transfer資料表建立一筆DB record，記錄此筆nonce的genVC是手機移轉
        	String nonce = UUID.randomUUID().toString();

        	// 取得credentialType
        	if(transCredential.getType().size() < 2) {
        		throw new VcException(VcException.ERR_CRED_VP_RETURN_TYPE_ERROR, "invalid vp validation response(type)");
        	}
        	String credentialType = transCredential.getType().get(1);

        	// credentialType中不包含"_"，報錯
    		if(credentialType.split("_").length == 1) {
    			throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_TYPE, "invalid credentialType");
    		}
    		String businessld = credentialType.split("_")[0];
    		LOGGER.info("""
                    [credential transfer request]::
                    nonce = {}
                    credentialType = {}
                    businessId = {}
                    """,
                    nonce, credentialType, businessld);

    		// 取得credential_issuer_identifier，ID_Token中會用到
    		Optional<CredentialIssuerConfigEntity> credentialIssuerConfigEntity = credentialIssuerConfigRepository.findByVcID(businessld);
    		if(credentialIssuerConfigEntity.isEmpty()) {
    			throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get credential_issuer_identifier from DB");
    		}

    		String credentialIssuerIdentifier = credentialIssuerConfigEntity.get().getCredentialIssuerIdentifier();
    		if(credentialIssuerIdentifier == null || credentialIssuerIdentifier.isBlank()) {
    			throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_ISSUER_IDENTIFIER, "invalid credential_issuer_identifier");
    		}

        	// 檢查是否允許進行手機移轉作業
        	String funcSwitchJson = credentialPolicyRepository.queryFuncSwitchByCredentialType(credentialType);
        	if(funcSwitchJson == null || funcSwitchJson.isBlank()) {
        		throw new VcException(VcException.ERR_SYS_NOT_SET_FUNCTION_SWITCH_YET_ERROR, "issuer has not yet set the func_switch column");
        	}
        	Map<String, Object> funcSwitchMap = JsonUtils.jsToMap(funcSwitchJson);
        	if(!funcSwitchMap.containsKey("enable_vc_transfer")) {
        		throw new VcException(VcException.ERR_SYS_NOT_SET_FUNCTION_SWITCH_YET_ERROR, "issuer has not yet set the func_switch column");
        	}else {
        		if(!funcSwitchMap.get("enable_vc_transfer").equals(true)) {
        			throw new VcException(VcException.ERR_CRED_TRANSFER_VC_NOT_ALLOWED_ERROR, "VC transfer is not allowed");
        		}

        		// 檢查 enable_tx_code 與 enable_vc_transfer，不能都是true
        		if(Boolean.TRUE.equals(funcSwitchMap.get("enable_tx_code")) && Boolean.TRUE.equals(funcSwitchMap.get("enable_vc_transfer"))) {
        			throw new VcException(VcException.ERR_CRED_TX_CODE_AND_VC_TRANSFER_BOTH_TRUE_ERROR, "enable_tx_code and enable_vc_transfer cannot both be true");
        		}
        	}

        	// 新建一筆record
        	CredentialTransferEntity credentialTransferEntity = new CredentialTransferEntity();
        	credentialTransferEntity.setTransactionID(nonce);
        	credentialTransferEntity.setCredentialType(credentialType);
        	credentialTransferEntity.setData(JsonUtils.voToJs(transCredential));
        	LocalDateTime issuanceTime = LocalDateTime.now();
        	credentialTransferEntity.setCreatedTime(new Timestamp(DateUtils.toDate(issuanceTime).getTime()));
        	// 10分鐘後過期
        	LocalDateTime expirationTime = DateUtils.calculate(issuanceTime, TimeUnit.MINUTE, 10);
        	credentialTransferEntity.setExpiredTime(new Timestamp(DateUtils.toDate(expirationTime).getTime()));
        	credentialTransferEntity.setStatus(DataStatus.VALID.getValue());
        	credentialTransferEntity.setOldCid(cid);

        	credentialTransferEntity = credentialTransferRepository.save(credentialTransferEntity);

        	if(!nonce.equals(credentialTransferEntity.getTransactionID())) {
        		throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store credential transfer data");
        	}

        	// step 4: 呼叫vc-501i寫入證件資料
        	CredentialDataRequestDTO credentialDataRequest = new CredentialDataRequestDTO();
        	credentialDataRequest.setTransactionId(nonce);
        	credentialDataRequest.setCredentialType(credentialType);
        	// 需要先把CredentialSubject中的id欄位拿掉(id是預設欄位)
        	LinkedHashMap<String, Object> credentialSubject = transCredential.getCredentialSubject();
        	if(!credentialSubject.containsKey("id")) {
        		throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_SUBJECT, "invalid credential subject");
        	}
        	// get holderDid from CredentialSubject
        	String holderDid = credentialSubject.get("id").toString();
        	// 刪除id欄位
        	credentialSubject.remove("id");
        	LOGGER.info("""
                    [credential transfer request]::
                    holderDid = {}
                    CredentialSubject(remove id) = {}
                    """,
                    holderDid, credentialSubject);
        	credentialDataRequest.setData(credentialSubject);

        	CredentialDataResponseDTO credentialDataResponse = JsonUtils.jsToVo(callCredentialDataService(credentialDataRequest), CredentialDataResponseDTO.class);

        	// check response
        	if(credentialDataResponse == null || !credentialDataResponse.validate()) {
        		throw new VcException(VcException.ERR_CRED_CRED_DATA_SERVICE_RETURN_ERROR, "invalid credential data response");
        	}
        	Console.showJson(credentialDataResponse.toString(), "credential data service response");

        	if(!nonce.equals(credentialDataResponse.getTransactionId())) {
        		throw new VcException(VcException.ERR_CRED_CRED_DATA_SERVICE_RETURN_ERROR, "fail to set credential data");
        	}

        	// step 5: 打包id_token
        	// 將holderDid雜湊後，作為sub
        	String sub = HashUtils.getSha256Hex(holderDid);
        	String idToken = genIdToken(sub, credentialType, credentialIssuerIdentifier, nonce, issuerKey);
        	LOGGER.info("""
                    [credential transfer request]::
                    sub = {}
                    credentialIssuerIdentifier = {}
                    """,
                    sub, credentialIssuerIdentifier);

        	// step 6: 呼叫oidvci-101i取得QRCode
        	OidvciQrcodeRequestDTO oidvciQrcodeRequest = new OidvciQrcodeRequestDTO(true, idToken);
        	OidvciQrcodeResponseDTO oidvciQrcodeResponse = callOidvciToGenQrcode(oidvciQrcodeRequest, businessld);
        	// check response
        	if(oidvciQrcodeResponse == null || !oidvciQrcodeResponse.validate()) {
        		throw new VcException(VcException.ERR_CRED_OIDVCI_RETURN_ERROR, "invalid oidvci qrcode response");
        	}
        	Console.showJson(oidvciQrcodeResponse.toString(), "Oidvci qrcode response");

        	// step 7: 回傳QRCode
        	result = new CredentialTransferResponseDTO(oidvciQrcodeResponse.getQrCode(), oidvciQrcodeResponse.getLink()).toString();
            httpStatus = HttpStatus.OK;

        } catch (VcException e) {

       		LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_TRANSFER_VC_ERROR, "fail to transfer vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }
    
    /**
     * suspend credential
     *
     * @param cid credential identifier, ex: 99928d83-f1c1-4eb8-935a-9a165e15d654
     * @return (credential status response, http status)
     */
    public Tuple.Pair<String, HttpStatus> suspend(String cid) {

        String result;
        HttpStatus httpStatus;

        try {
        	// step 0: check issuer's DID
        	String issuerDid = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_DID);
            if(issuerDid == null || issuerDid.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR, "issuer has not yet registered a DID");
            }

            // step 1: validate request
            if (cid == null || cid.isBlank()) {
                throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_ID, "invalid credential id");
            }

            // step 2: query credential
            Credential credential = credentialRepository.findByCid(cid);
            if (credential == null) {
                throw new VcException(VcException.ERR_CRED_CREDENTIAL_NOT_FOUND, "credential not found");
            }
            
            // 檢查是否啟用自動復用、若未啟用則使用預設值suspension_duration:999，suspension_duration_unit:YEAR
            int suspensionDuration = 999;
            String suspensionDurationUnit = "YEAR";
        	String funcSwitchJson = credentialPolicyRepository.queryFuncSwitchByCredentialType(credential.getCredentialType());
        	if(funcSwitchJson == null || funcSwitchJson.isBlank()) {
        		// use default value
        		;
        	} else {
        		Map<String, Object> funcSwitchMap = JsonUtils.jsToMap(funcSwitchJson);
            	if(!funcSwitchMap.containsKey("enable_auto_recover")) {
            		// use default value
            		;
            	}else {
            		if(!funcSwitchMap.get("enable_auto_recover").equals(true)) {
            			// use default value
            			;
            		} else {
            			suspensionDuration = funcSwitchMap.containsKey("suspension_duration") 
            					? Optional.ofNullable(funcSwitchMap.get("suspension_duration"))
            							.filter(v -> v instanceof Integer)
            							.map(v -> (Integer) v)
            							.orElseThrow(() -> new VcException(VcException.ERR_SYS_NOT_SET_FUNCTION_SWITCH_YET_ERROR, "suspension_duration is invalid"))
            					: 999;
            			suspensionDurationUnit = funcSwitchMap.containsKey("suspension_duration_unit")
            					? Optional.ofNullable(funcSwitchMap.get("suspension_duration_unit"))
            							.filter(v -> v instanceof String)
            							.map(v -> ((String) v).trim())
            							.filter(s -> !s.isEmpty())
            							.orElseThrow(() -> new VcException(VcException.ERR_SYS_NOT_SET_FUNCTION_SWITCH_YET_ERROR, "suspension_duration_unit is invalid"))
            					: "YEAR";
            			
            			// 檢查設定值是否合理
            			if(!isValidTimeUnit(suspensionDurationUnit)) {
            				throw new VcException(VcException.ERR_SYS_INVALID_TIME_UNIT, "invalid time unit");
            			}
            			
            			if(!checkValidityPeriod(suspensionDurationUnit, suspensionDuration)) {
            				throw new VcException(VcException.ERR_SYS_INVALID_TIME_DURATION, "invalid time duration");
            			}
            		}
            	}
        	}

            String credentialStatus = credential.getCredentialStatus();
            if (VcStatus.ACTIVE.equals(VcStatus.getByValue(credentialStatus))) {
            	;
            } else if (VcStatus.SUSPENDED.equals(VcStatus.getByValue(credentialStatus))) {
            	LOGGER.warn("[credential suspend request]:: credential is already suspended");
            	result = new CredentialStatusResponseDTO().setCredentialStatus(VcStatus.SUSPENDED.name()).toString();
                httpStatus = HttpStatus.OK;
                return Tuple.collect(result, httpStatus);
            } else if (VcStatus.REVOKED.equals(VcStatus.getByValue(credentialStatus))) {
                // credential is already revoked
            	throw new VcException(VcException.ERR_CRED_REVOKED_CRED_CANNOT_BE_SUSPENDED_ERROR, "revoked credential cannot be suspeneded");
            } else {
            	throw new VcException(VcException.ERR_CRED_CREDENTIAL_STATUS_UNKNOWN_ERROR, "credential status unknown");
            }

            String credentialType = credential.getCredentialType();
            int ticketNumber = credential.getTicketNumber();
            Tuple.Pair<Integer, Integer> tnPair = calculateFromTicketNumber(ticketNumber);
            int gid = tnPair.getA();
            int statusListIndex = tnPair.getB();

            // step 3: prepare、sign、validate、store status list
            OperateStatusListRequestDTO operateStatusListRequest = new OperateStatusListRequestDTO()
					.setOpType(Definition.STATUS_LIST_SUSPEND_VC)
					.setCredentialType(credentialType)
					.setStatusListType(Definition.StatusListType.suspension.name())
					.setGid(gid)
					.setIssuerDid(issuerDid)
					.setStatusListIndex(statusListIndex)
					.setVcStatus(VcStatus.SUSPENDED);
			String jwtSvc = statusListService.operateStatusList(operateStatusListRequest);
			
			// step 4: add suspend vc record to DB
			LocalDateTime suspensionBeginTime = LocalDateTime.now();
			LocalDateTime expectedRecoveryTime = DateUtils.calculate(suspensionBeginTime, TimeUnit.valueOf(suspensionDurationUnit.toUpperCase()), suspensionDuration);
			
			CredentialSuspensionEntity credentialSuspensionEntity = new CredentialSuspensionEntity()
					.setCid(cid)
					.setCredentialType(credentialType)
					.setSuspensionBeginTime(new Timestamp(DateUtils.toDate(suspensionBeginTime).getTime()))
					.setSuspensionDuration(suspensionDuration)
					.setSuspensionDurationUnit(suspensionDurationUnit)
					.setExpectedRecoveryTime(new Timestamp(DateUtils.toDate(expectedRecoveryTime).getTime()));
			credentialSuspensionEntity = credentialSuspensionRepository.save(credentialSuspensionEntity);
			if (!cid.equals(credentialSuspensionEntity.getCid())) {
                throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store credential suspension record");
            }

            // step 5: update credential status
            int count = credentialRepository.updateStatusByCid(cid, VcStatus.SUSPENDED.getValue(), Timestamp.valueOf(LocalDateTime.now()));
            if (count != 1) {
                throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update credential status");
            }

            result = new CredentialStatusResponseDTO().setCredentialStatus(VcStatus.SUSPENDED.name()).toString();
            httpStatus = HttpStatus.OK;

        } catch (VcException e) {

        	if(e.getCode() == VcException.ERR_CRED_CREDENTIAL_NOT_FOUND)
        		LOGGER.warn(e.getMessage(), e);
        	else
        		LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_SUSPEND_VC_ERROR, "fail to suspend vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }
    
    /**
     * recover credential
     *
     * @param cid credential identifier, ex: 99928d83-f1c1-4eb8-935a-9a165e15d654
     * @return (credential status response, http status)
     */
    public Tuple.Pair<String, HttpStatus> recover(String cid) {

        String result;
        HttpStatus httpStatus;

        try {
        	// step 0: check issuer's DID
        	String issuerDid = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_DID);
            if(issuerDid == null || issuerDid.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR, "issuer has not yet registered a DID");
            }

            // step 1: validate request
            if (cid == null || cid.isBlank()) {
                throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_ID, "invalid credential id");
            }

            // step 2: query credential
            Credential credential = credentialRepository.findByCid(cid);
            if (credential == null) {
                throw new VcException(VcException.ERR_CRED_CREDENTIAL_NOT_FOUND, "credential not found");
            }

            String credentialStatus = credential.getCredentialStatus();
            if (VcStatus.SUSPENDED.equals(VcStatus.getByValue(credentialStatus))) {
            	;
            } else if (VcStatus.ACTIVE.equals(VcStatus.getByValue(credentialStatus))) {
            	LOGGER.warn("[credential recover request]:: credential is already active");
            	result = new CredentialStatusResponseDTO().setCredentialStatus(VcStatus.ACTIVE.name()).toString();
                httpStatus = HttpStatus.OK;
                return Tuple.collect(result, httpStatus);
            } else if (VcStatus.REVOKED.equals(VcStatus.getByValue(credentialStatus))) {
                // credential is already revoked
            	throw new VcException(VcException.ERR_CRED_REVOKED_CRED_CANNOT_BE_RECOVERED_ERROR, "revoked credential cannot be recovered");
            } else {
            	throw new VcException(VcException.ERR_CRED_CREDENTIAL_STATUS_UNKNOWN_ERROR, "credential status unknown");
            }

            String credentialType = credential.getCredentialType();
            int ticketNumber = credential.getTicketNumber();
            Tuple.Pair<Integer, Integer> tnPair = calculateFromTicketNumber(ticketNumber);
            int gid = tnPair.getA();
            int statusListIndex = tnPair.getB();

            // step 3: prepare、sign、validate、store status list
            OperateStatusListRequestDTO operateStatusListRequest = new OperateStatusListRequestDTO()
					.setOpType(Definition.STATUS_LIST_RECOVER_VC)
					.setCredentialType(credentialType)
					.setStatusListType(Definition.StatusListType.suspension.name())
					.setGid(gid)
					.setIssuerDid(issuerDid)
					.setStatusListIndex(statusListIndex)
					.setVcStatus(VcStatus.ACTIVE);
			String jwtSvc = statusListService.operateStatusList(operateStatusListRequest);
			
			// step 4: delete suspend vc record from DB
			Optional<CredentialSuspensionEntity> credentialSuspensionEntity = credentialSuspensionRepository.findByCid(cid);
			if(credentialSuspensionEntity.isEmpty()) {
	        	throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get suspension record from DB");
	        } else {
	        	credentialSuspensionRepository.deleteById(cid);
	        }

            // step 5: update credential status
            int count = credentialRepository.updateStatusByCid(cid, VcStatus.ACTIVE.getValue(), Timestamp.valueOf(LocalDateTime.now()));
            if (count != 1) {
                throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update credential status");
            }

            result = new CredentialStatusResponseDTO().setCredentialStatus(VcStatus.ACTIVE.name()).toString();
            httpStatus = HttpStatus.OK;

        } catch (VcException e) {

        	if(e.getCode() == VcException.ERR_CRED_CREDENTIAL_NOT_FOUND)
        		LOGGER.warn(e.getMessage(), e);
        	else
        		LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_CRED_RECOVER_VC_ERROR, "fail to recover vc");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * get holder's data from DB
     *
     * @param transactionId transaction Id, credentialType associated credential type, ex: VirtualCardCredential
     * @return CredentialDataEntity
     * @throws VcException
     */
    private CredentialDataEntity getHolderDataFromDB(String transactionId, String credentialType) throws VcException{

    	Optional<CredentialDataEntity> credentialDataEntity = credentialDataRepository.findByTransactionIDAndCredentialType(transactionId, credentialType);
    	if(credentialDataEntity.isEmpty()) {
        	throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get holder's data from DB");
        }
    	else {
    		// 檢查Data可用性
    		String dataStatus = credentialDataEntity.get().getStatus();
    		Timestamp expiredTime = credentialDataEntity.get().getExpiredTime();
    		if(DataStatus.VALID.getValue().equals(dataStatus)) {
    			// 取得當前時間的 Timestamp
    	        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    			if(currentTime.after(expiredTime)){
    				credentialDataEntity.get().setStatus(DataStatus.EXPIRED.getValue());
    				credentialDataRepository.save(credentialDataEntity.get());
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_GENERATION_REQUEST, "holder's data has expired");
    			}
    			return credentialDataEntity.get();
    		}
    		else {
    			if(DataStatus.EXPIRED.getValue().equals(dataStatus)) {
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_GENERATION_REQUEST, "holder's data has expired");
    			}
    			else if(DataStatus.USED.getValue().equals(dataStatus)) {
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_GENERATION_REQUEST, "holder's data has already been used");
    			}
    			else {
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_GENERATION_REQUEST, "the status of holder's data is unclear");
    			}
    		}
    	}
    }

    /**
     * calculate gid and status list index from ticket number
     *
     * @param ticketNumber ticket number
     * @return (gid, status list index)
     */
    private Tuple.Pair<Integer, Integer> calculateFromTicketNumber(long ticketNumber) {

        // status list max size: 16 KB
        long tn = ticketNumber - 1;
        int statusListIndex = (int) (tn % (16 * 1024 * 8));
        int gid = (int)((tn - statusListIndex) / (16 * 1024 * 8));

        return Tuple.collect(gid, statusListIndex);
    }

//    /**
//     * get holder's data
//     *
//     * @param holderUid holder's uid
//     * @return holder's data
//     */
//    private Map<String, Object> getHolderData(String holderUid) {
//
//        // use `holderUid` to get holder data
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("id_number", "A128128128");
//        map.put("full_name", "黃青蛙");
//        map.put("address", "100台北市中正區忠孝西路一段66號");
//
//        return map;
//    }

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

    /**
     * load status list policy
     *
     * @param credentialType associated credential type, ex: StatusList2021Credential
     * @return policy
     */
    private Policy loadStatusListPolicy(String credentialType) {

        return new Policy()
            .setPid(UUID.randomUUID().toString())
            .setCredentialType(credentialType)
            .setSchemaName("")
            .setSchemaContent("")
            // effective duration: 7 days
            .setEffectiveDuration(new Policy.Duration(7, DateUtils.TimeUnit.DAY))
            .setSignatureAlgorithm("ES256");
    }

    /**
     * [dwpush-101i] call Push to notify
     *
     * @param request status update push request
     * @return status update push response
     * @throws VcException exception when fail to push to notify
     */
//    private PushNotifyStatusChangeResponseDTO callPushToNotify(PushNotifyStatusChangeRequestDTO request) throws VcException {
//
//        HttpUtils httpUtils = new HttpUtils.Builder()
//            .setUrl(preloadSetting.getUrlPushNotifyStatusChange())
//            .setAllowedHostnames(null)
//            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
//            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
//            .setEnableLog(preloadSetting.isHttpEnableLog())
//            .build();
//
//        // connect to push service
//        Tuple.Pair<Integer, String> response = httpUtils.postJson(request.toString());
//        int statusCode = response.getA();
//        String content = response.getB();
//
//        // check http status code and response content
//        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
//            String errorMessage = "fail to notify push service. (http status code, response content) = (" + statusCode + ", " + content + ")";
//            throw new VcException(VcException.ERR_CRED_PUSH_NOTIFY_ERROR, errorMessage);
//        }
//
//        return new PushNotifyStatusChangeResponseDTO(content);
//    }

    /**
     * [dwissuer-201i] call Demo to get holder's data
     *
     * @param request holder's data request
     * @return holder's data response
     * @throws VcException exception when fail to get holder's data
     */
    private GetHolderDataResponseDTO callDemoToGetHolderData(GetHolderDataRequestDTO request) throws VcException {

    	// get Authorization token
    	HttpHeaders headers = new HttpHeaders();
//    	headers = headerUtils.generateHeader(preloadSetting.getUrlGetToken(),
//    										preloadSetting.getHttpMaxConnectTimeout(),
//    										preloadSetting.getHttpMaxRetryTimes(), preloadSetting.isHttpEnableLog());
        headers.add("Access-Token", token);

        HttpUtils httpUtils = new HttpUtils.Builder()
            .setUrl(preloadSetting.getUrlDemoGetHolderData())
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog())
            .setHeader(headers.toSingleValueMap())
            .build();

        // connect to demo service
        Tuple.Pair<Integer, String> response = httpUtils.postJson(request.toString());
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage = "fail to get holder's data. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VcException(VcException.ERR_CRED_DEMO_GET_DATA_ERROR, errorMessage);
        }

        return new GetHolderDataResponseDTO(content);
    }

    /**
     * [dwfront-301i] call Frontend to get issuer's DID info
     *
     * @param issuer's DID
     * @return DID information response
     * @throws VcException exception when fail to get issuer's DID info
     */
    private DidGetIssuerInfoResponseDTO callFrontendToGetIssuerInfo(String issuerDid) throws VcException {

    	HttpUtils httpUtils = new HttpUtils.Builder()
    			.setUrl(preloadSetting.getUrlFrontendGetIssuerInfoDid() + issuerDid)
    			.setAllowedHostnames(null)
    			.setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
    			.setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
    			.setEnableLog(preloadSetting.isHttpEnableLog())
    			.build();

    	// connect to frontend service
    	Tuple.Pair<Integer, String> response = httpUtils.get();
    	int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
        	String errorMessage = "fail to get issuer's DID info. (http status code, response content) = (" + statusCode + ", " + content + ")";
        	throw new VcException(VcException.ERR_DID_FRONTEND_GET_ISSUER_DID_INFO_ERROR, errorMessage);
        }

        return new DidGetIssuerInfoResponseDTO(content);
    }

    /**
     * get vc_data_source from baas DB
     *
     * @param credentialType associated credential type, ex: VirtualCardCredential
     * @return int
     * @throws VcException
     */
    private int getVcDataSourceFromDB(String credentialType) throws VcException{

    	String businessld = credentialType.split("_")[0];
    	int vcDataSource = 0;

    	// 查詢Org表格中是否存在發證機關(businessId)的紀錄
    	Optional<OrgEntity> orgEntity = orgRepository.findByOrgId(businessld);
    	if(orgEntity.isEmpty()) {
    		throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to get vc_data_source from org");
    	}
    	else {
    		vcDataSource = Optional.ofNullable(orgEntity.get().getVcDataSource()).orElse(0);
    		if(vcDataSource != 501 && vcDataSource != 901) {
    			throw new VcException(VcException.ERR_CRED_VC_DATA_SOURCE_NOT_SET, "issuer has not yet set the vc_data_source");
    		}
    	}
    	return vcDataSource;
    }

    /**
     * call vp service to validate vp
     *
     * @param vp
     * @return Vp validation response
     * @throws VcException exception when fail to call vp service
     */
    private VpValidationResponseDTO callVpToValidate(List<String> request) throws VcException {

        HttpUtils httpUtils = new HttpUtils.Builder()
            .setUrl(preloadSetting.getUrlVpValidation())
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog())
            .build();

        // connect to vp service
        Tuple.Pair<Integer, String> response = httpUtils.postJson(JsonUtils.voToJs(request));
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if(statusCode != HttpStatus.OK.value()) {
        	if(content == null || content.isBlank()) {
        		String errorMessage = "fail to connect vp service. (http status code, response content) = (" + statusCode + ", " + content + ")";
                throw new VcException(VcException.ERR_CRED_CONNECT_VP_ERROR, errorMessage);
        	}
        	VpValidationErrorResponseDTO vpValidationErrorResponse = new VpValidationErrorResponseDTO(content);
        	if(vpValidationErrorResponse == null || !vpValidationErrorResponse.validate()) {
        		throw new VcException(VcException.ERR_CRED_VP_RETURN_ERROR, "invalid vp validation error response");
        	}else {
        		throw new VcException(VcException.ERR_CRED_VP_INVALID_ERROR, "Vp validation result: invalid!");
        	}
        }

        if(content == null || content.isBlank()) {
    		String errorMessage = "fail to connect vp service. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VcException(VcException.ERR_CRED_CONNECT_VP_ERROR, errorMessage);
    	}

        List<VpValidationResponseDTO> listVpResponse = null;
        try {
        	listVpResponse = JsonUtils.jsToList(content, VpValidationResponseDTO.class);
        	if(listVpResponse == null || listVpResponse.isEmpty()) {
        		throw new VcException(VcException.ERR_CRED_VP_RESPONSE_CONVERT_ERROR, "Failed to convert JSON string into List");
        	}
        }catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        	throw new VcException(VcException.ERR_CRED_VP_RESPONSE_CONVERT_ERROR, "Failed to convert JSON string into List");
        }

        return new VpValidationResponseDTO(listVpResponse.get(0).toString());
    }

    /**
     * call vc-501i service to set data
     *
     * @param transactionId、credentialType、data
     * @return set data response String
     * @throws VcException exception when fail to call vc-501i service
     */
    private String callCredentialDataService(CredentialDataRequestDTO credentialDataRequest) throws VcException {

    	String transactionId = null;
		String credentialType = null;
		LinkedHashMap<String, Object> data = null;

		if (credentialDataRequest != null && credentialDataRequest.validate()) {
			transactionId = credentialDataRequest.getTransactionId();
			credentialType = credentialDataRequest.getCredentialType();
            data = credentialDataRequest.getData();
        }

		LOGGER.info("[callCredentialDataService]::transactionId = {}, credentialType = {}, data = {}", transactionId, credentialType, data);

        // call vc-501i service

		Tuple.Pair<String, HttpStatus> result = credentialDataService.putDataToDB(credentialDataRequest);
		LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[putData result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "put credential data response");

        // check http status code and response content
        if (result.getB() != HttpStatus.CREATED || result.getA() == null || result.getA().isBlank()) {
            String errorMessage = "fail to call vc-501i service. (http status code, response content) = (" + result.getB().toString() + ", " + result.getA() + ")";
            throw new VcException(VcException.ERR_CRED_CALL_CRED_DATA_SERVICE_ERROR, errorMessage);
        }

        return result.getA();
    }

    /**
     * call oidvci service to gen QRCode
     *
     * @param authenticated、id_token
     * @return Oidvci qrcode response
     * @throws VcException exception when fail to call oidvci service
     */
    private OidvciQrcodeResponseDTO callOidvciToGenQrcode(OidvciQrcodeRequestDTO oidvciQrcodeRequest, String businessId) throws VcException {

        HttpUtils httpUtils = new HttpUtils.Builder()
            .setUrl(preloadSetting.getUrlOidvciQrcode() + businessId + "/qr-code")
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog())
            .build();

        // connect to oidvci service
        Tuple.Pair<Integer, String> response = httpUtils.postJson(JsonUtils.voToJs(oidvciQrcodeRequest));
        int statusCode = response.getA();
        String content = response.getB();
        
        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage = "fail to connect oidvci service. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VcException(VcException.ERR_CRED_CONNECT_OIDVCI_ERROR, errorMessage);
        }

        return new OidvciQrcodeResponseDTO(content);
    }

    /**
     * Gen ID_Token for VC transfer
     *
     * @param sub、credentialType、iss、nonce、issuerKey
     * @return ID_Token
     * @throws VcException exception when fail to gen ID_Token
     */
    private String genIdToken(String sub, String credentialType, String iss, String nonce, String issuerKey) throws VcException {

    	try {
	    	Date iat = new Date();
			Date jwt_exp = new Date();
	    	long lt = iat.getTime();
			jwt_exp.setTime(lt + 600*1000);	// 10 minutes

			// prepare issuer's key
	        ECKey ecKey = ECKey.parse(issuerKey);
	        ECPrivateKey privateKey = ecKey.toECPrivateKey();

	        // 建立ID_Token JWT claims
	    	JWTClaimsSet claims = new JWTClaimsSet.Builder()
	    			.issuer(iss)
	    			.subject(sub)
	    			.audience("moda_dw")
	    			.expirationTime(jwt_exp)
	    			.issueTime(iat)
	    			.claim("nonce", nonce)
	    			.claim("credential_configuration_id", credentialType)
	    			.build();

	    	// 簽署ID_Token
	    	JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).type(JOSEObjectType.JWT).build();
	    	SignedJWT signedJWT = new SignedJWT(header, claims);
	    	signedJWT.sign(new ECDSASigner(privateKey));

	    	String jwtString = signedJWT.serialize();
	    	return jwtString;

    	} catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_CRED_SIGN_IDT_ERROR, "fail to sign ID_Token");
        }

    }

    /**
     * Check whether this case is vc transfer.
     *
     * @param transactionId ,credentialType
     * @return true/false
     * @throws VcException
     */
    private CredentialTransferEntity checkVcTransfer(String transactionId, String credentialType) throws VcException{

    	Optional<CredentialTransferEntity> credentialTransferEntity = credentialTransferRepository.findByTransactionIDAndCredentialType(transactionId, credentialType);
    	if(credentialTransferEntity.isEmpty()) {
        	return null;
        }
    	else {
    		// 檢查Data可用性
    		String dataStatus = credentialTransferEntity.get().getStatus();
    		Timestamp expiredTime = credentialTransferEntity.get().getExpiredTime();
    		if(DataStatus.VALID.getValue().equals(dataStatus)) {
    			// 取得當前時間的 Timestamp
    	        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    			if(currentTime.after(expiredTime)){
    				credentialTransferEntity.get().setStatus(DataStatus.EXPIRED.getValue());
    				credentialTransferRepository.save(credentialTransferEntity.get());
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_TRANSFER_REQUEST, "Transfer data has expired");
    			}
    			return credentialTransferEntity.get();
    		}
    		else {
    			if(DataStatus.EXPIRED.getValue().equals(dataStatus)) {
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_TRANSFER_REQUEST, "Transfer data has expired");
    			}
    			else if(DataStatus.USED.getValue().equals(dataStatus)) {
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_TRANSFER_REQUEST, "Transfer data has already been used");
    			}
    			else {
    				throw new VcException(VcException.ERR_CRED_INVALID_CREDENTIAL_TRANSFER_REQUEST, "the status of transfer data is unclear");
    			}
    		}
    	}
    }

    /**
     * call revoke service to revoke vc
     *
     * @param cid
     * @return credential status response String
     * @throws VcException exception when fail to call revoke service
     */
    private String callRevokeService(String cid) throws VcException {

		LOGGER.info("[callRevokeService]::cid = {}", cid);

        // call revoke service

		Tuple.Pair<String, HttpStatus> result = revoke(cid);
		LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[revoke result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "revoke vc response");

        // check http status code and response content
        if (result.getB() != HttpStatus.OK || result.getA() == null || result.getA().isBlank()) {
            String errorMessage = "fail to call revoke service. (http status code, response content) = (" + result.getB().toString() + ", " + result.getA() + ")";
            throw new VcException(VcException.ERR_CRED_CALL_REVOKE_SERVICE_ERROR, errorMessage);
        }

        return result.getA();
    }

    private Map<String, Object> didToJWK(String holderDid) throws VcException {
    	LOGGER.info("[didToJWK]::holderDid = {}", holderDid);
    	
    	try {
    		String prefix = "did:key:z";
    		if(holderDid.startsWith(prefix)) {
    			// 移除 prefix
    			String did = holderDid.substring(prefix.length());
    			
    			// base58 decode
    			byte[] didB58Decode = Base58.decode(did);
    			byte[] didB58DecodeRemovePrefix = null;
    			if(didB58Decode == null || didB58Decode.length < 3) {
    				throw new VcException(VcException.ERR_CRED_INVALID_DID_FORMAT, "holderDid format is invalid: base58 decode byte[] null or length < 3");
    			}
    			if ((didB58Decode[0] & 0xFF) == 0xD1 && (didB58Decode[1] & 0xFF) == 0xD6 && (didB58Decode[2] & 0xFF) == 0x03) {
    				// 移除前 3 byte
    				didB58DecodeRemovePrefix = new byte[didB58Decode.length - 3];
    				System.arraycopy(didB58Decode, 3, didB58DecodeRemovePrefix, 0, didB58DecodeRemovePrefix.length);
    				
    				String didJwk = new String(didB58DecodeRemovePrefix, "UTF-8");
    				LOGGER.info("[didToJWK]::didJwk = {}", didJwk);
    				return JsonUtils.jsToMap(didJwk);
    			}else {
    				throw new VcException(VcException.ERR_CRED_INVALID_DID_FORMAT, "holderDid format is invalid: base58 decode byte[] prefix");
    			}
    				
    		}else {
    			throw new VcException(VcException.ERR_CRED_INVALID_DID_FORMAT, "holderDid format is invalid: holderDid prefix");
    		}
    	} catch (VcException e) {
    		LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty errorResponseProperty = (ErrorResponseProperty) e;
    		throw new VcException(errorResponseProperty.getResponseCode(), errorResponseProperty.getResponseMessage());
    	} catch (Exception e) {
    		LOGGER.error(e.getMessage(), e);
        	throw new VcException(VcException.ERR_CRED_PARSE_DID_ERROR, "Failed to parse DID");
    	}
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

	// delete expired data from table credential_transfer
	@Transactional
	@Scheduled(cron = "#{@preloadSetting.getScheduleCronDeleteData()}")
	public void deleteExpiredData() {
		// 從DB setting獲取要刪除幾分鐘前的資料
		LocalDateTime nMinutesAgo = LocalDateTime.now().minusMinutes(Long.valueOf(preloadSetting.getDataDeleteNMinutesAgo()));
		LOGGER.info("nMinutesAgo:{}", nMinutesAgo);
		// 將LocalDateTime轉為Timestamp
		Timestamp nMinutesAgoTimestamp = Timestamp.valueOf(nMinutesAgo);
		// 刪除資料
		credentialTransferRepository.deleteByExpiredTime(nMinutesAgoTimestamp);
	}
}
