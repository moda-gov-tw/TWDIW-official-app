package gov.moda.dw.verifier.oidvp.repository;

import gov.moda.dw.verifier.oidvp.domain.OidvpPropertyJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OidvpPropertyRepository extends JpaRepository<OidvpPropertyJpa, String> {}
