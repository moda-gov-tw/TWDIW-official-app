package gov.moda.dw.verifier.oidvp.service.vp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.model.VpValidatedResult;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.VpValidatedException;
import gov.moda.dw.verifier.oidvp.util.HttpUtils;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class PresentationValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationValidationService.class);

    private final HttpUtils httpUtils;

    public PresentationValidationService(OidvpConfig oidvpConfig) {
        this.httpUtils = new HttpUtils.HttpUtilsBuilder(3, oidvpConfig.getVpPresentationValidationHttpTimeout()).build();
    }

    /**
     * call vp validate service to validate vp_token
     *
     * @param vpServiceUri vp validation uri
     * @param vpTokens     the {@link ArrayNode} of vp_token
     * @return if {@code vpTokens} is valid will return List of {@link VpValidatedResult}.
     * @throws VpValidatedException if vp validate fail.
     */
    public List<VpValidatedResult> validateVpToken(URI vpServiceUri, ArrayNode vpTokens, String transactionId) throws OidvpException {
        String requestParam = vpTokens.toString();
        try {
            // call vp validate service
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("transaction-id", transactionId);
            ResponseEntity<VpValidatedResult[]> response = httpUtils.doPost(
                vpServiceUri.toString(),
                requestParam,
                headers,
                VpValidatedResult[].class);

            if (response.getBody() == null) {
                throw new OidvpException(OidvpError.VP_VALIDATE_FAIL, "vp service response null");
            }
            ArrayList<VpValidatedResult> responseList = new ArrayList<>();
            Collections.addAll(responseList, response.getBody());

            return responseList;
        } catch (RestClientResponseException e) {
            // check is validate service's error or else
            int code = Optional.of(e.getResponseBodyAsString())
                               .map(JsonUtils::getJsonNode)
                               .filter(n -> !n.isNull())
                               .map(m -> m.get("code"))
                               .map(JsonNode::asInt)
                               .orElseThrow(() -> {
                                   LOGGER.error("call vp service error, http={}, response={}", e.getStatusCode().value(), e.getResponseBodyAsString());
                                   return new RestClientException("call vp service error.(http=" + e.getStatusCode().value() + ", response=[" + e.getResponseBodyAsString() + "])", e);
                               });
            LOGGER.error("VP service returned(code={}, response={})", code, e.getResponseBodyAsString());
            throw new VpValidatedException(OidvpError.VP_VALIDATE_FAIL, "vp validate fail (code=" + code + ")");
        } catch (Exception e) {
            LOGGER.error("call vp service error , {}", e.getMessage(), e);
            throw new VpValidatedException(OidvpError.CALL_VP_SERVICE_ERROR);
        }
    }

    public ArrayNode getVpTokenArrayNode(String vpToken) throws VpValidatedException {
        // check is single string or single json or json array
        Assert.notNull(vpToken, "vpToken must not be null");
        JsonNode _vpToken = JsonUtils.getJsonNode(vpToken);
        ArrayNode arrayNode;
        if (_vpToken.isNull()) {
            if (!vpToken.startsWith("eyJ")) {
                throw new VpValidatedException(OidvpError.INVALID_VP, "invalid format in 'vp_token' parameter");
            }
            LOGGER.debug("vp_token is jwt string");
            arrayNode = JsonUtils.buildJsonArray(vpToken);
        } else {
            if (_vpToken.isArray()) {
                LOGGER.debug("vp_token is json array");
                arrayNode = (ArrayNode) _vpToken;
            } else if (_vpToken.isObject()) {
                LOGGER.debug("vp_token is json ");
                arrayNode = JsonUtils.buildJsonArray(_vpToken);
            } else {
                throw new VpValidatedException(OidvpError.INVALID_VP, "invalid format in 'vp_token' parameter");
            }
        }
        return arrayNode;
    }

    public String extractHolderDid(List<VpValidatedResult> vpValidatedResults) throws VpValidatedException {
        if (vpValidatedResults == null || vpValidatedResults.isEmpty()) {
            return null;
        }
        if (vpValidatedResults.size() == 1) {
            return vpValidatedResults.get(0).getHolderDid();
        } else {
            // assert vps is always from the same holder now.
            List<String> holderDidList = vpValidatedResults.stream().map(VpValidatedResult::getHolderDid).distinct().toList();
            if (holderDidList.size() != 1) {
                throw new VpValidatedException(OidvpError.INVALID_VP, "the values of holder_did from vp are not the same");
            } else {
                return holderDidList.get(0);
            }
        }
    }
}
