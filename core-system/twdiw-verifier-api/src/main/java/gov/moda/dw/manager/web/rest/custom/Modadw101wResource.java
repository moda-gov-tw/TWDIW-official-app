package gov.moda.dw.manager.web.rest.custom;

import gov.moda.dw.manager.service.custom.Modadw101wService;
import gov.moda.dw.manager.service.dto.custom.GetVerifierDataResDTO;
import gov.moda.dw.manager.service.dto.custom.Modadw101ReqDTO;
import gov.moda.dw.manager.service.dto.custom.Modadw101wEnvTestResDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyCertReqDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyCertAndDidResDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyDidReqDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("${app.base.path:/api}")
public class Modadw101wResource {

    @Autowired
    private Modadw101wService modadw101wService;

    @GetMapping("/modadw101w/didRegister")
    @PreAuthorize("hasAuthority('verifier_DIDregister')")
    public ResponseEntity<ResponseDTO<GetVerifierDataResDTO>> getInitData(@RequestParam String orgId) {

        log.info("Modadw101wResource-getInitData 進入DID註冊頁面初始化動作");

        if (orgId == null || orgId.isEmpty()) {
            log.warn("Modadw101wResource-getInitData 缺少必要參數");
            ResponseDTO responseDTO = new ResponseDTO<>();
            responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

        try {
            ResponseDTO<GetVerifierDataResDTO> response = modadw101wService.getInitData(orgId);
            log.info("Modadw101wResource-getInitData 結束取得頁面初始資料");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO<>(StatusCode.DID_INIT_EXCEPTION));
        }
    }

    @PostMapping("/modadw101w/didRegister")
    @PreAuthorize("hasAuthority('verifier_DIDregister')")
    public ResponseEntity<ResponseDTO<StatusCode>> didRegister(@RequestBody Modadw101ReqDTO req) {

        log.info("Modadw101wResource-didRegister 進入DID註冊");

        if (req.getOrg().getName() == null ||
            req.getOrg().getName_en() == null ||
            req.getOrg().getInfo() == null ||
            req.getOrg().getTaxId() == null ||
            req.getOrg().getServiceBaseURL() == null ||
            req.getSignature() == null
        ) {
            log.warn("Modadw101wResource-didRegister 缺少必要參數");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.REQUEST_MISSING_REQUIRED_PARAM));
        }

        try {
            StatusCode response = modadw101wService.register(req);
            log.info("Modadw101wResource-didRegister 結束DID註冊，response:{}", response);
            if (response != null) {
                return ResponseEntity.ok().body(new ResponseDTO<>(response));
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-createUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.DID_REGISTER_EXCEPTION));
    }

    @PostMapping("/modadw101w/verifyCert/{type}")
    @PreAuthorize("hasAuthority('verifier_DIDregister')")
    public ResponseEntity<ResponseDTO<VerifyCertAndDidResDTO>> verifyCert(
        @PathVariable("type") String type,
        @RequestBody VerifyCertReqDTO reqDTO
    ) {
        ResponseDTO<VerifyCertAndDidResDTO> responseDTO = new ResponseDTO<>();
        if (type == null || reqDTO.getB64Data() == null) {
            responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
            log.warn("Modadw101wResource-verifyCert 缺少必要參數 responseDTO={}", responseDTO);
            return ResponseEntity.ok().body(responseDTO);
        }

        try {
            ResponseDTO<VerifyCertAndDidResDTO> response = modadw101wService.verifyCert(reqDTO, type);
            return ResponseEntity.ok().body(response);
        } catch (Exception ex) {
            log.error("Modadw101wResource-verifyCert-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.internalServerError().body(new ResponseDTO<>(StatusCode.DID_VERIFY_EXCEPTION));
        }
    }

    @PostMapping("/modadw101w/verifyDid")
    @PreAuthorize("hasAuthority('verifier_DIDregister')")
    public ResponseEntity<ResponseDTO<VerifyCertAndDidResDTO>> verifyDID(@RequestBody VerifyDidReqDTO reqDTO) {
        ResponseDTO<VerifyCertAndDidResDTO> responseDTO = new ResponseDTO<>();
        if (reqDTO.getB64Data() == null || reqDTO.getToken() == null || reqDTO.getBaseUrl() == null) {
            responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
            log.warn("Modadw101wResource-verifyDID 缺少必要參數 responseDTO={}", responseDTO);
            return ResponseEntity.ok().body(responseDTO);
        }

        try {
            ResponseDTO<VerifyCertAndDidResDTO> response = modadw101wService.verifyDid(reqDTO);
            return ResponseEntity.ok().body(response);
        } catch (Exception ex) {
            log.error("Modadw101wResource-verifyDID-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.internalServerError().body(new ResponseDTO<>(StatusCode.DID_VERIFY_EXCEPTION));
        }
    }

    @GetMapping("/modadw101w/envTest")
    @PreAuthorize("hasAuthority('verifier_DIDregister')")
    public ResponseEntity<ResponseDTO<Modadw101wEnvTestResDTO>> testEnvironment() {
        log.info("Modadw101wResource-testEnvironment 進入環境檢測 API 呼叫");

        try {
            ResponseEntity<String> frontResponse = modadw101wService.callFront302i();
            log.info("Modadw101wResource-testEnvironment front API 呼叫結果: {}", frontResponse.getStatusCode());

            ResponseEntity<String> ivpasResponse = modadw101wService.callIvpasTest();
            log.info("Modadw101wResource-testEnvironment ivpas API 呼叫結果: {}", ivpasResponse.getStatusCode());

            Modadw101wEnvTestResDTO envResult = new Modadw101wEnvTestResDTO(
                frontResponse.getStatusCode().is2xxSuccessful(),
                ivpasResponse.getStatusCode().is2xxSuccessful()
            );

            ResponseDTO<Modadw101wEnvTestResDTO> responseDTO = new ResponseDTO<>();
            responseDTO.setData(envResult);
            if (envResult.isFrontendSuccess() && envResult.isIvpasSuccess()) {
                responseDTO.setStatusCode(StatusCode.SUCCESS);
            } else {
                responseDTO.setStatusCode(StatusCode.DID_ENV_CHECK_FAIL);
                responseDTO.setMsg("環境檢查未通過");
            }
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception ex) {
            log.error("Modadw101wResource-testEnvironment 發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            ResponseDTO responseDTO = new ResponseDTO<>();
            responseDTO.setStatusCode(StatusCode.DID_ENV_CHECK_FAIL);
            responseDTO.setMsg("呼叫外部 API 時發生錯誤：" + ex.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
}
