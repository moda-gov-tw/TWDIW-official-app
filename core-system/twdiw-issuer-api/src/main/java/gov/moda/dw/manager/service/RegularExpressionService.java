package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.repository.RegularExpressionRepository;
import gov.moda.dw.manager.service.dto.RegularExpressionDTO;
import gov.moda.dw.manager.service.mapper.RegularExpressionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RegularExpression}.
 */
@Service
@Transactional
public class RegularExpressionService {

    private final Logger log = LoggerFactory.getLogger(RegularExpressionService.class);

    private final RegularExpressionRepository regularExpressionRepository;

    private final RegularExpressionMapper regularExpressionMapper;

    public RegularExpressionService(
        RegularExpressionRepository regularExpressionRepository,
        RegularExpressionMapper regularExpressionMapper
    ) {
        this.regularExpressionRepository = regularExpressionRepository;
        this.regularExpressionMapper = regularExpressionMapper;
    }

    /**
     * Save a regularExpression.
     *
     * @param regularExpressionDTO the entity to save.
     * @return the persisted entity.
     */
    public RegularExpressionDTO save(RegularExpressionDTO regularExpressionDTO) {
        log.debug("Request to save RegularExpression : {}", regularExpressionDTO);
        RegularExpression regularExpression = regularExpressionMapper.toEntity(regularExpressionDTO);
        regularExpression = regularExpressionRepository.save(regularExpression);
        return regularExpressionMapper.toDto(regularExpression);
    }

    /**
     * Partially update a regularExpression.
     *
     * @param regularExpression the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RegularExpressionDTO> partialUpdate(RegularExpressionDTO regularExpression) {
        log.debug("Request to partially update RegularExpression : {}", regularExpression);

        return regularExpressionRepository
            .findById(regularExpression.getId())
            .map(existingRegularExpression -> {
                regularExpressionMapper.partialUpdate(existingRegularExpression, regularExpression);
                return existingRegularExpression;
            })
            .map(regularExpressionRepository::save)
            .map(regularExpressionMapper::toDto);
    }

    /**
     * Get all the regularExpressions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RegularExpressionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RegularExpressions");
        return regularExpressionRepository.findAll(pageable).map(regularExpressionMapper::toDto);
    }

    /**
     * Get one regularExpression by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RegularExpressionDTO> findOne(Long id) {
        log.debug("Request to get RegularExpression : {}", id);
        return regularExpressionRepository.findById(id).map(regularExpressionMapper::toDto);
    }

    /**
     * Delete the regularExpression by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RegularExpression : {}", id);
        regularExpressionRepository.deleteById(id);
    }
}
