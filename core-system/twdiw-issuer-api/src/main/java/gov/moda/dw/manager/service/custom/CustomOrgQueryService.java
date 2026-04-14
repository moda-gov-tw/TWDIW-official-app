package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.repository.OrgRepository;
import gov.moda.dw.manager.service.OrgQueryService;
import gov.moda.dw.manager.service.criteria.OrgCriteria;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.mapper.OrgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomOrgQueryService extends OrgQueryService {

    private final Logger log = LoggerFactory.getLogger(CustomOrgQueryService.class);

    private final OrgRepository orgRepository;

    private final OrgMapper orgMapper;

    public CustomOrgQueryService(OrgRepository orgRepository, OrgMapper orgMapper) {
        super(orgRepository, orgMapper);
        this.orgRepository = orgRepository;
        this.orgMapper = orgMapper;
    }

    @Transactional
    public List<OrgDTO> findByCriteria(OrgCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Org> specification = createSpecification(criteria);
        return orgMapper.toDto(orgRepository.findAll(specification));
    }
}
