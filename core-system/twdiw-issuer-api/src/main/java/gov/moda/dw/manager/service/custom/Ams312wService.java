package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.service.ExtendedUserLogQueryService;
import gov.moda.dw.manager.service.criteria.ExtendedUserLogCriteria;
import gov.moda.dw.manager.service.dto.ExtendedUserLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams312wAccountChangeResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams312wReqDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.InstantFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@Service
@Transactional
public class Ams312wService {

    private final ExtendedUserLogQueryService extendedUserLogQueryService;

    public Ams312wService(ExtendedUserLogQueryService extendedUserLogQueryService) {
        this.extendedUserLogQueryService = extendedUserLogQueryService;
    }

    public Ams312wAccountChangeResDTO getExtendedUserLog(Pageable pageable, Ams312wReqDTO ams312wReqDTO) {
        log.info("Ams312wService-getExtendedUserLog 開始查詢帳號異動清單 ams312wReqDTO={}", ams312wReqDTO);
        try {
            Ams312wAccountChangeResDTO ams312wAccountChangeResDTO = new Ams312wAccountChangeResDTO();

            //檢查傳入的值
            ExtendedUserLogCriteria extendedUserLogCriteria = this.setCriteriaFromSearch(ams312wReqDTO);

            //查詢
            Page<ExtendedUserLogDTO> findAll = extendedUserLogQueryService.findByCriteria(extendedUserLogCriteria, pageable);

            //整理回傳內容
            List<ExtendedUserLogDTO> extendedUserLogDTOList = findAll.getContent();
            List<ExtendedUserLogDTO> resultExtendedUserLogList = new ArrayList<>();
            for (ExtendedUserLogDTO extendedUserLogDTO : extendedUserLogDTOList) {
                ExtendedUserLogDTO resultExtendedUserLogDTO = this.toResultExtendedUserLogDTO(extendedUserLogDTO);
                resultExtendedUserLogList.add(resultExtendedUserLogDTO);
            }
            // 將分頁訊息放在Headers
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), findAll);

            ams312wAccountChangeResDTO.setExtendedUserLogDTOList(resultExtendedUserLogList);
            ams312wAccountChangeResDTO.setHeaders(headers);
            ams312wAccountChangeResDTO.setStatusCode(StatusCode.SUCCESS);

            return ams312wAccountChangeResDTO;
        } catch (Exception ex) {
            log.error("Ams312wService-getExtendedUserLog 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 設定查詢條件
     * @param ams312wReqDTO
     * @return
     */
    private ExtendedUserLogCriteria setCriteriaFromSearch(Ams312wReqDTO ams312wReqDTO) {
        log.info("Ams312wService-setCriteriaFromSearch 開始設定查詢條件 ams312wReqDTO={}", ams312wReqDTO);
        ExtendedUserLogCriteria extendedUserLogCriteria = new ExtendedUserLogCriteria();

        if (StringUtils.isNotEmpty(ams312wReqDTO.getLogType())) {
            extendedUserLogCriteria.setLogType(StringFilterUtils.toEqualStringFilter(ams312wReqDTO.getLogType()));
        }

        if (StringUtils.isNotEmpty(ams312wReqDTO.getUserId())) {
            extendedUserLogCriteria.setUserId(StringFilterUtils.toContainStringFilter(ams312wReqDTO.getUserId()));
        }

        if (StringUtils.isNotEmpty(ams312wReqDTO.getBeginDate())) {
            ams312wReqDTO.setBeginDate(ams312wReqDTO.getBeginDate().replace("-", "/"));
        }

        if (StringUtils.isNotEmpty(ams312wReqDTO.getEndDate())) {
            ams312wReqDTO.setEndDate(ams312wReqDTO.getEndDate().replace("-", "/"));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Instant beginDate = DateUtils.convertToInstant(ams312wReqDTO.getBeginDate(), formatter, true);
        Instant endDate = DateUtils.convertToInstant(ams312wReqDTO.getEndDate(), formatter, false);
        if (beginDate != null && endDate != null) {
            if (endDate.isAfter(beginDate)) {
                extendedUserLogCriteria.setLogTime(InstantFilterUtils.toInstantFilterBetween("logTime", beginDate, endDate));
            } else {
                log.error("Ams312wService-setCriteriaFromSearch，結束時間不能早於起始時間。");
            }
        } else {
            extendedUserLogCriteria.setLogTime(InstantFilterUtils.toInstantFilterBetween("logTime", beginDate, endDate));
        }

        log.info("Ams312wService-setCriteriaFromSearch 查詢條件 extendedUserLogCriteria={}", extendedUserLogCriteria);
        return extendedUserLogCriteria;
    }

    private ExtendedUserLogDTO toResultExtendedUserLogDTO(ExtendedUserLogDTO extendedUserLogDTO) {
        ExtendedUserLogDTO resultExtendedUserLogDTO = new ExtendedUserLogDTO();
        resultExtendedUserLogDTO.setId(extendedUserLogDTO.getId());
        resultExtendedUserLogDTO.setUserId(extendedUserLogDTO.getUserId());
        resultExtendedUserLogDTO.setUserName(extendedUserLogDTO.getUserName());
        resultExtendedUserLogDTO.setEmail(extendedUserLogDTO.getEmail());
        resultExtendedUserLogDTO.setUserTypeId(extendedUserLogDTO.getUserTypeId());
        resultExtendedUserLogDTO.setState(extendedUserLogDTO.getState());
        resultExtendedUserLogDTO.setLogType(extendedUserLogDTO.getLogType());
        resultExtendedUserLogDTO.setTel(extendedUserLogDTO.getTel());
        resultExtendedUserLogDTO.setLogTime(extendedUserLogDTO.getLogTime());
        resultExtendedUserLogDTO.setCreateTime(extendedUserLogDTO.getCreateTime());
        resultExtendedUserLogDTO.setAuthChangeTime(extendedUserLogDTO.getAuthChangeTime());
        resultExtendedUserLogDTO.setPwdResetTime(extendedUserLogDTO.getPwdResetTime());

        return resultExtendedUserLogDTO;
    }
}
