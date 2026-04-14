package gov.moda.dw.manager.repository.custom;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.repository.VCItemDataRepository;
import gov.moda.dw.manager.service.dto.custom.ClearVcItemDataDTO;
import gov.moda.dw.manager.service.dto.custom.GetVcCredentialDTO;
import gov.moda.dw.manager.service.dto.custom.GetVcItemDataDTO;

@Primary
@Repository
public interface CustomVCItemDataRepository extends VCItemDataRepository {

    String CLEAR_VC_ITEM_DATA_LIST_SQL =
            " select vi.serial_no, vi.name, vc.cid, vc.issuance_date, vid.* " +
            " from vc_manager.vc_item_data vid " +
            " left join vc_manager.vc_item vi on vi.id = vid.vc_item_id " +
            " left join vc.credential vc on vc.nonce = vid.transaction_id " +
            " where vid.valid <> :validType " +
            " and vid.clear_schedule_id = :clearScheduleId " +
            " and vid.clear_schedule_datetime = :clearScheduleDatetime ";

    String FIND_BY_DATA_TAG_LIST_SQL =
            """
                select
                    vc.cid as cid,
                    vc.credentialType as credentialType,
                    vc.issuanceDate as issuanceDate,
                    vc.expirationDate as expirationDate,
                    vc.credentialStatus as credentialStatus
                from
                    VCCredential vc
                left join VCItemData vid on
                    vc.nonce = vid.transactionId
                where
                    vc.cid is not null
                    and vid.dataTag = :dataTag
            """;

    String FIND_VC_ITEM_DATA_LIST_SQL =
            """
                select
                    vid.dataTag as dataTag,
                    vc.cid as cid,
                    vc.credentialType as credentialType,
                    vc.issuanceDate as issuanceDate,
                    vc.expirationDate as expirationDate,
                    vc.credentialStatus as credentialStatus
                from
                    VCCredential vc
                left join VCItemData vid on
                    vc.nonce = vid.transactionId
                where
                    vc.cid is not null
                    and ((:dataTag is null or :dataTag = '') or vid.dataTag = :dataTag)
                    and ((:vcUid is null or :vcUid = '') or vc.credentialType = :vcUid)
                    and ((:credentialStatus is null or :credentialStatus = '') or vc.credentialStatus = :credentialStatus)
            """;

    Optional<List<VCItemData>> findByIdIn(List<Long> ids);

    @Modifying
    @Query(
        "UPDATE VCItemData e SET e.clearScheduleId = :clearScheduleId,e.clearScheduleDatetime = :clearScheduleDatetime WHERE e.id IN (SELECT p.id From VCItemData p Where p.content is not null and p.clearScheduleId is null and p.businessId =:businessId )"
    )
    int updateContentNull(
        @Param("clearScheduleId") Long clearScheduleId,
        @Param("clearScheduleDatetime") Instant clearScheduleDatetime,
        @Param("businessId") String businessId
    );

    @Modifying
    @Query(
        "UPDATE VCItemData e SET e.content = NULL, e.pureContent = NULL , e.clearScheduleId = :clearScheduleId,e.clearScheduleDatetime = :clearScheduleDatetime WHERE e.id IN (SELECT p.id From VCItemData p Where p.content is not null and p.clearScheduleId is null and p.businessId =:businessId )"
    )
    int updateContentNullWithClean(
        @Param("clearScheduleId") Long clearScheduleId,
        @Param("clearScheduleDatetime") Instant clearScheduleDatetime,
        @Param("businessId") String businessId
    );

    @Modifying
    @Query("UPDATE VCItemData e SET e.valid = :valid WHERE e.id =:id")
    int updateValidByVcCid(@Param("valid") Integer valid, @Param("id") Long id);

    @Modifying
    @Query("UPDATE VCItemData e SET e.valid = :valid, e.scheduleRevokeMessage = :msg WHERE e.id =:id")
    int updateValidAndMessageById(@Param("valid") Integer valid, @Param("id") Long id, @Param("msg") String msg);

    @Modifying
    @Query("UPDATE VCItemData e SET e.scheduleRevokeMessage = :msg WHERE e.id =:id")
    int updateMessageById(@Param("id") Long id, @Param("msg") String msg);

    VCItemData findFirstByVcCid(String vcCid);

    boolean existsByVcItemId(Long id);

    List<VCItemData> findByClearScheduleIdAndClearScheduleDatetime(Long scheduleIdLong, Instant clearScheduleDatetime);

    VCItemData findBytransactionId(String nonce);

    VCItemData findFirstByTransactionId(String nonce);

    @Query(value = CLEAR_VC_ITEM_DATA_LIST_SQL, countQuery = CLEAR_VC_ITEM_DATA_LIST_SQL, nativeQuery = true)
    Page<ClearVcItemDataDTO> findClearVcItemDataList(@Param("validType") int validType,
            @Param("clearScheduleId") long clearScheduleId,
            @Param("clearScheduleDatetime") Instant clearScheduleDatetime, Pageable pageable);

    @Query(value = FIND_BY_DATA_TAG_LIST_SQL)
    Page<GetVcCredentialDTO> findByDataTag(@Param("dataTag") String dataTag, Pageable pageable);

    @Query(value = FIND_VC_ITEM_DATA_LIST_SQL)
    Page<GetVcItemDataDTO> findVcItemData(@Param("dataTag") String dataTag, @Param("vcUid") String vcUid,
            @Param("credentialStatus") String credentialStatus, Pageable pageable);

    List<VCItemData> findAllByTransactionIdIn(List<String> transactionIds);

}
