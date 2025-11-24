package gov.moda.dw.verifier.vc.vo;

import gov.moda.dw.verifier.vc.domain.Setting;
import gov.moda.dw.verifier.vc.repository.SettingRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PreloadSetting {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadSetting.class);

    // ===== name =====

    // http
    public static final String NAME_HTTP_MAX_CONNECT_TIMEOUT = "http.max_connect_timeout";
    public static final String NAME_HTTP_MAX_RETRY_TIMES = "http.max_retry_times";
    public static final String NAME_HTTP_ENABLE_LOG = "http.enable_log";
    public static final String NAME_HTTP_PROXY_ENABLE = "http.proxy.enable";
    public static final String NAME_HTTP_PROXY_PROTOCOL = "http.proxy.protocol";
    public static final String NAME_HTTP_PROXY_URL = "http.proxy.url";
    public static final String NAME_HTTP_PROXY_PORT = "http.proxy.port";

    // url
    public static final String NAME_URL_FRONTEND_QUERY_DID = "url.frontend.query_did";

    // ===== default value =====

    // unit: ms
    private static final int VALUE_HTTP_MAX_CONNECT_TIMEOUT = 10000;
    private static final int VALUE_HTTP_MAX_RETRY_TIMES = 2;

    private int httpMaxConnectTimeout;
    private int httpMaxRetryTimes;
    private boolean httpEnableLog;

    // proxy
    private boolean httpProxyEnable;
    private String httpProxyProtocol;
    private String httpProxyUrl;
    private Integer httpProxyPort;

    private String urlFrontendQueryDid;

    // TODO: unit test
    private String mockPublicKeyUrl;
    private String mockStatusListUrl;
    private String mockSchemaUrl;

    private final SettingRepository settingRepository;

    public PreloadSetting(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
        LOGGER.info("Loading setting ... {}, version = {}", (loadSetting() ? "OK" : "FAIL"), Definition.CURRENT_VERSION);
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

                    // url - frontend service query DID
                    case  NAME_URL_FRONTEND_QUERY_DID -> urlFrontendQueryDid = propValue;

                    // http - proxy enable
                    case NAME_HTTP_PROXY_ENABLE -> httpProxyEnable = Boolean.parseBoolean(propValue);

                    // http - proxy url
                    case NAME_HTTP_PROXY_URL -> httpProxyUrl = (propValue != null) ? propValue : "127.0.0.1";

                    // http - proxy port
                    case NAME_HTTP_PROXY_PORT -> httpProxyPort = (propValue != null) ? Integer.parseInt(propValue) : 8080;

                    // http - proxy protocol
                    case NAME_HTTP_PROXY_PROTOCOL -> httpProxyProtocol = (propValue != null) ? propValue : "http";
                }
                LOGGER.debug("{} : {}", propName, propValue);
            }

            // TODO: check if all required setting are ready
            LOGGER.info("version={}", Definition.CURRENT_VERSION);
            return true;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return false;
    }

    public int getHttpMaxConnectTimeout() {
        return httpMaxConnectTimeout <= 0 ? VALUE_HTTP_MAX_CONNECT_TIMEOUT : httpMaxConnectTimeout;
    }

    public int getHttpMaxRetryTimes() {
        return httpMaxRetryTimes;
    }

    public boolean isHttpEnableLog() {
        return httpEnableLog;
    }

    public String getUrlFrontendQueryDid() {
        return urlFrontendQueryDid;
    }

    public boolean isHttpProxyEnable() {
        return httpProxyEnable;
    }

    public Integer getHttpProxyPort() {
        return httpProxyPort;
    }

    public String getHttpProxyProtocol() {
        return httpProxyProtocol;
    }

    public String getHttpProxyUrl() {
        return httpProxyUrl;
    }

    public PreloadSetting setUrlFrontendQueryDid(String urlFrontendQueryDid) {
        this.urlFrontendQueryDid = urlFrontendQueryDid;
        return this;
    }

    public String getMockPublicKeyUrl() {
        return mockPublicKeyUrl;
    }

    public PreloadSetting setMockPublicKeyUrl(String mockPublicKeyUrl) {
        this.mockPublicKeyUrl = mockPublicKeyUrl;
        return this;
    }

    public String getMockStatusListUrl() {
        return mockStatusListUrl;
    }

    public PreloadSetting setMockStatusListUrl(String mockStatusListUrl) {
        this.mockStatusListUrl = mockStatusListUrl;
        return this;
    }

    public String getMockSchemaUrl() {
        return mockSchemaUrl;
    }

    public PreloadSetting setMockSchemaUrl(String mockSchemaUrl) {
        this.mockSchemaUrl = mockSchemaUrl;
        return this;
    }
}
