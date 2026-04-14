package gov.moda.dw.manager.web.rest;

import gov.moda.dw.manager.repository.VCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.service.VCItemFieldQueryService;
import gov.moda.dw.manager.service.VCItemFieldService;
import gov.moda.dw.manager.service.criteria.VCItemFieldCriteria;
import gov.moda.dw.manager.service.dto.VCItemFieldDTO;
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
 * REST controller for managing {@link gov.moda.dw.manager.domain.VCItemField}.
 */
@RestController
@RequestMapping("/api/vc-item-fields")
public class VCItemFieldResource {

    private static final Logger log = LoggerFactory.getLogger(VCItemFieldResource.class);

    private static final String ENTITY_NAME = "vCItemField";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VCItemFieldService vCItemFieldService;

    private final CustomVCItemFieldRepository vCItemFieldRepository;

    private final VCItemFieldQueryService vCItemFieldQueryService;

    public VCItemFieldResource(
        VCItemFieldService vCItemFieldService,
        CustomVCItemFieldRepository vCItemFieldRepository,
        VCItemFieldQueryService vCItemFieldQueryService
    ) {
        this.vCItemFieldService = vCItemFieldService;
        this.vCItemFieldRepository = vCItemFieldRepository;
        this.vCItemFieldQueryService = vCItemFieldQueryService;
    }

    /**
     * {@code POST  /vc-item-fields} : Create a new vCItemField.
     *
     * @param vCItemFieldDTO the vCItemFieldDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vCItemFieldDTO, or with status {@code 400 (Bad Request)} if the vCItemField has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PostMapping("")
    //    public ResponseEntity<VCItemFieldDTO> createVCItemField(@Valid @RequestBody VCItemFieldDTO vCItemFieldDTO) throws URISyntaxException {
    //        log.debug("REST request to save VCItemField : {}", vCItemFieldDTO);
    //        if (vCItemFieldDTO.getId() != null) {
    //            throw new BadRequestAlertException("A new vCItemField cannot already have an ID", ENTITY_NAME, "idexists");
    //        }
    //        vCItemFieldDTO = vCItemFieldService.save(vCItemFieldDTO);
    //        return ResponseEntity.created(new URI("/api/vc-item-fields/" + vCItemFieldDTO.getId()))
    //            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, vCItemFieldDTO.getId().toString()))
    //            .body(vCItemFieldDTO);
    //    }

    /**
     * {@code PUT  /vc-item-fields/:id} : Updates an existing vCItemField.
     *
     * @param id the id of the vCItemFieldDTO to save.
     * @param vCItemFieldDTO the vCItemFieldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vCItemFieldDTO,
     * or with status {@code 400 (Bad Request)} if the vCItemFieldDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vCItemFieldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PutMapping("/{id}")
    //    public ResponseEntity<VCItemFieldDTO> updateVCItemField(
    //        @PathVariable(value = "id", required = false) final Long id,
    //        @Valid @RequestBody VCItemFieldDTO vCItemFieldDTO
    //    ) throws URISyntaxException {
    //        log.debug("REST request to update VCItemField : {}, {}", id, vCItemFieldDTO);
    //        if (vCItemFieldDTO.getId() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(id, vCItemFieldDTO.getId())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!vCItemFieldRepository.existsById(id)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //        vCItemFieldDTO = vCItemFieldService.update(vCItemFieldDTO);
    //        return ResponseEntity.ok()
    //            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vCItemFieldDTO.getId().toString()))
    //            .body(vCItemFieldDTO);
    //    }

    /**
     * {@code PATCH  /vc-item-fields/:id} : Partial updates given fields of an existing vCItemField, field will ignore if it is null
     *
     * @param id the id of the vCItemFieldDTO to save.
     * @param vCItemFieldDTO the vCItemFieldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vCItemFieldDTO,
     * or with status {@code 400 (Bad Request)} if the vCItemFieldDTO is not valid,
     * or with status {@code 404 (Not Found)} if the vCItemFieldDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the vCItemFieldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    //    public ResponseEntity<VCItemFieldDTO> partialUpdateVCItemField(
    //        @PathVariable(value = "id", required = false) final Long id,
    //        @NotNull @RequestBody VCItemFieldDTO vCItemFieldDTO
    //    ) throws URISyntaxException {
    //        log.debug("REST request to partial update VCItemField partially : {}, {}", id, vCItemFieldDTO);
    //        if (vCItemFieldDTO.getId() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(id, vCItemFieldDTO.getId())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!vCItemFieldRepository.existsById(id)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //        Optional<VCItemFieldDTO> result = vCItemFieldService.partialUpdate(vCItemFieldDTO);
    //
    //        return ResponseUtil.wrapOrNotFound(
    //            result,
    //            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vCItemFieldDTO.getId().toString())
    //        );
    //    }

    /**
     * {@code GET  /vc-item-fields} : get all the vCItemFields.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vCItemFields in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VCItemFieldDTO>> getAllVCItemFields(
        VCItemFieldCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get VCItemFields by criteria: {}", criteria);

        Page<VCItemFieldDTO> page = vCItemFieldQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    /**
     * {@code GET  /vc-item-fields/count} : count all the vCItemFields.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    //    @GetMapping("/count")
    //    public ResponseEntity<Long> countVCItemFields(VCItemFieldCriteria criteria) {
    //        log.debug("REST request to count VCItemFields by criteria: {}", criteria);
    //        return ResponseEntity.ok().body(vCItemFieldQueryService.countByCriteria(criteria));
    //    }

    /**
     * {@code GET  /vc-item-fields/:id} : get the "id" vCItemField.
     *
     * @param id the id of the vCItemFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vCItemFieldDTO, or with status {@code 404 (Not Found)}.
     */
    //    @GetMapping("/{id}")
    //    public ResponseEntity<VCItemFieldDTO> getVCItemField(@PathVariable("id") Long id) {
    //        log.debug("REST request to get VCItemField : {}", id);
    //        Optional<VCItemFieldDTO> vCItemFieldDTO = vCItemFieldService.findOne(id);
    //        return ResponseUtil.wrapOrNotFound(vCItemFieldDTO);
    //    }

    /**
     * {@code DELETE  /vc-item-fields/:id} : delete the "id" vCItemField.
     *
     * @param id the id of the vCItemFieldDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    //    @DeleteMapping("/{id}")
    //    public ResponseEntity<Void> deleteVCItemField(@PathVariable("id") Long id) {
    //        log.debug("REST request to delete VCItemField : {}", id);
    //        vCItemFieldService.delete(id);
    //        return ResponseEntity.noContent()
    //            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
    //            .build();
    //    }
}
