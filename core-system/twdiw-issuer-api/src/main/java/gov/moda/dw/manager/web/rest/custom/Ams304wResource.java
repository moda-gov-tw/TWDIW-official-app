package gov.moda.dw.manager.web.rest.custom;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import gov.moda.dw.manager.domain.Nonce;
import gov.moda.dw.manager.repository.NonceRepository;
import gov.moda.dw.manager.service.dto.NonceDTO;
import gov.moda.dw.manager.util.CaptchaUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${app.base.path:/api}")
public class Ams304wResource {

  @Autowired
  private NonceRepository nonceRepository;

  /**
   * 產製 圖形驗證碼
   * @param response
   */
  @PostMapping("/modadw304w/getCaptcha")
  public void getVerifyCode(@RequestBody NonceDTO reqNonce, HttpServletResponse response) {
    log.info("## 開始 產生首頁圖形驗證碼 ## {}", reqNonce.getsId());
    final String random = CaptchaUtils.verifyCode();

    Nonce nonce =
      this.nonceRepository.findOneBySId(reqNonce.getsId()).orElseThrow(
          () -> new BadRequestAlertException("Nonce is null", "nonce", "nonceNull")
        );
    nonce.captchaCode(random);
    this.nonceRepository.save(nonce);

    CaptchaUtils.builder(response).code(random).colorCode().colorNoise().randomSizeCode().build();

    log.info("## 結束 產生首頁圖形驗證碼 ## {}", reqNonce.getsId());
  }

  @PostMapping("/modadw304w/validateCaptcha")
  public ResponseEntity<Nonce> validateCaptcha(@RequestBody NonceDTO req) {
    try {
        if(req.getCaptchaCode() == null) {
            throw new RuntimeException("圖形驗證碼為null");
        }
      Nonce nonce =
        this.nonceRepository.findOneBySIdAndCaptchaCode(req.getsId(), req.getCaptchaCode()).orElseThrow(
            () -> {
                // 當驗證碼錯誤時，先嘗試找到該筆 nonce 並刪除
                this.nonceRepository.findOneBySId(req.getsId()).ifPresent(n -> this.nonceRepository.delete(n));
                return new RuntimeException("圖形驗證碼錯誤");
            }
          );

      nonce.captchaCode("pass");
      Nonce result = this.nonceRepository.save(nonce);
      return ResponseEntity.ok().body(result);
    } catch (Exception e) {
      log.warn("檢核圖形驗證 出現錯誤: {}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));

      if (e instanceof RuntimeException) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                  .header("msg", Base64.encodeBase64String("檢核圖形驗證 出現錯誤".getBytes()))
                  .build();
      } else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .header("msg", Base64.encodeBase64String("重新整理頁面後，再嘗試輸入新的檢核圖形驗證碼".getBytes()))
                  .build();
      }
    }
  }
}
