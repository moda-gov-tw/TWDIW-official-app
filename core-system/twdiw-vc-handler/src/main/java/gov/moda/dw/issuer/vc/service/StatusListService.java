package gov.moda.dw.issuer.vc.service;

import gov.moda.dw.issuer.vc.domain.StatusList;
import gov.moda.dw.issuer.vc.repository.SettingRepository;
import gov.moda.dw.issuer.vc.repository.StatusListRepository;
import gov.moda.dw.issuer.vc.service.dto.*;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidGetIssuerInfoResponseDTO;
import gov.moda.dw.issuer.vc.task.StatusListPrepareTask;
import gov.moda.dw.issuer.vc.task.StatusListSignTask;
import gov.moda.dw.issuer.vc.task.StatusListValidateTask;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.DateUtils;
import gov.moda.dw.issuer.vc.util.HttpUtils;
import gov.moda.dw.issuer.vc.util.KeyUtils;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.Definition;
import gov.moda.dw.issuer.vc.vo.Policy;
import gov.moda.dw.issuer.vc.vo.PreloadSetting;
import gov.moda.dw.issuer.vc.vo.Uris;
import gov.moda.dw.issuer.vc.vo.VcException;
import gov.moda.dw.issuer.vc.vo.VcStatus;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PessimisticLockingFailureException;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import com.nimbusds.jose.jwk.ECKey;

/**
 * status list service
 *
 * @version 20240902
 */
@Service
public class StatusListService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatusListService.class);
	private final SettingRepository settingRepository;
	private final PreloadSetting preloadSetting;
	private final Uris uris;
	private final StatusListRepository statusListRepository;
	
	@Value("${vc.schedule.statuslist.renew}")
    private String enableRenewStatusList;
	
	@Value("${vc.key.enc}")
    private String vcKeyEnc;

	public StatusListService(SettingRepository settingRepository, PreloadSetting preloadSetting, Uris uris,
			StatusListRepository statusListRepository) {
		this.settingRepository = settingRepository;
		this.preloadSetting = preloadSetting;
		this.uris = uris;
		this.statusListRepository = statusListRepository;
	}

	/**
	 * [dwissuer-105i] generate and renew status list
	 *
	 * @param credentialType associated credential type, ex: CitizenCredential;
	 *                       statusListType, ex:revocation; gid, ex:0
	 * @return (status list response, http status)
	 */
	public Tuple.Pair<String, HttpStatus> generateAndRenew(String credentialType, String statusListType, int gid) {

		String result;
		HttpStatus httpStatus;

		try {
			// TODO: inner functionality wait for implementing, only for admin portal &
			// scheduling
			// step 0: check issuer's Did
			String issuerDid = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_DID);
			if (issuerDid == null || issuerDid.isBlank()) {
				throw new VcException(VcException.ERR_SYS_NOT_REGISTER_DID_YET_ERROR,
						"issuer has not yet registered a DID");
			}

			// check issuer's Did status
			DidGetIssuerInfoResponseDTO didGetIssuerInfoResponseDTO = callFrontendToGetIssuerInfo(issuerDid);
			Console.showJson(didGetIssuerInfoResponseDTO.toString(), "Get issuer's DID infomation response");

			// check content
			int code = didGetIssuerInfoResponseDTO.getCode();
			DidGetIssuerInfoResponseDTO.Data data = didGetIssuerInfoResponseDTO.getData();
			int didStatus = Optional.ofNullable(data).map(DidGetIssuerInfoResponseDTO.Data::getStatus).orElse(-1);

			if (code != 0 || data == null || didStatus == -1) {
				String errorMessage = "fail to get issuer's DID info. (code = " + code + ")";
				throw new VcException(VcException.ERR_DID_FRONTEND_GET_ISSUER_DID_INFO_ERROR, errorMessage);
			}
			// 1: 代表Did有效
			if (didStatus != 1) {
				throw new VcException(VcException.ERR_SYS_ISSUER_DID_NOT_VALID_ERROR, "issuer's Did is not VALID");
			}

			// step 1: prepare、sign、validate、store status list
			OperateStatusListRequestDTO operateStatusListRequest = new OperateStatusListRequestDTO()
					.setOpType(Definition.STATUS_LIST_RENEW).setCredentialType(credentialType)
					.setStatusListType(statusListType).setGid(gid).setIssuerDid(issuerDid).setStatusListIndex(-1)
					.setVcStatus(null);
			String jwtSvc = operateStatusList(operateStatusListRequest);

			result = new StatusListResponseDTO().setStatusList(jwtSvc).toString();
			httpStatus = HttpStatus.CREATED;
		} catch (VcException e) {

			LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
			result = new ErrorResponseDTO(e1).toString();
			httpStatus = e.toHttpStatus();

		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			VcException vcException = new VcException(VcException.ERR_SL_GENERATE_STATUS_LIST_ERROR,
					"fail to generate status list");
			result = new ErrorResponseDTO(vcException).toString();
			httpStatus = vcException.toHttpStatus();

		}

		return Tuple.collect(result, httpStatus);
	}
	
	@Recover
    public String recover(PessimisticLockingFailureException e, OperateStatusListRequestDTO operateStatusListRequest) throws VcException, Exception {
        throw new VcException(VcException.ERR_SL_RETRY_ERROR, "no record available, over maximum retry times");
    }

	/**
	 * The entry point for operating the status list
	 *
	 * @param opType, Operation Type, ex:; credentialType associated credential
	 *                type, ex: CitizenCredential; statusListType, ex:revocation;
	 *                gld, ex:0; issuerDid; statusListIndex; vcStatus,
	 *                ex:VcStatus.ACTIVE
	 * @return status list
	 * @throws VcException exception when fail to operate the status list
	 */
