package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.RegularExpression;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RegularExpression entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegularExpressionRepository extends JpaRepository<RegularExpression, Long>, JpaSpecificationExecutor<RegularExpression> {}
