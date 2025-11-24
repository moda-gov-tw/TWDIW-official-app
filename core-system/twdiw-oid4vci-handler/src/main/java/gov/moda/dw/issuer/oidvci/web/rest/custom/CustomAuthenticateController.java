package gov.moda.dw.issuer.oidvci.web.rest.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.issuer.oidvci.domain.ExtendedUser;
import gov.moda.dw.issuer.oidvci.repository.ExtendedUserRepository;
import gov.moda.dw.issuer.oidvci.security.accessToken.AccessTokenProvider;
import gov.moda.dw.issuer.oidvci.security.auth.AmsUsernamePasswordAuthenticationToken;
import gov.moda.dw.issuer.oidvci.security.jwt.JwtUserObject;
import gov.moda.dw.issuer.oidvci.service.dto.ExtendedUserDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.ExtendedUserMapper;
import gov.moda.dw.issuer.oidvci.util.JsonUtils;
import gov.moda.dw.issuer.oidvci.web.rest.vm.LoginVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.moda.dw.issuer.oidvci.security.SecurityUtils.AUTHORITIES_KEY;
import static gov.moda.dw.issuer.oidvci.security.SecurityUtils.JWT_ALGORITHM;

/**
 * Controller to authenticate users.
 */
@Slf4j
@RestController
@RequestMapping("${app.base.path:/api}")
public class CustomAuthenticateController {

    private final JwtEncoder jwtEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AccessTokenProvider accessTokenProvider;

    public static final String JWTUSER_KEY = "jwtuser";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private ExtendedUserRepository extendedUserRepository;

    @Autowired
    private ExtendedUserMapper extendedUserMapper;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    @Value("${ap.account.username}")
    private String account;

    @Value("${ap.account.password}")
    private String password;


    public CustomAuthenticateController(
        JwtEncoder jwtEncoder,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        AccessTokenProvider accessTokenProvider
    ) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.accessTokenProvider = accessTokenProvider;
    }

    @PostMapping("/modadw303w/authenticate")
    public ResponseEntity<JWTToken> authorize() {
        UsernamePasswordAuthenticationToken authenticationToken = null;

        LoginVM loginVM = new LoginVM();
        loginVM.setUsername(account);
        loginVM.setPassword(password);

        // 依據帳號搜尋 Extended User table 資料
        ExtendedUser extendedUser = extendedUserRepository.findOneByUserId(loginVM.getUsername())
            .orElseThrow(() -> {
                log.error("找不到該 user 在 ExtendedUser 表中的資料. username={}", loginVM.getUsername());
                return new BadCredentialsException("帳號或密碼錯誤");
            });

        log.info("找到的 extendedUser: {}", extendedUser);

        ExtendedUserDTO extendedUserDTO = extendedUserMapper.toDto(extendedUser);

        // User Type Id 是 AP，不驗 nonce Captcha
        if ("AP".equalsIgnoreCase(extendedUserDTO.getUserTypeId())) {
            // 封裝未驗證的 UsernamePasswordAuthenticationToken
            authenticationToken = new AmsUsernamePasswordAuthenticationToken(
                loginVM.getUsername(),
                loginVM.getPassword(),
                ""
            );
        }

        //進行身分驗證 及 製作客製化的jwtToken內容
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //身分驗證成功後將訊息放入 SecurityContext 儲存, 以利後續權限檢查或是確保訊息在整個請求處理過程中都可以被訪問。
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = this.createToken(authentication, false);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/modadw303w/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        JwtUserObject jwtUserObject = (JwtUserObject) authentication.getPrincipal();
        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(jwtUserObject.getUserId())
            .claim(AUTHORITIES_KEY, authorities)
            .claim(JWTUSER_KEY, JsonUtils.toJsonNoThrows(authentication.getPrincipal()))
            .expiresAt(validity)
            .build();

    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  /**
   * Object to return as body in JWT Authentication.
   */
  static class JWTToken {

    private String idToken;

    JWTToken(String idToken) {
      this.idToken = idToken;
    }

    @JsonProperty("id_token")
    String getIdToken() {
      return idToken;
    }

    void setIdToken(String idToken) {
      this.idToken = idToken;
    }
  }
}
