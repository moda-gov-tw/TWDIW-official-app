package gov.moda.dw.manager.web.rest.outside;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.domain.outside.vdr.category.CategoryVdrSimpleDto;
import gov.moda.dw.manager.service.custom.CustomCategoryService;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.outside.Category}.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryResource {

    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final CustomCategoryService customCategoryService;

    public CategoryResource(CustomCategoryService customCategoryService) {
        this.customCategoryService = customCategoryService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<List<CategoryVdrSimpleDto>> getAllCategoriesFromVDR() {
        return ResponseEntity.ok().body(customCategoryService.getFromVDRStatus1ToDto());
    }

}
