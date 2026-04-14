package gov.moda.dw.manager.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.domain.Field;
import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.RegularExpressionRepository;
import gov.moda.dw.manager.repository.custom.CustomFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.RegularExpressionQueryService;
import gov.moda.dw.manager.service.criteria.RegularExpressionCriteria;
import gov.moda.dw.manager.service.custom.CustomRegularExpressionService;
import gov.moda.dw.manager.service.dto.RegularExpressionDTO;
import gov.moda.dw.manager.util.RegexUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.RegularExpression}.
 */
@RestController
@RequestMapping("/api")
public class RegularExpressionResource {

    private final Logger log = LoggerFactory.getLogger(RegularExpressionResource.class);

    private static final String ENTITY_NAME = "regularExpression";
    private final CustomVCItemFieldRepository customVCItemFieldRepository;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomRegularExpressionService regularExpressionService;

    private final RegularExpressionRepository regularExpressionRepository;

    private final RegularExpressionQueryService regularExpressionQueryService;

    private final CustomFieldRepository customFieldRepository;

    private final CustomUserRepository customUserRepository;

    public RegularExpressionResource(
        CustomRegularExpressionService regularExpressionService,
        RegularExpressionRepository regularExpressionRepository,
        RegularExpressionQueryService regularExpressionQueryService,
        CustomFieldRepository customFieldRepository,
        CustomUserRepository customUserRepository,
        CustomVCItemFieldRepository customVCItemFieldRepository
    ) {
        this.regularExpressionService = regularExpressionService;
        this.regularExpressionRepository = regularExpressionRepository;
        this.regularExpressionQueryService = regularExpressionQueryService;
        this.customFieldRepository = customFieldRepository;
        this.customUserRepository = customUserRepository;
        this.customVCItemFieldRepository = customVCItemFieldRepository;
    }

