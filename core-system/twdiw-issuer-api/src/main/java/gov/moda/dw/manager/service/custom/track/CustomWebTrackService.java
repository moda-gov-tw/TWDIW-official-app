package gov.moda.dw.manager.service.custom.track;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gov.moda.dw.manager.config.Constants;
import gov.moda.dw.manager.security.accessToken.AccessTokenFilter;
import gov.moda.dw.manager.security.accessToken.AccessTokenProvider;
import gov.moda.dw.manager.service.ApiTrackService;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.dto.custom.WhitelistRequestDto;
import gov.moda.dw.manager.util.JsonUtils;
import gov.moda.dw.manager.util.StringTrimUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class CustomWebTrackService extends TrackService {

    // 加密 RequestBody、RequestParam 白名單
    private static final List<WhitelistRequestDto> ENCRYPT_REQUEST_WHITELIST = List.of(
            new WhitelistRequestDto("POST", "/api/modadw303w/authenticate"),    // 登入
            new WhitelistRequestDto("POST", "/api/modadw303w/generateOtp"),     // 發送 OTP
            new WhitelistRequestDto("POST", "/api/modadw303w/verifyOtp"),       // 驗證 OTP
            new WhitelistRequestDto("POST", "/api/modadw311w"),                 // 帳號異動相關(POST)
            new WhitelistRequestDto("GET", "/api/modadw311w"),                  // 帳號查詢相關(GET)
            new WhitelistRequestDto("POST", "/api/modadw301w/reset-bwd/init"),  // 重置密碼
            new WhitelistRequestDto("POST", "/api/qrcode/data"),                // 產生 QR Code(API)
            new WhitelistRequestDto("POST", "/api/vc-item-data")                // 產生 QR Code(WEB)
        );

    // 加密 ResponseBody 白名單
    private static final List<WhitelistRequestDto> ENCRYPT_RESPONSE_BODY_WHITELIST = List.of(
            new WhitelistRequestDto("POST", "/api/modadw311w"),                          // 帳號異動相關(POST)
            new WhitelistRequestDto("GET", "/api/modadw311w"),                           // 帳號查詢相關(GET)
            new WhitelistRequestDto("POST", "/modadw302w/reset-bwd/validate/resetKey"),  // 驗證重置 key
            new WhitelistRequestDto("POST", "/api/vc-item-data"),                        // 產生 QR Code(WEB)
            new WhitelistRequestDto("GET", "/api/vc-item-data/")                         // VC 模板資料查詢相關(WEB)
        );

    @Value("${gov.moda.dw.manager.company.id}")
    private String companyId;

    @Value("${attributeencryptor.key}")
    private String attributeKey;

    private final ApiTrackService apiTrackService;

    private final AccessTokenProvider accessTokenProvider;

    public CustomWebTrackService(ApiTrackService apiTrackService, AccessTokenProvider accessTokenProvider) {
        this.apiTrackService = apiTrackService;
        this.accessTokenProvider = accessTokenProvider;
    }

    public void saveWebTrack(Object[] args, Object res, String sysId, long rtt) {
        try {
            HttpServletRequest req = getRequest();
            if (req == null) {
                throw new BadRequestAlertException("HttpServletRequest not found!", "AOP", "HttpServletRequest not found!");
            }
    
            // 從header抓source IP。
            String ipAddress = req.getHeader("X-FORWARDED-FOR");
            if (StringUtils.isBlank(ipAddress)) {
                ipAddress = req.getHeader("X-Real-IP");
            }
            if (StringUtils.isBlank(ipAddress)) {
                ipAddress = req.getRemoteAddr();
            }
            if (ipAddress != null && ipAddress.contains(",")) {
                ipAddress = ipAddress.split(",")[0];
            }
    
            String cost = "0";
    
            Map<String, String> map = new HashMap<>();
            Enumeration<?> tmp_names = req.getHeaderNames();
            while (tmp_names.hasMoreElements()) {
                String key = (String) tmp_names.nextElement();
                String value = req.getHeader(key);
                map.put(key, value);
            }
            String reqBody = null;
            String reqParam = null;
            // 如在 ENCRYPT_REQUEST_WHITELIST 白名單內無需存 RequestBody、RequestParam
            boolean isReqWhitelisted = ENCRYPT_REQUEST_WHITELIST.stream()
                    .anyMatch(request -> StringUtils.equalsIgnoreCase(request.getMethod(), req.getMethod())
                            && StringUtils.startsWith(req.getRequestURI(), request.getUri()));
    
            if (args != null) {
                for (Object obj : args) {
                    if (obj == null || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse) {} else {
                        if (
                            obj instanceof ArrayList &&
                            ((ArrayList<?>) obj).get(0) != null &&
                            ((ArrayList<?>) obj).get(0) instanceof MultipartFile
                        ) {
                            break;
                        }
    
                        if (!isReqWhitelisted) {
                            reqBody = JsonUtils.toJsonNoThrows((obj));
                            reqParam = JsonUtils.toJson(req.getParameterMap());
                        }
    
                        break;
                    }
                }
            }
    
            String resJsonString = null;
            String resHeader = null;
            String resBody = null;
            String resstatusCodeValue = null;
            // 如在 ENCRYPT_RESPONSE_BODY_WHITELIST 白名單內無需存 ResponseBody
            boolean isResWhitelisted = ENCRYPT_RESPONSE_BODY_WHITELIST.stream()
                    .anyMatch(request -> StringUtils.equalsIgnoreCase(request.getMethod(), req.getMethod())
                            && StringUtils.startsWith(req.getRequestURI(), request.getUri()));

            if (res instanceof ResponseEntity) {
                resJsonString = JsonUtils.toJsonNoThrows(res);
                resHeader = this.getResponseDataByKey(resJsonString, "headers");
                if (!isResWhitelisted) resBody = this.getResponseDataByKey(resJsonString, "body");
                resstatusCodeValue = this.getResponseDataByKey(resJsonString, "statusCodeValue");
            } else {
                if (!isResWhitelisted) resBody = JsonUtils.toJsonNoThrows(res);
            }
    
            ApiTrackDTO apiTrackDTO = new ApiTrackDTO();
    
            String jwt = this.resolveToken(req);
            String from = null;
            if (jwt != null) {
                from = this.accessTokenProvider.getAuthenticationByAccessToken(jwt);
            }
            if (StringUtils.isNotBlank(jwt) && StringUtils.isNotBlank(from) && !this.accessTokenProvider.validateJwtToken(jwt)) {
                apiTrackDTO.setJhiFrom(from);
            } else {
                apiTrackDTO.setJhiFrom(Constants.ANONYMOUS_USER);
            }
    
            apiTrackDTO.setRequestHeader(StringTrimUtils.limit1000Size(JsonUtils.toJson(map)));
            apiTrackDTO.setUuid(String.valueOf(UUID.randomUUID()));
            apiTrackDTO.setUri(StringTrimUtils.limit1000Size(req.getRequestURI()));
            apiTrackDTO.setUrl(StringTrimUtils.limit1000Size(req.getRequestURL().toString()));
            apiTrackDTO.setAccessToken1(req.getHeader(AccessTokenFilter.ACCESS_TOKEN_HEADER));
            apiTrackDTO.setSource(ipAddress);
            apiTrackDTO.setRequestParam(reqParam);
            apiTrackDTO.setRequestBody(reqBody);
            apiTrackDTO.setRequestMethod(req.getMethod());
            apiTrackDTO.setResponseBody(resBody);
            apiTrackDTO.setResponseHeader(JsonUtils.toJsonNoThrows(resHeader));
            apiTrackDTO.setTimestamp(Instant.now());
            apiTrackDTO.setStatusCode(resstatusCodeValue);
            apiTrackDTO.setServiceId(sysId);
            apiTrackDTO.setRtt(String.valueOf(rtt));
            apiTrackDTO.setCost(cost);
            apiTrackDTO.setJhiTo(companyId);
    
            apiTrackService.save(apiTrackDTO);
        } catch (Exception e) {
            log.error("CustomWebTrackService-saveWebTrack，發生異常:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    private String getResponseDataByKey(String resJsonString, String key) {
        Object resObject = null;
        try {
            resObject = JsonUtils.getObjectByKey(Object.class, resJsonString, key);
            if (resObject != null) {
                return JsonUtils.toJsonNoThrows(resObject);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AccessTokenFilter.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
