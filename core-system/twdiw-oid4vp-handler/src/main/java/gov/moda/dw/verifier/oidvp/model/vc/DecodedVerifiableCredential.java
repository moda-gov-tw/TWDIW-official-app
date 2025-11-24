package gov.moda.dw.verifier.oidvp.model.vc;

import com.fasterxml.jackson.databind.JsonNode;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;

public class DecodedVerifiableCredential {

    private final String vcPath;
    private final FormatRegistry format;
    private final JsonNode credential;
    private final Boolean limitDisclosure;

    /**
     * create DecodedVerifiableCredential object
     *
     * @param vcPath          vcPath in the presentation_submission
     * @param format          vc format
     * @param credential      vc. if vc is jwt_vc/jwt_vc must decode to raw vc format
     * @param limitDisclosure if vc supported limitDisclosure
     */
    public DecodedVerifiableCredential(String vcPath, FormatRegistry format, JsonNode credential, Boolean limitDisclosure) {
        this.vcPath = vcPath;
        this.format = format;
        this.credential = credential;
        this.limitDisclosure = limitDisclosure;
    }

    public String getVcPath() {
        return vcPath;
    }

    public JsonNode getCredential() {
        return credential;
    }

    public FormatRegistry getFormat() {
        return format;
    }

    public Boolean isLimitDisclosure() {
        return FormatRegistry.VC_SD_JWT.equals(format) || limitDisclosure;
    }

    public String getJsonString() {
        return credential.toString();
    }

    @Override
    public String toString() {
        return getJsonString();
    }
}
