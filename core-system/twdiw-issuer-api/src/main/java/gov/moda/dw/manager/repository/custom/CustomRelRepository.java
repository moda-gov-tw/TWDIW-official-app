package gov.moda.dw.manager.repository.custom;

import java.util.List;
import gov.moda.dw.manager.domain.Rel;
import gov.moda.dw.manager.repository.RelRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface CustomRelRepository extends RelRepository {
  List<Rel> findOneByLeftCode(String accessToken);
}
