package gov.moda.dw.manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.Field;
import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.custom.CustomFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomRegularExpressionRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.dto.FieldDTO;
import gov.moda.dw.manager.service.mapper.FieldMapper;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.Field}.
 */
@Service
@Transactional
public class DwSandBoxVC101WService {

    private static final String ENTITY_NAME_FIELD = "field";

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC101WService.class);

    private final CustomFieldRepository fieldRepository;

    private final CustomRegularExpressionRepository regularExpressionRepository;

    private final CustomVCItemFieldRepository vcItemFieldRepository;

    private final FieldMapper fieldMapper;

    private final CustomFieldRepository customFieldRepository;

    private final CustomUserRepository customUserRepository;

    public DwSandBoxVC101WService(
        CustomFieldRepository fieldRepository,
        CustomRegularExpressionRepository regularExpressionRepository,
        CustomVCItemFieldRepository vcItemFieldRepository,
        FieldMapper fieldMapper,
        CustomFieldRepository customFieldRepository,
        CustomUserRepository customUserRepository
    ) {
        this.fieldRepository = fieldRepository;
        this.vcItemFieldRepository = vcItemFieldRepository;
        this.fieldMapper = fieldMapper;
        this.customFieldRepository = customFieldRepository;
        this.regularExpressionRepository = regularExpressionRepository;
        this.customUserRepository = customUserRepository;
    }

    /**
     * Save a field.
     *
     * @param fieldDTO the entity to save.
     * @return the persisted entity.
     */
    public List<FieldDTO> save(List<FieldDTO> fieldDTO) throws Exception {
        log.debug("Request to save Field : {}", fieldDTO);

        List<FieldDTO> result = new ArrayList<>();

        List<String> ename = new ArrayList<>();

        for (FieldDTO f : fieldDTO) {
            // 欄位名稱(英)，只能輸入英文數字和_(不可輸入id)
            if (!f.getEname().matches("^(?!id$)[a-zA-Z0-9_]+$")) {
                throw new RuntimeException("請求失敗，請確認輸入的欄位名稱(英)，只能輸入英文數字和_(不可輸入id)，若有疑問，請洽系統管理員。");
            }
            ename.add(f.getEname());
        }

        List<Field> fieldsByEname = fieldRepository.findByBusinessIdAndEnameIn(SecurityUtils.getJwtUserObject().get(0).getOrgId(), ename);

        if (!fieldsByEname.isEmpty()) {
            String nameList = String.join(",", ename);
            throw new BadRequestAlertException("The following names are duplicated: 「" + nameList + "」", ENTITY_NAME_FIELD, "exists");
        }

        for (FieldDTO item : fieldDTO) {
            RegularExpression regularExpression = null;
            if (item.getRegularExpressionId() != null) {
                Optional<RegularExpression> regularExpressionOptional = regularExpressionRepository.findById(item.getRegularExpressionId());
                if (regularExpressionOptional.isPresent()) {
                    regularExpression = regularExpressionOptional.get();
                }
            }

            Field field = fieldMapper.toEntity(item);
            field.setVisible(true);
            field.setBusinessId(SecurityUtils.getJwtUserObject().get(0).getOrgId());
            field.setRegularExpression(regularExpression);
            fieldRepository.save(field);
            result.add(fieldMapper.toDto(field));
        }

        return result;
    }

    /**
     * Update a field.
     *
     * @param fieldDTO the entity to save.
     * @return the persisted entity.
     */
    public FieldDTO update(FieldDTO fieldDTO) {
        log.debug("Request to update Field : {}", fieldDTO);
        Field field = fieldMapper.toEntity(fieldDTO);
        field = fieldRepository.save(field);
        return fieldMapper.toDto(field);
    }

    /**
     * Partially update a field.
     *
     * @param fieldDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FieldDTO> partialUpdate(FieldDTO fieldDTO) {
        log.debug("Request to partially update Field : {}", fieldDTO);

        return fieldRepository
            .findById(fieldDTO.getId())
            .map(existingField -> {
                fieldMapper.partialUpdate(existingField, fieldDTO);

                return existingField;
            })
            .map(fieldRepository::save)
            .map(fieldMapper::toDto);
    }

    /**
     * Get one field by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FieldDTO> findOne(Long id) {
        log.debug("Request to get Field : {}", id);
        Field field = fieldRepository.findByBusinessIdAndId(SecurityUtils.getJwtUserObject().get(0).getOrgId(), id);
        if (field == null) {
            return Optional.ofNullable(null);
        } else {
            return Optional.ofNullable(fieldMapper.toDto(field));
        }
    }

    /**
     * Get field by business id.
     *
     * @param businessId the business id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Page<FieldDTO> findAllByBusinessId(String businessId, Pageable page) {
        log.debug("Request to get Field by business id : {}", businessId);
        return fieldRepository.findByBusinessId(businessId, page).map(fieldMapper::toDto);
    }

    /**
     * Get field by business id.
     *
     * @param businessId the business id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Page<FieldDTO> findAllByBusinessIdAndType(String businessId, String type, Pageable page) {
        log.debug("Request to get Field by business id : {}", businessId, " type: {}", type);
        return fieldRepository.findByBusinessIdAndType(businessId, type, page).map(fieldMapper::toDto);
    }

    /**
     * Delete the field by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Field : {}", id);

        Optional<Field> field = customFieldRepository.findById(id);

        if (field.isPresent()) {
            // 有被用過的欄位不刪

            List<VCItemField> vcItemFieldList = vcItemFieldRepository.findByBusinessIdAndEname(
                field.get().getBusinessId(),
                field.get().getEname(),
                field.get().getType()
            );

            if (!vcItemFieldList.isEmpty()) {
                throw new BadRequestAlertException("This field is used", "", "used");
            }

            fieldRepository.deleteByBusinessIdAndId(SecurityUtils.getJwtUserObject().get(0).getOrgId(), id);
        }
    }
}
