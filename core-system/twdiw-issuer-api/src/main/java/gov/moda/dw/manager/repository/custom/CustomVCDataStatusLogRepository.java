package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.VCDataStatusLog;
import gov.moda.dw.manager.repository.VCDataStatusLogRepository;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface CustomVCDataStatusLogRepository extends VCDataStatusLogRepository {

  List<VCDataStatusLog> findByVcCidOrderByCrDatetimeDesc(String vcCid);
}
