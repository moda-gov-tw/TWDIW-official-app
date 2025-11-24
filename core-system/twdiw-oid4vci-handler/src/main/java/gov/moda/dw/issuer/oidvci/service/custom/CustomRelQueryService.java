package gov.moda.dw.issuer.oidvci.service.custom;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.issuer.oidvci.domain.Rel;
import gov.moda.dw.issuer.oidvci.repository.RelRepository;
import gov.moda.dw.issuer.oidvci.service.RelQueryService;
import gov.moda.dw.issuer.oidvci.service.criteria.RelCriteria;
import gov.moda.dw.issuer.oidvci.service.dto.RelDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.RelMapper;
import gov.moda.dw.issuer.oidvci.type.RelType;
import gov.moda.dw.issuer.oidvci.util.StringFilterUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link Rel} entities in the database.
 * The main input is a {@link RelCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RelDTO} which fulfills the criteria.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CustomRelQueryService extends RelQueryService {

    private final RelRepository relRepository;

    private final RelMapper relMapper;

    public CustomRelQueryService(RelRepository relRepository, RelMapper relMapper) {
        super(relRepository, relMapper);
        this.relRepository = relRepository;
        this.relMapper = relMapper;
    }

    /**
     * 查詢關聯型態為 Role::Res
     * 從Role去查詢
     */
    public List<RelDTO> findByLeftCode(String roleId, String stateType) {
        RelCriteria relCriteria = new RelCriteria();
        relCriteria.setLeftTbl(StringFilterUtils.toEqualStringFilter(RelType.ROLETORES.getLeftTbl()));
        relCriteria.setRightTbl(StringFilterUtils.toEqualStringFilter(RelType.ROLETORES.getRightTbl()));
        relCriteria.setLeftCode(StringFilterUtils.toEqualStringFilter(roleId));
        if (stateType != null) {
            relCriteria.setState(StringFilterUtils.toEqualStringFilter(stateType));
        }
        List<RelDTO> relDTOList = this.findByCriteria(relCriteria);
        return relDTOList;
    }

    /**
     * 查詢關聯型態為 AccessToken::Res
     * 從AccessToken去查詢
     */
    public List<RelDTO> findRelDTOListForAccessTokenStr(String leftCode) {
        RelCriteria relCriteria = new RelCriteria();
        relCriteria.setLeftTbl(StringFilterUtils.toEqualStringFilter(RelType.ACCESSTOKENTORES.getLeftTbl()));
        relCriteria.setRightTbl(StringFilterUtils.toEqualStringFilter(RelType.ACCESSTOKENTORES.getRightTbl()));
        relCriteria.setState(StringFilterUtils.toEqualStringFilter("1"));
        relCriteria.setLeftCode(StringFilterUtils.toEqualStringFilter(leftCode));
        return this.findByCriteria(relCriteria);
    }

    /**
     * 查詢關聯型態為 Role::Res and AccessToken::Res
     * 從Res去查詢
     */
    public List<RelDTO> findByRightCode(String resId) {
        RelCriteria relCriteria = new RelCriteria();
        //    relCriteria.setLeftTbl(StringFilterUtils.toEqualStringFilter(RelType.ROLETORES.getLeftTbl()));
        relCriteria.setRightTbl(StringFilterUtils.toEqualStringFilter(RelType.ROLETORES.getRightTbl()));
        relCriteria.setRightCode(StringFilterUtils.toEqualStringFilter(resId));
        List<RelDTO> relDTOList = this.findByCriteria(relCriteria);
        return relDTOList;
    }

    /**
     * 查詢關聯型態為 User::Role
     * 從Role去查詢
     */
    public List<RelDTO> findByRightCodeGetUser(String roleId) {
        RelCriteria relCriteria = new RelCriteria();
        relCriteria.setLeftTbl(StringFilterUtils.toEqualStringFilter(RelType.USERTOROLE.getLeftTbl()));
        relCriteria.setRightTbl(StringFilterUtils.toEqualStringFilter(RelType.USERTOROLE.getRightTbl()));
        relCriteria.setRightCode(StringFilterUtils.toEqualStringFilter(roleId));
        List<RelDTO> relDTOList = this.findByCriteria(relCriteria);
        return relDTOList;
    }

    /**
     * 查詢關聯型態為 User::Role
     * 從User去查詢
     */
    public List<RelDTO> findByLeftCodeGetRole(String userId) {
        RelCriteria relCriteria = new RelCriteria();
        relCriteria.setLeftTbl(StringFilterUtils.toEqualStringFilter(RelType.USERTOROLE.getLeftTbl()));
        relCriteria.setRightTbl(StringFilterUtils.toEqualStringFilter(RelType.USERTOROLE.getRightTbl()));
        relCriteria.setLeftCode(StringFilterUtils.toEqualStringFilter(userId));
        List<RelDTO> relDTOList = this.findByCriteria(relCriteria);
        return relDTOList;
    }

    /**
     * Return a {@link List} of {@link RelDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional
    public List<RelDTO> findByCriteria(RelCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Rel> specification = createSpecification(criteria);
        return relMapper.toDto(relRepository.findAll(specification));
    }
}
