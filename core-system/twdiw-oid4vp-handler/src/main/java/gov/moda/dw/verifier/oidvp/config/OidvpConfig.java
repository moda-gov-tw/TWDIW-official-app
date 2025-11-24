package gov.moda.dw.verifier.oidvp.config;

import com.nimbusds.jose.jwk.JWK;
import gov.moda.dw.verifier.oidvp.dao.OidvpPropertyDAO;
import gov.moda.dw.verifier.oidvp.domain.OidvpPropertyJpa;
import gov.moda.dw.verifier.oidvp.service.jwkStore.DefaultJWKStore;
import gov.moda.dw.verifier.oidvp.service.jwkStore.JWKStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OidvpConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OidvpConfig.class);

    private final OidvpPropertyDAO oidvpPropertyDAO;


    /**
     * required properties name
     */
    public static final String OIDVP_DOMAIN_URI_PREDICATE = "oidvp.domain-uri";
    public static final String ENDPOINT_SCHEMA_PREDICATE = "oidvp.endpoint-schema";
    public static final String VP_PRESENTATION_VALIDATION_URI_PREDICATE = "url.vp.presentation-validation";
    /**
     * optional properties name
     */
    // oidvp
    public static final String CLIENT_ID_PREDICATE = "oidvp.client-id";
    public static final String REDIRECT_URI_PREDICATE = "oidvp.redirect-uri";
    public static final String SESSION_EXPIRED_TIME_PREDICATE = "oidvp.session.expired";
    public static final String SESSION_DELETE_CRON_PREDICATE = "schedule.cron.delete-session.";
    public static final String VERIFY_RESULT_EXPIRED_TIME_PREDICATE = "verify-result.expired";
    public static final String VERIFY_RESULT_DELETE_CRON_PREDICATE = "schedule.cron.delete-result";
    public static final String VERIFY_RESULT_DELETE_AFTER_QUERY_PREDICATE = "verify-result.delete-after-query";
    public static final String VP_PRESENTATION_VALIDATION_HTTP_TIMEOUT_PREDICATE = "vp.presentation-validation.http-timeout";

    // did register url
    public static final String FRONTEND_GENERATE_DID_PREDICATE = "url.frontend.generate-did";
    public static final String FRONTEND_REGISTER_DID_PREDICATE = "url.frontend.register-did";
    public static final String FRONTEND_REVIEW_DID_PREDICATE = "url.frontend.review-did";
    public static final String FRONTEND_SERVICE_TIMEOUT_HTTP_PREDICATE = "frontend.service.http-timeout";
    public static final String FRONTEND_CREATE_DID_PREDICATE = "url.frontend.create-did";

    // verifier business service
    public static final String VERIFIER_BUSINESS_HTTP_TIMEOUT_PREDICATE = "verifierBusiness.http-timeout";
    public static final String CALLBACK_DOMAIN_LIST_PREDICATE = "callback.domain-list"; // split by ","

    // http proxy
    public static final String HTTP_PROXY_ENABLE_PREDICATE = "http.proxy.enable";
    public static final String HTTP_PROXY_PROTOCOL_PREDICATE = "http.proxy.protocol";
    public static final String HTTP_PROXY_URL_PREDICATE = "http.proxy.url";
    public static final String HTTP_PROXY_PORT_PREDICATE = "http.proxy.port";


    // final properties to use
    private String clientId;
    private URI endpointUri;
    private URI jwksURI;
    private URI presentationDefinitionUri;
    private URI redirectUri;
    private URI requestUri;
    private URI responseUri;
    private URI clientMetadataUri;
    private String issuer;
    private URI vpVerifyUri;
    private URI oidvpUri;
    private Integer sessionExpiredTime;
    private Integer verifyResultExpiredTime;
    private Boolean verifyResultDeleteAfterQuery;
    private String sessionDeleteCron;
    private String verifyResultDeleteCron;
    private Integer vpPresentationValidationHttpTimeout;
    private List<JWK> publicJWKs;
    private URI frontendGenerateDidURI;
    private URI frontendRegisterDidURI;
    private URI frontendReviewDidURI;
    private Integer frontendServiceHttpTimeout;
    private URI frontendCreateDidURI;
    private Integer verifierBusinessHttpTimeout;
    private List<String> callbackDomainList;
    private boolean httpProxyEnable;
    private String httpProxyProtocol;
    private String httpProxyUrl;
    private Integer httpProxyPort;

    private static final List<String> requiredProperties = new ArrayList<>();
    private final Map<String, String> properties = new HashMap<>();

    static {
        requiredProperties.add(OIDVP_DOMAIN_URI_PREDICATE);
        requiredProperties.add(ENDPOINT_SCHEMA_PREDICATE);
        requiredProperties.add(VP_PRESENTATION_VALIDATION_URI_PREDICATE);
    }

    public OidvpConfig(OidvpPropertyDAO oidvpPropertyDAO, JWKStore defaultJWKStore) {
        this.oidvpPropertyDAO = oidvpPropertyDAO;
        if (defaultJWKStore instanceof DefaultJWKStore) {
            LOGGER.info("Using default JWKStore");
        } else {
            LOGGER.info("Using JWKStore={}", defaultJWKStore.getClass().getName());
        }
        boolean loadingResult = loadConfigData();
        LOGGER.info("Loading setting ... {}", (loadingResult ? "OK" : "FAIL"));
        if (!loadingResult) {
            throw new IllegalStateException("LOADING SETTING FAILED");
        }
    }

    private boolean loadConfigData() {
        try {
            List<OidvpPropertyJpa> propertyList = oidvpPropertyDAO.getProperties();
            for (OidvpPropertyJpa property : propertyList) {
                String propertyName = property.getKey();
                String propertyValue = property.getValue();
                properties.put(propertyName, propertyValue);
            }
            buildProperty();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    private void buildProperty() throws Exception {
        try {
            String _oidvpDomainUri = getPropertyValue(OIDVP_DOMAIN_URI_PREDICATE);
            String oidvpDomainUri = _oidvpDomainUri.endsWith("/") ? _oidvpDomainUri : _oidvpDomainUri.concat("/");

            clientId = getPropertyValue(CLIENT_ID_PREDICATE);
            redirectUri = new URI(Optional.ofNullable(getPropertyValue(REDIRECT_URI_PREDICATE))
                                          .orElse("https://client.example.org/cb/redirect"));
            endpointUri = new URI(getPropertyValue(ENDPOINT_SCHEMA_PREDICATE));
            responseUri = new URI(oidvpDomainUri + "api/oidvp/authorization-response");
            jwksURI = new URI(oidvpDomainUri + "api/oidvp/jwks");
            clientMetadataUri = new URI(oidvpDomainUri + "api/oidvp/.well-known/openid-configuration");
            requestUri = new URI(oidvpDomainUri + "api/oidvp/request/");
            presentationDefinitionUri = new URI(oidvpDomainUri + "api/oidvp/presentation-definition/");
            vpVerifyUri = new URI(getPropertyValue(VP_PRESENTATION_VALIDATION_URI_PREDICATE));
            issuer = oidvpDomainUri + "api/oidvp";
            oidvpUri = new URI(oidvpDomainUri + "api/oidvp");

            // unit: minute
            sessionExpiredTime = Integer.valueOf(Optional.ofNullable(getPropertyValue(SESSION_EXPIRED_TIME_PREDICATE)).orElse("5"));
            // unit: minute
            verifyResultExpiredTime = Integer.valueOf(Optional.ofNullable(getPropertyValue(VERIFY_RESULT_EXPIRED_TIME_PREDICATE)).orElse("5"));
            verifyResultDeleteAfterQuery = Boolean.parseBoolean(Optional.ofNullable(getPropertyValue(VERIFY_RESULT_DELETE_AFTER_QUERY_PREDICATE)).orElse("true"));
            sessionDeleteCron = Optional.ofNullable(getPropertyValue(SESSION_DELETE_CRON_PREDICATE)).orElse("0 0 2 * * ?");
            verifyResultDeleteCron = Optional.ofNullable(getPropertyValue(VERIFY_RESULT_DELETE_CRON_PREDICATE)).orElse("0 0 * * * ?");

            // unit: second
            vpPresentationValidationHttpTimeout = Integer.valueOf(Optional.ofNullable(getPropertyValue(VP_PRESENTATION_VALIDATION_HTTP_TIMEOUT_PREDICATE)).orElse("30"));
            frontendServiceHttpTimeout = Integer.valueOf(Optional.ofNullable(getPropertyValue(FRONTEND_SERVICE_TIMEOUT_HTTP_PREDICATE)).orElse("5"));
            verifierBusinessHttpTimeout = Integer.valueOf(Optional.ofNullable(getPropertyValue(VERIFIER_BUSINESS_HTTP_TIMEOUT_PREDICATE)).orElse("60"));

            // set frontend did uri
            String value = getPropertyValue(FRONTEND_GENERATE_DID_PREDICATE);
            frontendGenerateDidURI = value == null ? null : new URI(value);
            value = getPropertyValue(FRONTEND_REGISTER_DID_PREDICATE);
            frontendRegisterDidURI = value == null ? null : new URI(value);
            value = getPropertyValue(FRONTEND_REVIEW_DID_PREDICATE);
            frontendReviewDidURI = value == null ? null : new URI(value);
            value = getPropertyValue(FRONTEND_CREATE_DID_PREDICATE);
            frontendCreateDidURI = value == null ? null : new URI(value);

            // set proxy
            httpProxyEnable = Boolean.parseBoolean(Optional.ofNullable(getPropertyValue(HTTP_PROXY_ENABLE_PREDICATE)).orElse("false"));
            if (httpProxyEnable) {
                httpProxyProtocol = Optional.ofNullable(getPropertyValue(HTTP_PROXY_PROTOCOL_PREDICATE))
                                            .filter(p -> p.equalsIgnoreCase("http") || p.equalsIgnoreCase("https"))
                                            .orElse("http");
                httpProxyUrl = Optional.ofNullable(getPropertyValue(HTTP_PROXY_URL_PREDICATE)).orElse("127.0.0.1");
                httpProxyPort = Integer.valueOf(Optional.ofNullable(getPropertyValue(HTTP_PROXY_PORT_PREDICATE)).orElse("8080"));
            }

            String callbackDomainListValue = getPropertyValue(CALLBACK_DOMAIN_LIST_PREDICATE);
            callbackDomainList = callbackDomainListValue == null ? Collections.emptyList() : Arrays.stream(callbackDomainListValue.split(",")).toList();

            LOGGER.debug("clientId={}", clientId);
            LOGGER.debug("redirectUri={}", redirectUri);
            LOGGER.debug("responseUri={}", responseUri);
            LOGGER.debug("jwksURI={}", jwksURI);
            LOGGER.debug("endpointUri={}", endpointUri);
            LOGGER.debug("clientMetadataUri={}", clientMetadataUri);
            LOGGER.debug("requestUri={}", requestUri);
            LOGGER.debug("presentationDefinitionUri={}", presentationDefinitionUri);
            LOGGER.debug("vpVerifyUri={}", vpVerifyUri);
            LOGGER.debug("issuer={}", issuer);
            LOGGER.debug("session_expired={} minutes", sessionExpiredTime);
            LOGGER.debug("verifyResult_expired={} minutes", verifyResultExpiredTime);
            LOGGER.debug("verifyResult_delete_after_query={}", verifyResultDeleteAfterQuery);
            LOGGER.debug("session_delete_cron={}", sessionDeleteCron);
            LOGGER.debug("verifyResult_delete_cron={}", verifyResultDeleteCron);
            LOGGER.debug("vpPresentationValidationTimeout={}", vpPresentationValidationHttpTimeout);
            LOGGER.debug("frontendServiceHttpTimeout={}", frontendServiceHttpTimeout);
            LOGGER.debug("frontendGenerateDidURI={}", frontendGenerateDidURI);
            LOGGER.debug("frontendRegisterDidURI={}", frontendRegisterDidURI);
            LOGGER.debug("frontendReviewDidURI={}", frontendReviewDidURI);
            LOGGER.debug("frontendCreateDidURI={}", frontendCreateDidURI);
            LOGGER.debug("httpProxyEnable={}", httpProxyEnable);
            if (httpProxyEnable) {
                LOGGER.debug("httpProxyProtocol={}", httpProxyProtocol);
                LOGGER.debug("httpProxyUrl={}", httpProxyUrl);
                LOGGER.debug("httpProxyPort={}", httpProxyPort);
            }

            LOGGER.info("VERSION={}", Constants.version);
        } catch (Exception exception) {
            LOGGER.error("set oidvp config error", exception);
            throw exception;
        }
    }

    private String getPropertyValue(String propertyName) {
        boolean isRequired = requiredProperties.contains(propertyName);
        String property = properties.get(propertyName);
        if (isRequired) {
            if (property == null || property.isEmpty() || property.isBlank()) {
                throw new IllegalArgumentException("property '" + propertyName + "' must be set");
            }
            return property;
        } else {
            return (property == null || property.isEmpty()) ? null : property;
        }
    }


    // getter
    public String getClientId() {
        return clientId;
    }

    public URI getClientMetadataUri() {
        return clientMetadataUri;
    }

    public URI getEndpointUri() {
        return endpointUri;
    }

    public String getIssuer() {
        return issuer;
    }

    public List<JWK> getPublicJWKs() {
        return publicJWKs;
    }

    public URI getJwksURI() {
        return jwksURI;
    }

    public URI getPresentationDefinitionUri() {
        return presentationDefinitionUri;
    }

    public URI getRedirectUri() {
        return redirectUri;
    }

    public URI getRequestUri() {
        return requestUri;
    }

    public URI getResponseUri() {
        return responseUri;
    }

    public URI getVpVerifyUri() {
        return vpVerifyUri;
    }

    public URI getOidvpUri() {
        return oidvpUri;
    }

    public Integer getSessionExpiredTime() {
        return sessionExpiredTime;
    }

    public Boolean getVerifyResultDeleteAfterQuery() {
        return verifyResultDeleteAfterQuery;
    }

    public Integer getVerifyResultExpiredTime() {
        return verifyResultExpiredTime;
    }

    public String getSessionDeleteCron() {
        return sessionDeleteCron;
    }

    public String getVerifyResultDeleteCron() {
        return verifyResultDeleteCron;
    }

    public Integer getVpPresentationValidationHttpTimeout() {
        return vpPresentationValidationHttpTimeout;
    }

    public Integer getFrontendServiceHttpTimeout() {
        return frontendServiceHttpTimeout;
    }

    public URI getFrontendGenerateDidURI() {
        return frontendGenerateDidURI;
    }

    public URI getFrontendRegisterDidURI() {
        return frontendRegisterDidURI;
    }

    public URI getFrontendReviewDidURI() {
        return frontendReviewDidURI;
    }

    public URI getFrontendCreateDidURI() {
        return frontendCreateDidURI;
    }

    public Integer getVerifierBusinessHttpTimeout() {
        return verifierBusinessHttpTimeout;
    }

    public List<String> getCallbackDomainList() {
        return callbackDomainList;
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
}
