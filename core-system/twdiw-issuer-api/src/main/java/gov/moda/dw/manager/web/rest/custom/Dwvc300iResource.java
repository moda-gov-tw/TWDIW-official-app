package gov.moda.dw.manager.web.rest.custom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.DwSandBoxVC302WService;
import gov.moda.dw.manager.service.dto.DwissuerVC203iResp;
import gov.moda.dw.manager.service.dto.Dwvc302iReqDTO;
import gov.moda.dw.manager.service.dto.Dwvc302iResDTO;
import gov.moda.dw.manager.util.HttpXxxErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 電子證件撤銷的 REST 控制器
 * 負責處理電子證件的撤銷操作，提供撤銷電子證件的 API 端點
 * 此控制器主要用於處理電子證件的狀態變更，例如撤銷或停用
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Dwvc300iResource {

    // 應用程式名稱，從配置檔中讀取
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    // 電子證件撤銷服務，用於處理電子證件的撤銷操作
    private final DwSandBoxVC302WService dwSandBoxVC302WService;

    /**
     * [DWVC-301] 發行端 VC 卡片狀態變更
     * 撤銷或變更電子證件狀態
     * 此 API 用於處理電子證件的撤銷操作，可以根據不同的 action 參數執行不同的操作
     * 例如：撤銷、停用、復用等
     *
     * @param cid 電子證件識別碼
     * @param action 要執行的動作（例如：revocation、suspension、recovery 等）
     * @return 操作結果，包含操作狀態和相關訊息
     */
    @PreAuthorize("hasAuthority('vc_removeVC_ExtApi')")  // 需要具有撤銷電子證件的權限
    @PutMapping(value = "/credential/{cid}/{action}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DwissuerVC203iResp> updateVcCredentialStatus(@PathVariable("cid") String cid,
            @PathVariable("action") String action) {
        // 記錄請求資訊
        log.debug("REST request to change credential action : {}, {}", cid, action);

        // 回傳操作結果
        try {
            // 呼叫服務執行電子證件撤銷操作
            DwissuerVC203iResp result = dwSandBoxVC302WService.updateVcCredentialStatus(cid, action);

            // 如有錯誤代碼
            if (StringUtils.isNotBlank(result.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }

            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }

    /**
     * [DWVC-302] 發行端 VC 多筆卡片狀態變更
     * 例如：撤銷、停用、復用等
     *
     * @param request cid 電子證件識別碼、action 要執行的動作（例如：revocation、suspension、recovery 等）
     * @return 操作結果，包含操作狀態和相關訊息
     */
    @PreAuthorize("hasAuthority('vc_removeVC_ExtApi')")  // 需要具有撤銷電子證件的權限
    @PutMapping(value = "/credential/multiaction", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Dwvc302iResDTO> multiaction(@RequestBody Dwvc302iReqDTO request) {
        // 記錄請求資訊
        log.debug("REST request to change credential multiaction action: {}, cids : {}", request.getAction(),
                request.getCids());

        // 回傳操作結果
        try {
            // 呼叫服務執行電子證件撤銷操作
            Dwvc302iResDTO result = dwSandBoxVC302WService.multiaction(request);
            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }

}
