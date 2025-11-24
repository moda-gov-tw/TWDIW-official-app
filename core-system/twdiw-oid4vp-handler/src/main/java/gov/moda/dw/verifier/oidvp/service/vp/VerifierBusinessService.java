package gov.moda.dw.verifier.oidvp.service.vp;

import com.fasterxml.jackson.databind.JsonNode;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.model.VerifyResult;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.verifierBusiness.VerifierBusinessCallbackRequest;
import gov.moda.dw.verifier.oidvp.model.verifierBusiness.VerifierBusinessCallbackResponse;
import gov.moda.dw.verifier.oidvp.util.HttpUtils;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import gov.moda.dw.verifier.oidvp.util.URIUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class VerifierBusinessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifierBusinessService.class);
    private static final int retryTimes = 2;
    private static final int duration = 2;
    public static final List<String> DEFAULT_CALLBACK_DOMAIN_LIST =
        Arrays.asList(
            "moda-digitalwallet-verifier-manager-service-svc",
            "moda-digitalwallet-docker-verifier-manager-service"
        );

    private final HttpUtils httpUtils;
    private final List<String> VALID_CALLBACK_DOMAIN_LIST;

    public VerifierBusinessService(OidvpConfig oidvpConfig) {
        httpUtils = new HttpUtils.HttpUtilsBuilder(3, oidvpConfig.getVerifierBusinessHttpTimeout()).setRetry(retryTimes, duration).build();
        VALID_CALLBACK_DOMAIN_LIST = oidvpConfig.getCallbackDomainList() == null || oidvpConfig.getCallbackDomainList().isEmpty() ? DEFAULT_CALLBACK_DOMAIN_LIST : oidvpConfig.getCallbackDomainList();
    }

    @Async("CallbackTaskExecutor")
    public void doVerifyResultCallbackAsync(String callbackUrl, VerifyResult verifyResult, String transactionId, String vpUid) {
        try {
            doVerifyResultCallback(callbackUrl, verifyResult, transactionId, vpUid);
            LOGGER.info("callback success.");
        } catch (Exception e) {
            // if callback failed, doing nothing for now.
            LOGGER.error("CALLBACK FAILED: path={}, {}", callbackUrl, e.getMessage(), e);
        }
    }

    public VerifierBusinessCallbackResponse doVerifyResultCallback(String callbackUrl, VerifyResult verifyResult, String transactionId, String vpUid) throws OidvpException {
        if (callbackUrl == null) {
            throw new IllegalArgumentException("callbackUrl must not be null");
        }
        if (verifyResult == null) {
            throw new IllegalArgumentException("verifyResult must not be null");
        }

        VerifierBusinessCallbackRequest verifierBusinessCallbackRequest = new VerifierBusinessCallbackRequest(transactionId, verifyResult, vpUid);
        try {
            ResponseEntity<VerifierBusinessCallbackResponse> responseEntity = httpUtils.doPost(callbackUrl, verifierBusinessCallbackRequest, null, VerifierBusinessCallbackResponse.class);
            VerifierBusinessCallbackResponse response = responseEntity.getBody();
            if (response == null) {
                throw new OidvpException(OidvpError.VERIFIER_BUSINESS_CALLBACK_RESPONSE_ERROR, "verifier business callback response null");
            }
            return response;
        } catch (RestClientResponseException e) {
            VerifierBusinessCallbackResponse response =
                Optional.of(e.getResponseBodyAsString())
                        .map(JsonUtils::getJsonNode)
                        .filter(n -> !n.isNull())
                        .map(j -> {
                            JsonNode code = j.get("code");
                            JsonNode message = j.get("message");
                            if (code == null || message == null) {
                                return null;
                            }
                            return new VerifierBusinessCallbackResponse(code.asText(), message.asText());
                        })
                        .orElseThrow(() -> {
                            LOGGER.error("call verifier business callback error, http={}, response={}", e.getStatusCode().value(), e.getResponseBodyAsString());
                            return new RestClientException("call verifier business callback error.(http=" + e.getStatusCode().value() + ", response=[" + e.getResponseBodyAsString() + "])", e);
                        });
            throw new OidvpException(OidvpError.VERIFIER_BUSINESS_CALLBACK_RESPONSE_ERROR, "verifier business callback response error, (http=" + e.getStatusCode().value() + ", code=" + response.getCode() + ", message=" + response.getMessage() + ").");
        } catch (Exception e) {
            LOGGER.error("call verifier business callback error(callback url={}), {}", callbackUrl, e.getMessage());
            throw new OidvpException(OidvpError.CALL_VERIFIER_BUSINESS_CALLBACK_ERROR, "call verifier business callback error, " + e.getMessage(), e);
        }
    }

    public boolean validateCallbackURI(String callback) {
        if (URIUtils.isValidURI(callback)) {
            try {
                URI callbackURI = new URI(callback);
                String host = callbackURI.getHost();
                if (VALID_CALLBACK_DOMAIN_LIST.contains(host)) {
                    return true;
                } else {
                    LOGGER.warn("invalid callback host: {}", host);
                    return false;
                }
            } catch (URISyntaxException e) {
                LOGGER.warn("parse callback url error: {}", e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }
}
