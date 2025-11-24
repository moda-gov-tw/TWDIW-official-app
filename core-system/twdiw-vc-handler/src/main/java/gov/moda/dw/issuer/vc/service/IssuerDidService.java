package gov.moda.dw.issuer.vc.service;

import com.danubetech.keyformats.crypto.ByteSigner;
import com.danubetech.keyformats.crypto.impl.P_256_ES256_PrivateKeySigner;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import gov.moda.dw.issuer.vc.repository.SettingRepository;
import gov.moda.dw.issuer.vc.service.dto.ErrorResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.ErrorResponseProperty;
import gov.moda.dw.issuer.vc.service.dto.IssuerDidRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.IssuerDidResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidCreateRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidCreateResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidGenerateRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidGenerateResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidRegisterRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidRegisterResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidReviewReqeustDTO;
import gov.moda.dw.issuer.vc.service.dto.frontend.DidReviewResponseDTO;
import gov.moda.dw.issuer.vc.util.*;
import gov.moda.dw.issuer.vc.vo.PreloadSetting;
import gov.moda.dw.issuer.vc.vo.VcException;
import gov.moda.dw.issuer.vc.vo.Definition;
import info.weboftrust.ldsignatures.adapter.JWSSignerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPrivateKey;
import java.util.Map;
import java.util.Optional;

/**
 * Issuer DID service
 *
 * @version 20240902
 */
