package gov.moda.dw.manager.web.rest.custom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.repository.custom.CustomVCItemDataRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.ScheduleQueryService;
import gov.moda.dw.manager.service.ScheduleService;
import gov.moda.dw.manager.service.criteria.ScheduleCriteria;
import gov.moda.dw.manager.service.criteria.VCItemDataCriteria;
import gov.moda.dw.manager.service.custom.CustomScheduleService;
import gov.moda.dw.manager.service.dto.ScheduleDTO;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import gov.moda.dw.manager.service.dto.custom.ClearVcItemDataDTO;
import gov.moda.dw.manager.type.VcItemDataValidType;
import gov.moda.dw.manager.util.SandBoxUtil;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.Schedule}.
 */
@RestController
@RequestMapping("/api/schedule")
public class CustomScheduleResource {

    private static final Logger log = LoggerFactory.getLogger(CustomScheduleResource.class);

    private static final String ENTITY_NAME = "schedule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomScheduleService customScheduleService;
    private final ScheduleService scheduleService;
    private final ScheduleQueryService scheduleQueryService;
    private final CustomVCItemDataRepository customVCItemDataRepository;

    public CustomScheduleResource(
        CustomScheduleService customScheduleService,
        ScheduleService scheduleService,
        ScheduleQueryService scheduleQueryService,
        CustomVCItemDataRepository customVCItemDataRepository
    ) {
        this.customScheduleService = customScheduleService;
        this.scheduleService = scheduleService;
        this.scheduleQueryService = scheduleQueryService;
        this.customVCItemDataRepository = customVCItemDataRepository;
    }

    public void checkIsAdmin() {
        boolean isAdmin = customScheduleService.checkIsAdminResult(SecurityUtils.getJwtUserObject().get(0).getUserId());
        if (!isAdmin) {
            throw new AccessDeniedException("You do not have permission to access this resource.");
        }
    }

    public String getLoginerBusinessId() {
        return SecurityUtils.getJwtUserObject().get(0).getOrgId();
    }

