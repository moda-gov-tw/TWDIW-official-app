package gov.moda.dw.manager.web.rest.custom;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.service.OidvpConfigService;
import gov.moda.dw.manager.service.criteria.OrgKeySettingCriteria;
import gov.moda.dw.manager.service.custom.Dwvp403iService;
import gov.moda.dw.manager.service.custom.Modadw103Service;
import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.CustomOrgKeySettingDTO;
import gov.moda.dw.manager.service.dto.custom.OrgKeyDetailResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgKeyGenerateResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgKeySettingResDTO;
import gov.moda.dw.manager.service.dto.custom.VPOrgKeySettingResDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * 負責 組織金鑰管理 控制器
 */
@RestController
@RequestMapping("/api/modadw103/orgKeySetting")
@RequiredArgsConstructor
@Slf4j
public class Modadw103Resource {

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Modadw103Service modadw103Service;

    private final Dwvp403iService dwvp403iService;

    private final OidvpConfigService oidvpConfigService;

    @PostMapping("")
    @PreAuthorize("hasAuthority('verifier_OrgKeySetting')")
    public ResponseEntity<?> createOrgSetting(@Valid @RequestBody CustomOrgKeySettingDTO customOrgKeySettingDTO) {
        try {
            dwvp403iService.createOrgSetting(customOrgKeySettingDTO);
            return ResponseEntity.ok(BaseHttpResDTO.success());
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('verifier_OrgKeySetting')")
    public ResponseEntity<VPOrgKeySettingResDTO> getAllOrgKeySetting(OrgKeySettingCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get Org Key Items");

        VPOrgKeySettingResDTO vpOrgKeySettingResDTO = new VPOrgKeySettingResDTO();

        // 查 DID
        Optional<OidvpConfig> verifierDID = oidvpConfigService.findOne("verifier.did");
        if (verifierDID.isPresent() && !Objects.equals(verifierDID.get().getPropertyValue(), "")) {
            vpOrgKeySettingResDTO.setVerifyDID(true);
        } else {
            vpOrgKeySettingResDTO.setVerifyDID(false);
        }

        Page<OrgKeySettingResDTO> page = modadw103Service.getAllOrgKeySetting(criteria, pageable);

        vpOrgKeySettingResDTO.setData(page.getContent());

        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(vpOrgKeySettingResDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_OrgKeySetting')")
    public ResponseEntity<OrgKeyDetailResDTO> getKeyItem(@PathVariable("id") Long id) {
        log.debug("REST request to get Org Key Item");

        Optional<OrgKeyDetailResDTO> orgKeyDetailResDTO = Optional.of(modadw103Service.getKeyItem(id));

        return ResponseUtil.wrapOrNotFound(orgKeyDetailResDTO);
    }

    @GetMapping("/generateKey")
    @PreAuthorize("hasAuthority('verifier_OrgKeySetting')")
    public ResponseEntity<OrgKeyGenerateResDTO> generateKey() {
        log.debug("REST request generate Key");

        try {
            OrgKeyGenerateResDTO result = modadw103Service.generateKey();
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            log.error("Modadw103Service-generateKey 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_OrgKeySetting')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.debug("REST request to delete Org Key");
        modadw103Service.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, "org_key_setting", id.toString()))
                .build();
    }

    @PostMapping("/setKeyActive/{id}")
    @PreAuthorize("hasAuthority('verifier_OrgKeySetting')")
    public ResponseEntity<Void> setOrgKeyActive(@PathVariable("id") Long id) {
        log.debug("REST request to activate Org Key");

        modadw103Service.setOrgKeyActive(id);
        return ResponseEntity.ok().build();
    }
}
