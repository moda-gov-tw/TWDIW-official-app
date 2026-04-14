package gov.moda.dw.manager.service.custom.track;

import gov.moda.dw.manager.config.Constants;
import gov.moda.dw.manager.security.accessToken.AccessTokenFilter;
import gov.moda.dw.manager.service.AccessTokenQueryService;
import gov.moda.dw.manager.service.ApiTrackService;
import gov.moda.dw.manager.service.criteria.AccessTokenCriteria;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.util.*;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CustomApiTrackService extends TrackService {

    @Value("${gov.moda.dw.manager.company.id}")
    private String companyId;

    private final ApiTrackService apiTrackService;

    private final AccessTokenQueryService accessTokenQueryService;

    public CustomApiTrackService(ApiTrackService apiTrackService, AccessTokenQueryService accessTokenQueryService) {
        this.apiTrackService = apiTrackService;
        this.accessTokenQueryService = accessTokenQueryService;
    }

    /**
     * for AOP
     *
     * @param args    HTTP request body
     * @param res     response DTO
     * @param resType API or WEB
     * @param rtt     API round trip time
     * @throws Exception Exception
     */
    public void saveApiTrack(Object[] args, ResponseDTO<?> res, String resType, long rtt) throws Exception {
        HttpServletRequest req = getRequest();
        if (req == null) {
            throw new BadRequestAlertException("HttpServletRequest not found!", "AOP", "HttpServletRequest not found!");
        }

        // 透過uri取得resId。
        String resId = ResUtils.extractResIdFromUri(req.getRequestURI());
        if (resId == null) {
            log.warn("CustomApiTrackService-saveApiTrack，無法解析的request uri:" + req.getRequestURI());
            return;
        }

        String cost = "0";
        String accessToken = StringTrimUtils.limit200Size(req.getHeader(AccessTokenFilter.ACCESS_TOKEN_HEADER));
        String apiType = this.checkTrackResType(resId, accessToken).getCode();

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

        ApiTrackDTO apiTrackDTO = new ApiTrackDTO();

        Map<String, String> map = new HashMap<>();
        Enumeration<?> tmp_names = req.getHeaderNames();
        while (tmp_names.hasMoreElements()) {
            String key = (String) tmp_names.nextElement();
            String value = req.getHeader(key);
            map.put(key, value);
        }

        if (req.getAttribute("FROM") != null) {
            apiTrackDTO.setJhiFrom(req.getAttribute("FROM").toString());
        } else {
            apiTrackDTO.setJhiFrom(Constants.ANONYMOUS_USER);
        }

        apiTrackDTO.setRequestHeader(StringTrimUtils.limit1000Size(JsonUtils.toJson(map)));
        apiTrackDTO.setUuid(UuidUtils.randomWithDateAndId());
        apiTrackDTO.setUri(StringTrimUtils.limit200Size(req.getRequestURI()));
        apiTrackDTO.setUrl(StringTrimUtils.limit200Size(req.getRequestURL().toString()));
        apiTrackDTO.setSource(ipAddress);
        apiTrackDTO.setAccessToken1(accessToken);
        apiTrackDTO.setRequestParam(StringTrimUtils.limit1000Size(JsonUtils.toJson(req.getParameterMap())));
        apiTrackDTO.setRequestBody(StringTrimUtils.limit1000Size(JsonUtils.toJson(args)));
        apiTrackDTO.setRequestMethod(req.getMethod());
        apiTrackDTO.setResponseBody(StringTrimUtils.limit1000Size(JsonUtils.toJson(res)));
        apiTrackDTO.setTimestamp(Instant.now());
        apiTrackDTO.setStatusCode(StringTrimUtils.limit200Size(res.getCode()));
        apiTrackDTO.setRtt(String.valueOf(rtt));
        apiTrackDTO.setServiceId(apiType);
        apiTrackDTO.setJhiTo(companyId);
        apiTrackDTO.setCost(cost);

        apiTrackService.save(apiTrackDTO);
    }

    private ResType checkTrackResType(String resId, String accessToken) {
        // 確認該track是api、web還是內部api。
        if (resId.toLowerCase().contains("w_")) {
            return ResType.WEB;
        } else {
            if (this.checkAccessToken(accessToken)) {
                return ResType.API;
            } else {
                return ResType.INTERNAL_API;
            }
        }
    }

    private boolean checkAccessToken(String accessToken) {
        AccessTokenCriteria criteria = new AccessTokenCriteria();
        criteria.setAccessToken(StringFilterUtils.toEqualStringFilter(accessToken));
        return accessTokenQueryService.countByCriteria(criteria) > 0;
    }
}
