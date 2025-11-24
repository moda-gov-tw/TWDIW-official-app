package gov.moda.dw.verifier.oidvp.service.metadata.handler;

import gov.moda.dw.verifier.oidvp.model.MetadataFieldsRequest;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import gov.moda.dw.verifier.oidvp.service.metadata.handler.MetadataHandlerClient.FieldName;
import gov.moda.dw.verifier.oidvp.util.URIUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TermsOfServiceUriHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TermsOfServiceUriHandler.class);
    public static final String fieldName = FieldName.TOS_URI;

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setField(VerifierMetadata metadata, Map<String, String> fieldMap) {
        String tos_uri = fieldMap.get(fieldName);
        try {
            URI tosURI = (tos_uri == null) ? null : new URI(tos_uri);
            metadata.setTermsOfServiceURI(tosURI);
        } catch (URISyntaxException e) {
            // should not happen
            LOGGER.error("fail to parse tos_uri from fieldMap: invalid uri", e);
            throw new IllegalArgumentException("fail to parse tos_uri from fieldMap: invalid uri", e);
        }
    }

    @Override
    public String checkAndGetFieldValue(MetadataFieldsRequest request) {
        String tosURI = request.getTosURI();
        if (tosURI == null) {
            return null;
        }

        if (URIUtils.isValidURI(tosURI)) {
            return tosURI;
        } else {
            throw new IllegalArgumentException("invalid tos_uri");
        }
    }
}
