package gov.moda.dw.manager.web.rest.outside;

import gov.moda.dw.manager.domain.outside.VcManagerVCItem;
import gov.moda.dw.manager.domain.outside.vdr.category.SelectCategoryReq;
import gov.moda.dw.manager.domain.outside.vdr.vcitem.VCItemVdr;
import gov.moda.dw.manager.service.custom.CustomVCItemFieldsVdrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing {@link VcManagerVCItem}.
 */
@RestController
@RequestMapping("/api/external/vc-items")
public class VCItemExternalResource {

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomVCItemFieldsVdrService customVCItemFieldsVdrService;

    public VCItemExternalResource(CustomVCItemFieldsVdrService customVCItemFieldsVdrService) {
        this.customVCItemFieldsVdrService = customVCItemFieldsVdrService;
    }

    @PostMapping("/specialOrder")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public List<VCItemVdr> getVCInfo(@RequestBody SelectCategoryReq req){
        String url = customVCItemFieldsVdrService.getVdrUrlFactory(req);
        return customVCItemFieldsVdrService.findVcItemsFromVdrUrl(url,req.getTaxId());
    }
}
