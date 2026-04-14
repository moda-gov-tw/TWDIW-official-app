package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.Res;
import gov.moda.dw.manager.repository.custom.CustomResRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.ResLogService;
import gov.moda.dw.manager.service.ResQueryService;
import gov.moda.dw.manager.service.ResService;
import gov.moda.dw.manager.service.criteria.ResCriteria;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.dto.ResLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams331wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams331wUpdateStateResDTO;
import gov.moda.dw.manager.service.mapper.ResMapper;
import gov.moda.dw.manager.type.*;
import gov.moda.dw.manager.type.*;
import gov.moda.dw.manager.util.StringFilterUtils;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class Ams331wService {

    private final ResService resService;
    private final ResQueryService resQueryService;
    private final ResMapper resMapper;
    private final CustomResRepository resRepository;
    private final CustomRelService relService;
    private final ResLogService resLogService;

    public Ams331wService(
        ResService resService,
        ResQueryService resQueryService,
        ResMapper resMapper,
        CustomResRepository resRepository,
        CustomRelService relService,
        ResLogService resLogService
    ) {
        this.resService = resService;
        this.resQueryService = resQueryService;
        this.resMapper = resMapper;
        this.resRepository = resRepository;
        this.relService = relService;
        this.resLogService = resLogService;
    }

    /**
     * 查詢功能列, 沒有塞入條件就查詢全部
     *
     * @param ams331wReqDTO
     * @param pageable
     * @return
     */
    public Page<ResDTO> getRes(Ams331wReqDTO ams331wReqDTO, Pageable pageable) {
        log.info("Ams331wService-getRes 進入查詢功能列 ams331wReqDTO={}", ams331wReqDTO);
        try {
            ResCriteria resCriteria = new ResCriteria();

            if (StringUtils.isNotEmpty(ams331wReqDTO.getTypeId())) {
                resCriteria.setTypeId(StringFilterUtils.toEqualStringFilter(ams331wReqDTO.getTypeId()));
            } else {
                resCriteria.setTypeId(StringFilterUtils.toNotEqualStringFilter(ResType.Node.getCode()));
            }

            if (StringUtils.isNotEmpty(ams331wReqDTO.getResId())) {
                resCriteria.setResId(StringFilterUtils.toContainStringFilter(ams331wReqDTO.getResId()));
            }

            if (StringUtils.isNotEmpty(ams331wReqDTO.getResName())) {
                resCriteria.setResName(StringFilterUtils.toContainStringFilter(ams331wReqDTO.getResName()));
            }

            if (StringUtils.isNotEmpty(ams331wReqDTO.getState())) {
                resCriteria.setState(StringFilterUtils.toEqualStringFilter(ams331wReqDTO.getState()));
            }

            log.info("Ams331wService-getRes 查詢條件 resCriteria={}", resCriteria);
            Page<ResDTO> find = resQueryService.findByCriteria(resCriteria, pageable);

            return find;
        } catch (Exception ex) {
            log.error("Ams331wService-getRes 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 更新功能啟停狀態
     *
     * @param ams331wReqDTO
     * @return
     */
    public Ams331wUpdateStateResDTO updateState(Ams331wReqDTO ams331wReqDTO) {
        log.info("Ams331wService-updateState 進入更新功能啟停狀態 Ams331wReqDTO={}", ams331wReqDTO);
        Ams331wUpdateStateResDTO ams331wUpdateStateResDTO = new Ams331wUpdateStateResDTO();
        try {
            //檢查傳入的值
            if (ams331wReqDTO == null || ams331wReqDTO.getResId() == null) {
                log.warn("Ams331wService-updateState 缺少必要參數 ams331wReqDTO={}", ams331wReqDTO);
                ams331wUpdateStateResDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                return ams331wUpdateStateResDTO;
            }

            //查詢要更改的功能是否存在
            Optional<Res> opRes = resRepository.findByResId(ams331wReqDTO.getResId());
            if (opRes.isPresent()) {
                ResDTO resDTO = resMapper.toDto(opRes.get());

                //修改功能啟停狀態
                ResDTO updateResult = null;
                AuthorityAction authorityAction = null;
                if (StateType.DISABLED.getCode().equals(resDTO.getState())) {
                    resDTO.setState(StateType.ENABLED.getCode());
                    authorityAction = AuthorityAction.RES_ENABLED;
                    log.info("Ams331wService-updateState 功能改為啟用狀態 res={}", resDTO.getState());
                } else {
                    resDTO.setState(StateType.DISABLED.getCode());
                    authorityAction = AuthorityAction.RES_DISABLED;
                    log.info("Ams331wService-updateState 功能改為停用狀態 res={}", resDTO.getState());
                }
                updateResult = resService.save(resDTO);

                //修改 rel 狀態, 並同步至 jhi_user_authority
                boolean isOk = relService.resAction(resDTO, authorityAction);
                if (isOk) {
                    log.info("Ams331wService-updateState 更新{}狀態完成", authorityAction);
                    ams331wUpdateStateResDTO.setResDTO(updateResult);
                    ams331wUpdateStateResDTO.setStatusCode(StatusCode.SUCCESS);

                    //新增異動紀錄
                    this.saveResLog(updateResult, LogType.MOD);
                } else {
                    log.info(
                        "Ams331wService-updateState 更新{}狀態異常, ams331wReqDTO={}, resAction response={}",
                        authorityAction,
                        ams331wReqDTO,
                        isOk
                    );
                    ams331wUpdateStateResDTO.setStatusCode(StatusCode.INVALID_ACTIVATED);
                }
            } else {
                log.error("Ams331wService-updateState 查詢不到功能資料, 無法執行更改啟/停狀態 resId={}", ams331wReqDTO.getResId());
                ams331wUpdateStateResDTO.setStatusCode(StatusCode.RES_NOT_EXISTS);
            }

            return ams331wUpdateStateResDTO;
        } catch (Exception ex) {
            log.error("Ams331wService-updateState 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 新增功能異動紀錄
     * @param resDTO
     * @param logType
     */
    public void saveResLog(ResDTO resDTO, LogType logType) {
        log.info("Ams331wService-saveResLog 開始紀錄功能{}異動 ResDTO={} LogType={}", logType.getName(), resDTO, logType);
        ResLogDTO resLogDTO = this.toResLogDTO(resDTO, logType.getCode());
        resLogService.save(resLogDTO);
        log.info("Ams331wService-saveResLog 紀錄功能{}異動完成 ResDTO={} LogType={}", logType.getName(), resDTO, logType);
    }

    public Instant getCurrentTimeInTaiwan() {
        // 獲取當前的Instant。
        Instant now = Instant.now();
        // 定義本地時區(Asia/Taipei)。
        ZoneId taiwanZoneId = ZoneId.of("Asia/Taipei");
        // 將Instant轉換為本地時區的ZonedDateTime
        ZonedDateTime taiwanTime = now.atZone(taiwanZoneId);
        // 返回本地時區的時間，轉換為Instant
        return taiwanTime.toInstant();
    }

    public ResLogDTO toResLogDTO(ResDTO resDTO, String logTypeCode) {
        ResLogDTO resLogDTO = new ResLogDTO();

        if (SecurityUtils.getCurrentUserLogin().isPresent()) {
            resLogDTO.setActor(SecurityUtils.getCurrentUserLogin().get());
        } else {
            if (resDTO.getDataRole1() != null) {
                resLogDTO.setActor("ipAddress:" + resDTO.getDataRole1());
            } else {
                resLogDTO.setActor("");
            }
        }
        resLogDTO.setLogType(logTypeCode);
        resLogDTO.setLogTime(Instant.now());
        resLogDTO.setTypeId(resDTO.getTypeId());
        resLogDTO.setResId(resDTO.getResId());
        resLogDTO.setResGrp(resDTO.getResGrp());
        resLogDTO.setResName(resDTO.getResName());
        resLogDTO.setDescription(resDTO.getDescription());
        resLogDTO.setState(resDTO.getState());
        resLogDTO.setApiUri(resDTO.getApiUri());
        resLogDTO.setWebUrl(resDTO.getWebUrl());
        resLogDTO.setDataRole1(resDTO.getDataRole1());
        resLogDTO.setDataRole2(resDTO.getDataRole2());
        resLogDTO.setCreateTime(resDTO.getCreateTime());

        return resLogDTO;
    }
}
