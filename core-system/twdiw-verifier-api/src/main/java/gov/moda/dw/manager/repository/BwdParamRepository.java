package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.BwdParam;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BwdParam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BwdParamRepository extends JpaRepository<BwdParam, Long>, JpaSpecificationExecutor<BwdParam> {}
