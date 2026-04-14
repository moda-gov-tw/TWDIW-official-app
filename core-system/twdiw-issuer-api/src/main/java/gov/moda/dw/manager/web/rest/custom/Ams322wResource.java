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

import gov.moda.dw.manager.service.custom.Ams322wService;
import gov.moda.dw.manager.service.dto.RoleLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams322wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams322wRoleLogResDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class Ams322wResource {

    private Ams322wService ams322wService;

    public Ams322wResource(Ams322wService ams322wService) {
        this.ams322wService = ams322wService;
    }

    @GetMapping("/modadw322w/search")
    @PreAuthorize("hasAuthority('ams322w_s')")
    public ResponseEntity<ResponseDTO<List<RoleLogDTO>>> searchRoleLog(
        Pageable pageable,
        @RequestParam(required = false) String roleId,
        @RequestParam(required = false) String logType,
        @RequestParam(required = false) String beginDate,
        @RequestParam(required = false) String endDate
    ) {
        log.info(
            "Ams312wResource-searchAccountLog 進入查詢功能 roleId={}, logType={} beginDate={} endDate={}",
            roleId,
            logType,
            beginDate,
            endDate
        );
        ResponseDTO<List<RoleLogDTO>> responseDTO = new ResponseDTO<>();
        try {
            Ams322wReqDTO request = new Ams322wReqDTO();
            request.setRoleId(roleId);
            request.setLogType(logType);
            request.setBeginDate(beginDate);
            request.setEndDate(endDate);
            //查詢異動紀錄
            Ams322wRoleLogResDTO ams322wRoleChangeLogResDTO = ams322wService.getRoleLog(pageable, request);
            if (ams322wRoleChangeLogResDTO != null) {
                if (ams322wRoleChangeLogResDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams322wRoleChangeLogResDTO.getRoleLogDTOList());
                }
                responseDTO.setStatusCode(ams322wRoleChangeLogResDTO.getStatusCode());

                return ResponseEntity.ok().headers(ams322wRoleChangeLogResDTO.getHeaders()).body(responseDTO);
            } else {
                log.error("Ams322wResource-searchRoleLog 角色異動清單回傳null request={}", request);
                responseDTO.setStatusCode(StatusCode.ROLECHANGE_SEARCH_EXCEPTION);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception ex) {
            log.error("Ams322wResource-searchRoleLog 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }
}
