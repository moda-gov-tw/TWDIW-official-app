package gov.moda.dw.manager.web.rest;

import gov.moda.dw.manager.repository.VPVerifyResultRepository;
import gov.moda.dw.manager.service.VPVerifyResultQueryService;
import gov.moda.dw.manager.service.VPVerifyResultService;
import gov.moda.dw.manager.service.criteria.VPVerifyResultCriteria;
import gov.moda.dw.manager.service.dto.VPVerifyResultDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.VPVerifyResult}.
 */
@RestController
@RequestMapping("/api/vp-verify-results")
public class VPVerifyResultResource {

    private static final Logger log = LoggerFactory.getLogger(VPVerifyResultResource.class);

    private static final String ENTITY_NAME = "vPVerifyResult";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VPVerifyResultService vPVerifyResultService;

    private final VPVerifyResultRepository vPVerifyResultRepository;

    private final VPVerifyResultQueryService vPVerifyResultQueryService;

    public VPVerifyResultResource(
        VPVerifyResultService vPVerifyResultService,
        VPVerifyResultRepository vPVerifyResultRepository,
        VPVerifyResultQueryService vPVerifyResultQueryService
    ) {
        this.vPVerifyResultService = vPVerifyResultService;
        this.vPVerifyResultRepository = vPVerifyResultRepository;
        this.vPVerifyResultQueryService = vPVerifyResultQueryService;
    }

    /**
     * {@code POST  /vp-verify-results} : Create a new vPVerifyResult.
     *
     * @param vPVerifyResultDTO the vPVerifyResultDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vPVerifyResultDTO, or with status {@code 400 (Bad Request)} if the vPVerifyResult has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPVerifyResultDTO> createVPVerifyResult(@Valid @RequestBody VPVerifyResultDTO vPVerifyResultDTO)
        throws URISyntaxException {
        log.debug("REST request to save VPVerifyResult : {}", vPVerifyResultDTO);
        if (vPVerifyResultDTO.getId() != null) {
            throw new BadRequestAlertException("A new vPVerifyResult cannot already have an ID", ENTITY_NAME, "idexists");
        }
        vPVerifyResultDTO = vPVerifyResultService.save(vPVerifyResultDTO);
        return ResponseEntity.created(new URI("/api/vp-verify-results/" + vPVerifyResultDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, vPVerifyResultDTO.getId().toString()))
            .body(vPVerifyResultDTO);
    }

    /**
     * {@code PUT  /vp-verify-results/:id} : Updates an existing vPVerifyResult.
     *
     * @param id the id of the vPVerifyResultDTO to save.
     * @param vPVerifyResultDTO the vPVerifyResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vPVerifyResultDTO,
     * or with status {@code 400 (Bad Request)} if the vPVerifyResultDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vPVerifyResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPVerifyResultDTO> updateVPVerifyResult(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VPVerifyResultDTO vPVerifyResultDTO
    ) throws URISyntaxException {
        log.debug("REST request to update VPVerifyResult : {}, {}", id, vPVerifyResultDTO);
        if (vPVerifyResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vPVerifyResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vPVerifyResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        vPVerifyResultDTO = vPVerifyResultService.update(vPVerifyResultDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vPVerifyResultDTO.getId().toString()))
            .body(vPVerifyResultDTO);
    }

    /**
     * {@code PATCH  /vp-verify-results/:id} : Partial updates given fields of an existing vPVerifyResult, field will ignore if it is null
     *
     * @param id the id of the vPVerifyResultDTO to save.
     * @param vPVerifyResultDTO the vPVerifyResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vPVerifyResultDTO,
     * or with status {@code 400 (Bad Request)} if the vPVerifyResultDTO is not valid,
     * or with status {@code 404 (Not Found)} if the vPVerifyResultDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the vPVerifyResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPVerifyResultDTO> partialUpdateVPVerifyResult(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VPVerifyResultDTO vPVerifyResultDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update VPVerifyResult partially : {}, {}", id, vPVerifyResultDTO);
        if (vPVerifyResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vPVerifyResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vPVerifyResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VPVerifyResultDTO> result = vPVerifyResultService.partialUpdate(vPVerifyResultDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vPVerifyResultDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /vp-verify-results} : get all the vPVerifyResults.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vPVerifyResults in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<List<VPVerifyResultDTO>> getAllVPVerifyResults(
        VPVerifyResultCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get VPVerifyResults by criteria: {}", criteria);

        Page<VPVerifyResultDTO> page = vPVerifyResultQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vp-verify-results/count} : count all the vPVerifyResults.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Long> countVPVerifyResults(VPVerifyResultCriteria criteria) {
        log.debug("REST request to count VPVerifyResults by criteria: {}", criteria);
        return ResponseEntity.ok().body(vPVerifyResultQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /vp-verify-results/:id} : get the "id" vPVerifyResult.
     *
     * @param id the id of the vPVerifyResultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vPVerifyResultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPVerifyResultDTO> getVPVerifyResult(@PathVariable("id") Long id) {
        log.debug("REST request to get VPVerifyResult : {}", id);
        Optional<VPVerifyResultDTO> vPVerifyResultDTO = vPVerifyResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vPVerifyResultDTO);
    }

    /**
     * {@code DELETE  /vp-verify-results/:id} : delete the "id" vPVerifyResult.
     *
     * @param id the id of the vPVerifyResultDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Void> deleteVPVerifyResult(@PathVariable("id") Long id) {
        log.debug("REST request to delete VPVerifyResult : {}", id);
        vPVerifyResultService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
