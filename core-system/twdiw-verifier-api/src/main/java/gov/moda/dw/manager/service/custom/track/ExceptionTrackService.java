package gov.moda.dw.manager.service.custom.track;

import gov.moda.dw.manager.config.Constants;
import gov.moda.dw.manager.security.accessToken.AccessTokenFilter;
import gov.moda.dw.manager.security.accessToken.AccessTokenProvider;
import gov.moda.dw.manager.service.ApiTrackService;
import gov.moda.dw.manager.service.criteria.ResCriteria;
import gov.moda.dw.manager.service.custom.CustomResQueryService;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.util.JsonUtils;
import gov.moda.dw.manager.util.StringTrimUtils;
import gov.moda.dw.manager.util.UuidUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExceptionTrackService extends TrackService {

    @Value("apiTrack")
    private String companyId;

    private final ApiTrackService apiTrackService;

    private final CustomResQueryService customResQueryService;

    private final AccessTokenProvider accessTokenProvider;

    public ExceptionTrackService(
            ApiTrackService apiTrackService,
            CustomResQueryService customResQueryService,
            AccessTokenProvider accessTokenProvider) {
        this.apiTrackService = apiTrackService;
        this.customResQueryService = customResQueryService;
        this.accessTokenProvider = accessTokenProvider;
    }

    /**
     * for ExceptionTranslator
     *
     * @param args HTTP request body
     * @param problem HTTP response
     * @param rtt always 0
     * @throws Exception Exception
     */
    public void saveHttpExceptionTrack(String args, ProblemDetailWithCause problem, long rtt) throws Exception {
        log.info("ExceptionTrackService-saveHttpExceptionTrack，儲存ApiTrack，ProblemDetailWithCause:{}", problem);
        HttpServletRequest req = getRequest();
        if (req == null) {
            throw new BadRequestAlertException("HttpServletRequest not found!", "AOP", "HttpServletRequest not found!");
        }

        String serviceId = "others";
        ResCriteria criteria = new ResCriteria();
        List<ResDTO> resDTOList = customResQueryService.findByCriteria(criteria);

        String uri = req.getRequestURI();
        // 判斷uri字串最後一個"/"分隔開始是否是"純數字"或者包含"@"。
        if (uri.matches(".*/(\\d+|.*@.*)$")) {
            uri = uri.substring(0, uri.lastIndexOf("/"));
        }

        if (!resDTOList.isEmpty() && StringUtils.isNotBlank(uri)) {
            for (ResDTO resDTO : resDTOList) {
                if (resDTO.getApiUri().contains(uri)) {
                    serviceId = resDTO.getTypeId();
                    break;
                }
            }
        }

        Map<String, String> map = new HashMap<>();
        Enumeration<?> tmp_names = req.getHeaderNames();
        while (tmp_names.hasMoreElements()) {
            String key = (String) tmp_names.nextElement();
            String value = req.getHeader(key);
            map.put(key, value);
        }

        // 從header抓 source IP。
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

        String accessToken = req.getHeader(AccessTokenFilter.ACCESS_TOKEN_HEADER);

        ApiTrackDTO apiTrackDTO = new ApiTrackDTO();
        apiTrackDTO.setUuid(UuidUtils.randomWithDateAndId());
        apiTrackDTO.setUri(StringTrimUtils.limit200Size(req.getRequestURI()));
        apiTrackDTO.setUrl(StringTrimUtils.limit200Size(req.getRequestURL().toString()));
        apiTrackDTO.setAccessToken1(StringTrimUtils.limit200Size(req.getHeader(AccessTokenFilter.ACCESS_TOKEN_HEADER)));
        apiTrackDTO.setRequestHeader(StringTrimUtils.limit2000Size(JsonUtils.toJson(map)));
        apiTrackDTO.setRequestParam(StringTrimUtils.limit2000Size(JsonUtils.toJson(req.getParameterMap())));
        apiTrackDTO.setRequestBody(StringTrimUtils.limit2000Size(args));
        apiTrackDTO.setRequestMethod(req.getMethod());
        apiTrackDTO.setResponseBody(StringTrimUtils.limit2000Size(JsonUtils.toJson(problem)));
        apiTrackDTO.setTimestamp(Instant.now());
        apiTrackDTO.setStatusCode(StringTrimUtils.limit200Size(String.valueOf(problem.getStatus())));
        apiTrackDTO.setSource(ipAddress);
        apiTrackDTO.setRtt(String.valueOf(rtt));
        apiTrackDTO.setAccessToken1(accessToken);
        apiTrackDTO.setServiceId(serviceId);
        apiTrackDTO.setJhiTo(companyId);
        apiTrackDTO.setCost("0");

        if (ResType.API.getCode().equals(serviceId)) {
            if (req.getAttribute("FROM") != null) {
                apiTrackDTO.setJhiFrom(req.getAttribute("FROM").toString());
            } else {
                apiTrackDTO.setJhiFrom(Constants.ANONYMOUS_USER);
            }
        } else {
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
        }

        apiTrackService.save(apiTrackDTO);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AccessTokenFilter.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
