package gov.moda.dw.issuer.vc.repository;

import gov.moda.dw.issuer.vc.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link Ticket} entity.
 *
 * @version 20240902
 */
@Repository("TicketRepository")
public interface TicketRepository extends JpaRepository<Ticket, String> {

    // [postgresql specific]
    // - use postgresql sequence, default sequence name is `seq_1` (need to set to database at initial stage)
    // - use `INSERT ... RETURNING *` statement, insert and return the new ticket
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO ticket (credential_type, ticket_number) VALUES (:credential_type, nextval(:sequence_name)) RETURNING *;")
    Ticket takeTicket(@Param("credential_type") String credentialType, @Param("sequence_name") String sequenceName);
}
