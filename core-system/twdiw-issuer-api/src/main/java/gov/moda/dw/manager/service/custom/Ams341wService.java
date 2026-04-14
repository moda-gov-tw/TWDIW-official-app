package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.ApiTrack;
import gov.moda.dw.manager.service.ApiTrackQueryService;
import gov.moda.dw.manager.service.criteria.ApiTrackCriteria;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.dto.custom.Ams341wApiTrackResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams341wReqDTO;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.InstantFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@Service
@Transactional
public class Ams341wService {

    private static final String ENTITY_NAME = "Ams312wService";

    private final CustomApiTrackQueryService apiTrackQueryService;

    public Ams341wService(CustomApiTrackQueryService apiTrackQueryService) {
        this.apiTrackQueryService = apiTrackQueryService;
    }

    public Ams341wApiTrackResDTO getAllApiTrack(Pageable pageable, Ams341wReqDTO ams341wReqDTO) {
        log.info("Ams341wService-getAllApiTrack 開始查詢帳號異動清單 ams341wReqDTO={}", ams341wReqDTO);
        try {
            Ams341wApiTrackResDTO ams341wApiTrackResDTO = new Ams341wApiTrackResDTO();

            //檢查傳入的值
            //            ApiTrackCriteria apiTrackCriteria = this.setCriteriaFromSearch(ams341wReqDTO);

            //查詢
            //            Page<ApiTrackDTO> findAll = apiTrackQueryService.findByCriteria(apiTrackCriteria, pageable);

            Specification<ApiTrack> specification = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (StringUtils.isNotEmpty(ams341wReqDTO.getSource())) {
                    predicates.add(criteriaBuilder.like(root.get("source"), "%" + ams341wReqDTO.getSource() + "%"));
                }

                if (StringUtils.isNotEmpty(ams341wReqDTO.getJhiFrom())) {
                    predicates.add(criteriaBuilder.like(root.get("jhiFrom"), "%" + ams341wReqDTO.getJhiFrom() + "%"));
                }

                if (StringUtils.isNotEmpty(ams341wReqDTO.getUri())) {
                    predicates.add(criteriaBuilder.like(root.get("uri"), "%" + ams341wReqDTO.getUri() + "%"));
                }

                if (StringUtils.isNotEmpty(ams341wReqDTO.getStatusCode())) {
                    if ("success".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                        predicates.add(root.get("statusCode").in(Arrays.asList("200", "201", "202", "204")));
                    } else if ("fail".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                        predicates.add(
                            criteriaBuilder.or(
                                criteriaBuilder.not(root.get("statusCode").in(Arrays.asList("200", "201", "202", "204"))),
                                criteriaBuilder.isNull(root.get("statusCode"))
                            )
                        );
                    } else {
                        predicates.add(criteriaBuilder.equal(root.get("statusCode"), ams341wReqDTO.getStatusCode()));
                    }
                }

                if (StringUtils.isNotEmpty(ams341wReqDTO.getBeginDate()) && StringUtils.isNotEmpty(ams341wReqDTO.getEndDate())) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    Instant beginDate = DateUtils.convertToInstant(ams341wReqDTO.getBeginDate().replace("-", "/"), formatter, true);
                    Instant endDate = DateUtils.convertToInstant(ams341wReqDTO.getEndDate().replace("-", "/"), formatter, false);

                    if (beginDate != null && endDate != null && endDate.isAfter(beginDate)) {
                        predicates.add(criteriaBuilder.between(root.get("timestamp"), beginDate, endDate));
                    } else {
                        log.error("Ams341wService-getAllApiTrack，結束時間不能早於起始時間。");
                    }
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<ApiTrackDTO> findAll = apiTrackQueryService.findBySpecification(specification, pageable);

            //整理回傳內容
            List<ApiTrackDTO> apiTrackDTOList = findAll.getContent();
            List<ApiTrackDTO> resultApiTrackList = new ArrayList<>();
            for (ApiTrackDTO apiTrackDTO : apiTrackDTOList) {
                ApiTrackDTO resultApiTrackDTO = this.toResultApiTrackDTO(apiTrackDTO);
                resultApiTrackList.add(resultApiTrackDTO);
            }
            // 將分頁訊息放在Headers
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), findAll);

