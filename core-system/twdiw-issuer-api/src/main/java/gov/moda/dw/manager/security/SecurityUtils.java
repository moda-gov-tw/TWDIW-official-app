package gov.moda.dw.manager.security;

import static gov.moda.dw.manager.web.rest.custom.CustomAuthenticateController.JWTUSER_KEY;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import gov.moda.dw.manager.security.accessToken.AccessTokenUserObject;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.util.JsonUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Utility class for Spring Security.
 */
@Slf4j
public final class SecurityUtils {

  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

  public static final String AUTHORITIES_KEY = "auth";

  private SecurityUtils() {}

  /**
   * Get the login of the current user.
   *
   * @return the login of the current user.
   */
  public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
  }

  private static String extractPrincipal(Authentication authentication) {
    if (authentication == null) {
      return null;
    } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
      return springSecurityUser.getUsername();
    } else if (authentication.getPrincipal() instanceof Jwt jwt) {
      return jwt.getSubject();
    } else if (authentication.getPrincipal() instanceof String s) {
      return s;
    } else if (authentication.getPrincipal() instanceof AccessTokenUserObject accessTokenUserObject) {
      return accessTokenUserObject.getOwner();
    }
    return null;
  }

  /**
   * Get the JWT of the current user.
   *
   * @return the JWT of the current user.
   */
  public static Optional<String> getCurrentUserJWT() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
      .filter(authentication -> authentication.getCredentials() instanceof String)
      .map(authentication -> (String) authentication.getCredentials());
  }

  /**
   * 取得當前登入者的 JWT tokenValue
   * @return
   */
  public static Optional<String> getCurrentUserJWT2() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication == null) {
      return Optional.empty();
    } else {
      Jwt credentials = (Jwt) authentication.getCredentials();
      return Optional.ofNullable(credentials.getTokenValue());
    }
  }

  /**
   * 取得登入的使用者物件
   * @return
   */
  public static List<JwtUserObject> getJwtUserObject() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();

    if (authentication == null) {
      log.info("JwtUserObject authentication == null");
      return null;
    } else if (authentication.getPrincipal() instanceof Jwt jwt) {
      List<JwtUserObject> jwtUserObject = JsonUtils.toObjects(JwtUserObject.class, jwt.getClaim(JWTUSER_KEY));
      return jwtUserObject;
    } else if (authentication.getPrincipal() instanceof JwtUserObject) {
      List<JwtUserObject> jwtUserObject = List.of((JwtUserObject) authentication.getPrincipal());
      return jwtUserObject;
    } else if (authentication.getPrincipal() instanceof AccessTokenUserObject) {
        AccessTokenUserObject accessTokenUserObject = (AccessTokenUserObject) authentication.getPrincipal();
        JwtUserObject jwtUserObject = new JwtUserObject(accessTokenUserObject.getOwner(), accessTokenUserObject.getOrgId());
        return List.of(jwtUserObject);
    }

    log.debug("authentication.getPrincipal() instanceof Jwt FAIL");

    return null;
  }

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise.
   */
  public static boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
  }

  /**
   * Checks if the current user has any of the authorities.
   *
   * @param authorities the authorities to check.
   * @return true if the current user has any of the authorities, false otherwise.
   */
  public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority)));
  }

  /**
   * Checks if the current user has none of the authorities.
   *
   * @param authorities the authorities to check.
   * @return true if the current user has none of the authorities, false otherwise.
   */
  public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
    return !hasCurrentUserAnyOfAuthorities(authorities);
  }

  /**
   * Checks if the current user has a specific authority.
   *
   * @param authority the authority to check.
   * @return true if the current user has the authority, false otherwise.
   */
  public static boolean hasCurrentUserThisAuthority(String authority) {
    return hasCurrentUserAnyOfAuthorities(authority);
  }

  private static Stream<String> getAuthorities(Authentication authentication) {
    return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
  }
}
