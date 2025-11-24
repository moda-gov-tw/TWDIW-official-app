package gov.moda.dw.issuer.vc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.moda.dw.issuer.vc.domain.OrgEntity;

/**
 * Spring Data JPA repository for the {@link OrgEntity} entity.
 *
 * @version 20241206
 */
@Repository("OrgRepository")
public interface OrgRepository extends JpaRepository<OrgEntity, String>{

	Optional<OrgEntity> findByOrgId(String orgId);
}