    /**
     * {@code POST  /schedules} : Create a new schedule.
     *
     * @param scheduleDTO the scheduleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleDTO, or with status {@code 400 (Bad Request)} if the schedule has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('vcSchema_clearData')")
    @PostMapping("")
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) throws URISyntaxException {
        log.debug("REST request to save Schedule : {}", scheduleDTO);

        if (scheduleDTO.getId() != null) {
            throw new BadRequestAlertException("A new schedule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Long userId = customScheduleService.findUserId(SecurityUtils.getJwtUserObject().get(0).getUserId());
        scheduleDTO.setCrUser(userId);
        String orgId = getLoginerBusinessId();
        if (orgId == null) {
            throw new AccessDeniedException("You must be belong one Org.");
        }
        scheduleDTO.setBusinessId(orgId);
        scheduleDTO = customScheduleService.save(scheduleDTO);
        return ResponseEntity.created(new URI("/api/schedules/" + scheduleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, scheduleDTO.getId().toString()))
            .body(scheduleDTO);
    }

    /**
     * {@code GET  /schedules} : get all the schedules.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of schedules in body.
     */
    @PreAuthorize("hasAuthority('vcSchema_clearData')")
    @GetMapping("")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules(
        ScheduleCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Schedules by criteria: {}", criteria);
        StringFilter stringFilter = new StringFilter();
        stringFilter.setEquals(getLoginerBusinessId());
        criteria.setBusinessId(stringFilter);
        Page<ScheduleDTO> page = scheduleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/check/isAdminRole")
    public ResponseEntity<Boolean> checkIsAdminRole() {
        log.debug("REST request to check/isAdminRole");
        boolean isAdmin = customScheduleService.checkIsAdminResult(SecurityUtils.getJwtUserObject().get(0).getUserId());
        return ResponseEntity.ok().body(isAdmin);
    }

    /**
     * {@code GET  /schedules/count} : count all the schedules.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @PreAuthorize("hasAuthority('vcSchema_clearData')")
    @GetMapping("/count")
    public ResponseEntity<Long> countSchedules(ScheduleCriteria criteria) {
        log.debug("REST request to count Schedules by criteria: {}", criteria);

        return ResponseEntity.ok().body(scheduleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /schedules/:id} : get the "id" schedule.
     *
     * @param id the id of the scheduleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleDTO, or with status {@code 404 (Not Found)}.
     */
    @PreAuthorize("hasAuthority('vcSchema_clearData')")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getSchedule(@PathVariable("id") Long id) {
        log.debug("REST request to get Schedule : {}", id);

        Optional<ScheduleDTO> scheduleDTO = scheduleService.findOne(id);
        if (scheduleDTO.isPresent()) {
            ScheduleDTO scheduleDTO1 = scheduleDTO.get();
            if (!scheduleDTO1.getBusinessId().equals(getLoginerBusinessId())) {
                throw new AccessDeniedException("You do not have permission to access this resource.");
            }
        }
        return ResponseUtil.wrapOrNotFound(scheduleDTO);
    }

    @PreAuthorize("hasAuthority('vcSchema_clearData')")
    @GetMapping("/{scheduleId}/vcItemData")
    public ResponseEntity<List<VCItemDataDTO>> getScheduleVcData(
        @PathVariable("scheduleId") Long scheduleId,
        VCItemDataCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Schedule : {}", scheduleId);

        // 查Schedule
        Optional<ScheduleDTO> scheduleDTO = scheduleService.findOne(scheduleId);
        Page<VCItemDataDTO> page = null;
        if (scheduleDTO.isPresent()) {
            ScheduleDTO scheduleDTO1 = scheduleDTO.get();
            Page<ClearVcItemDataDTO> clearVcItemDataPage = customVCItemDataRepository.findClearVcItemDataList(
                    VcItemDataValidType.INACTIVE.getCode(), scheduleDTO1.getId(), scheduleDTO1.getLastRunDatetime(),
                    pageable);

            page = clearVcItemDataPage.map(clearVcItemData -> {
                VCItemDataDTO vcItemDataDto = new VCItemDataDTO();
                VCItemDTO vcItemDto = new VCItemDTO();
                vcItemDto.setSerialNo(clearVcItemData.getSerialNo());
                vcItemDto.setName(clearVcItemData.getName());
                vcItemDataDto.setVcItem(vcItemDto);
                vcItemDataDto.setId(clearVcItemData.getId());
                vcItemDataDto.setContent(clearVcItemData.getContent());
                vcItemDataDto.setPureContent(clearVcItemData.getPureContent());
                vcItemDataDto.setCrUser(clearVcItemData.getCrUser());
                vcItemDataDto.setCrDatetime(clearVcItemData.getCrDatetime());
                vcItemDataDto.setValid(clearVcItemData.getValid());
                vcItemDataDto.setClearScheduleId(clearVcItemData.getClearScheduleId());
                vcItemDataDto.setClearScheduleDatetime(clearVcItemData.getClearScheduleDatetime());
                vcItemDataDto.setVcCid(SandBoxUtil.maskVCCid(clearVcItemData.getCid()));
                vcItemDataDto.setTransactionId(clearVcItemData.getTransactionId());
                vcItemDataDto.setBusinessId(clearVcItemData.getBusinessId());
                vcItemDataDto.setVcItemName(clearVcItemData.getVcItemName());
                vcItemDataDto.setQrCode(clearVcItemData.getQrCode());
                vcItemDataDto.setExpired(clearVcItemData.getExpired());
                vcItemDataDto.setScheduleRevokeMessage(clearVcItemData.getScheduleRevokeMessage());
                vcItemDataDto.setIssuanceDate(clearVcItemData.getIssuanceDate());
                return vcItemDataDto;
            });
        } else {
            throw new BadRequestAlertException("schedule not found", ENTITY_NAME, "schedule not found for scheduleId:" + scheduleId);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code DELETE  /schedules/:id} : delete the "id" schedule.
     *
     * @param id the id of the scheduleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('vcSchema_clearData')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable("id") Long id) {
        log.debug("REST request to delete Schedule : {}", id);

        customScheduleService.deleteCleanVCDataJob(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
