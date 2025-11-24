package gov.moda.dw.verifier.vc.util;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.credentialstatus.CredentialStatus;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.nimbusds.jwt.JWTClaimsSet;
import foundation.identity.jsonld.JsonLDObject;
import foundation.identity.jsonld.JsonLDUtils;
import info.weboftrust.ldsignatures.LdProof;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class VcFromJwtConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static VerifiableCredential fromJwtVerifiableCredential(JwtVerifiableCredential jwtVerifiableCredential) {
        VerifiableCredential payloadVerifiableCredential = VerifiableCredential.fromJson(((VerifiableCredential) jwtVerifiableCredential.getPayloadObject()).toString());
        CredentialSubject payloadCredentialSubject = payloadVerifiableCredential.getCredentialSubject();
        CredentialSubject.removeFromJsonLdObject(payloadVerifiableCredential);
        VcBuilder<? extends VcBuilder<?>> verifiableCredentialBuilder = getBuilder().base(payloadVerifiableCredential)
                                                                                    .defaultContexts(false)
                                                                                    .defaultTypes(false);
        JWTClaimsSet payload = jwtVerifiableCredential.getPayload();
        String jwtId = payload.getJWTID();
        if (jwtId != null && payloadVerifiableCredential.getId() == null) {
            verifiableCredentialBuilder.id(URI.create(jwtId));
        }

        if (payloadCredentialSubject != null) {
            CredentialSubject.Builder<? extends CredentialSubject.Builder<?>> credentialSubjectBuilder
                = CredentialSubject.builder().base(payloadCredentialSubject);
            String subject = payload.getSubject();
            if (subject != null && payloadCredentialSubject.getId() == null) {
                credentialSubjectBuilder.id(URI.create(subject));
            }

            CredentialSubject credentialSubject = credentialSubjectBuilder.build();
            verifiableCredentialBuilder.credentialSubject(credentialSubject);
        }

        String issuer = payload.getIssuer();
        if (issuer != null && payloadVerifiableCredential.getIssuer() == null) {
            verifiableCredentialBuilder.issuer(URI.create(issuer));
        }

        Date notBeforeTime = payload.getNotBeforeTime();
        if (notBeforeTime != null && payloadVerifiableCredential.getIssuanceDate() == null) {
            verifiableCredentialBuilder.issuanceDate(notBeforeTime);
        }

        Date expirationTime = payload.getExpirationTime();
        if (expirationTime != null && payloadVerifiableCredential.getExpirationDate() == null) {
            verifiableCredentialBuilder.expirationDate(expirationTime);
        }

        return verifiableCredentialBuilder.build();
    }

    public static VcBuilder<? extends VcBuilder<?>> getBuilder() {
        return new VcBuilder<>(new VerifiableCredential());
    }

    public static class VcBuilder<B extends VcBuilder<B>> extends JsonLDObject.Builder<B> {

        private URI issuer;
        private Date issuanceDate;
        private Date expirationDate;
        private CredentialSubject credentialSubject;
        private CredentialStatus credentialStatus;
        private LdProof ldProof;

        public VcBuilder(JsonLDObject jsonLdObject) {
            super(jsonLdObject);
            forceContextsArray(true);
            forceTypesArray(true);
            defaultContexts(true);
            defaultTypes(true);
        }

        public VerifiableCredential build() {
            super.build();
            if (issuer != null) {
                JsonLDUtils.jsonLdAdd(jsonLdObject, "issuer", uriToString(issuer));
            }

            if (issuanceDate != null) {
                JsonLDUtils.jsonLdAdd(jsonLdObject, "issuanceDate", dateToString(issuanceDate));
            }

            if (expirationDate != null) {
                JsonLDUtils.jsonLdAdd(jsonLdObject, "expirationDate", dateToString(expirationDate));
            }

            if (credentialSubject != null) {
                credentialSubject.addToJsonLDObject(jsonLdObject);
            }

            if (credentialStatus != null) {
                credentialStatus.addToJsonLDObject(jsonLdObject);
            }

            if (ldProof != null) {
                ldProof.addToJsonLDObject(jsonLdObject);
            }

            return (VerifiableCredential) jsonLdObject;
        }

        public B issuer(URI issuer) {
            this.issuer = issuer;
            return (B) this;
        }

        public B issuanceDate(Date issuanceDate) {
            this.issuanceDate = issuanceDate;
            return (B) this;
        }

        public B expirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return (B) this;
        }

        public B credentialSubject(CredentialSubject credentialSubject) {
            this.credentialSubject = credentialSubject;
            return (B) this;
        }

        public B credentialStatus(CredentialStatus credentialStatus) {
            this.credentialStatus = credentialStatus;
            return (B) this;
        }

        public B ldProof(LdProof ldProof) {
            this.ldProof = ldProof;
            return (B) this;
        }
    }

    private static String uriToString(URI uri) {
        return uri == null ? null : uri.toString();
    }

    private static String dateToString(Date date) {
        if (date == null) {
            return null;
        } else {
            Instant instant = date.toInstant();
            return instant.atOffset(ZoneOffset.UTC).format(DATE_TIME_FORMATTER);
        }
    }
}
