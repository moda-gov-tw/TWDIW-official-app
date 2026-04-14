package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.VCItemFieldRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface CustomVCItemFieldRepository extends VCItemFieldRepository {
    List<VCItemField> findByVcItemId(Long id);

    @Query(
        value = "SELECT vcItemField.* " +
        " FROM vc_item vcItem " +
        " LEFT JOIN vc_item_field vcItemField ON vcItem.id = vcItemField.vc_item_id " +
        " WHERE vcItem.business_id = :businessId and vcItemField.ename = :ename and vcItemField.type = :type",
        nativeQuery = true
    )
    List<VCItemField> findByBusinessIdAndEname(String businessId, String ename, String type);

    @Query(value = "select * from vc_item_field where regular_expression_id = :id limit 1", nativeQuery = true)
    VCItemField findByRegularExpressionId(Long id);

    List<VCItemField> findByVcItemIdOrderById(Long id);

    VCItemField findByVcItemIdAndEname(Long vcItemId, String ename);
    
    void deleteByVcItemId(Long vcItemId);
}
