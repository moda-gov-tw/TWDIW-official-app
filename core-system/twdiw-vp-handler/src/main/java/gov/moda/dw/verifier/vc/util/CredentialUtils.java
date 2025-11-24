package gov.moda.dw.verifier.vc.util;

import com.authlete.sd.Disclosure;
import com.authlete.sd.SDObjectDecoder;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import foundation.identity.jsonld.JsonLDUtils;
import gov.moda.dw.verifier.vc.vo.VpException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialUtils.class);
    public static final String CREDENTIAL_SUBJECT_PREDICATE = "credentialSubject";

    /**
     * re-construct sd jwt vc(for credential_subject field) to plain vc
     *
     * @param jwtVc          jwt vc
     * @param disclosureList disclosure list
     * @return plain vc
     */
    public static VerifiableCredential reconstructVc(JwtVerifiableCredential jwtVc, List<Disclosure> disclosureList)
        throws VpException
    {
        if (jwtVc == null) {
            throw new VpException(VpException.ERR_ILLEGAL_ARGUMENT, "jwtVc must not be null");
        }
        if (disclosureList == null) {
            throw new VpException(VpException.ERR_ILLEGAL_ARGUMENT, "disclosureList must not be null");
        }

        VerifiableCredential vc = VcFromJwtConverter.fromJwtVerifiableCredential(jwtVc);

        SDObjectDecoder decoder = new SDObjectDecoder();
        Map<String, Object> plaintexts = decoder.decode(vc.getCredentialSubject().toMap(), disclosureList);

        HashMap<String, Object> credentialSubject = new HashMap<>();
        credentialSubject.put(CREDENTIAL_SUBJECT_PREDICATE, plaintexts);

        JsonLDUtils.jsonLdRemove(vc, CREDENTIAL_SUBJECT_PREDICATE);
        JsonLDUtils.jsonLdAddAll(vc, credentialSubject);

        return vc;
    }
}
