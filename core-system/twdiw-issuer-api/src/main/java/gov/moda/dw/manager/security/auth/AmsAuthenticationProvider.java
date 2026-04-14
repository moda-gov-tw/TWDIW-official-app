package gov.moda.dw.manager.security.auth;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import gov.moda.dw.manager.service.custom.track.TrackService;
import gov.moda.dw.manager.util.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.repository.custom.CustomExtendedUserRepository;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.custom.BwdParamCustomService;
import gov.moda.dw.manager.type.BwdProfileType;
import gov.moda.dw.manager.type.BwdRuleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static gov.moda.dw.manager.security.SecurityUtils.AUTHORITIES_KEY;
import static gov.moda.dw.manager.security.SecurityUtils.JWT_ALGORITHM;

@Slf4j
@Component
public class AmsAuthenticationProvider extends DaoAuthenticationProvider {

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    public static final String JWTUSER_KEY = "jwtuser";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BwdParamCustomService bwdParamCustomService;

    @Autowired
    private CustomExtendedUserRepository customExtendedUserRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    private UserCache userCache = new NullUserCache();
    private boolean forcePrincipalAsString = false;
    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    //   @Override
    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails, AmsUsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(
                this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "帳號或密碼錯誤")
            );
        }
        // ! 取得 密碼規則 中的登入段落 需檢核的
        // * 取得 密碼規則
        List<BwdParam> rules = this.bwdParamCustomService.getBwdRule(BwdProfileType.DEFAULT, true);

        Map<String, BwdParam> ruleIdMap = new HashMap<>();
        for (BwdParam bwdParam : rules) {
            ruleIdMap.put(bwdParam.getRuleId(), bwdParam);
        }

        // * 檢查「前項」 及 取得錯誤訊息
        BwdRuleType ruleType = this.bwdParamCustomService.preRuleCheckForLogin(userDetails.getUsername(), ruleIdMap);
        // * 重新包裝 回應訊息
        if (null != ruleType) {
            String msg;
            log.warn("登入失敗: 帳號={} , 理由={}", userDetails.getUsername(), msg = ruleType.getMsg());

            switch (ruleType) {
                case BwdLocked:
                    msg = "帳號已鎖定";
                    break;
                case BwdLocked_AutoUnlock:
                    msg = "帳號已鎖定，稍後再登入";
                    break;
                default:
                    break;
            }

            throw new BadCredentialsException(msg);
        }

        // validate bwd
        String presentedPassword = authentication.getCredentials().toString();
        ExtendedUser extendedUser = this.getExtendedUser(userDetails.getUsername());
        if ("AP".equalsIgnoreCase(extendedUser.getUserTypeId())) {
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Failed to authenticate since password does not match stored value");

                this.bwdParamCustomService.increaseFailCount(userDetails.getUsername());
                throw new BadCredentialsException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "帳號或密碼錯誤")
                );
            }
        } else {
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword() + authentication.getNonceId())) {
                this.logger.debug("Failed to authenticate since password does not match stored value");

                this.bwdParamCustomService.increaseFailCount(userDetails.getUsername());
                throw new BadCredentialsException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "帳號或密碼錯誤")
                );
            }
        }
        // * 檢查「後項」 及 取得錯誤訊息
        ruleType = this.bwdParamCustomService.postRuleCheckForLogin(userDetails.getUsername(), ruleIdMap);
        // * 重新包裝 回應訊息
        if (null != ruleType) {
            String msg;
            log.warn("登入失敗: 帳號={} , 理由={}", userDetails.getUsername(), msg = ruleType.getMsg());

            switch (ruleType) {
                case BwdMaximumAge:
                    msg = "你的密碼已到期";
                    this.bwdParamCustomService.resetFailCount(userDetails.getUsername(), true);

                    break;
                case ForceToChangeBwd:
                    msg = "請依步驟變更您的密碼";
                    break;
                default:
                    break;
            }

            throw new BadCredentialsException(msg);
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        HttpServletRequest request = getCurrentHttpRequest();
        String clientIp = request != null ? getClientIp(request) : "未知 IP";
        String userAgent = request != null ? getUserAgent(request) : "未知 User-Agent";

        Assert.isInstanceOf(
            AmsUsernamePasswordAuthenticationToken.class,
            authentication,
            () ->
                this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.onlySupports",
                    "Only AmsUsernamePasswordAuthenticationToken is supported"
                )
        );
        String username = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(username);
        if (user == null) {
            cacheWasUsed = false;
            try {
                user = retrieveUser(username, (AmsUsernamePasswordAuthenticationToken) authentication);
            } catch (UsernameNotFoundException ex) {
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Taipei"));
                log.info(
                    "登入失敗(查無帳號), 時間={}, 使用者名稱={}, IP={}, User-Agent={}",
                    now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'XXX")),
                    username,
                    clientIp,
                    userAgent
                );

                this.logger.debug("Failed to find user '" + username + "'");
                if (!this.hideUserNotFoundExceptions) {
                    throw ex;
                }
                throw new BadCredentialsException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "帳號或密碼錯誤")
                );
            }
            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        }
        try {
            this.preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user, (AmsUsernamePasswordAuthenticationToken) authentication);
        } catch (AuthenticationException ex) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Taipei"));
            log.info(
                "登入失敗(密碼錯誤), 時間={}, 使用者名稱={}, IP={}, User-Agent={}",
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'XXX")),
                user.getUsername(),
                clientIp,
                userAgent
            );

            if (!cacheWasUsed) {
                throw ex;
            }
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false;
            user = retrieveUser(username, (AmsUsernamePasswordAuthenticationToken) authentication);
            this.preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user, (AmsUsernamePasswordAuthenticationToken) authentication);
        }
        this.postAuthenticationChecks.check(user);
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Taipei"));
        log.info(
            "登入成功, 時間={}, 使用者名稱={}, IP={}, User-Agent={}",
            now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'XXX")),
            user.getUsername(),
            clientIp,
            userAgent
        );

        // 取得 登入者所屬公司資料
        ExtendedUser extendedUser = this.getExtendedUser(username);

        // 客製jwtToken內容
        JwtUserObject customUser = new JwtUserObject(username, extendedUser.getOrgId());

        // 建立成功驗證身分的 Authentication 物件, 回傳, 以利後續使用.
        Authentication result = createSuccessAuthentication(customUser, authentication, user);
        return result;
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

    public Authentication getAuthentication(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        AmsUsernamePasswordAuthenticationToken authentication = new AmsUsernamePasswordAuthenticationToken(jwt.getSubject(), "", "");
        UserDetails user = retrieveUser(jwt.getSubject(), authentication);

        // 取得 登入者所屬公司資料
        ExtendedUser extendedUser = this.getExtendedUser(user.getUsername());
        JwtUserObject customUser = new JwtUserObject(user.getUsername(), extendedUser.getOrgId());

        return new UsernamePasswordAuthenticationToken(customUser, token, user.getAuthorities());
    }

    public boolean isTokenExpiringSoon(String token, int seconds) {
        Jwt jwt = getClaims(token);
        if (jwt == null) {
            return false;
        }

        Instant expirationTime = jwt.getExpiresAt();
        if (expirationTime == null) {
            return false;
        }

        Instant now = Instant.now();
        long remainingSeconds = expirationTime.getEpochSecond() - now.getEpochSecond();

        log.info("jwt token remaining time: {} seconds ", remainingSeconds + 60);

        if (remainingSeconds + 60 < 0) {    // jwtToken有效期與設定誤差一分鐘
            return false;
        }
        return remainingSeconds <= seconds;
    }

    public Jwt getClaims(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            return null;
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        return TrackService.getRequest();
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {

        private DefaultPreAuthenticationChecks() {
        }

        @Override
        public void check(UserDetails toCheck) {
            // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method 'check'");
        }
    }

    @Service
    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {

        private DefaultPostAuthenticationChecks() {
        }

        @Override
        public void check(UserDetails toCheck) {
            // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method 'check'");
        }
    }

    private ExtendedUser getExtendedUser(String username) {
        Optional<ExtendedUser> extendedUserOpt = customExtendedUserRepository.findOneByUserId(username);
        if (extendedUserOpt.isEmpty()) {
            log.error("AmsAuthenticationProvider-authenticate 找不到該user在extendedUser的資料. username={}", username);
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "缺少資料"));
        }

        ExtendedUser extendedUser = extendedUserOpt.get();
        return extendedUser;
    }
}
