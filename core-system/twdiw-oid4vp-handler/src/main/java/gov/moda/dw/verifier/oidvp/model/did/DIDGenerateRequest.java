package gov.moda.dw.verifier.oidvp.model.did;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.nimbusds.jose.jwk.ECKey;

public class DIDGenerateRequest {

    private final ECKey publicKeyJwk;

    public DIDGenerateRequest(ECKey publicKeyJwk) {
        this.publicKeyJwk = publicKeyJwk.toPublicJWK();
    }

    @JsonGetter("publicKeyJwk")
    @JsonRawValue
    public String serialize() {
        return publicKeyJwk.toJSONString();
    }

    public ECKey getPublicKeyJwk() {
        return publicKeyJwk;
    }
}
