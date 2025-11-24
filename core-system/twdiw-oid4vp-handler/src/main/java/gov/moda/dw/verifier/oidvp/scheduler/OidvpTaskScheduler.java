package gov.moda.dw.verifier.oidvp.scheduler;

import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.repository.SessionRepository;
import gov.moda.dw.verifier.oidvp.repository.VerifyResultRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class OidvpTaskScheduler implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OidvpTaskScheduler.class);

    private final SessionRepository sessionRepository;
    private final VerifyResultRepository verifyResultRepository;
    private final OidvpConfig oidvpConfig;

    public OidvpTaskScheduler(OidvpConfig oidvpConfig, SessionRepository sessionRepository, VerifyResultRepository verifyResultRepository) {
        this.oidvpConfig = oidvpConfig;
        this.sessionRepository = sessionRepository;
        this.verifyResultRepository = verifyResultRepository;
    }

    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.setThreadNamePrefix("OidvpScheduler-");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        String sessionDeleteCron = oidvpConfig.getSessionDeleteCron();
        String verifyResultDeleteCron = oidvpConfig.getVerifyResultDeleteCron();
        taskRegistrar.setScheduler(taskScheduler());
        taskRegistrar.addCronTask(() -> deleteExpiredSessionTask(), sessionDeleteCron);
        taskRegistrar.addCronTask(() -> deleteExpiredVerifyResultTask(), verifyResultDeleteCron);
    }

    public void deleteExpiredSessionTask() {
        LocalDateTime now = LocalDateTime.now();
        try {
            int deleted = sessionRepository.deleteExpiredSession(now);
            LOGGER.debug("DELETE SESSION SCHEDULER - deleted expired sessions = {}", deleted);
        } catch (Exception e) {
            LOGGER.error("DELETE SESSION SCHEDULER - delete expired session error : {}", e.getMessage());
        }
    }

    public void deleteExpiredVerifyResultTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = now.minusMinutes(oidvpConfig.getVerifyResultExpiredTime());
        try {
            int deleted = verifyResultRepository.deleteExpiredVerifyResult(expiredTime);
            LOGGER.debug("DELETE VERIFY_RESULT SCHEDULER - deleted expired VerifyResults = {}", deleted);
        } catch (Exception e) {
            LOGGER.error("DELETE VERIFY_RESULT SCHEDULER - delete expired VerifyResult error : {}", e.getMessage());
        }
    }
}
