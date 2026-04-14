package gov.moda.dw.manager.service.custom;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.Nonce;
import gov.moda.dw.manager.repository.NonceRepository;
import gov.moda.dw.manager.util.CaptchaUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Ams304wService {

    private final NonceRepository nonceRepository;

    /**
     * 取得驗證碼圖檔
     * 
     * @param sId
     * @return
     */
    public String getCaptcha(String sId) {
        final String random = CaptchaUtils.verifyCode();

        Optional<Nonce> nonceOpt = nonceRepository.findOneBySId(sId);
        if (nonceOpt.isEmpty()) {
            throw new BadRequestAlertException("Nonce is null", "nonce", "nonceNull");
        }

        Nonce nonce = nonceOpt.get();
        nonce.captchaCode(random);
        nonceRepository.save(nonce);

        return random;
    }

    /**
     * 檢核驗證碼
     * 
     * @param sId
     * @param captchaCode
     * @return
     */
    public Nonce validateCaptcha(String sId, String captchaCode) {
        if (StringUtils.isBlank(captchaCode)) {
            throw new RuntimeException("圖形驗證碼為null");
        }

        Optional<Nonce> nonceOpt = nonceRepository.findOneBySIdAndCaptchaCode(sId, captchaCode);
        if (nonceOpt.isEmpty()) {
            // 當驗證碼錯誤時，先嘗試找到該筆 nonce 並刪除
            nonceRepository.findOneBySId(sId).ifPresent(nonceRepository::delete);
            throw new RuntimeException("圖形驗證碼錯誤");
        }

        Nonce nonce = nonceOpt.get();
        nonce.captchaCode("pass");
        return nonceRepository.save(nonce);
    }

}
