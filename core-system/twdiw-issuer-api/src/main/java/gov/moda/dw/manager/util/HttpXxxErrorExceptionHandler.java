package gov.moda.dw.manager.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.moda.dw.manager.service.dto.BaseHttpResDTO;
import gov.moda.dw.manager.service.dto.Http4xxResDTO;
import gov.moda.dw.manager.service.dto.Http5xxResDTO;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import gov.moda.dw.manager.web.rest.errors.DWException;

public class HttpXxxErrorExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpXxxErrorExceptionHandler.class);


    public static ResponseEntity handleException(Exception ex) {
        if (ex instanceof DWException httpEx) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(toHttp4xxResDTO(httpEx));
        } else if (ex instanceof HttpClientErrorException) {
            HttpClientErrorException httpEx = (HttpClientErrorException) ex;
            HttpStatus statusCode = (HttpStatus) httpEx.getStatusCode();
            return ResponseEntity.status(statusCode).body(toHttp4xxResDTO(httpEx));
        } else if (ex instanceof BadRequestAlertException) {
            BadRequestAlertException httpEx = (BadRequestAlertException) ex;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(toHttp4xxResDTO(httpEx));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(toHttp5xxResDTO(ex));
        }
    }


    public static <T> BaseHttpResDTO<T> toHttp4xxResDTO(DWException ex) {
        log.warn("[toHttp4xxResDTO][DWException]發生:{}", ex.getMessage());

        BaseHttpResDTO<T> dto = new BaseHttpResDTO<>();
        dto.setCode(ex.getCode());
        dto.setMessage(ex.getMsg());
        return dto;
    }

    public static Http4xxResDTO toHttp4xxResDTO(BadRequestAlertException ex) {
        log.warn("[toHttp4xxResDTO][BadRequestAlertException]發生:{}", ex.getMessage());

        // 擷取 title='...' 的內容
        String title = Optional.ofNullable(ex.getMessage())
                .map(msg -> {
                    Matcher matcher = RegexUtils.matchWithTimeout("title='([^']*)'").matcher(msg);
                    if (null != matcher && matcher.find()) {
                        return matcher.group(1);
                    } else {
                        return null;
                    }
                })
                .filter(StringUtils::isNotBlank)
                .orElse(null);

        Http4xxResDTO dto = new Http4xxResDTO();
        dto.setCode("11001");
        dto.setMessage(title);
        return dto;
    }

    public static Http4xxResDTO toHttp4xxResDTO(HttpClientErrorException ex) {

        String responseBody = ex.getResponseBodyAsString();
        Http4xxResDTO dto = new Http4xxResDTO();
        try {
            if(responseBody.contains("resp_code")) {
                dto = toRespCodeMode(responseBody);
            }else{
                dto = toCodeMode(responseBody);
            }

        } catch (Exception parseException) {
            dto.setCode("49999");
            dto.setMessage("發生400的錯，但內部系統發生錯誤，請洽系統管理人員");
            // 記錄 JSON 解析錯誤
            log.error("[HttpClientErrorExceptionHandler] 無法解析 JSON: {}, Exception:{}", responseBody, ExceptionUtils.getStackTrace(parseException));
        }

        return dto;
    }

    private static Http4xxResDTO toRespCodeMode(String responseBody) throws JsonProcessingException {
        Http4xxResDTO dto = new Http4xxResDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        int code = jsonNode.get("resp_code").asInt();
        String message = jsonNode.get("resp_message").asText();
        dto.setCode(String.valueOf(code));
        dto.setMessage(message);
        return dto;
    }

    private static Http4xxResDTO toCodeMode(String responseBody) throws JsonProcessingException {
        Http4xxResDTO dto = new Http4xxResDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        int code = jsonNode.get("code").asInt();
        String message = jsonNode.get("message").asText();
        dto.setCode(String.valueOf(code));
        dto.setMessage(message);
        return dto;
    }

    public static Http5xxResDTO toHttp5xxResDTO(Exception ex) {
        log.error("[HttpClientErrorExceptionHandler] 系統發生非預期的錯誤，Message:{}, Exception:{}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
        Http5xxResDTO dto = new Http5xxResDTO();
        dto.setCode("59999");
        dto.setMessage("內部系統發生錯誤，請洽系統管理人員");
        return dto;
    }

    public static HttpClientErrorException genHttpClientErrorException(HttpStatus httpStatus, String code, String messsage) {
        Http4xxResDTO dto = new Http4xxResDTO();
        dto.setCode(code);
        dto.setMessage(messsage);

        String errorBody = JsonUtils.toJsonNoThrow(dto);

        // 建立 HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 建立一個 404 的 HttpClientErrorException
        HttpClientErrorException ex = HttpClientErrorException.create(
            httpStatus,        // HTTP 狀態碼
            httpStatus.toString(),                 // 狀態訊息（可自定）
            headers,                     // 回傳的 Header
            errorBody.getBytes(),        // 錯誤內容（byte array）
            null                         // 字元編碼（可為 null 預設 UTF-8）
        );
        return ex;

    }

}
