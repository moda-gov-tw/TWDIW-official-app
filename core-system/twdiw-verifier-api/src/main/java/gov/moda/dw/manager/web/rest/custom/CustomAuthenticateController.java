package gov.moda.dw.manager.web.rest.custom;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.security.UserNotActivatedException;
import gov.moda.dw.manager.security.accessToken.AccessTokenProvider;
import gov.moda.dw.manager.security.auth.AmsAuthenticationProvider;
import gov.moda.dw.manager.security.auth.AmsUsernamePasswordAuthenticationToken;
import gov.moda.dw.manager.service.NonceService;
import gov.moda.dw.manager.service.custom.Ams302wService;
import gov.moda.dw.manager.service.custom.Ams303wService;
import gov.moda.dw.manager.service.custom.Ams311wService;
import gov.moda.dw.manager.service.custom.BwdParamCustomService;
import gov.moda.dw.manager.service.dto.NonceDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserResultDTO;
import gov.moda.dw.manager.service.dto.custom.ForceBwdChangeDTO;
import gov.moda.dw.manager.service.dto.custom.GenerateOtpReqDTO;
import gov.moda.dw.manager.service.dto.custom.IsPasswordValidReqDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyOtpReqDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.RSAUtils;
import gov.moda.dw.manager.util.UuidUtils;
import gov.moda.dw.manager.web.rest.vm.LoginVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to authenticate users.
 */
@Slf4j
@RestController
@RequestMapping("${app.base.path:/api}")
public class CustomAuthenticateController {

    public static final String JWTUSER_KEY = "jwtuser";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CAPTCHA_PASSED_CODE = "pass";
    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccessTokenProvider accessTokenProvider;
    private final NonceService nonceService;
    private final BwdParamCustomService bwdParamCustomService;
    private final Ams302wService ams302wService;
    private final Ams303wService ams303wService;
    private final Ams311wService ams311wService;
    private final AmsAuthenticationProvider amsAuthenticationProvider;

    @Value("${fin.rsaUtil.prvKy}")
    private String privateKey;

    public CustomAuthenticateController(
        AuthenticationManagerBuilder authenticationManagerBuilder,
        AccessTokenProvider accessTokenProvider,
        NonceService nonceService,
        BwdParamCustomService bwdParamCustomService,
        Ams302wService ams302wService,
        Ams303wService ams303wService,
        Ams311wService ams311wService,
        AmsAuthenticationProvider amsAuthenticationProvider
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.accessTokenProvider = accessTokenProvider;
        this.nonceService = nonceService;
        this.bwdParamCustomService = bwdParamCustomService;
        this.ams302wService = ams302wService;
        this.ams303wService = ams303wService;
        this.ams311wService = ams311wService;
        this.amsAuthenticationProvider = amsAuthenticationProvider;
    }

    public record JWTToken(@JsonProperty("id_token") String idToken, @JsonProperty("detail") String detail) {}

