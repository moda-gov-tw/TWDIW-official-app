package gov.moda.dw.manager.web.rest;

import gov.moda.dw.manager.repository.VPItemFieldRepository;
import gov.moda.dw.manager.service.VPItemFieldQueryService;
import gov.moda.dw.manager.service.VPItemFieldService;
import gov.moda.dw.manager.service.criteria.VPItemFieldCriteria;
import gov.moda.dw.manager.service.dto.VPItemFieldDTO;
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
 * REST controller for managing {@link gov.moda.dw.manager.domain.VPItemField}.
 */
@RestController
@RequestMapping("/api/vp-item-fields")
public class VPItemFieldResource {

    private static final Logger log = LoggerFactory.getLogger(VPItemFieldResource.class);

    private static final String ENTITY_NAME = "vPItemField";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VPItemFieldService vPItemFieldService;

    private final VPItemFieldRepository vPItemFieldRepository;

    private final VPItemFieldQueryService vPItemFieldQueryService;

    public VPItemFieldResource(
        VPItemFieldService vPItemFieldService,
        VPItemFieldRepository vPItemFieldRepository,
        VPItemFieldQueryService vPItemFieldQueryService
    ) {
        this.vPItemFieldService = vPItemFieldService;
        this.vPItemFieldRepository = vPItemFieldRepository;
        this.vPItemFieldQueryService = vPItemFieldQueryService;
    }

    /**
     * {@code POST  /vp-item-fields} : Create a new vPItemField.
     *
     * @param vPItemFieldDTO the vPItemFieldDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vPItemFieldDTO, or with status {@code 400 (Bad Request)} if the vPItemField has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPItemFieldDTO> createVPItemField(@Valid @RequestBody VPItemFieldDTO vPItemFieldDTO) throws URISyntaxException {
        log.debug("REST request to save VPItemField : {}", vPItemFieldDTO);
        if (vPItemFieldDTO.getId() != null) {
            throw new BadRequestAlertException("A new vPItemField cannot already have an ID", ENTITY_NAME, "idexists");
        }
        vPItemFieldDTO = vPItemFieldService.save(vPItemFieldDTO);
        return ResponseEntity.created(new URI("/api/vp-item-fields/" + vPItemFieldDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, vPItemFieldDTO.getId().toString()))
            .body(vPItemFieldDTO);
    }

    /**
     * {@code PUT  /vp-item-fields/:id} : Updates an existing vPItemField.
     *
     * @param id the id of the vPItemFieldDTO to save.
     * @param vPItemFieldDTO the vPItemFieldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vPItemFieldDTO,
     * or with status {@code 400 (Bad Request)} if the vPItemFieldDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vPItemFieldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPItemFieldDTO> updateVPItemField(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VPItemFieldDTO vPItemFieldDTO
    ) throws URISyntaxException {
        log.debug("REST request to update VPItemField : {}, {}", id, vPItemFieldDTO);
        if (vPItemFieldDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vPItemFieldDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vPItemFieldRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        vPItemFieldDTO = vPItemFieldService.update(vPItemFieldDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vPItemFieldDTO.getId().toString()))
            .body(vPItemFieldDTO);
    }

    /**
     * {@code PATCH  /vp-item-fields/:id} : Partial updates given fields of an existing vPItemField, field will ignore if it is null
     *
     * @param id the id of the vPItemFieldDTO to save.
     * @param vPItemFieldDTO the vPItemFieldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vPItemFieldDTO,
     * or with status {@code 400 (Bad Request)} if the vPItemFieldDTO is not valid,
     * or with status {@code 404 (Not Found)} if the vPItemFieldDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the vPItemFieldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPItemFieldDTO> partialUpdateVPItemField(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VPItemFieldDTO vPItemFieldDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update VPItemField partially : {}, {}", id, vPItemFieldDTO);
        if (vPItemFieldDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vPItemFieldDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vPItemFieldRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VPItemFieldDTO> result = vPItemFieldService.partialUpdate(vPItemFieldDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vPItemFieldDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /vp-item-fields} : get all the vPItemFields.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vPItemFields in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<List<VPItemFieldDTO>> getAllVPItemFields(
        VPItemFieldCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get VPItemFields by criteria: {}", criteria);

        Page<VPItemFieldDTO> page = vPItemFieldQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vp-item-fields/count} : count all the vPItemFields.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Long> countVPItemFields(VPItemFieldCriteria criteria) {
        log.debug("REST request to count VPItemFields by criteria: {}", criteria);
        return ResponseEntity.ok().body(vPItemFieldQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /vp-item-fields/:id} : get the "id" vPItemField.
     *
     * @param id the id of the vPItemFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vPItemFieldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPItemFieldDTO> getVPItemField(@PathVariable("id") Long id) {
        log.debug("REST request to get VPItemField : {}", id);
        Optional<VPItemFieldDTO> vPItemFieldDTO = vPItemFieldService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vPItemFieldDTO);
    }

    /**
     * {@code DELETE  /vp-item-fields/:id} : delete the "id" vPItemField.
     *
     * @param id the id of the vPItemFieldDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Void> deleteVPItemField(@PathVariable("id") Long id) {
        log.debug("REST request to delete VPItemField : {}", id);
        vPItemFieldService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
