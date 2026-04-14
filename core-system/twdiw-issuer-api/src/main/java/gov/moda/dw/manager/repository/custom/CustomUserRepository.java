package gov.moda.dw.manager.repository.custom;

import java.util.List;
import java.util.Optional;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.UserRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Res entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomUserRepository extends UserRepository {
  List<User> findAllByLoginIn(List<String> logins);

  Optional<User> findOneByActivationKeyAndResetKey(String activationKey, String resetKey);
}
