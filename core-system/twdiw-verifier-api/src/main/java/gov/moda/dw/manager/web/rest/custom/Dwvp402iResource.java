package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvp402iService;
import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp402iReqDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 處理驗證端模板相關資料
 */
@RestController
@RequestMapping("/api/ext")
@RequiredArgsConstructor
@Slf4j
public class Dwvp402iResource {

    private final Dwvp402iService dwvp402iService;

    /**
     * 取得加密資料 QR Code
     * 
     * @param request
     * @return
     */
    @PostMapping("/offline/getEncryptionData")
    public ResponseEntity<BaseHttpResDTO> getEncryptionData(@Valid @RequestBody Dwvp402iReqDTO request) {
        log.debug("REST request to get encryption data");
        try {
            return ResponseEntity.ok(BaseHttpResDTO.success(dwvp402iService.getEncryptionData(request)));
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

}
