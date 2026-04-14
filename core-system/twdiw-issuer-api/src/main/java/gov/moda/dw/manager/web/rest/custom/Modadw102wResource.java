package gov.moda.dw.manager.web.rest.custom;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Modadw102wService;
import gov.moda.dw.manager.service.dto.custom.Modadw102wDefaultLogoResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgLogoReqDTO;
import gov.moda.dw.manager.service.dto.custom.OrgLogoResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgLogoUploadResultDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/modadw102w/uploadOrgLogo")
public class Modadw102wResource {

    private final Modadw102wService modadw102wService;

    public Modadw102wResource(Modadw102wService modadw102wService) {
        this.modadw102wService = modadw102wService;
    }

    /**
     * 組織 DID、LOGO 查詢
     *
     * @param request
     * @return
     */
    @GetMapping()
    @PreAuthorize("hasAuthority('issuer_OrgLogo')")
    public ResponseDTO<OrgLogoResDTO> getInit() {
        log.info("Modadw102wResource-orgLogoUpload-getInit 進入組織 LOGO 查詢");
        ResponseDTO<OrgLogoResDTO> responseDTO = new ResponseDTO<>();
        try {
            // 更新組織 LOGO
            OrgLogoUploadResultDTO orgResponseDTO = modadw102wService.search();

            if (orgResponseDTO != null) {
                if (orgResponseDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(orgResponseDTO.getOrgLogoResDTO());
                }
                responseDTO.setStatusCode(orgResponseDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Modadw102wResource-orgLogoUpload-getInit 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }

    }

    /**
     * 組織 LOGO 上傳
     *
     * @param request
     * @return
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('issuer_OrgLogo')")
    public ResponseDTO<OrgLogoResDTO> uploadOrgLogo(@RequestBody OrgLogoReqDTO request) {
        log.info("Modadw102wResource-orgLogoUpload 進入組織 LOGO 上傳");
        ResponseDTO<OrgLogoResDTO> responseDTO = new ResponseDTO<>();

        try {
            // 更新組織 LOGO
            OrgLogoUploadResultDTO orgResponseDTO = modadw102wService.uploadOrgLogo(request);

            if (orgResponseDTO != null) {
                if (orgResponseDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(orgResponseDTO.getOrgLogoResDTO());
                }
                responseDTO.setStatusCode(orgResponseDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Modadw102wResource-orgLogoUpload 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }

    }
    
    /**
     * 還原預設長方形 LOGO
     * 
     * @param orgName
     * @return 
     */
    @GetMapping("/reductionDefault")
    @PreAuthorize("hasAuthority('issuer_OrgLogo')")
    public ResponseEntity<ResponseDTO<Modadw102wDefaultLogoResDTO>> getDefaultLogo(@RequestParam String orgName) {
        log.info("Modadw102wResource-getDefaultLogo 還原預設長方形 LOGO");
        ResponseDTO<Modadw102wDefaultLogoResDTO> response;
        
        try {
            Modadw102wDefaultLogoResDTO result = modadw102wService.getDefaultLogo(orgName);

            if (result.getDefaultRectangleLogo() != null) {
                response = new ResponseDTO<>(result, StatusCode.SUCCESS);
            } else {
                response = new ResponseDTO<>(StatusCode.FAIL);
            }

        } catch (Exception ex) {
            log.error("getReductionDefaultForRectangleLogo 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            response = new ResponseDTO<>(StatusCode.FAIL);
        }

        return ResponseEntity.ok(response);
    }
}