//    public String operateStatusList(String opType, String credentialType, String statusListType, int gld,
//    								String issuerDid, int statusListIndex, VcStatus vcStatus) throws VcException, Exception {
	@Retryable(
			include = { PessimisticLockingFailureException.class },
			maxAttemptsExpression = "#{@preloadSetting.getStatusListMaxRetryTimes()}",
		    backoff = @Backoff(delayExpression = "#{@preloadSetting.getStatusListDelay()}", multiplier = 2)
		)
	@Transactional
	public String operateStatusList(OperateStatusListRequestDTO operateStatusListRequest)
			throws VcException, Exception {

		Policy policy = loadStatusListPolicy(Definition.DEFAULT_STATUS_LIST_CREDENTIAL_TYPE);
		URI contextUri = VerifiableCredentialContexts.JSONLD_CONTEXT_W3ID_VC_STATUS_LIST_2021_V1;
		URI statusListId;

		if (operateStatusListRequest == null || !operateStatusListRequest.validate()) {
			throw new VcException(VcException.ERR_SL_INVALID_STATUS_LIST_OPERATION_REQUEST,
					"invalid operate status list request");
		}

		String opType = operateStatusListRequest.getOpType();
		String credentialType = operateStatusListRequest.getCredentialType();
		String statusListType = operateStatusListRequest.getStatusListType();
		int gid = operateStatusListRequest.getGid();
		String issuerDid = operateStatusListRequest.getIssuerDid();
		int statusListIndex = operateStatusListRequest.getStatusListIndex();
		VcStatus vcStatus = operateStatusListRequest.getVcStatus();

		LOGGER.info("""
				[operate status list request]::
				opType = {}
				credentialType = {}
				statusListType = {}
				gid = {}
				issuerDid = {}
				statusListIndex = {}
				vcStatus = {}
				""", opType, credentialType, statusListType, gid, issuerDid, statusListIndex, vcStatus);

		try {
			statusListId = uris.generateStatusListId(credentialType,
					Definition.StatusListType.valueOf(statusListType).getTag().concat(String.valueOf(gid)));
		} catch (IllegalArgumentException e) {
			throw new VcException(VcException.ERR_SL_INPUT_STATUS_LIST_TYPE_ERROR, "status list type error");
		}


		URI statusListSubjectId = uris.generateStatusListSubjectId(statusListId);
		LOGGER.info("statusListId = {}", statusListId);
		StatusList statusList = null;
		try {
			statusList = statusListRepository.queryLatestByGldForUpdate(credentialType, statusListType, gid);
		} catch (PessimisticLockingFailureException e) {
			LOGGER.warn("目前資料行已被鎖，將由 Spring Retry 自動重試");
			throw new PessimisticLockingFailureException(e.getMessage(), e.getCause()); // 拋出給 Spring Retry 處理
		}
		
		String latestStatusList = null;

		if (statusList != null) {
			latestStatusList = statusList.getContent();
		} else {
			if (statusListIndex == 0 && Definition.STATUS_LIST_GEN_VC.equals(opType)) {
				LOGGER.info("new status list of group {} is created", gid);
			} else {
				throw new VcException(VcException.ERR_DB_QUERY_ERROR, "fail to query latest status list");
			}
		}

		URI issuerId = URI.create(issuerDid);
		URI publicKeysUri = uris.generatePublicKeysUri();

		// step 1: prepare status list
		StatusListPrepareTask statusListPrepareTask = new StatusListPrepareTask.Builder()
				.setLatestStatusList(latestStatusList)
				.setVcStatus(vcStatus)
				.setStatusListIndex(statusListIndex)
				.setPolicy(policy)
				.setIssuerId(issuerId)
				.setStatusListId(statusListId)
				.setStatusListSubjectId(statusListSubjectId)
				.setContextUri(contextUri)
				.setStatusListType(statusListType).build();
//			VerifiableCredential svc = statusListPrepareTask.start();
		Tuple.Pair<VerifiableCredential, LocalDateTime> prepareResult = statusListPrepareTask.start();
		VerifiableCredential svc = prepareResult.getA();
		LocalDateTime issuanceTime = prepareResult.getB();
		LOGGER.info("[Issuance Time]:: {}\n", issuanceTime);
		Console.showJson(svc.toJson(), "STATUS LIST JSON");

		// step 2: sign status list
		// step 2.1 if issuer key is encrypted, decrypt it
		String statusListKeyInDB = preloadSetting.getStatusListKey();
		String statusListKey = null;
		try {
        	if(!KeyUtils.isPossiblyJwk(statusListKeyInDB)) {
        		statusListKey = KeyUtils.GCMDecrypt(statusListKeyInDB, vcKeyEnc);
        	}
        	else
        		statusListKey = statusListKeyInDB;
        }catch(Exception e) {
        	LOGGER.error(e.getMessage(), e);
        	throw new VcException(VcException.ERR_SYS_DECRYPT_CIPHER_ERROR, "Failed to decrypt key cipher");
        }
        if(statusListKey == null || statusListKey.isBlank()) {
        	throw new VcException(VcException.ERR_SYS_DECRYPT_RESULT_NULL_ERROR, "The decrypted value is empty or null");
        }
		
		StatusListSignTask statusListSignTask = new StatusListSignTask.Builder()
				.setVerifiableCredential(svc)
				.setStatusListKey(statusListKey)
				.setPublicKeysUri(publicKeysUri).build();
		String jwtSvc = statusListSignTask.start();
		LOGGER.info("\nSTATUS LIST JWT (final) = {}\n", jwtSvc);

		// step 3: validate status list
		StatusListValidateTask statusListValidateTask = new StatusListValidateTask.Builder()
				.setStatusListCredentialType(Definition.DEFAULT_STATUS_LIST_CREDENTIAL_TYPE)
				.setStatusList(jwtSvc)
				.setStatusListKey(statusListKey).build();
		StatusListValidateTask.Result svtResult = statusListValidateTask.start();
		LOGGER.info("status list validate task result = {}", svtResult.toString());

		// step 4: store new status list
		String sid = UUID.randomUUID().toString();
		statusList = new StatusList()
				.setSid(sid)
				.setGid(gid)
				.setStatusListType(Definition.StatusListType.valueOf(statusListType).name())
				.setCredentialType(credentialType)
				.setIssuanceDate(Timestamp.from(DateUtils.toDate(issuanceTime).toInstant()))
				.setExpirationDate(Timestamp.from(svc.getExpirationDate().toInstant()))
				.setContent(jwtSvc);
		statusList = statusListRepository.save(statusList);
		if (!sid.equals(statusList.getSid())) {
			throw new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to store status list");
		}
		return jwtSvc;
	}

	/**
	 * generate status_list signing key
	 *
	 * @return (public key, http status)
	 */
	public Tuple.Pair<String, HttpStatus> genKey() {

		String result;
		HttpStatus httpStatus;

		try {

			// step 1: generate key
			String kid = "key-2";
			ECKey ecKey = KeyUtils.generateP256(kid);
			if (ecKey == null) {
				throw new VcException(VcException.ERR_SYS_GENERATE_KEY_ERROR,
						"fail to generate status_list signing key");
			}
			Map<String, Object> publicKeyJwk = ecKey.toPublicJWK().toJSONObject();

			// step 2: store key to database
			// step 2.1: encrypt status list key
			String iv = KeyUtils.genIV();
			if(vcKeyEnc == null || vcKeyEnc.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_SET_VC_KEY_ENC_ERROR, "invalid VC_KEY_ENC");
            }
			String statusListKeyCipher = KeyUtils.GCMEncrypt(ecKey.toJSONString(), vcKeyEnc, iv);
			
			int count = settingRepository.updatePropValueByPropName(PreloadSetting.NAME_STATUS_LIST_KEY,
					statusListKeyCipher);
			if (count != 1) {
				throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to update status_list signing key");
			}

			// step 3: reload setting
			boolean reloadOk = preloadSetting.reload();
			if (!reloadOk) {
				throw new VcException(VcException.ERR_SYS_RELOAD_SETTING_ERROR, "fail to reload setting");
			}

			result = new GenerateStatusListKeyResponseDTO().setKey(publicKeyJwk).toString();
			httpStatus = HttpStatus.CREATED;

		} catch (VcException e) {
			LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
			result = new ErrorResponseDTO(e1).toString();
			httpStatus = e.toHttpStatus();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			VcException vcException = new VcException(VcException.ERR_SYS_GENERATE_KEY_ERROR,
					"fail to generate status_list signing key");
			result = new ErrorResponseDTO(vcException).toString();
			httpStatus = vcException.toHttpStatus();
		}
		return Tuple.collect(result, httpStatus);
	}

	/**
	 * load status list policy
	 *
	 * @param credentialType associated credential type, ex:
	 *                       StatusList2021Credential
	 * @return policy
	 */
	private Policy loadStatusListPolicy(String credentialType) {

		return new Policy().setPid(UUID.randomUUID().toString()).setCredentialType(credentialType).setSchemaName("")
				.setSchemaContent("")
				// effective duration: 1 days
				.setEffectiveDuration(new Policy.Duration(1, DateUtils.TimeUnit.DAY)).setSignatureAlgorithm("ES256");
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
				.setUrl(preloadSetting.getUrlFrontendGetIssuerInfoDid() + issuerDid).setAllowedHostnames(null)
				.setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
				.setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes()).setEnableLog(preloadSetting.isHttpEnableLog())
				.build();

		// connect to frontend service
		Tuple.Pair<Integer, String> response = httpUtils.get();
		int statusCode = response.getA();
		String content = response.getB();

		// check http status code and response content
		if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
			String errorMessage = "fail to get issuer's DID info. (http status code, response content) = (" + statusCode
					+ ", " + content + ")";
			throw new VcException(VcException.ERR_DID_FRONTEND_GET_ISSUER_DID_INFO_ERROR, errorMessage);
		}

		return new DidGetIssuerInfoResponseDTO(content);
	}
	
	
	// renew status list in table status_list
	@Scheduled(cron = "#{@preloadSetting.getScheduleCronStatusListRenew()}")
	public void scheduleStatusListRenew() {
		final String MAX_RETRY_TIMES = preloadSetting.getStatusListMaxRetryTimes(); 
		
		if(enableRenewStatusList.equals("true")) {
			long startTime = System.currentTimeMillis();
			// renew process
			List<StatusList> latestStatusLists = statusListRepository.queryAllLatestStatusListByCredentialTypeAndStatusListTypeAndGid();
			
			List<StatusList> processList = new ArrayList<>(latestStatusLists);
			List<StatusList> successList = new ArrayList<>();
			List<StatusList> failedList = new ArrayList<>();
			
			int round = 0;
			
			while(!processList.isEmpty() && round < Integer.parseInt(MAX_RETRY_TIMES)) {
				
				round++;
				LOGGER.info("[Scheduled StatusList Renew]:: Renew round {}, statusLists to process: {}", round, processList.size());
				
				List<StatusList> retryList = new ArrayList<>();
				Tuple.Pair<String, HttpStatus> result = null;
				
				for(StatusList record : processList) {
					try {
						LOGGER.info("\n[Scheduled StatusList Renew]::\ncredential type = {}\n statusListType = {}\n gid = {}\n",
								record.getCredentialType(), record.getStatusListType(), record.getGid());
						
						result = generateAndRenew(record.getCredentialType(), record.getStatusListType(), record.getGid());
						LOGGER.info("[HTTP status]:: {}", result.getB().toString());
				        LOGGER.info("[renew result]:: {}\n", result.getA());
				        if(result.getB() == HttpStatus.CREATED) {
				        	successList.add(record);
				        }else {
				        	retryList.add(record);
				        }
						
					}catch(Exception e) {
						LOGGER.info("\n[Scheduled StatusList Renew]::Renew failed: \ncredential type = {}\n statusListType = {}\n gid = {}\n error : {}\n",
								record.getCredentialType(), record.getStatusListType(), record.getGid(), e.getMessage());
						retryList.add(record);
					}
				}
				
				processList = retryList;
				
				if(!processList.isEmpty()) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException ignored) {}
				}
			}
			
			failedList.addAll(processList);
			
			LOGGER.info("[Scheduled StatusList Renew]:: Renew success: {}", successList.size());
			LOGGER.info("[Scheduled StatusList Renew]:: Renew failed: {}", failedList.size());
			
			long endTime = System.currentTimeMillis();
			LOGGER.info("執行時間: " + (endTime - startTime) + " ms");
		}else {
			LOGGER.info("[Scheduled StatusList Renew]:: Skip scheduling based on the configuration.");
		}
	}
}
