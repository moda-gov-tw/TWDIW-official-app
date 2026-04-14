package gov.moda.dw.manager.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.service.dto.custom.BaseHttpResDTO;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.DWException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpErrorExceptionHandler {

    private HttpErrorExceptionHandler() {
    }

    /**
     * 讓呼叫端決定回傳型別 R
     * 
     * @param <T>
     * @param <R>
     * @param ex
     * @param returnType
     * @return
     */
    public static <T, R> ResponseEntity<R> handleException(Exception ex, Class<R> returnType) {
        BaseHttpResDTO<T> base = convertToBaseHttpResDTO(ex);
        Object body;

        // 呼叫端要求完整 Base 格式
        if (BaseHttpResDTO.class.isAssignableFrom(returnType)) {
            body = base;
        }
        // 呼叫端要求 payload → 目前沒有 payload → 回 null
        else if (Object.class.equals(returnType)) {
            body = null;
        }
        // 預設回傳 BaseHttpResDTO 格式
        else {
            body = base;
        }

        return (ResponseEntity<R>) ResponseEntity.status(getStatus(ex)).body(body);
    }

    private static HttpStatus getStatus(Exception ex) {
        if (ex instanceof HttpClientErrorException httpEx) {
            return (HttpStatus) httpEx.getStatusCode();
        }
        if (ex instanceof DWException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof BadRequestAlertException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static <T> BaseHttpResDTO<T> convertToBaseHttpResDTO(Exception ex) {

        if (ex instanceof DWException httpEx) {
            return toHttp4xxResDTO(httpEx);
        } else if (ex instanceof HttpClientErrorException httpEx) {
            return toHttp4xxResDTO(httpEx);
        } else if (ex instanceof BadRequestAlertException httpEx) {
            return toHttp4xxResDTO(httpEx);
        }
        return toHttp5xxResDTO(ex);
    }

    public static <T> BaseHttpResDTO<T> toHttp4xxResDTO(DWException ex) {
        log.warn("[toHttp4xxResDTO][DWException]發生:{}", ex.getMessage());

        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        dto.setCode(ex.getCode());
        dto.setMessage(ex.getMsg());
        return dto;
    }

    public static <T> BaseHttpResDTO<T> toHttp4xxResDTO(BadRequestAlertException ex) {
        log.warn("[toHttp4xxResDTO][BadRequestAlertException]發生:{}", ex.getMessage());

        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        dto.setCode("11001");
        dto.setMessage(ex.getErrorKey());
        return dto;
    }

    public static <T> BaseHttpResDTO<T> toHttp4xxResDTO(HttpClientErrorException ex) {
        String responseBody = ex.getResponseBodyAsString();
        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        try {
            if (responseBody.contains("resp_code")) {
                dto = toRespCodeMode(responseBody);
            } else {
                dto = toCodeMode(responseBody);
            }

        } catch (Exception parseException) {
            dto.setCode("49999");
            dto.setMessage("發生400的錯，但內部系統發生錯誤，請洽系統管理人員");
            // 記錄 JSON 解析錯誤
            log.error("[HttpClientErrorExceptionHandler] 無法解析 JSON: {}, Exception:{}", responseBody,
                    ExceptionUtils.getStackTrace(parseException));
        }

        return dto;
    }

    private static <T> BaseHttpResDTO<T> toRespCodeMode(String responseBody) throws JsonProcessingException {
        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        int code = jsonNode.get("resp_code").asInt();
        String message = jsonNode.get("resp_message").asText();
        dto.setCode(String.valueOf(code));
        dto.setMessage(message);
        return dto;
    }

    private static <T> BaseHttpResDTO<T> toCodeMode(String responseBody) throws JsonProcessingException {
        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        int code = jsonNode.get("code").asInt();
        String message = jsonNode.get("message").asText();
        dto.setCode(String.valueOf(code));
        dto.setMessage(message);
        return dto;
    }

    public static <T> BaseHttpResDTO<T> toHttp5xxResDTO(Exception ex) {
        log.error("[HttpClientErrorExceptionHandler] 系統發生非預期的錯誤，Message:{}, Exception:{}", ex.getMessage(),
                ExceptionUtils.getStackTrace(ex));
        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        dto.setCode("59999");
        dto.setMessage("內部系統發生錯誤，請洽系統管理人員");
        return dto;
    }

    public static <T> HttpClientErrorException genHttpClientErrorException(HttpStatus httpStatus, String code,
            String messsage) {
        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        dto.setCode(code);
        dto.setMessage(messsage);

        String errorBody = JsonUtils.toJsonNoThrow(dto);

        // 建立 HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 建立一個 404 的 HttpClientErrorException
        return HttpClientErrorException.create(httpStatus, // HTTP 狀態碼
                httpStatus.toString(), // 狀態訊息（可自定）
                headers, // 回傳的 Header
                errorBody.getBytes(), // 錯誤內容（byte array）
                null // 字元編碼（可為 null 預設 UTF-8）
        );
    }

}
