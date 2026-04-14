package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvp403iService;
import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.CustomOrgKeySettingDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 處理設定金鑰相關資料
 */
@RestController
@RequestMapping("/api/ext")
@RequiredArgsConstructor
@Slf4j
public class Dwvp403iResource {

    private final Dwvp403iService dwvp403iService;

    /**
     * 設定金鑰
     * 
     * @param request
     * @return
     */
    @PostMapping("/offline/addKey")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<BaseHttpResDTO> addKey(@RequestBody CustomOrgKeySettingDTO request) {
        log.debug("REST request to add key");
        try {
            dwvp403iService.createOrgSetting(request);
            return ResponseEntity.ok(BaseHttpResDTO.success());
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, BaseHttpResDTO.class);
        }
    }

}
