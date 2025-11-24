package gov.moda.dw.verifier.vc.service;

import gov.moda.dw.verifier.vc.service.dto.frontend.DidInfoResponseDTO;
import gov.moda.dw.verifier.vc.service.dto.vc.PublicKeyResponseDTO;
import gov.moda.dw.verifier.vc.service.dto.vc.StatusListResponseDTO;
import gov.moda.dw.verifier.vc.util.HttpUtils;
import gov.moda.dw.verifier.vc.util.Tuple;
import gov.moda.dw.verifier.vc.vo.PreloadSetting;
import gov.moda.dw.verifier.vc.vo.VpException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ResourceLoadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoadService.class);

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final PreloadSetting preloadSetting;

    public ResourceLoadService(PreloadSetting preloadSetting) {
        this.preloadSetting = preloadSetting;
    }

    /**
     * load issuer's public key
     *
     * @param jku url of issuer's public key
     * @param kid key id
     * @return issuer's public key
     */
    public Map<String, Object> loadIssuerPublicKey(String jku, String kid) throws VpException {
        if (jku == null || jku.isBlank() || kid == null || kid.isBlank()) {
            throw new VpException(VpException.ERR_CRED_LACK_OF_ISSUER_PUBLIC_KEY,
                "public key information not found in vc");
        }

        LOGGER.info("(jku, kid) = ({}, {})", jku, kid);

        // step 2: connect to issuer
        HttpUtils.Builder builder = new HttpUtils.Builder()
            .setUrl(jku)
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog());
        if (preloadSetting.isHttpProxyEnable()) {
            builder.setUseProxy(preloadSetting.isHttpProxyEnable())
                   .setProxyServerProtocol(preloadSetting.getHttpProxyProtocol())
                   .setProxyServerAddress(preloadSetting.getHttpProxyUrl())
                   .setProxyServerPort(preloadSetting.getHttpProxyPort());
        }
        HttpUtils httpUtils = builder.build();

        // connect to vc service
        Tuple.Pair<Integer, String> response = httpUtils.get();
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage =
                "fail to load issuer's public key. (http status code, response content) = (" + statusCode + ", "
                    + content + ")";
            throw new VpException(VpException.ERR_CONN_LOAD_ISSUER_PUBLIC_KEY_ERROR, errorMessage);
        }

        PublicKeyResponseDTO publicKeyResponseDTO = new PublicKeyResponseDTO(content);
        List<Map<String, Object>> keys = publicKeyResponseDTO.getKeys();
        if (keys == null || keys.isEmpty()) {
            throw new VpException(VpException.ERR_CONN_INVALID_ISSUER_PUBLIC_KEY, "invalid issuer's public key");
        }

        // find the matched key by kid
        Map<String, Object> publicKey = keys.stream()
                                            .filter(m -> kid.equals(m.get("kid")))
                                            .findFirst()
                                            .orElse(null);

        if (publicKey == null || publicKey.isEmpty()) {
            throw new VpException(VpException.ERR_CONN_NO_MATCHED_ISSUER_PUBLIC_KEY, "no matched issuer's public key");
        }

        return publicKey;
    }

    public String loadStatusList(String statusListUrl) throws VpException {
        // step 1: connect to issuer
        HttpUtils.Builder builder = new HttpUtils.Builder()
            .setUrl(statusListUrl)
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog());
        if (preloadSetting.isHttpProxyEnable()) {
            builder.setUseProxy(preloadSetting.isHttpProxyEnable())
                   .setProxyServerProtocol(preloadSetting.getHttpProxyProtocol())
                   .setProxyServerAddress(preloadSetting.getHttpProxyUrl())
                   .setProxyServerPort(preloadSetting.getHttpProxyPort());
        }
        HttpUtils httpUtils = builder.build();

        // connect to vc service
        Tuple.Pair<Integer, String> response = httpUtils.get();
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage =
                "fail to load issuer's status list. (http status code, response content) = (" + statusCode + ", "
                    + content + ")";
            throw new VpException(VpException.ERR_CONN_LOAD_ISSUER_STATUS_LIST_ERROR, errorMessage);
        }

        StatusListResponseDTO statusListResponseDTO = new StatusListResponseDTO(content);
        String statusList = Optional.of(statusListResponseDTO)
                                    .map(StatusListResponseDTO::getStatusList)
                                    .orElse(null);

        if (statusList == null || statusList.isBlank()) {
            throw new VpException(VpException.ERR_CONN_INVALID_ISSUER_STATUS_LIST, "invalid issuer's status list");
        }

        return statusList;
    }

    public String loadSchema(String schemaUrl) throws VpException {
        // step 1: connect to issuer
        HttpUtils.Builder builder = new HttpUtils.Builder()
            .setUrl(schemaUrl)
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog());
        if (preloadSetting.isHttpProxyEnable()) {
            builder.setUseProxy(preloadSetting.isHttpProxyEnable())
                   .setProxyServerProtocol(preloadSetting.getHttpProxyProtocol())
                   .setProxyServerAddress(preloadSetting.getHttpProxyUrl())
                   .setProxyServerPort(preloadSetting.getHttpProxyPort());
        }
        HttpUtils httpUtils = builder.build();

        // connect to vc service
        Tuple.Pair<Integer, String> response = httpUtils.get();
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage =
                "fail to load issuer's schema. (http status code, response content) = (" + statusCode + ", " + content
                    + ")";
            throw new VpException(VpException.ERR_CONN_LOAD_ISSUER_SCHEMA_ERROR, errorMessage);
        }

        return content;
    }

    /**
     * [dwfront-301i] call Frontend to query DID
     *
     * @param did issuer's did
     * @return DID query response
     * @throws VpException exception when fail to query DID
     */
    public int callFrontendToQueryDid(String did) throws VpException {

        String prefix = preloadSetting.getUrlFrontendQueryDid();
        String url = prefix.endsWith("/") ? prefix.concat(did) : prefix.concat("/").concat(did);

        // step 1: connect to frontend
        HttpUtils.Builder builder = new HttpUtils.Builder()
            .setUrl(url)
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog());
        if (preloadSetting.isHttpProxyEnable()) {
            builder.setUseProxy(preloadSetting.isHttpProxyEnable())
                   .setProxyServerProtocol(preloadSetting.getHttpProxyProtocol())
                   .setProxyServerAddress(preloadSetting.getHttpProxyUrl())
                   .setProxyServerPort(preloadSetting.getHttpProxyPort());
        }
        HttpUtils httpUtils = builder.build();

        // connect to frontend service
        Tuple.Pair<Integer, String> response = httpUtils.get();
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage =
                "fail to query DID. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VpException(VpException.ERR_DID_FRONTEND_QUERY_DID_ERROR, errorMessage);
        }

        DidInfoResponseDTO didInfoResponseDTO = new DidInfoResponseDTO(content);

        // check `status` field in `data`
        // 0: on chain not yet
        // 1: effective
        // 2: revoked
        // 3: fail to qualify
        return Optional.of(didInfoResponseDTO)
                       .map(DidInfoResponseDTO::getData)
                       .map(m -> m.get("status"))
                       .map(obj -> {
                           if (obj instanceof Integer) {
                               return (Integer) obj;
                           } else {
                               return -1;
                           }
                       })
                       .orElse(-1);
    }
}
