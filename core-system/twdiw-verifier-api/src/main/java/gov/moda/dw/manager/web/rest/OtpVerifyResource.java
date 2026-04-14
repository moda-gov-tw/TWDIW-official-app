package gov.moda.dw.manager.web.rest;

import gov.moda.dw.manager.repository.OtpVerifyRepository;
import gov.moda.dw.manager.service.OtpVerifyQueryService;
import gov.moda.dw.manager.service.OtpVerifyService;
import gov.moda.dw.manager.service.criteria.OtpVerifyCriteria;
import gov.moda.dw.manager.service.dto.OtpVerifyDTO;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.OtpVerify}.
 */
@RestController
@RequestMapping("/api/otp-verifies")
public class OtpVerifyResource {

    private static final Logger LOG = LoggerFactory.getLogger(OtpVerifyResource.class);

    private static final String ENTITY_NAME = "otpVerify";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OtpVerifyService otpVerifyService;

    private final OtpVerifyRepository otpVerifyRepository;

    private final OtpVerifyQueryService otpVerifyQueryService;

    public OtpVerifyResource(OtpVerifyService otpVerifyService, OtpVerifyRepository otpVerifyRepository,
            OtpVerifyQueryService otpVerifyQueryService) {
        this.otpVerifyService = otpVerifyService;
        this.otpVerifyRepository = otpVerifyRepository;
        this.otpVerifyQueryService = otpVerifyQueryService;
    }

    /**
     * {@code POST  /otp-verifies} : Create a new otpVerify.
     *
     * @param otpVerifyDTO the otpVerifyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new otpVerifyDTO, or with status {@code 400 (Bad Request)}
     *         if the otpVerify has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OtpVerifyDTO> createOtpVerify(@Valid @RequestBody OtpVerifyDTO otpVerifyDTO)
            throws URISyntaxException {
        LOG.debug("REST request to save OtpVerify : {}", otpVerifyDTO);
        if (otpVerifyDTO.getId() != null) {
            throw new BadRequestAlertException("A new otpVerify cannot already have an ID", ENTITY_NAME, "idexists");
        }
        otpVerifyDTO = otpVerifyService.save(otpVerifyDTO);
        return ResponseEntity
                .created(new URI("/api/otp-verifies/" + otpVerifyDTO.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, otpVerifyDTO.getId().toString()))
                .body(otpVerifyDTO);
    }

    /**
     * {@code PUT  /otp-verifies/:id} : Updates an existing otpVerify.
     *
     * @param id           the id of the otpVerifyDTO to save.
     * @param otpVerifyDTO the otpVerifyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated otpVerifyDTO, or with status {@code 400 (Bad Request)} if
     *         the otpVerifyDTO is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the otpVerifyDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OtpVerifyDTO> updateOtpVerify(@PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody OtpVerifyDTO otpVerifyDTO) throws URISyntaxException {
        LOG.debug("REST request to update OtpVerify : {}, {}", id, otpVerifyDTO);
        if (otpVerifyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, otpVerifyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!otpVerifyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        otpVerifyDTO = otpVerifyService.update(otpVerifyDTO);
        return ResponseEntity.ok().headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, otpVerifyDTO.getId().toString()))
                .body(otpVerifyDTO);
    }

    /**
     * {@code PATCH  /otp-verifies/:id} : Partial updates given fields of an
     * existing otpVerify, field will ignore if it is null
     *
     * @param id           the id of the otpVerifyDTO to save.
     * @param otpVerifyDTO the otpVerifyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated otpVerifyDTO, or with status {@code 400 (Bad Request)} if
     *         the otpVerifyDTO is not valid, or with status {@code 404 (Not Found)}
     *         if the otpVerifyDTO is not found, or with status
     *         {@code 500 (Internal Server Error)} if the otpVerifyDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OtpVerifyDTO> partialUpdateOtpVerify(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody OtpVerifyDTO otpVerifyDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update OtpVerify partially : {}, {}", id, otpVerifyDTO);
        if (otpVerifyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, otpVerifyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!otpVerifyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OtpVerifyDTO> result = otpVerifyService.partialUpdate(otpVerifyDTO);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true,
                ENTITY_NAME, otpVerifyDTO.getId().toString()));
    }

    /**
     * {@code GET  /otp-verifies} : get all the otpVerifies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of otpVerifies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OtpVerifyDTO>> getAllOtpVerifies(OtpVerifyCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get OtpVerifies by criteria: {}", criteria);

        Page<OtpVerifyDTO> page = otpVerifyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /otp-verifies/count} : count all the otpVerifies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOtpVerifies(OtpVerifyCriteria criteria) {
        LOG.debug("REST request to count OtpVerifies by criteria: {}", criteria);
        return ResponseEntity.ok().body(otpVerifyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /otp-verifies/:id} : get the "id" otpVerify.
     *
     * @param id the id of the otpVerifyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the otpVerifyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OtpVerifyDTO> getOtpVerify(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OtpVerify : {}", id);
        Optional<OtpVerifyDTO> otpVerifyDTO = otpVerifyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(otpVerifyDTO);
    }

    /**
     * {@code DELETE  /otp-verifies/:id} : delete the "id" otpVerify.
     *
     * @param id the id of the otpVerifyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOtpVerify(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OtpVerify : {}", id);
        otpVerifyService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}
