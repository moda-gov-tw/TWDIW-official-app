package gov.moda.dw.manager.job;

import gov.moda.dw.manager.domain.Schedule;
import gov.moda.dw.manager.repository.ScheduleRepository;
import gov.moda.dw.manager.service.custom.QuartzWorkerService;
import gov.moda.dw.manager.service.dto.ScheduleDTO;
import gov.moda.dw.manager.service.mapper.ScheduleMapper;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class SyncScheduleJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(SyncScheduleJob.class);
    private final QuartzWorkerService quartzWorkerService;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    public SyncScheduleJob(QuartzWorkerService quartzWorkerService, ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper) {
        this.quartzWorkerService = quartzWorkerService;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            int beforeSize = quartzWorkerService.getRunScheduleMap().size();

            List<Schedule> all = scheduleRepository.findAll();
            quartzWorkerService
                .getRunScheduleMap()
                .forEach((scheduleId, dto) -> {
                    boolean notExistInDB = all.stream().noneMatch(db -> db.getId().equals(scheduleId));
                    if (notExistInDB) {
                        try {
                            quartzWorkerService.delJob(QuartzWorkerService.cleanVCDataTaskNamePrefix + scheduleId);
                            quartzWorkerService.getRunScheduleMap().remove(scheduleId);
                            log.info("new remove ScheduleId {} success!", scheduleId);
                        } catch (SchedulerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

            all
                .stream()
                .map(scheduleMapper::toDto)
                .forEach(dto -> {
                    Long scheduleId = dto.getId();
                    ScheduleDTO existsScheduleDTO = quartzWorkerService.getRunScheduleMap().putIfAbsent(scheduleId, dto);
                    if (existsScheduleDTO == null) {
                        try {
                            String cronString = quartzWorkerService.generateCronString(dto);
                            if (cronString != null) {
                                quartzWorkerService.addCronJob(
                                    CleanVCItemDataJob.class,
                                    QuartzWorkerService.cleanVCDataTaskNamePrefix + dto.getId(),
                                    cronString,
                                    dto.getTimezone()
                                );
                                log.info("new add ScheduleId {} success!", dto.getId());
                            }
                        } catch (SchedulerException e) {
                            log.error("new add ScheduleId {} fail, 原因為:{}", scheduleId, ExceptionUtils.getStackTrace(e));
                            quartzWorkerService.getRunScheduleMap().remove(scheduleId);
                        }
                    }
                });
            log.info("SyncScheduleJob CurrentJob Count Before/After :{}/{}", beforeSize, quartzWorkerService.getRunScheduleMap().size());
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }
}