@Service
public class IssuerDidService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssuerDidService.class);

    private final PreloadSetting preloadSetting;
    private final SettingRepository settingRepository;

    @Value("${vc.key.enc}")
    private String vcKeyEnc;

    public IssuerDidService(PreloadSetting preloadSetting, SettingRepository settingRepository) {
        this.preloadSetting = preloadSetting;
        this.settingRepository = settingRepository;
    }

    /**
     * [dwissuer-101i] register issuer's DID
     *
     * @param issuerDidRequest DID register request
     * @return (registration response, http status)
     */
    public Tuple.Pair<String, HttpStatus> register(IssuerDidRequestDTO issuerDidRequest) {

        String result;
        HttpStatus httpStatus;

        try {
        	// step 0: validate request
        	if(issuerDidRequest == null || !issuerDidRequest.validate()) {
        		throw new VcException(VcException.ERR_DID_REGISTER_DID_REQUEST, "invalid DID registration request");
        	}

        	Map<String, Object> org = issuerDidRequest.getOrg();
        	// if p7data is null or blank, let p7data be ""
        	String p7data = Optional.ofNullable(issuerDidRequest.getP7data())
        			.filter(p7 -> !p7.isBlank())
        			.orElse("");

        	LOGGER.info("[DID register request]::org = {}, p7data = {}", org, p7data);

            // step 1: generate key
            // TODO: generate key in keystore or SoftHSM and use alias as kid
            // TODO: decide key id
            String kid = "key-1";
            ECKey ecKey = KeyUtils.generateP256(kid);
            if (ecKey == null) {
                throw new VcException(VcException.ERR_SYS_GENERATE_KEY_ERROR, "fail to generate key");
            }

            Map<String, Object> publicKeyJwk = ecKey.toPublicJWK().toJSONObject();

            // get access token for frontend
            String accessToken = settingRepository.queryByPropName(PreloadSetting.NAME_ACCESS_TOKEN_FRONTEND_REGISTER_DID);
            if(accessToken == null || accessToken.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_SET_FRONTEND_ACCESS_TOKEN_YET_ERROR, "issuer has not yet set access token for frontend");
            }

            // step 2: generate DID
            DidGenerateRequestDTO didGenerateRequestDTO = new DidGenerateRequestDTO(publicKeyJwk);
            Console.showJson(didGenerateRequestDTO.toString(), "DID generation request");
            DidGenerateResponseDTO didGenerateResponseDTO = callFrontendToGenerateDid(didGenerateRequestDTO, accessToken);
            Console.showJson(didGenerateResponseDTO.toString(), "DID generation response");

            // check content
            int code = didGenerateResponseDTO.getCode();
            DidGenerateResponseDTO.Data data = didGenerateResponseDTO.getData();
            Map<String, Object> didDocument = Optional.ofNullable(data)
                .map(DidGenerateResponseDTO.Data::getDid)
                .orElse(null);
            if (code != 0 || data == null || didDocument == null || didDocument.isEmpty()) {
                String errorMessage = "fail to generate DID. (code = " + code + ")";
                throw new VcException(VcException.ERR_DID_FRONTEND_GENERATE_DID_ERROR, errorMessage);
            }

            // step 3.1: prepare create data for Fronted
            // sign DID document
            String signedData = signDidDocument(ecKey, didDocument);
            Map<String, Object> orgInfo = org;

            // step 3.2: create DID
            DidCreateRequestDTO didCreateRequestDTO = new DidCreateRequestDTO(signedData, orgInfo, Definition.FRONTEND_ORG_TYPE, p7data);
            Console.showJson(didCreateRequestDTO.toString(), "DID create request");
            DidCreateResponseDTO didCreateResponseDTO = callFrontendToCreateDid(didCreateRequestDTO, accessToken);
            Console.showJson(didCreateResponseDTO.toString(), "DID creation response");

            // check content
            code = didCreateResponseDTO.getCode();
            if(code != 0) {
            	String errorMessage = "fail to create DID. (code = " + code + ")";
            	throw new VcException(VcException.ERR_DID_FRONTEND_CREATE_DID_ERROR, errorMessage);
            }

            // extract did information
            String did = extractIdentifierFromDidDocument(didDocument);
            String cleanDid = Jsoup.clean(did, Safelist.none());

//            // step 3.1: prepare registration data for Fronted
//            // sign DID document
//            String signedData = signDidDocument(ecKey, didDocument);
//            Map<String, Object> orgInfo = org;
////            Map<String, Object> orgInfo = JsonUtils.jsToMap(preloadSetting.getIssuerOrgInfo());
//
//            // step 3.2: register DID
//            DidRegisterRequestDTO didRegisterRequestDTO = new DidRegisterRequestDTO(signedData, orgInfo, Definition.FRONTEND_ORG_TYPE);
//            Console.showJson(didRegisterRequestDTO.toString(), "DID registration request");
//            DidRegisterResponseDTO didRegisterResponseDTO = callFrontendToRegisterDid(didRegisterRequestDTO, accessToken);
//            Console.showJson(didRegisterResponseDTO.toString(), "DID registration response");
//
//            // check content
//            code = didRegisterResponseDTO.getCode();
//            if (code != 0) {
//                String errorMessage = "fail to register DID. (code = " + code + ")";
//                throw new VcException(VcException.ERR_DID_FRONTEND_REGISTER_DID_ERROR, errorMessage);
//            }
//
//            // extract did information
//            String did = extractIdentifierFromDidDocument(didDocument);
//
//            // step 4: review DID
//            DidReviewReqeustDTO didReviewReqeustDTO = new DidReviewReqeustDTO(did, true);
//            Console.showJson(didReviewReqeustDTO.toString(), "DID review request");
//            DidReviewResponseDTO didReviewResponseDTO = callFrontendToReviewDid(didReviewReqeustDTO, accessToken);
//            Console.showJson(didReviewResponseDTO.toString(), "DID review response");
//
//            // check content
//            code = didReviewResponseDTO.getCode();
//            if (code != 0) {
//                String errorMessage = "fail to review DID. (code = " + code + ")";
//                throw new VcException(VcException.ERR_DID_FRONTEND_REVIEW_DID_ERROR, errorMessage);
//            }

            // step 5: store key & DID to database
            // step 5.1: encrypt issuer key
            String iv = KeyUtils.genIV();
            if(vcKeyEnc == null || vcKeyEnc.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_NOT_SET_VC_KEY_ENC_ERROR, "invalid VC_KEY_ENC");
            }
            String issuerKeyCipher = KeyUtils.GCMEncrypt(ecKey.toJSONString(), vcKeyEnc, iv);

            int count = settingRepository.updateTwoPropValuesByPropName(
                PreloadSetting.NAME_ISSUER_KEY, issuerKeyCipher,
                PreloadSetting.NAME_ISSUER_DID, cleanDid);

            if (count != 2) {
                throw new VcException(VcException.ERR_DB_UPDATE_ERROR, "fail to store key and DID");
            }

            // step 6: reload setting
            boolean reloadOk = preloadSetting.reload();
            if (!reloadOk) {
                throw new VcException(VcException.ERR_SYS_RELOAD_SETTING_ERROR, "fail to reload setting");
            }

            result = new IssuerDidResponseDTO().setDid(cleanDid).toString();
            httpStatus = HttpStatus.CREATED;

        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
            ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_DID_REGISTER_DID_ERROR, "fail to register DID");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwfront-101i] call Frontend to generate DID
     *
     * @param request DID generation request
     * @return DID generation response
     * @throws VcException exception when fail to generate DID
     */
    private DidGenerateResponseDTO callFrontendToGenerateDid(DidGenerateRequestDTO request, String accessToken) throws VcException {

        HttpHeaders headers = new HttpHeaders();
        try{
            accessToken = StringUtils.normalizeSpace(accessToken);
        } catch (Exception e) {
            String errorMessage = "fail to generate DID. Because access token contains invalid characters";
            throw new VcException(VcException.ERR_DID_FRONTEND_GENERATE_DID_ERROR, errorMessage);
        }
        headers.add("Access-Token", accessToken);

        HttpUtils httpUtils = new HttpUtils.Builder()
            .setUrl(preloadSetting.getUrlFrontendGenerateDid())
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog())
            .setHeader(headers.toSingleValueMap())
            .build();

        // connect to frontend service
        Tuple.Pair<Integer, String> response = httpUtils.postJson(request.toString());
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage = "fail to generate DID. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VcException(VcException.ERR_DID_FRONTEND_GENERATE_DID_ERROR, errorMessage);
        }

        return new DidGenerateResponseDTO(content);
    }

    /**
     * [dwfront-102i] call Frontend to register DID
     *
     * @param request DID registration request
     * @return DID registration response
     * @throws VcException exception when fail to register DID
     */
    private DidRegisterResponseDTO callFrontendToRegisterDid(DidRegisterRequestDTO request, String accessToken) throws VcException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Token", accessToken);

        HttpUtils httpUtils = new HttpUtils.Builder()
            .setUrl(preloadSetting.getUrlFrontendRegisterDid())
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog())
            .setHeader(headers.toSingleValueMap())
            .build();

        // connect to frontend service
        Tuple.Pair<Integer, String> response = httpUtils.postJson(request.toString());
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage = "fail to register DID. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VcException(VcException.ERR_DID_FRONTEND_REGISTER_DID_ERROR, errorMessage);
        }

        return new DidRegisterResponseDTO(content);
    }

    /**
     * [dwfront-103i] call Frontend to review DID
     *
     * @param request DID review request
     * @return DID review response
     * @throws VcException exception when fail to review DID
     */
    private DidReviewResponseDTO callFrontendToReviewDid(DidReviewReqeustDTO request, String accessToken) throws VcException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Token", accessToken);

        HttpUtils httpUtils = new HttpUtils.Builder()
            .setUrl(preloadSetting.getUrlFrontendReviewDid())
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog())
            .setHeader(headers.toSingleValueMap())
            .build();

        // connect to frontend service
        Tuple.Pair<Integer, String> response = httpUtils.postJson(request.toString());
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage = "fail to review DID. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VcException(VcException.ERR_DID_FRONTEND_REVIEW_DID_ERROR, errorMessage);
        }

        return new DidReviewResponseDTO(content);
    }

    /**
     * sign DID document (only support ES256)
     *
     * @param ecKey EC key
     * @param didDocument DID document
     * @return signed result (JWT)
     */
    private String signDidDocument(ECKey ecKey, Map<String, Object> didDocument) throws VcException {

        try {
            // prepare issuer's key
            ECPrivateKey privateKey = ecKey.toECPrivateKey();
            String kid = ecKey.getKeyID();
            String publicKeyJwk = ecKey.toPublicJWK().toJSONString();
            // Console.showJson(publicKeyJwk, "issuer's public key");

            // construct JWT claim set
            JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(didDocument);

            // sign jwt
            ByteSigner byteSigner = new P_256_ES256_PrivateKeySigner(privateKey);
            return signJwt(
                byteSigner,
                jwtClaimsSet,
                null,
                publicKeyJwk,
                kid);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_DID_SIGN_JWT_ERROR, "fail to sign did jwt (for DID registration)");
        }
    }

    private String signJwt(
        ByteSigner byteSigner,
        JWTClaimsSet jwtClaimsSet,
        String jwtHeaderType,
        String publicKey,
        String kid) throws Exception {

        JWSHeader.Builder jwsHeaderBuilder;
        JWSSigner jwsSigner;

        // only support P-256
        if (byteSigner instanceof P_256_ES256_PrivateKeySigner) {
            jwsHeaderBuilder = new JWSHeader.Builder(JWSAlgorithm.ES256);
            jwsSigner = new JWSSignerAdapter(byteSigner, JWSAlgorithm.ES256);
        } else {
            throw new Exception("unsupported signer");
        }

        // JWT header - typ
        if (jwtHeaderType == null || jwtHeaderType.isBlank()) {
            jwsHeaderBuilder.type(JOSEObjectType.JWT);
        } else {
            jwsHeaderBuilder.type(new JOSEObjectType(jwtHeaderType));
        }

        // JWT header - kid
        if (kid != null && !kid.isBlank()) {
            jwsHeaderBuilder.keyID(kid);
        }

        // JWT header - jwk
        if (publicKey != null && !publicKey.isBlank()) {
            JWK jwk = JWK.parse(publicKey).toPublicJWK();
            jwsHeaderBuilder.jwk(jwk);
            // force to use the 'kid' of public key
            jwsHeaderBuilder.keyID(jwk.getKeyID());
        }

        JWSHeader jwsHeader = jwsHeaderBuilder.build();

        if (jwtClaimsSet == null) {
            throw new Exception("invalid JWT claims set");
        }

        // construct JWS object, then sign
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
        signedJWT.sign(jwsSigner);

        return signedJWT.serialize();
    }

    /**
     * extract identifier from DID document
     *
     * @param didDocument DID document
     * @return identifier
     */
    private String extractIdentifierFromDidDocument(Map<String, Object> didDocument) throws VcException {

        if (!didDocument.containsKey("id")) {
            throw new VcException(VcException.ERR_DID_PARSE_DID_FROM_DOCUMENT_ERROR, "`id` not found in DID document");
        }

        return (String) didDocument.get("id");
    }

    /**
     * [dwfront-105i] call Frontend to create DID
     *
     * @param request DID creation request
     * @return DID creation response
     * @throws VcException exception when fail to create DID
     */
    private DidCreateResponseDTO callFrontendToCreateDid(DidCreateRequestDTO request, String accessToken) throws VcException {

        HttpHeaders headers = new HttpHeaders();
        try{
            accessToken = StringUtils.normalizeSpace(accessToken);
        } catch (Exception e) {
            String errorMessage = "fail to create DID. Because access token contains invalid characters";
            throw new VcException(VcException.ERR_DID_FRONTEND_CREATE_DID_ERROR, errorMessage);
        }
        headers.add("Access-Token", accessToken);

        HttpUtils httpUtils = new HttpUtils.Builder()
            .setUrl(preloadSetting.getUrlFrontendCreateDid())
            .setAllowedHostnames(null)
            .setMaxConnectTimeout(preloadSetting.getHttpMaxConnectTimeout())
            .setMaxRetryTimes(preloadSetting.getHttpMaxRetryTimes())
            .setEnableLog(preloadSetting.isHttpEnableLog())
            .setHeader(headers.toSingleValueMap())
            .build();

        // connect to frontend service
        Tuple.Pair<Integer, String> response = httpUtils.postJson(request.toString());
        int statusCode = response.getA();
        String content = response.getB();

        // check http status code and response content
        if (statusCode != HttpStatus.OK.value() || content == null || content.isBlank()) {
            String errorMessage = "fail to create DID. (http status code, response content) = (" + statusCode + ", " + content + ")";
            throw new VcException(VcException.ERR_DID_FRONTEND_CREATE_DID_ERROR, errorMessage);
        }

        return new DidCreateResponseDTO(content);
    }
}
