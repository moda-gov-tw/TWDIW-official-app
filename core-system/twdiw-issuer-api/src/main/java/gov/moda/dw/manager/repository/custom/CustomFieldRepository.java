package gov.moda.dw.manager.repository.custom;

import aj.org.objectweb.asm.commons.Remapper;
import gov.moda.dw.manager.domain.Field;
import gov.moda.dw.manager.repository.FieldRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface CustomFieldRepository extends FieldRepository {
    List<Field> findByEnameIn(List<String> ename);

    Field findByBusinessIdAndId(String orgId, Long id);

    void deleteByBusinessIdAndId(String orgId, Long id);

    List<Field> findByBusinessIdAndEnameIn(String businessId, List<String> ename);

    @Query(value = "select * from field where regular_expression_id = :id limit 1", nativeQuery = true)
    Field findByRegularExpressionId(Long id);
}
