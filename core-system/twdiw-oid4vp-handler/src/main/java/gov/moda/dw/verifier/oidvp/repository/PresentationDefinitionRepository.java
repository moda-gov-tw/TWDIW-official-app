package gov.moda.dw.verifier.oidvp.repository;

import gov.moda.dw.verifier.oidvp.domain.VpItemJpa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationDefinitionRepository extends JpaRepository<VpItemJpa, String> {

    Optional<VpItemJpa> findByBusinessIdAndSerialNumber(String businessId, String serialNumber);

    default Optional<VpItemJpa> getPresentationDefinition(String businessId, String serialNumber) {
        return findByBusinessIdAndSerialNumber(businessId, serialNumber);
    }
}
