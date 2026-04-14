package gov.moda.dw.manager.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.service.dto.custom.CustomVPItemDTO;

/**
 * Spring Data JPA repository for the VPItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VPItemRepository extends JpaRepository<VPItem, Long>, JpaSpecificationExecutor<VPItem> {
	
	String sql = ""
	        + "select vi.id, "
	        + "vi.serial_no as serialNo, "
	        + "vi.\"name\", "
	        + "vi.cr_user as crUser, "
	        + "vi.business_id as businessId, "
	        + "vi.presentation_definition as presentationDefinition, "
	        + "coalesce(vi.up_datetime, vi.cr_datetime) as upDatetime, "
	        + "vi.purpose, "
	        + "vi.terms, "
	        + "vi.up_user, "
	        + "vi.group_info, "
	        + "case when vi.is_static = true then '1' "
	        + "when vi.is_offline = true then '2' "
	        + "else '' end as model, "
	        + "vi.verifier_service_url as verifierServiceUrl, "
	        + "vi.callback_url as callBackUrl, "
	        + "vi.tag, "
	        + "vi.field_info as fieldInfo "
	        + "from vp_item vi "
	        + "where (:crUser is null or vi.cr_user = :crUser) "
	        + "and (:businessId is null or vi.business_id = :businessId)";

	@Query(value = sql, countQuery = sql, nativeQuery = true)
	Page<CustomVPItemDTO> getVPPage(@Param("crUser") Long crUser, @Param("businessId") String businessId, Pageable pageable);

}
