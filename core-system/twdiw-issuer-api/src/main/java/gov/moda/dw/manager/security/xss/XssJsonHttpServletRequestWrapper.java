package gov.moda.dw.manager.security.xss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.owasp.encoder.Encode;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * 專門處理 JSON 請求的 XSS 過濾器包裝器。
 * 會將 body 的 JSON 內容讀出後清理(僅 value)，
 * 再回傳乾淨的 InputStream 給後續流程使用。
 */
public class XssJsonHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] sanitizedBody;

    /**
     * 建構子：從原始請求中讀取 body，清理後轉成 byte array 儲存。
     *
     * @param request 原始 HttpServletRequest
     * @throws IOException 當讀取 stream 發生錯誤
     */
    public XssJsonHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        String body;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }

        // 清理 JSON 內容(只處理 value，保留 key)
        String cleaned = XssJsonSanitizer.sanitizeJson(body);
        this.sanitizedBody = cleaned.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 回傳替代的 InputStream，供後續 Spring 使用。
     */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sanitizedBody);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 非 async 模式下可忽略，必要時實作
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 回傳 JSON body 對應的文字 reader。
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 多值參數處理，但這裡其實與 JSON 無關，可視需求保留。
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
     * 單值參數處理，通常針對非 JSON 格式的表單參數。
     */
    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return encodeForHtml(value);
    }

    /**
     * Header 處理（如 Referer, User-Agent 等）
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return encodeForHtml(value);
    }

    /**
     * 安全的 HTML escape 包裝，避免 null。
     */
    private String encodeForHtml(String input) {
        return input == null ? null : Encode.forHtml(input);
    }

}
