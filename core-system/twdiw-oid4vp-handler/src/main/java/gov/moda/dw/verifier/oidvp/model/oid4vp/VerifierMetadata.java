package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.nimbusds.oauth2.sdk.client.ClientMetadata;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatDescription;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.util.Map;

public class VerifierMetadata extends ClientMetadata {

    // new parameter vp_formats
    private Map<FormatRegistry, FormatDescription> vpFormats;

    public VerifierMetadata() {
        super();
    }

    public void setVpFormats(Map<FormatRegistry, FormatDescription> vpFormats) {
        Map<String, Object> map = JsonUtils.convertObjectToMap(vpFormats);
        if (map == null) {
            throw new IllegalArgumentException("invalid vp_formats format");
        }
        this.vpFormats = vpFormats;
        super.setCustomField("vp_formats", map);
    }

    public Map<FormatRegistry, FormatDescription> getVpFormats() {
        return vpFormats;
    }
}
