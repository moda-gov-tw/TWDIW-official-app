package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.VPItemField;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the VPItemField entity. */
@SuppressWarnings("unused")
@Repository
public interface CustomVPItemFieldRepository
    extends JpaRepository<VPItemField, Long>, JpaSpecificationExecutor<VPItemField> {

  List<VPItemField> findByVpItemId(Long id);

  List<VPItemField> findByVpItemIdAndVcSerialNoAndEname(Long id, String vcSerialNo, String ename);
}
