package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvp404iService;
import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp404iReqDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 處理解密資料相關功能
 */
@RestController
@RequestMapping("/api/ext")
@RequiredArgsConstructor
@Slf4j
public class Dwvp404iResource {

    private final Dwvp404iService dwvp404iService;

    /**
     * 解密資料
     * 
     * @param request
     * @return
     */
    @PostMapping("/offline/getDecryptionData")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<BaseHttpResDTO> getDecryptionData(@RequestBody Dwvp404iReqDTO request) {
        log.debug("REST request to decrypt data");
        try {
            return ResponseEntity.ok(BaseHttpResDTO.success(dwvp404iService.getDecryptionData(request)));
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

}
