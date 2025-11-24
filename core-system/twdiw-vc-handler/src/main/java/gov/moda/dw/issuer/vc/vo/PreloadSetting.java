package gov.moda.dw.issuer.vc.vo;

import gov.moda.dw.issuer.vc.domain.Setting;
import gov.moda.dw.issuer.vc.repository.SettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * preload setting as cache
 *
 * @version 20240902
 */
@Component
public class PreloadSetting {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadSetting.class);

    // ===== name =====

    // http
    public static final String NAME_HTTP_MAX_CONNECT_TIMEOUT = "http.max_connect_timeout";
    public static final String NAME_HTTP_MAX_RETRY_TIMES = "http.max_retry_times";
    public static final String NAME_HTTP_ENABLE_LOG = "http.enable_log";
    
    // http proxy
    public static final String NAME_HTTP_PROXY_ENABLE = "http.proxy.enable";
    public static final String NAME_HTTP_PROXY_URL = "http.proxy.url";
    public static final String NAME_HTTP_PROXY_PORT = "http.proxy.port";
    public static final String NAME_HTTP_PROXY_PROTOCOL = "http.proxy.protocol";

    // issuer
    public static final String NAME_ISSUER_DID = "issuer.did";
    public static final String NAME_ISSUER_NAME = "issuer.name";
    public static final String NAME_ISSUER_ORG_INFO = "issuer.org.info";
    public static final String NAME_ISSUER_ENABLE_SELF_VERIFY = "issuer.enable_self_verify";
    public static final String NAME_ISSUER_KEY = "issuer.key";

    // log backup
    public static final String NAME_LOG_BACKUP_N_DAYS_AGO = "log.backup.n_days_ago";
    public static final String NAME_LOG_BACKUP_SINGLE_QUERY_SIZE = "log.backup.single_query_size";

    // schedule
    public static final String NAME_SCHEDULE_CRON_SL_RENEW = "schedule.cron.status_list_renew";
    public static final String NAME_SCHEDULE_CRON_LOG_BACKUP = "schedule.cron.log_backup";
    public static final String NAME_SCHEDULE_CRON_DELETE_DATA = "schedule.cron.delete_data";

    // selective disclosure
    public static final String NAME_SD_ACTIVATE = "sd.activate";
    public static final String NAME_SD_SDJWT_ENABLE_DECOY = "sd.sdjwt.enable_decoy";

    // url
    public static final String NAME_URL_FRONTEND_GENERATE_DID = "url.frontend.generate_did";
    public static final String NAME_URL_FRONTEND_REGISTER_DID = "url.frontend.register_did";
    public static final String NAME_URL_FRONTEND_REVIEW_DID = "url.frontend.review_did";
    public static final String NAME_URL_PUSH_NOTIFY_STATUS_CHANGE = "url.push.notify_status_change";
    public static final String NAME_URL_DEMO_GET_HOLDER_DATA = "url.demo.get_holder_data";
    public static final String NAME_URL_VC_BASE_PATH = "url.vc.base_path";
    public static final String NAME_URL_FRONTEND_GET_ISSUER_INFO_DID = "url.frontend.get_issuer_info_did";
    public static final String NAME_URL_GET_TOKEN = "url.get_token";
    public static final String NAME_URL_VP_VALIDATION = "url.vp_validation";
    public static final String NAME_URL_OIDVCI_QRCODE = "url.oidvci_qrcode";
    public static final String NAME_URL_FRONTEND_CREATE_DID = "url.frontend.create_did";

    // status list
    public static final String NAME_STATUS_LIST_KEY = "status.list.key";
    public static final String NAME_STATUS_LIST_MAX_RETRY_TIMES = "status.list.max_retry_times";
    public static final String NAME_STATUS_DELAY = "status.list.delay";

    // data
    public static final String NAME_DATA_DELETE_N_MINUTES_AGO = "data.delete.n_minutes_ago";

    // access token
    public static final String NAME_ACCESS_TOKEN_FRONTEND_REGISTER_DID = "access_token.frontend.register_did";

    // ===== default value =====

    // unit: ms
    private static final int VALUE_HTTP_MAX_CONNECT_TIMEOUT = 3000;
    private static final int VALUE_HTTP_MAX_RETRY_TIMES = 2;
    private static final int VALUE_LOG_BACKUP_N_DAYS_AGO = 7;
    private static final int VALUE_LOG_BACKUP_SINGLE_QUERY_SIZE = 100;
    
    // http proxy default values
    private static final boolean VALUE_HTTP_PROXY_ENABLE = false;
    private static final String VALUE_HTTP_PROXY_URL = "127.0.0.1";
    private static final int VALUE_HTTP_PROXY_PORT = 8080;
    private static final String VALUE_HTTP_PROXY_PROTOCOL = "http";

    private int httpMaxConnectTimeout;
    private int httpMaxRetryTimes;
    private boolean httpEnableLog;
    
    // http proxy settings
    private boolean httpProxyEnable;
    private String httpProxyUrl;
    private int httpProxyPort;
    private String httpProxyProtocol;
    private String issuerDid;
    private String issuerName;
    private String issuerOrgInfo;
    private String issuerKey;
    private boolean issuerEnableSelfVerify;
    private int logBackupNDaysAgo;
    private int logBackupSingleQuerySize;
    private String scheduleCronStatusListRenew;
    private String scheduleCronLogBackup;
    private boolean sdActivate;
    private boolean sdSdjwtEnableDecoy;
    private String urlFrontendGenerateDid;
    private String urlFrontendRegisterDid;
    private String urlFrontendReviewDid;
    private String urlPushNotifyStatusChange;
    private String urlDemoGetHolderData;
    private String urlVcBasePath;
    private String urlFrontendGetIssuerInfoDid;
    private String urlVpValidation;
    private String urlOidvciQrcode;
    private String scheduleCronDeleteData;
    private String dataDeleteNMinutesAgo;
    private String urlGetToken;
    private String accessTokenFrontendRegisterDid;

    private String statusListKey;
    private String statusListMaxRetryTimes;
    private String statusListDelay;
    
    private String urlFrontendCreateDid;

    private final SettingRepository settingRepository;

    public PreloadSetting(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
        LOGGER.info("Loading setting ... {}", (loadSetting() ? "OK" : "FAIL"));
    }

    public boolean reload() {
        return loadSetting();
    }

    /**
     * load setting from database
     *
     * @return loading result
     */
    private boolean loadSetting() {

        try {
            // load prop. data from database
            List<Setting> settingList = settingRepository.findAll();
            for (Setting setting : settingList) {
                String propName = setting.getPropName();
                String propValue = setting.getPropValue();

                switch (propName) {

                    // http - max connect timeout
                    case  NAME_HTTP_MAX_CONNECT_TIMEOUT -> httpMaxConnectTimeout = (propValue != null) ? Integer.parseInt(propValue) : VALUE_HTTP_MAX_CONNECT_TIMEOUT;

                    // http - max retry time
                    case  NAME_HTTP_MAX_RETRY_TIMES -> httpMaxRetryTimes = (propValue != null) ? Integer.parseInt(propValue) : VALUE_HTTP_MAX_RETRY_TIMES;

                    // http - enable log
                    case NAME_HTTP_ENABLE_LOG -> httpEnableLog = Boolean.parseBoolean(propValue);
                    
                    // http - proxy enable
                    case NAME_HTTP_PROXY_ENABLE -> httpProxyEnable = Boolean.parseBoolean(propValue);
                    
                    // http - proxy url
                    case NAME_HTTP_PROXY_URL -> httpProxyUrl = (propValue != null) ? propValue : VALUE_HTTP_PROXY_URL;
                    
                    // http - proxy port
                    case NAME_HTTP_PROXY_PORT -> httpProxyPort = (propValue != null) ? Integer.parseInt(propValue) : VALUE_HTTP_PROXY_PORT;
                    
                    // http - proxy protocol
                    case NAME_HTTP_PROXY_PROTOCOL -> httpProxyProtocol = (propValue != null) ? propValue : VALUE_HTTP_PROXY_PROTOCOL;

                    // issuer - DID
                    case  NAME_ISSUER_DID -> issuerDid = propValue;

                    // issuer - name
                    case  NAME_ISSUER_NAME -> issuerName = propValue;

                    // issuer - organization information
                    case NAME_ISSUER_ORG_INFO -> issuerOrgInfo = propValue;

                    // issuer - enable self verify
                    case  NAME_ISSUER_ENABLE_SELF_VERIFY -> issuerEnableSelfVerify = Boolean.parseBoolean(propValue);

                    // issuer - key
                    case  NAME_ISSUER_KEY -> issuerKey = propValue;

                    // log backup - n days ago
                    case  NAME_LOG_BACKUP_N_DAYS_AGO -> logBackupNDaysAgo = (propValue != null) ? Integer.parseInt(propValue) : VALUE_LOG_BACKUP_N_DAYS_AGO;

                    // log backup - single query size
                    case  NAME_LOG_BACKUP_SINGLE_QUERY_SIZE -> logBackupSingleQuerySize = (propValue != null) ? Integer.parseInt(propValue) : VALUE_LOG_BACKUP_SINGLE_QUERY_SIZE;

                    // schedule - cron expression of status list renew task
                    case  NAME_SCHEDULE_CRON_SL_RENEW -> scheduleCronStatusListRenew = propValue;

                    // schedule - cron expression of log backup task
                    case  NAME_SCHEDULE_CRON_LOG_BACKUP -> scheduleCronLogBackup = propValue;

                    // selective disclosure - activate
                    case  NAME_SD_ACTIVATE -> sdActivate = Boolean.parseBoolean(propValue);

                    // sd - SD-JWT enable decoy
                    case  NAME_SD_SDJWT_ENABLE_DECOY -> sdSdjwtEnableDecoy = Boolean.parseBoolean(propValue);

                    // url - frontend service generate DID
                    case  NAME_URL_FRONTEND_GENERATE_DID -> urlFrontendGenerateDid = propValue;

                    // url - frontend service register DID
                    case  NAME_URL_FRONTEND_REGISTER_DID -> urlFrontendRegisterDid = propValue;

                    // url - frontend service review DID
                    case  NAME_URL_FRONTEND_REVIEW_DID -> urlFrontendReviewDid = propValue;

                    // url - push service notify app
                    case NAME_URL_PUSH_NOTIFY_STATUS_CHANGE -> urlPushNotifyStatusChange = propValue;

                    // url - demo service get holder's data
                    case NAME_URL_DEMO_GET_HOLDER_DATA -> urlDemoGetHolderData = propValue;

                    // url - vc service base path
                    case  NAME_URL_VC_BASE_PATH -> urlVcBasePath = propValue;

                    // status list - signing key
                    case  NAME_STATUS_LIST_KEY -> statusListKey = propValue;

                    // url - frontend service get issuer info DID
                    case  NAME_URL_FRONTEND_GET_ISSUER_INFO_DID -> urlFrontendGetIssuerInfoDid = propValue;

                    // schedule - cron expression of delete expired data task
                    case  NAME_SCHEDULE_CRON_DELETE_DATA -> scheduleCronDeleteData = propValue;

                    // delete data - n minutes ago
                    case NAME_DATA_DELETE_N_MINUTES_AGO -> dataDeleteNMinutesAgo = propValue;

                    // url - get token
                    case  NAME_URL_GET_TOKEN -> urlGetToken = propValue;

                    // status list - max retry times
                    case NAME_STATUS_LIST_MAX_RETRY_TIMES -> statusListMaxRetryTimes = propValue;

                    // status list - delay
                    case NAME_STATUS_DELAY -> statusListDelay = propValue;

                    // url - vp validation
                    case NAME_URL_VP_VALIDATION -> urlVpValidation = propValue;

                    // url - oidvci qrcode
                    case NAME_URL_OIDVCI_QRCODE -> urlOidvciQrcode = propValue;

                    // access token - frontend service register DID
                    case NAME_ACCESS_TOKEN_FRONTEND_REGISTER_DID ->  accessTokenFrontendRegisterDid = propValue;
                    
                    // url - frontend service create DID
                    case NAME_URL_FRONTEND_CREATE_DID -> urlFrontendCreateDid = propValue;
                }
            }

            // TODO: check if all required setting are ready

            return true;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * get PreloadSetting value and DB value
     *
     * @return loading result
     */
    public Map<String, List<String>> getPreloadSettingValueAndDBValue(){

    	Map<String, List<String>> settingComparisonMap = new HashMap<>();
    	try {
            // load prop. data from database
            List<Setting> settingList = settingRepository.findAll();
            for (Setting setting : settingList) {
                String propName = setting.getPropName();
                String propValue = setting.getPropValue();

                switch (propName) {

                    // http - max connect timeout
                    case  NAME_HTTP_MAX_CONNECT_TIMEOUT -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getHttpMaxConnectTimeout()), propValue)));

                    // http - max retry time
                    case  NAME_HTTP_MAX_RETRY_TIMES -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getHttpMaxRetryTimes()), propValue)));

                    // http - enable log
                    case NAME_HTTP_ENABLE_LOG -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(isHttpEnableLog()), propValue)));
                    
                    // http - proxy enable
                    case NAME_HTTP_PROXY_ENABLE -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(isHttpProxyEnable()), propValue)));
                    
                    // http - proxy url
                    case NAME_HTTP_PROXY_URL -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getHttpProxyUrl()), propValue)));
                    
                    // http - proxy port
                    case NAME_HTTP_PROXY_PORT -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getHttpProxyPort()), propValue)));
                    
                    // http - proxy protocol
                    case NAME_HTTP_PROXY_PROTOCOL -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getHttpProxyProtocol()), propValue)));

                    // issuer - DID
                    case  NAME_ISSUER_DID -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getIssuerDid()), propValue)));

                    // issuer - name
                    case  NAME_ISSUER_NAME -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getIssuerName()), propValue)));

                    // issuer - organization information
                    case NAME_ISSUER_ORG_INFO -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getIssuerOrgInfo()), propValue)));

                    // issuer - enable self verify
                    case  NAME_ISSUER_ENABLE_SELF_VERIFY -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(isIssuerEnableSelfVerify()), propValue)));

                    // issuer - key
                    case  NAME_ISSUER_KEY -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getIssuerKey()), propValue)));

                    // log backup - n days ago
                    case  NAME_LOG_BACKUP_N_DAYS_AGO -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getLogBackupNDaysAgo()), propValue)));

                    // log backup - single query size
                    case  NAME_LOG_BACKUP_SINGLE_QUERY_SIZE -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getLogBackupSingleQuerySize()), propValue)));

                    // schedule - cron expression of status list renew task
                    case  NAME_SCHEDULE_CRON_SL_RENEW -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getScheduleCronStatusListRenew()), propValue)));

                    // schedule - cron expression of log backup task
                    case  NAME_SCHEDULE_CRON_LOG_BACKUP -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getScheduleCronLogBackup()), propValue)));

                    // selective disclosure - activate
                    case  NAME_SD_ACTIVATE -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(isSdActivate()), propValue)));

                    // sd - SD-JWT enable decoy
                    case  NAME_SD_SDJWT_ENABLE_DECOY -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(isSdSdjwtEnableDecoy()), propValue)));

                    // url - frontend service generate DID
                    case  NAME_URL_FRONTEND_GENERATE_DID -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlFrontendGenerateDid()), propValue)));

                    // url - frontend service register DID
                    case  NAME_URL_FRONTEND_REGISTER_DID -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlFrontendRegisterDid()), propValue)));

                    // url - frontend service review DID
                    case  NAME_URL_FRONTEND_REVIEW_DID -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlFrontendReviewDid()), propValue)));

                    // url - push service notify app
                    case NAME_URL_PUSH_NOTIFY_STATUS_CHANGE -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlPushNotifyStatusChange()), propValue)));

                    // url - demo service get holder's data
                    case NAME_URL_DEMO_GET_HOLDER_DATA -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlDemoGetHolderData()), propValue)));

                    // url - vc service base path
                    case  NAME_URL_VC_BASE_PATH -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlVcBasePath()), propValue)));

                    // status list - signing key
                    case  NAME_STATUS_LIST_KEY -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getStatusListKey()), propValue)));

                    // url - frontend service get issuer info DID
                    case  NAME_URL_FRONTEND_GET_ISSUER_INFO_DID -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlFrontendGetIssuerInfoDid()), propValue)));

                    // schedule - cron expression of delete expired data task
                    case  NAME_SCHEDULE_CRON_DELETE_DATA -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getScheduleCronDeleteData()), propValue)));

                    // delete data - n minutes ago
                    case NAME_DATA_DELETE_N_MINUTES_AGO -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(
							getDataDeleteNMinutesAgo()), propValue)));

                    // url - get token
                    case  NAME_URL_GET_TOKEN -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlGetToken()), propValue)));

                    // status list - max retry times
                    case NAME_STATUS_LIST_MAX_RETRY_TIMES -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getStatusListMaxRetryTimes()), propValue)));

                    // status list - delay
                    case NAME_STATUS_DELAY -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getStatusListDelay()), propValue)));

                    // url - vp validation
                    case NAME_URL_VP_VALIDATION -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlVpValidation()), propValue)));

                    // url - oidvci qrcode
                    case NAME_URL_OIDVCI_QRCODE -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlOidvciQrcode()), propValue)));

                    // access token - frontend service register DID
                    case NAME_ACCESS_TOKEN_FRONTEND_REGISTER_DID ->  settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getAccessTokenFrontendRegisterDid()), propValue)));
                    
                    // url - frontend service create DID
                    case NAME_URL_FRONTEND_CREATE_DID -> settingComparisonMap.put(propName, new ArrayList<>(List.of(String.valueOf(getUrlFrontendCreateDid()), propValue)));
                }
            }
            return settingComparisonMap;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    	return settingComparisonMap;
    }

    public int getHttpMaxConnectTimeout() {
        return httpMaxConnectTimeout;
    }

    public int getHttpMaxRetryTimes() {
        return httpMaxRetryTimes;
    }

    public boolean isHttpEnableLog() {
        return httpEnableLog;
    }

    public String getIssuerDid() {
    	if(issuerDid == null || issuerDid.isBlank()) {
    		throw new IllegalStateException("X 未設定：issuer.did，請確認資料庫配置。");
    	}
        return issuerDid;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getIssuerOrgInfo() {
        return issuerOrgInfo;
    }

    public String getIssuerKey() {
        return issuerKey;
    }

    public boolean isIssuerEnableSelfVerify() {
        return issuerEnableSelfVerify;
    }

    public int getLogBackupNDaysAgo() {
        return logBackupNDaysAgo;
    }

    public int getLogBackupSingleQuerySize() {
        return logBackupSingleQuerySize;
    }

    public String getScheduleCronStatusListRenew() {
    	if(scheduleCronStatusListRenew == null || scheduleCronStatusListRenew.isBlank()) {
    		throw new IllegalStateException("X 未設定：schedule.cron.status_list_renew，請確認資料庫配置。");
    	}
        return scheduleCronStatusListRenew;
    }

    public String getScheduleCronLogBackup() {
        return scheduleCronLogBackup;
    }

    public boolean isSdActivate() {
        return sdActivate;
    }

    public boolean isSdSdjwtEnableDecoy() {
        return sdSdjwtEnableDecoy;
    }

    public String getUrlFrontendGenerateDid() {
    	if(urlFrontendGenerateDid == null || urlFrontendGenerateDid.isBlank()) {
    		throw new IllegalStateException("X 未設定：url.frontend.generate_did，請確認資料庫配置。");
    	}
        return urlFrontendGenerateDid;
    }

    public String getUrlFrontendRegisterDid() {
    	if(urlFrontendRegisterDid == null || urlFrontendRegisterDid.isBlank()) {
    		throw new IllegalStateException("X 未設定：url.frontend.register_did，請確認資料庫配置。");
    	}
        return urlFrontendRegisterDid;
    }

    public String getUrlFrontendReviewDid() {
    	if(urlFrontendReviewDid == null || urlFrontendReviewDid.isBlank()) {
    		throw new IllegalStateException("X 未設定：url.frontend.review_did，請確認資料庫配置。");
    	}
        return urlFrontendReviewDid;
    }

    public String getUrlPushNotifyStatusChange() {
        return urlPushNotifyStatusChange;
    }

    public String getUrlDemoGetHolderData() {
    	if(urlDemoGetHolderData == null || urlDemoGetHolderData.isBlank()) {
    		throw new IllegalStateException("X 未設定：url.demo.get_holder_data，請確認資料庫配置。");
    	}
        return urlDemoGetHolderData;
    }

    public String getUrlVcBasePath() {
    	if(urlVcBasePath == null || urlVcBasePath.isBlank()) {
    		throw new IllegalStateException("X 未設定：url.vc.base_path，請確認資料庫配置。");
    	}
        return urlVcBasePath;
    }

	public String getStatusListKey() {
		if(statusListKey == null || statusListKey.isBlank()) {
			throw new IllegalStateException("X 未設定：status.list.key，請確認資料庫配置。");
		}
		return statusListKey;
	}

	public String getUrlFrontendGetIssuerInfoDid() {
		if(urlFrontendGetIssuerInfoDid == null || urlFrontendGetIssuerInfoDid.isBlank()) {
			throw new IllegalStateException("X 未設定：url.frontend.get_issuer_info_did，請確認資料庫配置。");
		}
		return urlFrontendGetIssuerInfoDid;
	}

	public String getScheduleCronDeleteData() {
		if(scheduleCronDeleteData == null || scheduleCronDeleteData.isBlank()) {
			throw new IllegalStateException("X 未設定：schedule.cron.delete_data，請確認資料庫配置。");
		}
		return scheduleCronDeleteData;
	}

	public String getDataDeleteNMinutesAgo() {
		if(dataDeleteNMinutesAgo == null || dataDeleteNMinutesAgo.isBlank()) {
			throw new IllegalStateException("X 未設定：data.delete.n_minutes_ago，請確認資料庫配置。");
		}
		return dataDeleteNMinutesAgo;
	}

	public String getUrlGetToken() {
		return urlGetToken;
	}

	public String getStatusListMaxRetryTimes() {
		if(statusListMaxRetryTimes == null || statusListMaxRetryTimes.isBlank()) {
			throw new IllegalStateException("X 未設定：status.list.max_retry_times，請確認資料庫配置。");
		}
		return statusListMaxRetryTimes;
	}

	public PreloadSetting setStatusListMaxRetryTimes(String statusListMaxRetryTimes) {
		this.statusListMaxRetryTimes = statusListMaxRetryTimes;
		return this;
	}

	public String getStatusListDelay() {
		if(statusListDelay == null || statusListDelay.isBlank()) {
			throw new IllegalStateException("X 未設定：status.list.delay，請確認資料庫配置。");
		}
		return statusListDelay;
	}

	public String getUrlFrontendCreateDid() {
		if(urlFrontendCreateDid == null || urlFrontendCreateDid.isBlank()) {
			throw new IllegalStateException("X 未設定：url.frontend.create_did，請確認資料庫配置。");
		}
		return urlFrontendCreateDid;
	}

	public PreloadSetting setUrlFrontendCreateDid(String urlFrontendCreateDid) {
		this.urlFrontendCreateDid = urlFrontendCreateDid;
		return this;
	}

	public PreloadSetting setStatusListDelay(String statusListDelay) {
		this.statusListDelay = statusListDelay;
		return this;
	}

	public PreloadSetting setUrlGetToken(String urlGetToken) {
		this.urlGetToken = urlGetToken;
		return this;
	}

	public PreloadSetting setUrlFrontendGenerateDid(String urlFrontendGenerateDid) {
        this.urlFrontendGenerateDid = urlFrontendGenerateDid;
        return this;
    }

    public PreloadSetting setUrlFrontendRegisterDid(String urlFrontendRegisterDid) {
        this.urlFrontendRegisterDid = urlFrontendRegisterDid;
        return this;
    }

    public PreloadSetting setUrlFrontendReviewDid(String urlFrontendReviewDid) {
        this.urlFrontendReviewDid = urlFrontendReviewDid;
        return this;
    }

    public PreloadSetting setUrlPushNotifyStatusChange(String urlPushNotifyStatusChange) {
        this.urlPushNotifyStatusChange = urlPushNotifyStatusChange;
        return this;
    }

    public PreloadSetting setUrlDemoGetHolderData(String urlDemoGetHolderData) {
        this.urlDemoGetHolderData = urlDemoGetHolderData;
        return this;
    }

	public PreloadSetting setUrlFrontendGetIssuerInfoDid(String urlFrontendGetIssuerInfoDid) {
		this.urlFrontendGetIssuerInfoDid = urlFrontendGetIssuerInfoDid;
		return this;
	}

	public PreloadSetting setScheduleCronDeleteData(String scheduleCronDeleteData) {
		this.scheduleCronDeleteData = scheduleCronDeleteData;
		return this;
	}

	public PreloadSetting setDataDeleteNMinutesAgo(String dataDeleteNMinutesAgo) {
		this.dataDeleteNMinutesAgo = dataDeleteNMinutesAgo;
		return this;
	}

	public String getUrlVpValidation() {
		if(urlVpValidation == null || urlVpValidation.isBlank()) {
			throw new IllegalStateException("X 未設定：url.vp_validation，請確認資料庫配置。");
		}
		return urlVpValidation;
	}

	public PreloadSetting setUrlVpValidation(String urlVpValidation) {
		this.urlVpValidation = urlVpValidation;
		return this;
	}

	public String getUrlOidvciQrcode() {
		if(urlOidvciQrcode == null || urlOidvciQrcode.isBlank()) {
			throw new IllegalStateException("X 未設定：url.oidvci_qrcode，請確認資料庫配置。");
		}
		return urlOidvciQrcode;
	}

	public PreloadSetting setUrlOidvciQrcode(String urlOidvciQrcode) {
		this.urlOidvciQrcode = urlOidvciQrcode;
		return this;
	}

    public String getAccessTokenFrontendRegisterDid() {
    	if(accessTokenFrontendRegisterDid == null || accessTokenFrontendRegisterDid.isBlank()) {
    		throw new IllegalStateException("X 未設定：access_token.frontend.register_did，請確認資料庫配置。");
    	}
    	return accessTokenFrontendRegisterDid; 
    }

    public PreloadSetting setAccessTokenFrontendRegisterDid(String accessTokenFrontendRegisterDid) {
        this.accessTokenFrontendRegisterDid = accessTokenFrontendRegisterDid;
        return this;
    }

    // ===== HTTP Proxy Getters =====
    
    public boolean isHttpProxyEnable() {
        return httpProxyEnable;
    }

    public String getHttpProxyUrl() {
        return httpProxyUrl;
    }

    public int getHttpProxyPort() {
        return httpProxyPort;
    }

    public String getHttpProxyProtocol() {
        return httpProxyProtocol;
    }

    // ===== HTTP Proxy Setters =====
    
    public PreloadSetting setHttpProxyEnable(boolean httpProxyEnable) {
        this.httpProxyEnable = httpProxyEnable;
        return this;
    }

    public PreloadSetting setHttpProxyUrl(String httpProxyUrl) {
        this.httpProxyUrl = httpProxyUrl;
        return this;
    }

    public PreloadSetting setHttpProxyPort(int httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
        return this;
    }

    public PreloadSetting setHttpProxyProtocol(String httpProxyProtocol) {
        this.httpProxyProtocol = httpProxyProtocol;
        return this;
    }
}
