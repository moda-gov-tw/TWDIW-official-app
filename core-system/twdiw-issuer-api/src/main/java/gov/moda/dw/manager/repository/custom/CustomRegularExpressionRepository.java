package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.repository.RegularExpressionRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomRegularExpressionRepository extends RegularExpressionRepository {
    List<RegularExpression> findByType(String require);
}
