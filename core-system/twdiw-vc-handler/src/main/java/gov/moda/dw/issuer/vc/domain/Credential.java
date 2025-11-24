package gov.moda.dw.issuer.vc.domain;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

/**
 * entity of credential
 *
 * @version 20240902
 */
@Entity
@Table(name = "credential")
public class Credential {

    @Id
    @Column(name = "cid")
    private String cid;

    @Column(name = "credential_type")
    private String credentialType;

    @Column(name = "credential_subject_id")
    private String credentialSubjectId;

    @Column(name = "issuance_date")
    private Timestamp issuanceDate;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @Column(name = "content")
    private String content;

    @Column(name = "ticket_number")
    private int ticketNumber;

    @Column(name = "last_update_time")
    private Timestamp lastUpdateTime;

    @Column(name = "credential_status")
    private String credentialStatus;

    @Column(name = "nonce")
    private String nonce;

    /**
     * default constructor of entity is NECESSARY for JPA
     */
    public Credential() {
    }

    public String getCid() {
        return cid;
    }

    public Credential setCid(String cid) {
        this.cid = cid;
        return this;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public Credential setCredentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public String getCredentialSubjectId() {
        return credentialSubjectId;
    }

    public Credential setCredentialSubjectId(String credentialSubjectId) {
        this.credentialSubjectId = credentialSubjectId;
        return this;
    }

    public Timestamp getIssuanceDate() {
        return issuanceDate;
    }

    public Credential setIssuanceDate(Timestamp issuanceDate) {
        this.issuanceDate = issuanceDate;
        return this;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public Credential setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Credential setContent(String content) {
        this.content = content;
        return this;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public Credential setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
        return this;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Credential setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    public String getCredentialStatus() {
        return credentialStatus;
    }

    public Credential setCredentialStatus(String credentialStatus) {
        this.credentialStatus = credentialStatus;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public Credential setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
