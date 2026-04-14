package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.service.ResLogQueryService;
import gov.moda.dw.manager.service.criteria.ResLogCriteria;
import gov.moda.dw.manager.service.dto.ResLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams332wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams332wResLogResDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.InstantFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@Service
@Transactional
public class Ams332wService {

    private static final String ENTITY_NAME = "Ams332wService";

    private final ResLogQueryService resLogQueryService;

    public Ams332wService(ResLogQueryService resLogQueryService) {
        this.resLogQueryService = resLogQueryService;
    }

    public Ams332wResLogResDTO getResLog(Pageable pageable, Ams332wReqDTO ams332wReqDTO) {
        log.info("{}-getResLog 開始查詢功能異動清單 ams332wReqDTO={}", ENTITY_NAME, ams332wReqDTO);
        try {
            Ams332wResLogResDTO ams332wResLogResDTO = new Ams332wResLogResDTO();

            //檢查傳入的值
            ResLogCriteria resLogCriteria = this.setCriteriaFromSearch(ams332wReqDTO);

            //查詢
            Page<ResLogDTO> findAll = resLogQueryService.findByCriteria(resLogCriteria, pageable);

            //整理回傳內容
            List<ResLogDTO> resLogDTOList = findAll.getContent();
            // 將分頁訊息放在Headers
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), findAll);

            ams332wResLogResDTO.setResLogDTO(resLogDTOList);
            ams332wResLogResDTO.setHeaders(headers);
            ams332wResLogResDTO.setStatusCode(StatusCode.SUCCESS);

            return ams332wResLogResDTO;
        } catch (Exception ex) {
            log.error("{}-getResLog 發生錯誤, 錯誤原因為={}", ENTITY_NAME, ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 設定查詢條件
     * @param ams332wReqDTO
     * @return
     */
    private ResLogCriteria setCriteriaFromSearch(Ams332wReqDTO ams332wReqDTO) {
        log.info("{}-setCriteriaFromSearch 開始設定查詢條件 ams332wReqDTO={}", ENTITY_NAME, ams332wReqDTO);
        ResLogCriteria resLogCriteria = new ResLogCriteria();

        if (StringUtils.isNotEmpty(ams332wReqDTO.getLogType())) {
            resLogCriteria.setLogType(StringFilterUtils.toEqualStringFilter(ams332wReqDTO.getLogType()));
        }

        if (StringUtils.isNotEmpty(ams332wReqDTO.getResId())) {
            resLogCriteria.setResId(StringFilterUtils.toContainStringFilter(ams332wReqDTO.getResId()));
        }

        if (StringUtils.isNotEmpty(ams332wReqDTO.getBeginDate())) {
            ams332wReqDTO.setBeginDate(ams332wReqDTO.getBeginDate().replace("-", "/"));
        }

        if (StringUtils.isNotEmpty(ams332wReqDTO.getEndDate())) {
            ams332wReqDTO.setEndDate(ams332wReqDTO.getEndDate().replace("-", "/"));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Instant beginDate = DateUtils.convertToInstant(ams332wReqDTO.getBeginDate(), formatter, true);
        Instant endDate = DateUtils.convertToInstant(ams332wReqDTO.getEndDate(), formatter, false);
        if (beginDate != null && endDate != null) {
            if (endDate.isAfter(beginDate)) {
                resLogCriteria.setLogTime(InstantFilterUtils.toInstantFilterBetween("logTime", beginDate, endDate));
            } else {
                log.error("Ams332wService-setCriteriaFromSearch，結束時間不能早於起始時間。");
            }
        } else {
            resLogCriteria.setLogTime(InstantFilterUtils.toInstantFilterBetween("logTime", beginDate, endDate));
        }

        log.info("{}-setCriteriaFromSearch 查詢條件 resLogCriteria={}", ENTITY_NAME, resLogCriteria);
        return resLogCriteria;
    }

    /**
     * 檢查日期起始時間與結束時間
     * @param begin
     * @param end
     * @return
     */
    private StatusCode checkQueryDate(Instant begin, Instant end) {
        log.info("{}-checkQueryDate 檢查日期 begin={}, end={}", ENTITY_NAME, begin, end);

        if (begin != null && end != null && end.isBefore(begin)) {
            log.info("{}-checkQueryDate 結束時間不可小於起始時間 beginDate={}, endDate={}", ENTITY_NAME, begin, end);
            return StatusCode.DATE_RANGE_ERROR;
        }

        if (begin == null) {
            begin = Instant.EPOCH;
            log.debug("====={}-checkQueryDate begin==null = {}", ENTITY_NAME, begin);
        }
        if (end == null) {
            // 取得當日23:59:59。
            Date date = new Date();
            end = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault())
                .with(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();
            log.debug("====={}-checkQueryDate end==null = {}", ENTITY_NAME, end);
        } else {
            end = end.plusSeconds(24 * 60 * 60 - 1);
            log.debug("====={}-checkQueryDate end!=null = {}", ENTITY_NAME, end);
        }

        return StatusCode.SUCCESS;
    }
}
