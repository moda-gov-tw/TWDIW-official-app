package gov.moda.dw.manager.web.rest.custom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.domain.Nonce;
import gov.moda.dw.manager.service.custom.Ams304wService;
import gov.moda.dw.manager.service.dto.NonceDTO;
import gov.moda.dw.manager.util.CaptchaUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${app.base.path:/api}")
@RequiredArgsConstructor
public class Ams304wResource {

    private final Ams304wService ams304wService;

    /**
     * 產製 圖形驗證碼
     * 
     * @param reqNonce
     * @param response
     */
    @PostMapping("/modadw304w/getCaptcha")
    public void getCaptcha(@RequestBody NonceDTO reqNonce, HttpServletResponse response) {
        String sId = reqNonce.getsId();
        log.info("## 開始 產生首頁圖形驗證碼 ## {}", sId);
        String random = ams304wService.getCaptcha(sId);

        CaptchaUtils.builder(response).code(random).colorCode().colorNoise().randomSizeCode().build();
        log.info("## 結束 產生首頁圖形驗證碼 ## {}", sId);
    }

    /**
     * 檢核 圖形驗證碼
     * 
     * @param req
     * @return
     */
    @PostMapping("/modadw304w/validateCaptcha")
    public ResponseEntity<Nonce> validateCaptcha(@RequestBody NonceDTO req) {
        try {
            Nonce result = ams304wService.validateCaptcha(req.getsId(), req.getCaptchaCode());
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            log.warn("檢核圖形驗證 出現錯誤: {}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));

            if (e instanceof RuntimeException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .header("msg", Base64.encodeBase64String("檢核圖形驗證 出現錯誤".getBytes())).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("msg", Base64.encodeBase64String("重新整理頁面後，再嘗試輸入新的檢核圖形驗證碼".getBytes())).build();
            }
        }
    }

}
