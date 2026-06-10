package gov.moda.dw.monitor.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.WebServerNamespace;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthEndpointGroups;
import org.springframework.boot.actuate.health.HealthEndpointWebExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CustomHealthWebExtension extends HealthEndpointWebExtension {

    private static final Logger log = LoggerFactory.getLogger(CustomHealthWebExtension.class);

    private final BuildProperties buildProperties;

    @Autowired(required = false)
    public CustomHealthWebExtension(
        HealthContributorRegistry registry,
        HealthEndpointGroups groups,
        BuildProperties buildProperties
    ) {
        // timeout 給 30 秒，跟官方預設一致
        super(registry, groups, Duration.ofSeconds(30));
        this.buildProperties = buildProperties;
    }

    @Override
    public WebEndpointResponse<HealthComponent> health(
        ApiVersion apiVersion,
        WebServerNamespace serverNamespace,
        SecurityContext securityContext,
        boolean showDetails,
        String... path
    ) {
        WebEndpointResponse<HealthComponent> response =
            super.health(apiVersion, serverNamespace, securityContext, showDetails, path);

        if (buildProperties != null) {
            log.info("CustomHealthWebExtension: Health check triggered, twdiw-release={}, image-version={}",
                buildProperties.get("twdiw-release.version"),
                buildProperties.getVersion()
            );

            HealthComponent body = response.getBody();

            if (body instanceof Health health) {
                // 建立新的 Health，details加入 release-version
                Health.Builder builder = Health.status(health.getStatus());

                // 保留原有的所有 details
                if (health.getDetails() != null) {
                    health.getDetails().forEach(builder::withDetail);
                }

                // 加入 release-version
                builder.withDetail("twdiw-release", buildProperties.get("twdiw-release.version"));
                builder.withDetail("image-version", buildProperties.getVersion());

                return new WebEndpointResponse<>(builder.build(), response.getStatus());
            } else if (body instanceof CompositeHealth compositeHealth) {
                // 處理 CompositeHealth 的情況
                Health.Builder builder = Health.status(compositeHealth.getStatus());

                // 保留原有的所有 details
                if (compositeHealth.getDetails() != null) {
                    compositeHealth.getDetails().forEach(builder::withDetail);
                }

                // 加入 release-version
                builder.withDetail("twdiw-release", buildProperties.get("twdiw-release.version"));
                builder.withDetail("image-version", buildProperties.getVersion());

                return new WebEndpointResponse<>(builder.build(), response.getStatus());
            }
        } else {
            log.info("CustomHealthWebExtension: Health check triggered, but buildProperties is null.");
        }

        return response;
    }
}
