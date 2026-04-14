package gov.moda.dw.manager.web.rest.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.DwSandBoxVC302WService;
import gov.moda.dw.manager.service.dto.VCItemDataActionDTO;
import gov.moda.dw.manager.service.dto.VCItemDataUpdateFailDTO;
import jakarta.validation.Valid;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api/vc-item-data")
public class DwSandBoxVC302WResource {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC302WResource.class);

    private static final String ENTITY_NAME = "vCItemData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DwSandBoxVC302WService dwSandBoxVC302WService;

    public DwSandBoxVC302WResource(DwSandBoxVC302WService dwSandBoxVC302WService) {
        this.dwSandBoxVC302WService = dwSandBoxVC302WService;
    }

    /**
     * VC 撤銷、停用、復用
     * 
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('vcSchema_removeVC')")
    @PatchMapping(value = "", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VCItemDataUpdateFailDTO> updateVcItemDataStatus(
            @Valid @RequestBody List<VCItemDataActionDTO> request) {
        log.debug("REST request to save VCItemData : {}", request);
        VCItemDataUpdateFailDTO failResult = dwSandBoxVC302WService.multiAction(request);
        String failList = failResult.getFailList().stream().collect(Collectors.joining(","));

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, failList))
                .body(failResult);
    }
}
