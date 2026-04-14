package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvp401iService;
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
public class Dwvp401iResource {

    private final Dwvp401iService dwvp401iService;

    /**
     * 取得於 offline 模式的自主揭露 qrcode
     * 
     * @param vpUid
     * @return
     */
    @GetMapping("/offline/qrcode/{vpUid}")
    public ResponseEntity<BaseHttpResDTO> getOfflineQrcodeByVpUid(@PathVariable("vpUid") String vpUid) {
        log.debug("REST request to get offline qrcode by vpUid : {}", vpUid);
        try {
            return ResponseEntity.ok(BaseHttpResDTO.success(dwvp401iService.getOfflineQrcodeByVpUid(vpUid)));
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

}
