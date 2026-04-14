package gov.moda.dw.manager.service.custom;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.VPItemField;
import gov.moda.dw.manager.repository.VPItemFieldRepository;
import gov.moda.dw.manager.service.VPItemFieldQueryService;
import gov.moda.dw.manager.service.criteria.VPItemFieldCriteria;
import gov.moda.dw.manager.service.dto.VPItemFieldDTO;
import gov.moda.dw.manager.service.mapper.VPItemFieldMapper;

/**
 * Service Implementation for managing {@link VPItemField}.
 */
@Service
@Transactional
public class CustomVPItemFieldService {

    private static final Logger log = LoggerFactory.getLogger(CustomVPItemFieldService.class);
    private final VPItemFieldQueryService vPItemFieldQueryService;
    private final VPItemFieldRepository vPItemFieldRepository;
    private final VPItemFieldMapper vPItemFieldMapper;
    private final CustomVPItemAuthHelperService customVPItemAuthHelperService;
    public CustomVPItemFieldService(VPItemFieldQueryService vPItemFieldQueryService, VPItemFieldRepository vPItemFieldRepository, VPItemFieldMapper vPItemFieldMapper, CustomVPItemAuthHelperService customVPItemAuthHelperService) {
        this.vPItemFieldQueryService = vPItemFieldQueryService;
        this.vPItemFieldRepository = vPItemFieldRepository;
        this.vPItemFieldMapper = vPItemFieldMapper;
        this.customVPItemAuthHelperService = customVPItemAuthHelperService;
    }

    public Page<VPItemFieldDTO>  getAllVPItemFields(VPItemFieldCriteria criteria, Pageable pageable){
        return vPItemFieldQueryService.findByCriteria(customVPItemAuthHelperService.queryVPFieldFilterByAuth(criteria), pageable);
    }

    /**
     * Get one vPItemField by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VPItemFieldDTO> findOne(Long id) {
        log.debug("Request to get VPItemField : {}", id);
        customVPItemAuthHelperService.checkVpItemAuthByVpFieldId(id,"VPItemFieldService.findOne");
        return vPItemFieldRepository.findById(id).map(vPItemFieldMapper::toDto);
    }

}
