package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.service.dto.custom.CustomVPItemDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the VPItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomVPItemRepository extends JpaRepository<VPItem, Long>, JpaSpecificationExecutor<VPItem> {

    int countBySerialNo(String serialNo);

    int countBySerialNoAndBusinessId(String serialNo, String businessId);

    Optional<VPItem> findBySerialNoAndBusinessId(String serialNo, String businessId);

    List<VPItem> findByIsOfflineTrue();

    String sql = """
            SELECT 
             vi.id, 
             vi.serial_no as serialNo, 
             vi.name, 
             vi.cr_user as crUser, 
             vi.business_id as businessId, 
             vi.presentation_definition as presentationDefinition, 
             coalesce(vi.up_datetime, vi.cr_datetime) as upDatetime, 
             vi.purpose, 
             vi.terms, 
             vi.up_user,
             vi.group_info,
             case when vi.is_static = true then '1'
             when vi.is_offline = true then '2'
             else '' end as model,
             vi.verifier_service_url as verifierServiceUrl,
             vi.callback_url as callBackUrl,
             vi.tag,
             vi.is_encrypt_enabled as isEncryptEnabled,
             vi.field_info as fieldInfo
            FROM vp_item vi 
            WHERE (:crUser is null or vi.cr_user = :crUser)
             AND (:businessId is null or vi.business_id = :businessId)
             AND (:serialNo is null or vi.serial_no like concat('%',:serialNo,'%'))
             AND (:name is null or vi.name like concat('%',:name,'%'))
             AND coalesce(vi.up_datetime, vi.cr_datetime) >= (coalesce(:startDate, coalesce(vi.up_datetime, vi.cr_datetime)))
             AND coalesce(vi.up_datetime, vi.cr_datetime) <= (coalesce(:endDate, coalesce(vi.up_datetime, vi.cr_datetime)))
            """;
    @Query(value = sql, countQuery = sql, nativeQuery = true)
    Page<CustomVPItemDTO> getVPPage(@Param("crUser") Long crUser, 
                                    @Param("businessId") String businessId,
                                    @Param("serialNo") String serialNo,
                                    @Param("name") String name,
                                    @Param("startDate") Instant startDate, 
                                    @Param("endDate") Instant endDate, Pageable pageable);
    
    boolean existsByBusinessId(String orgId);
}
