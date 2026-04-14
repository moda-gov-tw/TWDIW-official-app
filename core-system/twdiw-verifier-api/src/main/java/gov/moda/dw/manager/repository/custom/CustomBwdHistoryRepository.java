package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.BwdHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomBwdHistoryRepository extends JpaRepository<BwdHistory, Long>, JpaSpecificationExecutor<BwdHistory> {
    List<BwdHistory> findAllByUserIdOrderByCreateTimeDesc(String userId);
}
