package gov.moda.dw.manager.security.xss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.owasp.encoder.Encode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 用於清理 URL 參數與 Header 中潛在 XSS 的 HttpServletRequest 包裝器。
 * 適用於 content-type 為 application/x-www-form-urlencoded 或 query string。
 */
@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 清理多值參數陣列中的每一項。
     */
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return new String[0];
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = encodeForHtml(values[i]);
        }
        return encodedValues;
    }

    /**
     * 清理單一參數值。
     */
    @Override
    public String getParameter(String parameter) {
        log.debug("Intercepting parameter: {}", parameter);
        String value = super.getParameter(parameter);
        return encodeForHtml(value);
    }

    /**
     * 清理單一 Header 值。
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return encodeForHtml(value);
    }

    /**
     * 清理多個 Header 值。
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);
        List<String> sanitizedList = new ArrayList<>();
        while (headers.hasMoreElements()) {
            sanitizedList.add(encodeForHtml(headers.nextElement()));
        }
        return Collections.enumeration(sanitizedList);
    }

    /**
     * 封裝 HTML escape 處理，避免 null。
     */
    private String encodeForHtml(String input) {
        return input == null ? null : Encode.forHtml(input);
    }

}
