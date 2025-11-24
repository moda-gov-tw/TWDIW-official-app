package gov.moda.dw.verifier.oidvp.service.metadata.handler;

import gov.moda.dw.verifier.oidvp.domain.MetadataJpa;
import gov.moda.dw.verifier.oidvp.model.MetadataFieldsRequest;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class MetadataHandlerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataHandlerClient.class);
    private final List<MetadataHandler> handlers;

    public MetadataHandlerClient(List<MetadataHandler> handlers) {
        this.handlers = handlers;
        if (this.handlers.isEmpty()) {
            LOGGER.warn("metadata handler is empty");
        }
    }

    public List<MetadataJpa> collectUpdateFields(MetadataFieldsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("collect metadata field error: MetadataFieldsRequest must not be null");
        }
        ArrayList<MetadataJpa> jpaList = new ArrayList<>();
        for (MetadataHandler handler : handlers) {
            String fieldValue = handler.checkAndGetFieldValue(request);
            if (fieldValue != null) {
                MetadataJpa fieldToBeUpdated = new MetadataJpa(handler.getFieldName(), fieldValue);
                jpaList.add(fieldToBeUpdated);
            }
        }
        return jpaList;
    }

    public VerifierMetadata getVerifierMetadataByFields(List<MetadataJpa> metadataJpaList) {
        Assert.notNull(metadataJpaList, "metadataJpaList must not be null");
        VerifierMetadata verifierMetadata = new VerifierMetadata();
        HashMap<String, String> fieldMap = new HashMap<>();
        metadataJpaList.forEach(field -> fieldMap.put(field.getFieldName(), field.getFieldValue()));
        for (MetadataHandler handler : handlers) {
            handler.setField(verifierMetadata, fieldMap);
        }
        return verifierMetadata;
    }

    public static class FieldName {

        public static final String JWKS = "jwks";
        public static final String JWKS_URI = "jwks_uri";
        public static final String CLIENT_NAME = "client_name";
        public static final String TOS_URI = "tos_uri";
        public static final String VP_FORMATS = "vp_formats";
        public static final String RESPONSE_TYPES = "response_types";

        public static final HashSet<String> MODIFIABLE_FIELD_SET = new HashSet<>();

        static {
            MODIFIABLE_FIELD_SET.add(CLIENT_NAME);
            MODIFIABLE_FIELD_SET.add(TOS_URI);
        }
    }
}
