package gov.moda.dw.verifier.oidvp.config;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class TaskExecutorConfig {

    private static final int REQUEST_WAIT_TIME = 2;

    @Bean("CallbackTaskExecutor")
    public Executor CallbackTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        double maxPoolSize = processors * (1 + (REQUEST_WAIT_TIME / 0.1));

        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize((int) maxPoolSize);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
        executor.setTaskDecorator(new LogbackContextMapDecorator());
        executor.setThreadNamePrefix("callbackTask-");

        executor.initialize();
        return executor;
    }

    public static class LogbackContextMapDecorator implements TaskDecorator {

        @Override
        @NonNull
        public Runnable decorate(@NonNull Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return setMdcContextMap(runnable, contextMap);
        }

        private Runnable setMdcContextMap(Runnable runnable, Map<String, String> contextMap) {
            return () -> {
                Map<String, String> previous = MDC.getCopyOfContextMap();
                if (contextMap == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(contextMap);
                }
                try {
                    runnable.run();
                } finally {
                    if (previous == null) {
                        MDC.clear();
                    } else {
                        MDC.setContextMap(previous);
                    }
                }
            };
        }
    }
}
