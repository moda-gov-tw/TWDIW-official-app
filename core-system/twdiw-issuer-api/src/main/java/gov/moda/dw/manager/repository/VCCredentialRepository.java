package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.VCCredential;
import gov.moda.dw.manager.service.dto.VCCredentialDTO;
import gov.moda.dw.manager.service.dto.custom.VCCredentialQueryDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VCCredentialRepository extends JpaRepository<VCCredential, String>, JpaSpecificationExecutor<VCCredential> {
    VCCredential findByCid(String vccid);

    String sql =
        " select vc.*, vi.name, vid.clear_schedule_datetime, vid.clear_schedule_id, vid.valid, vid.data_tag, vid.transaction_id, vi.business_id as org_id, org.org_tw_name, vi.serial_no , vid.expired " +
        " from vc.credential vc " +
        " left join vc_manager.vc_item_data vid on vid.transaction_id = vc.nonce " +
        " left join vc_manager.vc_item vi on vi.id = vid.vc_item_id " +
        " left join vc_manager.org org on org.org_id = vi.business_id " +
        " where (cast(:#{#params.dataTag} as text) is null or vid.data_tag like concat('%', COALESCE(:#{#params.dataTag}, ''), '%')) " +
        "   and (cast(:#{#params.transactionId} as text) is null or vid.transaction_id like concat('%', COALESCE(:#{#params.transactionId}, ''), '%')) " +
        "   and (cast(:#{#params.orgId} as text) is null or vi.business_id = cast(:#{#params.orgId} as text)) " +
        "   and (cast(:#{#params.crUserId} as text) is null or vi.cr_user = :#{#params.crUserId}) " +
        "   and (cast(:#{#params.vcSerialNo} as text) is null or vi.serial_no like concat('%', COALESCE(:#{#params.vcSerialNo}, ''), '%')) " +
        "   and (cast(:#{#params.credentialStatus} as text) is null or vc.credential_status = cast(:#{#params.credentialStatus} as text)) " +
        "   and (cast(:#{#params.issuanceDateStart} as timestamp) is null or vc.issuance_date >= cast(:#{#params.issuanceDateStart} as timestamp))" +
        "   and (cast(:#{#params.issuanceDateEnd} as timestamp) is null or vc.issuance_date <= cast(:#{#params.issuanceDateEnd} as timestamp))" +
        " order by " +
        " case when cast(:#{#params.sortType} as text) is not null and cast(:#{#params.sortType} as text) = 'desc' then vc.issuance_date end desc, " +
        " case when cast(:#{#params.sortType} as text) is not null and cast(:#{#params.sortType} as text) = 'asc' then vc.issuance_date end asc ";

    @Query(value = sql, countQuery = sql, nativeQuery = true)
    Page<VCCredentialDTO> findBySql(@Param("params") VCCredentialQueryDTO params, Pageable pageable);

    List<VCCredential> findAllByNonceIn(List<String> txIds);
}
