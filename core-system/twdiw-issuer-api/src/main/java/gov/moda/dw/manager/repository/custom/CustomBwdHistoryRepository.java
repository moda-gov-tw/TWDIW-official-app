package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.BwdHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomBwdHistoryRepository extends JpaRepository<BwdHistory, Long>, JpaSpecificationExecutor<BwdHistory> {
    List<BwdHistory> findAllByUserIdOrderByCreateTimeDesc(String userId);
}
