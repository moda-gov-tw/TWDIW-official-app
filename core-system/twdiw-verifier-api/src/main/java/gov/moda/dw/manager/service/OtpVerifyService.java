package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.OtpVerify;
import gov.moda.dw.manager.repository.OtpVerifyRepository;
import gov.moda.dw.manager.service.dto.OtpVerifyDTO;
import gov.moda.dw.manager.service.mapper.OtpVerifyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link gov.moda.dw.manager.domain.OtpVerify}.
 */
@Service
@Transactional
public class OtpVerifyService {

    private static final Logger LOG = LoggerFactory.getLogger(OtpVerifyService.class);

    private final OtpVerifyRepository otpVerifyRepository;

    private final OtpVerifyMapper otpVerifyMapper;

    public OtpVerifyService(OtpVerifyRepository otpVerifyRepository, OtpVerifyMapper otpVerifyMapper) {
        this.otpVerifyRepository = otpVerifyRepository;
        this.otpVerifyMapper = otpVerifyMapper;
    }

    /**
     * Save a otpVerify.
     *
     * @param otpVerifyDTO the entity to save.
     * @return the persisted entity.
     */
    public OtpVerifyDTO save(OtpVerifyDTO otpVerifyDTO) {
        LOG.debug("Request to save OtpVerify : {}", otpVerifyDTO);
        OtpVerify otpVerify = otpVerifyMapper.toEntity(otpVerifyDTO);
        otpVerify = otpVerifyRepository.save(otpVerify);
        return otpVerifyMapper.toDto(otpVerify);
    }

    /**
     * Update a otpVerify.
     *
     * @param otpVerifyDTO the entity to save.
     * @return the persisted entity.
     */
    public OtpVerifyDTO update(OtpVerifyDTO otpVerifyDTO) {
        LOG.debug("Request to update OtpVerify : {}", otpVerifyDTO);
        OtpVerify otpVerify = otpVerifyMapper.toEntity(otpVerifyDTO);
        otpVerify = otpVerifyRepository.save(otpVerify);
        return otpVerifyMapper.toDto(otpVerify);
    }

    /**
     * Partially update a otpVerify.
     *
     * @param otpVerifyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OtpVerifyDTO> partialUpdate(OtpVerifyDTO otpVerifyDTO) {
        LOG.debug("Request to partially update OtpVerify : {}", otpVerifyDTO);

        return otpVerifyRepository.findById(otpVerifyDTO.getId()).map(existingOtpVerify -> {
            otpVerifyMapper.partialUpdate(existingOtpVerify, otpVerifyDTO);

            return existingOtpVerify;
        }).map(otpVerifyRepository::save).map(otpVerifyMapper::toDto);
    }

    /**
     * Get one otpVerify by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OtpVerifyDTO> findOne(Long id) {
        LOG.debug("Request to get OtpVerify : {}", id);
        return otpVerifyRepository.findById(id).map(otpVerifyMapper::toDto);
    }

    /**
     * Delete the otpVerify by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete OtpVerify : {}", id);
        otpVerifyRepository.deleteById(id);
    }
}
