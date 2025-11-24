package gov.moda.dw.verifier.oidvp.common;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;

public enum SupportedALG {

  /** EC key use curve P-256(secp256r1), signed algorithm ECDSA-256 */
  EC_P256_ECDSA256("EC", Curve.P_256, null, JWSAlgorithm.ES256);

  private final String keyType;
  private final Curve curve;
  private final Integer keyLength;
  private final JWSAlgorithm algorithm;

  SupportedALG(
      final String keyType,
      final Curve curve,
      final Integer keyLength,
      final JWSAlgorithm algorithm) {
    this.keyType = keyType;
    this.curve = curve;
    this.keyLength = keyLength;
    this.algorithm = algorithm;
  }

  public Curve getCurve() {
    return curve;
  }

  public JWSAlgorithm getAlgorithm() {
    return algorithm;
  }

  public Integer getKeyLength() {
    return keyLength;
  }

  public String getKeyType() {
    return keyType;
  }
}
