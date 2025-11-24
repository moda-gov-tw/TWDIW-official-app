package gov.moda.dw.verifier.oidvp.service.metadata;

import com.nimbusds.jose.jwk.JWKSet;
import gov.moda.dw.verifier.oidvp.model.MetadataFieldsRequest;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private final MetadataEntity metadataEntity;


    public MetadataService(MetadataEntity metadataEntity) {
        this.metadataEntity = metadataEntity;
    }

    public VerifierMetadata getVerifierMetadata() throws SQLException {
        return metadataEntity.loadMetadata();
    }

    public void saveMetadataProperty(MetadataFieldsRequest request) throws SQLException {
        metadataEntity.saveMetadataProperty(request);
    }

    public VerifierMetadata saveMetadataPropertyAndLoad(MetadataFieldsRequest request) throws SQLException {
        metadataEntity.saveMetadataProperty(request);
        return getVerifierMetadata();
    }

    public void deleteMetadataProperty(MetadataFieldsRequest request) throws SQLException {
        List<String> removeFields = request.getRemoveFields();
        metadataEntity.removeMetadataProperty(removeFields);
    }

    public VerifierMetadata deleteMetadataPropertyAndLoad(MetadataFieldsRequest request) throws SQLException {
        List<String> removeFields = request.getRemoveFields();
        metadataEntity.removeMetadataProperty(removeFields);
        return getVerifierMetadata();
    }

    public JWKSet getPublicJWKSet() {
        return metadataEntity.getPublicJWKSet();
    }
}
