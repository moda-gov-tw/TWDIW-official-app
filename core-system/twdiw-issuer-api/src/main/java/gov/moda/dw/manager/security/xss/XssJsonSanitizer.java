package gov.moda.dw.manager.security.xss;

import java.util.LinkedHashMap;
import java.util.Map;

import org.owasp.encoder.Encode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * XSS JSON 清理工具： 
 * 將 JSON 字串中的 value 進行 HTML escape，
 * 防止惡意腳本注入，但保留 key 名稱與 JSON 結構不變。
 */
@Slf4j
public class XssJsonSanitizer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private XssJsonSanitizer() {
        // 禁止實例化
    }

    /**
     * 將傳入的 JSON 字串進行 XSS 清理，只清理 value，保留 key。
     *
     * @param rawJson 原始 JSON 字串
     * @return 清理後的 JSON 字串
     */
    public static String sanitizeJson(String rawJson) {
        try {
            log.debug("rawJson (Before sanitize): {}", rawJson);

            // 將 JSON 字串轉為 Map
            Map<String, Object> map = objectMapper.readValue(rawJson, new TypeReference<>() {
            });
            Map<String, Object> sanitized = sanitizeMap(map);
            String cleanedJson = objectMapper.writeValueAsString(sanitized);
            log.debug("rawJson (After sanitize): {}", cleanedJson);

            return cleanedJson;
        } catch (JsonProcessingException e) {
            // 若解析失敗，回傳原始內容以避免中斷流程
            log.warn("JSON parse failed, returning raw JSON. Error: {}", e.getMessage());
            return rawJson;
        }
    }

    /**
     * 遞迴處理 Map 中每個欄位，僅對 value 做 XSS HTML escape。
     *
     * @param map 原始 Map
     * @return 處理後的新 Map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> sanitizeMap(Map<String, Object> map) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String str) {
                // 對字串進行 HTML escape，防止 XSS
                sanitized.put(key, Encode.forHtml(str));
            } else if (value instanceof Map<?, ?> nestedMap) {
                // 遞迴處理巢狀 Map
                sanitized.put(key, sanitizeMap((Map<String, Object>) nestedMap));
            } else {
                // 其他型別（數值、布林等）不處理
                sanitized.put(key, value);
            }
        }
        return sanitized;
    }

}
