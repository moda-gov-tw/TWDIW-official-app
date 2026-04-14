package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.Field;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Field entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FieldRepository extends JpaRepository<Field, Long>, JpaSpecificationExecutor<Field> {
    Page<Field> findByBusinessId(String businessId, Pageable pageable);
    Page<Field> findByBusinessIdAndType(String businessId, String Type, Pageable pageable);
}
