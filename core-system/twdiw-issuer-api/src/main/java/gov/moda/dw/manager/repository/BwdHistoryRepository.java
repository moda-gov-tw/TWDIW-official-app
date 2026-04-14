package gov.moda.dw.manager.repository;

import java.util.List;
import gov.moda.dw.manager.domain.BwdHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BwdHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BwdHistoryRepository extends JpaRepository<BwdHistory, Long>, JpaSpecificationExecutor<BwdHistory> {
  List<BwdHistory> findAllByUserIdOrderByCreateTimeDesc(String userId);
}
