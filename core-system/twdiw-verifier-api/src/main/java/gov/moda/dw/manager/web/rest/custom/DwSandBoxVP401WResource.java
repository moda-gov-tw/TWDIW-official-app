package gov.moda.dw.manager.web.rest.custom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;

import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.service.OidvpConfigService;
import gov.moda.dw.manager.service.custom.DwSandBoxVP401WService;
import gov.moda.dw.manager.service.dto.VPItemDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxV401WCreateAndEditResDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVP401RequestDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVP401WDownloadStaticQRCodeReqDTO;
import gov.moda.dw.manager.service.dto.custom.DwSandBoxVP401WStaticQRCodeResDTO;
import gov.moda.dw.manager.service.dto.custom.GetVPItemDataResDTO;
import gov.moda.dw.manager.service.dto.custom.ModelTypeDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.service.dto.custom.UpsertVPItemReqDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemListResDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemSaveTermsDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemSearchAllResDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemSerialNoValidDTO;
import gov.moda.dw.manager.service.dto.custom.ValidVPItemByStepReqDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.VerifierOid4vp101iRespDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.StringValidateUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/vp-item")
public class DwSandBoxVP401WResource {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVP401WResource.class);

    private static final String ENTITY_NAME = "vPItem";

    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DwSandBoxVP401WService dwSandBoxVP401WService;

    private final OidvpConfigService oidvpConfigService;

    public DwSandBoxVP401WResource(DwSandBoxVP401WService dwSandBoxVP401WService,
            OidvpConfigService oidvpConfigService) {
        this.dwSandBoxVP401WService = dwSandBoxVP401WService;
        this.oidvpConfigService = oidvpConfigService;
    }

    @PostMapping("/checkSerialNoValid")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Boolean> checkSerialNoValid(@Valid @RequestBody VPItemSerialNoValidDTO vPItemCreateDTO) {
        return ResponseEntity.ok().body(StringValidateUtils.isValidIdString(vPItemCreateDTO.getSerialNo()));
    }

    /**
     * 取得模式下拉選單
     * 
     * @return
     */
    @GetMapping("/getModelTypeSelect")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<ResponseDTO<List<ModelTypeDTO>>> getModelTypeSelect() {
        ResponseDTO<List<ModelTypeDTO>> responseDTO = new ResponseDTO<>();

        List<ModelTypeDTO> modelTypeDTOs = dwSandBoxVP401WService.getModelTypeSelect();

        responseDTO.setStatusCode(StatusCode.SUCCESS);
        responseDTO.setData(modelTypeDTOs);

        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<DwSandBoxV401WCreateAndEditResDTO> createVPItemFromVDR(
            @Valid @RequestBody UpsertVPItemReqDTO request) throws URISyntaxException {
        log.debug("REST request to save VPItem : {}", request);
        if (!StringValidateUtils.isValidIdString(request.getSerialNo())) {
            throw new BadRequestAlertException("exists error", ENTITY_NAME, "serialNo is not Valid");
        }
        DwSandBoxV401WCreateAndEditResDTO result = dwSandBoxVP401WService.save(request);
        return ResponseEntity
                .created(new URI("/api/vp-items/" + result.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code GET  /vp-items} : get all the vPItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of vPItems in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPItemListResDTO> getAllVPItems(DwSandBoxVP401RequestDTO req,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get VPItems by req: {}", req);

        VPItemListResDTO result = new VPItemListResDTO();

        // 查DID
        Optional<OidvpConfig> verifierDID = oidvpConfigService.findOne("verifier.did");
        if (verifierDID.isPresent() && !Objects.equal(verifierDID.get().getPropertyValue(), "")) {
            result.setVerifierDID(true);
        }
        Page<VPItemSearchAllResDTO> page = dwSandBoxVP401WService.getAllVPItems(req, pageable);

        result.setData(page.getContent());

        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(result);
    }

    @GetMapping("/{id}/qrcode")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VerifierOid4vp101iRespDTO> getVPItemQrcode(@PathVariable("id") Long id) {
        log.debug("REST request to get VPItem QRCode: {}", id);
        UUID transactionId = UUID.randomUUID();
        return ResponseUtil
                .wrapOrNotFound(Optional.ofNullable(dwSandBoxVP401WService.getQrcode(id, transactionId.toString(), "N")));
    }

    @GetMapping("/verifyResult/{transactionId}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<JsonNode> getVPItemVerifyResult(@PathVariable("transactionId") String transactionId) {
        log.debug("REST request to get VPItem VerifyResult :  transaction_id : {} ", transactionId);
        return ResponseUtil
                .wrapOrNotFound(Optional.ofNullable(dwSandBoxVP401WService.getVerifyResultForWeb(transactionId, null)));
    }

    /**
     * {@code DELETE  /vp-items/:id} : delete the "id" vPItem.
     *
     * @param id the id of the vPItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Void> deleteVPItemWithFields(@PathVariable("id") Long id) {
        log.debug("REST request to delete VPItem : {}", id);
        dwSandBoxVP401WService.deleteWithField(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    @PostMapping("/saveTerms")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<VPItemDTO> saveTerms(@Valid @RequestBody VPItemSaveTermsDTO vpItemSaveTermsDTO)
            throws URISyntaxException {
        log.debug("REST request to save VPItem Terms");
        if (!StringValidateUtils.isValidIdString(vpItemSaveTermsDTO.getSerialNo())) {
            throw new BadRequestAlertException("exists error", ENTITY_NAME, "serialNo is not Valid");
        }

        VPItemDTO vPItemDTO = dwSandBoxVP401WService.saveTerms(vpItemSaveTermsDTO);
        return ResponseEntity
                .created(new URI("/api/vp-items/" + vPItemDTO.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, vPItemDTO.getId().toString()))
                .body(vPItemDTO);
    }

    @GetMapping(value = "/getTerms/{serialNo}/{bussinessId}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<String> getTerms(@PathVariable("serialNo") String serialNo,
            @PathVariable("bussinessId") String bussinessId) {
        log.debug("REST request to get VPItem Terms");
        String result = dwSandBoxVP401WService.getTerms(serialNo, bussinessId);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code PUT  /vp-item/:id} : Updates an existing vPItem.
     * 
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<DwSandBoxV401WCreateAndEditResDTO> updateVPItem(
            @PathVariable(value = "id", required = true) final Long id,
            @Valid @RequestBody UpsertVPItemReqDTO request) {
        log.debug("REST request to update VPItem");

        DwSandBoxV401WCreateAndEditResDTO result = dwSandBoxVP401WService.update(request, id);
        return ResponseEntity.ok().headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code GET  /vp-item/:id} : get an existing vpItem by id.
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<GetVPItemDataResDTO> getVPItem(@PathVariable(value = "id", required = true) final Long id) {
        log.debug("REST request to get VPItem by id : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dwSandBoxVP401WService.getVPItem(id)));
    }

    /**
     * {@code POST /vp-item/valid} : valid vpItem by isEdit and step.
     * 
     * @param request
     * @return
     */
    @PostMapping("/valid")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Void> validVPItemByStep(@Valid @RequestBody ValidVPItemByStepReqDTO request) {
        log.debug("REST request to valid VPItem by isEdit {} and step {}", request.getIsEdit(), request.getStep());
        dwSandBoxVP401WService.validVPItemByStep(request, false);
        return ResponseEntity.noContent().headers(
                HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, request.getStep().toString()))
                .build();
    }

    /**
     * 取得靜態QRCode
     * 
     * @param credentialType
     * @return
     */
    @GetMapping("/get/staticQRCode")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<ResponseDTO<DwSandBoxVP401WStaticQRCodeResDTO>> getStaticQRCode(
            @RequestParam String credentialType) {
        log.debug("REST request to getStaticQRCode: {}", credentialType);

        ResponseDTO<DwSandBoxVP401WStaticQRCodeResDTO> responseDTO = new ResponseDTO<>();

        DwSandBoxVP401WStaticQRCodeResDTO result = dwSandBoxVP401WService.getStaticQRCode(credentialType);

        responseDTO.setStatusCode(StatusCode.SUCCESS);
        responseDTO.setData(result);

        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 下載靜態QRCode
     * 
     * @param req
     * @return
     */
    @PostMapping("/download")
    @PreAuthorize("hasAuthority('verifier_CreateVP')")
    public ResponseEntity<Object> downloadStaticQRCode(@RequestBody DwSandBoxVP401WDownloadStaticQRCodeReqDTO req) {
        log.debug("REST request to getStaticQRCode");

        byte[] imageBytes = dwSandBoxVP401WService.downloadStaticQRCode(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("attachment", "qrcode.png");

        return ResponseEntity.ok().headers(headers).body(imageBytes);
    }
}
