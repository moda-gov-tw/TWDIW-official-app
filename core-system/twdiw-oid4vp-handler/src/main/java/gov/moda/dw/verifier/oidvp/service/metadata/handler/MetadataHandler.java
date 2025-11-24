package gov.moda.dw.verifier.oidvp.service.metadata.handler;

import gov.moda.dw.verifier.oidvp.model.MetadataFieldsRequest;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import java.util.Map;

public abstract class MetadataHandler {

    public abstract String getFieldName();

    public abstract void setField(VerifierMetadata metadata, Map<String, String> fieldMap);

    public abstract String checkAndGetFieldValue(MetadataFieldsRequest request);
}
