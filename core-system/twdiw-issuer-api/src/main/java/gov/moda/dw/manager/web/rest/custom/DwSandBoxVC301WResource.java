package gov.moda.dw.manager.web.rest.custom;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.VCCredentialQueryService;
import gov.moda.dw.manager.service.custom.CustomUserService;
import gov.moda.dw.manager.service.custom.CustomVCItemDataService;
import gov.moda.dw.manager.service.custom.DwSandBoxVC301WService;
import gov.moda.dw.manager.service.dto.CreateVCItemDataDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import gov.moda.dw.manager.service.dto.VCItemDataEffectDTO;
import gov.moda.dw.manager.service.dto.custom.VCCredentialQueryDTO;
import gov.moda.dw.manager.service.dto.custom.VcItemDataFieldDataResDTO;
import gov.moda.dw.manager.util.HttpXxxErrorExceptionHandler;
import gov.moda.dw.manager.util.RSAUtils;
import jakarta.validation.Valid;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.VCItemData}.
 */
@RestController
@RequestMapping("/api/vc-item-data")
public class DwSandBoxVC301WResource {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC301WResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${sandbox.privileged-account}")
    private String privilegedAccount;

    @Value("${sandbox.commonAccountOrgId:00000000}")
    private String commonAccountOrgId;

    @Value("${fin.rsaUtil.prvKy}")
    private String privateKey;

    private final CustomVCItemDataService vCItemDataService;

    private final VCCredentialQueryService vcCredentialQueryService;

    private final CustomUserService customUserService;

    private final DwSandBoxVC301WService dwSandBoxVC301WService;

    public DwSandBoxVC301WResource(
        CustomVCItemDataService vCItemDataService,
        VCCredentialQueryService vcCredentialQueryService,
        CustomUserService customUserService, DwSandBoxVC301WService dwSandBoxVC301WService
    ) {
        this.vCItemDataService = vCItemDataService;
        this.vcCredentialQueryService = vcCredentialQueryService;
        this.customUserService = customUserService;
        this.dwSandBoxVC301WService = dwSandBoxVC301WService;
    }

    /**
     * 建立 VC 資料 (傳入 VC 資料並呼叫 dwissuer-oid4vci-101i 建立 qrcode 及 dwissuer-vc-501i 把 vc 資料送入核心系統)
     */
    @PreAuthorize("hasAuthority('vcSchema_createVC')")
    @PostMapping("")
    public ResponseEntity<VCItemDataDTO> createVCItemData(@Valid @RequestBody CreateVCItemDataDTO vCItemDataDTO) {
        log.debug("REST request to save VCItemData");
        try {
            VCItemDataDTO result = dwSandBoxVC301WService.save(vCItemDataDTO);

            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }

    /**
     * 跨表取已生效 VC 資料，再回頭 join 沙盒自己的 table 以達到對內容做關鍵字篩選需求
     */
    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @GetMapping("")
    public ResponseEntity<Page<VCItemDataEffectDTO>> getAllVCItemDataV2(
        VCCredentialQueryDTO request,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.debug("REST request to get VCItemData from table vc.credential: {}", request);

        // 非特權帳號只能看到同組織建立的 VC 模版
        if (!checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId())) {
            request.setOrgId(SecurityUtils.getJwtUserObject().get(0).getOrgId());
            if (SecurityUtils.getJwtUserObject().get(0).getOrgId().equals(commonAccountOrgId)) {
                request.setCrUserId(customUserService.getLoginUserId());
            } else {
                request.setCrUserId(null);
            }
        }

        Page<VCItemDataEffectDTO> result = vcCredentialQueryService.findBySQL(request, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), result);

        return ResponseEntity.ok().headers(headers).body(result);
    }

    /**
     * 跨表取指定的已生效 VC 資料，再回頭 join 沙盒自己的 table 取該 VC 資料
     */
    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @GetMapping("/detail/{vccid}")
    public ResponseEntity<VcItemDataFieldDataResDTO> getVCItemDataDetail(@PathVariable("vccid") String vccidEncode)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.debug("REST request to get VCItemDataDetail from table vc.credential vcid: {}", vccidEncode);

        String vccidDecode = RSAUtils.privateDecryptFromBase32(vccidEncode, RSAUtils.getPrivateKey(privateKey));

        VcItemDataFieldDataResDTO result = vcCredentialQueryService.findVcItemDetail(vccidDecode);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 透過 vc data id 向 dwissuer-vc-402i 查詢 vc qrcode 狀態
     */
    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @GetMapping("/{id}")
    public ResponseEntity<VCItemDataDTO> getVCItemData(@PathVariable("id") Long id) {
        log.debug("REST request to get VCItemData : {}", id);
        Optional<VCItemDataDTO> vCItemDataDTO = vCItemDataService.findAndUpdateVCDataStatus(id);
        return ResponseUtil.wrapOrNotFound(vCItemDataDTO);
    }

    public Boolean checkIsPrivilegedAccount(String userId) {
        List<RoleDTO> roles = customUserService.getRoles(userId);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }

}
