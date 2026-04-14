package gov.moda.dw.manager.web.rest.custom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.domain.Nonce;
import gov.moda.dw.manager.repository.NonceRepository;
import gov.moda.dw.manager.service.custom.Ams301wService;
import gov.moda.dw.manager.service.dto.custom.Ams301ReqDTO;
import lombok.extern.slf4j.Slf4j;

/*
 * 重置密碼
 */
@Slf4j
@RestController
@RequestMapping("${app.base.path:/api}")
public class Ams301wResource {

    @Autowired
    private Ams301wService ams301wService;

    @Autowired
    private NonceRepository nonceRepository;

    /**
     * 重置密碼
     * @param req
     */
    @PostMapping("/modadw301w/reset-bwd/init")
    public ResponseEntity<Nonce> requestBwdReset(@RequestBody Ams301ReqDTO req) {
        log.debug("[重置密碼]/modadw301w/reset-bwd/init");

        if (StringUtils.isAnyEmpty(req.getMail(), req.getTel())) {
            throw new RuntimeException("請求失敗，請確認輸入的資訊");
        }

        try {
            // 檢查驗證碼
            if (req.getNonce() == null || StringUtils.isEmpty(req.getNonce().getCaptchaCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("msg", Base64.encodeBase64String("圖形驗證碼為null".getBytes()))
                    .build();
            }

            Nonce nonce = nonceRepository.findOneBySIdAndCaptchaCode(req.getNonce().getsId(), req.getNonce().getCaptchaCode())
                .orElseThrow(
                    () -> {
                        // 當驗證碼錯誤時，先嘗試找到該筆 nonce 並刪除
                        this.nonceRepository.findOneBySId(req.getNonce().getsId()).ifPresent(n -> this.nonceRepository.delete(n));
                        return new RuntimeException("圖形驗證碼錯誤");
                    }
                );

            nonce.captchaCode("pass");
            Nonce result = this.nonceRepository.save(nonce);

            if (!this.ams301wService.doResetBwd(req)) {
                throw new RuntimeException("伺服器發送郵件錯誤");
            }

            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            log.warn("重置密碼發生錯誤: {}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));

            if (e instanceof RuntimeException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("msg", Base64.encodeBase64String("重置密碼發生錯誤".getBytes()))
                    .build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("msg", Base64.encodeBase64String("重新整理頁面後，再嘗試輸入新的檢核圖形驗證碼".getBytes()))
                    .build();
            }
        }
    }
}
