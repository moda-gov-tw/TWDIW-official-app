package gov.moda.dw.manager.job;

import java.time.Instant;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import gov.moda.dw.manager.service.custom.CustomScheduleService;
import gov.moda.dw.manager.service.custom.CustomVCItemDataService;
import gov.moda.dw.manager.service.dto.ScheduleDTO;

@DisallowConcurrentExecution
public class CleanVCItemDataJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(CleanVCItemDataJob.class);

    private final CustomVCItemDataService customVCItemDataService;
    private final CustomScheduleService customScheduleService;

    public CleanVCItemDataJob(
        CustomVCItemDataService customVCItemDataService,
        CustomScheduleService customScheduleService
    ) {
        this.customVCItemDataService = customVCItemDataService;
        this.customScheduleService = customScheduleService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Instant now = Instant.now();
            JobDetail jobDetail = context.getJobDetail();
            JobKey key = jobDetail.getKey();

            String[] jobKeyArray = key.toString().split("_");
            String scheduleId = null;
            if (jobKeyArray.length > 1) {
                scheduleId = jobKeyArray[1];
            }
            if (scheduleId != null) {
                // 更新Schedule表
                Long scheduleIdLong = Long.valueOf(scheduleId);
                int updateScheduleCount = customScheduleService.updateLastRunDatetime(scheduleIdLong, now);
                log.info("updateScheduleCount:{}", updateScheduleCount);
                // 清除 VCItemData
                if (updateScheduleCount > 0) {
                    Optional<ScheduleDTO> scheduleDTOOpt = customScheduleService.findOne(scheduleIdLong);
                    if (scheduleDTOOpt.isPresent()) {
                        ScheduleDTO scheduleDTO = scheduleDTOOpt.get();
                        String businessId = scheduleDTO.getBusinessId();

                        int i = customVCItemDataService.updateContentNull(scheduleIdLong, now, businessId);
                        log.info("Custom Schedule {} Update Count:{}", scheduleId, i);

                    }
                }
            }
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }

}
