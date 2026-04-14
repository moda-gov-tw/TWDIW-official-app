package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.service.RoleLogQueryService;
import gov.moda.dw.manager.service.criteria.RoleLogCriteria;
import gov.moda.dw.manager.service.dto.RoleLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams322wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams322wRoleLogResDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.InstantFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@Service
@Transactional
public class Ams322wService {

    private final RoleLogQueryService roleLogQueryService;

    public Ams322wService(RoleLogQueryService roleLogQueryService) {
        this.roleLogQueryService = roleLogQueryService;
    }

    public Ams322wRoleLogResDTO getRoleLog(Pageable pageable, Ams322wReqDTO ams322wReqDTO) {
        log.info("Ams322wService-getRoleLog 開始查詢角色異動清單 ams322wReqDTO={}", ams322wReqDTO);
        try {
            Ams322wRoleLogResDTO ams322wRoleChangeLogResDTO = new Ams322wRoleLogResDTO();

            //檢查傳入的值
            RoleLogCriteria roleLogCriteria = this.setCriteriaFromSearch(ams322wReqDTO);

            //查詢
            Page<RoleLogDTO> findAll = roleLogQueryService.findByCriteria(roleLogCriteria, pageable);

            //整理回傳內容
            List<RoleLogDTO> roleLogDTOList = findAll.getContent();
            // 將分頁訊息放在Headers
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), findAll);

            ams322wRoleChangeLogResDTO.setRoleLogDTOList(roleLogDTOList);
            ams322wRoleChangeLogResDTO.setHeaders(headers);
            ams322wRoleChangeLogResDTO.setStatusCode(StatusCode.SUCCESS);

            return ams322wRoleChangeLogResDTO;
        } catch (Exception ex) {
            log.error("Ams322wService-getRoleLog 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 設定查詢條件
     * @param ams322wReqDTO
     * @return
     */
    private RoleLogCriteria setCriteriaFromSearch(Ams322wReqDTO ams322wReqDTO) {
        log.info("Ams322wService-setCriteriaFromSearch 開始設定查詢條件 ams322wReqDTO={}", ams322wReqDTO);
        RoleLogCriteria roleLogCriteria = new RoleLogCriteria();

        if (StringUtils.isNotEmpty(ams322wReqDTO.getLogType())) {
            roleLogCriteria.setLogType(StringFilterUtils.toEqualStringFilter(ams322wReqDTO.getLogType()));
        }

        if (StringUtils.isNotEmpty(ams322wReqDTO.getRoleId())) {
            roleLogCriteria.setRoleId(StringFilterUtils.toContainStringFilter(ams322wReqDTO.getRoleId()));
        }

        if (StringUtils.isNotEmpty(ams322wReqDTO.getBeginDate())) {
            ams322wReqDTO.setBeginDate(ams322wReqDTO.getBeginDate().replace("-", "/"));
        }

        if (StringUtils.isNotEmpty(ams322wReqDTO.getEndDate())) {
            ams322wReqDTO.setEndDate(ams322wReqDTO.getEndDate().replace("-", "/"));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Instant beginDate = DateUtils.convertToInstant(ams322wReqDTO.getBeginDate(), formatter, true);
        Instant endDate = DateUtils.convertToInstant(ams322wReqDTO.getEndDate(), formatter, false);
        if (beginDate != null && endDate != null) {
            if (endDate.isAfter(beginDate)) {
                roleLogCriteria.setLogTime(InstantFilterUtils.toInstantFilterBetween("logTime", beginDate, endDate));
            } else {
                log.error("Ams322wService-setCriteriaFromSearch，結束時間不能早於起始時間。");
            }
        } else {
            roleLogCriteria.setLogTime(InstantFilterUtils.toInstantFilterBetween("logTime", beginDate, endDate));
        }

        log.info("Ams322wService-setCriteriaFromSearch 查詢條件 roleLogCriteria={}", roleLogCriteria);
        return roleLogCriteria;
    }
}
