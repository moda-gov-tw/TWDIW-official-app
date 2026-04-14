package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.UserAuthority;
import gov.moda.dw.manager.domain.UserAuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link UserAuthority} entity. (CHT 補)
 * and
 * Spring Data JPA repository for the {@link UserAuthorityId} entity. (CHT 補)
 */
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, UserAuthorityId> {
    void deleteByIdUserId(Long userId);

    void deleteByIdAuthorityName(String authorityName);
}
