package gov.moda.dw.manager.security.xss;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * XSS 防護過濾器。
 * 根據請求類型決定是否對請求參數或 JSON body 進行 HTML 編碼清理。
 */
@Component
public class XssFilter extends OncePerRequestFilter {

    /**
     * 過濾進入的 HTTP 請求，套用對應的 Request Wrapper 以防範 XSS 攻擊。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 檢查Content-Type是否為JSON
        String contentType = request.getContentType();
        HttpServletRequestWrapper wrappedRequest;

        if (contentType != null && contentType.trim().toLowerCase().startsWith("application/json")) {
            // JSON 請求，攔截並清理 request body 中的 value
            wrappedRequest = new XssJsonHttpServletRequestWrapper(request);
        } else {
            // 非 JSON 請求，清理參數與 header 值
            wrappedRequest = new XssHttpServletRequestWrapper(request);
        }

        // 將包裝過的 request 傳遞給後續 filter/servlet
        filterChain.doFilter(wrappedRequest, response);
    }

}
