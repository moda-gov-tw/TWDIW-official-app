package gov.moda.dw.verifier.vc.util;

import gov.moda.dw.verifier.vc.vo.VpException;
import io.ipfs.multibase.Base58;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DidUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DidUtils.class);

    /**
     * extract public key from the DID of issuer
     *
     * @param did issuer's did
     * @return public key
     * @throws VpException exception when fail to query DID
     */
    public static Map<String, Object> extractIssuerPublicKey(String did) throws VpException {

        // check the format of issuer's DID
        String[] parts = did.split(":");
        if (parts.length != 3) {
            throw new VpException(VpException.ERR_CRED_INVALID_ISSUER_DID_FORMAT, "the format of issuer's DID DOES NOT match did:key:xxxxx");
        }

        // check prefix `z`
        String multibase = parts[2];
        if (multibase == null || multibase.isBlank() || !multibase.startsWith("z")) {
            throw new VpException(VpException.ERR_CRED_INVALID_ISSUER_DID_FORMAT, "the multibase value of issuer's DID DOES NOT start with `z`");
        }

        // discard the prefix `z`
        multibase = multibase.substring(1);

        try {
            // Base58 decode
            byte[] mbBytes = Base58.decode(multibase);

            // check prefix `D1D603`
            byte[] prefix = new byte[]{(byte) 0xD1, (byte) 0xD6, (byte) 0x03};
            if (mbBytes.length == 0 || !Arrays.equals(Arrays.copyOfRange(mbBytes, 0, 3), prefix)) {
                throw new VpException(VpException.ERR_CRED_INVALID_ISSUER_DID_FORMAT, "fail to decode issuer's DID (invalid hex data)");
            }

            // discard the prefix `D1D603`
            byte[] keyBytes = Arrays.copyOfRange(mbBytes, 3, mbBytes.length);

            // decode hex to get public key (JWK)
            String jwk = new String(keyBytes, StandardCharsets.UTF_8);
            Map<String, Object> publicKey = Optional.of(jwk)
                                                    .map(JsonUtils::jsToMap)
                                                    .orElse(null);

            if (publicKey == null || publicKey.isEmpty()) {
                throw new VpException(VpException.ERR_CRED_INVALID_ISSUER_DID_FORMAT, "fail to decode issuer's DID");
            }

            return publicKey;
        } catch (VpException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_CRED_INVALID_ISSUER_DID_FORMAT, "fail to decode issuer's DID");
        }
    }
}
