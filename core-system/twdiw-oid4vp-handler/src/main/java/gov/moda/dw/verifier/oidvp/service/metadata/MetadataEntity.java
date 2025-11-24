package gov.moda.dw.verifier.oidvp.service.metadata;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.ResponseType;
import gov.moda.dw.verifier.oidvp.config.CacheManagerConfig;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.dao.MetadataDAO;
import gov.moda.dw.verifier.oidvp.domain.MetadataJpa;
import gov.moda.dw.verifier.oidvp.model.MetadataFieldsRequest;
import gov.moda.dw.verifier.oidvp.model.errors.InvalidParameterException;
import gov.moda.dw.verifier.oidvp.model.oid4vp.OidvpResponseType;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatDescription;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatDescription.JwtAlgValue;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;
import gov.moda.dw.verifier.oidvp.service.metadata.handler.MetadataHandlerClient;
import gov.moda.dw.verifier.oidvp.service.metadata.handler.MetadataHandlerClient.FieldName;
import gov.moda.dw.verifier.oidvp.service.metadata.jwk.PublicJWKProvider;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheManager = CacheManagerConfig.METADATA_CACHE_MANAGER, cacheNames = CacheManagerConfig.METADATA_CACHE_NAME)
public class MetadataEntity {

    public static final String KEY_VALUE = "#root.caches[0].name";

    private final MetadataDAO metadataDAO;
    private final MetadataHandlerClient metadataHandlerClient;

    private final HashSet<ResponseType> responseTypes;
    private final Map<FormatRegistry, FormatDescription> supportedFormat;
    private final URI jwksURI;
    private final List<PublicJWKProvider> publicJWKProviders;


    public MetadataEntity(MetadataDAO metadataDAO, MetadataHandlerClient metadataHandlerClient, OidvpConfig oidvpConfig, List<PublicJWKProvider> publicJWKProviders) {
        this.metadataDAO = metadataDAO;
        this.metadataHandlerClient = metadataHandlerClient;
        this.publicJWKProviders = publicJWKProviders;
        supportedFormat = getDefaultSupportedVpFormat();
        jwksURI = oidvpConfig.getJwksURI();
        responseTypes = new HashSet<>(Collections.singleton(OidvpResponseType.VPTOKEN));
    }

    @Cacheable(key = KEY_VALUE)
    public VerifierMetadata loadMetadata() throws SQLException {
        List<MetadataJpa> fields = metadataDAO.getAllFields();
        VerifierMetadata metadata = metadataHandlerClient.getVerifierMetadataByFields(fields);
        metadata.setResponseTypes(responseTypes);
        metadata.setVpFormats(supportedFormat);
        JWKSet publicJWKSet = getPublicJWKSet();
        metadata.setJWKSet((publicJWKSet.isEmpty()) ? null : publicJWKSet);
        metadata.setJWKSetURI(jwksURI);
        return metadata;
    }

    @CacheEvict(key = KEY_VALUE)
    public void saveMetadataProperty(MetadataFieldsRequest request) throws SQLException {
        List<MetadataJpa> toBeSavedList = metadataHandlerClient.collectUpdateFields(request);
        metadataDAO.saveAll(toBeSavedList);
    }

    @CacheEvict(key = KEY_VALUE)
    public void removeMetadataProperty(List<String> removeFields) throws SQLException {
        if (removeFields == null || removeFields.isEmpty()) {
            return;
        }
        List<String> fieldNameList = removeFields.stream().map(String::trim).toList();
        for (String name : fieldNameList) {
            if (!FieldName.MODIFIABLE_FIELD_SET.contains(name)) {
                throw new InvalidParameterException("unknown field to delete: " + name);
            }
        }
        metadataDAO.deleteFields(fieldNameList);
    }

    public JWKSet getPublicJWKSet() {
        return new JWKSet(collectJWK(publicJWKProviders));
    }

    private List<JWK> collectJWK(List<PublicJWKProvider> publicJWKProviders) {
        ArrayList<JWK> jwkList = new ArrayList<>();
        for (PublicJWKProvider publicJWKProvider : publicJWKProviders) {
            List<JWK> jwKs = publicJWKProvider.getPublicJWKs();
            if (jwKs != null && !jwKs.isEmpty()) {
                jwkList.addAll(jwKs);
            }
        }
        return jwkList;
    }

    private Map<FormatRegistry, FormatDescription> getDefaultSupportedVpFormat() {
        HashMap<FormatRegistry, FormatDescription> defaultFormat = new HashMap<>();

        // vp
        HashSet<JwtAlgValue> vpAlg = new HashSet<>();
        vpAlg.add(JwtAlgValue.ES256);
        defaultFormat.put(FormatRegistry.JWT_VP, FormatDescription.alg(vpAlg));

        // vc
        HashSet<JwtAlgValue> vcAlg = new HashSet<>();
        vcAlg.add(JwtAlgValue.ES256);
        defaultFormat.put(FormatRegistry.JWT_VC, FormatDescription.alg(vcAlg));

        return defaultFormat;
    }
}
