package gov.moda.dw.manager.service.custom.common;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.VCDataStatusLog;
import gov.moda.dw.manager.repository.custom.CustomVCDataStatusLogRepository;
import gov.moda.dw.manager.service.custom.CustomUserService;
import gov.moda.dw.manager.service.dto.VCDataStatusLogDTO;
import gov.moda.dw.manager.service.mapper.VCDataStatusLogMapper;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VC 資料狀態紀錄服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomVCDataStatusLogService {

    private static final String ENTITY_NAME = "VCDataStatusLog";

    private static final String INVALID_FIELDS = "invalidFields";

    private final CustomUserService customUserService;

    private final CustomVCDataStatusLogRepository customVCDataStatusLogRepository;

    private final VCDataStatusLogMapper vcDataStatusLogMapper;

    /**
     * 建立一筆 VC 資料狀態紀錄
     *
     * 此方法會根據提供的參數建立一筆 VCDataStatusLog 紀錄，並寫入資料庫。
     *
     * @param vcCid          VC 憑證編號
     * @param vcItemId       vcItemId
     * @param transactionId  交易序號
     * @param valid          狀態 [0: ACTIVE(有效)、1: SUSPENDED(停用)、2: REVOKED(撤銷)、301:
     *                       INACTIVE(沒掃 QR Code)]
     * @param lastUpdateTime 最後更新時間(預設系統當前時間)
     * @param isSchedule     是否為排程呼叫
     */
    public void createStatusLog(String vcCid, Long vcItemId, String transactionId, Integer valid,
            Instant lastUpdateTime, boolean isSchedule) {
        // 判斷是否為排程呼叫
        long crUserId = -1;
        if (!isSchedule) {
            crUserId = customUserService.getLoginUserId();
        }

        // 如未傳 lastUpdateTime 則預設系統當前時間
        Instant now = Instant.now();
        lastUpdateTime = Objects.requireNonNullElse(lastUpdateTime, now);

        // 檢核 建立 VC 資料狀態紀錄參數
        this.validCreateStatusLog(vcCid, vcItemId, transactionId, valid);

        // 建立 VC 資料狀態紀錄
        VCDataStatusLog vcDataStatusLog = new VCDataStatusLog();
        vcDataStatusLog.setVcCid(vcCid);
        vcDataStatusLog.setVcItemId(vcItemId);
        vcDataStatusLog.setTransactionId(transactionId);
        vcDataStatusLog.setValid(valid);
        vcDataStatusLog.setCrUser(crUserId);
        vcDataStatusLog.setCrDatetime(now);
        vcDataStatusLog.setLastUpdateTime(lastUpdateTime);

        // 儲存資料
        customVCDataStatusLogRepository.save(vcDataStatusLog);
    }

    /**
     * 查詢 VC 資料狀態紀錄 By vcCid
     * 
     * @param vcCid VC 憑證編號
     * @return
     */
    public List<VCDataStatusLogDTO> getStatusLogByVcCidOrderByCrDatetimeDesc(String vcCid) {
        return vcDataStatusLogMapper.toDto(customVCDataStatusLogRepository.findByVcCidOrderByCrDatetimeDesc(vcCid));
    }

    /**
     * 檢核 建立 VC 資料狀態紀錄參數
     * 
     * @param vcCid         VC 憑證編號
     * @param vcItemId      vcItemId
     * @param transactionId 交易序號
     * @param valid         狀態 [0: ACTIVE(有效)、1: SUSPENDED(停用)、2: REVOKED(撤銷)、301:
     *                      INACTIVE(沒掃 QR Code)]
     */
    private void validCreateStatusLog(String vcCid, Long vcItemId, String transactionId, Integer valid) {
        if (StringUtils.isBlank(vcCid)) {
            throw new BadRequestAlertException("vcCid", ENTITY_NAME, INVALID_FIELDS);
        }

        if (null == vcItemId) {
            throw new BadRequestAlertException("vcItemId", ENTITY_NAME, INVALID_FIELDS);
        }

        if (StringUtils.isBlank(transactionId)) {
            throw new BadRequestAlertException("transactionId", ENTITY_NAME, INVALID_FIELDS);
        }

        if (null == valid) {
            throw new BadRequestAlertException("valid", ENTITY_NAME, INVALID_FIELDS);
        }
    }

}
