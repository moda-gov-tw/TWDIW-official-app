package gov.moda.dw.manager.repository.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.repository.VCItemRepository;

public interface CustomVCItemRepository extends VCItemRepository {
    
    VCItem findBySerialNoAndBusinessId(String serialNo, String businessId);
    
    List<VCItem> findBySerialNoAndBusinessIdAndIsTempAndTypeIn(String serialNo, String businessId, boolean isTemp,List<String> type);
    
    List<VCItem> findByIsTempAndTypeIn(boolean isTemp ,List<String> type);
    
    VCItem findBySerialNoAndBusinessIdAndIsTempTrue(String serialNo, String businessId);
    
    VCItem findBySerialNoAndBusinessIdAndIsTempFalse(String serialNo, String businessId);

    List<VCItem> findAllByBusinessId(String orgId);
    
    List<VCItem> findAllByBusinessIdOrderById(String orgIdForQuery);

    VCItem findByIdAndSerialNo(Long vcId, String vcSerialNo);

    List<VCItem> findAllByBusinessIdAndIsTempFalseOrderByIdDesc(String orgId);

    boolean existsByBusinessId(String orgId);

    List<VCItem> findAllByBusinessIdAndCrUserOrderByIdDesc(String orgIdForQuery, Long userId);

    @Query(
        value = "select it.* from vc_manager.vc_item it " +
        "join vc_manager.vc_item_data itData on it.id = itData.vc_item_id " +
        "where itData.id = :dataId",
        nativeQuery = true
    )
    Optional<VCItem> searchByIdByDataId(@Param("dataId") Long id);

    Optional<VCItem> findById(Long vcId);
    
    Optional<VCItem> findByIdAndIsTempFalse(Long vcId);
    
    VCItem findByIdAndIsTempTrue(Long vcId);

    long count();
}
