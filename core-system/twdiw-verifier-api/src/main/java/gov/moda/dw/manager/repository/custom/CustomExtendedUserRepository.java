package gov.moda.dw.manager.repository.custom;

import java.util.List;
import java.util.Optional;
import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.service.dto.custom.ExtendedUserResDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the ExtendedUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomExtendedUserRepository
        extends JpaRepository<ExtendedUser, Long>, JpaSpecificationExecutor<ExtendedUser> {
    Optional<ExtendedUser> findOneByUserId(String userId);

    List<ExtendedUser> findAllByOrgId(String orgId);

    void deleteByUserId(String userId);

    @Query(value = """
            select user_id, user_name
            from vp_manager.extended_user
            where user_name Like :userName
            """, nativeQuery = true)
    List<ExtendedUserResDTO> findByUserName(@Param("userName") String userName);

    @Transactional
    @Modifying
    @Query(value = """
            update vp_manager.extended_user
            set user_name = :userName
            where user_id = :userId
            """, nativeQuery = true)
    int updateByUserId(@Param("userName") String userName, @Param("userId") String userId);
}
