package gov.moda.dw.manager.security.accessToken;

import static gov.moda.dw.manager.security.SecurityUtils.AUTHORITIES_KEY;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import gov.moda.dw.manager.security.jwt.CustomJwtDecoder;
import gov.moda.dw.manager.service.AccessTokenQueryService;
import gov.moda.dw.manager.service.criteria.AccessTokenCriteria;
import gov.moda.dw.manager.service.criteria.ResCriteria;
import gov.moda.dw.manager.service.custom.CustomRelQueryService;
import gov.moda.dw.manager.service.custom.CustomResQueryService;
import gov.moda.dw.manager.service.dto.AccessTokenDTO;
import gov.moda.dw.manager.service.dto.RelDTO;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.type.StateType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.LongFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
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

    // 警告：不應在程式碼中硬編碼任何 token 或密鑰！
    // 若需要內部測試用 token，請透過環境變數或設定檔案提供
    // public static final String INNER_ACCESS_TOKEN = "移除以避免安全風險";

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
     * 確認AccessToken有效性。
     */
    public StatusCode validateToken(String accessToken) {
        AccessTokenDTO accessTokenDTO = getAccessTokenDetail(accessToken);
        if (accessTokenDTO == null) {
            log.warn("AccessTokenProvider-validateToken，AccessToken不存在，accessToken:{}", accessToken);
            return StatusCode.TOKEN_NOT_EXISTS_FOR_FILTER;
        }
        if (!AC_TYPE_UNLIMITED.equals(accessTokenDTO.getActype())) {
            log.warn("AccessTokenProvider-validateToken，AccessToken限期異常，accessTokenDTO:{}", accessTokenDTO);
            return StatusCode.TOKEN_AC_TYPE_EXCEPTION;
        }
        if (StateType.DISABLED.getEnName().equals(accessTokenDTO.getState())) {
            log.warn("AccessTokenProvider-validateToken，AccessToken未啟用，accessTokenDTO:{}", accessTokenDTO);
            return StatusCode.TOKEN_DISABLED;
        }

        log.debug("Access-Token {} is {}", accessToken, accessTokenDTO.getState());
        return StatusCode.SUCCESS;
    }

    /**
     * 取得AccessTokenDTO。
     */
    public AccessTokenDTO getAccessTokenDetail(String accessToken) {
        AccessTokenCriteria criteria = new AccessTokenCriteria();
        criteria.setAccessToken(StringFilterUtils.toEqualStringFilter(accessToken));
        List<AccessTokenDTO> accessTokenDTOList = accessTokenQueryService.findByCriteria(criteria);
        if (!accessTokenDTOList.isEmpty()) {
            return accessTokenDTOList.get(0);
        }
        return null;
    }

    /**
     * 取得AccessToken權限清單。
     */
    public Authentication getAuthenticationByAccessToken(String accessToken, String sourceIp) {
        List<RelDTO> relDTOList = this.customRelQueryService.findRelDTOListForAccessTokenStr(accessToken);
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 取得此AccessToken(已啟用)的Role再取得Role的權限清單
        AccessTokenDTO accessTokenDto = this.getAccessTokenDetail(accessToken);
        if (null != accessTokenDto && StringUtils.equals(StateType.ENABLED.getCode(), accessTokenDto.getState())) {
            List<RelDTO> relUserRoleList = this.customRelQueryService.findByLeftCodeGetRole(accessTokenDto.getOwner());
            for (RelDTO relUserRole : relUserRoleList) {
                List<RelDTO> relRoleResList = this.customRelQueryService.findByLeftCode(relUserRole.getRightCode(),
                        StateType.ENABLED.getCode());
                relDTOList.addAll(relRoleResList);
            }
        }

        for (RelDTO relDTO : relDTOList) {
            ResCriteria resCriteria = new ResCriteria();
            resCriteria.setId(LongFilterUtils.toEqualLongFilter(relDTO.getRightId()));
            List<ResDTO> resDTOList = this.customResQueryService.findByCriteria(resCriteria);
            if (!resDTOList.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(resDTOList.get(0).getResId()));
            }
        }

        AccessTokenDTO accessTokenDTO = getAccessTokenDetail(accessToken);
        AccessTokenUserObject accessTokenUserObject = new AccessTokenUserObject();
        BeanUtils.copyProperties(accessTokenDTO, accessTokenUserObject);
        accessTokenUserObject.setLoginIp(sourceIp);
        // 返回身份驗證信息。
        return new UsernamePasswordAuthenticationToken(accessTokenUserObject, accessToken, authorities);
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
            log.warn("AccessTokenProvider-getJwtUserName，Failed to decode JWT token: {}", ExceptionUtils.getStackTrace(ex));
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
