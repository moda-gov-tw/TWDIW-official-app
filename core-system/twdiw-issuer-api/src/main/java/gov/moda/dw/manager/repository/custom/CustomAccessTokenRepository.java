package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.repository.AccessTokenRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the AccessToken entity. */
@SuppressWarnings("unused")
@Repository
public interface CustomAccessTokenRepository extends AccessTokenRepository {
  Optional<AccessToken> findOneById(Long id);

  AccessToken findByAccessToken(String token);

  Optional<AccessToken> findFirstByOwnerOrderByCreateTimeDesc(String owner);

  List<AccessToken> findByOwner(String owner);

  List<AccessToken> findAllByOrgId(String orgId);
}
