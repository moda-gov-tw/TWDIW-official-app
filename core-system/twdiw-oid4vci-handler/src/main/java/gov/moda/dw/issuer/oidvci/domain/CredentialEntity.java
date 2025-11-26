package gov.moda.dw.issuer.oidvci.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //標示為實體類別
@Table(name = "credential") //DB_TABLE_NAME
public class CredentialEntity {

    @Id //標示何者為Primary Key
    @Column(name="cid")
    private String CID;

    @Column(name="credential_type")
    private String credentialType;

    @Column(name="credential_subject_id")
    private String credentialSubjectId;

    @Column(name="issuance_date")
    private String issuanceDate;

    @Column(name="expiration_date")
    private String expirationDate;

    @Column(name="content")
    private String Content;

    @Column(name="ticket_number")
    private String ticketNumber;

    @Column(name="last_update_time")
    private String lastUpdateTime;

    @Column(name="credential_status")
    private String credentialStatus;

    @Column(name="nonce")
    private String Nonce;

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getCredentialSubjectId() {
        return credentialSubjectId;
    }

    public void setCredentialSubjectId(String credentialSubjectId) {
        this.credentialSubjectId = credentialSubjectId;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCredentialStatus() {
        return credentialStatus;
    }

    public void setCredentialStatus(String credentialStatus) {
        this.credentialStatus = credentialStatus;
    }

    public String getNonce() {
        return Nonce;
    }

    public void setNonce(String Nonce) {
        this.Nonce = Nonce;
    }
}
