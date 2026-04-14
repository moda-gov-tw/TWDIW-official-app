package gov.moda.dw.manager.web.rest.custom;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.service.custom.Ams331wService;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams331wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams331wUpdateStateResDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@RestController
@RequestMapping("/api")
public class Ams331wResource {

    private final Ams331wService ams331wService;

    public Ams331wResource(Ams331wService ams331wService) {
        this.ams331wService = ams331wService;
    }

    @GetMapping("/modadw331w/search")
    @PreAuthorize("hasAuthority('ams331w_s')")
    public ResponseEntity<ResponseDTO<List<ResDTO>>> searchRes(
        Pageable pageable,
        @RequestParam(required = false) String resId,
        @RequestParam(required = false) String resName,
        @RequestParam(required = false) String typeId,
        @RequestParam(required = false) String state
    ) {
        log.info("Ams331wResource-searchRes 進入查詢功能 resId={}, resName={} typeId={} state={}", resId, resName, typeId, state);
        ResponseDTO<List<ResDTO>> responseDTO = new ResponseDTO<>();
        try {
            Ams331wReqDTO request = new Ams331wReqDTO();
            request.setResId(resId);
            request.setResName(resName);
            request.setTypeId(typeId);
            request.setState(state);

            // 查詢功能
            Page<ResDTO> page = ams331wService.getRes(request, pageable);

            if (page != null) {
                List<ResDTO> resDTOList = page.getContent();

                //整理要回傳的資料
                List<ResDTO> resultResList = new ArrayList<>();
                for (ResDTO resDTO : resDTOList) {
                    ResDTO resultResDTO = this.saveToResultResDTO(resDTO);
                    resultResList.add(resultResDTO);
                }

                responseDTO.setStatusCode(StatusCode.SUCCESS);
                responseDTO.setData(resultResList);

                // 將分頁訊息放在Headers
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(responseDTO);
            } else {
                log.error("Ams331wService-searchAllRes 查詢功能列回傳null request={}", request);
                responseDTO.setStatusCode(StatusCode.RES_SEARCH_EXCEPTION);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception ex) {
            log.error("Ams331wService-searchAllRes 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @PostMapping("/modadw331w/updateState")
    @PreAuthorize("hasAuthority('ams331w_upState')")
    public ResponseDTO<Ams331wUpdateStateResDTO> setState(@RequestBody Ams331wReqDTO request) {
        log.info("Ams331wResource-setState 進入功能起停狀態更改 Ams331wReqDTO={}", request);
        ResponseDTO<Ams331wUpdateStateResDTO> responseDTO = new ResponseDTO<>();
        try {
            //更新啟停狀態
            Ams331wUpdateStateResDTO ams331wUpdateStateResDTO = ams331wService.updateState(request);
            if (ams331wUpdateStateResDTO != null) {
                if (ams331wUpdateStateResDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams331wUpdateStateResDTO);
                }
                responseDTO.setStatusCode(ams331wUpdateStateResDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams331wResource-setState 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    private ResDTO saveToResultResDTO(ResDTO resDTO) {
        ResDTO resultDTO = new ResDTO();
        resultDTO.setId(resDTO.getId());
        resultDTO.setResGrp(resDTO.getResGrp());
        resultDTO.setResId(resDTO.getResId());
        resultDTO.setResName(resDTO.getResName());
        resultDTO.setDescription(resDTO.getDescription());
        resultDTO.setTypeId(resDTO.getTypeId());
        resultDTO.setApiUri(resDTO.getApiUri());
        resultDTO.setWebUrl(resDTO.getWebUrl());
        resultDTO.setState(resDTO.getState());
        resultDTO.setCreateTime(resDTO.getCreateTime());

        return resultDTO;
    }
}
