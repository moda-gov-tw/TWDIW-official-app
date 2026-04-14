package gov.moda.dw.manager.web.rest.custom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.DwSandBoxVP401WService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/terms")
public class DwSandBoxVP701WExternalResource {

    private final DwSandBoxVP401WService dwSandBoxVP401WService;

    public DwSandBoxVP701WExternalResource(DwSandBoxVP401WService dwSandBoxVP401WService) {
        this.dwSandBoxVP401WService = dwSandBoxVP401WService;
    }

    /**
     * 查詢使用者條款
     * 
     * @param serialNo
     * @param bussinessId
     * @return
     */
    @GetMapping(value = "/search/{serialNo}/{bussinessId}")
    public ResponseEntity<String> getTerms(@PathVariable("serialNo") String serialNo,
            @PathVariable("bussinessId") String bussinessId) {

        // 1. 從資料庫查詢使用者條款
        String result = dwSandBoxVP401WService.getTerms(serialNo, bussinessId);
        if (StringUtils.isBlank(result)) {
            return ResponseEntity.notFound().build();
        }

        // 替換換行符號
        result = result.replace("\n", "</br>");

        // 2. 直接回傳 terms
        return ResponseEntity.ok(result);
    }
}
