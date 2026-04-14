package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.repository.AccessTokenRepository;
import gov.moda.dw.manager.service.AccessTokenQueryService;
import gov.moda.dw.manager.service.criteria.AccessTokenCriteria;
import gov.moda.dw.manager.service.mapper.AccessTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomAccessTokenQueryService extends AccessTokenQueryService {

    private final Logger log = LoggerFactory.getLogger(CustomAccessTokenQueryService.class);

    private final AccessTokenRepository accessTokenRepository;

    private final AccessTokenMapper accessTokenMapper;

    public CustomAccessTokenQueryService(AccessTokenRepository accessTokenRepository, AccessTokenMapper accessTokenMapper) {
        super(accessTokenRepository, accessTokenMapper);
        this.accessTokenRepository = accessTokenRepository;
        this.accessTokenMapper = accessTokenMapper;
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional
    public long countByCriteria(AccessTokenCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AccessToken> specification = createSpecification(criteria);
        return this.accessTokenRepository.count(specification);
    }

}
