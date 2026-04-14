package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.repository.BwdParamRepository;
import gov.moda.dw.manager.service.BwdParamQueryService;
import gov.moda.dw.manager.service.criteria.BwdParamCriteria;
import gov.moda.dw.manager.service.dto.BwdParamDTO;
import gov.moda.dw.manager.service.mapper.BwdParamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomBwdParamQueryService extends BwdParamQueryService {

    private final Logger log = LoggerFactory.getLogger(CustomBwdParamQueryService.class);

    private final BwdParamRepository bwdParamRepository;

    private final BwdParamMapper bwdParamMapper;

    public CustomBwdParamQueryService(BwdParamRepository bwdParamRepository, BwdParamMapper bwdParamMapper) {
        super(bwdParamRepository, bwdParamMapper);
        this.bwdParamRepository = bwdParamRepository;
        this.bwdParamMapper = bwdParamMapper;
    }

    @Transactional(readOnly = true)
    public List<BwdParamDTO> findByCriteria(BwdParamCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BwdParam> specification = createSpecification(criteria);
        return bwdParamMapper.toDto(bwdParamRepository.findAll(specification));
    }
}
