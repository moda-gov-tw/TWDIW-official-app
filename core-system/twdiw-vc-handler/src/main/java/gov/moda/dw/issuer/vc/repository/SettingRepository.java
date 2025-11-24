package gov.moda.dw.issuer.vc.repository;

import gov.moda.dw.issuer.vc.domain.Setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link Setting} entity.
 *
 * @version 20240902
 */
@Repository("SettingRepository")
public interface SettingRepository extends JpaRepository<Setting, String> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE setting SET prop_value = :prop_value WHERE prop_name = :prop_name")
    int updatePropValueByPropName(@Param("prop_name") String propName, @Param("prop_value") String propValue);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE setting SET prop_value = CASE " +
        "WHEN prop_name = :prop_name_1 THEN :prop_value_1 " +
        "WHEN prop_name = :prop_name_2 THEN :prop_value_2 " +
        "END WHERE prop_name IN (:prop_name_1, :prop_name_2)")
    int updateTwoPropValuesByPropName(@Param("prop_name_1") String propName1, @Param("prop_value_1") String propValue1,
                                      @Param("prop_name_2") String propName2, @Param("prop_value_2") String propValue2);
    
    @Query(nativeQuery = true, value = "SELECT prop_value FROM setting WHERE prop_name = :prop_name")
    String queryByPropName(@Param("prop_name") String propName);
    
    @Query(nativeQuery = true, value = "SELECT * FROM setting WHERE prop_name IN (:prop_names) FOR UPDATE NOWAIT")
    List<Setting> queryTwoPropValuesByPropNameForUpdate(@Param("prop_names") List<String> propNames);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
            CREATE OR REPLACE PROCEDURE drop_sequence_safe(seq_name text)
            LANGUAGE plpgsql
            AS $$
            BEGIN
                EXECUTE format('DROP SEQUENCE IF EXISTS %I', seq_name);
            END;
            $$;
            """)
    void createProcedure();
}
