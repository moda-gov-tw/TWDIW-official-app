package gov.moda.dw.issuer.vc.repository;

import gov.moda.dw.issuer.vc.domain.StatusList;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link StatusList} entity.
 *
 * @version 20240902
 */
@Repository("StatusListRepository")
public interface StatusListRepository extends JpaRepository<StatusList, String> {

//    @Query(nativeQuery = true, value = "SELECT * FROM status_list s WHERE s.credential_type = :credential_type and s.status_list_type = :status_list_type and s.gid = :gid ORDER BY s.issuance_date DESC LIMIT 1")
	@Query(nativeQuery = true, value = "SELECT * FROM status_list s WHERE s.credential_type = :credential_type and s.status_list_type = :status_list_type and s.gid = :gid ORDER BY s.issuance_date DESC LIMIT 1 FOR UPDATE NOWAIT")
    StatusList queryLatestByGldForUpdate(@Param("credential_type") String credentialType, @Param("status_list_type") String statusListType, @Param("gid") int gid);
	
	@Query(nativeQuery = true, value = "SELECT * FROM status_list s WHERE s.credential_type = :credential_type and s.status_list_type = :status_list_type and s.gid = :group ORDER BY s.issuance_date DESC LIMIT 1")
	StatusList queryLatestStatusList(@Param("credential_type") String credentialType, @Param("status_list_type") String statusListType, @Param("group") int group);
    
    @Query(nativeQuery = true, value = "SELECT credential_type, status_list_type, MAX(gid) FROM status_list GROUP BY credential_type, status_list_type")
    List<Object[]> queryMaxGidForAllCredentialTypeAndStatusListType();
    
    @Query(nativeQuery = true, value = """
	    		SELECT * FROM (
    				SELECT *, ROW_NUMBER() OVER (
	        			PARTITION BY status_list_type, credential_type, gid
	        			ORDER BY issuance_date DESC
    				) AS row_num
    				FROM status_list
    			) AS ranked
    			WHERE row_num = 1
    		""")
    List<StatusList> queryAllLatestStatusListByCredentialTypeAndStatusListTypeAndGid();
}
