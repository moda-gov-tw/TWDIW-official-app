package gov.moda.dw.manager.repository;

import java.util.Optional;
import gov.moda.dw.manager.domain.Nonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Nonce entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NonceRepository extends JpaRepository<Nonce, Long>, JpaSpecificationExecutor<Nonce> {
  Optional<Nonce> findOneBySId(String sId);
  Optional<Nonce> findOneBySIdAndCaptchaCode(String sId, String captcha);
  Optional<Nonce> findByNonceId(String nonceId);
}
