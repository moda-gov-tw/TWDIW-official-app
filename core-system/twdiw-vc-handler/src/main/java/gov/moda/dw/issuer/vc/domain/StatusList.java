package gov.moda.dw.issuer.vc.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

/**
 * entity of status list
 *
 * @version 20240902
 */
@Entity
@Table(name = "status_list")
public class StatusList {

    @Id
    @Column(name = "sid")
    private String sid;

    @Column(name = "gid")
    private int gid;

    @Column(name = "status_list_type")
    private String statusListType;

    @Column(name = "credential_type")
    private String credentialType;

    @Column(name = "issuance_date")
    private Timestamp issuanceDate;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @Column(name = "content")
    private String content;

    /**
     * default constructor of entity is NECESSARY for JPA
     */
    public StatusList() {
    }

    public String getSid() {
        return sid;
    }

    public StatusList setSid(String sid) {
        this.sid = sid;
        return this;
    }

    public int getGid() {
        return gid;
    }

    public StatusList setGid(int gid) {
        this.gid = gid;
        return this;
    }

    public String getStatusListType() {
        return statusListType;
    }

    public StatusList setStatusListType(String statusListType) {
        this.statusListType = statusListType;
        return this;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public StatusList setCredentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public Timestamp getIssuanceDate() {
        return issuanceDate;
    }

    public StatusList setIssuanceDate(Timestamp issuanceDate) {
        this.issuanceDate = issuanceDate;
        return this;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public StatusList setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public String getContent() {
        return content;
    }

    public StatusList setContent(String content) {
        this.content = content;
        return this;
    }
}
