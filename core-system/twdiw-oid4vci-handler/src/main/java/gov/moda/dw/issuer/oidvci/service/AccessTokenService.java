package gov.moda.dw.issuer.oidvci.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.openid.connect.sdk.Nonce;

import gov.moda.dw.issuer.oidvci.domain.AccessTokenEntity;
import gov.moda.dw.issuer.oidvci.domain.PreAuthCodeEntity;
import gov.moda.dw.issuer.oidvci.repository.AccessTokenRepository;

@Service
@Transactional
public class AccessTokenService {

	private static final Logger log = LoggerFactory.getLogger(AccessTokenService.class);

	private final String CODE_VALID = "VALID";
	private final String CODE_EXPIRED = "EXPIRED";
	private final String CODE_USED = "USED";

	private final AccessTokenRepository access_token_repository;

	public AccessTokenService(AccessTokenRepository access_token_repository) {
		this.access_token_repository = access_token_repository;
	}

	// 產製access_token record
	public boolean genAccessToken(String token_value, String client_id, String user_id, Timestamp create_time, Timestamp expire_time,
			String nonce_from_idp, String c_nonce, Timestamp c_nonce_create_time, Timestamp c_nonce_expire_time,
			String authorization_details, String pre_auth_code, String token_status, String additional_info) {
		if(access_token_repository.existsById(token_value))
			return false;
		else
		{
			AccessTokenEntity access_token_entity = new AccessTokenEntity();
			access_token_entity.setTokenValue(token_value);
			access_token_entity.setClientID(client_id);
			access_token_entity.setUserID(user_id);
			access_token_entity.setCreateTime(create_time);
			access_token_entity.setExpireTime(expire_time);
			access_token_entity.setNonceFromIdp(nonce_from_idp);
			access_token_entity.setcNonce(c_nonce);
			access_token_entity.setcNonceCreateTime(c_nonce_create_time);
			access_token_entity.setcNonceExpireTime(c_nonce_expire_time);
			access_token_entity.setAuthorizationDetails(authorization_details);
			access_token_entity.setPreAuthCode(pre_auth_code);
			access_token_entity.setTokenStatus(token_status);
			access_token_entity.setAdditionalInfo(additional_info);
			access_token_repository.save(access_token_entity);
			return true;
		}
	}

	// 查詢access_token by client_id、access_token
	public Optional<AccessTokenEntity> getAccessTokenByTokenValue(String token_value){
		return access_token_repository.findById(token_value);
	}

	// 檢查access_token有效性
	public String checkAccessTokenValidity(AccessTokenEntity access_token_entity){
		String token_status = access_token_entity.getTokenStatus();
		Timestamp expire_time = access_token_entity.getExpireTime();
		if(!token_status.equals(CODE_VALID))
			return token_status;
		// 取得當前時間的 Timestamp
        Timestamp current_time = new Timestamp(System.currentTimeMillis());
        // 檢查是否已經逾期
        if(current_time.after(expire_time))
        {
        	access_token_entity.setTokenStatus(CODE_EXPIRED);
        	access_token_repository.save(access_token_entity);
        	return CODE_EXPIRED;
        }

		return CODE_VALID;
	}

	// 驗證c_nonce
	public boolean checkCNonceValidity(AccessTokenEntity access_token_entity, String jwt_c_nonce) {
		String c_nonce = access_token_entity.getcNonce();
		Timestamp cnonce_expire_time = access_token_entity.getcNonceExpireTime();
		if(!jwt_c_nonce.equals(c_nonce))
			return false;
		else
		{
			// 取得當前時間的 Timestamp
	        Timestamp current_time = new Timestamp(System.currentTimeMillis());
	        // 檢查是否已經逾期
	        if(current_time.after(cnonce_expire_time))
	        	return false;
	        else
	        	return true;
		}
	}

	// 更新c_nonce以及相關時間
	public String updateCNonceAndTTL(AccessTokenEntity access_token_entity, int token_ttl) {
		Nonce c_nonce = new Nonce();
		long req_time = new Date().getTime();
		Timestamp c_nonce_create_time = null;
		Timestamp c_nonce_expire_time = null;
		long minutes_in_millis = token_ttl * 1000;//10 minutes = 600,000 millisecond
		c_nonce_create_time = new Timestamp(req_time);
		c_nonce_expire_time = new Timestamp(req_time + minutes_in_millis);

		access_token_entity.setcNonce(c_nonce.getValue());
		access_token_entity.setcNonceCreateTime(c_nonce_create_time);
		access_token_entity.setcNonceExpireTime(c_nonce_expire_time);
		access_token_repository.save(access_token_entity);
		return c_nonce.getValue();
	}

	// update access_token的token_status
	public void updateAccessTokenTokenStatus(AccessTokenEntity access_token_entity, String token_status) {
		access_token_entity.setTokenStatus(token_status);
		access_token_repository.save(access_token_entity);
	}

	// 定期清理pre_auth_code過期的record
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteExpiredData() {
    	// 獲取目前時間的兩天前
		LocalDateTime two_days_ago = LocalDateTime.now().minusDays(2);
		// 將LocalDateTime轉為Timestamp
		Timestamp two_days_ago_timestamp = Timestamp.valueOf(two_days_ago);
		access_token_repository.deleteByExpiredTime(two_days_ago_timestamp);
    }
}
