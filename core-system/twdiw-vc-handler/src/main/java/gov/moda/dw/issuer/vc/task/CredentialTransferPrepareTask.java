package gov.moda.dw.issuer.vc.task;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authlete.sd.Disclosure;
import com.authlete.sd.SDObjectEncoder;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.credentialstatus.CredentialStatus;
import com.danubetech.verifiablecredentials.credentialstatus.StatusList2021Entry;

import foundation.identity.jsonld.JsonLDObject;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.DateUtils;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.Definition;
import gov.moda.dw.issuer.vc.vo.Policy;
import gov.moda.dw.issuer.vc.vo.VcException;

/**
 * credential transfer prepare task
 *
 * @version 20250428
 */
public class CredentialTransferPrepareTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(CredentialTransferPrepareTask.class);
	
	private final String holderDid;

    private final Map<String, Object> holderData;
    private final int statusListIndex;

    private final String credentialType;
    private final URI issuerId;
    private final URI credentialId;
    private final URI statusListId;
    private final URI schemaUri;
    
    private final String expirationTimeStr;
    
    private CredentialTransferPrepareTask(Builder builder) {
    	
    	this.holderDid = builder.holderDid;

        this.holderData = builder.holderData;
        this.statusListIndex = builder.statusListIndex;

        this.credentialType = builder.credentialType;
        this.issuerId = builder.issuerId;
        this.credentialId = builder.credentialId;
        this.statusListId = builder.statusListId;
        this.schemaUri = builder.schemaUri;
        this.expirationTimeStr = builder.expirationTimeStr;
    }
    
    public Tuple.Pair<VerifiableCredential, List<Disclosure>> start() throws VcException {

        VerifiableCredential vc = prepare(holderData);
        Console.showJson(vc.toJson(), "PLAIN VC JSON");

        return prepareSd();
    }
    
    public Tuple.Pair<VerifiableCredential, List<Disclosure>> prepareSd() throws VcException {

        // selective disclosure (SD-JWT)
        SDObjectEncoder encoder = new SDObjectEncoder();
        encoder.setHashAlgorithmIncluded(true);
        // disable decoy, default is (0.5, 1.5)
        encoder.setDecoyMagnification(0.0, 0.0);
        Map<String, Object> claims = encoder.encode(holderData);
        List<Disclosure> disclosureList = encoder.getDisclosures();

        return Tuple.collect(prepare(claims), disclosureList);
    }
    
    public VerifiableCredential prepare(Map<String, Object> claims) throws VcException {

        try {
            // prepare credential subject
            CredentialSubject credentialSubject = CredentialSubject.builder()
                .id(URI.create(holderDid))
                .claims(claims)
                .build();

            // prepare credential status
            String index = String.valueOf(statusListIndex);
            URI id = URI.create(statusListId.toString().concat("#").concat(index));
            CredentialStatus credentialStatus = StatusList2021Entry.builder()
                .id(id)
                .statusPurpose(Definition.StatusListType.revocation.name())
                .statusListIndex(index)
                .statusListCredential(statusListId)
                .build();

            // prepare credential schema
            Map<String, Object> credentialSchema = new HashMap<>();
            credentialSchema.put("id", schemaUri.toString());
            credentialSchema.put("type", Definition.DEFAULT_JSON_SCHEMA_TYPE);

            // calculate issuance time
            // transfer expiration time
            LocalDateTime issuanceTime = LocalDateTime.now();
            Instant instant = Instant.parse(expirationTimeStr);
            Date expirationDate = Date.from(instant);

            // construct credential
            VerifiableCredential vc = VerifiableCredential.builder()
                // additional contexts
                // .context(contextUri)
                // credential type
                .type(credentialType)
                // credential id, ex: https://digitalwallet.moda:8443/vc/api/credential/999
                .id(credentialId)
                // issuer id, ex: did:example:123456
                .issuer(issuerId)
                // TODO: use UTC time or local time (UTC+8) ?
                // issuance date, ex: 2024-09-02T18:56:59Z
                .issuanceDate(DateUtils.toDate(issuanceTime))
                // expiration date, ex: 2029-09-02T18:56:59Z
                .expirationDate(expirationDate)
                // credential subject
                .credentialSubject(credentialSubject)
                // credential status
                .credentialStatus(credentialStatus)
                .build();

            // append schema
            JsonLDObject.fromMap(credentialSchema).addToJsonLDObject(vc, Definition.VC_FIELD_NAME_CREDENTIAL_SCHEMA);

            return vc;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_CRED_PREPARE_VC_ERROR, "fail to prepare credential");
        }
    }
    
    public static class Builder {

        private String holderDid;

        private Map<String, Object> holderData;
        private int statusListIndex;

        private String credentialType;
        private URI issuerId;
        private URI credentialId;
        private URI statusListId;
        private URI schemaUri;
        
        private String expirationTimeStr;

        public Builder() {
        }

        public Builder setHolderDid(String holderDid) {
            this.holderDid = holderDid;
            return this;
        }

        public Builder setHolderData(Map<String, Object> holderData) {
            this.holderData = holderData;
            return this;
        }

        public Builder setStatusListIndex(int statusListIndex) {
            this.statusListIndex = statusListIndex;
            return this;
        }

        public Builder setCredentialType(String credentialType) {
			this.credentialType = credentialType;
			return this;
		}

		public Builder setIssuerId(URI issuerId) {
            this.issuerId = issuerId;
            return this;
        }

        public Builder setCredentialId(URI credentialId) {
            this.credentialId = credentialId;
            return this;
        }

        public Builder setStatusListId(URI statusListId) {
            this.statusListId = statusListId;
            return this;
        }

        public Builder setSchemaUri(URI schemaUri) {
            this.schemaUri = schemaUri;
            return this;
        }

        public Builder setExpirationTimeStr(String expirationTimeStr) {
			this.expirationTimeStr = expirationTimeStr;
			return this;
		}

		public CredentialTransferPrepareTask build() {
            // TODO: check all required fields
            return new CredentialTransferPrepareTask(this);
        }
    }
}
