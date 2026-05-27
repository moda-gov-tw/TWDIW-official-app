package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.OtpVerify;
import gov.moda.dw.manager.repository.OtpVerifyRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomOtpVerifyRepository extends OtpVerifyRepository {

  Optional<OtpVerify> findByEmail(String email);

  Optional<OtpVerify> findByEmailAndIsPassTrue(String email);
}
