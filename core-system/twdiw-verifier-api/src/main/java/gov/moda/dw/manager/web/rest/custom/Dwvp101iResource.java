package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvp101iService;
import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvp101iResDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 提供驗證端模板相關資料
 */
@RestController
@RequestMapping("/api/ext")
@RequiredArgsConstructor
@Slf4j
public class Dwvp101iResource {

    private final Dwvp101iService dwvp101iService;

    /**
     * 取得驗證端模板相關資料
     *
     * @param vpUid 統編_模板代碼
     * @return 取得驗證端模板相關資料
     */
    @GetMapping("/vpdata/{vpUid}")
    public ResponseEntity<BaseHttpResDTO> getVPItemDataByVpUid(@PathVariable("vpUid") String vpUid) {
        log.debug("REST request to get VPItemData by vpUid : {}", vpUid);
        try {
            Dwvp101iResDTO result = dwvp101iService.getVPItemDataByVpUid(vpUid);
            return ResponseEntity.ok(BaseHttpResDTO.success(result));
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

}
