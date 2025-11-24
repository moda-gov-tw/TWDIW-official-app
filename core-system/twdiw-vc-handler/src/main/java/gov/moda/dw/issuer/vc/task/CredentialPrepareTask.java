package gov.moda.dw.issuer.vc.task;

import com.authlete.sd.Disclosure;
import com.authlete.sd.SDObjectEncoder;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.credentialstatus.CredentialStatus;
import com.danubetech.verifiablecredentials.credentialstatus.StatusList2021Entry;
import foundation.identity.jsonld.JsonLDObject;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.DateUtils;
import gov.moda.dw.issuer.vc.util.JsonUtils;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.Definition;
import gov.moda.dw.issuer.vc.vo.Policy;
import gov.moda.dw.issuer.vc.vo.VcException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * credential prepare task
 *
 * @version 20250526
 */
public class CredentialPrepareTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialPrepareTask.class);

    private final String holderDid;

    private final Map<String, Object> holderData;
    private final int statusListIndex;

    private final Policy policy;
    private final URI issuerId;
    private final URI credentialId;
    private final URI statusListId;
    private final URI schemaUri;
    // private final URI contextUri;
    
    private final LocalDateTime selectedIssuanceDate;
    private final LocalDateTime selectedExpirationDate;
    
    private final URI statusListIdSuspension;

    private CredentialPrepareTask(Builder builder) {

        this.holderDid = builder.holderDid;

        this.holderData = builder.holderData;
        this.statusListIndex = builder.statusListIndex;

        this.policy = builder.policy;
        this.issuerId = builder.issuerId;
        this.credentialId = builder.credentialId;
        this.statusListId = builder.statusListId;
        this.schemaUri = builder.schemaUri;
        // this.contextUri = builder.contextUri;
        
        this.selectedIssuanceDate = builder.selectedIssuanceDate;
        this.selectedExpirationDate = builder.selectedExpirationDate;
        
        this.statusListIdSuspension = builder.statusListIdSuspension;
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
            
            URI idSuspension = URI.create(statusListIdSuspension.toString().concat("#").concat(index));
            CredentialStatus credentialStatusSuspension = StatusList2021Entry.builder()
                .id(idSuspension)
                .statusPurpose(Definition.StatusListType.suspension.name())
                .statusListIndex(index)
                .statusListCredential(statusListIdSuspension)
                .build();
            List<CredentialStatus> statusLists = List.of(credentialStatus, credentialStatusSuspension);

            // prepare credential schema
            Map<String, Object> credentialSchema = new HashMap<>();
            credentialSchema.put("id", schemaUri.toString());
            credentialSchema.put("type", Definition.DEFAULT_JSON_SCHEMA_TYPE);
            
            LocalDateTime issuanceTime = null;
            LocalDateTime expirationTime = null;
            
            issuanceTime = selectedIssuanceDate;
            expirationTime = selectedExpirationDate;
            
            // construct credential
            VerifiableCredential vc = VerifiableCredential.builder()
                // additional contexts
                // .context(contextUri)
                // credential type
                .type(policy.getCredentialType())
                // credential id, ex: https://digitalwallet.moda:8443/vc/api/credential/999
                .id(credentialId)
                // issuer id, ex: did:example:123456
                .issuer(issuerId)
                // TODO: use UTC time or local time (UTC+8) ?
                // issuance date, ex: 2024-09-02T18:56:59Z
                .issuanceDate(DateUtils.toDate(issuanceTime))
                // expiration date, ex: 2029-09-02T18:56:59Z
                .expirationDate(DateUtils.toDate(expirationTime))
                // credential subject
                .credentialSubject(credentialSubject)
                // credential status
                .credentialStatus(credentialStatus)
                .build();
            
            List<Map<String, Object>> jacksonArray = new ArrayList<>();
            for (CredentialStatus cs : statusLists) {
                Map<String, Object> map = cs.getJsonObject(); // 原本是 Map<String, Object>
                jacksonArray.add(map); // 不轉 JSONObject，而是原生 map
            }
            vc.getJsonObject().put("credentialStatus", jacksonArray);

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

        private Policy policy;
        private URI issuerId;
        private URI credentialId;
        private URI statusListId;
        private URI schemaUri;
        // private URI contextUri;
        
        private LocalDateTime selectedIssuanceDate;
        private LocalDateTime selectedExpirationDate;
        
        private URI statusListIdSuspension;

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

        public Builder setPolicy(Policy policy) {
            this.policy = policy;
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

//        public Builder setContextUri(URI contextUri) {
//            this.contextUri = contextUri;
//            return this;
//        }

		public Builder setSelectedIssuanceDate(LocalDateTime selectedIssuanceDate) {
			this.selectedIssuanceDate = selectedIssuanceDate;
			return this;
		}

		public Builder setSelectedExpirationDate(LocalDateTime selectedExpirationDate) {
			this.selectedExpirationDate = selectedExpirationDate;
			return this;
		}

		public Builder setStatusListIdSuspension(URI statusListIdSuspension) {
			this.statusListIdSuspension = statusListIdSuspension;
			return this;
		}

		public CredentialPrepareTask build() {
            // TODO: check all required fields
            return new CredentialPrepareTask(this);
        }
    }
}
