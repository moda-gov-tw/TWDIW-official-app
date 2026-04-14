package gov.moda.dw.manager.web.rest.custom;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.manager.service.custom.Ams332wService;
import gov.moda.dw.manager.service.dto.ResLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams332wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams332wResLogResDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class Ams332wResource {

    private static final String ENTITY_NAME = "Ams332wResource";

    private Ams332wService ams332wService;

    public Ams332wResource(Ams332wService ams332wService) {
        this.ams332wService = ams332wService;
    }

    @GetMapping("/modadw332w/search")
    @PreAuthorize("hasAuthority('ams332w_s')")
    public ResponseEntity<ResponseDTO<List<ResLogDTO>>> searchResLog(
        Pageable pageable,
        @RequestParam(required = false) String resId,
        @RequestParam(required = false) String beginDate,
        @RequestParam(required = false) String endDate
    ) {
        log.info("{}-searchResLog 進入功能異動清單查詢 resId={}, beginDate={} endDate={}", ENTITY_NAME, resId, beginDate, endDate);
        ResponseDTO<List<ResLogDTO>> responseDTO = new ResponseDTO<>();
        try {
            Ams332wReqDTO request = new Ams332wReqDTO();
            request.setResId(resId);
            request.setBeginDate(beginDate);
            request.setEndDate(endDate);

            Ams332wResLogResDTO ams332wResLogResDTO = ams332wService.getResLog(pageable, request);
            if (ams332wResLogResDTO != null) {
                if (ams332wResLogResDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams332wResLogResDTO.getResLogDTO());
                }
                responseDTO.setStatusCode(ams332wResLogResDTO.getStatusCode());

                return ResponseEntity.ok().headers(ams332wResLogResDTO.getHeaders()).body(responseDTO);
            } else {
                log.error("{}-searchResLog 功能異動清單回傳null request={}", ENTITY_NAME, request);
                responseDTO.setStatusCode(StatusCode.ROLECHANGE_SEARCH_EXCEPTION);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception ex) {
            log.error("{}-searchResLog 發生錯誤, 錯誤原因為={}", ENTITY_NAME, ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }
}
