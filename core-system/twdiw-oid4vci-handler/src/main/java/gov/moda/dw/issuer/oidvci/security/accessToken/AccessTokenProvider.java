package gov.moda.dw.issuer.oidvci.security.accessToken;

import static gov.moda.dw.issuer.oidvci.security.SecurityUtils.AUTHORITIES_KEY;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import gov.moda.dw.issuer.oidvci.security.jwt.CustomJwtDecoder;
import gov.moda.dw.issuer.oidvci.service.AccessTokenQueryService;
import gov.moda.dw.issuer.oidvci.service.criteria.AccessTokenCriteria;
import gov.moda.dw.issuer.oidvci.service.criteria.ResCriteria;
import gov.moda.dw.issuer.oidvci.service.custom.CustomRelQueryService;
import gov.moda.dw.issuer.oidvci.service.custom.CustomResQueryService;
import gov.moda.dw.issuer.oidvci.service.dto.AccessTokenDTO;
import gov.moda.dw.issuer.oidvci.service.dto.RelDTO;
import gov.moda.dw.issuer.oidvci.service.dto.ResDTO;
import gov.moda.dw.issuer.oidvci.type.StateType;
import gov.moda.dw.issuer.oidvci.type.StatusCode;
import gov.moda.dw.issuer.oidvci.util.LongFilterUtils;
import gov.moda.dw.issuer.oidvci.util.StringFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenProvider.class);

    private final AccessTokenQueryService accessTokenQueryService;

    private final CustomRelQueryService customRelQueryService;

    private final CustomResQueryService customResQueryService;

    private final JwtDecoder jwtDecoder;

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String AC_TYPE_UNLIMITED = "unlimited";

    private static SecureRandom secureRandom = new SecureRandom();

    private static final Set<String> logoutList = new LinkedHashSet<>();

    //    private final CustomJwtDecoder customJwtDecoder;

    public AccessTokenProvider(
        AccessTokenQueryService accessTokenQueryService,
        CustomRelQueryService customRelQueryService,
        CustomResQueryService customResQueryService,
        JwtDecoder jwtDecoder
        //        CustomJwtDecoder customJwtDecoder
    ) {
        this.accessTokenQueryService = accessTokenQueryService;
        this.customRelQueryService = customRelQueryService;
        this.customResQueryService = customResQueryService;
        this.jwtDecoder = jwtDecoder;
        //        this.customJwtDecoder = customJwtDecoder;
    }

    /**
     * 產生一組AccessToken
     */
    public String createToken() {
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++) sb.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        return sb.toString();
    }

    /**
     * 將提早登出的jwtToken存入清單
     *
     * @param authToken
     * @return
     */
    public boolean logoutToken(String authToken) {
        try {
            // 登出 (強制逾期)
            logoutList.add(authToken);
            if (logoutList.size() > 50) {
                logoutList.clear();
            }
            log.info("logoutList-size:" + logoutList.size());

            return true;
        } catch (Exception e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
            return false;
        }
    }

    /**
     * 判斷是否被登出(強制逾期)
     *
     * @param authToken
     * @return
     */
    public boolean validateJwtToken(String authToken) {
        //        Jwt res = customJwtDecoder.decode(authToken);
        if (logoutList.contains(authToken)) {
            return true;
        }
        return false;
    }

    public String getAuthenticationByAccessToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException ex) {
            log.error("AccessTokenProvider-getJwtUserName，Failed to decode JWT token: {}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Collection<? extends GrantedAuthority> authorities = Arrays.stream(jwt.getClaimAsString(AUTHORITIES_KEY).split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

            User principal = new User(jwt.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (JwtException ex) {
            log.error("AccessTokenProvider-getAuthentication，Failed to decode JWT token: {}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }
}
