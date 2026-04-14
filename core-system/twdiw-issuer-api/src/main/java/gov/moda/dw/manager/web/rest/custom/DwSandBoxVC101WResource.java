package gov.moda.dw.manager.web.rest.custom;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.domain.Field;
import gov.moda.dw.manager.repository.FieldRepository;
import gov.moda.dw.manager.repository.custom.CustomFieldRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.DwSandBoxVC101WService;
import gov.moda.dw.manager.service.FieldQueryService;
import gov.moda.dw.manager.service.criteria.FieldCriteria;
import gov.moda.dw.manager.service.dto.FieldDTO;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.Field}.
 */
@RestController
@RequestMapping("/api/fields")
public class DwSandBoxVC101WResource {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC101WResource.class);

    private static final String ENTITY_NAME = "field";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DwSandBoxVC101WService dwSandBoxVC101WService;

    private final FieldRepository fieldRepository;

    private final FieldQueryService fieldQueryService;

    public DwSandBoxVC101WResource(
        FieldRepository fieldRepository,
        FieldQueryService fieldQueryService,
        DwSandBoxVC101WService dwSandBoxVC101WService,
        CustomFieldRepository customFieldRepository
    ) {
        this.fieldRepository = fieldRepository;
        this.fieldQueryService = fieldQueryService;
        this.dwSandBoxVC101WService = dwSandBoxVC101WService;
    }

    /**
     * {@code POST  /fields} : Create a new field.
     *
     * @param fieldDTO the fieldDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fieldDTO, or with status {@code 400 (Bad Request)} if the field has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyAuthority('vc_fields_basic','vc_fields_normal')")
    @PostMapping("")
    public ResponseEntity<List<FieldDTO>> createField(@Valid @RequestBody List<FieldDTO> fieldDTO) throws Exception {
        log.debug("REST request to save Field : {}", fieldDTO);

        List<FieldDTO> result = dwSandBoxVC101WService.save(fieldDTO);

        return ResponseEntity.created(new URI("/api/fields/"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ""))
            .body(result);
    }

    @PreAuthorize("hasAnyAuthority('vc_fields_basic','vc_fields_normal')")
    @PutMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Field> updateNameAndVisible(
        @PathVariable(value = "id", required = true) final Long id,
        @NotNull @RequestBody FieldDTO fieldDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Field name and visible: {}, {}", id, fieldDTO);

        if (fieldDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fieldDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        Optional<Field> fieldOpt = fieldRepository.findById(id);

        if (fieldOpt.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Field field = fieldOpt.get();
        field.setVisible(fieldDTO.getVisible());

        fieldRepository.save(field);

        return ResponseEntity.created(new URI("/api/fields/" + fieldDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, fieldDTO.getId().toString()))
            .body(field);
    }

    /**
     * {@code GET  /fields} : get all the fields.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fields in body.
     */
    @PreAuthorize("hasAnyAuthority('vc_fields_basic','vc_fields_normal','vcSchema_createVC')")
    @GetMapping("")
    public ResponseEntity<List<FieldDTO>> getAllFields(
        FieldCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Fields by criteria: {}", criteria);

        if (
            (criteria.getType().getContains() != null && criteria.getType().getContains().contains("NORMAL")) ||
            (criteria.getType().getEquals() != null && criteria.getType().getEquals().equals("NORMAL")) ||
            (criteria.getType().getIn() != null && criteria.getType().getIn().contains("NORMAL"))
        ) {
            StringFilter sf = new StringFilter();
            sf.setEquals(SecurityUtils.getJwtUserObject().get(0).getOrgId());
            criteria.setBusinessId(sf);
        }

        Page<FieldDTO> page = fieldQueryService.findByCriteria(criteria, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fields/:id} : get the "id" field.
     *
     * @param id the id of the fieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fieldDTO, or with status {@code 404 (Not Found)}.
     */
    @PreAuthorize("hasAnyAuthority('vc_fields_basic','vc_fields_normal','vcSchema_createVC')")
    @GetMapping("/{id}")
    public ResponseEntity<FieldDTO> getField(@PathVariable("id") Long id) {
        log.debug("REST request to get Field : {}", id);
        Optional<FieldDTO> fieldDTO = dwSandBoxVC101WService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fieldDTO);
    }

    /**
     * {@code DELETE  /fields/:id} : delete the "id" field.
     *
     * @param id the id of the fieldDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAnyAuthority('vc_fields_basic','vc_fields_normal')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteField(@PathVariable("id") Long id) {
        log.debug("REST request to delete Field : {}", id);

        dwSandBoxVC101WService.delete(id);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