            ams341wApiTrackResDTO.setApiTrackDTOList(resultApiTrackList);
            ams341wApiTrackResDTO.setHeaders(headers);
            ams341wApiTrackResDTO.setStatusCode(StatusCode.SUCCESS);

            return ams341wApiTrackResDTO;
        } catch (Exception ex) {
            log.error("Ams341wService-getAllApiTrack-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    /**
     * 設定查詢條件。
     */
    private ApiTrackCriteria setCriteriaFromSearch(Ams341wReqDTO ams341wReqDTO) {
        log.info("Ams341wService-setCriteriaFromSearch 開始設定查詢條件 ams341wReqDTO={}", ams341wReqDTO);
        ApiTrackCriteria apiTrackCriteria = new ApiTrackCriteria();

        if (StringUtils.isNotEmpty(ams341wReqDTO.getSource())) {
            apiTrackCriteria.setSource(StringFilterUtils.toContainStringFilter(ams341wReqDTO.getSource()));
        }

        if (StringUtils.isNotBlank(ams341wReqDTO.getJhiFrom())) {
            apiTrackCriteria.setJhiFrom(StringFilterUtils.toContainStringFilter(ams341wReqDTO.getJhiFrom()));
        }

        if (StringUtils.isNotEmpty(ams341wReqDTO.getUri())) {
            apiTrackCriteria.setUri(StringFilterUtils.toContainStringFilter(ams341wReqDTO.getUri()));
        }

        if (StringUtils.isNotEmpty(ams341wReqDTO.getStatusCode())) {
            if ("success".equals(ams341wReqDTO.getStatusCode()) || "fail".equals(ams341wReqDTO.getStatusCode())) {
                //                if (ResType.WEB.getCode().equals(ams341wReqDTO.getServiceId().toLowerCase())) {
                //                    if ("success".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                //                        apiTrackCriteria.setStatusCode(StringFilterUtils.toInStringFilter(Arrays.asList("200", "201", "202", "204")));
                //                    }
                //                    if ("fail".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                //                        apiTrackCriteria.setStatusCode(StringFilterUtils.toNotInStringFilter(Arrays.asList("200", "201", "202", "204")));
                //                    }
                //                }
                //                if (ResType.API.getCode().equals(ams341wReqDTO.getServiceId().toLowerCase())) {
                //                    if ("success".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                //                        apiTrackCriteria.setStatusCode(StringFilterUtils.toEqualStringFilter("0"));
                //                    }
                //                    if ("fail".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                //                        apiTrackCriteria.setStatusCode(StringFilterUtils.toNotEqualStringFilter("0"));
                //                    }
                //                }
                if ("success".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                    apiTrackCriteria.setStatusCode(StringFilterUtils.toInStringFilter(Arrays.asList("200", "201", "202", "204")));
                }
                if ("fail".equalsIgnoreCase(ams341wReqDTO.getStatusCode())) {
                    apiTrackCriteria.setStatusCode(StringFilterUtils.toNotInStringFilter(Arrays.asList("200", "201", "202", "204")));
                    apiTrackCriteria.setStatusCode(StringFilterUtils.toIsNullFilter());
                }
            } else {
                apiTrackCriteria.setStatusCode(StringFilterUtils.toEqualStringFilter(ams341wReqDTO.getStatusCode()));
            }
        }

        if (StringUtils.isNotEmpty(ams341wReqDTO.getBeginDate())) {
            ams341wReqDTO.setBeginDate(ams341wReqDTO.getBeginDate().replace("-", "/"));
        }

        if (StringUtils.isNotEmpty(ams341wReqDTO.getEndDate())) {
            ams341wReqDTO.setEndDate(ams341wReqDTO.getEndDate().replace("-", "/"));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Instant beginDate = DateUtils.convertToInstant(ams341wReqDTO.getBeginDate(), formatter, true);
        Instant endDate = DateUtils.convertToInstant(ams341wReqDTO.getEndDate(), formatter, false);
        if (beginDate != null && endDate != null) {
            if (endDate.isAfter(beginDate)) {
                apiTrackCriteria.setTimestamp(InstantFilterUtils.toInstantFilterBetween("timestamp", beginDate, endDate));
            } else {
                log.error("Ams341wService-setCriteriaFromSearch，結束時間不能早於起始時間。");
            }
        } else {
            apiTrackCriteria.setTimestamp(InstantFilterUtils.toInstantFilterBetween("timestamp", beginDate, endDate));
        }

        log.info("Ams341wService-setCriteriaFromSearch 查詢條件 apiTrackCriteria={}", apiTrackCriteria);
        return apiTrackCriteria;
    }

    /**
     * 檢查日期起始時間與結束時間
     * @param begin
     * @param end
     * @return
     */
    private StatusCode checkQueryDate(Instant begin, Instant end) {
        log.info("Ams341wService-checkQueryDate 檢查日期 begin={}, end={}", begin, end);

        if (begin != null && end != null && end.isBefore(begin)) {
            log.info("Ams341wService-checkQueryDate 結束時間不可小於起始時間 beginDate={}, endDate={}", begin, end);
            return StatusCode.DATE_RANGE_ERROR;
        }

        if (begin == null) {
            begin = Instant.EPOCH;
            log.debug("=====Ams341wService-checkQueryDate begin==null = {}", begin);
        }
        if (end == null) {
            // 取得當日23:59:59。
            Date date = new Date();
            end = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault())
                .with(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();
            log.debug("=====Ams341wService-checkQueryDate end==null = {}", end);
        } else {
            end = end.plusSeconds(24 * 60 * 60 - 1);
            log.debug("=====Ams341wService-checkQueryDate end!=null = {}", end);
        }

        return StatusCode.SUCCESS;
    }

    private ApiTrackDTO toResultApiTrackDTO(ApiTrackDTO apiTrackDTO) {
        ApiTrackDTO resultApiTrackDTO = new ApiTrackDTO();
        resultApiTrackDTO.setId(apiTrackDTO.getId());
        resultApiTrackDTO.setAccessToken1(apiTrackDTO.getAccessToken1());
        resultApiTrackDTO.setAccessToken2(apiTrackDTO.getAccessToken2());
        resultApiTrackDTO.setCharged(apiTrackDTO.getCharged());
        resultApiTrackDTO.setCost(apiTrackDTO.getCost());
        resultApiTrackDTO.setJhiFrom(apiTrackDTO.getJhiFrom());
        resultApiTrackDTO.setJhiTo(apiTrackDTO.getJhiTo());
        resultApiTrackDTO.setRequestBody(apiTrackDTO.getRequestBody());
        resultApiTrackDTO.setRequestParam(apiTrackDTO.getRequestParam());
        resultApiTrackDTO.setRequestMethod(apiTrackDTO.getRequestMethod());
        resultApiTrackDTO.setRtt(apiTrackDTO.getRtt());
        resultApiTrackDTO.setServiceId(apiTrackDTO.getServiceId());
        resultApiTrackDTO.setSource(apiTrackDTO.getSource());
        resultApiTrackDTO.setStatusCode(apiTrackDTO.getStatusCode());
        resultApiTrackDTO.setSynced(apiTrackDTO.getSynced());
        resultApiTrackDTO.setTimestamp(apiTrackDTO.getTimestamp());
        resultApiTrackDTO.setUri(apiTrackDTO.getUri());
        resultApiTrackDTO.setUrl(apiTrackDTO.getUrl());
        resultApiTrackDTO.setUuid(apiTrackDTO.getUuid());

        return resultApiTrackDTO;
    }
}
