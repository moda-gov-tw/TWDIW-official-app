package gov.moda.dw.manager.web.rest.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.CustomInfoService;
import gov.moda.dw.manager.service.dto.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.CustomVersionInfoResDTO;

/**
 * 取得相關資訊的 REST 控制器 負責查詢各系統 management info 資料
 */
@RestController
@RequestMapping("/api/info")
public class CustomInfoResource {

    // 記錄器，用於記錄系統日誌
    private static final Logger log = LoggerFactory.getLogger(CustomInfoResource.class);

    private final CustomInfoService customInfoService;

    /**
     * 建構子，注入所需的服務
     * 
     * @param customInfoService
     */
    public CustomInfoResource(CustomInfoService customInfoService) {
        this.customInfoService = customInfoService;
    }

    /**
     * 取得各系統 management info 資料
     * 
     * @return
     */
    @GetMapping("/version")
    public ResponseEntity<BaseHttpResDTO<CustomVersionInfoResDTO>> getVersionInfo() {
        log.debug("[取得各系統 management info 資料]/api/info/version");

        return ResponseEntity.ok(BaseHttpResDTO.success(customInfoService.getVersionInfo()));
    }

    /**
     * 下載操作手冊
     * 
     * @return
     */
    @GetMapping("/manual")
    public ResponseEntity<Resource> downloadManualFile() {
        log.debug("[下載操作手冊]/api/info/manual");
        byte[] fileBytes = customInfoService.getManualFileBytes();

        // 確保檔案大小非空
        if (fileBytes.length == 0) {
            log.warn("操作手冊檔案為空");
            return ResponseEntity.noContent().build();
        }

        // 創建 Resource
        Resource fileResource = new ByteArrayResource(fileBytes);

        // 設置 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("operation-manual.pdf").build());
        headers.setContentLength(fileBytes.length);

        return ResponseEntity.ok().headers(headers).body(fileResource);
    }
}
