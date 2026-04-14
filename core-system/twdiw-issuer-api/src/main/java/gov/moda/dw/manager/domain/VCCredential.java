package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "credential", schema = "vc")
public class VCCredential implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cid")
    private String cid;

    @Column(name = "credential_type")
    private String credentialType;

    @Column(name = "credential_subject_id")
    private String credentialSubjectId;

    @Column(name = "issuance_date")
    private Instant issuanceDate;

    @Column(name = "expiration_date")
    private Instant expirationDate;

    @Column(name = "content")
    private String content;

    @Column(name = "ticket_number")
    private Long ticketNumber;

    @Column(name = "last_update_time")
    private Instant lastUpdateTime;

    @Column(name = "credential_status")
    private String credentialStatus;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getCredentialStatus() {
        return credentialStatus;
    }

    public void setCredentialStatus(String credentialStatus) {
        this.credentialStatus = credentialStatus;
    }

    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Instant lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Instant getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Instant issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getCredentialSubjectId() {
        return credentialSubjectId;
    }

    public void setCredentialSubjectId(String credentialSubjectId) {
        this.credentialSubjectId = credentialSubjectId;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Column(name = "nonce")
    private String nonce;
}
