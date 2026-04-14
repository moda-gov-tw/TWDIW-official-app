package gov.moda.dw.manager.web.rest.custom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.repository.custom.CustomSettingRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.DwSandBoxVC201WQueryService;
import gov.moda.dw.manager.service.DwSandBoxVC201WService;
import gov.moda.dw.manager.service.VCItemListRespDTO;
import gov.moda.dw.manager.service.criteria.VCItemCriteria;
import gov.moda.dw.manager.service.custom.CustomDwSandBoxVC201WQueryService;
import gov.moda.dw.manager.service.custom.CustomUserService;
import gov.moda.dw.manager.service.dto.CustomVCItemSettingDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.VC503iResp;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemExposeDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVC201WDownloadReqDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVC201WStaticQRCodeResDTO;
import gov.moda.dw.manager.service.dto.custom.IALTypeDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.service.dto.custom.VCItemServiceUrlDTO;
import gov.moda.dw.manager.service.dto.custom.VCItemStopIssuingReqDTO;
import gov.moda.dw.manager.service.dto.custom.VCItemTitleDTO;
import gov.moda.dw.manager.type.IALType;
import gov.moda.dw.manager.type.StatusCode;
import jakarta.validation.Valid;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.moda.dw.manager.domain.VCItem}.
 */
@RestController
@RequestMapping("/api/vc-items")
public class DwSandBoxVC201WResource {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC201WResource.class);

    private static final String ENTITY_NAME = "vCItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    private final DwSandBoxVC201WService dwSandBoxVC201WService;

    private final CustomVCItemRepository vCItemRepository;

    private final DwSandBoxVC201WQueryService vCItemQueryService;

    private final CustomDwSandBoxVC201WQueryService customVCItemQueryService;

    private final CustomUserService customUserService;

    private final CustomSettingRepository customSettingRepository;

    private final CustomOrgRepository customOrgRepository;

    public DwSandBoxVC201WResource(DwSandBoxVC201WService vCItemService, CustomVCItemRepository vCItemRepository,
            DwSandBoxVC201WQueryService vCItemQueryService, CustomDwSandBoxVC201WQueryService customVCItemQueryService,
            CustomUserService customUserService, CustomSettingRepository customSettingRepository,
            CustomOrgRepository customOrgRepository) {
        this.dwSandBoxVC201WService = vCItemService;
        this.vCItemRepository = vCItemRepository;
        this.vCItemQueryService = vCItemQueryService;
        this.customVCItemQueryService = customVCItemQueryService;
        this.customUserService = customUserService;
        this.customSettingRepository = customSettingRepository;
        this.customOrgRepository = customOrgRepository;
    }

    /**
     * {@code POST  /vc-items} : Create a new vCItem.
     *
     * @param vCItemDTO the vCItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new vCItemDTO, or with status {@code 400 (Bad Request)} if
     *         the vCItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('vcSchema_createVC')")
    @PostMapping("")
    public ResponseEntity<VCItemDTO> createVCItem(@Valid @RequestBody VCItemDTO vCItemDTO)
            throws URISyntaxException, JsonProcessingException {
        log.debug("REST request to save VCItem : {}", vCItemDTO);

        vCItemDTO = dwSandBoxVC201WService.save(vCItemDTO);

        return ResponseEntity
                .created(new URI("/api/vc-items/" + vCItemDTO.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, vCItemDTO.getId().toString()))
                .body(vCItemDTO);
    }

    @PreAuthorize("hasAuthority('vcSchema_createVC')")
    @PostMapping("temp")
    public ResponseEntity<VCItemDTO> tempVCItem(@Valid @RequestBody VCItemDTO vCItemDTO)
            throws URISyntaxException, JsonProcessingException {
        log.debug("REST request to temp VCItem : {}", vCItemDTO);

        vCItemDTO = dwSandBoxVC201WService.temp(vCItemDTO);

        return ResponseEntity
                .created(new URI("/api/vc-items/" + vCItemDTO.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, vCItemDTO.getId().toString()))
                .body(vCItemDTO);
    }

    @PreAuthorize("hasAuthority('vcSchema_createVC')")
    @PostMapping("setting")
    public ResponseEntity<CustomVCItemSettingDTO> createVCItemSetting(
            @Valid @RequestBody CustomVCItemSettingDTO vcItemSettingDTO) throws URISyntaxException {
        log.debug("REST request to save VCItem setting : {}", vcItemSettingDTO);

        vcItemSettingDTO = dwSandBoxVC201WService.createVCItemSetting(vcItemSettingDTO.getVcId(), vcItemSettingDTO);

        return ResponseEntity.created(new URI("/api/vc-items/setting/" + vcItemSettingDTO.getVcId())).headers(HeaderUtil
                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, vcItemSettingDTO.getVcId().toString()))
                .body(vcItemSettingDTO);
    }

    @PreAuthorize("hasAuthority('vcSchema_createVC')")
    @PostMapping("expose")
    public ResponseEntity<VCItemExposeDTO> updateVCItemExpose(@Valid @RequestBody VCItemExposeDTO request)
            throws URISyntaxException {
        log.debug("REST request to update VCItem expose: {}", request);

        VCItemExposeDTO result = dwSandBoxVC201WService.updateVCItemExpose(request);

        return ResponseEntity.created(new URI("/api/vc-items/expose/")).headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, request.getVcId().toString()))
                .body(result);
    }

    @PreAuthorize("hasAuthority('vcSchema_createVC')")
    @PostMapping("issuerServiceUrl")
    public ResponseEntity<VCItemServiceUrlDTO> updateVCItemIssuerServiceUrl(
            @Valid @RequestBody VCItemServiceUrlDTO request) throws URISyntaxException {
        log.debug("REST request to update VCItem Issuer Service Url: {}", request);

        VCItemServiceUrlDTO result = dwSandBoxVC201WService.updateVCItemIssuerServiceUrl(request);

        return ResponseEntity.created(new URI("/api/vc-items/")).headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, request.getId().toString()))
                .body(result);
    }

    /**
     * {@code GET  /vc-items} : get all the vCItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of vCItems in body.
     */
    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @GetMapping("")
    public ResponseEntity<VCItemListRespDTO> getAllVCItems(VCItemCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get VCItems by criteria: {}", criteria);

        // 搜尋條件為組織代號或名稱時需要
        String businessIdContains = null;
        if (criteria.getBusinessId() != null && criteria.getBusinessId().getContains() != null) {
            businessIdContains = criteria.getBusinessId().getContains();
        }

        // 非特權帳號只能看到同組織建立的 VC Schema
        // 非特權但為00000000組織的，只能看到自己建的(和公開的)
        if (!checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId())) {
            StringFilter sf = new StringFilter();
            sf.setEquals(SecurityUtils.getJwtUserObject().get(0).getOrgId());
            criteria.setBusinessId(sf);
        } else {
            // 因應組織搜尋，admin 要把 criteria.busiessId 清空
            criteria.setBusinessId(null);
        }

        Page<VCItemDTO> page = vCItemQueryService.findByCriteria(criteria, pageable, businessIdContains);
        for (VCItemDTO item : page.getContent()) {
            item.setCardCover(null);
        }

        VCItemListRespDTO result = new VCItemListRespDTO();

        // did
        Optional<Setting> issuerDID = customSettingRepository.findByPropName("issuer.did");
        if (issuerDID.isPresent() && !issuerDID.get().getPropValue().isEmpty()) {
            result.setIssuerDID(true);
        }

        // 查組織
        Optional<Org> org = customOrgRepository.findByOrgId(SecurityUtils.getJwtUserObject().get(0).getOrgId());
        if (org.isPresent()) {
            result.setVcSourceType(org.get().getVcDataSource());
        }

        result.setData(page.getContent());

        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(result);
    }

    /**
     * {@code GET  /vc-items/:id} : get the "id" vCItem.
     *
     * @param id the id of the vCItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the vCItemDTO, or with status {@code 404 (Not Found)}.
     */
    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @GetMapping("/{id}")
    public ResponseEntity<VCItemDTO> getVCItem(@PathVariable("id") Long id) {
        log.debug("REST request to get VCItem : {}", id);

        Optional<VCItemDTO> vcItemDTO = Optional.of(dwSandBoxVC201WService.getVCItem(id));

        return ResponseUtil.wrapOrNotFound(vcItemDTO);
    }

    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @GetMapping("/list/{orgId}")
    public ResponseEntity<List<VCItemTitleDTO>> getVCItemList(@PathVariable("orgId") String credentialType) {
        log.debug("REST request to get VCItems list");
        List<VCItemTitleDTO> result = dwSandBoxVC201WService.findVCItemTitleList(credentialType);
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @DeleteMapping("/{id}")
    public ResponseEntity<VC503iResp> delete(@PathVariable("id") Long id) throws JsonProcessingException {
        log.debug("REST request to delete vc_schema : {}", id);

        VC503iResp result = dwSandBoxVC201WService.delete(id);

        return ResponseEntity.ok().body(result);
    }

    public Boolean checkIsPrivilegedAccount(String userId) {
        List<RoleDTO> roles = customUserService.getRoles(userId);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }

    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @GetMapping("/list/ial")
    public ResponseEntity<ResponseDTO<List<IALTypeDTO>>> getIALList() {
        ResponseDTO<List<IALTypeDTO>> responseDTO = new ResponseDTO<>();
        List<IALTypeDTO> dtoList = new ArrayList<>();
        for (IALType type : IALType.values()) {
            IALTypeDTO dto = new IALTypeDTO();
            dto.setCode(type.getCode());
            dto.setName(type.getName());
            dtoList.add(dto);
        }
        responseDTO.setStatusCode(StatusCode.SUCCESS);
        responseDTO.setData(dtoList);

        return ResponseEntity.ok().body(responseDTO);

    }

    /**
     * 產製靜態QR Code
     * 
     * @param credentialType
     * @return String
     */
    @GetMapping("/qrCode")
    public ResponseEntity<ResponseDTO<DwSandBoxVC201WStaticQRCodeResDTO>> getStaticQRCode(
            @RequestParam("credentialType") String credentialType) {
        log.debug("REST request to get getStaticQRCode # credentialType: {}, logoFile: {}", credentialType);
        ResponseDTO<DwSandBoxVC201WStaticQRCodeResDTO> responseDTO = new ResponseDTO<>();

        DwSandBoxVC201WStaticQRCodeResDTO result = dwSandBoxVC201WService.getStaticQRCode(credentialType);

        if (result != null) {
            responseDTO.setStatusCode(StatusCode.SUCCESS);
        } else {
            responseDTO.setStatusCode(StatusCode.FAIL);
        }

        responseDTO.setData(result);

        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 匯出靜態QR Code
     * 
     * @param base64
     * @return byte[]
     */
    @PostMapping("/download")
    public ResponseEntity<Object> downloadStaticQRCode(@RequestBody DwSandBoxVC201WDownloadReqDTO request) {
        log.debug("REST request to get downloadStaticQRCode : {}", request);

        byte[] imageBytes = dwSandBoxVC201WService.downloadStaticQRCode(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("attachment", "qrcode.png");

        return ResponseEntity.ok().headers(headers).body(imageBytes);

    }

    @PreAuthorize("hasAuthority('vcSchema_createVC')")
    @PostMapping("/check/serial")
    public ResponseEntity<String> checkSerialNumberDuplicate(@RequestBody String serialNo) {
        log.debug("REST request to check VCItem serial number : {}", serialNo);

        dwSandBoxVC201WService.checkSerialNumber(serialNo);

        return ResponseEntity.ok(serialNo);
    }

    /**
     * 停止發行 VCItem
     * 
     * @param request
     * @return
     */
    @PreAuthorize("hasAnyAuthority('vcSchema_createVC','vcSchema_removeVC')")
    @PostMapping("/stopIssuing")
    public ResponseEntity<Void> stopIssuing(@RequestBody VCItemStopIssuingReqDTO request) {
        log.debug("REST request to stopIssuing vc_schema : {}", request.getId());

        dwSandBoxVC201WService.stopIssuing(request);

        return ResponseEntity.ok().build();
    }

}
