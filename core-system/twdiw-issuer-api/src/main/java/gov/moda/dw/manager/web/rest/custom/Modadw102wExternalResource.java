package gov.moda.dw.manager.web.rest.custom;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Display Org Logo
 */
@Slf4j
@RestController
@RequestMapping("/api/logo")
public class Modadw102wExternalResource {

    private final CustomOrgRepository orgRepository;

    public Modadw102wExternalResource(CustomOrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    /**
     * 查詢組織 Logo (正方形 base64)
     * 
     * @param orgId
     * @return
     */
    @GetMapping(value = "/square/{orgId}")
    public ResponseEntity<String> getLogoSquare(@PathVariable("orgId") String orgId) {
        // 從資料庫查詢正方形圖片的 Base64 字串
        Optional<Org> org = orgRepository.findByOrgIdOrderByOrgId(orgId);

        if (org.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String base64Image = org.get().getLogoSquare();

        if (StringUtils.isBlank(base64Image)) {
            return ResponseEntity.notFound().build();
        }

        // 直接回傳 Base64 Data
        return ResponseEntity.ok(base64Image);
    }

    /**
     * 組織 Logo (長方形 base64)
     * 
     * @param orgId
     * @return
     */
    @GetMapping(value = "/rectangle/{orgId}")
    public ResponseEntity<String> getLogoRectangle(@PathVariable("orgId") String orgId) {
        // 從資料庫查詢長方形圖片的 Base64 字串
        Optional<Org> org = orgRepository.findByOrgIdOrderByOrgId(orgId);

        if (org.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String base64Image = org.get().getLogoRectangle();

        if (StringUtils.isBlank(base64Image)) {
            return ResponseEntity.notFound().build();
        }

        // 直接回傳 Base64 Data
        return ResponseEntity.ok(base64Image);
    }

}
