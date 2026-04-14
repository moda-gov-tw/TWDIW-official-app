package gov.moda.dw.manager.web.rest.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Dwvc101iService;
import gov.moda.dw.manager.service.dto.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.custom.Dwvc101iResDTO;
import gov.moda.dw.manager.util.HttpXxxErrorExceptionHandler;

/**
 * 提供發行端模板相關資料 REST 控制器 
 * 負責取得發行端模板相關資料的查詢
 */
@RestController
@RequestMapping("/api")
public class Dwvc101iResource {

    // 記錄器，用於記錄系統日誌
    private static final Logger log = LoggerFactory.getLogger(Dwvc101iResource.class);

    // 取得發行端模板相關資料
    private final Dwvc101iService dwvc101iService;

    /**
     * 建構子，注入所需的服務
     * 
     * @param dwvc101iService
     */
    public Dwvc101iResource(Dwvc101iService dwvc101iService) {
        this.dwvc101iService = dwvc101iService;
    }

    /**
     * 提供發行端模板相關資料
     * 
     * @param vcUid 統編_模板代碼
     * @return Dwvc101iResDTO 取得發行端模板相關資料 DTO
     */
    @GetMapping("/ext/vcdata/{vcUid}")
    public ResponseEntity<BaseHttpResDTO<Dwvc101iResDTO>> getExtVcData(@PathVariable("vcUid") String vcUid) {
        log.debug("Request to get getIssuerData By vcUid : {}", vcUid);
        try {
            return ResponseEntity.ok(BaseHttpResDTO.success(dwvc101iService.getExtVcData(vcUid)));
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }
}
