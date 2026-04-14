package gov.moda.dw.monitor.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LoggingAppInfoContributor implements InfoContributor {

    private static final Logger log = LoggerFactory.getLogger(LoggingAppInfoContributor.class);

    private final BuildProperties buildProperties;

    @Autowired(required = false)
    public LoggingAppInfoContributor(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Override
    public void contribute(Info.Builder builder) {

        if (buildProperties != null) {
            log.info("LoggingAppInfoContributor: App Info check triggered, version={}, name={}, group={}, artifact={}, time={}",
                    buildProperties.getVersion(),
                    buildProperties.getName(),
                    buildProperties.getGroup(),
                    buildProperties.getArtifact(),
                    buildProperties.getTime()
            );

            Map<String, Object> build = new LinkedHashMap<>();
            build.put("artifact", buildProperties.getArtifact());
            build.put("name", buildProperties.getName());
            build.put("group", buildProperties.getGroup());
            build.put("version", buildProperties.getVersion());
            build.put("time", buildProperties.getTime());

            // ✅ 加上你想補充的欄位
            // build.put("api-version", "v2");

            builder.withDetail("build", build);
        } else
            log.info("LoggingAppInfoContributor: App Info check triggered, but buildProperties is null.");
    }
}
