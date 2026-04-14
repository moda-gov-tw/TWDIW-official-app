package gov.moda.dw.manager.web.rest.custom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.service.custom.DwSandBoxVP401WService;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxV401WCreateAndEditResDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVP401RequestDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemReqDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemSearchAllResDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemSerialNoValidDTO;
import gov.moda.dw.manager.util.StringValidateUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;


@RestController
@RequestMapping("/api/external/vp-item")
public class DwSandBoxVP401WExternalResource {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVP401WExternalResource.class);

    private static final String ENTITY_NAME = "vPItem";

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DwSandBoxVP401WService dwSandBoxVP401WService;

    public DwSandBoxVP401WExternalResource(DwSandBoxVP401WService dwSandBoxVP401WService) {
        this.dwSandBoxVP401WService = dwSandBoxVP401WService;
    }

    @PostMapping("/checkSerialNoValid")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<Boolean> checkSerialNoValid(
        @Valid @RequestBody VPItemSerialNoValidDTO vPItemCreateDTO
    ) {
        return ResponseEntity.ok().body(StringValidateUtils.isValidIdString(vPItemCreateDTO.getSerialNo()));
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<DwSandBoxV401WCreateAndEditResDTO> createVPItemFromVDR(@Valid @RequestBody UpsertVPItemReqDTO request)
            throws URISyntaxException {
        log.debug("REST request to save VPItem : {}", request);
        if (!StringValidateUtils.isValidIdString(request.getSerialNo())) {
            throw new BadRequestAlertException("exists error", ENTITY_NAME, "serialNo is not Valid");
        }
        DwSandBoxV401WCreateAndEditResDTO vPItemDTO = dwSandBoxVP401WService.save(request);
        return ResponseEntity
                .created(new URI("/api/vp-items/" + vPItemDTO.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, vPItemDTO.getId().toString()))
                .body(vPItemDTO);
    }


    /**
     * {@code GET  /vp-items} : get all the vPItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vPItems in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<List<VPItemSearchAllResDTO>> getAllVPItems(
            DwSandBoxVP401RequestDTO req,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get VPItems by criteria: {}", req);
        Page<VPItemSearchAllResDTO> page = dwSandBoxVP401WService.getAllVPItems(req, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code DELETE  /vp-items/:id} : delete the "id" vPItem.
     *
     * @param id the id of the vPItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<Void> deleteVPItemWithFields(@PathVariable("id") Long id) {
        log.debug("REST request to delete VPItem : {}", id);
        dwSandBoxVP401WService.deleteWithField(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
