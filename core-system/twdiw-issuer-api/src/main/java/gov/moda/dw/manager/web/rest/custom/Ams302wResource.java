package gov.moda.dw.manager.web.rest.custom;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.service.custom.Ams302wService;
import gov.moda.dw.manager.service.custom.Ams311wService;
import gov.moda.dw.manager.service.dto.custom.Ams302wValidateResetKetResDTO;
import gov.moda.dw.manager.service.dto.custom.BwdChangeDTO;
import gov.moda.dw.manager.service.dto.custom.JwtUserDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.util.RSAUtils;
import gov.moda.dw.manager.web.rest.vm.KeyAndPasswordVM;
// import gov.moda.dw.manager.service.dto.custom.BwdRSAResultDTO;
import lombok.extern.slf4j.Slf4j;

/*
 * 更改密碼
 */
@Slf4j
@RestController
@RequestMapping("${app.base.path:/api}")
public class Ams302wResource {

    private static final String ENTITY_NAME = "Ams302wResource";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${fin.rsaUtil.prvKy}")
    private String privateKey;

    @Value("${fin.rsaUtil.pky}")
    private String publicKey;

    @Autowired
    private Ams302wService ams302wService;

    @Autowired
    private Ams311wService ams311wService;

    /**
     * 驗證 重置key
     * @param resetKey
     * @return
     */
    @PostMapping("/modadw302w/reset-bwd/validate/resetKey")
    public ResponseEntity<Ams302wValidateResetKetResDTO> validateResetKey(@RequestBody String resetKey) {
        log.debug("驗證 重置key: {}", resetKey);

        Ams302wValidateResetKetResDTO result = null;
        if (StringUtils.isNotBlank(resetKey)) {
            result = this.ams302wService.getUserIdAndName(resetKey);
        } else {
            throw new RuntimeException("請求失敗，key不可為空");
        }

        return ResponseEntity.ok().body(result);
    }

    public record FinishPasswordResetResponse(String code, String message) {}

    @PostMapping("/modadw302w/reset-bwd/finish")
    public ResponseEntity<FinishPasswordResetResponse> finishPasswordReset(@RequestBody KeyAndPasswordVM request) {
        String reqNewBwd = request.getNewBwd();

        if (StringUtils.isBlank(reqNewBwd)) {
            return ResponseEntity.badRequest().body(new FinishPasswordResetResponse("-1", "密碼未輸入"));
        }

        try {
            String decrypted = RSAUtils.privateDecrypt(reqNewBwd, privateKey);

            if (!this.ams302wService.checkBwdLength(decrypted)) {
                return ResponseEntity.badRequest().body(new FinishPasswordResetResponse("-1", "密碼長度錯誤"));
            }

            this.ams302wService.finishResetBwd(decrypted, request.getKey());
            return ResponseEntity.ok(new FinishPasswordResetResponse("0", "重設密碼成功"));
        } catch (Exception e) {
            log.warn("請求重設密碼錯誤：{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
            return ResponseEntity.internalServerError().body(new FinishPasswordResetResponse("-1", e.getMessage()));
        }
    }

    @PostMapping("/modadw302w/change-bwd/finish")
    public ResponseEntity<ResponseDTO> finishPasswordChange(@RequestBody BwdChangeDTO req) {
        if (StringUtils.isBlank(req.getCurrentBwd()) || StringUtils.isBlank(req.getNewBwd())) {
            throw new RuntimeException("請求失敗，現在密碼或新密碼未輸入");
        }

        try {
            // 解密RSA
            String currentBwd = RSAUtils.privateDecrypt(req.getCurrentBwd(), RSAUtils.getPrivateKey(privateKey));
            String newBwd = RSAUtils.privateDecrypt(req.getNewBwd(), RSAUtils.getPrivateKey(privateKey));

            if (!this.ams302wService.checkBwdLength(newBwd)) {
                throw new RuntimeException("請求失敗，新密碼長度錯誤");
            }

            User user = this.ams302wService.getChangeUser(currentBwd, req.getUserId());
            this.ams302wService.finishChangeBwd(user, newBwd);
            return ResponseEntity.ok(new ResponseDTO<>());
        } catch (Exception e) {

            if (e instanceof RuntimeException) {
                log.warn("請求修改密碼錯誤：{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e.getMessage());
            } else {
                log.error("請求修改密碼錯誤：{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
                throw new RuntimeException("請求失敗，請確認輸入的新舊密碼資訊");
            }
        }
    }

    @PostMapping("/modadw302w/change-bwd/finishWithAuthorization")
    public ResponseEntity<ResponseDTO> finishWithJWTPasswordChange(@RequestBody BwdChangeDTO req) {
        if (StringUtils.isBlank(req.getCurrentBwd()) || StringUtils.isBlank(req.getNewBwd())) {
            throw new RuntimeException("請求失敗，現在密碼或新密碼未輸入");
        }

        try {
            // 解密RSA
            String currentBwd = RSAUtils.privateDecrypt(req.getCurrentBwd(), RSAUtils.getPrivateKey(privateKey));
            String newBwd = RSAUtils.privateDecrypt(req.getNewBwd(), RSAUtils.getPrivateKey(privateKey));

            if (!this.ams302wService.checkBwdLength(newBwd)) {
                throw new RuntimeException("請求失敗，新密碼長度錯誤");
            }

            // 取得JwtUserDTO
            JwtUserDTO jwtUserDTO = null;
            try {
                jwtUserDTO = this.ams311wService.extractJwtUser();
            } catch (JsonProcessingException ex) {
                log.error(
                    "[{}-finishWithJWTPasswordChange]，變更使用者密碼，發生錯誤，{}，錯誤原因:{}。",
                    ENTITY_NAME,
                    "JWT取得使用者帳號發生Exception",
                    ExceptionUtils.getStackTrace(ex)
                );
                throw new RuntimeException(ex.getMessage());
            }

            User user = this.ams302wService.getChangeUser(currentBwd, jwtUserDTO.getUserId());
            this.ams302wService.finishChangeBwd(user, newBwd);
            return ResponseEntity.ok(new ResponseDTO<>());
        } catch (Exception ex) {

            if (ex instanceof RuntimeException) {
                log.warn("請求修改密碼錯誤：{}\n{}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
                throw new RuntimeException(ex.getMessage());
            } else {
                log.error("請求修改密碼錯誤：{}\n{}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
                throw new RuntimeException("請求失敗，請確認輸入的新舊密碼資訊");
            }
        }
    }
}
