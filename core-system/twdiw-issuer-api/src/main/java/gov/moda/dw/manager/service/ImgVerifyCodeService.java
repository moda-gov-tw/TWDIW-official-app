package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.ImgVerifyCode;
import gov.moda.dw.manager.repository.ImgVerifyCodeRepository;
import gov.moda.dw.manager.service.dto.ImgVerifyCodeDTO;
import gov.moda.dw.manager.service.mapper.ImgVerifyCodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ImgVerifyCode}.
 */
@Service
@Transactional
public class ImgVerifyCodeService {

  private final Logger log = LoggerFactory.getLogger(ImgVerifyCodeService.class);

  private final ImgVerifyCodeRepository imgVerifyCodeRepository;

  private final ImgVerifyCodeMapper imgVerifyCodeMapper;

  public ImgVerifyCodeService(ImgVerifyCodeRepository imgVerifyCodeRepository, ImgVerifyCodeMapper imgVerifyCodeMapper) {
    this.imgVerifyCodeRepository = imgVerifyCodeRepository;
    this.imgVerifyCodeMapper = imgVerifyCodeMapper;
  }

  /**
   * Save a imgVerifyCode.
   *
   * @param imgVerifyCodeDTO the entity to save.
   * @return the persisted entity.
   */
  public ImgVerifyCodeDTO save(ImgVerifyCodeDTO imgVerifyCodeDTO) {
    log.debug("Request to save ImgVerifyCode : {}", imgVerifyCodeDTO);
    ImgVerifyCode imgVerifyCode = imgVerifyCodeMapper.toEntity(imgVerifyCodeDTO);
    imgVerifyCode = imgVerifyCodeRepository.save(imgVerifyCode);
    return imgVerifyCodeMapper.toDto(imgVerifyCode);
  }

  /**
   * Update a imgVerifyCode.
   *
   * @param imgVerifyCodeDTO the entity to save.
   * @return the persisted entity.
   */
  public ImgVerifyCodeDTO update(ImgVerifyCodeDTO imgVerifyCodeDTO) {
    log.debug("Request to update ImgVerifyCode : {}", imgVerifyCodeDTO);
    ImgVerifyCode imgVerifyCode = imgVerifyCodeMapper.toEntity(imgVerifyCodeDTO);
    imgVerifyCode = imgVerifyCodeRepository.save(imgVerifyCode);
    return imgVerifyCodeMapper.toDto(imgVerifyCode);
  }

  /**
   * Partially update a imgVerifyCode.
   *
   * @param imgVerifyCodeDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<ImgVerifyCodeDTO> partialUpdate(ImgVerifyCodeDTO imgVerifyCodeDTO) {
    log.debug("Request to partially update ImgVerifyCode : {}", imgVerifyCodeDTO);

    return imgVerifyCodeRepository
      .findById(imgVerifyCodeDTO.getId())
      .map(existingImgVerifyCode -> {
        imgVerifyCodeMapper.partialUpdate(existingImgVerifyCode, imgVerifyCodeDTO);

        return existingImgVerifyCode;
      })
      .map(imgVerifyCodeRepository::save)
      .map(imgVerifyCodeMapper::toDto);
  }

  /**
   * Get one imgVerifyCode by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ImgVerifyCodeDTO> findOne(Long id) {
    log.debug("Request to get ImgVerifyCode : {}", id);
    return imgVerifyCodeRepository.findById(id).map(imgVerifyCodeMapper::toDto);
  }

  /**
   * Delete the imgVerifyCode by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete ImgVerifyCode : {}", id);
    imgVerifyCodeRepository.deleteById(id);
  }
}
