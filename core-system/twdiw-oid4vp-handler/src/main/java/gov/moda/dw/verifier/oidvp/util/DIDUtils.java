package gov.moda.dw.verifier.oidvp.util;

import com.nimbusds.jose.jwk.JWK;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import io.ipfs.multibase.Base58;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DIDUtils {

    public static JWK extractPublicKeyFromDID(String did) throws OidvpException {
        String[] parts = did.split(":");
        if (parts.length != 3) {
            throw new OidvpRuntimeException(OidvpError.PARSE_DID_ERROR, "the did format not match did:key:xxxxx");
        }

        // check prefix `z`
        String multibase = parts[2];
        if (multibase == null || multibase.isBlank() || !multibase.startsWith("z")) {
            throw new OidvpRuntimeException(OidvpError.PARSE_DID_ERROR, "the multibase value of issuer's DID does not start with 'z'");
        }

        // discard the prefix `z`
        multibase = multibase.substring(1);

        try {
            // Base58 decode
            byte[] mbBytes = Base58.decode(multibase);

            // check prefix `D1D603`
            byte[] prefix = new byte[]{(byte) 0xD1, (byte) 0xD6, (byte) 0x03};
            if (mbBytes.length == 0 || !Arrays.equals(Arrays.copyOfRange(mbBytes, 0, 3), prefix)) {
                throw new OidvpRuntimeException(OidvpError.PARSE_DID_ERROR, "fail to decode issuer's DID (invalid hex data)");
            }

            // discard the prefix `D1D603`
            byte[] keyBytes = Arrays.copyOfRange(mbBytes, 3, mbBytes.length);

            // decode hex to get public key (JWK)
            String jwk = new String(keyBytes, StandardCharsets.UTF_8);

            return JWK.parse(jwk);
        } catch (Exception e) {
            throw new OidvpException(OidvpError.PARSE_DID_ERROR, e.getMessage(), e);
        }
    }
}
