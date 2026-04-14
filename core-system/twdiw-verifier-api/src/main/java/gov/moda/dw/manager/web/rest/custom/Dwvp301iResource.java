package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvp301iService;
import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp301iCallbackReqDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class Dwvp301iResource {

    private final Dwvp301iService dwvp301iService;
    
    /**
     * VP 驗證結果通道處理 callback 驗證端端服務
     *
     * @param request callback 請求
     * @return callback 回應
     */
    @PostMapping("/ext/result")
    public ResponseEntity<BaseHttpResDTO> processCallback(@RequestBody Dwvp301iCallbackReqDTO request) {
        log.info("REST request to process VP verification callback #start");
        try {
            dwvp301iService.processCallback(request);
            return ResponseEntity.ok(BaseHttpResDTO.success());
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

}
