package gov.moda.dw.manager.repository.custom;

import java.util.Optional;
import gov.moda.dw.manager.domain.Res;
import gov.moda.dw.manager.repository.ResRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Res entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomResRepository extends ResRepository {
  Optional<Res> findByResId(@Param("resId") String resId);
}
