package gov.moda.dw.issuer.oidvci.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.issuer.oidvci.repository.PreAuthCodeRepository;
import gov.moda.dw.issuer.oidvci.domain.*;




@Service
@Transactional
public class PreAuthCodeService {

	private static final Logger log = LoggerFactory.getLogger(PreAuthCodeService.class);

	private final String CODE_VALID = "VALID";
	private final String CODE_EXPIRED = "EXPIRED";
	private final String CODE_USED = "USED";

	private final PreAuthCodeRepository pre_auth_repository;

	public PreAuthCodeService(PreAuthCodeRepository pre_auth_repository) {
        this.pre_auth_repository = pre_auth_repository;
    }


	// 查詢Pre-auth code
	public Optional<PreAuthCodeEntity> getPreAuthCode(String uid_enc, String nonce) {
        String cleanUidEnc = Jsoup.clean(uid_enc, Safelist.none());
        String cleanNonce = Jsoup.clean(nonce, Safelist.none());
		PreAuthCodeId pre_auth_id = new PreAuthCodeId(cleanUidEnc, cleanNonce);
		return pre_auth_repository.findById(pre_auth_id);
	}

	// 查詢Pre-auth code by client_id、pre_auth_code
	public Optional<PreAuthCodeEntity> getPreAuthCodeByClientIDAndPreAuthCode(String client_id, String pre_auth_code){
		return pre_auth_repository.findByClientIDAndPreCode(client_id, pre_auth_code);
	}

	// 檢查Pre-auth code可用性
	public String checkPreAuthCodeValidity(PreAuthCodeEntity pre_auth_code_entity){
		String code_status = pre_auth_code_entity.getCodeStatus();
		Timestamp expire_time = pre_auth_code_entity.getExpireTime();
		if(!code_status.equals(CODE_VALID))
			return code_status;
		// 取得當前時間的 Timestamp
        Timestamp current_time = new Timestamp(System.currentTimeMillis());
        // 檢查是否已經逾期
        if(current_time.after(expire_time))
        {
        	pre_auth_code_entity.setCodeStatus(CODE_EXPIRED);
        	pre_auth_repository.save(pre_auth_code_entity);
        	return CODE_EXPIRED;
        }

		return CODE_VALID;
	}
	// 產製Pre-auth code
	public boolean genPreAuthCode(String uid_enc, String nonce, String client_id, Timestamp create_time, Timestamp expire_time, String code_status, String credential_configuration_id, String pre_code,String tx_code,String jsonString) {

		PreAuthCodeId pre_code_id = new PreAuthCodeId(uid_enc, nonce);
		if(pre_auth_repository.existsById(pre_code_id))
			return false;
		else
		{
			PreAuthCodeEntity pre_auth_code_entity = new PreAuthCodeEntity(pre_code_id);
			pre_auth_code_entity.setClientID(client_id);
			pre_auth_code_entity.setCreateTime(create_time);
			pre_auth_code_entity.setPreCode(pre_code);
			pre_auth_code_entity.setExpireTime(expire_time);
			pre_auth_code_entity.setCodeStatus(code_status);
			pre_auth_code_entity.setCredentialConfigurationId(credential_configuration_id);
            pre_auth_code_entity.setTxCode(tx_code);
            pre_auth_code_entity.setCID(jsonString);
            log.info("tx_code is 2...." + tx_code);
			pre_auth_repository.save(pre_auth_code_entity);
			return true;
		}
	}

	// update Pre-auth code code_status
	public void updatePreAuthCodeCodeStatus(PreAuthCodeEntity pre_auth_code_entity, String code_status) {
		pre_auth_code_entity.setCodeStatus(code_status);
		pre_auth_repository.save(pre_auth_code_entity);
	}

	// 定期清理pre_auth_code過期的record
    @Scheduled(cron = "0 0 1 * * ?")
	public void deleteExpiredData() {
		// 獲取目前時間的兩天前
		LocalDateTime two_days_ago = LocalDateTime.now().minusDays(2);
		// 將LocalDateTime轉為Timestamp
		Timestamp two_days_ago_timestamp = Timestamp.valueOf(two_days_ago);
		pre_auth_repository.deleteByExpiredTime(two_days_ago_timestamp);
	}
}
