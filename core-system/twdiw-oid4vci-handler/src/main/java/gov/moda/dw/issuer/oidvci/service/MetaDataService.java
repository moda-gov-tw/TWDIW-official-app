package gov.moda.dw.issuer.oidvci.service;


import java.util.List;

import gov.moda.dw.issuer.oidvci.domain.MetaDataEntity;
import gov.moda.dw.issuer.oidvci.repository.MetaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MetaDataService {
    private static final Logger log = LoggerFactory.getLogger(MetaDataService.class);

    private final MetaDataRepository metaDataRepository;

    public MetaDataService(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }

    public List<MetaDataEntity> getMetaData(String org_id) {
        return metaDataRepository.findByOrgId(org_id);
    }

}
