package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvc100iService;
import gov.moda.dw.manager.service.dto.CreateVCItemDataDTO;
import gov.moda.dw.manager.service.dto.Dwvc100iResDTO;
import gov.moda.dw.manager.util.HttpXxxErrorExceptionHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 電子證件資料的 REST 控制器 負責處理電子證件資料的建立和查詢等相關操作
 */
@Slf4j
@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
public class Dwvc100iResource {

    // 電子證件資料處理服務
    private final Dwvc100iService dwvc100iService;

    /**
     * [DWVC-101] 發行端模組產生QR Code
     *
     * @param request 電子證件資料
     * @return 建立成功的電子證件資料
     */
    @PreAuthorize("hasAuthority('vc_createVC_ExtApi')") // 需要具有建立電子證件的權限
    @PostMapping("/data")
    public ResponseEntity<Dwvc100iResDTO> doQrcodeWithData(@Valid @RequestBody CreateVCItemDataDTO request) {
        // 記錄請求內容
        log.info("[DWVC-101]/api/qrcode/data");

        try {
            // 呼叫服務建立有個資的電子證件
            Dwvc100iResDTO result = dwvc100iService.createVCHasDataForAPI(request);

            // 回傳建立成功的回應
            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }

}