    @PostMapping("/modadw303w/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        // 檢核 OTP 是否已驗證成功
        ams311wService.verifyOtpIsPass(loginVM.getUsername());

        Long nonceId = Optional.ofNullable(loginVM.getNonce()).map(NonceDTO::getId).orElse(0L);

        try {
            // 依據帳號搜尋 Extended User table 資料
            String username = loginVM.getUsername();
            String pwd = loginVM.getPassword();

            Ams311wCurrentUserResultDTO ams311wCurrentUserResultDTO = ams311wService.queryPersonalInfo(username);

            if (ams311wCurrentUserResultDTO == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Ams311wCurrentUserResDTO currentUser = ams311wCurrentUserResultDTO.getAms311wCurrentUserResDTO();
            StatusCode statusCode = ams311wCurrentUserResultDTO.getStatusCode();

            if (currentUser == null || !StatusCode.SUCCESS.equals(statusCode)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String requestNonceId;
            boolean isAuthRequestFromAPServer = "AP".equalsIgnoreCase(currentUser.getUserTypeId());

            if (isAuthRequestFromAPServer) {
                throw new RuntimeException("AP 帳號無法進行登入");
            } else {
                NonceDTO nonce = this.nonceService.findOne(nonceId).orElseThrow(() -> new BadCredentialsException("帳號密碼錯誤"));
                // 檢查 captcha 是否已經被驗證過
                if (!CAPTCHA_PASSED_CODE.equals(nonce.getCaptchaCode())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
                requestNonceId = nonce.getNonceId();
            }

            // 封裝未驗證的 UsernamePasswordAuthenticationToken
            UsernamePasswordAuthenticationToken authenticationToken = new AmsUsernamePasswordAuthenticationToken(
                username,
                pwd,
                requestNonceId
            );

            // 進行身分驗證
            // 身分驗證成功後將訊息放入 SecurityContext 儲存, 以利後續權限檢查或是確保訊息在整個請求處理過程中都可以被訪問。
            // 製作客製化的jwtToken內容
            // 重置帳號驗證失敗次數
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = amsAuthenticationProvider.createToken(authentication, Optional.ofNullable(loginVM.isRememberMe()).orElse(false));
            this.bwdParamCustomService.resetFailCount(username);

            // 更新 OtpVerify
            ams311wService.updateOtpVerify(username);

            return new ResponseEntity<>(new JWTToken(jwt, null), HttpStatus.OK);
        } catch (Exception e) {
            if (e.getCause() instanceof UserNotActivatedException || e instanceof BadCredentialsException) {
                log.warn("error while attempting login: {}", ExceptionUtils.getRootCauseMessage(e));
                String errorMessage = e.getMessage();
                log.info("errorMessage: {}", errorMessage);
                if (errorMessage != null && errorMessage.equals("你的密碼已到期")) {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    return new ResponseEntity<>(
                        new JWTToken(null, "密碼已到期，請至忘記密碼頁面更新密碼"),
                        httpHeaders,
                        HttpStatus.UNAUTHORIZED
                    );
                }
                if (errorMessage != null && errorMessage.equals("帳號已鎖定，稍後再登入")) {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    return new ResponseEntity<>(
                        new JWTToken(null, "帳號或密碼錯誤達3次以上，請於15分鐘後再重新登入"),
                        httpHeaders,
                        HttpStatus.UNAUTHORIZED
                    );
                }
            } else {
                log.warn("error while attempting login: {}", ExceptionUtils.getRootCauseMessage(e));
                String errorMessage = e.getMessage();
                if (errorMessage != null && errorMessage.equals("AP 帳號無法進行登入")) {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    return new ResponseEntity<>(
                            new JWTToken(null, "AP 帳號無法進行登入"),
                            httpHeaders,
                            HttpStatus.UNAUTHORIZED
                            );
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } finally {
            // 在一般的登入情況下再進行刪除，若是AP登入則不需要刪除
            if (nonceId > 0) {
                nonceService.delete(nonceId);
            }
        }
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/modadw303w/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    @PostMapping("/modadw303w/logout")
    public ResponseDTO<?> doLogout(HttpServletRequest request) {
        log.info("準備登出");
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.debug(bearerToken);

        String jwt = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            jwt = bearerToken.substring(7);
        }
        boolean logoutOk = accessTokenProvider.logoutToken(jwt);
        if (logoutOk) {
            log.info("登出成功");
        } else {
            log.info("token驗證失敗");
        }

        return new ResponseDTO<>(StatusCode.SUCCESS);
    }

    /**
     * 沿用密碼
     *
     * @param userId 使用者Id
     */
    @PostMapping("/modadw303w/forceKeepBwd")
    public void keep(@RequestBody String userId) {
        log.info("使用了沿用密碼");
        try {
            this.bwdParamCustomService.keepBwd(userId);
        } catch (Exception e) {
            log.error("沿用密碼出現錯誤: {}", ExceptionUtils.getRootCauseMessage(e));
            throw e;
        }
    }

    @PostMapping("/modadw303w/forceChangeBwd")
    public void finishPasswordChange(@RequestBody ForceBwdChangeDTO req) {
        if (!this.ams302wService.checkBwdLength(req.getNewBwd())) {
            throw new RuntimeException("請求失敗，請確認輸入的新舊密碼資訊");
        }

        try {
            String currentBwd = this.decrypt(req.getCurrentBwd());
            String newBwd = this.decrypt(req.getNewBwd());
            User user = this.ams302wService.getChangeUser(currentBwd, req.getUserId());

            this.ams302wService.finishChangeBwd(user, newBwd);
        } catch (Exception e) {

            if (e instanceof RuntimeException) {
                log.warn("請求修改密碼錯誤：{}\n{}", e.getMessage(), ExceptionUtils.getRootCauseMessage(e));
                throw new RuntimeException(e.getMessage());
            } else {
                log.error("請求修改密碼錯誤：{}\n{}", e.getMessage(), ExceptionUtils.getRootCauseMessage(e));
                throw new RuntimeException("請求失敗，請確認輸入的新舊密碼資訊");
            }
        }
    }

    @GetMapping("/modadw303w/generateNonce")
    public ResponseEntity<NonceDTO> generateNonce() {
        String sid = UuidUtils.getRandomString(32);
        String nonceId = UuidUtils.getRandomString(32);
        LocalDate createTime = LocalDate.now();

        NonceDTO nonce = new NonceDTO();
        nonce.setsId(sid);
        nonce.setNonceId(nonceId);
        nonce.setCreateTime(createTime);

        NonceDTO savedNonce = this.nonceService.save(nonce);
        log.debug("nonce created: {}", savedNonce);

        return ResponseEntity.ok().body(savedNonce);
    }

    /**
     * 確認登入的使用者所使用的密碼是否還未超過有效期
     */
    @PostMapping("/modadw303w/isPasswordNotExpired")
    public ResponseEntity<ResponseDTO<?>> isPasswordValid(@RequestBody IsPasswordValidReqDTO reqDTO) {
        log.debug("[確認登入的使用者所使用的密碼是否還未超過有效期]/api/modadw303w/isPasswordNotExpired");
        StatusCode result = this.ams303wService.isPasswordValid(reqDTO);

        return switch (result) {
            case SUCCESS, LOGIN_IS_PASS_WORD_VALID_RULE_NOT_FOUND, LOGIN_IS_PASS_WORD_VALID_NO_HISTORY_RECORD -> ResponseEntity.ok()
                .body(new ResponseDTO<>(StatusCode.SUCCESS));
            case LOGIN_IS_PASS_WORD_VALID_PASSWORD_EXPIRED -> ResponseEntity.ok()
                .body(new ResponseDTO<>(StatusCode.LOGIN_IS_PASS_WORD_VALID_PASSWORD_EXPIRED));
            default -> ResponseEntity.ok().body(new ResponseDTO<>(result));
        };
    }

    /**
     * 發送 OTP
     * 
     * @param request
     * @return
     */
    @PostMapping("/modadw303w/generateOtp")
    public ResponseEntity<Void> generateOtp(@RequestBody GenerateOtpReqDTO request) {
        log.debug("[發送 OTP]/api/modadw303w/generateOtp");
        ams311wService.generateOtp(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 驗證 OTP
     * 
     * @param request
     * @return
     */
    @PostMapping("/modadw303w/verifyOtp")
    public ResponseEntity<Void> verifyOtp(@RequestBody VerifyOtpReqDTO request) {
        log.debug("[驗證 OTP]/api/modadw303w/generateOtp");
        ams311wService.verifyOtp(request);
        return ResponseEntity.ok().build();
    }

    /**
     * RSA解密
     */
    private String decrypt(String s) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return RSAUtils.privateDecrypt(s, RSAUtils.getPrivateKey(privateKey));
    }
}
