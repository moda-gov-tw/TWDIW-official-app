package gov.moda.dw.issuer.vc.service;

import com.nimbusds.jose.jwk.ECKey;
import gov.moda.dw.issuer.vc.domain.StatusList;
import gov.moda.dw.issuer.vc.repository.SettingRepository;
import gov.moda.dw.issuer.vc.repository.StatusListRepository;
import gov.moda.dw.issuer.vc.service.dto.*;
import gov.moda.dw.issuer.vc.util.DateUtils;
import gov.moda.dw.issuer.vc.util.JsonUtils;
import gov.moda.dw.issuer.vc.util.KeyUtils;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.Definition;
import gov.moda.dw.issuer.vc.vo.Policy;
import gov.moda.dw.issuer.vc.vo.PreloadSetting;
import gov.moda.dw.issuer.vc.vo.VcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * public information service
 *
 * @version 20240902
 */
@Service
public class PublicInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicInfoService.class);

    private final PreloadSetting preloadSetting;
    private final StatusListRepository statusListRepository;
    private final SettingRepository settingRepository;
    
    @Value("${vc.key.enc}")
    private String vcKeyEnc;

    public PublicInfoService(PreloadSetting preloadSetting, StatusListRepository statusListRepository,
    		SettingRepository settingRepository) {
        this.preloadSetting = preloadSetting;
        this.statusListRepository = statusListRepository;
        this.settingRepository = settingRepository;
    }

    /**
     * [dwissuer-107i] get status list
     *
     * @param credentialType associated credential type, ex: VirtualCardCredential
     * @param groupName associated group name, ex: r123
     * @return (status list response, http status)
     */
    public Tuple.Pair<String, HttpStatus> getStatusList(String credentialType, String groupName) {

        String result;
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            // step 1: validate request
            if (credentialType == null || credentialType.isBlank()) {
                throw new VcException(VcException.ERR_INFO_INVALID_CREDENTIAL_TYPE, "invalid credential type");
            }

            if (groupName == null || groupName.isBlank()) {
                throw new VcException(VcException.ERR_INFO_INVALID_GROUP_NAME, "invalid status list group name");
            }

            Tuple.Pair<Definition.StatusListType, Integer> pair = parseByGroupName(groupName);
            Definition.StatusListType statusListType = pair.getA();
            int gid = pair.getB();

            // step 2: query latest status list
            StatusList statusList = statusListRepository.queryLatestStatusList(credentialType, statusListType.name(),
                    gid);
            if (statusList == null) {
                throw new VcException(VcException.ERR_INFO_STATUS_LIST_NOT_FOUND, "status list not found");
            }
            String latestStatusList = statusList.getContent();

            result = new StatusListResponseDTO().setStatusList(latestStatusList).toString();

        } catch (VcException e) {

        	if(e.getCode() == VcException.ERR_INFO_STATUS_LIST_NOT_FOUND)
        		LOGGER.warn(e.getMessage(), e);
        	else
        		LOGGER.error(e.getMessage(), e);
            ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_SL_QUERY_STATUS_LIST_ERROR, "fail to query status list");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwissuer-107i] get schema
     *
     * @param schemaName schema name, ex: virtual-card-v1.json
     * @return (schema, http status)
     */
    public Tuple.Pair<String, HttpStatus> getSchema(String schemaName) {

        String result;
        HttpStatus httpStatus;

        try {
            // step 1: validate request
            if (schemaName == null || schemaName.isBlank()) {
                throw new VcException(VcException.ERR_INFO_INVALID_SCHEMA_NAME, "invalid schema name");
            }

            // step 2: load schema
            // TODO: preload policy from database
            Policy policy = loadCredentialPolicy(schemaName);
            String schema = Optional.ofNullable(policy)
                .map(Policy::getSchemaContent)
                .orElse(null);

            if (schema == null || schema.isBlank()) {
                throw new VcException(VcException.ERR_INFO_SCHEMA_NOT_FOUND, "schema not found");
            }

            result = schema;
            httpStatus = HttpStatus.OK;

        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
            ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_INFO_SCHEMA_NOT_FOUND, "schema not found");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * [dwissuer-107i] get public keys
     *
     * @return (public keys, http status)
     */
    public Tuple.Pair<String, HttpStatus> getPublicKeys() {

        String result;
        HttpStatus httpStatus;

        try {
            // step 1: prepare issuer's key
            // TODO: consider how to store issuer's key
            String issuerKeyInDB = settingRepository.queryByPropName(PreloadSetting.NAME_ISSUER_KEY);
            if (issuerKeyInDB == null || issuerKeyInDB.isBlank()) {
                throw new VcException(VcException.ERR_INFO_PUBLIC_KEY_NOT_FOUND, "issuer's public key not found");
            }
            
            // step 1.1: if issuer key is encrypted, decrypt it
            String issuerKey = null;
            try {
            	if(!KeyUtils.isPossiblyJwk(issuerKeyInDB)) {
            		issuerKey = KeyUtils.GCMDecrypt(issuerKeyInDB, vcKeyEnc);
            	}
            	else
            		issuerKey = issuerKeyInDB;
            }catch(Exception e) {
            	LOGGER.error(e.getMessage(), e);
            	throw new VcException(VcException.ERR_SYS_DECRYPT_CIPHER_ERROR, "Failed to decrypt key cipher");
            }
            if(issuerKey == null || issuerKey.isBlank()) {
            	throw new VcException(VcException.ERR_SYS_DECRYPT_RESULT_NULL_ERROR, "The decrypted value is empty or null");
            }

            ECKey ecKey = ECKey.parse(issuerKey);
            Map<String, Object> issuerPublicKey = JsonUtils.jsToMap(ecKey.toPublicJWK().toJSONString());
            
            // step 2: prepare status list signing key
            String statusListKeyInDB = preloadSetting.getStatusListKey();
            if(statusListKeyInDB == null || statusListKeyInDB.isBlank()) {
            	throw new VcException(VcException.ERR_INFO_PUBLIC_KEY_NOT_FOUND, "status list signing key not found");
            }
            
            // step 2.1: if status list key is encrypted, decrypt it
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
            
            ECKey statusListEcKey = ECKey.parse(statusListKey);
            Map<String, Object> statusListPublicKey = JsonUtils.jsToMap(statusListEcKey.toPublicJWK().toJSONString());

            // step 3: store 2 keys in List
            List<Map<String, Object>> keys = new ArrayList<>();
            keys.add(issuerPublicKey);
            keys.add(statusListPublicKey);
            
            result = new PublicKeyResponseDTO(keys).toString();
            httpStatus = HttpStatus.OK;

        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
            ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_INFO_PUBLIC_KEY_NOT_FOUND, "fail to show issuer's public key");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }

        return Tuple.collect(result, httpStatus);
    }

    /**
     * parse group name
     * <p>
     * ex: r999 -> (revocation, 999)
     *
     * @param groupName group name
     * @return (status list group, gid)
     */
    private Tuple.Pair<Definition.StatusListType, Integer> parseByGroupName(String groupName) {

        if (groupName != null && groupName.length() >= 2) {

            try {
                String tag = groupName.substring(0, 1);
                int gid = Integer.parseInt(groupName.substring(1));

                Definition.StatusListType slg = Definition.StatusListType.getInstance(tag);
                if (slg != null) {
                    return Tuple.collect(slg, gid);
                }

            } catch (Exception ignored) {
            }
        }

        return null;
    }

    /**
     * load credential policy
     *
     * @param schemaName associated schema name, ex: virtual-card-v1.json
     * @return policy
     */
    private Policy loadCredentialPolicy(String schemaName) {

        String id = preloadSetting.getUrlVcBasePath() + "/api/schema/" + schemaName;

        return new Policy()
            .setPid(UUID.randomUUID().toString())
            .setCredentialType("VirtualCardCredential")
            .setSchemaName(schemaName)
            // generated from `SchemaUtil.generateSchema()`
            .setSchemaContent("{\"$id\":\"" + id + "\",\"$schema\":\"https://json-schema.org/draft/2020-12/schema#\",\"title\":\"VirtualCardCredential\",\"description\":\"VirtualCardCredential using JsonSchema\",\"type\":\"object\",\"properties\":{\"credentialSubject\":{\"title\":\"credentialSubject\",\"type\":\"object\",\"properties\":{\"id\":{\"title\":\"id\",\"type\":\"string\",\"format\":\"uri\"},\"id_number\":{\"title\":\"id_number\",\"type\":\"string\"},\"full_name\":{\"title\":\"full_name\",\"type\":\"string\"},\"address\":{\"title\":\"address\",\"type\":\"string\"}}}}}")
            // effective duration: 5 years
            .setEffectiveDuration(new Policy.Duration(5, DateUtils.TimeUnit.YEAR))
            .setSignatureAlgorithm("ES256");
    }
}
