package gov.moda.dw.issuer.oidvci.service.custom.track;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.issuer.oidvci.security.accessToken.AccessTokenFilter;
import gov.moda.dw.issuer.oidvci.service.ApiTrackService;
import gov.moda.dw.issuer.oidvci.service.criteria.ResCriteria;
import gov.moda.dw.issuer.oidvci.service.custom.CustomResQueryService;
import gov.moda.dw.issuer.oidvci.service.dto.ApiTrackDTO;
import gov.moda.dw.issuer.oidvci.service.dto.ResDTO;
import gov.moda.dw.issuer.oidvci.util.JsonUtils;
import gov.moda.dw.issuer.oidvci.util.StringFilterUtils;
import gov.moda.dw.issuer.oidvci.util.StringTrimUtils;
import gov.moda.dw.issuer.oidvci.util.UuidUtils;
import gov.moda.dw.issuer.oidvci.web.rest.errors.BadRequestAlertException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

@Slf4j
@Service
public class ExceptionTrackService extends TrackService {

    @Value("apiTrack")
    private String companyId;

    private final ApiTrackService apiTrackService;

    private final CustomResQueryService customResQueryService;

    public ExceptionTrackService(ApiTrackService apiTrackService, CustomResQueryService customResQueryService) {
        this.apiTrackService = apiTrackService;
        this.customResQueryService = customResQueryService;
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
        criteria.setApiUri(StringFilterUtils.toEqualStringFilter(req.getRequestURI()));
        List<ResDTO> resDTOList = customResQueryService.findByCriteria(criteria);
        // 有找到唯一且對應的res。
        if (resDTOList.size() == 1) {
            serviceId = resDTOList.get(0).getTypeId();
        }

        ApiTrackDTO apiTrackDTO = new ApiTrackDTO();
        apiTrackDTO.setUuid(UuidUtils.randomWithDateAndId());
        apiTrackDTO.setUri(StringTrimUtils.limit200Size(req.getRequestURI()));
        apiTrackDTO.setUrl(StringTrimUtils.limit200Size(req.getRequestURL().toString()));

        apiTrackDTO.setAccessToken1(StringTrimUtils.limit200Size(req.getHeader(AccessTokenFilter.ACCESS_TOKEN_HEADER)));

        Map<String, String> map = new HashMap<>();

        Enumeration<?> tmp_names = req.getHeaderNames();
        while (tmp_names.hasMoreElements()) {
            String key = (String) tmp_names.nextElement();
            String value = req.getHeader(key);
            map.put(key, value);
        }
        apiTrackDTO.setRequestHeader(StringTrimUtils.limit2000Size(JsonUtils.toJson(map)));
        apiTrackDTO.setRequestParam(StringTrimUtils.limit2000Size(JsonUtils.toJson(req.getParameterMap())));
        apiTrackDTO.setRequestBody(StringTrimUtils.limit2000Size(args));
        apiTrackDTO.setRequestMethod(req.getMethod());
        apiTrackDTO.setResponseBody(StringTrimUtils.limit2000Size(JsonUtils.toJson(problem)));
        //        apiTrackDTO.setResponseHeader();
        apiTrackDTO.setTimestamp(Instant.now());
        apiTrackDTO.setStatusCode(StringTrimUtils.limit200Size(String.valueOf(problem.getStatus())));
        // 從header抓 source IP。
        String ipAddress = req.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || "".equals(ipAddress)) {
            ipAddress = req.getRemoteAddr();
        }
        apiTrackDTO.setSource(ipAddress);
        apiTrackDTO.setRtt(String.valueOf(rtt));

        String accessToken = req.getHeader(AccessTokenFilter.ACCESS_TOKEN_HEADER);
        apiTrackDTO.setAccessToken1(accessToken);

        if (req.getAttribute("FROM") != null) {
            apiTrackDTO.setJhiFrom(req.getAttribute("FROM").toString());
        }
        apiTrackDTO.setServiceId(serviceId);
        apiTrackDTO.setJhiTo(companyId);
        apiTrackDTO.setCost("0");
        apiTrackService.save(apiTrackDTO);
    }
}
