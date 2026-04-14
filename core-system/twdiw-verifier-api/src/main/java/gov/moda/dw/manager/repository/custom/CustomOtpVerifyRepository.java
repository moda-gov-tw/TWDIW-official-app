package gov.moda.dw.manager.repository.custom;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import gov.moda.dw.manager.domain.OtpVerify;
import gov.moda.dw.manager.repository.OtpVerifyRepository;

@Repository
public interface CustomOtpVerifyRepository extends OtpVerifyRepository {

    Optional<OtpVerify> findByEmail(String email);

    Optional<OtpVerify> findByEmailAndIsPassTrue(String email);

}
