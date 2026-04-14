package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.ImgVerifyCode;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ImgVerifyCode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImgVerifyCodeRepository extends JpaRepository<ImgVerifyCode, Long>, JpaSpecificationExecutor<ImgVerifyCode> {}
