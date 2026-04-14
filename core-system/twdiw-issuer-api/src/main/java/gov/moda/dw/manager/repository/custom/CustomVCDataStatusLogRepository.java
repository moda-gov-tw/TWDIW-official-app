package gov.moda.dw.manager.repository.custom;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import gov.moda.dw.manager.domain.VCDataStatusLog;
import gov.moda.dw.manager.repository.VCDataStatusLogRepository;

@Primary
@Repository
public interface CustomVCDataStatusLogRepository extends VCDataStatusLogRepository {

    List<VCDataStatusLog> findByVcCidOrderByCrDatetimeDesc(String vcCid);

}
