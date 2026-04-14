package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.domain.Schedule;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.job.CleanVCItemDataJob;
import gov.moda.dw.manager.job.SyncScheduleJob;
import gov.moda.dw.manager.repository.ScheduleRepository;
import gov.moda.dw.manager.repository.custom.CustomScheduleRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.ScheduleDTO;
import gov.moda.dw.manager.service.mapper.ScheduleMapper;
import gov.moda.dw.manager.type.AuthorityAction;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Getter;

@Service
public class CustomScheduleService {

    private static final Logger log = LoggerFactory.getLogger(CustomScheduleService.class);

    private final QuartzWorkerService quartzWorkerService;
    private final CustomRelService customRelService;
    private final ScheduleRepository scheduleRepository;
    private final CustomScheduleRepository customScheduleRepository;
    private final CustomUserRepository customUserRepository;
    private final ScheduleMapper scheduleMapper;

    @Value("${sandbox.privileged-account:allAuth}")
    String privilegedAccount;

    @Value("${custom.job.enableJobWork}")
    private Boolean enableJobWork;

    @Getter
    private ThreadPoolExecutor threadpool = new ThreadPoolExecutor(
        10,
        50,
        120,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue(200),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public CustomScheduleService(
        QuartzWorkerService quartzWorkerService,
        CustomRelService customRelService,
        ScheduleRepository scheduleRepository,
        CustomScheduleRepository customScheduleRepository,
        CustomUserRepository customUserRepository,
        ScheduleMapper scheduleMapper
    ) {
        this.quartzWorkerService = quartzWorkerService;
        this.customRelService = customRelService;
        this.scheduleRepository = scheduleRepository;
        this.customScheduleRepository = customScheduleRepository;
        this.customUserRepository = customUserRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @PostConstruct
    public void init() {
        log.info("=== Load Schedule Data, startup Job! : {} ===", enableJobWork);

        if (enableJobWork) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("Thread sleep interrupted", e);
                Thread.currentThread().interrupt();
            }

            log.info("load Job");
            // 排程表中的排程
            List<Schedule> all = scheduleRepository.findAll();
            all
                .stream()
                .map(scheduleMapper::toDto)
                .forEach(dto -> {
                    Long scheduleId = dto.getId();
                    try {
                        String cronString = quartzWorkerService.generateCronString(dto);
                        if (cronString != null) {
                            quartzWorkerService.addCronJob(
                                CleanVCItemDataJob.class,
                                QuartzWorkerService.cleanVCDataTaskNamePrefix + dto.getId(),
                                cronString,
                                dto.getTimezone()
                            );
                            quartzWorkerService.getRunScheduleMap().putIfAbsent(dto.getId(), dto);
                        }
                    } catch (SchedulerException e) {
                        log.error("init add ScheduleId {} fail, 原因為:{}", scheduleId, ExceptionUtils.getStackTrace(e));
                    }
                });

            // 每分鐘執行一次同步排程
            try {
                quartzWorkerService.addCronJob(SyncScheduleJob.class, "SyncJob", "30 * * * * ?", null);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Long findUserId(String login) {
        return customUserRepository.findOneByLogin(login).orElseThrow().getId();
    }

    public List<RoleDTO> getRoles(String login) {
        User user = customUserRepository.findOneByLogin(login).orElseThrow();
        return customRelService.accountAction(user, null, AuthorityAction.ACCOUNT_SEARCH_ROLE);
    }

    public boolean checkIsAdminResult(String login) {
        List<RoleDTO> roles = getRoles(login);
        return roles.stream().map(RoleDTO::getRoleId).anyMatch(e -> privilegedAccount.equals(e));
    }

    /**
     * Save a schedule.
     *
     * @param scheduleDTO the entity to save.
     * @return the persisted entity.
     */
    public ScheduleDTO save(ScheduleDTO scheduleDTO) {
        log.debug("Request to save Schedule : {}", scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        String timezone = scheduleDTO.getTimezone() == null ? QuartzWorkerService.DefaultTimeZone : scheduleDTO.getTimezone();
        schedule.setTimezone(timezone);
        schedule.setCrDatetime(Instant.now());
        schedule = scheduleRepository.save(schedule);
        ScheduleDTO result = scheduleMapper.toDto(schedule);
        if (enableJobWork) {
            try {
                quartzWorkerService.addCronJob(
                    CleanVCItemDataJob.class,
                    QuartzWorkerService.cleanVCDataTaskNamePrefix + schedule.getId(),
                    quartzWorkerService.generateCronString(scheduleDTO),
                    timezone
                );
                quartzWorkerService.getRunScheduleMap().putIfAbsent(result.getId(), result);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Transactional
    public int updateLastRunDatetime(Long id, Instant lastRunDatetime) {
        return customScheduleRepository.updateLastRunDatetime(id, lastRunDatetime);
    }

    @Transactional
    public void deleteCleanVCDataJob(Long scheduleId) {
        log.debug("Request to delete Schedule : {}", scheduleId);
        scheduleRepository.deleteById(scheduleId);
        if (enableJobWork) {
            try {
                quartzWorkerService.delJob(QuartzWorkerService.cleanVCDataTaskNamePrefix + scheduleId);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Optional<ScheduleDTO> findOne(Long id) {
        log.debug("Request to get Schedule : {}", id);
        return scheduleRepository.findById(id).map(scheduleMapper::toDto);
    }
}
