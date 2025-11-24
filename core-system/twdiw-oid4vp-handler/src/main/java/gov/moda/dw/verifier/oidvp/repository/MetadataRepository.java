package gov.moda.dw.verifier.oidvp.repository;

import gov.moda.dw.verifier.oidvp.domain.MetadataJpa;
import gov.moda.dw.verifier.oidvp.domain.OidvpPropertyJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetadataRepository extends JpaRepository<MetadataJpa, String> {}
