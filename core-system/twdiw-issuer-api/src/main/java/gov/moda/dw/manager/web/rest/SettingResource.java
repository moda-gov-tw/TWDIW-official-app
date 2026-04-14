package gov.moda.dw.manager.web.rest;

import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.repository.custom.CustomSettingRepository;
import gov.moda.dw.manager.service.SettingQueryService;
import gov.moda.dw.manager.service.SettingService;
import gov.moda.dw.manager.service.criteria.SettingCriteria;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.Setting}.
 */
@RestController
@RequestMapping("/api/setting")
public class SettingResource {
    private static final Logger log = LoggerFactory.getLogger(SettingResource.class);

    private static final String ENTITY_NAME = "setting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SettingService settingService;

    private final CustomSettingRepository settingRepository;

    private final SettingQueryService settingQueryService;

    public SettingResource(
        SettingService settingService,
        CustomSettingRepository settingRepository,
        SettingQueryService settingQueryService
    ) {
        this.settingService = settingService;
        this.settingRepository = settingRepository;
        this.settingQueryService = settingQueryService;
    }

    /**
     * {@code POST  /setting} : Create a new setting.
     *
     * @param setting the setting to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new setting.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Setting> createSetting(@Valid @RequestBody Setting setting) throws URISyntaxException {
        log.debug("REST request to save Setting : {}", setting);

        setting = settingService.save(setting);
        return ResponseEntity.created(new URI("/api/setting/" + setting.getPropName()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, setting.getPropName()))
            .body(setting);
    }

    /**
     * {@code PUT  /setting/:propName} : Updates an existing setting.
     *
     * @param propName the propName of the setting to save.
     * @param setting the setting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated setting,
     * or with status {@code 400 (Bad Request)} if the setting is not valid,
     * or with status {@code 500 (Internal Server Error)} if the setting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{propName}")
    public ResponseEntity<Setting> updateSetting(
        @PathVariable(value = "propName", required = false) final String propName,
        @Valid @RequestBody Setting setting
    ) throws URISyntaxException {
        log.debug("REST request to update Setting : {}, {}", propName, setting);
        if (setting.getPropName() == null) {
            throw new BadRequestAlertException("Invalid propName", ENTITY_NAME, "propNamenull");
        }
        if (!Objects.equals(propName, setting.getPropName())) {
            throw new BadRequestAlertException("Invalid propName", ENTITY_NAME, "propNameinvalid");
        }

        if (!settingRepository.existsByPropName(propName)) {
            throw new BadRequestAlertException("propName not found", ENTITY_NAME, "propNamenotfound");
        }

        setting = settingService.update(setting);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, setting.getPropName()))
            .body(setting);
    }

    /**
     * {@code PATCH  /setting/:propName} : Partial updates given fields of an existing setting, field will ignore if it is null
     *
     * @param propName the propName of the setting to save.
     * @param setting the setting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated setting,
     * or with status {@code 400 (Bad Request)} if the setting is not valid,
     * or with status {@code 404 (Not Found)} if the setting is not found,
     * or with status {@code 500 (Internal Server Error)} if the setting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{propName}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Setting> partialUpdateCategory(
        @PathVariable(value = "propName", required = false) final String propName,
        @NotNull @RequestBody Setting setting
    ) throws URISyntaxException {
        log.debug("REST request to partial update Setting partially : {}, {}", propName, setting);
        if (setting.getPropName() == null) {
            throw new BadRequestAlertException("Invalid propName", ENTITY_NAME, "propNamenull");
        }
        if (!Objects.equals(propName, setting.getPropName())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "propNameinvalid");
        }

        if (!settingRepository.existsByPropName(propName)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "propNamenotfound");
        }

        Optional<Setting> result = settingService.partialUpdate(setting);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, setting.getPropName())
        );
    }

    /**
     * {@code GET  /setting} : get all the settings.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Setting>> getAllSettings(
        SettingCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Settings by criteria: {}", criteria);

        Page<Setting> page = settingQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /setting/count} : count all the settings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSetting(SettingCriteria criteria) {
        log.debug("REST request to count Categories by criteria: {}", criteria);
        return ResponseEntity.ok().body(settingQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /setting/:propName} : get the "propName" setting.
     *
     * @param propName the propName of the setting to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the setting, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{propName}")
    public ResponseEntity<Setting> getSetting(@PathVariable("propName") String propName) {
        log.debug("REST request to get Setting : {}", propName);
        Optional<Setting> setting = settingService.findOne(propName);
        return ResponseUtil.wrapOrNotFound(setting);
    }

    /**
     * {@code DELETE  /setting/:propName} : delete the "propName" setting.
     *
     * @param propName the id of the setting to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{propName}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("propName") String propName) {
        log.debug("REST request to delete Setting : {}", propName);
        settingService.delete(propName);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, propName))
            .build();
    }
}
