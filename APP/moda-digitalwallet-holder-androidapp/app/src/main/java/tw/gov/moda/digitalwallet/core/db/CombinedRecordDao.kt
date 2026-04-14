package tw.gov.moda.digitalwallet.core.db

import androidx.room.Dao
import androidx.room.Query
import tw.gov.moda.digitalwallet.data.db.CombinedCredentialOperationRecord

@Dao
interface CombinedRecordDao {
    @Query(
        """
            SELECT 
                'credential' || uid AS uid,
                walletId,
                vcId,
                text,
                authorizationUnit,
                authorizationPurpose,
                authorizationField,
                3 AS status, -- 預設值 OperationEnum.AUTHORIZATION_CARD
                datetime
            FROM credential_record
            WHERE vcId = :vcId
            UNION ALL
            SELECT 
                'operation' || uid AS uid,
                walletId,
                vcId,
                text,
                NULL AS authorizationUnit,
                NULL AS authorizationPurpose,
                NULL AS authorizationField,
                status,
                datetime
            FROM operation_record
            WHERE vcId = :vcId
            ORDER BY datetime 
        """
    )
    suspend fun getAllCombinedCredentialRecords(vcId: Long): List<CombinedCredentialOperationRecord>
}