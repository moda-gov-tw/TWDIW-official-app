package gov.moda.dw.manager.repository.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.moda.dw.manager.domain.VCCredential;
import gov.moda.dw.manager.repository.VCCredentialRepository;
import gov.moda.dw.manager.service.dto.custom.VCCredentialRevokeVcItemDataDTO;

@Primary
@Repository
public interface CustomVCCredentialRepository extends VCCredentialRepository {

    String REVOKE_VC_ITEM_DATA_BY_CID_SQL =
            " select vc.cid, vid.* " +
            " from vc.credential vc " +
            " left join vc_manager.vc_item_data vid on vid.transaction_id = vc.nonce " +
            " where vc.cid = :cid ";

    String REVOKE_VC_ITEM_DATA_LIST_SQL =
            " select vc.cid, vid.* " +
            " from vc.credential vc " +
            " left join vc_manager.vc_item_data vid on vid.transaction_id = vc.nonce " +
            " where vc.cid is not null " +
            " and vid.valid <> :validType " +
            " and vid.clear_schedule_id is not null " +
            " and vid.clear_schedule_datetime is not null ";

    String REVOKE_VC_CREDENTIAL_LIST_SQL =
            " select vc.cid, vc.last_update_time, vid.* " +
            " from vc.credential vc " +
            " left join vc_manager.vc_item_data vid on vid.transaction_id = vc.nonce " +
            " where vc.cid is not null " +
            " and vc.credential_status = cast(:validType as varchar) " +
            " and vid.valid <> :validType ";

    String REVOKE_VC_CREDENTIAL_NOT_IN_LOG_LIST_SQL = 
            " select vc.cid, vc.last_update_time " +
            " from vc.credential vc " +
            " left join vc_manager.vc_item_data vid on vid.transaction_id = vc.nonce " +
            " where vc.cid is not null " +
            " and vid.vc_cid is not null " +
            " and vc.credential_status = cast(:validType as varchar) " +
            " and not exists ( " +
            " select 1 " +
            " from vc_manager.vc_data_status_log vdsl " +
            " where vdsl.vc_cid = vc.cid " +
            " and vdsl.valid = :validType " +
            " ) ";

    @Query(value = REVOKE_VC_ITEM_DATA_BY_CID_SQL, countQuery = REVOKE_VC_ITEM_DATA_BY_CID_SQL, nativeQuery = true)
    Optional<VCCredentialRevokeVcItemDataDTO> findRevokeVcItemDataByCid(@Param("cid") String cid);

    @Query(value = REVOKE_VC_ITEM_DATA_LIST_SQL, countQuery = REVOKE_VC_ITEM_DATA_LIST_SQL, nativeQuery = true)
    List<VCCredentialRevokeVcItemDataDTO> findRevokeVcItemDataList(@Param("validType") int validType);

    @Query(value = REVOKE_VC_CREDENTIAL_LIST_SQL, countQuery = REVOKE_VC_CREDENTIAL_LIST_SQL, nativeQuery = true)
    List<VCCredentialRevokeVcItemDataDTO> findRevokeVcCredentialList(@Param("validType") int validType);

    @Query(value = REVOKE_VC_CREDENTIAL_NOT_IN_LOG_LIST_SQL, countQuery = REVOKE_VC_CREDENTIAL_NOT_IN_LOG_LIST_SQL, nativeQuery = true)
    List<VCCredentialRevokeVcItemDataDTO> findRevokeVcCredentialLogList(@Param("validType") int validType);

    List<VCCredential> findAllByCidIn(List<String> cids);

}
