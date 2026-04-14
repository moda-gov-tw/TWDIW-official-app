package gov.moda.dw.manager.web.rest;

import gov.moda.dw.manager.repository.VCDataStatusLogRepository;
import gov.moda.dw.manager.service.VCDataStatusLogQueryService;
import gov.moda.dw.manager.service.VCDataStatusLogService;
import gov.moda.dw.manager.service.criteria.VCDataStatusLogCriteria;
import gov.moda.dw.manager.service.dto.VCDataStatusLogDTO;
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
 * REST controller for managing
 * {@link gov.moda.dw.manager.domain.VCDataStatusLog}.
 */
@RestController
@RequestMapping("/api/vc-data-status-logs")
public class VCDataStatusLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(VCDataStatusLogResource.class);

    private static final String ENTITY_NAME = "vCDataStatusLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VCDataStatusLogService vCDataStatusLogService;

    private final VCDataStatusLogRepository vCDataStatusLogRepository;

    private final VCDataStatusLogQueryService vCDataStatusLogQueryService;

    public VCDataStatusLogResource(VCDataStatusLogService vCDataStatusLogService,
            VCDataStatusLogRepository vCDataStatusLogRepository,
            VCDataStatusLogQueryService vCDataStatusLogQueryService) {
        this.vCDataStatusLogService = vCDataStatusLogService;
        this.vCDataStatusLogRepository = vCDataStatusLogRepository;
        this.vCDataStatusLogQueryService = vCDataStatusLogQueryService;
    }

    /**
     * {@code POST  /vc-data-status-logs} : Create a new vCDataStatusLog.
     *
     * @param vCDataStatusLogDTO the vCDataStatusLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new vCDataStatusLogDTO, or with status
     *         {@code 400 (Bad Request)} if the vCDataStatusLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VCDataStatusLogDTO> createVCDataStatusLog(
            @Valid @RequestBody VCDataStatusLogDTO vCDataStatusLogDTO) throws URISyntaxException {
        LOG.debug("REST request to save VCDataStatusLog : {}", vCDataStatusLogDTO);
        if (vCDataStatusLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new vCDataStatusLog cannot already have an ID", ENTITY_NAME,
                    "idexists");
        }
        vCDataStatusLogDTO = vCDataStatusLogService.save(vCDataStatusLogDTO);
        return ResponseEntity.created(new URI("/api/vc-data-status-logs/" + vCDataStatusLogDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        vCDataStatusLogDTO.getId().toString()))
                .body(vCDataStatusLogDTO);
    }

    /**
     * {@code PUT  /vc-data-status-logs/:id} : Updates an existing vCDataStatusLog.
     *
     * @param id                 the id of the vCDataStatusLogDTO to save.
     * @param vCDataStatusLogDTO the vCDataStatusLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated vCDataStatusLogDTO, or with status
     *         {@code 400 (Bad Request)} if the vCDataStatusLogDTO is not valid, or
     *         with status {@code 500 (Internal Server Error)} if the
     *         vCDataStatusLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VCDataStatusLogDTO> updateVCDataStatusLog(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody VCDataStatusLogDTO vCDataStatusLogDTO) throws URISyntaxException {
        LOG.debug("REST request to update VCDataStatusLog : {}, {}", id, vCDataStatusLogDTO);
        if (vCDataStatusLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vCDataStatusLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vCDataStatusLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        vCDataStatusLogDTO = vCDataStatusLogService.update(vCDataStatusLogDTO);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                vCDataStatusLogDTO.getId().toString())).body(vCDataStatusLogDTO);
    }

    /**
     * {@code PATCH  /vc-data-status-logs/:id} : Partial updates given fields of an
     * existing vCDataStatusLog, field will ignore if it is null
     *
     * @param id                 the id of the vCDataStatusLogDTO to save.
     * @param vCDataStatusLogDTO the vCDataStatusLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated vCDataStatusLogDTO, or with status
     *         {@code 400 (Bad Request)} if the vCDataStatusLogDTO is not valid, or
     *         with status {@code 404 (Not Found)} if the vCDataStatusLogDTO is not
     *         found, or with status {@code 500 (Internal Server Error)} if the
     *         vCDataStatusLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VCDataStatusLogDTO> partialUpdateVCDataStatusLog(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody VCDataStatusLogDTO vCDataStatusLogDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update VCDataStatusLog partially : {}, {}", id, vCDataStatusLogDTO);
        if (vCDataStatusLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vCDataStatusLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vCDataStatusLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VCDataStatusLogDTO> result = vCDataStatusLogService.partialUpdate(vCDataStatusLogDTO);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true,
                ENTITY_NAME, vCDataStatusLogDTO.getId().toString()));
    }

    /**
     * {@code GET  /vc-data-status-logs} : get all the vCDataStatusLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of vCDataStatusLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VCDataStatusLogDTO>> getAllVCDataStatusLogs(VCDataStatusLogCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get VCDataStatusLogs by criteria: {}", criteria);

        Page<VCDataStatusLogDTO> page = vCDataStatusLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vc-data-status-logs/count} : count all the vCDataStatusLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countVCDataStatusLogs(VCDataStatusLogCriteria criteria) {
        LOG.debug("REST request to count VCDataStatusLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(vCDataStatusLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /vc-data-status-logs/:id} : get the "id" vCDataStatusLog.
     *
     * @param id the id of the vCDataStatusLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the vCDataStatusLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VCDataStatusLogDTO> getVCDataStatusLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get VCDataStatusLog : {}", id);
        Optional<VCDataStatusLogDTO> vCDataStatusLogDTO = vCDataStatusLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vCDataStatusLogDTO);
    }

    /**
     * {@code DELETE  /vc-data-status-logs/:id} : delete the "id" vCDataStatusLog.
     *
     * @param id the id of the vCDataStatusLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVCDataStatusLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete VCDataStatusLog : {}", id);
        vCDataStatusLogService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}
