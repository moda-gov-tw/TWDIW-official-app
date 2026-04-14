package gov.moda.dw.manager.web.rest.outside;

import gov.moda.dw.manager.domain.outside.vdr.category.SelectCategoryVCItemReq;
import gov.moda.dw.manager.domain.outside.vdr.vcitem.VCItemFieldVdr;
import gov.moda.dw.manager.service.custom.CustomVCItemFieldsVdrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

import java.util.List;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.outside.VCItemField}.
 */
@RestController
@RequestMapping("/api/external/vc-item-fields")
public class VCItemFieldExternalResource {

    private static final Logger log = LoggerFactory.getLogger(VCItemFieldExternalResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomVCItemFieldsVdrService customVCItemFieldsVdrService;

    public VCItemFieldExternalResource(CustomVCItemFieldsVdrService customVCItemFieldsVdrService) {
        this.customVCItemFieldsVdrService = customVCItemFieldsVdrService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<List<VCItemFieldVdr>> getAllVCItemFieldsFromVDR(
        Pageable pageable,
        @RequestBody SelectCategoryVCItemReq req
    ) {
        log.debug("REST request to get external VCItemFields");
        Page<VCItemFieldVdr> page = customVCItemFieldsVdrService.findFieldsByPageForOneVCItem(req,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
