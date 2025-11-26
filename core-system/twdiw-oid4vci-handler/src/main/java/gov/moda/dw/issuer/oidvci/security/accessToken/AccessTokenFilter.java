package gov.moda.dw.issuer.oidvci.security.accessToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import gov.moda.dw.issuer.oidvci.service.dto.custom.ResponseDTO;
import gov.moda.dw.issuer.oidvci.type.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

public class AccessTokenFilter extends GenericFilterBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AccessTokenProvider accessTokenProvider;

    public static final String ACCESS_TOKEN_HEADER = "Access-Token";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String HEADER_IP_ADDRESS = "X-FORWARDED-FOR";

    public AccessTokenFilter(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        log.debug("進入doFilter。");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        // 把request存到attribute裡面，以利各方取得。
        servletRequest = new AmsHttpServletRequestWrapper((HttpServletRequest) servletRequest);

        // 取得AccessToken後取得來源IP位置。
        String sourceIp = "";

        // 確認這次請求頭中是否包含AccessToken，若沒有包含，直接進入下一個過濾器。
        String accessToken = httpServletRequest.getHeader(ACCESS_TOKEN_HEADER);
        boolean isTokenExisted = StringUtils.hasText(accessToken);
        if (!isTokenExisted) {
            String jwt = resolveToken(httpServletRequest);

            //若request的JwtToken已被強制登出，回覆Http 401錯誤。
            if (StringUtils.hasText(jwt) && accessTokenProvider.validateJwtToken(jwt)) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.setContentType("application/json");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.getWriter().println(new ResponseDTO<>(StatusCode.SID_ILLEGAL));
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            sourceIp = this.getSourceIp(httpServletRequest);
            log.debug("AccessTokenFilter-doFilter，取得此次請求中的AccessToken，請求IP位置:{}，accessToken(masked):{}", sourceIp, maskToken(accessToken));

            // 驗證AccessToken有效性。
            String resMsg = "";
//            StatusCode accessTokenValidated = this.accessTokenProvider.validateToken(accessToken);

//            if (StatusCode.SUCCESS.getCode().equals(accessTokenValidated.getCode())) {
//                Authentication authentication = this.accessTokenProvider.getAuthenticationByAccessToken(accessToken, sourceIp);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                servletRequest.setAttribute("FROM", ((AccessTokenUserObject) authentication.getPrincipal()).getOrgId());
//                servletRequest.setAttribute("EXT", true);
//
//                log.info(
//                    "AccessTokenFilter-doFilter，AccessToken驗證成功，authentication:{}，權限List:{}",
//                    authentication,
//                    authentication.getAuthorities().toString()
//                );
//                // AccessTokenFilter結束，進入下一個攔截器。
//                filterChain.doFilter(servletRequest, servletResponse);
//            } else {
//                resMsg = accessTokenValidated.getMsg();
//            }
            if (!resMsg.isEmpty()) {
                servletResponse.setContentType("application/json;charset=UTF-8");
                PrintWriter out = servletResponse.getWriter();
                out.println("{\"response\":{\"message\":\"" + resMsg + "\"}}");
            } else {
                // 尚未啟用 AccessToken 驗證，先讓請求繼續
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    /**
     * 取得請求來源IP位置。
     */
    private String getSourceIp(HttpServletRequest request) {
        // 從請求頭中獲取來源IP位置，若無法取得，則直接取用請求來源IP位置。
        String ipAddress = request.getHeader(HEADER_IP_ADDRESS);
        if (!StringUtils.hasText(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        log.debug("獲取IP位置，ipAddress:{}", ipAddress);
        return ipAddress;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String maskToken(String token) {
        if (!StringUtils.hasText(token) || token.length() < 8) {
            return "****";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}
