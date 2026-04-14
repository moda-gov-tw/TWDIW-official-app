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

import gov.moda.dw.manager.service.custom.Ams312wService;
import gov.moda.dw.manager.service.dto.ExtendedUserLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams312wAccountChangeResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams312wReqDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class Ams312wResource {

    private Ams312wService ams312wService;

    public Ams312wResource(Ams312wService ams312wService) {
        this.ams312wService = ams312wService;
    }

    @GetMapping("/modadw312w/search")
    @PreAuthorize("hasAuthority('ams312w_s')")
    public ResponseEntity<ResponseDTO<List<ExtendedUserLogDTO>>> searchAccountLog(
        Pageable pageable,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String actionType,
        @RequestParam(required = false) String beginDate,
        @RequestParam(required = false) String endDate
    ) {
        log.info(
            "Ams312wResource-searchAccountLog 進入查詢功能 userId={}, actionType={} beginDate={} endDate={}",
            userId,
            actionType,
            beginDate,
            endDate
        );
        ResponseDTO<List<ExtendedUserLogDTO>> responseDTO = new ResponseDTO<>();
        try {
            Ams312wReqDTO request = new Ams312wReqDTO();
            request.setUserId(userId);
            request.setLogType(actionType);
            request.setBeginDate(beginDate);
            request.setEndDate(endDate);

            //查詢異動紀錄
            Ams312wAccountChangeResDTO ams312wAccountChangeResDTO = ams312wService.getExtendedUserLog(pageable, request);
            if (ams312wAccountChangeResDTO != null) {
                if (ams312wAccountChangeResDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams312wAccountChangeResDTO.getExtendedUserLogDTOList());
                }
                responseDTO.setStatusCode(ams312wAccountChangeResDTO.getStatusCode());

                return ResponseEntity.ok().headers(ams312wAccountChangeResDTO.getHeaders()).body(responseDTO);
            } else {
                log.error("Ams312wResource-searchAccountLog 帳號異動清單回傳null request={}", request);
                responseDTO.setStatusCode(StatusCode.ACCOUNTCHANGE_SEARCH_EXCEPTION);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception ex) {
            log.error("Ams312wResource-searchAccountLog 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }
}
