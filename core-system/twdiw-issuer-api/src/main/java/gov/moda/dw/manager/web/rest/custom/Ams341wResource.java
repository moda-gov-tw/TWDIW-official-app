package gov.moda.dw.manager.web.rest.custom;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.domain.ApiTrack;
import gov.moda.dw.manager.service.custom.Ams341wService;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.dto.custom.Ams341wApiTrackResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams341wReqDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;

/**
 * REST controller for managing {@link ApiTrack}.
 */
@RestController
@RequestMapping("/api")
public class Ams341wResource {


    private final Logger log = LoggerFactory.getLogger(Ams341wResource.class);

    private final Ams341wService ams341wService;

    public Ams341wResource(Ams341wService ams341wService) {
        this.ams341wService = ams341wService;
    }

    /**
     * 取得所有ApiTrack清單by搜尋條件。
     */
    @GetMapping("/modadw341w")
    @PreAuthorize("hasAuthority('ams341w_s')")
    public ResponseEntity<ResponseDTO<List<ApiTrackDTO>>> findApiTrackListByConditions(
        Pageable pageable,
        @RequestParam(required = false) String source,
        @RequestParam(required = false) String jhiFrom,
        @RequestParam(required = false) String uri,
        @RequestParam(required = false) String statusCode,
        @RequestParam(required = false) String beginDate,
        @RequestParam(required = false) String endDate
    ) {
        log.info(
            "Ams341wResource-findApiTrackListByConditions 進入查詢功能 " +
            "source={}, jhiFrom={} uri={} statusCode={} beginDate={} endDate={}",
            source,
            jhiFrom,
            uri,
            statusCode,
            beginDate,
            endDate
        );
        ResponseDTO<List<ApiTrackDTO>> responseDTO = new ResponseDTO<>();
        try {
            Ams341wReqDTO request = new Ams341wReqDTO();
            request.setSource(source);
            request.setJhiFrom(jhiFrom);
            request.setUri(uri);
            request.setStatusCode(statusCode);
            request.setBeginDate(beginDate);
            request.setEndDate(endDate);

            // 查詢所有accessToken
            Ams341wApiTrackResDTO ams341wApiTrackResDTO = ams341wService.getAllApiTrack(pageable, request);
            if (ams341wApiTrackResDTO != null) {
                if (ams341wApiTrackResDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams341wApiTrackResDTO.getApiTrackDTOList());
                }
                responseDTO.setStatusCode(ams341wApiTrackResDTO.getStatusCode());

                return ResponseEntity.ok().headers(ams341wApiTrackResDTO.getHeaders()).body(responseDTO);
            } else {
                log.error("Ams341wResource-findApiTrackListByConditions 帳號異動清單回傳null request={}", request);
                responseDTO.setStatusCode(StatusCode.API_TRACK_SEARCH_DB_EXCEPTION);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception ex) {
            log.error("Ams341wResource-findApiTrackListByConditions-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.API_TRACK_SEARCH_DB_EXCEPTION));
    }
}