    /**
     * {@code POST  /regular-expressions} : Create a new regularExpression.
     *
     * @param regularExpression the regularExpression to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new regularExpression, or with status {@code 400 (Bad Request)} if the regularExpression has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('vc_fields_regex')")
    @PostMapping("/regular-expressions")
    public ResponseEntity<RegularExpressionDTO> createRegularExpression(@Valid @RequestBody RegularExpressionDTO regularExpression)
        throws URISyntaxException {
        log.debug("REST request to save RegularExpression : {}", regularExpression);
        if (regularExpression.getId() != null) {
            throw new BadRequestAlertException("A new regularExpression cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RegularExpressionDTO result = regularExpressionService.save(regularExpression);
        return ResponseEntity.created(new URI("/api/regular-expressions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /regular-expressions/:id} : Updates an existing regularExpression.
     *
     * @param id the id of the regularExpression to save.
     * @param regularExpression the regularExpression to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regularExpression,
     * or with status {@code 400 (Bad Request)} if the regularExpression is not valid,
     * or with status {@code 500 (Internal Server Error)} if the regularExpression couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('vc_fields_regex')")
    @PutMapping("/regular-expressions/{id}")
    public ResponseEntity<RegularExpressionDTO> updateRegularExpression(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RegularExpressionDTO regularExpression
    ) throws URISyntaxException {
        log.debug("REST request to update RegularExpression : {}, {}", id, regularExpression);
        if (regularExpression.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regularExpression.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regularExpressionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RegularExpression> regularExpressionOpt = regularExpressionRepository.findById(id);

        if (!regularExpressionOpt.get().getType().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        // 編譯正則表達式（具超時保護）
        Pattern pattern = RegexUtils.matchWithTimeout(regularExpression.getRegularExpression());
        if (null == pattern) {
            throw new BadRequestAlertException("Regex error", ENTITY_NAME, "Regex_Error");
        }

        RegularExpressionDTO result = regularExpressionService.save(regularExpression);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, regularExpression.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /regular-expressions/:id} : Partial updates given fields of an existing regularExpression, field will ignore if it is null
     *
     * @param id the id of the regularExpression to save.
     * @param regularExpression the regularExpression to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regularExpression,
     * or with status {@code 400 (Bad Request)} if the regularExpression is not valid,
     * or with status {@code 404 (Not Found)} if the regularExpression is not found,
     * or with status {@code 500 (Internal Server Error)} if the regularExpression couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('vc_fields_regex')")
    @PatchMapping(value = "/regular-expressions/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<RegularExpressionDTO> partialUpdateRegularExpression(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RegularExpressionDTO regularExpression
    ) throws URISyntaxException {
        log.debug("REST request to partial update RegularExpression partially : {}, {}", id, regularExpression);
        if (regularExpression.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regularExpression.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regularExpressionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RegularExpressionDTO> result = regularExpressionService.partialUpdate(regularExpression);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, regularExpression.getId().toString())
        );
    }

    /**
     * {@code GET  /regular-expressions} : get all the regularExpressions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of regularExpressions in body.
     */
    @PreAuthorize("hasAnyAuthority('vc_fields_regex','vcSchema_createVC')")
    @GetMapping("/regular-expressions")
    public ResponseEntity<List<RegularExpressionDTO>> getAllRegularExpressions(RegularExpressionCriteria criteria, Pageable pageable) {
        log.debug("REST request to get RegularExpressions by criteria: {}", criteria);

        if (criteria.getType() == null || criteria.getType().getEquals().isEmpty()) {
            StringFilter newStringFilter = new StringFilter();
            List<String> typeIn = new ArrayList<>();
            typeIn.add("specified");
            typeIn.add(SecurityUtils.getJwtUserObject().get(0).getOrgId());
            newStringFilter.setIn(typeIn);
            criteria.setType(newStringFilter);
        }

        Page<RegularExpressionDTO> page = regularExpressionQueryService.findByCriteria(criteria, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /regular-expressions/count} : count all the regularExpressions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @PreAuthorize("hasAnyAuthority('vc_fields_regex','vcSchema_createVC')")
    @GetMapping("/regular-expressions/count")
    public ResponseEntity<Long> countRegularExpressions(RegularExpressionCriteria criteria) {
        log.debug("REST request to count RegularExpressions by criteria: {}", criteria);
        return ResponseEntity.ok().body(regularExpressionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /regular-expressions/:id} : get the "id" regularExpression.
     *
     * @param id the id of the regularExpression to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the regularExpression, or with status {@code 404 (Not Found)}.
     */
    @PreAuthorize("hasAnyAuthority('vc_fields_regex','vcSchema_createVC')")
    @GetMapping("/regular-expressions/{id}")
    public ResponseEntity<RegularExpressionDTO> getRegularExpression(@PathVariable Long id) {
        log.debug("REST request to get RegularExpression : {}", id);
        Optional<RegularExpressionDTO> regularExpression = regularExpressionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(regularExpression);
    }

    /**
     * {@code DELETE  /regular-expressions/:id} : delete the "id" regularExpression.
     *
     * @param id the id of the regularExpression to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('vc_fields_regex')")
    @DeleteMapping("/regular-expressions/{id}")
    public ResponseEntity<Void> deleteRegularExpression(@PathVariable Long id) {
        log.debug("REST request to delete RegularExpression : {}", id);

        Optional<RegularExpression> regularExpressionOpt = regularExpressionRepository.findById(id);

        if (!regularExpressionOpt.get().getType().equals(SecurityUtils.getJwtUserObject().get(0).getOrgId())) {
            throw new BadRequestAlertException("Not created by your organization, cannot be deleted", ENTITY_NAME, "cannotdelete");
        }

        VCItemField vcItemField = customVCItemFieldRepository.findByRegularExpressionId(id);

        if (vcItemField != null) {
            throw new BadRequestAlertException("regular is used", ENTITY_NAME, "used");
        }

        Field field = customFieldRepository.findByRegularExpressionId(id);

        if (field != null) {
            throw new BadRequestAlertException("regular is used", ENTITY_NAME, "used");
        }

        regularExpressionService.delete(id);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
