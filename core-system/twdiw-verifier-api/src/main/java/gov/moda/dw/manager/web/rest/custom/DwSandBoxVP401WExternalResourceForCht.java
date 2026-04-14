package gov.moda.dw.manager.web.rest.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import gov.moda.dw.manager.service.custom.DwSandBoxVP401WService;
import gov.moda.dw.manager.service.dto.custom.ext.api.VerifierOid4vp101iV2RespDTO;
import gov.moda.dw.manager.service.dto.custom.ext.api.VerifierOid4vp201iReqDTO;
import gov.moda.dw.manager.util.HttpErrorExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class DwSandBoxVP401WExternalResourceForCht {

    private final DwSandBoxVP401WService dwSandBoxVP401WService;

    @GetMapping("/api/oidvp/qrcode")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<VerifierOid4vp101iV2RespDTO> getVPItemQrcode2(@RequestParam("ref") String ref,
            @RequestParam("transactionId") String transactionId,
            @RequestParam(name = "isCallback", defaultValue = "N") String isCallback) {
        log.debug("REST request to get VPItem QRCode: ref:{} , transaction_id:{}, isCallback:{}", ref, transactionId,
                isCallback);
        try {
            return ResponseEntity.ok(dwSandBoxVP401WService.getQrcodeExternal(ref, transactionId, isCallback));
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, VerifierOid4vp101iV2RespDTO.class);
        }
    }

    @PostMapping("/api/oidvp/result")
    @PreAuthorize("hasAuthority('VP_Create_ExtApi')")
    public ResponseEntity<JsonNode> postVPItemVerifyResult(
            @RequestBody VerifierOid4vp201iReqDTO verifierOid4vp201iReqDTO) {
        String transactionId = verifierOid4vp201iReqDTO.getTransactionId();
        String responseCode = verifierOid4vp201iReqDTO.getResponseCode();
        log.debug("REST request to get VPItem VerifyResult :  transaction_id : {} , response_code : {} ", transactionId,
                responseCode);
        try {
            return ResponseEntity.ok(dwSandBoxVP401WService.getVerifyResult(transactionId, responseCode));
        } catch (Exception ex) {
            return HttpErrorExceptionHandler.handleException(ex, JsonNode.class);
        }
    }

}
