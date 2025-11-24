package gov.moda.dw.verifier.oidvp.service.metadata.handler;

import gov.moda.dw.verifier.oidvp.model.MetadataFieldsRequest;
import gov.moda.dw.verifier.oidvp.model.oid4vp.VerifierMetadata;
import gov.moda.dw.verifier.oidvp.service.metadata.handler.MetadataHandlerClient.FieldName;
import java.util.Map;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class ClientNameHandler extends MetadataHandler {

    private static final String fieldName = FieldName.CLIENT_NAME;

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setField(VerifierMetadata metadata, Map<String, String> fieldMap) {
        metadata.setName(fieldMap.get(fieldName));
    }

    @Override
    public String checkAndGetFieldValue(MetadataFieldsRequest request) {
        String clientName = request.getClientName();
        if (clientName == null) {
            return null;
        }

        if (clientName.length() > 100) {
            throw new IllegalArgumentException("invalid client_name length");
        }
        return StringEscapeUtils.escapeHtml4(clientName);
    }
}
