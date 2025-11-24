package gov.moda.dw.verifier.oidvp.repository;

import gov.moda.dw.verifier.oidvp.domain.PresentationDefinitionJpa;
import gov.moda.dw.verifier.oidvp.domain.PresentationDefinitionJpa.PresentationDefinitionJpaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PDRepository extends JpaRepository<PresentationDefinitionJpa, PresentationDefinitionJpaId> {

    @Transactional
    @Modifying
    @Query(value = "delete from presentation_definition where serial_no=? and business_id=?", nativeQuery = true)
    int deleteBySerialNoAndBusinessId(String serialNo, String businessId);
}
