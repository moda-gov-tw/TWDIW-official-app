package gov.moda.dw.manager.web.rest.custom;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.CustomUserService;
import gov.moda.dw.manager.service.custom.CustomVCItemDataService;
import gov.moda.dw.manager.service.dto.Dwvc201iResDTO;
import gov.moda.dw.manager.service.dto.Dwvc202iResDTO;
import gov.moda.dw.manager.service.dto.Dwvc203iReqDTO;
import gov.moda.dw.manager.service.dto.Dwvc203iResDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.VCDataQrcodeReqDTO;
import gov.moda.dw.manager.service.dto.custom.DwIssuerOidVci101iRes;
import gov.moda.dw.manager.util.HttpXxxErrorExceptionHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 電子證件資料的 REST 控制器
 * 負責處理電子證件資料的查詢、建立和 QR Code 相關操作
 * 包含外部 API 整合和沙盒環境的資料處理
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Dwvc200iResource {

    // 應用程式名稱，從配置檔中讀取
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    // 特權帳號設定，從配置檔中讀取
    @Value("${sandbox.privileged-account}")
    private String privilegedAccount;

    // 電子證件資料服務
    private final CustomVCItemDataService vCItemDataService;

    // 使用者服務
    private final CustomUserService customUserService;

    /**
     * [DWVC-201] 發行端VC卡片單一資料查詢
     * 透過 nonce 向 dwissuer-vc-402i 查詢電子證件 QR Code 狀態
     * 此 API 用於查詢使用者是否已經掃描 QR Code 並完成電子證件的建立
     *
     * @param nonce 交易識別碼
     * @return 電子證件狀態資訊
     * @throws IOException 當 IO 操作失敗時拋出
     */
    @PreAuthorize("hasAnyAuthority('vc_createVC_ExtApi','vc_removeVC_ExtApi')")  // 需要具有建立或移除電子證件的權限
    @GetMapping("/credential/nonce/{nonce_id}")
    public ResponseEntity<Dwvc201iResDTO> getVCItemDataByNonce(@PathVariable("nonce_id") String nonce) throws IOException {
        log.debug("REST request to get VCItemData : {}", nonce);
        try {
            // 查詢並更新電子證件狀態
            Dwvc201iResDTO dwvc201iResDTO = vCItemDataService.findAndUpdateVCDataStatusByNonce(nonce);

            return ResponseEntity.ok().body(dwvc201iResDTO);
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }

    /**
     * 傳入 JWT，代為呼叫 dwissuer-oid4vci-101i 取得電子證件資料用的 QR Code
     * 此 API 用於產生電子證件資料的 QR Code
     *
     * @param business_id 業務識別碼
     * @param request QR Code 請求資料
     * @return QR Code 資訊
     */
    @PreAuthorize("hasAuthority('vc_createVC_ExtApi')")  // 需要具有建立電子證件的權限
    @PostMapping("/issuer/{business_id}/qr-code")
    public ResponseEntity<DwIssuerOidVci101iRes> createVCDataQrcode(
        @PathVariable("business_id") String businessId,
        @RequestBody @Valid VCDataQrcodeReqDTO request
    ) {
        log.debug("REST request to create VCItemData qrcode (getdata) : {}", request);

        // 產生 QR Code 並取得結果
        DwIssuerOidVci101iRes result = vCItemDataService.createQrcode(request);

        // 回傳成功結果
        return ResponseEntity.ok().body(result);
    }

    /**
     * [DWVC-202] 透過資料標籤查詢發行端 VC 卡片
     *
     * @param dataTag 資料標註
     * @param pageable 分頁參數
     * @return 發行端 VC 卡片
     */
    @PreAuthorize("hasAuthority('vc_createVC_ExtApi')") // 需要具有建立電子證件的權限
    @GetMapping("/credential/datatag/{dataTag}")
    public ResponseEntity<Dwvc202iResDTO> getVCItemDataByDataTag(@PathVariable("dataTag") String dataTag,
            @org.springdoc.core.annotations.ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        log.info("REST request to get VCItemData by dataTag : {}, pageable : {}", dataTag, pageable);
        try {
            // 查詢發行端 VC 卡片
            Dwvc202iResDTO result = vCItemDataService.findVcItemDataByDataTag(dataTag, pageable);

            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }

    /**
     * [DWVC-203] 透過 VC 卡片相關資訊查詢發行端 VC 卡片
     *
     * @param request VC 卡片相關資訊
     * @return 發行端 VC 卡片
     */
    @PreAuthorize("hasAuthority('vc_createVC_ExtApi')") // 需要具有建立電子證件的權限
    @PostMapping("/credential/vcdata")
    public ResponseEntity<Dwvc203iResDTO> getVCItemData(@RequestBody Dwvc203iReqDTO request) {
        log.debug(
                "REST request to get VCItemData by dataTag : {}, vcUid : {}, credentialStatus : {}, page : {}, size : {}",
                request.getDataTag(), request.getVcUid(), request.getCredentialStatus(), request.getPage(),
                request.getSize());
        try {
            // 查詢發行端 VC 卡片
            Dwvc203iResDTO result = vCItemDataService.findVcItemData(request);

            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            return HttpXxxErrorExceptionHandler.handleException(ex);
        }
    }

    /**
     * 檢查使用者是否為特權帳號
     * 根據使用者 ID 查詢其角色，判斷是否具有特權帳號權限
     *
     * @param userId 使用者 ID
     * @return 是否為特權帳號
     */
    public Boolean checkIsPrivilegedAccount(String userId) {
        // 取得使用者的角色列表
        List<RoleDTO> roles = customUserService.getRoles(userId);
        // 檢查是否具有特權帳號角色
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }
}
