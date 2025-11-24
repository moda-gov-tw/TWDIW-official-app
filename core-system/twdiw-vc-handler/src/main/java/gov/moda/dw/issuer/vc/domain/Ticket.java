package gov.moda.dw.issuer.vc.domain;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

/**
 * entity of setting
 *
 * @version 20240902
 */
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "credential_type")
    private String credentialType;

    @Column(name = "ticket_number")
    private int ticketNumber;

    @Column(name = "created_at")
    private Timestamp createdAt;

    /**
     * default constructor of entity is NECESSARY for JPA
     */
    public Ticket() {
    }

    public int getId() {
        return id;
    }

    public Ticket setId(int id) {
        this.id = id;
        return this;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public Ticket setCredentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public Ticket setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Ticket setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
