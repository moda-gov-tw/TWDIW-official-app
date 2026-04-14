package gov.moda.dw.manager.repository.outside;

import gov.moda.dw.manager.domain.outside.VcManagerVCItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VcManagerVCItemRepository extends JpaRepository<VcManagerVCItem, Long>, JpaSpecificationExecutor<VcManagerVCItem> {

    List<VcManagerVCItem> findByBusinessIdAndExposeOrCrUser(String taxId, boolean b, long crUser);

    List<VcManagerVCItem> findByBusinessIdAndExposeOrBusinessId(String taxId, boolean b, String businessId);

}
