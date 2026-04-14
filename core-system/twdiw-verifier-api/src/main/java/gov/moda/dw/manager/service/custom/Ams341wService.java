package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.domain.ApiTrack;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.dto.custom.Ams341wApiTrackResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams341wReqDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DateUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@Service
@Transactional
public class Ams341wService {

    private final CustomApiTrackQueryService apiTrackQueryService;

    public Ams341wService(CustomApiTrackQueryService apiTrackQueryService) {
        this.apiTrackQueryService = apiTrackQueryService;
    }

    public Ams341wApiTrackResDTO getAllApiTrack(Pageable pageable, Ams341wReqDTO ams341wReqDTO) {
        log.info("Ams341wService-getAllApiTrack 開始查詢帳號異動清單 ams341wReqDTO={}", ams341wReqDTO);
        try {
            Ams341wApiTrackResDTO ams341wApiTrackResDTO = new Ams341wApiTrackResDTO();

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
                        predicates.add(criteriaBuilder.or(
                            criteriaBuilder.not(root.get("statusCode").in(Arrays.asList("200", "201", "202", "204"))),
                            criteriaBuilder.isNull(root.get("statusCode"))
                        ));
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
