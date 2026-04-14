package gov.moda.dw.manager.schedule;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.repository.custom.CustomVCCredentialRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemDataRepository;
import gov.moda.dw.manager.service.custom.DwSandBoxVC302WService;
import gov.moda.dw.manager.service.custom.common.CustomVCDataStatusLogService;
import gov.moda.dw.manager.service.dto.custom.VCCredentialRevokeVcItemDataDTO;
import gov.moda.dw.manager.type.VcItemDataValidType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncRevokedVcItemDataSchedule {

    private final DwSandBoxVC302WService dwSandBoxVC302WService;

    private final CustomVCDataStatusLogService customVCDataStatusLogService;

    private final CustomVCCredentialRepository customVCCredentialRepository;

    private final CustomVCItemDataRepository customVCItemDataRepository;

    @Value("${custom.schedule.syncRevokedVcItemDataSwitch}")
    private Boolean syncRevokedVcItemDataSwitch;

    @Scheduled(cron = "${custom.schedule.syncRevokedVcItemDataCron}")
    private void syncRevokedVcItemDataSchedule() {
        if (Boolean.TRUE.equals(syncRevokedVcItemDataSwitch)) {
            this.run();
        }
    }

    private void run() {
        log.info("同步已撤銷 VC 資料狀態排程開始...");
        int successCount = 0;
        try {
            Integer revoked = VcItemDataValidType.REVOKED.getCode();
            // step1 查詢 credential 為撤銷且 vc_item_data 不為撤銷的 VC
            List<VCCredentialRevokeVcItemDataDTO> revokeVcCredentialList = customVCCredentialRepository
                    .findRevokeVcCredentialList(revoked);

            // step2 同步 credential 狀態至 vc_item_data 及 vc_data_status_log
            for (VCCredentialRevokeVcItemDataDTO vcCredentiaDTO : revokeVcCredentialList) {
                // 進行 VC 撤銷，成功後回寫 vc_item_data valid，並建立一筆 VC 資料狀態紀錄
                dwSandBoxVC302WService.executeVcActionAndRecord(null, vcCredentiaDTO.getCid(),
                        vcCredentiaDTO.getLastUpdateTime(), VcItemDataValidType.REVOKED.getAction());
                successCount++;
            }

            // step3 當 credential 為撤銷且 vc_data_status_log 無撤銷紀錄時，建立一筆 VC 資料狀態紀錄
            List<VCCredentialRevokeVcItemDataDTO> revokeVcCredentialLogList = customVCCredentialRepository
                    .findRevokeVcCredentialLogList(revoked);
            for (VCCredentialRevokeVcItemDataDTO vcCredentiaDTO : revokeVcCredentialLogList) {
                VCItemData vcData = customVCItemDataRepository.findFirstByVcCid(vcCredentiaDTO.getCid());
                // 建立一筆 VC 資料狀態紀錄
                customVCDataStatusLogService.createStatusLog(vcCredentiaDTO.getCid(), vcData.getId(),
                        vcData.getTransactionId(), revoked, vcCredentiaDTO.getLastUpdateTime(), true);
                successCount++;
            }
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        log.info("SyncRevokedVcItemData Schedule Update Count : {}", successCount);
    }

}
