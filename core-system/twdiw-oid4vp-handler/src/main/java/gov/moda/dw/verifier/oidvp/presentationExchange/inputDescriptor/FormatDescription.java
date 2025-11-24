package gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashSet;

@JsonInclude(value = Include.NON_EMPTY)
public class FormatDescription {

    private HashSet<JwtAlgValue> alg;

    @JsonProperty("proof_type")
    private HashSet<LdpProofType> proofType;

    @JsonProperty("sd-jwt_alg_values")
    private HashSet<JwtAlgValue> sdJwtAlgValues;

    @JsonProperty("kb-jwt_alg_values")
    private HashSet<JwtAlgValue> kbJwtAlgValues;

    private FormatDescription() {
    }

    private FormatDescription(HashSet<JwtAlgValue> alg, HashSet<LdpProofType> proofType, HashSet<JwtAlgValue> sdJwtAlgValues, HashSet<JwtAlgValue> kbJwtAlgValues) {
        this.alg = alg;
        this.proofType = proofType;
        this.sdJwtAlgValues = sdJwtAlgValues;
        this.kbJwtAlgValues = kbJwtAlgValues;
    }

    public static FormatDescription alg(HashSet<JwtAlgValue> alg) {
        return new FormatDescription(alg, null, null, null);
    }

    public static FormatDescription proofType(HashSet<LdpProofType> proofType) {
        return new FormatDescription(null, proofType, null, null);
    }

    public static FormatDescription sdJwtAlgValues(HashSet<JwtAlgValue> sdJwtAlgValues) {
        return new FormatDescription(null, null, sdJwtAlgValues, null);
    }

    public static FormatDescription kbJwtAlgValues(HashSet<JwtAlgValue> kbJwtAlgValues) {
        return new FormatDescription(null, null, null, kbJwtAlgValues);
    }

    public static FormatDescription sdJwtAlgAndKbJwtAlg(HashSet<JwtAlgValue> sdJwtAlgValues, HashSet<JwtAlgValue> kbJwtAlgValues) {
        return new FormatDescription(null, null, sdJwtAlgValues, kbJwtAlgValues);
    }

    public HashSet<JwtAlgValue> getAlg() {
        return alg;
    }

    public HashSet<JwtAlgValue> getKbJwtAlgValues() {
        return kbJwtAlgValues;
    }

    public HashSet<LdpProofType> getProofType() {
        return proofType;
    }

    public HashSet<JwtAlgValue> getSdJwtAlgValues() {
        return sdJwtAlgValues;
    }

    public static class JwtAlgValue extends FormatAlgorithm {

        public static final JwtAlgValue HS256 = new JwtAlgValue("HS256");
        public static final JwtAlgValue HS384 = new JwtAlgValue("HS384");
        public static final JwtAlgValue HS512 = new JwtAlgValue("HS512");
        public static final JwtAlgValue RS256 = new JwtAlgValue("RS256");
        public static final JwtAlgValue RS384 = new JwtAlgValue("RS384");
        public static final JwtAlgValue RS512 = new JwtAlgValue("RS512");
        public static final JwtAlgValue ES256 = new JwtAlgValue("ES256");
        public static final JwtAlgValue ES384 = new JwtAlgValue("ES384");
        public static final JwtAlgValue ES512 = new JwtAlgValue("ES512");
        public static final JwtAlgValue PS256 = new JwtAlgValue("PS256");
        public static final JwtAlgValue PS384 = new JwtAlgValue("PS384");
        public static final JwtAlgValue PS512 = new JwtAlgValue("PS512");

        private static final Type type = Type.JWT;
        private String value;

        private JwtAlgValue() {
        }

        private JwtAlgValue(String value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return type;
        }

        @JsonValue
        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class LdpProofType extends FormatAlgorithm {

        public static final LdpProofType Ed25519Signature2018 = new LdpProofType("Ed25519Signature2018");
        public static final LdpProofType RsaSignature2018 = new LdpProofType("RsaSignature2018");
        public static final LdpProofType EcdsaSecp256k1Signature2019 = new LdpProofType("EcdsaSecp256k1Signature2019");
        public static final LdpProofType EcdsaSecp256k1RecoverySignature2020 = new LdpProofType("EcdsaSecp256k1RecoverySignature2020");
        public static final LdpProofType JsonWebSignature2020 = new LdpProofType("JsonWebSignature2020");
        public static final LdpProofType GpgSignature2020 = new LdpProofType("GpgSignature2020");
        public static final LdpProofType JcsEd25519Signature2020 = new LdpProofType("JcsEd25519Signature2020");
        public static final LdpProofType BbsBlsSignature2020 = new LdpProofType("BbsBlsSignature2020");
        public static final LdpProofType Bls12381G2Key2020 = new LdpProofType("Bls12381G2Key2020");

        private static final Type type = Type.LDP;
        private String value;

        private LdpProofType() {
        }

        private LdpProofType(String value) {
            this.value = value;
        }

        @Override
        public Type getType() {
            return type;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return getValue();
        }
    }

    public abstract static class FormatAlgorithm {

        public enum Type {JWT, LDP}

        public abstract Type getType();

        public abstract String getValue();
    }
}
