package gov.moda.dw.manager.web.rest.custom;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.service.criteria.VPItemFieldCriteria;
import gov.moda.dw.manager.service.custom.CustomVPItemFieldService;
import gov.moda.dw.manager.service.dto.VPItemFieldDTO;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.VPItemField}.
 */
@RestController
@RequestMapping("/api/external/vp-item-field")
public class CustomVPItemFieldExternalResource {

    private static final Logger log = LoggerFactory.getLogger(CustomVPItemFieldExternalResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomVPItemFieldService customVPItemFieldService;


    public CustomVPItemFieldExternalResource(
        CustomVPItemFieldService customVPItemFieldService
    ) {
        this.customVPItemFieldService = customVPItemFieldService;
    }


    /**
     * {@code GET  /vp-item-fields} : get all the vPItemFields.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vPItemFields in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<List<VPItemFieldDTO>> getAllVPItemFields(
        VPItemFieldCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get VPItemFields by criteria: {}", criteria);
        Page<VPItemFieldDTO> page = customVPItemFieldService.getAllVPItemFields(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    /**
     * {@code GET  /vp-item-fields/:id} : get the "id" vPItemField.
     *
     * @param id the id of the vPItemFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vPItemFieldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<VPItemFieldDTO> getVPItemField(@PathVariable("id") Long id) {
        log.debug("REST request to get VPItemField : {}", id);
        Optional<VPItemFieldDTO> vPItemFieldDTO = customVPItemFieldService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vPItemFieldDTO);
    }

}
