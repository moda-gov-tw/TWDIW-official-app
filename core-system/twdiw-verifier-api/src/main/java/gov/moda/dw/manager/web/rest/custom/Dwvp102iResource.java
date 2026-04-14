package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvp102iService;
import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 處理驗證端模板相關資料
 */
@RestController
@RequestMapping("/api/ext")
@RequiredArgsConstructor
@Slf4j
public class Dwvp102iResource {

    private final Dwvp102iService dwvp102iService;

    /**
     * 提供 VP 驗證端 offline 相關資料
     * 
     * @return
     */
    @GetMapping("/offline/all")
    public ResponseEntity<BaseHttpResDTO> getAllOfflineVPItemData() {
        log.debug("REST request to get all offline VPItemData");
        try {
            return ResponseEntity.ok(BaseHttpResDTO.success(dwvp102iService.getAllOfflineVPItemData()));
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

}
